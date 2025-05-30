package com.example.detectcontroller.ui.presentation.composeFunc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicatorCircle(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Green,
    inactiveColor: Color = Color.LightGray,
    height: Dp = 24.dp,
    width: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(width, height)
            .background(
                color = if (isActive) activeColor else inactiveColor,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .shadow(
                elevation = if (isActive) 8.dp else 0.dp,
                shape = CircleShape,
                ambientColor = if (isActive) activeColor else inactiveColor,
                spotColor = if (isActive) activeColor else inactiveColor
            )
    )
}