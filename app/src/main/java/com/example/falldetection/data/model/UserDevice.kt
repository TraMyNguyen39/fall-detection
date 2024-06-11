package com.example.falldetection.data.model

import com.example.falldetection.R
import java.util.Date

data class UserDevice(
//    var id: String,
    val patientEmail: String,
    val userEmail: String,
    val reminderName: String,
    var birthDate: Date? = null,
    val avatarImg: Int? = null,
    var fullName: String? = null,
) {
    constructor() :
            this("", "", "", null, R.drawable.avatar_1)
}
