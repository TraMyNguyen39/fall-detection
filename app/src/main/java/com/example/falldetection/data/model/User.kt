package com.example.falldetection.data.model

import java.util.Date

data class User(
    val address: String,
    val birthDate: Date,
    val email: String,
    val fullName: String
) {
    constructor() : this("", Date(), "", "")
}
