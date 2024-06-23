package com.example.falldetection.data.model

import com.example.falldetection.R
import java.util.Date

data class UserDevice(
//    var id: String,
    val patientEmail: String,
    val userEmail: String,
    val reminderName: String,
    var birthDate: Date? = null,
    var avatarImg: String? = null,
    var fullName: String? = null,
) {
    constructor() :
            this("", "", "", null, null)
}
