package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.UiStateEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.UiState
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class SaveDataInDBUseCase(private val database: AppDatabase) {
//
//    suspend fun execute(uiState: UiState) {
//        withContext(Dispatchers.IO) {
//            val uiStateEntity = UiStateEntity(
//                timedv = uiState.timedv,
//                stt = uiState.stt,
//                url = uiState.url,
//                irl = uiState.irl,
//                pwr = uiState.pwr,
//                frq = uiState.frq,
//                tmp = uiState.tmp,
//                rmode = uiState.rmode,
//                gomode = uiState.gomode,
//                modes = uiState.modes,
////                bVis = uiState.bVis,
//            )
//            database.uiStateDao().insertUiState(uiStateEntity)
//        }
//    }
//}

class SaveDataInDBUseCase(private val repository: DBRepository) {

    suspend fun execute(uiState: UiState) {
        withContext(Dispatchers.IO) {
            repository.saveDataServerInDB(uiState)
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