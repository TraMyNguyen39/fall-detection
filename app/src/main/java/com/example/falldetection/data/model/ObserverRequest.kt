package com.example.falldetection.data.model

import java.util.Date

data class ObserverRequest(
    var supervisorEmail: String,
    var patientEmail: String,
    val time: Date,
    val reminderName: String
) {
    constructor() : this("", "", Date(), "")
}
