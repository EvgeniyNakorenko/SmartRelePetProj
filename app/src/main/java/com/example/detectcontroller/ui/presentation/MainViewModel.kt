package com.example.detectcontroller.ui.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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
import com.example.detectcontroller.data.remote.remDTO.UiState
import com.example.detectcontroller.domain.db.DeleteLastEventServerByIdFromDBUseCase
import com.example.detectcontroller.domain.db.GetOneLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.InsertRegServerInDBUseCase
import com.example.detectcontroller.domain.db.LoadDataFromDBUseCase
import com.example.detectcontroller.domain.db.LoadEventServerFromDBUseCase
import com.example.detectcontroller.domain.db.LoadLastEventServerFromDBUseCase
import com.example.detectcontroller.domain.registration.RegGetDataSMSUseCase
import com.example.detectcontroller.domain.registration.RegGetDataWIFIUseCase
import com.example.detectcontroller.domain.registration.RegSendDataWIFIUseCase
import com.example.detectcontroller.domain.server.DeleteEventServerUseCase
import com.example.detectcontroller.domain.server.GetServerSettingsUseCase
import com.example.detectcontroller.domain.server.SendServerGoModeUseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode012UseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode3UseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode4UseCase
import com.example.detectcontroller.domain.server.SendServerSettingsMode5UseCase
import com.example.detectcontroller.domain.server.SendSettingsServerUseCase
import com.example.detectcontroller.service.ForegroundService
import com.example.detectcontroller.ui.presentation.DialogState.INVISIBLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
    var counter: Int = 0
)

sealed class ScreenEvent {
    data class OpenSettingsI(val value: String) : ScreenEvent()
    data class OpenSettingsTAR(val value: String) : ScreenEvent()
    data class OpenSettingsCOU(val value: String) : ScreenEvent()
    data class OpenSettingsU(val value: String) : ScreenEvent()
    data class OpenSettingsP(val value: String) : ScreenEvent()
    data class OpenSettingsT(val value: String) : ScreenEvent()
    data class OpenSettingsRel(val value: String) : ScreenEvent()
    data class OpenScreenRel(val value: String) : ScreenEvent()
    data class LoadEventServerFromDB(val value: String) : ScreenEvent()
    data class LoadLastEventServerFromDB(val value: String) : ScreenEvent()

//    data class ShowEventAlarmI(val value: Boolean) : ScreenEvent()
//    data class ShowEventAlarmU(val value: Boolean) : ScreenEvent()
//    data class ShowEventAlarmP(val value: Boolean) : ScreenEvent()

    data class DeleteEventAlarmFromServer(val value: String) : ScreenEvent()

    data class ShowDialog(val value: DialogState) : ScreenEvent()
    data class RegSMS(val value: String) : ScreenEvent()
    data class RegWIFI(val value: String) : ScreenEvent()
    data class SendSettingsServer(val value: SendSettingsDTO) : ScreenEvent()
    data class SendServerSettingsMode3(val value: SendServerSettingsMode3DTO) : ScreenEvent()
    data class SendServerSettingsMode4(val value: SendServerSettingsMode4DTO) : ScreenEvent()
    data class SendServerSettingsMode5(val value: SendServerSettingsMode5DTO) : ScreenEvent()
    data class SendServerSettingsMode012(val value: SendServerSettingsMode012DTO) : ScreenEvent()
    data class SendServerGoMode(val value: String) : ScreenEvent()
    data class GetServerSettings(val value: RequestDataDTO) : ScreenEvent()
}

