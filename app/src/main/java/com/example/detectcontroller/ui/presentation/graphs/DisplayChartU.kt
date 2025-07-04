package com.example.detectcontroller.ui.presentation.graphs

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.detectcontroller.ui.presentation.MainViewModel


@Composable
fun DisplayChartU(viewModel: MainViewModel) {
//    HandleScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        (context as Activity).requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    val uiStates by viewModel.uiStateDTOListGraph.collectAsState()
    val reversedUiStates = uiStates.reversed()
    val pointsDataU = convertUiStatesToPointsU(reversedUiStates)
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val chartHeight = screenHeight / 1 // Половина высоты экрана, разделенная на 3 графика
    val xMod = reversedUiStates.map { it.timedv.drop(8).take(9).replace('-', '.') }


    Column(
        modifier = Modifier
            .fillMaxSize() // Занимать весь доступный размер
            .padding(8.dp) // Небольшой отступ от краев
    ) {
        Text(
            text = "U, В",
            modifier = Modifier.padding(start = 10.dp)
        )
        Crossfade(
            targetState = pointsDataU,
            animationSpec = tween(durationMillis = 500)
        ) { data ->
            Box(
                modifier = Modifier
                    .fillMaxSize() // График занимает все доступное пространство
                    .weight(1f) // Занимает все доступное пространство в Column
            ) {
            SingleLineChartWithGridLines(
                pointsData = data,
                chartHeight = chartHeight,
                lineColor = Color.Magenta,
//                    "U, В",
                modifier = Modifier.fillMaxSize(),
                xMod = xMod,
                textSize = 16
            )
        }}

        DisposableEffect(Unit) {
            onDispose {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}

