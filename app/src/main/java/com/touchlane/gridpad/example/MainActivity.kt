/*
 * MIT License
 *
 * Copyright (c) 2022 Touchlane LLC tech@touchlane.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.touchlane.gridpad.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.touchlane.gridpad.GridPad
import com.touchlane.gridpad.GridPadCellSize
import com.touchlane.gridpad.GridPadCells
import com.touchlane.gridpad.GridPadPlacementPolicy
import com.touchlane.gridpad.example.ui.component.*
import com.touchlane.gridpad.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GridPadExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListOfPads()
                }
            }
        }
    }
}

@Composable
fun PadCard(ratio: Float = 1f, content: @Composable () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
fun BlueprintCard(ratio: Float = 1f, title: String? = null, content: @Composable () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .padding(16.dp),
        colors = CardDefaults.cardColors(AndreaBlue)
    ) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(top = 32.dp, start = 32.dp, end = 32.dp)
                    .background(AndreaBlue),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Box(
            modifier = Modifier
                .padding(32.dp)
                .background(AndreaBlue)
        ) {
            content()
        }
    }
}

@Composable
fun SimpleCalculatorPadCard() {
    PadCard(ratio = 0.9f) {
        SimpleCalculatorPadTheme(
            colors = SimpleCalculatorPadColors(
                content = AswadBlack,
                removeContent = HeatWave,
                actionsContent = AndreaBlue,
                background = White
            )
        ) {
            SimpleCalculatorPad()
        }
    }
}

@Composable
fun SimplePriorityCalculatorPadCard() {
    PadCard {
        SimplePriorityCalculatorPadTheme(
            colors = SimplePriorityCalculatorPadColors(
                content = White,
                background = AswadBlack,
                removeBackground = HeatWave,
                actionsBackground = AndreaBlue
            )
        ) {
            SimplePriorityCalculatorPad()
        }
    }
}

@Composable
fun EngineeringCalculatorPadCard() {
    PadCard(ratio = 1.1f) {
        EngineeringCalculatorPadTheme(
            colors = EngineeringCalculatorPadColors(
                content = AswadBlack,
                removeContent = HeatWave,
                actionsContent = AndreaBlue,
                functionsContent = BarneyPurple,
                background = White,
                numBackground = White.copy(alpha = 0.1f)
            )
        ) {
            EngineeringCalculatorPad()
        }
    }
}

@Composable
fun InteractivePinPadCard() {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            PinPadTheme(
                colors = PinPadColors(
                    content = White,
                    removeContent = HeatWave,
                    background = AswadBlack
                )
            ) {
                InteractivePinPad()
            }
        }
    }
}

@Composable
fun SimpleBlueprintCard() {
    BlueprintCard(ratio = 1.5f) {
        GridPad(
            cells = GridPadCells(rowCount = 3, columnCount = 4)
        ) {
            repeat(13) { item { BlueprintBox() } }
        }
    }
}

@Composable
fun CustomSizeBlueprintCard() {
    BlueprintCard(ratio = 1.5f) {
        GridPad(
            cells = GridPadCells.Builder(rowCount = 3, columnCount = 4)
                .rowSize(index = 0, size = GridPadCellSize.Weight(2f))
                .columnSize(index = 3, size = GridPadCellSize.Fixed(90.dp))
                .build()
        ) {
            repeat(12) { item { BlueprintBox() } }
        }
    }
}


@Composable
fun SimpleBlueprintCardWithContent() {
    BlueprintCard(ratio = 1.5f) {
        val rowCount = 3
        val columnCount = 4
        GridPad(cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)) {
            repeat(rowCount * columnCount) { item { BlueprintBox() } }
        }
        GridPad(
            cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)
        ) {
            item { ContentBlueprintBox("[0;0]") }
            item { ContentBlueprintBox("[0;1]") }
        }
    }
}

@Composable
fun SimpleBlueprintCardWithContentMixOrdering() {
    BlueprintCard(ratio = 1.5f) {
        val rowCount = 3
        val columnCount = 4
        GridPad(cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)) {
            repeat(rowCount * columnCount) { item { BlueprintBox() } }
        }
        GridPad(
            cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)
        ) {
            item(row = 1, column = 2) { ContentBlueprintBox("[1;2]\nOrder: 1") }
            item(row = 0, column = 1) { ContentBlueprintBox("[0;1]\nOrder: 2") }
        }
    }
}

@Composable
fun SimpleBlueprintCardWithSpans() {
    BlueprintCard(ratio = 1.5f) {
        val rowCount = 3
        val columnCount = 4
        GridPad(cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)) {
            repeat(rowCount * columnCount) { item { BlueprintBox() } }
        }
        GridPad(
            cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)
        ) {
            item(rowSpan = 3, columnSpan = 2) {
                ContentBlueprintBox("[0;0]\nSpan: 3x2")
            }
            item(rowSpan = 2, columnSpan = 2) {
                ContentBlueprintBox("[0;2]\nSpan: 2x2")
            }
        }
    }
}

@Composable
fun SimpleBlueprintCardWithSpansOverlapped() {
    BlueprintCard(ratio = 1.5f) {
        val rowCount = 3
        val columnCount = 4
        GridPad(cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)) {
            repeat(rowCount * columnCount) { item { BlueprintBox() } }
        }
        GridPad(
            cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)
        ) {
            item(rowSpan = 3, columnSpan = 2) {
                ContentBlueprintBox("[0;0]\nSpan: 3x2")
            }
            item(row = 2, column = 1, columnSpan = 3) {
                ContentBlueprintBox("[2;1]\nSpan: 1x3, overlapped")
            }
        }
    }
}

@Composable
fun SimpleBlueprintCardPolicyHorizontalSeTb() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.HORIZONTAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.START_END,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.TOP_BOTTOM
    )
}

@Composable
fun SimpleBlueprintCardPolicyHorizontalEsTb() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.HORIZONTAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.END_START,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.TOP_BOTTOM
    )
}

@Composable
fun SimpleBlueprintCardPolicyHorizontalSeBt() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.HORIZONTAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.START_END,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.BOTTOM_TOP
    )
}

@Composable
fun SimpleBlueprintCardPolicyHorizontalEsBt() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.HORIZONTAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.END_START,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.BOTTOM_TOP
    )
}

@Composable
fun SimpleBlueprintCardPolicyVerticalSeTb() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.VERTICAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.START_END,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.TOP_BOTTOM
    )
}

@Composable
fun SimpleBlueprintCardPolicyVerticalEsTb() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.VERTICAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.END_START,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.TOP_BOTTOM
    )
}

@Composable
fun SimpleBlueprintCardPolicyVerticalSeBt() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.VERTICAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.START_END,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.BOTTOM_TOP
    )
}

@Composable
fun SimpleBlueprintCardPolicyVerticalEsBt() {
    SimpleBlueprintCardPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.VERTICAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.END_START,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.BOTTOM_TOP
    )
}

@Composable
fun SimpleBlueprintCardPolicy(
    mainAxis: GridPadPlacementPolicy.MainAxis,
    horizontalDirection: GridPadPlacementPolicy.HorizontalDirection,
    verticalDirection: GridPadPlacementPolicy.VerticalDirection
) {
    val layoutDirection = LocalLayoutDirection.current
    BlueprintCard(
        ratio = 1.2f,
        title = "LayoutDirection = $layoutDirection\nmainAxis = $mainAxis\nhorizontal = $horizontalDirection\nvertical = $verticalDirection"
    ) {
        val rowCount = 3
        val columnCount = 4
        GridPad(cells = GridPadCells(rowCount = rowCount, columnCount = columnCount)) {
            repeat(rowCount * columnCount) { item { BlueprintBox() } }
        }
        GridPad(
            cells = GridPadCells(rowCount = rowCount, columnCount = columnCount),
            placementPolicy = GridPadPlacementPolicy(
                mainAxis = mainAxis,
                horizontalDirection = horizontalDirection,
                verticalDirection = verticalDirection
            )
        ) {
            repeat(rowCount * columnCount) {
                item {
                    ContentBlueprintBox("$it")
                }
            }
        }
    }
}

@Composable
fun ListOfPads(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        item {
            InteractivePinPadCard()
        }
        item {
            EngineeringCalculatorPadCard()
        }
        item {
            SimplePriorityCalculatorPadCard()
        }
        item {
            SimpleCalculatorPadCard()
        }
        item {
            SimpleBlueprintCard()
        }
        item {
            CustomSizeBlueprintCard()
        }
        item {
            SimpleBlueprintCardWithContent()
        }
        item {
            SimpleBlueprintCardWithContentMixOrdering()
        }
        item {
            SimpleBlueprintCardWithSpans()
        }
        item {
            SimpleBlueprintCardWithSpansOverlapped()
        }
        item {
            SimpleBlueprintCardPolicyHorizontalSeTb()
        }
        item {
            SimpleBlueprintCardPolicyHorizontalEsTb()
        }
        item {
            SimpleBlueprintCardPolicyHorizontalSeBt()
        }
        item {
            SimpleBlueprintCardPolicyHorizontalEsBt()
        }
        item {
            SimpleBlueprintCardPolicyVerticalSeTb()
        }
        item {
            SimpleBlueprintCardPolicyVerticalEsTb()
        }
        item {
            SimpleBlueprintCardPolicyVerticalSeBt()
        }
        item {
            SimpleBlueprintCardPolicyVerticalEsBt()
        }
        item {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SimpleBlueprintCardPolicyHorizontalSeTb()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GridPadExampleTheme {
        ListOfPads()
    }
}