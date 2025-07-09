package com.example.detectcontroller.ui.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.DeleteEventDTO
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode1
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode3
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode4
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode5
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode6
import com.example.detectcontroller.data.remote.remDTO.RegResponseDTO
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode012DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode3DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode4DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode5DTO
import com.example.detectcontroller.data.remote.remDTO.SendSettingsDTO
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.UiStateDTO
import com.example.detectcontroller.domain.DBRepository
import com.example.detectcontroller.domain.models.ErrorServerMod
import com.example.detectcontroller.domain.registration.RegGetDataWIFIUseCase
import com.example.detectcontroller.domain.registration.RegSendDataWIFIUseCase
import com.example.detectcontroller.domain.server.DeleteEventServerUseCase
import com.example.detectcontroller.domain.server.FetchDataUseCase
import com.example.detectcontroller.domain.server.GetServerSettingsUseCase
import com.example.detectcontroller.domain.server.SendServerGoModeUseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode012UseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode3UseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode4UseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode5UseCase
import com.example.detectcontroller.domain.server.SendSettingsServerUseCase
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.INVISIBLE
import com.example.detectcontroller.ui.presentation.composeFunc.DialogState.SCREEN_HOME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class ListState(
    var iEventAlarmVisible: Boolean = false,
    var iEventAlarmValue: String = "",
    var iEventAlarmDate: String = "",
    var iEventAlarmId: Int = 0,

    var uEventAlarmVisible: Boolean = false,
    var uEventAlarmValue: String = "",
    var uEventAlarmDate: String = "",
    var uEventAlarmId: Int = 0,

    var pEventAlarmVisible: Boolean = false,
    var pEventAlarmValue: String = "",
    var pEventAlarmDate: String = "",
    var pEventAlarmId: Int = 0,

    var eventAlarmVisible: Boolean = false,
    val const: Boolean = false,
    var dialogState: DialogState = INVISIBLE,
    var currentScreen: DialogState = SCREEN_HOME,
    var counter: Int = 0
)

sealed class ScreenEvent {
    data class OpenSettingsI(val value: String) : ScreenEvent()
    data class OpenSettingsTAR(val value: String) : ScreenEvent()
    data class OpenSettingsCOU(val value: String) : ScreenEvent()
    data class OpenSettingsU(val value: String) : ScreenEvent()
    data class OpenSettingsP(val value: String) : ScreenEvent()
    data class OpenSettingsT(val value: String) : ScreenEvent()
    data class SaveShPrefSettingsRel(val value: String) : ScreenEvent()
    data class OpenScreenRel(val value: String) : ScreenEvent()
    data class LoadEventServerFromDB(val value: String) : ScreenEvent()
    data class LoadLastEventServerFromDB(val value: String) : ScreenEvent()
    data class GetErrors(val value: String) : ScreenEvent()

//    data class ShowEventAlarmI(val value: Boolean) : ScreenEvent()
//    data class ShowEventAlarmU(val value: Boolean) : ScreenEvent()
//    data class ShowEventAlarmP(val value: Boolean) : ScreenEvent()

    data class DeleteEventAlarmFromServer(val value: String) : ScreenEvent()

    data class ShowDialog(val value: DialogState) : ScreenEvent()
    data class ShowScreen(val value: DialogState) : ScreenEvent()
    data class RegSMS(val value: String) : ScreenEvent()
    data class RegWIFI(val value: String) : ScreenEvent()
    data class SendSettingsServer(val value: SendSettingsDTO) : ScreenEvent()
    data class SendServerSettingsMode3(val value: SendServerSettingsMode3DTO) : ScreenEvent()
    data class SendServerSettingsMode4(val value: SendServerSettingsMode4DTO) : ScreenEvent()
    data class SendServerSettingsMode5(val value: SendServerSettingsMode5DTO) : ScreenEvent()
    data class SendServerSettingsMode012(val value: SendServerSettingsMode012DTO) : ScreenEvent()
    data class SendServerGoMode(val value: String) : ScreenEvent()
    data class GetServerSettings(val value: RequestDataDTO) : ScreenEvent()

    data class SendServerStopMode(val value: String) : ScreenEvent()
    data class GetAllRegServerFromDB(val value: String) : ScreenEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val dBRepository: DBRepository

) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val contextIn = application.applicationContext
    //    private val contextIn = context
    private val regGetDataWIFIUseCase = RegGetDataWIFIUseCase()
    private val regSendDataWIFIUseCase = RegSendDataWIFIUseCase()
    private val deleteEventServerUseCase = DeleteEventServerUseCase()
    private val sendSettingsServerUseCase = SendSettingsServerUseCase()
    private val fetchDataUseCase = FetchDataUseCase()
    private val sendServerSettingsMode3UseCase = SendServerSettingsMode3UseCase()
    private val sendServerSettingsMode4UseCase = SendServerSettingsMode4UseCase()
    private val sendServerSettingsMode5UseCase = SendServerSettingsMode5UseCase()
    private val sendServerSettingsMode012UseCase = SendServerSettingsMode012UseCase()
    private val sendServerGoModeUseCase = SendServerGoModeUseCase()
