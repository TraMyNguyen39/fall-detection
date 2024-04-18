package com.example.falldetection.data.model

data class Device(
    val id: String,
    val fullName: String
) {
    constructor() : this("", "")
}
