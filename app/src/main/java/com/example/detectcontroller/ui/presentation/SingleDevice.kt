package com.example.detectcontroller.ui.presentation

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.detectcontroller.R
import com.example.detectcontroller.data.remote.remDTO.DeleteEventDTO
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.service.ForegroundService.Companion.RELE_MODE_GO
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.P_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.TAR_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.TAR_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.T_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE2
import kotlinx.coroutines.delay

@Composable
fun SingleDevice(
    mainViewModel: MainViewModel,
    preferences: SharedPreferences,
) {

    val uiState by mainViewModel.uiState.collectAsState()
    var savedId : Int? = 0


    val finalEventState by mainViewModel.finalEventState.collectAsState()
    val eventsListState by mainViewModel.eventServerList.collectAsState()
    val itemsEvent = eventsListState.reversed()
    var isVisibleAlertU by remember { mutableStateOf(false) }
    var isVisibleAlertI by remember { mutableStateOf(false) }
    var isVisibleAlertP by remember { mutableStateOf(false) }

//    preferences
//        .edit()
//        .putInt("SAVEDID", finalEventState?.id ?: 0)
//        .apply()

    LaunchedEffect(Unit) {
        while (true) {
            mainViewModel.createEvent(ScreenEvent.LoadLastEventServerFromDB(""))
            mainViewModel.createEvent(ScreenEvent.LoadEventServerFromDB(""))
            delay(5000)
            savedId = preferences.getInt("SAVEDID",0) ?: 0

            eventsListState.reversed().take(20).forEach {
                if (it.name == "evu" && it.id > (savedId ?: 0)){
                    isVisibleAlertU = true
                }
                if (it.name == "evi" && it.id > (savedId ?: 0)){
                    isVisibleAlertI = true
                }
                if (it.name == "evi" && it.id > (savedId ?: 0)){
                    isVisibleAlertP = true
                }
                if (it.name == "gomode"){
                    mainViewModel.createEvent(ScreenEvent.DeleteEventAlarmFromServer(""))
                }
            }
        }
    }


    val releName = mainViewModel.textFieldValue1SETREL.last().toString()
    val releMode = mainViewModel.releModeValue.last()



    Column() {
//    Column(modifier = Modifier.padding(12.dp)) {
        Box(
            modifier = Modifier
                .padding(8.dp) // Добавляем отступ между элементами
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(8.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val releModeGoVisVal by mainViewModel.buttonGoVisib.collectAsState()
                val isCheckedVar by mainViewModel.releModeGO.collectAsState()

                Text(text = "  $releName", style = MaterialTheme.typography.bodyMedium)

                ToggleIconButton(
                    isChecked = isCheckedVar,
                    onCheckedChange = {
                        mainViewModel.createEvent(ScreenEvent.SendServerGoMode(""))
                        mainViewModel.set_buttonGoVisib(false)
//                        if (isCheckedVar) {
//                            mainViewModel.set_releModeGO(false)
//                        }else{
//                            mainViewModel.set_releModeGO(true)
//                        }

                    },
                    enabled = releModeGoVisVal,
                )

                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "режим: $releMode", style = MaterialTheme.typography.bodyMedium)
                    Row() {
//                    Row(modifier = Modifier.padding(4.dp)) {
                        Text(text = "состояние: ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = uiState.stt,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                }

                IconButton(
                    enabled = releModeGoVisVal,
                    onClick = {
//                        showDialog.value = true
                        mainViewModel.createEvent(
                            ScreenEvent.ShowDialog(
                                DialogState.SETTINGS_REL
                            )
                        )
                    },
                    modifier = Modifier.offset(x = 7.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_burger_menu),
                        contentDescription = "Настройки"
                    )
                }
            }}
            if (uiState.error != null) {
                Text(text = uiState.error!!)
            } else {
                val items = listOf(
                    Item(
                        uiState.url,
                        "Напряжение, В",
                        Color.White,
//                        Color.Magenta,
                        if (isVisibleAlertU) true else false
//                        if ((finalEventState?.name ?: "") == "evu") finalEventState else null
                    ),
                    Item(
                        uiState.irl,
                        "Ток, А",
                        Color.White,
//                        Color(206, 210, 58),
                        if (isVisibleAlertI) true else false
//                        if ((finalEventState?.name ?: "") == "evi") finalEventState else null
                    ),
                    Item(
                        uiState.pwr,
                        "Мощность, Вт",
                        Color.White,
//                        Color.Cyan,
                        if (isVisibleAlertP) true else false
//                        if ((finalEventState?.name ?: "") == "evp") finalEventState else null
                    ),
                    Item(
                        uiState.tmp,
                        "Температура, °C",
                        Color.White,
//                        Color(0xFFFFC0CB),
                        false
                    ),
                    Item(
                        "Count",
                        "Счетчик, кВт*ч",
                        Color.White,
//                        Color.Gray,
                        false
                    ), // Swapped position
                    Item(
                        "Tar",
                        "Тарификатор, ₽",
                        Color.White,
//                        Color(255, 165, 0),
                        false
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items) { item ->

                        Box(
                            modifier = Modifier
                                .padding(4.dp) // Добавляем отступ между элементами
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        item.color,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)


                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 0.dp)

                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {},
                                            enabled = false,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .offset(
                                                    x = 26.dp,
                                                    y = (-12).dp
                                                )
                                        ) {

                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_report_problem_24),
                                                contentDescription = "Предупреждение",
                                                tint = Color.Red,
//                                                modifier = Modifier.alpha(
//
//                                                    if (isVisibleAlertU || isVisibleAlertI || isVisibleAlertP) 1.0f else 0.0f
//
//                                                )
                                                modifier = Modifier.alpha(if ( item.eventAlarm == true ) 1.0f else 0.0f)
                                            )

//                                            Icon(
//                                                painter = painterResource(id = R.drawable.baseline_report_problem_24),
//                                                contentDescription = "Предупреждение",
//                                                tint = Color.Red,
//                                                modifier = Modifier.alpha(
//                                                    if (item.eventAlarm != null &&
//                                                        (item.eventAlarm.id > savedId || savedId == 0) &&
//                                                        when (item.text) {
//                                                            uiState.url -> (finalEventState?.name ?: "") == "evu"
//                                                            uiState.irl -> (finalEventState?.name ?: "") == "evi"
//                                                            uiState.pwr -> (finalEventState?.name ?: "") == "evp"
//                                                            uiState.tmp -> (finalEventState?.name ?: "") == "evt"
//                                                            else -> true
//                                                        }
//                                                    ) 1.0f else 0.0f
//                                                )
//                                            )

                                        }

                                        IconButton(
                                            onClick = {
                                                when (item.text) {
                                                    uiState.irl ->
                                                        mainViewModel.createEvent(
                                                            ScreenEvent.ShowDialog(
                                                                DialogState.SETTINGS_I
                                                            )
                                                        )

                                                    uiState.url -> mainViewModel.createEvent(
                                                        ScreenEvent.ShowDialog(
                                                            DialogState.SETTINGS_U
                                                        )
                                                    )

                                                    uiState.pwr -> mainViewModel.createEvent(
                                                        ScreenEvent.ShowDialog(
                                                            DialogState.SETTINGS_P
                                                        )
                                                    )

                                                    uiState.tmp -> mainViewModel.createEvent(
                                                        ScreenEvent.ShowDialog(
                                                            DialogState.SETTINGS_TEMP
                                                        )
                                                    )

                                                    "Count" -> mainViewModel.createEvent(
                                                        ScreenEvent.ShowDialog(
                                                            DialogState.SETTINGS_COU
                                                        )
                                                    )

                                                    "Tar" -> mainViewModel.createEvent(
                                                        ScreenEvent.ShowDialog(
                                                            DialogState.SETTINGS_TAR
                                                        )
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .offset(
                                                    x = 12.dp,
                                                    y = (-12).dp
                                                )
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_burger_menu),
                                                contentDescription = "Меню"
                                            )
                                        }
                                    }

                                }
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.8f
                                    )

                                    when (item.text) {
                                        "Count" -> Text(
                                            text = "000",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                        )

                                        "Tar" -> Text(
                                            text = "000",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                        )

                                        else -> Text(
                                            text = item.text.drop(4),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
//                                                .padding(start = 16.dp, end = 16.dp)
                                    ) {


                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            var proValueMin: Float
                                            var proValueMax: Float
                                            var colorValue: Color = Color.Green

                                            if (item.text != "LoadLoad..") {
                                                when (item.text.take(3)) {
                                                    uiState.irl.take(3) -> {
                                                        proValueMin = preferences.getFloat(
                                                            I_TEXT_FIELD_VALUE2,
                                                            0.1f
                                                        )
                                                        proValueMax = preferences.getFloat(
                                                            I_TEXT_FIELD_VALUE1,
                                                            50.0f
                                                        )

                                                        val currentValue =
                                                            item.text.drop(5).toFloatOrNull()
                                                                ?: 10.0f
                                                        colorValue =
                                                            when (currentValue) {
                                                                in proValueMin..proValueMax -> Color.Green
                                                                else -> Color.Red
                                                            }

                                                        CustomLinearProgressIndicator(
                                                            progress = 0.5f, // 50% прогресса
                                                            modifier = Modifier.weight(1f),
                                                            progressColor = colorValue,
                                                            backgroundColor = colorValue
                                                        )
                                                    }

                                                    uiState.url.take(3) -> {
                                                        proValueMin = preferences.getInt(
                                                            U_TEXT_FIELD_VALUE2,
                                                            90
                                                        ).toFloat()
                                                        proValueMax = preferences.getInt(
                                                            U_TEXT_FIELD_VALUE1,
                                                            300
                                                        ).toFloat()

                                                        val currentValue =
                                                            item.text.drop(5).toFloatOrNull()
                                                                ?: 100.0f
                                                        colorValue =
                                                            when (currentValue) {
                                                                in proValueMin..proValueMax -> Color.Green
                                                                else -> Color.Red
                                                            }

                                                        CustomLinearProgressIndicator(
                                                            progress = 0.5f, // 50% прогресса
                                                            modifier = Modifier.weight(1f),
                                                            progressColor = colorValue,
                                                            backgroundColor = colorValue
                                                        )
                                                    }

                                                    uiState.pwr.take(3) -> {
//                                                    proValueMin = preferences.getInt(
//                                                        P_TEXT_FIELD_VALUE2,
//                                                        100
//                                                    ).toFloat()
                                                        proValueMax = preferences.getInt(
                                                            P_TEXT_FIELD_VALUE1,
                                                            20000
                                                        ).toFloat()

                                                        val currentValue =
                                                            item.text.drop(5).toFloatOrNull()
                                                                ?: 1000.0f
                                                        colorValue =
                                                            when (currentValue) {
                                                                in 0f..proValueMax -> Color.Green
                                                                else -> Color.Red
                                                            }

                                                        CustomLinearProgressIndicator(
                                                            progress = 0.5f, // 50% прогресса
                                                            modifier = Modifier.weight(1f),
                                                            progressColor = colorValue,
                                                            backgroundColor = colorValue
                                                        )
                                                    }

                                                    uiState.tmp.take(3) -> {
//                                                    proValueMin = preferences.getInt(
//                                                        T_TEXT_FIELD_VALUE2,
//                                                        1
//                                                    ).toFloat()
                                                        proValueMax = preferences.getInt(
                                                            T_TEXT_FIELD_VALUE1,
                                                            100
                                                        ).toFloat()

                                                        val currentValue =
                                                            item.text.drop(5).toIntOrNull()
                                                                ?: 0
                                                        colorValue =
                                                            when (currentValue.toFloat()) {
                                                                in 0f..proValueMax -> Color.Green
                                                                else -> Color.Red
                                                            }

                                                        CustomLinearProgressIndicator(
                                                            progress = 0.5f, // 50% прогресса
                                                            modifier = Modifier.weight(1f),
                                                            progressColor = colorValue,
                                                            backgroundColor = colorValue
                                                        )
                                                    }

                                                    "Cou" -> {
                                                        proValueMin = preferences.getInt(
                                                            TAR_TEXT_FIELD_VALUE1,
                                                            1000
                                                        ).toFloat()

                                                        CustomLinearProgressIndicator(
                                                            progress = 111.toFloat() / proValueMin.toFloat(), // 50% прогресса
                                                            modifier = Modifier.weight(1f),
                                                        )
                                                    }

                                                    "Tar" -> {
                                                        proValueMin = preferences.getInt(
                                                            TAR_TEXT_FIELD_VALUE1,
                                                            0
                                                        ).toFloat()
                                                        proValueMax = preferences.getInt(
                                                            TAR_TEXT_FIELD_VALUE2,
                                                            1000
                                                        ).toFloat()

                                                        CustomLinearProgressIndicator(
                                                            progress = proValueMax.toFloat() / proValueMin.toFloat(), // 50% прогресса
                                                            modifier = Modifier.weight(1f),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
//                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "норма  ",
//                                                    modifier = Modifier.padding(start = 8.dp),
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.8f
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
            if (
                mainViewModel.checkboxValue3I.last() || mainViewModel.checkboxValue3U.last() || mainViewModel.checkboxValue3P.last()
            ) {
                LineChartScreen().GraphSwitcher(mainViewModel)
            }
        }
//    }
    }