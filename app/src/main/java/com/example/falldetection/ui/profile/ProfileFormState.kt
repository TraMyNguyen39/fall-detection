package com.example.falldetection.ui.profile

import android.view.textclassifier.ConversationActions.Message

data class ProfileFormState(
    val isCorrect: Boolean = false,
    val errorMessage: Int? = null
)