//    private val getAllRegServerFromDBUseCase = GetAllRegServerFromDBUseCase(database)

    private val getServerSettingsUseCase = GetServerSettingsUseCase()
    private var gomodeVar: Boolean = false
    private var toastJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        const val REL_SETTINGS = "rel_settings"
        const val RELE_MODE_VALUE = "releModeValue"
        const val RELE_MODE3_TIME_ON = "releMode3TimeOff"
        const val RELE_MODE4_TIME_ON = "releMode4TimeOn"
        const val RELE_MODE4_TIME_OFF = "releMode4TimeOff"
        const val RELE_MODE5_TIME_ON = "releMode5TimeOn"
        const val RELE_MODE5_TIME_OFF = "releMode5TimeOff"

        const val RELE_MODE_GO = "releModeGO"
        const val B_VIS = "B_VIS"

        const val I_TEXT_FIELD_VALUE1 = "textFieldValue1I"
        const val I_TEXT_FIELD_VALUE2 = "textFieldValue2I"
        const val I_CHECKBOX_VALUE1 = "checkboxValue1I"
        const val I_CHECKBOX_VALUE2 = "checkboxValue2I"
        const val I_CHECKBOX_VALUE3 = "checkboxValue3I"

        const val U_TEXT_FIELD_VALUE1 = "textFieldValue1U"
        const val U_TEXT_FIELD_VALUE2 = "textFieldValue2U"
        const val U_CHECKBOX_VALUE1 = "checkboxValue1U"
        const val U_CHECKBOX_VALUE2 = "checkboxValue2U"
        const val U_CHECKBOX_VALUE3 = "checkboxValue3U"

        const val P_TEXT_FIELD_VALUE1 = "textFieldValue1P"

        //        const val P_TEXT_FIELD_VALUE2 = "textFieldValue2P"
        const val P_CHECKBOX_VALUE1 = "checkboxValue1P"
        const val P_CHECKBOX_VALUE2 = "checkboxValue2P"
        const val P_CHECKBOX_VALUE3 = "checkboxValue3P"

        const val T_TEXT_FIELD_VALUE1 = "textFieldValue1T"

        //        const val T_TEXT_FIELD_VALUE2 = "textFieldValue2T"
        const val T_CHECKBOX_VALUE1 = "checkboxValue1T"
        const val T_CHECKBOX_VALUE2 = "checkboxValue2T"

        const val TAR_TEXT_FIELD_VALUE1 = "textFieldValue1TAR"
        const val TAR_TEXT_FIELD_VALUE2 = "textFieldValue2TAR"
        const val TAR_CHECKBOX_VALUE1 = "checkboxValue1TAR"
        const val TAR_CHECKBOX_VALUE2 = "checkboxValue2TAR"

        const val COU_TEXT_FIELD_VALUE1 = "textFieldValue1TAR"
        const val COU_CHECKBOX_VALUE1 = "checkboxValue1TAR"
        const val COU_CHECKBOX_VALUE2 = "checkboxValue2TAR"

        const val SETREL_TEXT_FIELD_VALUE1 = "textFieldValue1SETREL"

        const val REG_DVID = "dvid"
        const val REG_TKN = "tkn"
        const val REG_TYPEDV = "typedv"
        const val REG_NUM = "num"
        const val REG_COM = "com"
    }

//    var _screenOrientation by mutableStateOf(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    private val preferences: SharedPreferences =
        application.getSharedPreferences(REL_SETTINGS, Context.MODE_PRIVATE)

    private val _finalEventState = MutableStateFlow(
        StatusEventServerDTO(
            preferences.getInt("EVENT_ID", 0) ?: 0,
            preferences.getString("EVENT_TIMEEV", "0") ?: "0",
            preferences.getString("EVENT_RSTATE", "") ?: "0",
            preferences.getString("EVENT_VALUE", "") ?: "0",
            preferences.getString("EVENT_NAME", "") ?: "0"
        )
    )


    private val _savedEventId = MutableStateFlow(Int.MAX_VALUE)
    val savedErrorId: StateFlow<Int> = _savedEventId
    fun setEvId(id: Int){
        _savedEventId.value = id
    }

    private val _isVisibleAlertU = MutableStateFlow(false)
    val isVisibleAlertU: StateFlow<Boolean> = _isVisibleAlertU

    private val _isVisibleAlertI = MutableStateFlow(false)
    val isVisibleAlertI: StateFlow<Boolean> = _isVisibleAlertI

    private val _isVisibleAlertP = MutableStateFlow(false)
    val isVisibleAlertP: StateFlow<Boolean> = _isVisibleAlertP

    fun startEventsChecking(savedId: Int) {
        viewModelScope.launch {
            while (screenState.value.currentScreen == SCREEN_HOME) {
                loadEvents()
                checkEvents(savedId)
                delay(3000)
            }
        }
    }

    private suspend fun loadEvents() {
        createEvent(ScreenEvent.LoadLastEventServerFromDB(""))
        createEvent(ScreenEvent.LoadEventServerFromDB(""))
    }

    private fun checkEvents(savedId: Int) {
        _eventServerList.value
//            .asReversed()
            .take(20)
            .forEach { event ->
                when (event.name) {
                    "evu" -> if (event.id > savedId) {
                        _isVisibleAlertU.value = true
                    } else{
                        _isVisibleAlertU.value = false
                    }

                    "evi" -> if (event.id > savedId) {
                        _isVisibleAlertI.value =true
                    } else{
                        _isVisibleAlertI.value = false
                    }

                    "evp" -> if (event.id > savedId){
                        _isVisibleAlertP.value =true
                    } else {
                        _isVisibleAlertP.value = false
                    }

                    "gomode" -> createEvent(ScreenEvent.DeleteEventAlarmFromServer(""))
                }
            }
    }

    val finalEventState: StateFlow<StatusEventServerDTO?> = _finalEventState

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _screenState = mutableStateOf(ListState())
    val screenState: State<ListState> = _screenState

    private val _uiStateDTO = MutableStateFlow(UiStateDTO())
    val uiStateDTO: StateFlow<UiStateDTO> = _uiStateDTO

    private val _errorsListState = MutableStateFlow(listOf(ErrorServerMod()))
    val errorsListState: StateFlow<List<ErrorServerMod>> = _errorsListState

    private val _regState = MutableStateFlow(listOf(RegServerEntity()))
    val regState: StateFlow<List<RegServerEntity>> = _regState

    // Новый StateFlow для хранения списка последних 10 значений UiStateDTO
    private val _uiStateDTOList = MutableStateFlow<List<UiStateDTO>>(emptyList())
    val uiStateDTOListGraph: StateFlow<List<UiStateDTO>> = _uiStateDTOList

    ///////////////////
    // Новый StateFlow для StatusEventServerDTO
    private val _eventServerList = MutableStateFlow<List<StatusEventServerDTO>>(emptyList())
    val eventServerList: StateFlow<List<StatusEventServerDTO>> = _eventServerList

    private val _saveRegDataWIFI = mutableStateListOf<RequestDataDTO>(
        RequestDataDTO(
            dvid = preferences.getString(REG_DVID, "0").toString(),
            tkn = preferences.getString(REG_TKN, "0").toString(),
            typedv = preferences.getString(REG_TYPEDV, "5")?.toInt() ?: 0,
            num = preferences.getString(REG_NUM, "4")?.toInt() ?: 0,
            com = preferences.getString(REG_COM, "").toString()
        )
    )
    val regDataWIFI: SnapshotStateList<RequestDataDTO> = _saveRegDataWIFI
    fun setRegDataWIFI(item: RegResponseDTO) {
        ////////////FIXIT
        val itemToSave = RequestDataDTO(item.devid, item.token, item.typedv, item.num, "reg")
        _saveRegDataWIFI.add(itemToSave)
        showToast("Устройство зарегистрировано")
    }

    //    SETREL
    private val _textFieldValue1SETREL =
        mutableStateListOf(
            preferences.getString(SETREL_TEXT_FIELD_VALUE1, "Имя устройства") ?: "Имя устройства"
        )
    val textFieldValue1SETREL: SnapshotStateList<String> = _textFieldValue1SETREL
    fun setTextFieldValue1SETREL(text: String) {
        _textFieldValue1SETREL.add(text)
    }

    //rele mode

    private val _releModeValue =
        mutableStateListOf(preferences.getString(RELE_MODE_VALUE, "Выключено") ?: "Выключено")
    val releModeValue: SnapshotStateList<String> = _releModeValue
    fun set_releModeValue(rmode: Int) {
        var res = "Выключено"
        when (rmode) {
            0 -> res = "Выключено"
            1 -> res = "Тумблер"
            2 -> res = "Кнопка"
            3 -> res = "На время"
            4 -> res = "Таймер"
            5 -> res = "Цикл"
        }
        _releModeValue.add(res)
    }

    //rele mode 3

    private val _releMode3TimeOn =
        mutableStateListOf<String>(
            preferences.getString(RELE_MODE3_TIME_ON, "00:00:00") ?: "00:00:00"
        )
    val releMode3TimeOn: SnapshotStateList<String> = _releMode3TimeOn

    fun set_releMode3TimeOn(time: String) {
        if (_releMode3TimeOn.isNotEmpty()) {
            _releMode3TimeOn[_releMode3TimeOn.lastIndex] = time
        } else {
            _releMode3TimeOn.add(time)
        }
        preferences.edit().putString(RELE_MODE3_TIME_ON, time).apply()
    }

    //rele mode 4


    private val _releMode4TimeOn =
        mutableStateListOf<String>(
            preferences.getString(RELE_MODE4_TIME_ON, "00:00:00") ?: "00:00:00"
        )
    val releMode4TimeOn: SnapshotStateList<String> = _releMode4TimeOn
    fun set_releMode4TimeOn(time: String) {
        if (_releMode4TimeOn.isNotEmpty()) {
            _releMode4TimeOn[_releMode4TimeOn.lastIndex] = time
        } else {
            _releMode4TimeOn.add(time)
        }
        preferences.edit().putString(RELE_MODE4_TIME_ON, time).apply()
    }

    private val _releMode4TimeOff =
        mutableStateListOf<String>(
            preferences.getString(RELE_MODE4_TIME_OFF, "23:59:59") ?: "23:59:59"
        )
    val releMode4TimeOff: SnapshotStateList<String> = _releMode4TimeOff
    fun set_releMode4TimeOff(time: String) {
        if (_releMode4TimeOff.isNotEmpty()) {
            _releMode4TimeOff[_releMode4TimeOff.lastIndex] = time
        } else {
            _releMode4TimeOff.add(time)
        }
        preferences.edit().putString(RELE_MODE4_TIME_OFF, time).apply()
    }


    //rele mode 5


    private val _releMode5TimeOn =
        mutableStateListOf<String>(
            preferences.getString(RELE_MODE5_TIME_ON, "00:00:00") ?: "00:00:00"
        )
    val releMode5TimeOn: SnapshotStateList<String> = _releMode5TimeOn
    fun set_releMode5TimeOn(time: String) {
        if (_releMode5TimeOn.isNotEmpty()) {
            _releMode5TimeOn[_releMode5TimeOn.lastIndex] = time
        } else {
            _releMode5TimeOn.add(time)
        }
        preferences.edit().putString(RELE_MODE5_TIME_ON, time).apply()
    }

    private val _releMode5TimeOff =
        mutableStateListOf<String>(
            preferences.getString(RELE_MODE5_TIME_OFF, "23:59:59") ?: "23:59:59"
        )
    val releMode5TimeOff: SnapshotStateList<String> = _releMode5TimeOff
    fun set_releMode5TimeOff(time: String) {
        if (_releMode5TimeOff.isNotEmpty()) {
            _releMode5TimeOff[_releMode5TimeOff.lastIndex] = time
        } else {
            _releMode5TimeOff.add(time)
        }
        preferences.edit().putString(RELE_MODE5_TIME_OFF, time).apply()
    }

    //rele mode go

    private val _releModeGO =
        MutableStateFlow<Boolean>(
            preferences.getBoolean(RELE_MODE_GO, false) ?: false
        )
    val releModeGO: StateFlow<Boolean> = _releModeGO
    fun set_releModeGO(value: Boolean) {
        _releModeGO.value = value
    }

    private val _buttonGoVisib = MutableStateFlow(true)
    val buttonGoVisib: StateFlow<Boolean> = _buttonGoVisib
    fun set_buttonGoVisib(value: Boolean) {
        Thread.sleep(100)
        _buttonGoVisib.value = value
    }

    //releStt

    //    var releStt : Boolean = false
