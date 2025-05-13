package com.example.detectcontroller.ui.presentation.nav

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.detectcontroller.ui.presentation.Dialog
import com.example.detectcontroller.ui.presentation.DialogState
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.SingleDevice

@Composable
fun MyApp(mainViewModel: MainViewModel, preferences: SharedPreferences) {

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFAFEEEE))
                .padding(8.dp)
        ) {
            Column {

                SingleDevice(mainViewModel, preferences)
            }
        }
        if (mainViewModel.screenState.value.dialogState != DialogState.INVISIBLE) {
            Dialog(mainViewModel)
        }
    }
}