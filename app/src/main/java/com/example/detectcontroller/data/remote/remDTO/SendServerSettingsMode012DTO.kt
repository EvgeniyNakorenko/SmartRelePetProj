package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class SendServerSettingsMode012DTO(
    val dvid: String,
    val tkn: String,
    val typedv: Int,
    val num: Int,
    val com: String,
    val rmode: Int,
)
