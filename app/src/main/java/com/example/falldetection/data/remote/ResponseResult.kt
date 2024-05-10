package com.example.falldetection.data.remote
data class ResponseResult<T>(
    val success: Boolean,
    val data: T? = null,
    val error: Int?,
)
