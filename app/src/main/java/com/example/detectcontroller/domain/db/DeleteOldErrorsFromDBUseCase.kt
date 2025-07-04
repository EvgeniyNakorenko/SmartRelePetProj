package com.example.detectcontroller.domain.db

import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteOldErrorsFromDBUseCase(private val repository: DBRepository) {
        suspend fun execute(threshold: Long) {
        return withContext(Dispatchers.IO) {
            repository.clearOldErrors(threshold)
        }
    }
}



//class DeleteLastEventServerByIdFromDBUseCase(private val repository: DBRepository) {
//    suspend fun execute(id: Int) {
//        return withContext(Dispatchers.IO) {
//            repository.deleteLastEventServerById(id)
//            repository.deleteLastEventDBById(id)
//        }
//    }
//}