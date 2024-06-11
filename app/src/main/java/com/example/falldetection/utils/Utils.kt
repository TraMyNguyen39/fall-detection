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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

object Utils {
    var token: String? = null
    var role: Int = Role.SUPERVISOR.ordinal

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

    fun getCurrentEmail(context: Context): String? {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_account_key), Context.MODE_PRIVATE
        )
        return sharedPref.getString(context.getString(R.string.preference_email_key), null)
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
    fun isValidDate(birthDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false
        return try {
            val date = dateFormat.parse(birthDate)
            val calendar = Calendar.getInstance().apply { time = date!! }

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val birthYear = calendar.get(Calendar.YEAR)
            birthYear in 1901..<currentYear
        } catch (e: Exception) {
            false
        }

    }
    fun isValidPhoneNumber(phone: String): Boolean {
        val regex = "^((\\+84)|0)[3|5|7|8|9][0-9]{8}$".toRegex()
        return phone.matches(regex)
    }

    @SuppressLint("SimpleDateFormat")
    fun getAge(birthDate: Date) : Int {
        val yearFormat = SimpleDateFormat("yyyy")
        val currentYear = yearFormat.format(Date())
        val birthYear = yearFormat.format(birthDate)
        return currentYear.toInt() - birthYear.toInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun timeStringToDate(str: String): Date? {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

        return try {
            str.let { sdf.parse(str) }
        } catch (e: ParseException) {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToTimeString(date: Date?): String? {
        val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy")

        return try {
            date?.let {
                // Subtract 7 hours from the date
                val millis = it.time - TimeUnit.HOURS.toMillis(7)
                val adjustedDate = Date(millis)
                sdf.format(adjustedDate)
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToString(date: Date?): String? {
        val sdf = SimpleDateFormat("dd/MM/yyyy")

        return try {
            date?.let { sdf.format(it) }
        } catch (e: ParseException) {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun stringToDate(date: String?): Date? {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return try {
            date?.let { sdf.parse(it) }
        } catch (e: ParseException) {
            null
        }
    }
}