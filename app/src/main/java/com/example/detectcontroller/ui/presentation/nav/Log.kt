package com.example.detectcontroller.ui.presentation.nav

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.ScreenEvent
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState


@Composable
fun Log(mainViewModel: MainViewModel , preferences: SharedPreferences) {
    mainViewModel.createEvent(ScreenEvent.ShowScreen(DialogState.SCREEN_LOG))
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Events log", style = MaterialTheme.typography.headlineMedium)
        val eventsListState by mainViewModel.eventServerList.collectAsState()
        val errorsListState by mainViewModel.errorsListState.collectAsState()

        val itemsEvent = eventsListState.reversed()
//        val itemsEvent = (eventsListState.map { event ->
//            Pair(event.timeev, event)
//        } + errorsListState.map { error ->
//            Pair(error.timeev, error)
//        }).sortedByDescending { it.first }
//            .map { it.second }


        if (!itemsEvent.isNullOrEmpty()) {


            preferences
                .edit()
                .putInt("SAVEDID", eventsListState.last().id)
                .apply()

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(itemsEvent) { item ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
//                                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color.LightGray)
//                                                .clip(RoundedCornerShape(8.dp))

                        ) {
//                                        Text(text = item.toString(), color = Color.Black)
                            Text(text = "ID: ${item.id}", color = Color.Black)
                            Text(text = "Time: ${item.timeev}", color = Color.Black)
                            Text(
                                text = "State: ${item.rstate}",
                                color = Color.Black
                            )
                            Text(text = "Value: ${item.value}", color = Color.Black)
                            Text(text = "Name: ${item.name}", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}