package com.example.detectcontroller.domain.models

data class ErrorServerMod(
    val id: Int,
    val errorCode: Int,
    val errorMessage: String,
    val timestamp: Long,
    val deviceId: String?
)
