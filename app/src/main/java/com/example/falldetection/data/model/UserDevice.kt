package com.example.falldetection.data.model

import com.example.falldetection.R
import java.util.Date

data class UserDevice(
    var id: String,
    val deviceId: String,
    val userId: String,
    val reminderName: String,
    val birthDate: Date?,
    val avatarImg: Int?
) {
    constructor() : this("", "", "", "", null, R.drawable.avatar_1)
}
