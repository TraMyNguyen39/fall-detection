package com.example.falldetection.data.datasource

data class ResponseResultService<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String?,
)