package com.example.falldetection.data.model

import com.example.falldetection.utils.Role
import java.util.Date

// sua lai
data class User(
    val email: String,
    var password: String?,
    val fullName: String,
    val role: Int = Role.SUPERVISOR.ordinal,
    val address: String? = null,
    val birthDate: Date? = null,
    val avtUrl: String? = null
) {
    constructor() : this("", null, "", Role.SUPERVISOR.ordinal, null,
        null, null)
}
