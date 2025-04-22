package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class GetSettingsDTOrmode6(
    override val rmode: Int,
    val ntrm: Int,
    val rtype: Int,
    val mtrm: Int,
    val th: Int,
    val tl: Int,
    val kos: Int,
    val kp: Int,
    val kd: Float,
    val ki: Float,
    val dir: Int,
) : SettingsBaseDTO
