package com.example.detectcontroller.ui.presentation.graphs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        LineChart(
            modifier = Modifier
                .weight(1f)
                .height(chartHeight.dp),
            lineChartData = data
        )
    }
}


