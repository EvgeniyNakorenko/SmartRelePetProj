package com.example.detectcontroller.ui.presentation.composeFunc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    progressColor: Color = Color.Blue
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .height(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .background(progressColor)
                .height(4.dp)
        )
    }
}
