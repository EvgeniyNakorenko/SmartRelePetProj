package com.example.detectcontroller.ui.presentation.nav

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.ScreenEvent
import com.example.detectcontroller.ui.presentation.composeFunc.Dialog
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState
import com.example.detectcontroller.ui.presentation.composeFunc.SingleDevice

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    preferences: SharedPreferences,
    navController: NavHostController
) {
    mainViewModel.createEvent(ScreenEvent.ShowScreen(DialogState.SCREEN_HOME))
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Home Screen", style = MaterialTheme.typography.headlineMedium)
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFAFEEEE))
                    .padding(8.dp)
            ) {
                Column {

                    SingleDevice(mainViewModel, preferences,navController)
                }
            }
            if (mainViewModel.screenState.value.dialogState != DialogState.INVISIBLE) {
                Dialog(mainViewModel)
            }
        }
    }
}