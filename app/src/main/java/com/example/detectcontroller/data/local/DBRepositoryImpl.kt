package com.example.detectcontroller.data.local

import com.example.detectcontroller.data.local.locDTO.UiStateEntity
import com.example.detectcontroller.data.remote.remDTO.UiState
import com.example.detectcontroller.domain.DBRepository

class DBRepositoryImpl(private val database: AppDatabase) : DBRepository {

    override suspend fun getUiStates(): List<UiState> {
        return database.uiStateDao().getFirstTen().map { it.toDomain() }
    }
    private fun UiStateEntity.toDomain(): UiState {
        return UiState(
            timedv = timedv,
            stt = stt,
            url = url,
            irl = irl,
            pwr = pwr,
            frq = frq,
            tmp = tmp,
            rmode = rmode,
            gomode = gomode,
            modes = modes
        )
    }
}