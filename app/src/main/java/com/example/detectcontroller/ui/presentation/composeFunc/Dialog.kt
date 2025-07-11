package com.example.detectcontroller.ui.presentation.composeFunc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode012DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode3DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode4DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode5DTO
import com.example.detectcontroller.data.remote.remDTO.SendSettingsDTO
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.ScreenEvent
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.ADD_NEW_REL
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.ERROR
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.INVISIBLE
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_COU
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_I
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_P
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_REL
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_TAR
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_TEMP
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SETTINGS_U
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection


@Composable
fun Dialog(
    mainViewModel: MainViewModel,
) {

//    if (!(preferences.getBoolean(RELE_MODE_GO, false) ?: false)){
//        mainViewModel.set_releModeGO(false)
//    }else mainViewModel.set_releModeGO(true)

//    private val preferences: SharedPreferences =
//        application.getSharedPreferences(REL_SETTINGS, Context.MODE_PRIVATE)

    val deviceData = mainViewModel.listDevices.last()


    val isAlarmEventVisibleState by mainViewModel.finalEventState.collectAsState()
    val eventsListState by mainViewModel.eventServerList.collectAsState()

    var isVisibleSettingsMenu by remember { mutableStateOf(false) }
    var isVisibleAlarmEventMenu by remember { mutableStateOf(false) }
    var isVisibleGraphicMenu by remember { mutableStateOf(false) }

    val textFieldValue1 = remember { mutableStateOf("") }
    val textFieldValue2 = remember { mutableStateOf("") }
    val checkboxValue1 = remember { mutableStateOf(false) }
    val checkboxValue2 = remember { mutableStateOf(false) }
    val checkboxValue3 = remember { mutableStateOf(false) }
    val releMode3TimeValue = remember { mutableStateOf(mainViewModel.releMode3TimeOn.last()) }
    val releMode4TimeOnValue = remember { mutableStateOf(mainViewModel.releMode4TimeOn.last()) }
    val releMode4TimeOffValue = remember { mutableStateOf(mainViewModel.releMode4TimeOff.last()) }
    val releMode5TimeOnValue = remember { mutableStateOf(mainViewModel.releMode5TimeOn.last()) }
    val releMode5TimeOffValue = remember { mutableStateOf(mainViewModel.releMode5TimeOff.last()) }

    val dvidValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.last()?.dvid.toString() ?: "") }
    val tknValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.last()?.tkn.toString() ?: "") }
    val typedvValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.last()?.typedv.toString() ?: "") }
    val numValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.last()?.num.toString() ?: "") }


    // Инициализация переменных для хранения ресурсов заголовка и текста диалогового окна
    var titleResource0 = ""
    var titleResource = ""
    var textIT1Resource = ""
    var textIT2Resource = ""
    var textCB1Resource = ""
    var textCB2Resource = ""
    var textCB3Resource = ""

    // Определение ресурсов заголовка и текста в зависимости от текущего состояния диалога
    when (mainViewModel.screenState.value.dialogState) {
        SETTINGS_I -> {
            titleResource = "Настройки защиты \nпо току"
            titleResource0 = "Датчик тока"
            textIT1Resource = "Верхняя граница ${mainViewModel.textFieldValue1I.last()}"
            textIT2Resource = "Нижняя граница ${mainViewModel.textFieldValue2I.last()}"
            textCB1Resource = "Вкл. защиту"
            textCB2Resource = "Вкл. пуш уведомления"
            textCB3Resource = "Показывать график"
            checkboxValue1.value = mainViewModel.checkboxValue1I.last()
            checkboxValue2.value = mainViewModel.checkboxValue2I.last()
            checkboxValue3.value = mainViewModel.checkboxValue3I.last()
        }

        SETTINGS_U -> {
            titleResource0 = "Датчик напряжения"
            titleResource = "Настройки защиты \nпо напряжению"
            textIT1Resource = "Верхняя граница ${mainViewModel.textFieldValue1U.last()}"
            textIT2Resource = "Нижняя граница ${mainViewModel.textFieldValue2U.last()}"
            textCB1Resource = "Вкл. защиту"
            textCB2Resource = "Вкл. пуш уведомления"
            textCB3Resource = "Показывать график"
            checkboxValue1.value = mainViewModel.checkboxValue1U.last()
            checkboxValue2.value = mainViewModel.checkboxValue2U.last()
            checkboxValue3.value = mainViewModel.checkboxValue3U.last()
        }

        SETTINGS_P -> {
            titleResource0 = "Датчик мощности"
            titleResource = "Настройки защиты \nпо мощности"
            textIT1Resource = "Верхняя граница ${mainViewModel.textFieldValue1P.last()}"
//            textIT2Resource = "Нижняя граница ${mainViewModel.textFieldValue2P.last()}"
            textCB1Resource = "Вкл. защиту"
            textCB2Resource = "Вкл. пуш уведомления"
            textCB3Resource = "Показывать график"
            checkboxValue1.value = mainViewModel.checkboxValue1P.last()
            checkboxValue2.value = mainViewModel.checkboxValue2P.last()
            checkboxValue3.value = mainViewModel.checkboxValue3P.last()
        }

        SETTINGS_TAR -> {
            titleResource0 = "Тарификатор"
            titleResource = "Настройки \nтарификатора"
            textIT1Resource = "Тариф, руб/кВт*ч ${mainViewModel.textFieldValue1TAR.last()}"
            textIT2Resource = "Расход, руб. ${mainViewModel.textFieldValue2TAR.last()}"
            textCB1Resource = "Откл при достижении"
            textCB2Resource = "Вкл. пуш уведомления"
            checkboxValue1.value = mainViewModel.checkboxValue1TAR.last()
            checkboxValue2.value = mainViewModel.checkboxValue2TAR.last()
        }

        SETTINGS_COU -> {
            titleResource0 = "Счетчик"
            titleResource = "Настройки ограничения \nпо счетчику"
            textIT1Resource = "Верхняя граница ${mainViewModel.textFieldValue1COU.last()}"
            textIT2Resource = "Очистить счетчик"
            textCB1Resource = "Откл. при достижении"
            textCB2Resource = "Вкл. пуш уведомления"
            checkboxValue1.value = mainViewModel.checkboxValue1COU.last()
            checkboxValue2.value = mainViewModel.checkboxValue2COU.last()
        }

        SETTINGS_REL -> {
            titleResource0 = "Настройки реле"
            titleResource = "Изменить"
            textIT1Resource = "Имя устройства: ${mainViewModel.textFieldValue1SETREL.last()}"
            textIT2Resource = mainViewModel.releModeValue.last()
        }

        SETTINGS_TEMP -> {
            titleResource0 = "Датчик температуры"
            titleResource = "Настройки защиты \nпо температуре"
            textIT1Resource = "Верхняя граница ${mainViewModel.textFieldValue1T.last()}"
//            textIT2Resource = "Нижняя граница ${mainViewModel.textFieldValue2T.last()}"
            textCB1Resource = "Вкл. защиту"
            textCB2Resource = "Вкл. пуш уведомления"
            checkboxValue1.value = mainViewModel.checkboxValue1T.last()
            checkboxValue2.value = mainViewModel.checkboxValue2T.last()
        }

        ADD_NEW_REL -> {
            titleResource0 = "Регистрация контроллера"
            titleResource =
                "Выберите интерфейс общения для регистрации"
            textCB1Resource = "Wi-Fi"
            textCB2Resource = "Ручной ввод"
        }

        INVISIBLE -> TODO()

        ERROR -> TODO()
//        DialogState.SCREEN_NEW -> TODO()
//        DialogState.SCREEN_DEVICES -> TODO()
//        DialogState.SCREEN_LOG -> TODO()
//        DialogState.SCREEN_HOME -> TODO()
        else -> {}
    }

    // Создание диалогового окна с заданными параметрами
    AlertDialog(

        onDismissRequest = {
            mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
        },
        title = { Text(text = titleResource0, style = TextStyle(fontSize = 28.sp)) },

        text = {
            Column {
                if (mainViewModel.screenState.value.dialogState == ADD_NEW_REL) {
                    Text(
                        text = titleResource,
                        fontSize = 22.sp
                    )
                } else {

                    Text(
                        text = titleResource,
                        modifier = Modifier
                            .clickable(onClick = {
                                isVisibleSettingsMenu = !isVisibleSettingsMenu
                            }),
                        fontSize = 22.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .size(
                            width = if (isVisibleSettingsMenu || mainViewModel.screenState.value.dialogState == ADD_NEW_REL) 300.dp else 0.dp,
                            height = if (isVisibleSettingsMenu || mainViewModel.screenState.value.dialogState == ADD_NEW_REL) 400.dp else 0.dp
                        )
                        .alpha(if (isVisibleSettingsMenu || mainViewModel.screenState.value.dialogState == ADD_NEW_REL) 1f else 0f),
                ) {

                    when (mainViewModel.screenState.value.dialogState) {
                        ADD_NEW_REL -> {

                            Column {

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = checkboxValue1.value,
                                        onCheckedChange = { isChecked ->
                                            checkboxValue1.value = isChecked
                                            checkboxValue2.value = false
                                        },
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text(text = textCB1Resource)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = checkboxValue2.value,
                                        onCheckedChange = { isChecked ->
                                            checkboxValue2.value = isChecked
                                            checkboxValue1.value = false
                                        },
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text(text = textCB2Resource)
                                }

                                OutlinedTextField(
                                    value = dvidValue1.value,
                                    onValueChange = { newValue ->
                                        dvidValue1.value = newValue
                                    },
                                    enabled = checkboxValue2.value,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    label = { Text("dvid") }
                                )
                                OutlinedTextField(
                                    value = tknValue1.value,
                                    onValueChange = { newValue ->
                                        tknValue1.value = newValue
                                    },
                                    enabled = checkboxValue2.value,
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    label = { Text("tkn") }
                                )
                                OutlinedTextField(
                                    value = typedvValue1.value,
                                    onValueChange = { newValue ->
                                        typedvValue1.value = newValue
                                    },
                                    enabled = checkboxValue2.value,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    label = { Text("typedv") }
                                )
                                OutlinedTextField(
                                    value = numValue1.value,
                                    onValueChange = { newValue ->
                                        numValue1.value = newValue
                                    },
                                    enabled = checkboxValue2.value,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    label = { Text("num") }
                                )

                            }
                        }

                        SETTINGS_REL -> {
                            Column {

                                OutlinedTextField(
                                    value = textFieldValue1.value,
                                    onValueChange = { newValue ->
                                        if (newValue.isNotBlank()) textFieldValue1.value = newValue
                                    },
                                    label = { Text(textIT1Resource) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CustomSpinner(
                                    options = listOf(
                                        "Выключено",
                                        "Тумблер",
                                        "Кнопка",
                                        "На время",
                                        "Таймер",
                                        "Цикл"
                                    ),
//                                    mainViewModel = mainViewModel
                                    valueDefault = mainViewModel.releModeValue.last(),
                                    labelSt = "Режимы работы реле",
                                    onOptionSelected = { index ->
                                        mainViewModel.set_releModeValue(index)
                                        mainViewModel.createEvent(
                                            ScreenEvent.SaveShPrefSettingsRel(
                                                ""
                                            )
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                when (mainViewModel.releModeValue.last()) {
                                    "На время" -> {

                                        Column {
                                            Text("На время")
                                            Text("Время включения реле : ${mainViewModel.releMode3TimeOn.lastOrNull() ?: "00:00:00"}")

                                            val hoursStateM3 = remember {
                                                mutableStateOf(
                                                    mainViewModel.releMode3TimeOn.lastOrNull()
                                                        ?.substringBefore(':') ?: "0"
                                                )
                                            }
                                            val minutesStateM3 = remember {
                                                mutableStateOf(
                                                    mainViewModel.releMode3TimeOn.lastOrNull()
                                                        ?.substringAfter(':')?.substringBefore(':')
                                                        ?: "0"
                                                )
                                            }
                                            val secondsStateM3 = remember {
                                                mutableStateOf(
                                                    mainViewModel.releMode3TimeOn.lastOrNull()
                                                        ?.substringAfterLast(':') ?: "0"
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                CustomSpinner(
                                                    modifier = Modifier.width(90.dp),
                                                    options = (0..23).map {
                                                        it.toString().padStart(2, '0')
                                                    },
//                                                    options = (0..23).toList().map { it.toString() },
                                                    valueDefault = hoursStateM3.value.padStart(
                                                        2,
                                                        '0'
                                                    ),
//                                                    valueDefault = "00",
                                                    labelSt = "h",
                                                    onOptionSelected = { hours ->
                                                        hoursStateM3.value =
                                                            hours.toString().padStart(2, '0')

                                                        releMode3TimeValue.value =
                                                            "${
                                                                hoursStateM3.value.padStart(
                                                                    2,
                                                                    '0'
                                                                )
                                                            }:${
                                                                minutesStateM3.value.padStart(
                                                                    2,
                                                                    '0'
                                                                )
                                                            }:${
                                                                secondsStateM3.value.padStart(
                                                                    2,
                                                                    '0'
                                                                )
                                                            }"
//                                                        mainViewModel.updateReleMode3Time(hours, minutesState.value, secondsState.value)
                                                    }
                                                )
                                                CustomSpinner(
                                                    modifier = Modifier.width(90.dp),
                                                    options = (0..59).map {
                                                        it.toString().padStart(2, '0')
                                                    },
                                                    valueDefault = minutesStateM3.value.padStart(
                                                        2,
                                                        '0'
                                                    ),
                                                    labelSt = "m",
                                                    onOptionSelected = { minutes ->
                                                        minutesStateM3.value =
                                                            minutes.toString().padStart(2, '0')

                                                        releMode3TimeValue.value =
                                                            "${hoursStateM3.value}:${minutesStateM3.value}:${secondsStateM3.value}"

                                                    }
                                                )

                                                CustomSpinner(
                                                    modifier = Modifier.width(90.dp),
                                                    options = (0..59).map {
                                                        it.toString().padStart(2, '0')
                                                    },
                                                    valueDefault = secondsStateM3.value.padStart(
                                                        2,
                                                        '0'
                                                    ),
                                                    labelSt = "s",
                                                    onOptionSelected = { seconds ->
                                                        secondsStateM3.value =
                                                            seconds.toString().padStart(2, '0')
                                                        releMode3TimeValue.value =
                                                            "${hoursStateM3.value}:${minutesStateM3.value}:${secondsStateM3.value}"

                                                    }
                                                )
                                            }

                                        }
//                                        val parts = tOn.split(":").map { it.toInt() }
//                                        releMode3TimeValue.value = Triple(parts[0], parts[1], parts[2])
//                                        releMode3TimeValue.value = tOn
                                    }

                                    "Таймер" -> {

                                        val hoursStateM4St = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode4TimeOn.lastOrNull()
                                                    ?.substringBefore(':') ?: "0"
                                            )
                                        }
                                        val minutesStateM4St = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode4TimeOn.lastOrNull()
                                                    ?.substringAfter(':')?.substringBefore(':')
                                                    ?: "0"
                                            )
                                        }
                                        val secondsStateM4St = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode4TimeOn.lastOrNull()
                                                    ?.substringAfterLast(':') ?: "0"
                                            )
                                        }

                                        val hoursStateM4Fi = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode4TimeOff.lastOrNull()
                                                    ?.substringBefore(':') ?: "0"
                                            )
                                        }
                                        val minutesStateM4Fi = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode4TimeOff.lastOrNull()
                                                    ?.substringAfter(':')?.substringBefore(':')
                                                    ?: "0"
                                            )
                                        }
                                        val secondsStateM4Fi = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode4TimeOff.lastOrNull()
                                                    ?.substringAfterLast(':') ?: "0"
                                            )
                                        }

                                        Column {
                                            Text("Время включения реле : ${mainViewModel.releMode4TimeOn.lastOrNull() ?: "00:00:00"}")

                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
//                                                    horizontalArrangement = Arrangement.SpaceAround
                                                ) {

                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..23).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = hoursStateM4St.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "h",
                                                        onOptionSelected = { hours ->
                                                            hoursStateM4St.value =
                                                                hours.toString().padStart(2, '0')
                                                            releMode4TimeOnValue.value =
                                                                "${hoursStateM4St.value}:${minutesStateM4St.value}:${secondsStateM4St.value}"
                                                        }
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..59).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = minutesStateM4St.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "m",
                                                        onOptionSelected = { minutes ->
                                                            minutesStateM4St.value =
                                                                minutes.toString().padStart(2, '0')
                                                            releMode4TimeOnValue.value =
                                                                "${hoursStateM4St.value}:${minutesStateM4St.value}:${secondsStateM4St.value}"
                                                        }
                                                    )
