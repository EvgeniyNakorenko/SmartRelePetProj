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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.models.ErrorServerMod
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.ScreenEvent
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState

sealed class TimelineItem {
    abstract val time: String

    data class EventItem(val event: StatusEventServerDTO) : TimelineItem() {
        override val time: String get() = event.timeev
    }

    data class ErrorItem(val error: ErrorServerMod) : TimelineItem() {
        override val time: String get() = error.timeev
    }
}


@Composable
fun Log(mainViewModel: MainViewModel, preferences: SharedPreferences) {
    mainViewModel.createEvent(ScreenEvent.ShowScreen(DialogState.SCREEN_LOG))
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Events log", style = MaterialTheme.typography.headlineMedium)
        val eventsListState by mainViewModel.eventServerList.collectAsState()
        val errorsListState by mainViewModel.errorsListState.collectAsState()

        val timelineItems = remember(eventsListState, errorsListState) {
            (eventsListState.map { TimelineItem.EventItem(it) } +
                    errorsListState.map { TimelineItem.ErrorItem(it) })
                .sortedBy { it.time }
                .reversed()
        }

        if (!timelineItems.isNullOrEmpty()) {
//        if (!itemsEvent.isNullOrEmpty()) {


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
                items(timelineItems) { item ->
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
                            when (item) {
                                is TimelineItem.EventItem ->{
                                    Column {
                                        Text(text = "Event", color = Color.Black)
                                        Text(text = "Time: ${item.event.timeev}", color = Color.Black)
                                        Text(text = "State: ${item.event.rstate}", color = Color.Black)
                                        Text(text = "Value: ${item.event.value}", color = Color.Black)
                                        Text(text = "Name: ${item.event.name}", color = Color.Black)
                                    }
                                }

                                is TimelineItem.ErrorItem -> {
                                    Column {
                                        Text(text = "Error", color = Color.Red)
                                        Text(text = "Time: ${item.error.timeev}", color = Color.Black)
                                        Text(text = "Code: ${item.error.errorCode}", color = Color.Black)
                                        Text(
                                            text = "Message: ${item.error.errorMessage}",
                                            color = Color.Black
                                        )
                                        item.error?.let {
                                            Text(text = "Device: $it", color = Color.Black)
                                        }
                                    }
                                }
                            }
////                                        Text(text = item.toString(), color = Color.Black)
//                            Text(text = "Event", color = Color.Black)
//
//                            Text(text = "Time: ${item.timeev}", color = Color.Black)
//                            Text(
//                                text = "State: ${item.rstate}",
//                                color = Color.Black
//                            )
//                            Text(text = "Value: ${item.value}", color = Color.Black)
//                            Text(text = "Name: ${item.name}", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}