package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.remote.remDTO.UiStateDTO
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class SaveDataInDBUseCase(private val database: AppDatabase) {
//
//    suspend fun execute(uiStateDTO: UiStateDTO) {
//        withContext(Dispatchers.IO) {
//            val uiStateEntity = UiStateEntity(
//                timedv = uiStateDTO.timedv,
//                stt = uiStateDTO.stt,
//                url = uiStateDTO.url,
//                irl = uiStateDTO.irl,
//                pwr = uiStateDTO.pwr,
//                frq = uiStateDTO.frq,
//                tmp = uiStateDTO.tmp,
//                rmode = uiStateDTO.rmode,
//                gomode = uiStateDTO.gomode,
//                modes = uiStateDTO.modes,
////                bVis = uiStateDTO.bVis,
//            )
//            database.uiStateDao().insertUiState(uiStateEntity)
//        }
//    }
//}

class SaveDataInDBUseCase(private val repository: DBRepository) {

    suspend fun execute(uiStateDTO: UiStateDTO) {
        withContext(Dispatchers.IO) {
            repository.saveDataServerInDB(uiStateDTO)
        }
    }
}

//class SaveEventServerInDBUseCase(private val repository: DBRepository) {
//
//    suspend fun execute(statusEventServerDTO: StatusEventServerDTO) {
//        withContext(Dispatchers.IO) {
//            repository.saveEventServerInDB(statusEventServerDTO)
//        }
//    }
//}