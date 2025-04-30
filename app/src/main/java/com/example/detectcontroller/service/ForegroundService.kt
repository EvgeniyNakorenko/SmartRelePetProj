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
import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.DeleteEventDTO
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.db.GetAllRegServerFromDBUseCase
import com.example.detectcontroller.domain.db.GetOneLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.InsertLastEventServerInDBUseCase
import com.example.detectcontroller.domain.db.LoadLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.SaveDataInDBUseCase
import com.example.detectcontroller.domain.db.SaveEventServerInDBUseCase
import com.example.detectcontroller.domain.server.CheckServerEventUseCase
import com.example.detectcontroller.domain.server.DeleteEventServerUseCase
import com.example.detectcontroller.domain.server.FetchDataUseCase
import com.example.detectcontroller.ui.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class ForegroundService() : Service() {

    private lateinit var notificationManager: NotificationManager

    // onStartCommand can be called multiple times, so we keep track of "started" state manually
    private var isStarted = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private val fetchDataUseCase = FetchDataUseCase()

    private lateinit var database: AppDatabase
    private lateinit var saveDataInDBUseCase: SaveDataInDBUseCase

    private lateinit var saveEventUseCase: SaveEventServerInDBUseCase
    private lateinit var loadLastEventServerFromDBUseCase: LoadLastEventServerFromDBUseCase
    private lateinit var getOneLastEventServerFromDBUseCase: GetOneLastEventServerFromDBUseCase
    private lateinit var getAllRegServerFromDBUseCase: GetAllRegServerFromDBUseCase
    private lateinit var insertLastEventServerInDBUseCase: InsertLastEventServerInDBUseCase
    private lateinit var deleteEventServerUseCase: DeleteEventServerUseCase
    private lateinit var checkServerEventUseCase: CheckServerEventUseCase
    private lateinit var preferences: SharedPreferences
    private lateinit var deviceData: RequestDataDTO
    private var goModeOff: Boolean = false


//    val requestDataDTO = RequestDataDTO(
//        "0123456789qsrt1",
//        "",
//        5,
//        4,
//        "rs"
//    )

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferences = application.getSharedPreferences(REL_SETTINGS, Context.MODE_PRIVATE)
        goModeOff = preferences.getBoolean(RELE_MODE_GO, false) ?: false

        database = AppDatabase.getDatabase(this)
        saveDataInDBUseCase = SaveDataInDBUseCase(database)
        saveEventUseCase = SaveEventServerInDBUseCase(database)
        loadLastEventServerFromDBUseCase = LoadLastEventServerFromDBUseCase(database)
        getOneLastEventServerFromDBUseCase = GetOneLastEventServerFromDBUseCase(database)
        getAllRegServerFromDBUseCase = GetAllRegServerFromDBUseCase(database)
        insertLastEventServerInDBUseCase = InsertLastEventServerInDBUseCase(database)
        checkServerEventUseCase = CheckServerEventUseCase()
        deleteEventServerUseCase = DeleteEventServerUseCase()
        deviceData = getGegData("rs")
//        deviceData = RequestDataDTO("0123456789qsrt1", "", 5, 4, "rs")
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
            startDataLoading(deviceData)
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
            while (isActive) {
                try {
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

                    val reqCOM = "rev"
//                    requestEv = RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, reqCOM)
                    requestEv = deviceData.also { it.com = "rev"}

                    val event = checkServerEventUseCase.execute(requestEv)
                    delay(100)

                    var lastEvent: StatusEventServerDTO? =
                        getOneLastEventServerFromDBUseCase.execute(
                            preferences.getInt(
                                "EVENT_ID",
                                0
                            )
                        )

                    event.onSuccess { res ->
                        if (lastEvent != res) {
                            if (res.name == "gomode") {
                                deleteEventServerUseCase.execute(
                                    deleteEventDTO = DeleteEventDTO(
                                        dvid = deviceData.dvid,
                                        tkn = deviceData.tkn,
                                        typedv = deviceData.typedv,
                                        num = deviceData.num,
                                        com = "del",
                                        id = res.id
                                    )
                                ).onSuccess {
                                    if (goModeOff) {
                                        preferences.edit().putBoolean(RELE_MODE_GO, true).apply()
                                    } else {
                                        preferences.edit().putBoolean(RELE_MODE_GO, false).apply()
                                    }
                                }
                            } else {
                                saveEventUseCase.execute(res)
                                insertLastEventServerInDBUseCase.execute(
                                    LastEventsServerEntity(
                                        id = res.id,
                                        timeev = res.timeev,
                                        rstate = res.rstate,
                                        value = res.value,
                                        name = res.name
                                    )
                                )
                                delay(100)
                                showNotification("Внимание, новое событие", res.toString())
                                preferences
                                    .edit()
                                    .putInt("EVENT_ID", res.id)
                                    .putString("EVENT_VALUE", res.value)
                                    .putString("EVENT_NAME", res.name)
                                    .putString("EVENT_RSTATE", res.rstate)
                                    .putString("EVENT_TIMEEV", res.timeev)
                                    .apply()
                            }

                        }
                    }.onFailure { error ->
                        error.printStackTrace()
                    }
                    delay(5000)
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

    private fun startDataLoading(deviceData: RequestDataDTO) {
//        val requestDataDTO = RequestDataDTO(
//            "0123456789qsrt1",
//            "",
//            5,
//            4,
//            "rs"
//        )
        val requestDataDTO = deviceData

        scope.launch {
            ////////////////////////////////////////////
//            repeat(9) {
//                saveDataInDBUseCase.execute(
//                    UiState(
//                        "timedv: ${Random.nextInt(1, 31)}-06-2024 14:13:12",
//                        "----0",
//                        "----${Random.nextInt(200, 230)}",
//                        "----${Random.nextInt(2, 6)}",
//                        "----${Random.nextInt(800, 1200)}",
//                        "----${Random.nextInt(48, 52)}",
//                        "----${Random.nextInt(20, 31)}"
//                    )
//                )
//            }
            delay(100)
            var regData: RegServerEntity?
            regData = null
            try {
                regData = getAllRegServerFromDBUseCase.execute().lastOrNull()
            } catch (e: Exception) {
                Log.e(TAG, "download reg data error", e)
            }

            ////////////////////////////////////////////
            while (isActive) {
                try {
                    if (!regData.isNotNull()) println("no regData")
//                    val loadReq = regData?.let {
//                        RequestDataDTO(
//                            dvid = it.dvid,
//                            tkn = regData.tkn,
//                            typedv = regData.typedv,
//                            num = regData.num,
//                            com = regData.com
//                        )
//                    }
                    val loadReq = deviceData.also { it.com = "rs" }

//                    val fact = fetchDataUseCase.execute(requestDataDTO)
                    val fact = loadReq?.let { fetchDataUseCase.execute(it) }
                    fact?.onSuccess { res ->
                        saveDataInDBUseCase.execute(res)
                    }?.onFailure { error ->
                        error.printStackTrace()
                    }
                    delay(5000)
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
