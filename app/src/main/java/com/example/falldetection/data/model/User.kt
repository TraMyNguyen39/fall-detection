package com.example.falldetection.data.model

import java.util.Date

data class User(
    var uid: String,
    val address: String,
    val birthDate: Date,
    val email: String,
    val fullName: String
) {
    constructor() : this("", "", Date(), "", "")
}
