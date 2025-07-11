package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.remote.remDTO.UiStateDTO
import com.example.detectcontroller.domain.DBRepository

//class LoadDataFromDBUseCase(private val database: AppDatabase) {
//
//    suspend fun execute(): List<UiStateDTO> {
//        return withContext(Dispatchers.IO) {
//            val dataList = database.uiStateDao().getFirstTen()
//            if (dataList.isEmpty()) {
//                emptyList()
//            } else {
//                dataList.map { data ->
//                    UiStateDTO(
//                        timedv = data.timedv,
//                        stt = data.stt,
//                        url = data.url,
//                        irl = data.irl,
//                        pwr = data.pwr,
//                        frq = data.frq,
//                        tmp = data.tmp,
//                        rmode = data.rmode,
//                        gomode = data.gomode,
//                        modes = data.modes,
////                        bVis = false
//                    )
//                }
//            }
//        }
//    }
//}

class LoadDataFromDBUseCase(
    private val repository: DBRepository
) {
    suspend fun execute(): List<UiStateDTO> {
        return repository.getUiStates()
    }
}
