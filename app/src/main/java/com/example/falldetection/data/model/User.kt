package com.example.falldetection.data.model

import com.example.falldetection.utils.Gender
import com.example.falldetection.utils.Role
import java.util.Date

// sua lai
data class User(
    var email: String,
    var password: String? = null,
    val fullName: String,
    val role: Int = Role.SUPERVISOR.ordinal,
    val gender: Int = Gender.MALE.ordinal,
    val address: String? = null,
    val birthDate: Date? = null,
    val phoneNumber: String? = null,
    val avtUrl: String? = null
) {
    constructor() : this(
        "", null, "", Role.SUPERVISOR.ordinal, Gender.MALE.ordinal, null,
        null, null
    )
}
