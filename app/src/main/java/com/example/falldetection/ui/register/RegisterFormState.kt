package com.example.falldetection.ui.register

data class RegisterFormState (
    val emailError: Int? = null,
    val reminderNameError: Int? = null,
    val isCorrect: Boolean = false
)