package com.example.falldetection.data.model

import java.util.Date

data class FallHistoryItem(
    val deviceId: String,
    val time: Date?,
    val address: String?
) {
    constructor() : this(deviceId = "", time = null, address = null)
}
