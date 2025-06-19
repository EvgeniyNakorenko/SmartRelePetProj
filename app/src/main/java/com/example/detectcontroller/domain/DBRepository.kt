package com.example.detectcontroller.domain

import com.example.detectcontroller.data.remote.remDTO.UiState

interface DBRepository {
    suspend fun getUiStates(): List<UiState>

}