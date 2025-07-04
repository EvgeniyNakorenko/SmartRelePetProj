package com.example.detectcontroller.domain.db

import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class DeleteLastEventServerByIdFromDBUseCase(private val database: AppDatabase) {
//    suspend fun execute(id: Int) {
//        return withContext(Dispatchers.IO) {
//            database.uiStateDao().deleteLastEventServerById(id)
//            database.uiStateDao().deleteLastEventDBById(id)
//        }
//    }
//}

//class LoadDataFromDBUseCase(
//    private val repository: DBRepository
//) {
//    suspend fun execute(): List<UiStateDTO> {
//        return repository.getUiStates()
//    }
//}

class DeleteLastEventServerByIdFromDBUseCase(private val repository: DBRepository) {
    suspend fun execute(id: Int) {
        return withContext(Dispatchers.IO) {
            repository.deleteLastEventServerById(id)
            repository.deleteLastEventDBById(id)
        }
    }
}