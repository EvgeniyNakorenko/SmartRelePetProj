package com.example.detectcontroller.ui.presentation.nav

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.detectcontroller.ui.presentation.MainViewModel

@Composable
fun HomeScreen(mainViewModel: MainViewModel,
               preferences: SharedPreferences
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Home Screen", style = MaterialTheme.typography.headlineMedium)
        MyApp(mainViewModel,preferences)
    }
}