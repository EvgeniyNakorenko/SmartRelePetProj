package com.example.detectcontroller.ui.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Surface
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.detectcontroller.R
import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.db.DeleteLastEventServerByIdFromDBUseCase
import com.example.detectcontroller.domain.db.GetAllRegServerFromDBUseCase
import com.example.detectcontroller.domain.db.GetOneLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.InsertRegServerInDBUseCase
import com.example.detectcontroller.domain.db.LoadDataFromDBUseCase
import com.example.detectcontroller.domain.db.LoadEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.LoadLastEventServerFromDBUseCase
import com.example.detectcontroller.service.ForegroundService
import com.example.detectcontroller.service.ForegroundService.Companion.TAG
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.P_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.RELE_MODE_GO
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.TAR_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.TAR_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.T_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var getAllRegServerFromDBUseCase: GetAllRegServerFromDBUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isIgnoringBatteryOptimizations(this)) requestIgnoreBatteryOptimization(this)
//        ForegroundService.startService(applicationContext)
        ForegroundService.startService(this)

        askNotificationPermission()

        val database = AppDatabase.getDatabase(this)
        val loadDataFromDBUseCase = LoadDataFromDBUseCase(database)
        val loadEventServerFromDBUseCase = LoadEventServerFromDBUseCase(database)
        val loadLastEventServerFromDBUseCase = LoadLastEventServerFromDBUseCase(database)
        val deleteLastEventServerByIdFromDBUseCase =
            DeleteLastEventServerByIdFromDBUseCase(database)
        val getOneLastEventServerFromDBUseCase = GetOneLastEventServerFromDBUseCase(database)
        val insertRegServerInDBUseCase = InsertRegServerInDBUseCase(database)
        getAllRegServerFromDBUseCase = GetAllRegServerFromDBUseCase(database)

        val viewModelFactory =
            MainViewModelFactory(
                this.application,
                this,
                loadDataFromDBUseCase,
                loadEventServerFromDBUseCase,
                loadLastEventServerFromDBUseCase,
                deleteLastEventServerByIdFromDBUseCase,
                getOneLastEventServerFromDBUseCase,
                insertRegServerInDBUseCase
            )


        val viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        startBackgroundTask(viewModel)

        val preferences: SharedPreferences =
            this.getSharedPreferences(MainViewModel.REL_SETTINGS, Context.MODE_PRIVATE)

//        val requestDataDTO = RequestDataDTO(
//            "0123456789qsrt1", "secur123456789", 5, 4, "rv"
//        )
//        viewModel.createEvent(ScreenEvent.GetServerSettings(requestDataDTO))

        setContent {
            MyApp(viewModel, preferences)
        }

        if (!(preferences.getBoolean(RELE_MODE_GO, false) ?: false)) {
            viewModel.set_releModeGO(false)
        } else viewModel.set_releModeGO(true)


    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
//        ForegroundService.stopService(this)
        viewModelJob.cancel()
    }



    private fun startBackgroundTask(viewModel: MainViewModel) {
        uiScope.launch {
            while (isActive) {

                var regData: RegServerEntity?
                regData = null
                try {
                    regData = getAllRegServerFromDBUseCase.execute().lastOrNull()
                } catch (e: Exception) {
                    Log.e(TAG, "download reg data error", e)
                }

                // Выполнение в фоновом потоке
//                val requestDataDTO = RequestDataDTO(
//                    "0123456789qsrt1", "secur123456789", 5, 4, "rv"
//                )
                withContext(Dispatchers.IO) {

                    if (regData != null) {
                        viewModel.createEvent(
                            ScreenEvent.GetServerSettings(
                                RequestDataDTO(
                                    regData.dvid,
                                    regData.tkn,
                                    regData.typedv,
                                    regData.num,
                                    com = "rv"

                                )
                            )
                        )
                    }


                }
                delay(60_000)
            }
        }
    }

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun requestIgnoreBatteryOptimization(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Разрешение на уведомления предоставлено", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Разрешение уже предоставлено
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Показать объяснение, почему нужно разрешение
                }

                else -> {
                    // Запросить разрешение
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

}

data class Item(
    val text: String,
    val description: String,
    val color: Color,
    val eventAlarm: StatusEventServerDTO?
)

@Composable
fun TopBar(mainViewModel: MainViewModel, preferences: SharedPreferences) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Color.Red, shape = RoundedCornerShape(16.dp))

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(4.dp)
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "      Устройства", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = {
//                showDialog.value = true
                mainViewModel.createEvent(
                    ScreenEvent.ShowDialog(
                        DialogState.ADD_NEW_REL
                    )
                )
            }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_circle_outline_24),
                    contentDescription = "Настройки",

                    )
            }
        }
    }
}