//                                                    CustomSpinner(
//                                                        modifier = Modifier.width(90.dp),
//                                                        options = (0..59).map {
//                                                            it.toString().padStart(2, '0')
//                                                        },
//                                                        valueDefault = secondsStateM4St.value.padStart(2, '0'),
//                                                        labelSt = "s",
//                                                        onOptionSelected = { seconds ->
//                                                            secondsStateM4St.value =
//                                                                seconds.toString().padStart(2, '0')
//                                                            releMode4TimeOnValue.value =
//                                                                "${hoursStateM4St.value}:${minutesStateM4St.value}:${secondsStateM4St.value}"
//
//                                                        }
//                                                    )

                                                }
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text("Время выключения реле : ${mainViewModel.releMode4TimeOff.lastOrNull() ?: "23:59:59"}")
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
//                                                    horizontalArrangement = Arrangement.SpaceAround
                                                ) {

                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..23).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = hoursStateM4Fi.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "h",
                                                        onOptionSelected = { hours ->
                                                            hoursStateM4Fi.value =
                                                                hours.toString().padStart(2, '0')
                                                            releMode4TimeOffValue.value =
                                                                "${hoursStateM4Fi.value}:${minutesStateM4Fi.value}:${secondsStateM4Fi.value}"

                                                        }
                                                    )

                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..59).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = minutesStateM4Fi.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "m",
                                                        onOptionSelected = { minutes ->
                                                            minutesStateM4Fi.value =
                                                                minutes.toString().padStart(2, '0')
                                                            releMode4TimeOffValue.value =
                                                                "${hoursStateM4Fi.value}:${minutesStateM4Fi.value}:${secondsStateM4Fi.value}"

                                                        }
                                                    )
