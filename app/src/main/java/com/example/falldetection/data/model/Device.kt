package com.example.falldetection.data.model

data class Device (
    var patientEmail: String,
    var deviceCode: String
) {
    constructor() : this("", "")
}