package com.example.falldetection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.falldetection.R
import com.google.android.material.textfield.TextInputEditText
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

object Utils {
    var token: String? = null

    fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }
        })
    }
    fun isOnline(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    fun hideKeyBoard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getCurrentAccount(context: Context): String? {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_account_key), Context.MODE_PRIVATE
        )
        return sharedPref.getString(context.getString(R.string.preference_email_key), null);
    }

    fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        return regex.matches(password)
    }

    fun isValidEmail(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getAge(birthDate: Date) : Int {
        val yearFormat = SimpleDateFormat("yyyy")
        val currentYear = yearFormat.format(Date())
        val birthYear = yearFormat.format(birthDate)
        return currentYear.toInt() - birthYear.toInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun timeStampToString(time: Timestamp?): String? {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return time?.let { sdf.format(it) }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToTimeString(date: Date?): String? {
        val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy")
        return date?.let { sdf.format(it) }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToString(date: Date?): String? {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return date?.let { sdf.format(it) }
    }
}