//                                                    CustomSpinner(
//                                                        modifier = Modifier.width(90.dp),
//                                                        options = (0..59).map {
//                                                            it.toString().padStart(2, '0')
//                                                        },
//                                                        valueDefault = secondsStateM4Fi.value.padStart(2, '0'),
//                                                        labelSt = "s",
//                                                        onOptionSelected = { seconds ->
//                                                            secondsStateM4Fi.value =
//                                                                seconds.toString().padStart(2, '0')
//                                                            releMode4TimeOffValue.value =
//                                                                "${hoursStateM4Fi.value}:${minutesStateM4Fi.value}:${secondsStateM4Fi.value}"
//
//                                                        }
//                                                    )

                                                }
                                            }
                                        }
                                    }

                                    "Цикл" -> {
//

                                        val hoursStateM5St = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode5TimeOn.lastOrNull()
                                                    ?.substringBefore(':') ?: "00"
                                            )
                                        }
                                        val minutesStateM5St = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode5TimeOn.lastOrNull()
                                                    ?.substringAfter(':')?.substringBefore(':')
                                                    ?: "00"
                                            )
                                        }
                                        val secondsStateM5St = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode5TimeOn.lastOrNull()
                                                    ?.substringAfterLast(':') ?: "00"
                                            )
                                        }

                                        val hoursStateM5Fi = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode5TimeOff.lastOrNull()
                                                    ?.substringBefore(':') ?: "00"
                                            )
                                        }
                                        val minutesStateM5Fi = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode5TimeOff.lastOrNull()
                                                    ?.substringAfter(':')?.substringBefore(':')
                                                    ?: "00"
                                            )
                                        }
                                        val secondsStateM5Fi = remember {
                                            mutableStateOf(
                                                mainViewModel.releMode5TimeOff.lastOrNull()
                                                    ?.substringAfterLast(':') ?: "00"
                                            )
                                        }

                                        Column {
                                            Text("Время работы цикла : ${mainViewModel.releMode5TimeOn.lastOrNull() ?: "00:00:00"}")

                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {

                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..23).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = hoursStateM5St.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "h",
                                                        onOptionSelected = { hours ->
                                                            hoursStateM5St.value =
                                                                hours.toString().padStart(2, '0')
                                                            releMode5TimeOnValue.value =
                                                                "${hoursStateM5St.value}:${minutesStateM5St.value}:${secondsStateM5St.value}"
                                                        }
                                                    )

                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..59).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = minutesStateM5St.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "m",
                                                        onOptionSelected = { minutes ->
                                                            minutesStateM5St.value =
                                                                minutes.toString().padStart(2, '0')
                                                            releMode5TimeOnValue.value =
                                                                "${hoursStateM5St.value}:${minutesStateM5St.value}:${secondsStateM5St.value}"
                                                        }
                                                    )
                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..59).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = secondsStateM5St.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "s",
                                                        onOptionSelected = { seconds ->
                                                            secondsStateM5St.value =
                                                                seconds.toString().padStart(2, '0')
                                                            releMode5TimeOnValue.value =
                                                                "${hoursStateM5St.value}:${minutesStateM5St.value}:${secondsStateM5St.value}"

                                                        }
                                                    )

                                                }
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text("Время паузы цикла : ${mainViewModel.releMode5TimeOff.lastOrNull() ?: "23:59:59"}")
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {

                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..23).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = hoursStateM5Fi.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "h",
                                                        onOptionSelected = { hours ->
                                                            hoursStateM5Fi.value =
                                                                hours.toString().padStart(2, '0')
                                                            releMode5TimeOffValue.value =
                                                                "${hoursStateM5Fi.value}:${minutesStateM5Fi.value}:${secondsStateM5Fi.value}"

                                                        }
                                                    )

                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..59).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = minutesStateM5Fi.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "m",
                                                        onOptionSelected = { minutes ->
                                                            minutesStateM5Fi.value =
                                                                minutes.toString().padStart(2, '0')
                                                            releMode5TimeOffValue.value =
                                                                "${hoursStateM5Fi.value}:${minutesStateM5Fi.value}:${secondsStateM5Fi.value}"

                                                        }
                                                    )
                                                    CustomSpinner(
                                                        modifier = Modifier.width(90.dp),
                                                        options = (0..59).map {
                                                            it.toString().padStart(2, '0')
                                                        },
                                                        valueDefault = secondsStateM5Fi.value.padStart(
                                                            2,
                                                            '0'
                                                        ),
                                                        labelSt = "s",
                                                        onOptionSelected = { seconds ->
                                                            secondsStateM5Fi.value =
                                                                seconds.toString().padStart(2, '0')
                                                            releMode5TimeOffValue.value =
                                                                "${hoursStateM5Fi.value}:${minutesStateM5Fi.value}:${secondsStateM5Fi.value}"

                                                        }
                                                    )

                                                }
                                            }
                                        }


                                    }
                                }
                            }
                        }

                        else -> {

                            OutlinedTextField(
                                value = textFieldValue1.value,
                                onValueChange = { newValue ->
                                    // Allow digits and at most one decimal point
                                    val filteredValue =
                                        newValue.filter { it.isDigit() || it == '.' }
                                    // Check if the filtered value has more than one decimal point
                                    if (filteredValue.count { it == '.' } <= 1) {
                                        textFieldValue1.value = filteredValue
                                    }
                                },
                                label = { Text(textIT1Resource) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            when (mainViewModel.screenState.value.dialogState) {
                                SETTINGS_COU -> TextButton(
                                    onClick = {
                                        mainViewModel.createEvent(
                                            ScreenEvent.ShowDialog(
                                                INVISIBLE
                                            )
                                        )
                                    }) {
                                    Text("Очистить")
                                }

                                SETTINGS_TAR -> TextButton(
                                    onClick = {
                                        mainViewModel.createEvent(
                                            ScreenEvent.ShowDialog(
                                                INVISIBLE
                                            )
                                        )
                                    }) {
                                    Text("Очистить")
                                }

                                SETTINGS_P -> {}
                                SETTINGS_TEMP -> {}

                                else -> OutlinedTextField(
                                    value = textFieldValue2.value,
                                    onValueChange = { newValue ->
                                        // Allow digits and at most one decimal point
                                        val filteredValue =
                                            newValue.filter { it.isDigit() || it == '.' }
                                        // Check if the filtered value has more than one decimal point
                                        if (filteredValue.count { it == '.' } <= 1) {
                                            textFieldValue2.value = filteredValue
                                        }
                                    },
                                    label = { Text(textIT2Resource) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = checkboxValue1.value,
                                    onCheckedChange = { checkboxValue1.value = it }
                                )
                                Text(text = textCB1Resource)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = checkboxValue2.value,
                                    onCheckedChange = { checkboxValue2.value = it }
                                )
                                Text(text = textCB2Resource)
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (
                    mainViewModel.screenState.value.dialogState != SETTINGS_COU &&
                    mainViewModel.screenState.value.dialogState != SETTINGS_TAR &&
                    mainViewModel.screenState.value.dialogState != SETTINGS_TEMP &&
                    mainViewModel.screenState.value.dialogState != ADD_NEW_REL //&&
//                    mainViewModel.screenState.value.dialogState != SETTINGS_REL
                ) {

//                    var isAlarmEventVisible by remember { mutableStateOf(mainViewModel.eventAlarmState.value) }

//                    Text(
//                        text = if (mainViewModel.screenState.value.dialogState == SETTINGS_REL) "Журнал" else "События",
//
//                        modifier = Modifier
//                            .clickable(
//                                onClick = {
//                                    isVisibleAlarmEventMenu = !isVisibleAlarmEventMenu
//                                },
//                                enabled = when (mainViewModel.screenState.value.dialogState) {
//                                    SETTINGS_I -> {
//                                        isAlarmEventVisibleState?.name == ("evi" ?: false)
//                                    }
//
//                                    SETTINGS_U -> {
//                                        isAlarmEventVisibleState?.name == ("evu" ?: false)
//                                    }
//
//                                    SETTINGS_P -> {
//                                        isAlarmEventVisibleState?.name == ("evp" ?: false)
//
//                                    }
//
//                                    SETTINGS_REL -> true
//                                    else -> false
//                                }
//                            ),
//                        fontSize = 22.sp
//                    )
                    var heightValue =
                        if (mainViewModel.screenState.value.dialogState == SETTINGS_REL) 500.dp else 150.dp
                    Column(
                        modifier = Modifier
                            .size(
                                width = if (isVisibleAlarmEventMenu) 300.dp else 0.dp,
                                height = if (isVisibleAlarmEventMenu) heightValue else 0.dp
                            )
                            .alpha(if (isVisibleAlarmEventMenu) 1f else 0f),
                    ) {

                        if (mainViewModel.screenState.value.dialogState == SETTINGS_REL) {
                            val itemsEvent = eventsListState.reversed()
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

                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(16.dp)
                                .alpha(
                                    when (mainViewModel.screenState.value.dialogState) {
                                        SETTINGS_I -> {
                                            if (isAlarmEventVisibleState?.name == "evi") 1.0f else 0.0f
                                        }

                                        SETTINGS_U -> {
                                            if (isAlarmEventVisibleState?.name == "evu") 1.0f else 0.0f
                                        }

                                        SETTINGS_P -> {
                                            if (isAlarmEventVisibleState?.name == "evp") 1.0f else 0.0f

                                        }

                                        else -> 0.0f
                                    }
                                ),
                            onClick = {

                                isVisibleAlarmEventMenu = !isVisibleAlarmEventMenu
                                mainViewModel.createEvent(ScreenEvent.DeleteEventAlarmFromServer(""))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Yellow,
                                contentColor = Color.Red
                            )
                        ) {
                            Text(
                                text = when (mainViewModel.screenState.value.dialogState) {
                                    SETTINGS_I -> "Warning! \nDate: ${isAlarmEventVisibleState?.timeev ?: ""}, \nValue: ${isAlarmEventVisibleState?.value ?: ""}"
                                    SETTINGS_U -> "Warning! \nDate: ${isAlarmEventVisibleState?.timeev ?: ""}, \nValue: ${isAlarmEventVisibleState?.value ?: ""}"
                                    SETTINGS_P -> "Warning! \nDate: ${isAlarmEventVisibleState?.timeev ?: ""}, \nValue: ${isAlarmEventVisibleState?.value ?: ""}"
                                    else -> ""
                                }
//                                text = "Warning! \nDate: 13-06-2024 14:13:12, \nValue: 221",
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(16.dp))

//
//                    if (mainViewModel.screenState.value.dialogState != SETTINGS_REL) {
//                        Text(
//                            text = "График",
//                            modifier = Modifier
//                                .clickable(onClick = {
//                                    isVisibleGraphicMenu = !isVisibleGraphicMenu
//                                }),
//                            fontSize = 22.sp
//                        )
//
//                        DisplayChartI(
//                            viewModel = mainViewModel,
//                            isFullScreen = isVisibleGraphicMenu,
//                            onCloseFullScreen = { isVisibleGraphicMenu = false } // Закрываем график
//                        )
//
//
////                        Column(
////                            modifier = Modifier
////                                .size(
////                                    width = if (isVisibleGraphicMenu) 300.dp else 0.dp,
////                                    height = if (isVisibleGraphicMenu) 100.dp else 0.dp
////                                )
////                                .alpha(if (isVisibleGraphicMenu) 1f else 0f),
////                        ) {
////                            Row(verticalAlignment = Alignment.CenterVertically) {
//////                                Checkbox(
//////                                    checked = checkboxValue3.value,
//////                                    onCheckedChange = { checkboxValue3.value = it }
//////                                )
////                                Text(text = textCB3Resource)
////                            }
////                        }
//                    }

                }
            }

        },

        confirmButton = {

            // Кнопка подтверждения действия
            TextButton(onClick = {

                when (mainViewModel.screenState.value.dialogState) {
                    SETTINGS_I -> {
                        mainViewModel.setTextFieldValue1(
                            textFieldValue1.value.toFloatOrNull()
                                ?: mainViewModel.textFieldValue1I.last()
                        )
                        mainViewModel.setTextFieldValue2(
                            textFieldValue2.value.toFloatOrNull()
                                ?: mainViewModel.textFieldValue2I.last()
                        )
                        mainViewModel.setCheckboxValue1(checkboxValue1.value)
                        mainViewModel.setCheckboxValue2(checkboxValue2.value)
                        mainViewModel.setCheckboxValue3(checkboxValue3.value)
                        mainViewModel.createEvent(ScreenEvent.OpenSettingsI(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                    }

                    SETTINGS_U -> {
                        mainViewModel.setTextFieldValue1U(
                            textFieldValue1.value.toFloatOrNull()
                                ?: mainViewModel.textFieldValue1U.last().toFloat()
                        )
                        mainViewModel.setTextFieldValue2U(
                            textFieldValue2.value.toFloatOrNull()
                                ?: mainViewModel.textFieldValue2U.last().toFloat()
                        )
                        mainViewModel.setCheckboxValue1U(checkboxValue1.value)
                        mainViewModel.setCheckboxValue2U(checkboxValue2.value)
                        mainViewModel.setCheckboxValue3U(checkboxValue3.value)
                        mainViewModel.createEvent(ScreenEvent.OpenSettingsU(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                    }

                    SETTINGS_P -> {
                        mainViewModel.setTextFieldValue1P(
                            textFieldValue1.value.toIntOrNull()
                                ?: mainViewModel.textFieldValue1P.last()
                        )
//                        mainViewModel.setTextFieldValue2P(
//                            textFieldValue2.value.toIntOrNull()
//                                ?: mainViewModel.textFieldValue2P.last()
//                        )
                        mainViewModel.setCheckboxValue1P(checkboxValue1.value)
                        mainViewModel.setCheckboxValue2P(checkboxValue2.value)
                        mainViewModel.setCheckboxValue3P(checkboxValue3.value)
                        mainViewModel.createEvent(ScreenEvent.OpenSettingsP(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                    }

                    INVISIBLE -> {}
                    SETTINGS_TAR -> {
                        mainViewModel.setTextFieldValue1TAR(
                            textFieldValue1.value.toIntOrNull()
                                ?: mainViewModel.textFieldValue1TAR.last()
                        )
                        mainViewModel.setTextFieldValue2TAR(
                            textFieldValue2.value.toIntOrNull()
                                ?: mainViewModel.textFieldValue2TAR.last()
                        )
                        mainViewModel.setCheckboxValue1TAR(checkboxValue1.value)
                        mainViewModel.setCheckboxValue2TAR(checkboxValue2.value)
                        mainViewModel.createEvent(ScreenEvent.OpenSettingsTAR(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                    }

                    SETTINGS_COU -> {
                        mainViewModel.setTextFieldValue1COU(
                            textFieldValue1.value.toIntOrNull()
                                ?: mainViewModel.textFieldValue1COU.last()
                        )
                        mainViewModel.setCheckboxValue1COU(checkboxValue1.value)
                        mainViewModel.setCheckboxValue2COU(checkboxValue2.value)
                        mainViewModel.createEvent(ScreenEvent.OpenSettingsCOU(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))

                    }

                    SETTINGS_TEMP -> {
                        mainViewModel.setTextFieldValue1T(
                            textFieldValue1.value.toIntOrNull()
                                ?: mainViewModel.textFieldValue1T.last()
                        )
//                        mainViewModel.setTextFieldValue2T(
//                            textFieldValue2.value.toIntOrNull()
//                                ?: mainViewModel.textFieldValue2T.last()
//                        )
                        mainViewModel.setCheckboxValue1T(checkboxValue1.value)
                        mainViewModel.setCheckboxValue2T(checkboxValue2.value)
                        mainViewModel.createEvent(ScreenEvent.OpenSettingsT(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                    }

                    SETTINGS_REL -> {

                        mainViewModel.createEvent(ScreenEvent.SendServerStopMode(""))

                        if (textFieldValue1.value.isNotBlank()) {
                            mainViewModel.setTextFieldValue1SETREL(textFieldValue1.value)
                        }
                        if (mainViewModel.releModeValue.last() == "На время") {
                            mainViewModel.set_releMode3TimeOn(releMode3TimeValue.value)
                        }
                        when (mainViewModel.releModeValue.last()) {

                            "Выключено" -> {

                                mainViewModel.createEvent(
                                    ScreenEvent.SendServerSettingsMode012(
                                        SendServerSettingsMode012DTO(
                                            dvid = deviceData.dvid,
                                            tkn = deviceData.tkn,
                                            typedv = deviceData.typedv,
                                            num = deviceData.num,
                                            com = "wv",
                                            rmode = 0,
                                        )
                                    )
                                )
                            }

                            "Тумблер" -> {
                                mainViewModel.createEvent(
                                    ScreenEvent.SendServerSettingsMode012(
                                        SendServerSettingsMode012DTO(
                                            dvid = deviceData.dvid,
                                            tkn = deviceData.tkn,
                                            typedv = deviceData.typedv,
                                            num = deviceData.num,
                                            com = "wv",
                                            rmode = 1,
                                        )
                                    )
                                )
                            }

                            "Кнопка" -> {
                                mainViewModel.createEvent(
                                    ScreenEvent.SendServerSettingsMode012(
                                        SendServerSettingsMode012DTO(
                                            dvid = deviceData.dvid,
                                            tkn = deviceData.tkn,
                                            typedv = deviceData.typedv,
                                            num = deviceData.num,
                                            com = "wv",
                                            rmode = 2,
                                        )
                                    )
                                )
                            }

                            "На время" -> {
                                mainViewModel.set_releMode3TimeOn(
                                    releMode3TimeValue.value.padStart(
                                        2,
                                        '0'
                                    )
                                )
                                mainViewModel.createEvent(
                                    ScreenEvent.SendServerSettingsMode3(
                                        SendServerSettingsMode3DTO(

                                            dvid = deviceData.dvid,
                                            tkn = deviceData.tkn,
                                            typedv = deviceData.typedv,
                                            num = deviceData.num,
                                            com = "wv",
                                            rmode = 3,
                                            tOn = releMode3TimeValue.value.padStart(2, '0')
                                        )
                                    )
                                )
                            }

                            "Таймер" -> {
                                mainViewModel.set_releMode4TimeOn(
                                    releMode4TimeOnValue.value.padStart(2, '0')
                                )
                                mainViewModel.set_releMode4TimeOff(
                                    releMode4TimeOffValue.value.padStart(2, '0')
                                )
                                mainViewModel.createEvent(
                                    ScreenEvent.SendServerSettingsMode4(
                                        SendServerSettingsMode4DTO(
//
                                            dvid = deviceData.dvid,
                                            tkn = deviceData.tkn,
                                            typedv = deviceData.typedv,
                                            num = deviceData.num,
                                            com = "wv",
                                            rmode = 4,
                                            tRTCOn = releMode4TimeOnValue.value.padStart(2, '0'),
                                            tRTCOff = releMode4TimeOffValue.value.padStart(2, '0')
                                        )
                                    )
                                )
                            }

                            "Цикл" -> {
                                mainViewModel.set_releMode5TimeOn(
                                    releMode5TimeOnValue.value.padStart(2, '0')
                                )
                                mainViewModel.set_releMode5TimeOff(
                                    releMode5TimeOffValue.value.padStart(2, '0')
                                )
                                mainViewModel.createEvent(
                                    ScreenEvent.SendServerSettingsMode5(
                                        SendServerSettingsMode5DTO(

                                            dvid = deviceData.dvid,
                                            tkn = deviceData.tkn,
                                            typedv = deviceData.typedv,
                                            num = deviceData.num,
                                            com = "wv",
                                            rmode = 5,
                                            tClOn = releMode5TimeOnValue.value.padStart(2, '0'),
                                            tClOff = releMode5TimeOffValue.value.padStart(2, '0')
                                        )
                                    )
                                )
                            }
                        }
                        mainViewModel.createEvent(ScreenEvent.SaveShPrefSettingsRel(""))
                        mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                    }

                    ERROR -> TODO()
                    ADD_NEW_REL -> TODO()
                    DialogState.SCREEN_NEW -> TODO()
                    DialogState.SCREEN_DEVICES -> TODO()
                    DialogState.SCREEN_LOG -> TODO()
                    DialogState.SCREEN_HOME -> TODO()
                }

                val sendSettingsDTO = SendSettingsDTO(

                    dvid = deviceData.dvid,
                    tkn = deviceData.tkn,
                    typedv = deviceData.typedv,
                    num = deviceData.num,
                    com = "wp",
                    prton = 5,
                    upm = if (mainViewModel.checkboxValue1U.last()) 1 else 0,
                    ulh = mainViewModel.textFieldValue1U.last(),
                    ull = mainViewModel.textFieldValue2U.last(),
                    ipm = if (mainViewModel.checkboxValue1I.last()) 1 else 0,
                    ilh = mainViewModel.textFieldValue1I.last(),
                    ill = mainViewModel.textFieldValue2I.last(),
                    ppm = if (mainViewModel.checkboxValue1P.last()) 1 else 0,
                    plh = mainViewModel.textFieldValue1P.last(),
                    tpm = if (mainViewModel.checkboxValue1P.last()) 1 else 0,
                    tlh = mainViewModel.textFieldValue1T.last(),
                )

                if (mainViewModel.screenState.value.dialogState != SETTINGS_REL
                ) {
                    mainViewModel.createEvent(ScreenEvent.SendSettingsServer(sendSettingsDTO))
                }

            }) {
                Text(text = "Ok")
            }
        },

        dismissButton = {
            // Кнопка отмены действия, если текущее состояние не ошибка
            if (mainViewModel.screenState.value.dialogState != ERROR) {
                TextButton(onClick = {
                    mainViewModel.createEvent(ScreenEvent.ShowDialog(INVISIBLE))
                }) {
                    Text(text = "Отклонить")
                }
            }
        },

        )
}


enum class DialogState {
    SETTINGS_I,
    SETTINGS_U,
    SETTINGS_P,
    INVISIBLE,
    SETTINGS_TAR,
    SETTINGS_COU,
    SETTINGS_TEMP,
    SETTINGS_REL,
    ADD_NEW_REL,
    ERROR,
    SCREEN_NEW,
    SCREEN_DEVICES,
    SCREEN_LOG,
    SCREEN_HOME
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSpinner(
    modifier: Modifier = Modifier,
    options: List<String>,
    valueDefault: String,
    labelSt: String,
    onOptionSelected: (Int) -> Unit
//    mainViewModel: MainViewModel,
//    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Форматируем значение для отображения (добавляем ведущий ноль если нужно)
    val displayedValue = remember(valueDefault) {
        valueDefault.toIntOrNull()?.toString()?.padStart(2, '0') ?: valueDefault.padStart(2, '0')
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            value = displayedValue,
            onValueChange = { /* Не изменять */ },
            readOnly = true,
            label = { Text(labelSt) },
//            label = { Text("Режимы работы реле") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    onClick = {
//                        mainViewModel.set_releModeValue(index)
//                        mainViewModel.createEvent(ScreenEvent.SaveShPrefSettingsRel(""))
                        onOptionSelected(index)
                        expanded = false
                    },
                    text = { Text(text = option) }
                )
            }
        }
    }
}

fun formatTriple(triple: Triple<Int, Int, Int>): String {
    return "${triple.first.toString().padStart(2, '0')}:${
        triple.second.toString().padStart(2, '0')
    }:${triple.third.toString().padStart(2, '0')}"
}

fun formatPair(pair: Pair<Int, Int>): String {
    return "${pair.first.toString().padStart(2, '0')}:${pair.second.toString().padStart(2, '0')}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWithSeconds() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("") }
//    val clockState = rememberClockState()

    Button(onClick = { showDialog = true }) {
        Text("Выбрать время")
    }

    if (showDialog) {
        ClockDialog(
            selection = ClockSelection.HoursMinutesSeconds { h, m, s ->
                selectedTime = "$h:$m:$s"
                showDialog = false
            },
            config = ClockConfig(is24HourFormat = true),
            state = UseCaseState(),
        )
    }

    Text("Выбрано: $selectedTime")
}