package com.example.falldetection.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.model.LoginFormState
import com.example.falldetection.data.model.SignupFormState
import com.example.falldetection.data.model.User
import com.example.falldetection.data.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _signupFormState = MutableLiveData<SignupFormState>()
    val signupFormState: LiveData<SignupFormState> = _signupFormState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _forgetPassMessage = MutableLiveData<Int?>()
    val forgetPassMessage: LiveData<Int?> = _forgetPassMessage

    fun login(email: String, password: String, callback: (Int?) -> Unit) {
        repository.login(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (repository.getCurrentAccount()?.isEmailVerified == true) {
                    viewModelScope.launch {
                        val user = repository.getUserByEmail(email)
                        _user.postValue(user)
                        callback(null)
                    }
                } else {
                    repository.logout()
                    callback(R.string.txt_check_your_email)
                }
            } else {
                if (task.exception is FirebaseNetworkException) {
                    callback(R.string.txt_no_internet)
                } else {
                    callback(R.string.txt_wrong_username_password)
                }
            }
        }
    }

    fun signup(email: String, password: String, confirmPassword: String, callback: (Int?) -> Unit) {
        // check if confirmPassword == password
        if (password != confirmPassword) {
            callback(R.string.txt_confirm_pass_not_equal_pass)
            return
        }

        // if equal, signup
        repository.signup(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // create user in firebase store
                repository.addNewUser(email).addOnCompleteListener { addUserTask ->
                    if (addUserTask.isSuccessful) {
                        // after that, send a verification email for user
                        repository.getCurrentAccount()?.sendEmailVerification()
                            ?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    repository.logout()
                                    callback(R.string.txt_check_your_email_signup) // Gửi email thành công
                                } else {
                                    repository.logout()
                                    callback(R.string.txt_cant_send_email)
                                }
                            }
                    } else {
                        repository.logout()
                        callback(R.string.txt_signup_failed)
                    }
                }
            } else {
                Log.d("TAG", task.exception.toString())
                when (task.exception) {
                    is FirebaseNetworkException -> {
                        callback(R.string.txt_no_internet)
                    }

                    is FirebaseAuthUserCollisionException -> {
                        callback(R.string.txt_account_is_available)
                    }

                    else -> {
                        callback(R.string.txt_signup_failed)
                    }
                }
            }
        }
    }

    fun sendResetPassword(email: String, callback: (Int) -> Unit) {
        repository.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(R.string.txt_check_your_email_forget_pass)
                } else {
                    if (it.exception is FirebaseNetworkException) {
                        callback(R.string.txt_no_internet)
                    } else {
                        callback(R.string.txt_cant_send_email)
                    }
                }
            }
    }

    fun loginFormChange(email: String, password: String) {
        if (email.isBlank()) {
            _loginFormState.postValue(LoginFormState(emailError = R.string.txt_error_email_blank))
        } else if (!isValidEmail(email)) {
            _loginFormState.postValue(LoginFormState(emailError = R.string.txt_error_email_invalid))
        } else if (password.isBlank()) {
            _loginFormState.postValue(LoginFormState(passwordError = R.string.txt_error_password_blank))
        } else {
            _loginFormState.postValue(LoginFormState(isCorrect = true))
        }
    }

    fun signupFormChange(email: String, password: String) {
        if (email.isBlank()) {
            _signupFormState.postValue(SignupFormState(emailError = R.string.txt_error_email_blank))
        } else if (!isValidEmail(email)) {
            _signupFormState.postValue(SignupFormState(emailError = R.string.txt_error_email_invalid))
        } else if (password.isBlank()) {
            _signupFormState.postValue(SignupFormState(passwordError = R.string.txt_error_password_blank))
        } else if (!isValidPassword(password)) {
            _signupFormState.postValue(SignupFormState(passwordError = R.string.txt_error_password_format))
        } else {
            _signupFormState.postValue(SignupFormState(isCorrect = true))
        }
    }

    fun forgetPasswordFormChange(email: String) {
        if (email.isBlank()) {
            _forgetPassMessage.postValue(R.string.txt_error_email_blank)
        } else if (!isValidEmail(email)) {
            _forgetPassMessage.postValue(R.string.txt_error_email_invalid)
        } else {
            _forgetPassMessage.postValue(null)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        return regex.matches(password)
    }

    private fun isValidEmail(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }
}