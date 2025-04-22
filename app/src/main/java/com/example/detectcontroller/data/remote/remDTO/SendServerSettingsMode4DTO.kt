package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class SendServerSettingsMode4DTO(
    val dvid: String,
    val tkn: String,
    val typedv: Int,
    val num: Int,
    val com: String,
    val rmode: Int,
    val tRTCOn: String,
    val tRTCOff: String,
)
