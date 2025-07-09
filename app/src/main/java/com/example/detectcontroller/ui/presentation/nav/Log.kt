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
import androidx.compose.ui.unit.sp
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.models.ErrorServerMod
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.ScreenEvent
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Заголовок, который будет всегда сверху
        Text(
            text = "Журнал событий",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Остальное содержимое
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            val eventsListState by mainViewModel.eventServerList.collectAsState()
            val errorsListState by mainViewModel.errorsListState.collectAsState()

            val timelineItems = remember(eventsListState, errorsListState) {
                (eventsListState.map { TimelineItem.EventItem(it) } +
                        errorsListState.map { TimelineItem.ErrorItem(it) })
                    .sortedBy { it.time }
                    .reversed()
            }

            fun formatUnixTime(
                unixTime: Long,
                pattern: String = "dd.MM.yyyy HH:mm:ss",
                zoneId: ZoneId = ZoneId.systemDefault()
            ): String {
                val instant = Instant.ofEpochSecond(unixTime)
                val formatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId)
                return formatter.format(instant)
            }

            if (!timelineItems.isNullOrEmpty()) {
                if (!eventsListState.isNullOrEmpty()) {
                    preferences
                        .edit()
                        .putInt("SAVEDID", eventsListState.last().id)
                        .apply()
                    mainViewModel.setEvId(eventsListState.last().id)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(timelineItems) { item ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                            {
                                when (item) {
                                    is TimelineItem.EventItem -> {
                                        Column {
//                                            Text(text = "Event ${item.event.id}", color = Color.Red)
                                            Text(
                                                text = "${formatUnixTime(item.event.timeev.toLong())}",
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "${item.event.value}",
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    is TimelineItem.ErrorItem -> {
                                        Column {
//                                            Text(text = "Error ${item.error.id}", color = Color.Red)
                                            Text(
                                                text = "${formatUnixTime(item.error.timeev.toLong())}",
                                                color = Color.Black
                                            )
                                            Text(text = "Ошибка сервера", color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}