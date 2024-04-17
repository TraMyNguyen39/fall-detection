package com.example.falldetection.ui.login

import android.view.textclassifier.ConversationActions.Message

data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isCorrect: Boolean = false,
//    val errorMessage: Int? = null
)
