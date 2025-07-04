package com.example.detectcontroller.domain.db

import com.example.detectcontroller.domain.DBRepository
import com.example.detectcontroller.domain.models.ErrorServerMod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetErrorsFromDBUseCase(private val repository: DBRepository) {
        suspend fun execute(): List<ErrorServerMod> {
        return withContext(Dispatchers.IO) {
            repository.getAllErrors()
        }
    }

}
