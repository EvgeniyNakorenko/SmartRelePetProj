package com.example.detectcontroller.ui.presentation


import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.detectcontroller.domain.db.DeleteLastEventServerByIdFromDBUseCase
import com.example.detectcontroller.domain.db.GetOneLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.InsertLastEventServerInDBUseCase
import com.example.detectcontroller.domain.db.InsertRegServerInDBUseCase
import com.example.detectcontroller.domain.db.LoadDataFromDBUseCase
import com.example.detectcontroller.domain.db.LoadEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.LoadLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.SaveEventServerInDBUseCase

class MainViewModelFactory(
    private val application: Application,
    private val context: Context,
    private val loadDataFromDBUseCase: LoadDataFromDBUseCase,
    private val loadEventServerFromDBUseCase : LoadEventServerFromDBUseCase,
    private val loadLastEventServerFromDBUseCase : LoadLastEventServerFromDBUseCase,
    private val deleteLastEventServerByIdFromDBUseCase: DeleteLastEventServerByIdFromDBUseCase,
    private val getOneLastEventServerFromDBUseCase: GetOneLastEventServerFromDBUseCase,
    private val insertRegServerInDBUseCase: InsertRegServerInDBUseCase,
    private val saveEventServerInDBUseCase: SaveEventServerInDBUseCase,

    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                application,
                context,
                loadDataFromDBUseCase,
                loadEventServerFromDBUseCase,
                loadLastEventServerFromDBUseCase,
                deleteLastEventServerByIdFromDBUseCase,
                getOneLastEventServerFromDBUseCase,
                insertRegServerInDBUseCase,
                saveEventServerInDBUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
