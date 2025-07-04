package com.example.detectcontroller.domain.models

data class ErrorServerMod(
    val id: Int = 0,
    val errorCode: Int = 0,
    val errorMessage: String = "0",
    val timeev: String = "0",
    val deviceId: String? = "0"
)
