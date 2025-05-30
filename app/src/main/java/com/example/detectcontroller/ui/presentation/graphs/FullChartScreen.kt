package com.example.detectcontroller.ui.presentation.graphs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.detectcontroller.ui.presentation.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullChartScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val uiStates by viewModel.uiStateListGraph.collectAsState()
    val reversedUiStates = uiStates.reversed()
    val pointsDataI = convertUiStatesToPointsI(reversedUiStates)
    val xMod = reversedUiStates.map { it.timedv.drop(8).take(9).replace('-', '.') }

    // Обработка кнопки "Назад"
    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LineChartScreen().GraphSwitcher(viewModel)

        }
    }
}