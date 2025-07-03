package com.example.detectcontroller.domain

import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.UiState


//@Module
//@InstallIn(SingletonComponent::class)
//@Binds

interface DBRepository {
//    @Binds
    suspend fun getUiStates(): List<UiState>
    suspend fun deleteLastEventServerById(id: Int)
    suspend fun deleteLastEventDBById(id: Int)
    suspend fun getEventServerFromDB(): List<StatusEventServerDTO>
    suspend fun getOneLastEventServerFromDB(id: Int): StatusEventServerDTO?
    suspend fun saveEventServerInDB(statusEventServerDTO: StatusEventServerDTO)
    suspend fun insertRegServer(regServerEntity: RegServerEntity)
    suspend fun getAllRegServer(): List<RegServerEntity>
    suspend fun saveDataServerInDB(uiState: UiState)
    suspend fun insertLastEventServerDB(lastEventsServerEntity: LastEventsServerEntity)


}