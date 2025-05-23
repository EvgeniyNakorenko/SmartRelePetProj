package com.example.detectcontroller.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.detectcontroller.data.remote.remDTO.UiState

class LineChartScreen {


    @Composable
    fun SingleLineChartWithGridLines(
        pointsData: List<Point>,
        chartHeight: Int,
        lineColor: Color,
//        label: String,
        modifier: Modifier = Modifier,
        xMod: List<String>,
        textSize: Int = 12 // Новый параметр для размера текста
    ) {

        if (pointsData.isEmpty()) {
            Text("Нет данных для отображения графика")
            return
        }

        val steps = 5
        val axisLabelFontSize = 12.sp // Уменьшение размера шрифта в 2 раза

        val xAxisData = AxisData.Builder()
            .axisStepSize(33.dp)
            .topPadding(105.dp)
            .steps(pointsData.size - 1)
            .labelData { i ->
                xMod[i].take(5).apply {
                    if (this.lastOrNull() == '.') {
                        this.dropLast(1)
                    }
                }
            }
            .labelAndAxisLinePadding(15.dp)
            .axisLabelFontSize(axisLabelFontSize)
            .build()
        val yAxisData = AxisData.Builder()
            .steps(steps)
            .labelAndAxisLinePadding(20.dp)
            .axisLabelFontSize(axisLabelFontSize)
            .labelData { i ->
                val yMin = pointsData.minOf { it.y }
                val yMax = pointsData.maxOf { it.y }
                val yScale = (yMax - yMin) / steps
                val value = ((i * yScale) + yMin).toFloat()
                String.format("%.1f", value)
            }
            .build()
        val data = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = pointsData,
                        LineStyle(color = lineColor),
                        IntersectionPoint(radius = 3.dp),
                        SelectionHighlightPoint(),
                        ShadowUnderLine(),
                        SelectionHighlightPopUp(
                            labelSize = 12.sp,
                            popUpLabel = { _, y -> "Y: ${String.format("%.1f", y)}" }
                        )
                    )
                )
            ),
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = GridLines()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = modifier.fillMaxWidth()
        ) {

//            Text(
//                text = "label",
//                fontSize = 16.sp,
//                color = Color.Black,
////                modifier = Modifier.padding(start = 8.dp),
//                fontWeight = FontWeight.Bold
//            )

            LineChart(
                modifier = Modifier
                    .weight(1f)
                    .height(chartHeight.dp),
                lineChartData = data
            )
        }
    }

    @Composable
    fun DisplayChartI(viewModel: MainViewModel) {
        val uiStates by viewModel.uiStateListGraph.collectAsState()
        val reversedUiStates = uiStates.reversed()
        val pointsDataI = convertUiStatesToPointsI(reversedUiStates)
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        val chartHeight = screenHeight / 1 // Половина высоты экрана, разделенная на 3 графика

        val xMod = reversedUiStates.map { it.timedv.drop(8).take(9).replace('-', '.') }

        Column(
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            Text(
                text = "I, A",
                modifier = Modifier.padding(start = 10.dp)
            )
            Crossfade(
                targetState = pointsDataI,
                animationSpec = tween(durationMillis = 500)
            ) { data ->
                SingleLineChartWithGridLines(
                    data,
                    chartHeight,
                    Color.Yellow,
//                    "",
                    modifier = Modifier.weight(1f),
                    xMod,
                    textSize = 16
                )
            }
        }
    }

    @Composable
    fun DisplayChartU(viewModel: MainViewModel) {
        val uiStates by viewModel.uiStateListGraph.collectAsState()
        val reversedUiStates = uiStates.reversed()
        val pointsDataU = convertUiStatesToPointsU(reversedUiStates)
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        val chartHeight = screenHeight / 1 // Половина высоты экрана, разделенная на 3 графика
        val xMod = reversedUiStates.map { it.timedv.drop(8).take(9).replace('-', '.') }

        Column(
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            Text(
                text = "U, В",
                modifier = Modifier.padding(start = 10.dp)
            )
            Crossfade(
                targetState = pointsDataU,
                animationSpec = tween(durationMillis = 500)
            ) { data ->
                SingleLineChartWithGridLines(
                    data,
                    chartHeight,
                    Color.Magenta,
//                    "U, В",
                    modifier = Modifier.weight(1f),
                    xMod,
                    textSize = 16
                )
            }
        }
    }

    @Composable
    fun DisplayChartP(viewModel: MainViewModel) {
        val uiStates by viewModel.uiStateListGraph.collectAsState()
        val reversedUiStates = uiStates.reversed()
        val pointsDataP = convertUiStatesToPointsP(reversedUiStates)
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        val chartHeight = screenHeight / 1
        val xMod = reversedUiStates.map { it.timedv.drop(8).take(9).replace('-', '.') }

        Column(
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            Text(
                text = "P, Вт",
                modifier = Modifier.padding(start = 10.dp)
            )
            Crossfade(
                targetState = pointsDataP,
                animationSpec = tween(durationMillis = 500)
            ) { data ->
                SingleLineChartWithGridLines(
                    data,
                    chartHeight,
                    Color.Cyan,
//                    "P, Вт",
                    modifier = Modifier.weight(1f),
                    xMod,
                    textSize = 16
                )
            }
        }
    }

    @Composable
    fun GraphSwitcher(mainViewModel: MainViewModel) {
        var currentGraph by remember { mutableStateOf(0) }

        val isCheckboxValue3Checked = mainViewModel.checkboxValue3I.last()
        val isCheckboxValue3UChecked = mainViewModel.checkboxValue3U.last()
        val isCheckboxValue3PChecked = mainViewModel.checkboxValue3P.last()

        val activeGraphs = listOf(
            isCheckboxValue3Checked,
            isCheckboxValue3UChecked,
            isCheckboxValue3PChecked
        ).mapIndexedNotNull { index, isChecked ->
            if (isChecked) index else null
        }

        // Ensure currentGraph is within bounds
        if (currentGraph >= activeGraphs.size) {
            currentGraph = 0
        }

        fun nextGraph() {
            currentGraph = (currentGraph + 1) % activeGraphs.size
        }

        Column(
            modifier = Modifier.heightIn(max = 250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )

            if (activeGraphs.size > 1) {
                Button(
                    onClick = { nextGraph() },
                    modifier = Modifier.size(170.dp, 30.dp),
                    colors = buttonColors
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "Следующий график",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.offset(y = (-3).dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Crossfade(targetState = currentGraph) { graphIndex ->
                when (activeGraphs.getOrNull(graphIndex)) {
                    0 -> DisplayChartI(mainViewModel)
                    1 -> DisplayChartU(mainViewModel)
                    2 -> DisplayChartP(mainViewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }


}

fun convertUiStatesToPointsI(uiStates: List<UiState>): List<Point> {
    return uiStates.mapIndexed { index, uiState ->
        Point(
            x = index.toFloat(),
            y = uiState.irl.drop(4).toFloatOrNull() ?: 0f
        )
    }
}

fun convertUiStatesToPointsU(uiStates: List<UiState>): List<Point> {
    return uiStates.mapIndexed { index, uiState ->
        Point(
            x = index.toFloat(),
            y = uiState.url.drop(4).toFloatOrNull() ?: 0f
        )
    }
}

fun convertUiStatesToPointsP(uiStates: List<UiState>): List<Point> {
    return uiStates.mapIndexed { index, uiState ->
        Point(
            x = index.toFloat(),
            y = uiState.pwr.drop(4).toFloatOrNull() ?: 0f
        )
    }
}

