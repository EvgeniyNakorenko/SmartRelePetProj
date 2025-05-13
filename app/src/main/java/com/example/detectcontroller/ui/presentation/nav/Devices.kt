package com.example.detectcontroller.ui.presentation.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Devices() {
    Box(
        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
    ) {
//        Text("Devices Screen", style = MaterialTheme.typography.headlineMedium)
        val options = listOf("Device 1", "Device 2", "Device 3")
        var selectedOption by remember { mutableStateOf(options[0]) }

        Column {
            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option }
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}