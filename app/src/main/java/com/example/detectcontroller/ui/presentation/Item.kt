package com.example.detectcontroller.ui.presentation

import androidx.compose.ui.graphics.Color
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO

data class Item(
    val text: String,
    val description: String,
    val color: Color,
    val eventAlarm: Boolean
)
