package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class GetSettingsDTOrmode5(
    override val rmode: Int,
    val tClOn: String,
    val tClOff: String,
    val prton: Int,
    val upm: Int,
    val ulh: Int,
    val ull: Int,
    val ipm: Int,
    val ilh: Float,
    val ill: Float,
    val ppm: Int,
    val plh: Int,
    val tpm: Int,
    val tlh: Int
) : SettingsBaseDTO
