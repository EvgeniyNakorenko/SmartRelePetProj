package com.example.detectcontroller.ui.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.domain.db.GetAllRegServerFromDBUseCase
import com.example.detectcontroller.service.ForegroundService
import com.example.detectcontroller.service.ForegroundService.Companion.TAG
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.INVISIBLE
import com.example.detectcontroller.ui.presentation.nav.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private val viewModelJob = Job()
    private var checkJob: Job? = null
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var preferences: SharedPreferences
//    private lateinit var viewModel: MainViewModel

    private val viewModel: MainViewModel by viewModels()
//    private  var gomodeVar : Boolean = false

//    private lateinit var getAllRegServerFromDBUseCase: GetAllRegServerFromDBUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isIgnoringBatteryOptimizations(this)) requestIgnoreBatteryOptimization(this)
        ForegroundService.startService(this)

        askNotificationPermission()

        startBackgroundTask(viewModel)

        preferences =
            this.getSharedPreferences(MainViewModel.REL_SETTINGS, Context.MODE_PRIVATE)

        setContent {
            MaterialTheme {
                MainScreen(viewModel, preferences)
            }
        }

    }


    override fun onPause() {
        super.onPause()
//        checkJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModelJob.cancel()
    }

    private fun startBackgroundTask(viewModel: MainViewModel) {
        lifecycleScope.launch {
            while (isActive) {

                var regData: RegServerEntity?
                regData = null
                try {
                    viewModel.createEvent(ScreenEvent.GetAllRegServerFromDB(""))
                    regData = viewModel.regState.value.lastOrNull()
//                    regData = getAllRegServerFromDBUseCase.execute().lastOrNull()
                } catch (e: Exception) {
                    Log.e(TAG, "download reg data error", e)
                }

                withContext(Dispatchers.IO) {

                    if (regData != null) {
                        if (viewModel.screenState.value.dialogState == INVISIBLE) {
                            viewModel.createEvent(
                                ScreenEvent.GetServerSettings(
                                    RequestDataDTO(
                                        regData.dvid,
                                        regData.tkn,
                                        regData.typedv,
                                        regData.num,
                                        com = "rv"
                                    )
                                )
                            )
                        }

                    }
                }
                delay(60_000)
            }
        }
    }

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun requestIgnoreBatteryOptimization(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Разрешение на уведомления предоставлено", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                }

                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

