package com.example.detectcontroller.ui.presentation.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object New : Screen("new", "New", Icons.Default.Add)
    data object Devices : Screen("devices", "Devices", Icons.Default.List)
    data object Log : Screen("log", "Log", Icons.Default.Info)

    companion object {
        val bottomNavItems = listOf(Home, New, Devices, Log)
    }
}