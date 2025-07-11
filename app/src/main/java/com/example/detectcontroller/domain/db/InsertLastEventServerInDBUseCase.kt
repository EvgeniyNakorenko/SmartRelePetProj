package com.example.detectcontroller.domain.db

import com.example.detectcontroller.domain.DBRepository
import com.example.detectcontroller.domain.models.ErrorServerMod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class InsertLastEventServerInDBUseCase(private val repository: DBRepository) {
    suspend fun execute(error: ErrorServerMod) {
        withContext(Dispatchers.IO) {
            repository.insertError(error)
        }
    }
}

