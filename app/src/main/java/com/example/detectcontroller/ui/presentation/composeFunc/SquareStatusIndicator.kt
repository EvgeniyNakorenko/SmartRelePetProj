package com.example.detectcontroller.ui.presentation.composeFunc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SquareStatusIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Green,
    inactiveColor: Color = Color.Red,
    size: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    glowEffect: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = if (isActive) activeColor else inactiveColor,
                shape = RectangleShape
            )
            .border(
                width = borderWidth,
                color = Color.Black.copy(alpha = 0.2f),
                shape = RectangleShape
            )
            .shadow(
                elevation = if (glowEffect && isActive) 8.dp else 0.dp,
                shape = RectangleShape,
                ambientColor = if (isActive) activeColor else Color.Transparent,
                spotColor = if (isActive) activeColor else Color.Transparent
            )
    )
}