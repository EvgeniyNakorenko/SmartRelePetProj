package com.example.detectcontroller.ui.presentation.composeFunc

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.detectcontroller.R
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.P_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.TAR_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.TAR_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.T_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.ScreenEvent
import com.example.detectcontroller.ui.presentation.utils.Item
import com.example.detectcontroller.ui.presentation.utils.Screen


@Composable
fun SingleDevice(
    mainViewModel: MainViewModel,
    preferences: SharedPreferences,
    navController: NavHostController
) {

    val uiState by mainViewModel.uiStateDTO.collectAsState()
    val releModeGoVisVal by mainViewModel.buttonGoVisib.collectAsState()
    val savedId by mainViewModel.savedErrorId.collectAsState()

    LaunchedEffect(savedId) {
        mainViewModel.startEventsChecking(savedId)
    }

    val isVisibleAlertU by mainViewModel.isVisibleAlertU.collectAsState()
    val isVisibleAlertI by mainViewModel.isVisibleAlertI.collectAsState()
    val isVisibleAlertP by mainViewModel.isVisibleAlertP.collectAsState()

    val releName = mainViewModel.textFieldValue1SETREL.last().toString()

    Column {
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
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "  $releName", style = MaterialTheme.typography.bodyMedium)

                ToggleIconButton(
                    isChecked = uiState.modes == "1" || uiState.modes == "6",
                    onCheckedChange = {
                        mainViewModel.set_buttonGoVisib(false)
                        when(uiState.modes){
                            "1" -> mainViewModel.createEvent(ScreenEvent.SendServerStopMode(""))
                            "0" -> {
                                mainViewModel.createEvent(ScreenEvent.SendServerStopMode(""))
                                mainViewModel.createEvent(ScreenEvent.SendServerGoMode(""))
                            }
                            "3" -> {
                                mainViewModel.createEvent(ScreenEvent.SendServerStopMode(""))
                                mainViewModel.createEvent(ScreenEvent.SendServerGoMode(""))
                            }
                            "6" -> mainViewModel.createEvent(ScreenEvent.SendServerStopMode(""))
                        }

                    },
                    enabled = releModeGoVisVal,
                )

                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "онлайн: ${uiState.online}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "режим: ${uiState.rmode}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row {
                        Text(text = "состояние: ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = uiState.stt.takeLast(1),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                }

                IconButton(
                    enabled = releModeGoVisVal,
                    onClick = {
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
            }
        }

        val items = listOf(
            Item(
                text = uiState.url,
                description = "Напряжение, В",
                color = Color.White,
                eventAlarm = isVisibleAlertU
            ),
            Item(
                text = uiState.irl,
                description = "Ток, А",
                color = Color.White,
                eventAlarm = isVisibleAlertI
            ),
            Item(
                text = uiState.pwr,
                description = "Мощность, Вт",
                color = Color.White,
                eventAlarm = isVisibleAlertP
            ),
            Item(
                text = uiState.tmp,
                description = "Температура, °C",
                color = Color.White,
                eventAlarm = false
            ),
            Item(
                text = "Count",
                description = "Счетчик, кВт*ч",
                color = Color.White,
                eventAlarm = false
            ),
            Item(
                text = "Tar",
                description = "Тарификатор, ₽",
                color = Color.White,
                eventAlarm = false
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
                        .padding(4.dp)
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
                            .padding(16.dp)
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
//                                    Spacer(modifier = Modifier.weight(1f))

                                if (item.description != "Счетчик, кВт*ч" && item.description != "Тарификатор, ₽") {

                                    Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = "Line Chart",
                                        tint = Color.Blue,
                                        modifier = Modifier
//                                            .size(40.dp)
                                            .offset(y = (-12).dp)
                                            .offset(x = (43).dp)
                                            .clickable(onClick = {
                                                when (item.description) {
                                                    "Напряжение, В" -> {
                                                        navController.navigate(Screen.DisplayChartU.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) {
                                                                saveState = true
                                                            }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }


                                                    }

                                                    "Ток, А" -> {
                                                        navController.navigate(Screen.DisplayChartI.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) {
                                                                saveState = true
                                                            }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    }

                                                    "Мощность, Вт" -> {
                                                        navController.navigate(Screen.DisplayChartP.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) {
                                                                saveState = true
                                                            }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    }

                                                    "Температура, °C" -> {}
                                                }

                                            })
                                    )

                                }

                                IconButton(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .offset(
                                            x = (-26).dp,
                                            y = (-12).dp
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_report_problem_24),
                                        contentDescription = "Предупреждение",
                                        tint = Color.Red,
                                        modifier = Modifier.alpha(if (item.eventAlarm == true) 1.0f else 0.0f)
                                    )

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
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.5f
                            )

                            when (item.text) {
                                "Count" -> Text(
                                    text = "000",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.5f
                                )

                                "Tar" -> Text(
                                    text = "000",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.5f
                                )

                                else -> Text(
                                    text = item.text.drop(4),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.5f
                                )
                            }



                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var proValueMin: Float = 0.1F
                                var proValueMax: Float = 5000.1F
                                val colorValue: Color
                                var proOnOff = false

                                if (item.text != "LoadLoad..") {
                                    when (item.text.take(3)) {
                                        uiState.irl.take(3) -> {
                                            proValueMin = preferences.getFloat(
                                                I_TEXT_FIELD_VALUE2,
                                                0.1f
                                            )
                                            proValueMax = preferences.getFloat(
                                                I_TEXT_FIELD_VALUE1,
                                                5.0f
                                            )

                                            proOnOff =
                                                mainViewModel.checkboxValue1I.last() == true

                                            val currentValue =
                                                item.text.drop(5).toFloatOrNull()
                                                    ?: 10.0f
                                            colorValue =
                                                when (currentValue) {
                                                    in proValueMin..proValueMax -> Color.Green
                                                    else -> Color.Red
                                                }

                                        }

                                        uiState.url.take(3) -> {
                                            proValueMin = preferences.getInt(
                                                U_TEXT_FIELD_VALUE2,
                                                200
                                            ).toFloat()
                                            proValueMax = preferences.getInt(
                                                U_TEXT_FIELD_VALUE1,
                                                300
                                            ).toFloat()

                                            proOnOff =
                                                mainViewModel.checkboxValue1U.last() == true

                                            val currentValue =
                                                item.text.drop(5).toFloatOrNull()
                                                    ?: 100.0f
                                            colorValue =
                                                when (currentValue) {
                                                    in proValueMin..proValueMax -> Color.Green
                                                    else -> Color.Red
                                                }


                                        }

                                        uiState.pwr.take(3) -> {

                                            proValueMax = preferences.getInt(
                                                P_TEXT_FIELD_VALUE1,
                                                20
                                            ).toFloat()

                                            proOnOff =
                                                mainViewModel.checkboxValue1P.last() == true

                                            val currentValue =
                                                item.text.drop(5).toFloatOrNull()
                                                    ?: 10.0f
                                            colorValue =
                                                when (currentValue) {
                                                    in 0f..proValueMax -> Color.Green
                                                    else -> Color.Red
                                                }

                                        }

                                        uiState.tmp.take(3) -> {

                                            proValueMax = preferences.getInt(
                                                T_TEXT_FIELD_VALUE1,
                                                100
                                            ).toFloat()

                                            proOnOff =
                                                mainViewModel.checkboxValue1T.last() == true

                                            val currentValue =
                                                item.text.drop(5).toIntOrNull()
                                                    ?: 0
                                            colorValue =
                                                when (currentValue.toFloat()) {
                                                    in 0f..proValueMax -> Color.Green
                                                    else -> Color.Red
                                                }


                                        }

                                        "Cou" -> {
                                            proValueMin = preferences.getInt(
                                                TAR_TEXT_FIELD_VALUE1,
                                                1000
                                            ).toFloat()


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


                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Bottom
                                ) {

                                    Text(
                                        text = "$proValueMin/$proValueMax",
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.2f
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    if (item.description != "Счетчик, кВт*ч" && item.description != "Тарификатор, ₽") {
                                        Text(
                                            text = "норма  ",
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.5f
                                        )

                                        SquareStatusIndicator(
                                            isActive = ((item.text.drop(5).toFloatOrNull())
                                                ?: 10.0f) > proValueMin && ((item.text.drop(5)
                                                .toFloatOrNull())
                                                ?: 10.0f) < proValueMax
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = "защита  ",
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.5f
                                        )

                                        StatusIndicatorCircle(
                                            isActive = proOnOff
                                        )
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