@Composable
fun SingleDevice(
    mainViewModel: MainViewModel,
    preferences: SharedPreferences,
) {
//    mainViewModel.createEvent(ScreenEvent.LoadLastEventServerFromDB(""))

    val uiState by mainViewModel.uiState.collectAsState()
//    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            mainViewModel.createEvent(ScreenEvent.LoadLastEventServerFromDB(""))
            mainViewModel.createEvent(ScreenEvent.LoadEventServerFromDB(""))
            delay(5000)
        }
    }
    val finalEventState by mainViewModel.finalEventState.collectAsState()

    val releName = mainViewModel.textFieldValue1SETREL.last().toString()
    val releMode = mainViewModel.releModeValue.last()

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Color.Red, shape = RoundedCornerShape(20.dp))
            .background(
                Color.White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(
                        Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

//                var isEnabled by remember { mutableStateOf(true) }
                val releModeGoVisVal by mainViewModel.buttonGoVisib.collectAsState()

                val isChecked by mainViewModel.releModeGO.collectAsState()
//                var isChecked by remember { mutableStateOf(releModeGoVisVal) }


                Text(text = "  $releName", style = MaterialTheme.typography.bodyMedium)

                ToggleIconButton(
                    isChecked = isChecked,
                    onCheckedChange = {
                        mainViewModel.set_buttonGoVisib(false)
                        mainViewModel.createEvent(ScreenEvent.SendServerGoMode(""))
//                        isEnabled = false
                    },
                    contentDescription = "Переключатель",
                    enabled = releModeGoVisVal,
                )


//                Column(
//                    modifier = Modifier.padding(4.dp),
////                    horizontalAlignment = Alignment.Start // ← Выравнивание всех детей по левому краю
//                ) {
//                    Text(
//                        text = "режим: $releMode",
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier.align(Alignment.Start).padding(4.dp) // ← Только горизонтальное выравнивание
//                    )
//                    Row(
//                        modifier = Modifier
//                            .padding(4.dp)
//                            .align(Alignment.Start) // ← Только горизонтальное выравнивание
//                    ) {
//                        Text(text = "состояние: ", style = MaterialTheme.typography.bodyMedium)
//                        Text(
//                            text = "вкл",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Blue
//                        )
//                    }
//                }

                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "режим: $releMode", style = MaterialTheme.typography.bodyMedium)
                    Row() {
//                    Row(modifier = Modifier.padding(4.dp)) {
                        Text(text = "состояние: ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "вкл", style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }
                }

                IconButton(
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
            }
            if (uiState.error != null) {
                Text(text = uiState.error!!)
            } else {
                val items = listOf(
                    Item(
                        uiState.url,
                        "Напряжение, В",
                        Color.Magenta,
                        if ((finalEventState?.name ?: "") == "evu") finalEventState else null
                    ),
                    Item(
                        uiState.irl,
                        "Ток, А",
                        Color(206, 210, 58),
                        if ((finalEventState?.name ?: "") == "evi") finalEventState else null
                    ),
                    Item(
                        uiState.pwr,
                        "Мощность, Вт",
                        Color.Cyan,
                        if ((finalEventState?.name ?: "") == "evp") finalEventState else null
                    ),
                    Item(
                        uiState.tmp,
                        "Температура, °C",
                        Color(0xFFFFC0CB),
                        if ((finalEventState?.name ?: "") == "evt") finalEventState else null
                    ),
                    Item(
                        "Count",
                        "Счетчик, кВт*ч",
                        Color.Gray,
                        null
                    ), // Swapped position
                    Item(
                        "Tar",
                        "Тарификатор, ₽",
                        Color(255, 165, 0),
                        null
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items) { item ->

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
                                            tint = Color.Yellow,
                                            modifier = Modifier.alpha(if (item.eventAlarm != null) 1.0f else 0.0f)

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
            if (
                mainViewModel.checkboxValue3I.last() || mainViewModel.checkboxValue3U.last() || mainViewModel.checkboxValue3P.last()
            ) {
                LineChartScreen().GraphSwitcher(mainViewModel)
            }
        }
    }
}

@Composable
fun MyApp(mainViewModel: MainViewModel, preferences: SharedPreferences) {

//    LaunchedEffect(Unit) {
//        while (true) {
//            mainViewModel.createEvent(ScreenEvent.LoadLastEventServerFromDB(""))
//            delay(5000)
//        }
//    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFAFEEEE))
                .padding(8.dp)
        ) {
            Column {
                TopBar(mainViewModel, preferences)
                SingleDevice(mainViewModel, preferences)
            }
        }
        if (mainViewModel.screenState.value.dialogState != DialogState.INVISIBLE) {
            Dialog(mainViewModel)
        }
    }
}

