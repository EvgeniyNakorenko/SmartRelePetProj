package com.example.detectcontroller.ui.presentation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ToggleIconButton(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    contentDescription: String? = null
) {
    IconButton(
        onClick = { onCheckedChange(!isChecked) },
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Filled.CheckCircle else Icons.Filled.CheckCircle,
            contentDescription = contentDescription,
            tint = if (isChecked) Color.Blue
            else Color.Red
        )
    }
}
