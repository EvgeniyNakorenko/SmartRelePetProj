package com.example.detectcontroller.domain

import com.example.detectcontroller.data.remote.remDTO.DeleteDeviceEventResponseDTO
import com.example.detectcontroller.data.remote.remDTO.SettingsBaseDTO
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.StatusRegServer
import com.example.detectcontroller.data.remote.remDTO.UiStateDTO

interface ServerRepo {
    fun checkServerEvent(): Result<StatusEventServerDTO>
    fun deleteEventServer(): Result<DeleteDeviceEventResponseDTO>
    fun fetchDataUseCase(): Result<UiStateDTO>
    fun getServerSettingsUseCase(): Result<SettingsBaseDTO>
    fun sendServerGoModeUseCase(): Result<StatusRegServer>
    fun sendServerSettingsMode3UseCase(): Result<StatusRegServer>
}