//    private val _releStt = MutableStateFlow(true)
//    val releStt: StateFlow<Boolean> = _releStt
    fun set_releStt(value: Boolean) {
        Thread.sleep(100)
//        _releStt.value = value
    }

    //I
    private val _textFieldValue1I =
        mutableStateListOf(preferences.getFloat(I_TEXT_FIELD_VALUE1, 50.0f))
    val textFieldValue1I: SnapshotStateList<Float> = _textFieldValue1I
    fun setTextFieldValue1(text: Float) {
        _textFieldValue1I.add(text)
    }

    private val _textFieldValue2I =
        mutableStateListOf(preferences.getFloat(I_TEXT_FIELD_VALUE2, 0.1f))
    val textFieldValue2I: SnapshotStateList<Float> = _textFieldValue2I
    fun setTextFieldValue2(text: Float) {
        _textFieldValue2I.add(text)
    }

    private val _checkboxValue1I =
        mutableStateListOf(preferences.getBoolean(I_CHECKBOX_VALUE1, false))
    val checkboxValue1I: SnapshotStateList<Boolean> = _checkboxValue1I
    fun setCheckboxValue1(text: Boolean) {
        _checkboxValue1I.add(text)
    }

    private val _checkboxValue2I =
        mutableStateListOf(preferences.getBoolean(I_CHECKBOX_VALUE2, false))
    val checkboxValue2I: SnapshotStateList<Boolean> = _checkboxValue2I
    fun setCheckboxValue2(text: Boolean) {
        _checkboxValue2I.add(text)
    }

    private val _checkboxValue3I =
        mutableStateListOf(preferences.getBoolean(I_CHECKBOX_VALUE3, false))
    val checkboxValue3I: SnapshotStateList<Boolean> = _checkboxValue3I
    fun setCheckboxValue3(text: Boolean) {
        _checkboxValue3I.add(text)
    }

    //U
    private val _textFieldValue1U =
        mutableStateListOf(preferences.getInt(U_TEXT_FIELD_VALUE1, 300))
    val textFieldValue1U: SnapshotStateList<Int> = _textFieldValue1U
    fun setTextFieldValue1U(text: Float) {
        _textFieldValue1U.add(text.toInt())
    }

    private val _textFieldValue2U = mutableStateListOf(preferences.getInt(U_TEXT_FIELD_VALUE2, 90))
    val textFieldValue2U: SnapshotStateList<Int> = _textFieldValue2U
    fun setTextFieldValue2U(text: Float) {
        _textFieldValue2U.add(text.toInt())
    }

    private val _checkboxValue1U =
        mutableStateListOf(preferences.getBoolean(U_CHECKBOX_VALUE1, false))
    val checkboxValue1U: SnapshotStateList<Boolean> = _checkboxValue1U
    fun setCheckboxValue1U(text: Boolean) {
        _checkboxValue1U.add(text)
    }

    private val _checkboxValue2U =
        mutableStateListOf(preferences.getBoolean(U_CHECKBOX_VALUE2, false))
    val checkboxValue2U: SnapshotStateList<Boolean> = _checkboxValue2U
    fun setCheckboxValue2U(text: Boolean) {
        _checkboxValue2U.add(text)
    }

    private val _checkboxValue3U =
        mutableStateListOf(preferences.getBoolean(U_CHECKBOX_VALUE3, false))
    val checkboxValue3U: SnapshotStateList<Boolean> = _checkboxValue3U
    fun setCheckboxValue3U(text: Boolean) {
        _checkboxValue3U.add(text)
    }

    //P
    private val _textFieldValue1P =
        mutableStateListOf(preferences.getInt(P_TEXT_FIELD_VALUE1, 20000))
    val textFieldValue1P: SnapshotStateList<Int> = _textFieldValue1P
    fun setTextFieldValue1P(text: Int) {
        _textFieldValue1P.add(text)
    }


    private val _checkboxValue1P =
        mutableStateListOf(preferences.getBoolean(P_CHECKBOX_VALUE1, false))
    val checkboxValue1P: SnapshotStateList<Boolean> = _checkboxValue1P
    fun setCheckboxValue1P(text: Boolean) {
        _checkboxValue1P.add(text)
    }

    private val _checkboxValue2P =
        mutableStateListOf(preferences.getBoolean(P_CHECKBOX_VALUE2, false))
    val checkboxValue2P: SnapshotStateList<Boolean> = _checkboxValue2P
    fun setCheckboxValue2P(text: Boolean) {
        _checkboxValue2P.add(text)
    }

    private val _checkboxValue3P =
        mutableStateListOf(preferences.getBoolean(P_CHECKBOX_VALUE3, false))
    val checkboxValue3P: SnapshotStateList<Boolean> = _checkboxValue3P
    fun setCheckboxValue3P(text: Boolean) {
        _checkboxValue3P.add(text)
    }

    //T
    private val _textFieldValue1T = mutableStateListOf(preferences.getInt(T_TEXT_FIELD_VALUE1, 99))
    val textFieldValue1T: SnapshotStateList<Int> = _textFieldValue1T
    fun setTextFieldValue1T(text: Int) {
        _textFieldValue1T.add(text)
    }

    private val _checkboxValue1T =
        mutableStateListOf(preferences.getBoolean(T_CHECKBOX_VALUE1, false))
    val checkboxValue1T: SnapshotStateList<Boolean> = _checkboxValue1T
    fun setCheckboxValue1T(text: Boolean) {
        _checkboxValue1T.add(text)
    }

    private val _checkboxValue2T =
        mutableStateListOf(preferences.getBoolean(T_CHECKBOX_VALUE2, false))
    val checkboxValue2T: SnapshotStateList<Boolean> = _checkboxValue2T
    fun setCheckboxValue2T(text: Boolean) {
        _checkboxValue2T.add(text)
    }

    //    TAR
    private val _textFieldValue1TAR =
        mutableStateListOf(preferences.getInt(TAR_TEXT_FIELD_VALUE1, 1000))
    val textFieldValue1TAR: SnapshotStateList<Int> = _textFieldValue1TAR
    fun setTextFieldValue1TAR(text: Int) {
        _textFieldValue1TAR.add(text)
    }

    private val _textFieldValue2TAR =
        mutableStateListOf(preferences.getInt(TAR_TEXT_FIELD_VALUE2, 1))
    val textFieldValue2TAR: SnapshotStateList<Int> = _textFieldValue2TAR
    fun setTextFieldValue2TAR(text: Int) {
        _textFieldValue2TAR.add(text)
    }

    private val _checkboxValue1TAR =
        mutableStateListOf(preferences.getBoolean(TAR_CHECKBOX_VALUE1, false))
    val checkboxValue1TAR: SnapshotStateList<Boolean> = _checkboxValue1TAR
    fun setCheckboxValue1TAR(text: Boolean) {
        _checkboxValue1TAR.add(text)
    }

    private val _checkboxValue2TAR =
        mutableStateListOf(preferences.getBoolean(TAR_CHECKBOX_VALUE2, false))
    val checkboxValue2TAR: SnapshotStateList<Boolean> = _checkboxValue2TAR
    fun setCheckboxValue2TAR(text: Boolean) {
        _checkboxValue2TAR.add(text)
    }

    //    COU
    private val _textFieldValue1COU =
        mutableStateListOf(preferences.getInt(COU_TEXT_FIELD_VALUE1, 1000))
    val textFieldValue1COU: SnapshotStateList<Int> = _textFieldValue1COU
    fun setTextFieldValue1COU(text: Int) {
        _textFieldValue1COU.add(text)
    }

    private val _checkboxValue1COU =
        mutableStateListOf(preferences.getBoolean(COU_CHECKBOX_VALUE1, false))
    val checkboxValue1COU: SnapshotStateList<Boolean> = _checkboxValue1COU
    fun setCheckboxValue1COU(text: Boolean) {
        _checkboxValue1COU.add(text)
    }

    private val _checkboxValue2COU =
        mutableStateListOf(preferences.getBoolean(COU_CHECKBOX_VALUE2, false))
    val checkboxValue2COU: SnapshotStateList<Boolean> = _checkboxValue2COU
    fun setCheckboxValue2COU(text: Boolean) {
        _checkboxValue2COU.add(text)
    }

    private val _listDevices = mutableStateListOf(
        RequestDataDTO(
            preferences.getString(REG_DVID, "") ?: "",
            preferences.getString(REG_TKN, "") ?: "",
            preferences.getString(REG_TYPEDV, "")?.toIntOrNull() ?: 0,
            preferences.getString(REG_NUM, "")?.toIntOrNull() ?: 0,
            "rs"
        )
    )
    val listDevices: SnapshotStateList<RequestDataDTO> = _listDevices


    init {

        _savedEventId.value = preferences.getInt("SAVEDID", Int.MAX_VALUE) ?: 0

        createEvent(ScreenEvent.OpenScreenRel(""))
        createEvent(ScreenEvent.GetErrors(""))
        if (!(preferences.getBoolean(RELE_MODE_GO, false) ?: false)) {
            set_releModeGO(false)
        } else set_releModeGO(true)
//        createEvent(ScreenEvent.LoadLastEventServerFromDB(""))

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

        requestEv = RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "rs")
        _listDevices.add(requestEv)

        gomodeVar = (preferences.getBoolean(RELE_MODE_GO, false))

    }

    fun createEvent(event: ScreenEvent) {
        scope.launch {
            onEvent(event)
        }
    }

    private suspend fun onEvent(event: ScreenEvent) {
        when (event) {

            is ScreenEvent.OpenScreenRel -> {
//                fetchData()
                delay(100)
                loadDataFromDatabase()
            }

            is ScreenEvent.ShowDialog -> {
                _screenState.value = _screenState.value.copy(dialogState = event.value)
//                _eventAlarmState.value = _screenState.value.copy(dialogState = event.value)
            }

            is ScreenEvent.OpenSettingsI -> updateSettingsI()
            is ScreenEvent.OpenSettingsP -> updateSettingsP()
            is ScreenEvent.OpenSettingsU -> updateSettingsU()
            is ScreenEvent.OpenSettingsT -> updateSettingsT()
            is ScreenEvent.OpenSettingsTAR -> updateSettingsTAR()
            is ScreenEvent.OpenSettingsCOU -> updateSettingsCOU()
            is ScreenEvent.SaveShPrefSettingsRel -> saveShPrefSettingsRel()
            is ScreenEvent.RegSMS -> {
                val regServerEntity = RegServerEntity(
                    dvid = regDataWIFI.last().dvid,
                    tkn = regDataWIFI.last().tkn,
                    typedv = regDataWIFI.last().typedv,
                    com = "rs",
                    num = regDataWIFI.last().num
                )
                val requestDataDTO = RequestDataDTO(
                    dvid = regDataWIFI.last().dvid,
                    tkn = regDataWIFI.last().tkn,
                    typedv = regDataWIFI.last().typedv,
                    com = "rs",
                    num = regDataWIFI.last().num
                )
                dBRepository.insertRegServer(regServerEntity)
//                insertRegServerInDBUseCase.execute(regServerEntity)
                saveRegDataPrefs(requestDataDTO)
                _listDevices.add(requestDataDTO)
            }

            is ScreenEvent.RegWIFI -> {
                regGetDataWIFI()
                delay(500)
//                regSendDataWIFI()
            }

            is ScreenEvent.LoadEventServerFromDB -> loadEventServerFromDB()

            is ScreenEvent.LoadLastEventServerFromDB -> loadLastEventServerFromDB()
            is ScreenEvent.DeleteEventAlarmFromServer -> deleteDeviceEventServer()
            is ScreenEvent.SendSettingsServer -> sendSettingsServer(event.value)

            is ScreenEvent.SendServerSettingsMode3 -> {
                sendServerSettingsMode3(event.value)
                _releModeGO.value = false
//                _releStt.value = false
            }

            is ScreenEvent.SendServerSettingsMode4 -> {
                sendServerSettingsMode4(event.value)
                _releModeGO.value = false
//                _releStt.value = false
            }

            is ScreenEvent.SendServerSettingsMode5 -> {
                sendServerSettingsMode5(event.value)
                _releModeGO.value = false
//                _releStt.value = false
            }

            is ScreenEvent.SendServerSettingsMode012 -> {
                sendServerSettingsMode012(event.value)
                _releModeGO.value = false
//                _releStt.value = false
            }

            is ScreenEvent.GetServerSettings -> getSettingsServer(event.value)

            is ScreenEvent.SendServerGoMode -> {
                sendServerGOMode()
//                set_buttonGoVisib(res)
            }

            is ScreenEvent.ShowScreen -> {
                _screenState.value = _screenState.value.copy(currentScreen = event.value)
                if (_screenState.value.dialogState == DialogState.SCREEN_LOG) {
                    _isVisibleAlertU.value = false
                    _isVisibleAlertI.value = false
                    _isVisibleAlertP.value = false
                }
            }

            is ScreenEvent.SendServerStopMode -> {
                sendServerStopMode()
//                restartRel()
            }

            is ScreenEvent.GetAllRegServerFromDB -> {
                _regState.value = dBRepository.getAllRegServer()
            }

            is ScreenEvent.GetErrors -> {
                _errorsListState.value = dBRepository.getAllErrors()
            }
        }
    }


    private suspend fun sendServerGOMode() {
        Thread.sleep(100)
        _buttonGoVisib.value = false

        preferences.edit().putBoolean(B_VIS, false).apply()

//        _uiStateDTO.value = _uiStateDTO.value.copy(bVis = false)
        val reqDVID = preferences.getString(REG_DVID, "") ?: ""
        val reqTKN = preferences.getString(REG_TKN, "") ?: ""
        val reqTYPEVD = preferences.getString(REG_TYPEDV, "")?.toIntOrNull() ?: 0
        val reqNUM = preferences.getString(REG_NUM, "")?.toIntOrNull() ?: 0

        val sendServerStop = sendServerGoModeUseCase.execute(
            RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "stop")
        )

        showToast("Команда отправлена на сервер")
        sendServerStop.onSuccess { result ->
            if (result.status == 0) {

                viewModelScope.launch {
                    while (isActive) {
                        var gomodeZero = ""
                        gomodeZero = _uiStateDTO.value.gomode
                        if (gomodeZero == "0") break
                        delay(1000)
                    }

                    val sendServerGoMode = sendServerGoModeUseCase.execute(
                        RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "gomode")
                    )
                    sendServerGoMode.onSuccess { result ->
                        if (result.status == 0) {

                            showToastDelay("Ожидаем ответ на устройство")
                            println("stop отправлен на сервер")
                        } else {
                            println("stop не отправлен на сервер")
                        }
                    }.onFailure { e ->
                        e.printStackTrace()
                        showToast("stop не отправлен на сервер")
//            set_buttonGoVisib(true)
                    }

                }
//                if (_uiStateDTO.value.gomode == "0")


//                showToastDelay("Ожидаем ответ на устройство")
//                println("gomode отправлен на сервер")
            } else {
                println("gomode не отправлен на сервер")
            }
        }.onFailure { e ->
            e.printStackTrace()
            showToast("gomode не отправлен на сервер")
//            set_buttonGoVisib(true)
        }
    }


    private suspend fun sendServerStopMode() {
        Thread.sleep(100)
        _buttonGoVisib.value = false
        preferences.edit().putBoolean(B_VIS, false).apply()
//        _uiStateDTO.value = _uiStateDTO.value.copy(bVis = false)
        val reqDVID = preferences.getString(REG_DVID, "") ?: ""
        val reqTKN = preferences.getString(REG_TKN, "") ?: ""
        val reqTYPEVD = preferences.getString(REG_TYPEDV, "")?.toIntOrNull() ?: 0
        val reqNUM = preferences.getString(REG_NUM, "")?.toIntOrNull() ?: 0

        val sendServerGoMode = sendServerGoModeUseCase.execute(
            RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "stop")
        )
        showToast("Команда отправлена на сервер")
        sendServerGoMode.onSuccess { result ->
            if (result.status == 0) {

                showToastDelay("Ожидаем ответ на устройство")
                println("stop отправлен на сервер")
            } else {
                println("stop не отправлен на сервер")
            }
        }.onFailure { e ->
            e.printStackTrace()
            showToast("stop не отправлен на сервер")
//            set_buttonGoVisib(true)
        }
    }


    private fun saveShPrefSettingsRel() {
        preferences
            .edit()
            .putString(SETREL_TEXT_FIELD_VALUE1, textFieldValue1SETREL.last())
            .putString(RELE_MODE_VALUE, releModeValue.last())
            .putString(RELE_MODE3_TIME_ON, _releMode3TimeOn.last().toString())
            .putString(RELE_MODE4_TIME_ON, _releMode4TimeOn.last().toString())
            .putString(RELE_MODE4_TIME_OFF, _releMode4TimeOff.last().toString())
            .putString(RELE_MODE5_TIME_ON, _releMode5TimeOn.last().toString())
            .putString(RELE_MODE5_TIME_OFF, _releMode5TimeOff.last().toString())
            .apply()
    }

    fun Triple<Int, Int, Int>.toString(): String {
        return "${this.first.toString().padStart(2, '0')}:${
            this.second.toString().padStart(2, '0')
        }:${this.third.toString().padStart(2, '0')}"
    }

    fun String.parseToTriple(): Triple<Int, Int, Int>? {
        val parts = this.split(':')
        return if (parts.size == 3 && parts.all { it.all { c -> c.isDigit() } }) {
            Triple(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else null
    }

    private fun getSettingsServer(requestDataDTO: RequestDataDTO) {
        viewModelScope.launch {
            try {
                val getSettings = getServerSettingsUseCase.execute(requestDataDTO)
                getSettings.onSuccess { result ->

                    when (result) {
                        is GetSettingsDTOrmode1 -> {
                            set_releModeValue(result.rmode)
                            setCheckboxValue1U(result.upm == 1)
                            setTextFieldValue1U(result.ulh.toFloat())
                            setTextFieldValue2U(result.ull.toFloat())
                            setCheckboxValue1(result.ipm == 1)
                            setTextFieldValue1(result.ilh.toFloat())
                            setTextFieldValue2(result.ill.toFloat())
                            setCheckboxValue1P(result.ppm == 1)
                            setTextFieldValue1P(result.plh)
                            setCheckboxValue1T(result.tpm == 1)
                            setTextFieldValue1T(result.tlh)
                        }

                        is GetSettingsDTOrmode3 -> {
                            set_releModeValue(result.rmode)
                            setCheckboxValue1U(result.upm == 1)
                            setTextFieldValue1U(result.ulh.toFloat())
                            setTextFieldValue2U(result.ull.toFloat())
                            setCheckboxValue1(result.ipm == 1)
                            setTextFieldValue1(result.ilh.toFloat())
                            setTextFieldValue2(result.ill.toFloat())
                            setCheckboxValue1P(result.ppm == 1)
                            setTextFieldValue1P(result.plh)
                            setCheckboxValue1T(result.tpm == 1)
                            setTextFieldValue1T(result.tlh)
                            _releMode3TimeOn.add(result.tOn)
//                            set_releMode3TimeOn(result.tOn)
                        }

                        is GetSettingsDTOrmode4 -> {
                            set_releModeValue(result.rmode)
//                            set_releMode4Time(result.tRTCOn, result.tRTCOff)
                            _releMode4TimeOn.add(result.tRTCOn)
                            _releMode4TimeOff.add(result.tRTCOff)
                            setCheckboxValue1U(result.upm == 1)
                            setTextFieldValue1U(result.ulh.toFloat())
                            setTextFieldValue2U(result.ull.toFloat())
                            setCheckboxValue1(result.ipm == 1)
                            setTextFieldValue1(result.ilh.toFloat())
                            setTextFieldValue2(result.ill.toFloat())
                            setCheckboxValue1P(result.ppm == 1)
                            setTextFieldValue1P(result.plh)
                            setCheckboxValue1T(result.tpm == 1)
                            setTextFieldValue1T(result.tlh)
                        }

                        is GetSettingsDTOrmode5 -> {
                            set_releModeValue(result.rmode)
                            _releMode5TimeOn.add(result.tClOn)
                            _releMode5TimeOff.add(result.tClOff)
                            setCheckboxValue1U(result.upm == 1)
                            setTextFieldValue1U(result.ulh.toFloat())
                            setTextFieldValue2U(result.ull.toFloat())
                            setCheckboxValue1(result.ipm == 1)
                            setTextFieldValue1(result.ilh.toFloat())
                            setTextFieldValue2(result.ill.toFloat())
                            setCheckboxValue1P(result.ppm == 1)
                            setTextFieldValue1P(result.plh)
                            setCheckboxValue1T(result.tpm == 1)
                            setTextFieldValue1T(result.tlh)

                        }

                        is GetSettingsDTOrmode6 -> {
                            set_releModeValue(result.rmode)
                        }
                    }
                }.onFailure {
                    it.printStackTrace()
//                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                showToast("Произошла неизвестная ошибка")
            }
        }
    }


    private fun showToast(message: String) {
        val context = contextIn?.takeIf { ctx ->
            ctx is Activity && !ctx.isFinishing
        } ?: return

        handler.removeCallbacksAndMessages(null)
        handler.post {
            try {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Toast", "Failed to show toast: ${e.message}")
            }
        }
    }

    private fun showToastDelay(message: String) {
        val context = contextIn?.takeIf { ctx ->
            ctx is Activity && !ctx.isFinishing
        } ?: return

        toastJob?.cancel()
        toastJob = viewModelScope.launch(Dispatchers.Main) {
            try {
                delay(3000)
                if (context is Activity && !context.isFinishing) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                Log.e("Toast", "Delay toast failed: ${e.message}")
            }
        }
    }

    private fun sendServerSettingsMode012(sendServerSettingsMode012DTO: SendServerSettingsMode012DTO) {


        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode012UseCase.execute(sendServerSettingsMode012DTO)
                showToast("Отправлена команда на сервер")
                sendSettings.onSuccess { result ->
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере" else "Изменения не сохранены на сервере"
                    )
                }.onFailure {
                    it.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun sendServerSettingsMode5(sendServerSettingsMode5DTO: SendServerSettingsMode5DTO) {
        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode5UseCase.execute(sendServerSettingsMode5DTO)
                showToast("Отправлена команда на сервер")
                sendSettings.onSuccess { result ->
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере" else "Изменения не сохранены на сервере"
                    )
                }.onFailure {
                    it.printStackTrace()
//                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun sendServerSettingsMode4(sendServerSettingsMode4DTO: SendServerSettingsMode4DTO) {
        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode4UseCase.execute(sendServerSettingsMode4DTO)
                showToast("Отправлена команда на сервер")
                sendSettings.onSuccess { result ->
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере" else "Изменения не сохранены на сервере"
                    )
                }.onFailure {
                    it.printStackTrace()
//                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun sendServerSettingsMode3(sendServerSettingsMode3DTO: SendServerSettingsMode3DTO) {
        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode3UseCase.execute(sendServerSettingsMode3DTO)
                showToast("Отправлена команда на сервер")
                sendSettings.onSuccess { result ->
//                    delay(5000)
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере (3 режим)" else "Не сохранены"
                    )
                }.onFailure {
                    it.printStackTrace()
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun sendSettingsServer(sendSettingsDTO: SendSettingsDTO) {
        viewModelScope.launch {
            try {
                val sendSettings = sendSettingsServerUseCase.execute(sendSettingsDTO)
                sendSettings.onSuccess { result ->
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере" else "Изменения не сохранены на сервере"
                    )
                }.onFailure {
                    it.printStackTrace()
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Произошла неизвестная ошибка")
            }
        }
    }


    private suspend fun deleteDeviceEventServer() {
        viewModelScope.launch {
            try {
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


                val event = dBRepository.getOneLastEventServerFromDB(
                    preferences.getInt(
                        "EVENT_ID",
                        0
                    )
                )

//                val event =
//                    getOneLastEventServerFromDBUseCase.execute(
//                        preferences.getInt(
//                            "EVENT_ID",
//                            0
//                        )
//                    )
                val dto =
                    DeleteEventDTO(
                        reqDVID,
                        reqTKN,
                        reqTYPEVD,
                        reqNUM,
                        "del",
                        event?.id ?: 0
                    )

                val del = deleteEventServerUseCase.execute(dto)
                when (event?.rstate) {
                    "0" -> {
                        sendServerStopMode()
                        _releModeGO.value = false
                        preferences.edit().putBoolean(RELE_MODE_GO, false).apply()
//                        _buttonGoVisib.value = true


                    }

                    "1" -> {
                        _releModeGO.value = true
                        preferences.edit().putBoolean(RELE_MODE_GO, true).apply()
//                        _buttonGoVisib.value = true
                    }

                    "3" -> {
                        sendServerStopMode()
                        _releModeGO.value = false
                        preferences.edit().putBoolean(RELE_MODE_GO, false).apply()
//                        _buttonGoVisib.value = true


                    }

                    "6" -> {
                        _releModeGO.value = true
                        preferences.edit().putBoolean(RELE_MODE_GO, true).apply()
//                        _buttonGoVisib.value = true
                    }
                }
                del.onSuccess { res ->
                    if (res.success) {

                        dBRepository.deleteLastEventServerById(preferences.getInt("EVENT_ID", 0))
//                        deleteLastEventServerByIdFromDBUseCase.execute(
//                            preferences.getInt(
//                                "EVENT_ID",
//                                0
//                            )
//                        )

                        preferences
                            .edit()
                            .putInt("EVENT_ID", 0)
                            .putString("EVENT_VALUE", "0")
                            .putString("EVENT_NAME", "0")
                            .putString("EVENT_RSTATE", "0")
                            .putString("EVENT_TIMEEV", "0")
                            .apply()

                        _finalEventState.value = StatusEventServerDTO(0)
                    }
                }.onFailure { error ->
                    deleteDeviceEventServer()
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun regGetDataWIFI() {
        viewModelScope.launch {
            try {
                val regGetDataWIFI = regGetDataWIFIUseCase.execute()
                regGetDataWIFI.onSuccess { res ->
                    val regDataEntity = RegServerEntity(
                        dvid = res.devid,
                        tkn = res.token,
                        com = "reg",
                        typedv = res.typedv,
                        num = 1
                    )
                    val regDataDTO = RequestDataDTO(
                        dvid = regDataEntity.dvid,
                        tkn = regDataEntity.tkn,
                        typedv = regDataEntity.typedv,
                        num = regDataEntity.num,
                        com = regDataEntity.com
                    )
                    val regSendDataWIFI = regSendDataWIFIUseCase.execute(
                        regDataDTO
                    )
                    regSendDataWIFI.onSuccess { result ->
                        if (result.status == 10) {
                            ////////
//                            val regReq2 =
//                                RequestDataDTO("0123456789qsrt1", "", 5, 1, "reg")
//                            insertRegServerInDBUseCase.execute(
//                                RegServerEntity(
//                                    dvid = regReq2.dvid,
//                                    tkn = regReq2.tkn,
//                                    com = "rs",
//                                    typedv = regReq2.typedv,
//                                    num = 1
//                                )
//                            )
                            showToast("Отказ в регистрации (устройство уже записано в базе)")

                        } else {
                            showToast("Регистрация успешна")

                            dBRepository.insertRegServer(regDataEntity.copy(com = "rs"))
//                            insertRegServerInDBUseCase.execute(regDataEntity.copy(com = "rs"))
                            saveRegDataPrefs(regDataDTO)
                            _listDevices.add(regDataDTO)
                        }
                    }
//                    setRegDataWIFI(res)
                }.onFailure { error ->
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun loadDataFromDatabase() {
        viewModelScope.launch {
            var counterBase = 0
            var counterServ = 0
            var counterErr = 0
            while (isActive) {
//                val uiStateFromDb = loadDataFromDBUseCase.execute()
                val uiStateFromDb = dBRepository.getUiStates()
                _uiStateDTOList.value = uiStateFromDb

                uiStateFromDb.firstOrNull()?.let { newState ->
                    if (_uiStateDTO.value.timedv != newState.timedv) {
                        _uiStateDTO.value = newState
                        delay(2000)
                        _buttonGoVisib.value = true
                        counterBase = 0
                        counterServ = 0
                        counterErr = 0
                    } else {
                        counterBase++
                        if (counterBase % 5 == 0) {
                            val reqDVID = preferences.getString(REG_DVID, "") ?: ""
                            val reqTKN = preferences.getString(REG_TKN, "") ?: ""
                            val reqTYPEVDString = preferences.getString(REG_TYPEDV, "")
                            val reqTYPEVD = if (!reqTYPEVDString.isNullOrEmpty()) {
                                reqTYPEVDString.toInt()
                            } else 0

                            val reqNUMString = preferences.getString(REG_NUM, "")
                            val reqNUM = if (!reqNUMString.isNullOrEmpty()) {
                                reqNUMString.toInt()
                            } else 4

                            val loadReq =
                                RequestDataDTO(
                                    reqDVID,
                                    reqTKN,
                                    reqTYPEVD,
                                    reqNUM,
                                    "rs",
//                                    event?.id ?: 0
                                )

                            val dataFromServer = loadReq?.let {
                                fetchDataUseCase.execute(it)
                            }

                            if (dataFromServer?.isFailure == true) {
                                if (counterErr == 0) {
                                    counterServ++
                                    if (counterServ == 4) {
                                        counterErr = 1
                                        dataFromServer.onFailure { error ->
                                            error.printStackTrace()
                                            showToast("Ошибка сервера")
                                            showToast("${error.message}")

                                            val currentTimeInSeconds =
                                                System.currentTimeMillis() / 1000

                                            dBRepository.insertError(
                                                ErrorServerMod(
                                                    errorCode = error.hashCode(),
                                                    errorMessage = "${error.message}",
                                                    timeev = "$currentTimeInSeconds",
                                                    deviceId = preferences.getString(REG_DVID, "1")
                                                )
                                            )
                                            delay(100)
                                            createEvent(ScreenEvent.GetErrors(""))

                                        }
                                        counterServ = 0
                                    }
                                }

                            } else counterErr = 0
                            _buttonGoVisib.value = true
                        }
                        delay(2000)
                    }
                } ?: delay(2000)
            }
        }
    }


    private fun loadEventServerFromDB() {
        viewModelScope.launch {
            val events = dBRepository.getEventServerFromDB()
//            val events = loadEventServerFromDBUseCase.execute()
            _eventServerList.value = events
        }
    }

    private fun loadLastEventServerFromDB() {
        viewModelScope.launch {

            val savedEventPrefsID = preferences.getInt("EVENT_ID", 0)
            try {
//                val lastEvent =
//                    getOneLastEventServerFromDBUseCase.execute(savedEventPrefsID)

                if (savedEventPrefsID != 0){
                    val lastEvent = dBRepository.getOneLastEventServerFromDB(savedEventPrefsID)
                    if (lastEvent != null) {
                        _finalEventState.value = lastEvent
                    } else {
                        _finalEventState.value = StatusEventServerDTO()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
//            val lastEvent = loadLastEventServerFromDBUseCase.execute()
//            if (lastEvent != null && lastEvent.id != savedEventPrefsID) {
//                _finalEventState.value = lastEvent
//            }

        }
    }

    private fun saveRegDataPrefs(requestDataDTO: RequestDataDTO) {
        preferences
            .edit()
            .putString(REG_DVID, requestDataDTO.dvid)
            .putString(REG_TKN, requestDataDTO.tkn)
            .putString(REG_TYPEDV, requestDataDTO.typedv.toString())
            .putString(REG_NUM, requestDataDTO.num.toString())
            .putString(REG_COM, requestDataDTO.toString())
            .apply()
    }

    private fun updateSettingsI() {
        preferences
            .edit()
            .putFloat(I_TEXT_FIELD_VALUE1, textFieldValue1I.last())
            .putFloat(I_TEXT_FIELD_VALUE2, textFieldValue2I.last())
            .putBoolean(I_CHECKBOX_VALUE1, checkboxValue1I.last())
            .putBoolean(I_CHECKBOX_VALUE2, checkboxValue2I.last())
            .putBoolean(I_CHECKBOX_VALUE3, checkboxValue3I.last())
            .apply()
    }

    private fun updateSettingsU() {
        preferences
            .edit()
            .putInt(U_TEXT_FIELD_VALUE1, textFieldValue1U.last())
            .putInt(U_TEXT_FIELD_VALUE2, textFieldValue2U.last())
            .putBoolean(U_CHECKBOX_VALUE1, checkboxValue1U.last())
            .putBoolean(U_CHECKBOX_VALUE2, checkboxValue2U.last())
            .putBoolean(U_CHECKBOX_VALUE3, checkboxValue3U.last())
            .apply()
    }

    private fun updateSettingsP() {
        preferences
            .edit()
            .putInt(P_TEXT_FIELD_VALUE1, textFieldValue1P.last())
//            .putInt(P_TEXT_FIELD_VALUE2, textFieldValue2P.last())
            .putBoolean(P_CHECKBOX_VALUE1, checkboxValue1P.last())
            .putBoolean(P_CHECKBOX_VALUE2, checkboxValue2P.last())
            .putBoolean(P_CHECKBOX_VALUE3, checkboxValue3P.last())
            .apply()
    }

    private fun updateSettingsT() {
        preferences
            .edit()
            .putInt(T_TEXT_FIELD_VALUE1, textFieldValue1T.last())
//            .putInt(T_TEXT_FIELD_VALUE2, textFieldValue2T.last())
            .putBoolean(T_CHECKBOX_VALUE1, checkboxValue1T.last())
            .putBoolean(T_CHECKBOX_VALUE2, checkboxValue2T.last())
            .apply()
    }

    private fun updateSettingsTAR() {
        preferences
            .edit()
            .putInt(TAR_TEXT_FIELD_VALUE1, textFieldValue1TAR.last())
            .putInt(TAR_TEXT_FIELD_VALUE2, textFieldValue2TAR.last())
            .putBoolean(TAR_CHECKBOX_VALUE1, checkboxValue1TAR.last())
            .putBoolean(TAR_CHECKBOX_VALUE2, checkboxValue2TAR.last())
            .apply()
    }

    private fun updateSettingsCOU() {
        preferences
            .edit()
            .putInt(COU_TEXT_FIELD_VALUE1, textFieldValue1COU.last())
            .putBoolean(COU_CHECKBOX_VALUE1, checkboxValue1COU.last())
            .putBoolean(COU_CHECKBOX_VALUE2, checkboxValue2COU.last())
            .apply()
    }

    override fun onCleared() {
        toastJob?.cancel()
        handler.removeCallbacksAndMessages(null)

        super.onCleared()
    }
}

