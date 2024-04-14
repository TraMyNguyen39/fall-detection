package com.example.falldetection.data.model

import android.view.textclassifier.ConversationActions.Message

data class SignupFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isCorrect: Boolean = false,
    val errorMessage: Int? = null,
    val sendMailSuccess: Boolean = false
)
