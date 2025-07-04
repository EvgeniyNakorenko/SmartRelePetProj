package com.example.detectcontroller.domain.db

import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DeleteLastEventServerByIdFromDBUseCase(private val repository: DBRepository) {
    suspend fun execute(id: Int) {
        return withContext(Dispatchers.IO) {
            repository.deleteLastEventServerById(id)
            repository.deleteLastEventDBById(id)
        }
    }
}