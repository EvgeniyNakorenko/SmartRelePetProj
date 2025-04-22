package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class StatusEventServerDTO(
    val id: Int = 0,
    val timeev: String = "0",
    val rstate: String = "0",
    val value: String = "0",
    val name: String = "0",
)
