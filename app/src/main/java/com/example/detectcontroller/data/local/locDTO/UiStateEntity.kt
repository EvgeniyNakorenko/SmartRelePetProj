package com.example.detectcontroller.data.local.locDTO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ui_state")
data class UiStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timedv: String,
    val stt: String,
    val url: String,
    val irl: String,
    val pwr: String,
    val frq: String,
    val tmp: String,
    val rmode: String,
    val gomode: String,
    val modes: String,
    val online: String,
//    val bVis: Boolean
)