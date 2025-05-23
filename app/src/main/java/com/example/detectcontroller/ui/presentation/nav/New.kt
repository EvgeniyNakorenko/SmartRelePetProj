package com.example.detectcontroller.ui.presentation.nav

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.extensions.isNotNull
import com.example.detectcontroller.data.remote.remDTO.RegResponseDTO
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.ui.presentation.MainViewModel
import com.example.detectcontroller.ui.presentation.ScreenEvent


@Composable
fun New(mainViewModel: MainViewModel,) {

    val titleResource =
        "Выберите интерфейс общения для регистрации"
    val textCB1Resource = "Wi-Fi"
    val textCB2Resource = "Ручной ввод"

    val checkboxValue1 = remember { mutableStateOf(true) }
    val checkboxValue2 = remember { mutableStateOf(false) }
//    var regDataWIFI : RequestDataDTO?
//    regDataWIFI = mainViewModel.regDataWIFI?.last()

    val dvidValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.lastOrNull()?.dvid ?: "0") }
    val tknValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.lastOrNull()?.tkn ?: "0") }
    val typedvValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.lastOrNull()?.typedv.toString() ?: "") }
    val numValue1 =
        remember { mutableStateOf(mainViewModel.regDataWIFI?.lastOrNull()?.num.toString() ?: "") }

//    Box(
//        modifier = Modifier
//            .padding(8.dp)
//            .border(2.dp, Color.Red, shape = RoundedCornerShape(20.dp))
//            .background(
////                Color(249, 250, 251),
//                Color(0xFFE6EAF2),
//                shape = RoundedCornerShape(20.dp)
//            )
//    ) {
    Column {
        Text(
            text = titleResource,
            fontSize = 22.sp,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

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
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    onClick = { /* Handle Cancel action */ },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    onClick = {
                        if (checkboxValue1.value) {
                            mainViewModel.createEvent(ScreenEvent.RegWIFI(""))
                        } else {
                            mainViewModel.setRegDataWIFI(
                                RegResponseDTO(
                                    devid = dvidValue1.value,
                                    token = tknValue1.value,
                                    typedv = typedvValue1.value.toInt(),
                                    num = numValue1.value.toInt()
                                )
                            )
                            mainViewModel.createEvent(ScreenEvent.RegSMS(""))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            }
        }

    }
//    }
}