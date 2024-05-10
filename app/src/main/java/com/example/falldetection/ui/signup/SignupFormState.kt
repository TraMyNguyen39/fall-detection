package com.example.falldetection.ui.signup

data class SignupFormState(
    val isCorrect: Boolean = false,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val fullNameError: Int? = null
)
