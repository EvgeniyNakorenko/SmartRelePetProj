package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteLastEventServerByIdFromDBUseCase(private val database: AppDatabase) {
    suspend fun execute(id: Int) {
        return withContext(Dispatchers.IO) {
            database.uiStateDao().deleteLastEventServerById(id)
        }
    }
}