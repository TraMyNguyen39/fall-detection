package com.example.falldetection.data.model

import java.util.Date

data class FallHistoryItem(
    val patientEmail: String,
    val time: Date?,
    val address: String?
) {
    constructor() : this(patientEmail = "", time = null, address = null)
}
