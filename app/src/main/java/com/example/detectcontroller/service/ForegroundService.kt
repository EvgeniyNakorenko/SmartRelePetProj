package com.example.detectcontroller.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import co.yml.charts.common.extensions.isNotNull
import com.example.detectcontroller.R
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.DeleteEventDTO
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.domain.DBRepository
import com.example.detectcontroller.domain.db.InsertLastEventServerInDBUseCase
import com.example.detectcontroller.domain.server.CheckServerEventUseCase
import com.example.detectcontroller.domain.server.DeleteEventServerUseCase
import com.example.detectcontroller.domain.server.FetchDataUseCase
import com.example.detectcontroller.ui.presentation.MainActivity
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.I_TEXT_FIELD_VALUE2
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.P_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE1
import com.example.detectcontroller.ui.presentation.MainViewModel.Companion.U_TEXT_FIELD_VALUE2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class ForegroundService : Service() {

    @Inject
    lateinit var dBRepository: DBRepository
    private lateinit var notificationManager: NotificationManager

    private var isStarted = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private val fetchDataUseCase = FetchDataUseCase()
    private lateinit var deleteEventServerUseCase: DeleteEventServerUseCase
    private lateinit var checkServerEventUseCase: CheckServerEventUseCase
    private lateinit var preferences: SharedPreferences
    private lateinit var deviceData: RequestDataDTO
    private var goModeOff: Boolean = false


    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferences = application.getSharedPreferences(REL_SETTINGS, Context.MODE_PRIVATE)
        goModeOff = preferences.getBoolean(RELE_MODE_GO, false) ?: false

        checkServerEventUseCase = CheckServerEventUseCase()
        deleteEventServerUseCase = DeleteEventServerUseCase()
        deviceData = getGegData("rs")
    }

    private fun getGegData(com: String): RequestDataDTO {
        val requestEv: RequestDataDTO
        val reqDVID = preferences.getString(REG_DVID, "") ?: ""
        val reqTKN = preferences.getString(REG_TKN, "") ?: ""
        val reqTYPEVDString = preferences.getString(REG_TYPEDV, "")
        val reqTYPEVD = if (!reqTYPEVDString.isNullOrEmpty()) {
            reqTYPEVDString.toInt()
        } else 0
        val reqNUMString = preferences.getString(REG_NUM, "")
        val reqNUM = if (!reqNUMString.isNullOrEmpty()) {
            reqNUMString.toInt()
        } else 0

        requestEv = RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, com)
        return requestEv
    }

    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            makeForeground()
            startEventServerLoading()
            startDataLoading()
            isStarted = true
        }
        return START_STICKY
    }

    private fun makeForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createServiceNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Запущен фоновый процесс releMonitoring")
            .setContentText("Запущена проверка событий")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun startEventServerLoading() {
        scope.launch {
            var regData: RegServerEntity? = null

            while (isActive) {

                try {
                    val requestEv: RequestDataDTO

                    if (regData == null) {
                        try {
                            regData = dBRepository.getAllRegServer().lastOrNull()
//                            regData = getAllRegServerFromDBUseCase.execute().lastOrNull()
                        } catch (e: Exception) {
                            Log.e(TAG, "download reg data error", e)
                        }
                    }

                    if (!regData.isNotNull()) {
                        println("no regData")
                    } else {


                        val loadReq = regData?.let {
                            RequestDataDTO(
                                dvid = regData.dvid,
                                tkn = regData.tkn,
                                typedv = regData.typedv,
                                num = regData.num,
                                com = "rev"
                            )
                        }

                        val event = loadReq?.let { checkServerEventUseCase.execute(it) }
                        delay(10)

                        fun convertToUnixTimestamp(dateTimeString: String): Long {
                            // Формат для "день-месяц-год час:минута:секунда"
                            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                            val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
                            return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                        }

                        event?.onSuccess { res ->
                            res.value = res.value.drop(3)
                            res.timeev = convertToUnixTimestamp(res.timeev).toString()
                            when (res.name) {

                                "evi" -> {

                                    val minValue = preferences.getFloat(I_TEXT_FIELD_VALUE1, 1f)
                                    val maxValue = preferences.getFloat(I_TEXT_FIELD_VALUE2, 1f)
                                    when {
                                        minValue > res.value.toFloat() -> res.value =
                                            "${res.value} Ампер. ЗНАЧЕНИЕ НИЖЕ НОРМЫ.\n" +
                                                    "ЗАЩИТА ПО ТОКУ."

                                        maxValue < res.value.toFloat() -> res.value =
                                            "${res.value} Ампер. ЗНАЧЕНИЕ ВЫШЕ НОРМЫ.\n" +
                                                    "ЗАЩИТА ПО ТОКУ."
                                    }

                                }

                                "evu" -> {
                                    val minValue = preferences.getFloat(U_TEXT_FIELD_VALUE1, 1f)
                                    val maxValue = preferences.getFloat(U_TEXT_FIELD_VALUE2, 1f)
                                    when {
                                        minValue > res.value.toFloat() -> res.value =
                                            "${res.value} Вольт. ЗНАЧЕНИЕ НИЖЕ НОРМЫ.\n" +
                                                    "ЗАЩИТА ПО НАПРЯЖЕНИЮ."

                                        maxValue < res.value.toFloat() -> res.value =
                                            "${res.value} Вольт. ЗНАЧЕНИЕ ВЫШЕ НОРМЫ.\n" +
                                                    "ЗАЩИТА ПО НАПРЯЖЕНИЮ."
                                    }
                                }

                                "evp" -> {
                                    val maxValue = preferences.getInt(P_TEXT_FIELD_VALUE1, 1)
                                    when {

                                        maxValue < res.value.toInt() -> res.value =
                                            "${res.value} Ватт. ЗНАЧЕНИЕ ВЫШЕ НОРМЫ.\n" +
                                                    "ЗАЩИТА ПО МОЩНОСТИ."
                                    }
                                }

                                "evt" -> {

                                }
                            }

//                        preferences.getFloat(I_TEXT_FIELD_VALUE1


                            dBRepository.saveEventServerInDB(res)
                            dBRepository.insertLastEventServerDB(
                                LastEventsServerEntity(
                                    id = res.id,
                                    timeev = res.timeev,
                                    rstate = res.rstate,
                                    value = res.value,
                                    name = res.name
                                )
                            )
//                        insertLastEventServerInDBUseCase.execute(
//                            LastEventsServerEntity(
//                                id = res.id,
//                                timeev = res.timeev,
//                                rstate = res.rstate,
//                                value = res.value,
//                                name = res.name
//                            )
//                        )
                            delay(10)

                            preferences
                                .edit()
                                .putInt("EVENT_ID", res.id)
                                .putString("EVENT_VALUE", res.value)
                                .putString("EVENT_NAME", res.name)
                                .putString("EVENT_RSTATE", res.rstate)
                                .putString("EVENT_TIMEEV", res.timeev)
                                .apply()

                            if (res.name != "gomode") {
                                showNotification("Внимание, новое событие", res.toString())

                                deleteEventServerUseCase.execute(
                                    deleteEventDTO = DeleteEventDTO(
                                        dvid = loadReq.dvid,
                                        tkn = loadReq.tkn,
                                        typedv = loadReq.typedv,
                                        num = loadReq.num,
                                        com = "del",
                                        id = res.id
                                    )
                                ).onSuccess { println("Событие удалено") }
                            }

                        }?.onFailure { error ->
                            error.printStackTrace()
                        }
                    }
                    delay(3000)
                } catch (e: Exception) {
                    Log.e(TAG, "download error", e)
                }
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Установите приоритет
            .build()
        notificationManager.notify(
            Random.nextInt(1, Int.MAX_VALUE),
            notification
        )
    }

    private fun startDataLoading() {

        scope.launch {
            ////////////////////////////////////////////

            var regData: RegServerEntity?
            regData = null
            var delay = 4000L
            ////////////////////////////////////////////
            while (isActive) {
                delay(10)

                if (regData == null) {
                    try {
                        regData = dBRepository.getAllRegServer().lastOrNull()
//                        regData = getAllRegServerFromDBUseCase.execute().lastOrNull()
                    } catch (e: Exception) {
                        Log.e(TAG, "download reg data error", e)
                    }
                }
                try {
                    if (!regData.isNotNull()) println("no regData")
                    val loadReq = regData?.let {
                        RequestDataDTO(
                            dvid = it.dvid,
                            tkn = regData.tkn,
                            typedv = regData.typedv,
                            num = regData.num,
                            com = regData.com
                        )
                    }

                    ////////////////////////////////////////////
//                    repeat(9) {
//                        saveDataInDBUseCase.execute(
//                            UiStateDTO(
//                                "timedv: ${Random.nextInt(1, 31)}-06-2024 14:13:12",
//                                "----0",
//                                "----${Random.nextInt(200, 230)}",
//                                "----${Random.nextInt(2, 6)}",
//                                "----${Random.nextInt(800, 1200)}",
//                                "----${Random.nextInt(48, 52)}",
//                                "----${Random.nextInt(20, 31)}"
//                            )
//                        )
//                    }
                    delay(100)
                    ////////////////////////////////////////////

                    val dataFromServer = loadReq?.let { fetchDataUseCase.execute(it) }
                    dataFromServer?.onSuccess { res ->
                        dBRepository.saveDataServerInDB(res)
//                        saveDataInDBUseCase.execute(res)

                    }?.onFailure { error ->
                        error.printStackTrace()
                    }
                    delay(delay)
//                    delay *= 2
//                    preferences.edit().putBoolean(B_VIS,true).apply()
                } catch (e: Exception) {
                    Log.e(TAG, "download error", e)
                }
            }
        }
    }

    companion object {
        const val RELE_MODE_GO = "releModeGO"
        const val REG_DVID = "dvid"
        const val REG_TKN = "tkn"
        const val REG_TYPEDV = "typedv"
        const val REG_NUM = "num"
        const val REG_COM = "com"
        const val REL_SETTINGS = "rel_settings"
        const val TAG = "ForegroundService"
        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "1001"
        fun startService(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            context.startForegroundService(intent)
        }
    }
}