class MainViewModel(
    application: Application,
    context: Context,
    private val loadDataFromDBUseCase: LoadDataFromDBUseCase,
    private val loadEventServerFromDBUseCase: LoadEventServerFromDBUseCase,
    private val loadLastEventServerFromDBUseCase: LoadLastEventServerFromDBUseCase,
    private val deleteLastEventServerByIdFromDBUseCase: DeleteLastEventServerByIdFromDBUseCase,
    private val getOneLastEventServerFromDBUseCase: GetOneLastEventServerFromDBUseCase,
//    private val getRegServerByDvidFromDBUseCase: GetRegServerByDvidFromDBUseCase,
//    private val insertLastEventServerInDBUseCase: InsertLastEventServerInDBUseCase,
    private val insertRegServerInDBUseCase: InsertRegServerInDBUseCase,
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val contextIn = context
    private val regGetDataWIFIUseCase = RegGetDataWIFIUseCase()
    private val regGetDataSMSUseCase = RegGetDataSMSUseCase()
    private val regSendDataWIFIUseCase = RegSendDataWIFIUseCase()
    private val deleteEventServerUseCase = DeleteEventServerUseCase()
    private val sendSettingsServerUseCase = SendSettingsServerUseCase()

    private val sendServerSettingsMode3UseCase = SendServerSettingsMode3UseCase()
    private val sendServerSettingsMode4UseCase = SendServerSettingsMode4UseCase()
    private val sendServerSettingsMode5UseCase = SendServerSettingsMode5UseCase()
    private val sendServerSettingsMode012UseCase = SendServerSettingsMode012UseCase()
    private val sendServerGoModeUseCase = SendServerGoModeUseCase()
//    private val getAllRegServerFromDBUseCase = GetAllRegServerFromDBUseCase(database)

    private val getServerSettingsUseCase = GetServerSettingsUseCase()


    companion object {
        const val REL_SETTINGS = "rel_settings"
        const val RELE_MODE_VALUE = "releModeValue"
        const val RELE_MODE3_TIME_ON = "releMode3TimeOff"
        const val RELE_MODE4_TIME_ON = "releMode4TimeOn"
        const val RELE_MODE4_TIME_OFF = "releMode4TimeOff"
        const val RELE_MODE5_TIME_ON = "releMode5TimeOn"
        const val RELE_MODE5_TIME_OFF = "releMode5TimeOff"

        const val RELE_MODE_GO = "releModeGO"

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
    val finalEventState: StateFlow<StatusEventServerDTO?> = _finalEventState

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _screenState = mutableStateOf(ListState())
    val screenState: State<ListState> = _screenState

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // Новый StateFlow для хранения списка последних 10 значений UiState
    private val _uiStateList = MutableStateFlow<List<UiState>>(emptyList())
    val uiStateList: StateFlow<List<UiState>> = _uiStateList

    ///////////////////
    // Новый StateFlow для StatusEventServerDTO
    private val _eventServerList = MutableStateFlow<List<StatusEventServerDTO>>(emptyList())
    val eventServerList: StateFlow<List<StatusEventServerDTO>> = _eventServerList

    private val _saveRegDataWIFI = mutableStateListOf<RequestDataDTO>(RequestDataDTO())
    val regDataWIFI: SnapshotStateList<RequestDataDTO> = _saveRegDataWIFI
    fun setRegDataWIFI(item: RegResponseDTO) {
        ////////////FIXIT
//        val id = "0123456789qsrt4"
        val itemToSave = RequestDataDTO(item.devid, item.token, item.typedv, 1, "reg")
        _saveRegDataWIFI.add(itemToSave)
    }

    //    SETREL
    private val _textFieldValue1SETREL =
        mutableStateListOf(
            preferences.getString(SETREL_TEXT_FIELD_VALUE1, "Name_device") ?: "Name_device"
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
        _releMode3TimeOn.add(time)
    }

    //rele mode 4
    private val _releMode4Time = mutableStateListOf<Pair<String, String>>(getTimeOrDefaultMode4())
    val releMode4Time: SnapshotStateList<Pair<String, String>> = _releMode4Time
    fun getTimeOrDefaultMode4(): Pair<String, String> {
        val timeStringOn = preferences.getString(RELE_MODE4_TIME_ON, "00:00:00") ?: "00:00:00"
        val timeStringOff = preferences.getString(RELE_MODE4_TIME_OFF, "00:00:00") ?: "00:00:00"
        return Pair(timeStringOn, timeStringOff)
    }

    fun set_releMode4Time(tRTCOn: String, tRTCOff: String) {
        _releMode4Time.add(Pair(tRTCOn, tRTCOff))
    }

    //rele mode 5
    private val _releMode5Time = mutableStateListOf<Pair<String, String>>(getTimeOrDefaultMode5())
    val releMode5Time: SnapshotStateList<Pair<String, String>> = _releMode5Time
    fun getTimeOrDefaultMode5(): Pair<String, String> {
        val timeStringOn = preferences.getString(RELE_MODE5_TIME_ON, "00:00:00") ?: "00:00:00"
        val timeStringOff = preferences.getString(RELE_MODE5_TIME_OFF, "00:00:00") ?: "00:00:00"

        return Pair(timeStringOn, timeStringOff)
    }

    fun set_releMode5Time(tClOn: String, tClOff: String) {
        _releMode5Time.add(Pair(tClOn, tClOff))
    }

    //rele mode go

//    private val _uiState = MutableStateFlow(UiState())
//    val uiState: StateFlow<UiState> = _uiState


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
        _buttonGoVisib.value = value
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

//    private val _textFieldValue2P = mutableStateListOf(preferences.getInt(P_TEXT_FIELD_VALUE2, 100))
//    val textFieldValue2P: SnapshotStateList<Int> = _textFieldValue2P
//    fun setTextFieldValue2P(text: Int) {
//        _textFieldValue2P.add(text)
//    }

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

//    private val _textFieldValue2T = mutableStateListOf(preferences.getInt(T_TEXT_FIELD_VALUE2, 0))
//    val textFieldValue2T: SnapshotStateList<Int> = _textFieldValue2T
//    fun setTextFieldValue2T(text: Int) {
//        _textFieldValue2T.add(text)
//    }

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

    val listDevices = mutableStateListOf<RequestDataDTO>()


    init {
        createEvent(ScreenEvent.OpenScreenRel(""))
        if (!(preferences.getBoolean(RELE_MODE_GO, false) ?: false)) {
            set_releModeGO(false)
        } else set_releModeGO(true)
//        createEvent(ScreenEvent.LoadLastEventServerFromDB(""))


        val requestEv: RequestDataDTO
        val reqDVID = preferences.getString(ForegroundService.REG_DVID, "") ?: ""
        val reqTKN = preferences.getString(ForegroundService.REG_TKN, "") ?: ""
        val reqTYPEVDString = preferences.getString(ForegroundService.REG_TYPEDV, "")
        val reqTYPEVD = if (!reqTYPEVDString.isNullOrEmpty()) {
            reqTYPEVDString.toInt()
        } else 0
        val reqNUMString = preferences.getString(ForegroundService.REG_NUM, "")
        val reqNUM = if (!reqNUMString.isNullOrEmpty()) {
            reqNUMString.toInt()
        } else 0

        requestEv = RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "rs")
        listDevices.add(requestEv)

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
            is ScreenEvent.OpenSettingsRel -> openSettingsRel()
            is ScreenEvent.RegSMS -> {
                insertRegServerInDBUseCase.execute(
                    RegServerEntity(
                        dvid = regDataWIFI.last().dvid,
                        tkn = regDataWIFI.last().tkn,
                        typedv = regDataWIFI.last().typedv,
                        com = "rs",
                        num = 4
                    )
                )
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

            is ScreenEvent.SendServerSettingsMode3 -> sendServerSettingsMode3(event.value)
            is ScreenEvent.SendServerSettingsMode4 -> sendServerSettingsMode4(event.value)
            is ScreenEvent.SendServerSettingsMode5 -> sendServerSettingsMode5(event.value)

            is ScreenEvent.GetServerSettings -> getSettingsServer(event.value)
            is ScreenEvent.SendServerSettingsMode012 -> {
                TODO()
            }

            is ScreenEvent.SendServerGoMode -> {
                sendServerGOMode()
//                set_buttonGoVisib(res)
            }
        }
    }

    private suspend fun sendServerGOMode() {
        val reqDVID = preferences.getString(REG_DVID, "") ?: ""
        val reqTKN = preferences.getString(REG_TKN, "") ?: ""
        val reqTYPEVD = preferences.getString(REG_TYPEDV, "")?.toIntOrNull() ?: 0
        val reqNUM = preferences.getString(REG_NUM, "")?.toIntOrNull() ?: 0
        val releState = preferences.getBoolean(RELE_MODE_GO, false)

        val sendServerGoMode = sendServerGoModeUseCase.execute(
            RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "gomode")
        )
        sendServerGoMode.onSuccess { result ->
            if (result.status == 0) {
                when (releState) {
                    true -> {
                        set_releModeGO(false)
//                        preferences.getString(RELE_MODE_GO, "0")
                        preferences.edit().putBoolean(RELE_MODE_GO, false).apply()
                    }

                    false -> {
                        set_releModeGO(true)
                        preferences.edit().putBoolean(RELE_MODE_GO, true).apply()
                    }
                }
                set_buttonGoVisib(true)
            } else {
                set_buttonGoVisib(true)
            }
        }.onFailure { e ->
            e.printStackTrace()
            set_buttonGoVisib(true)
        }

    }

    private fun openSettingsRel() {
        preferences
            .edit()
            .putString(SETREL_TEXT_FIELD_VALUE1, textFieldValue1SETREL.last())
            .putString(RELE_MODE_VALUE, releModeValue.last())
            .putString(RELE_MODE3_TIME_ON, _releMode3TimeOn.last().toString())
            .putString(RELE_MODE4_TIME_ON, _releMode4Time.last().toString())
            .putString(RELE_MODE4_TIME_OFF, _releMode4Time.last().toString())
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
                            set_releMode3TimeOn(result.tOn)
                        }

                        is GetSettingsDTOrmode4 -> {
                            set_releModeValue(result.rmode)
                            set_releMode4Time(result.tRTCOn, result.tRTCOff)
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
                            set_releMode5Time(result.tClOn, result.tClOff)
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
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun showToast(message: String) {
        contextIn.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendServerSettingsMode012(sendServerSettingsMode012DTO: SendServerSettingsMode012DTO) {
//        sendServerSettingsMode012UseCase

    }

    private fun sendServerSettingsMode5(sendServerSettingsMode5DTO: SendServerSettingsMode5DTO) {
        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode5UseCase.execute(sendServerSettingsMode5DTO)
                sendSettings.onSuccess { result ->
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере" else "Не сохранены"
                    )
                }.onFailure {
                    it.printStackTrace()
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun sendServerSettingsMode4(sendServerSettingsMode4DTO: SendServerSettingsMode4DTO) {
        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode4UseCase.execute(sendServerSettingsMode4DTO)
                sendSettings.onSuccess { result ->
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере" else "Не сохранены"
                    )
                }.onFailure {
                    it.printStackTrace()
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun sendServerSettingsMode3(sendServerSettingsMode3DTO: SendServerSettingsMode3DTO) {
        viewModelScope.launch {
            try {
                val sendSettings =
                    sendServerSettingsMode3UseCase.execute(sendServerSettingsMode3DTO)
                sendSettings.onSuccess { result ->
                    delay(5000)
                    showToast(
                        if (result.status == 0) "Изменения сохранены на сервере (3 режим)" else "Не сохранены"
                    )
                }.onFailure {
                    it.printStackTrace()
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
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
                        if (result.status == 0) "Изменения сохранены на сервере (уставки)" else "Не сохранены"
                    )
                }.onFailure {
                    it.printStackTrace()
                    showToast("Произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
                showToast("Произошла неизвестная ошибка")
            }
        }
    }

    private fun regGetDataSMS() {
        viewModelScope.launch {
            try {
                val regGetDataSMS = regGetDataSMSUseCase.execute()

            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
            }
        }
    }

    private fun deleteDeviceEventServer() {
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

//                val event = loadLastEventServerFromDBUseCase.execute()
                val event =
                    getOneLastEventServerFromDBUseCase.execute(preferences.getInt("EVENT_ID", 0))
                val dto =
                    DeleteEventDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, "del", event?.id ?: 0)

                val del = deleteEventServerUseCase.execute(dto)

                del.onSuccess { res ->
                    if (res.success) {
                        showToast("Событие (id=$reqDVID) сохранено в журнал и удалено с сервера")

                        deleteLastEventServerByIdFromDBUseCase.execute(
                            preferences.getInt(
                                "EVENT_ID",
                                0
                            )
                        )

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
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
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

                            insertRegServerInDBUseCase.execute(regDataEntity.copy(com = "rs"))
                            saveRegDataPrefs(regDataDTO)
                        }
                    }
//                    setRegDataWIFI(res)
                }.onFailure { error ->
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e(ForegroundService.TAG, "download error", e)
                showToast("Произошла неизвестная ошибка")
            }
        }
    }

//    private fun getGegData(com: String): RequestDataDTO {
//        val requestEv: RequestDataDTO
//        val reqDVID = preferences.getString(ForegroundService.REG_DVID, "") ?: ""
//        val reqTKN = preferences.getString(ForegroundService.REG_TKN, "") ?: ""
//        val reqTYPEVDString = preferences.getString(ForegroundService.REG_TYPEDV, "")
//        val reqTYPEVD = if (!reqTYPEVDString.isNullOrEmpty()) {
//            reqTYPEVDString.toInt()
//        } else 0
//        val reqNUMString = preferences.getString(ForegroundService.REG_NUM, "")
//        val reqNUM = if (!reqNUMString.isNullOrEmpty()) {
//            reqNUMString.toInt()
//        } else 0
//
//        requestEv = RequestDataDTO(reqDVID, reqTKN, reqTYPEVD, reqNUM, com)
//        return requestEv
//    }

//    private fun regSendDataWIFI() {
//        viewModelScope.launch {
//            try {
////                val regReq2 = RequestDataDTO("0123456789qsrt1", "", 5, 4, "reg")
//                val regSendDataWIFI = regSendDataWIFIUseCase.execute(regReq2)
//                regSendDataWIFI.onSuccess { result ->
//
//                    if (result.status == 10) {
//                        ////////
//                        saveRegDataPrefs(regReq2)
//                        showToast("Отказ в регистрации (устройство уже записано в базе)")
//
//                    } else {
//                        showToast("Регистрация успешна")
//                    }
//                }.onFailure { error ->
//                    error.printStackTrace()
//                    showToast("Произошла ошибка при регистрации")
//                }
//            } catch (e: Exception) {
//                Log.e(ForegroundService.TAG, "download error", e)
//                showToast("Произошла неизвестная ошибка")
//            }
//        }
//    }

    private fun loadDataFromDatabase() {
        viewModelScope.launch {
            while (true) {
                ////////////////
                delay(5000)
                val uiStateFromDb = loadDataFromDBUseCase.execute()
                _uiStateList.value = uiStateFromDb
                if (uiStateFromDb.isNotEmpty()) {
                    _uiState.value = uiStateFromDb.first()
                }
            }
        }
    }

    private fun loadEventServerFromDB() {
        viewModelScope.launch {
            val events = loadEventServerFromDBUseCase.execute()
            _eventServerList.value = events
        }
    }

    private fun loadLastEventServerFromDB() {
        viewModelScope.launch {

            val savedEventPrefsID = preferences.getInt("EVENT_ID", 0)
            try {
                val lastEvent = getOneLastEventServerFromDBUseCase.execute(savedEventPrefsID)

                if (lastEvent != null) {
                    _finalEventState.value = lastEvent
                } else {
                    _finalEventState.value = StatusEventServerDTO()
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

}

