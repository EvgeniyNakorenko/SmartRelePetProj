package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.DBRepository

//class LoadEventServerFromDBUseCase(private val database: AppDatabase) {
//    suspend fun execute(): List<StatusEventServerDTO> {
//        return withContext(Dispatchers.IO) {
//            val dataList = database.uiStateDao().getAllEventsServer()
//            if (dataList.isEmpty()) {
//                emptyList()
//            } else {
//                dataList.map { data ->
//                    StatusEventServerDTO(
//                        id = data.id,
//                        timeev = data.timeev,
//                        rstate = data.rstate,
//                        value = data.value,
//                        name = data.name,
//                    )
//                }
//            }
//        }
//    }
//}

class LoadEventServerFromDBUseCase(private val repository: DBRepository) {
    suspend fun execute(): List<StatusEventServerDTO> {
        return repository.getEventServerFromDB()
    }

}

//class LoadDataFromDBUseCase(
//    private val repository: DBRepository
//) {
//    suspend fun execute(): List<UiStateDTO> {
//        return repository.getUiStates()
//    }
//}


//class DeleteLastEventServerByIdFromDBUseCase(private val repository: DBRepository) {
//    suspend fun execute(id: Int) {
//        return withContext(Dispatchers.IO) {
//            repository.deleteLastEventServerById(id)
//            repository.deleteLastEventDBById(id)
//        }
//    }
//}