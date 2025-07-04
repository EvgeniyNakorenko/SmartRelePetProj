package com.example.detectcontroller.data.local.locDTO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_server")
data class EventServerEntity(
    @PrimaryKey
    val id: Int,
    var timeev: String,
    val rstate: String,
    val value: String,
    val name: String,
)
