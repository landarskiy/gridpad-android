# GridPad Jetpack Compose layout

![github_title](https://user-images.githubusercontent.com/2251498/207064470-d719699d-319a-4267-9636-c4f058d5a7aa.png)

**GridPad** is a Jetpack Compose library that allows you to place UI elements in a predefined grid, 
manage spans in two dimensions, have flexible controls to manage row and column sizes.

[![Maven Central](https://img.shields.io/maven-central/v/com.touchlane/gridpad?label=MavenCentral&logo=apache-maven)](https://search.maven.org/artifact/com.touchlane/gridpad)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Build Status](https://github.com/touchlane/gridpad-android/workflows/Test/badge.svg)](https://github.com/touchlane/gridpad-android/actions)

# Usage

GridPad is inspired by LazyRow/LazyColumn and LazyVerticalGrid/LazyHorizontalGrid APIs, which makes
its use intuitive.

Key features and limitations:

* Follows [slot API](https://developer.android.com/jetpack/compose/layouts/basics#slot-based-layouts)
concept.
* Not lazy. All content will be measured and placed instantly.
* GridPad must have limited bounds.
* It's possible to specify the exact place on the grid for each element.
* A cell can contain more than one item. The draw order will be the same as the place order.
* Each item in a cell can have different spans.
* Each item can have horizontal and vertical spans simultaneously.
* Each row and column can have a specific size: fixed or weight-based.

![grid_examples](https://user-images.githubusercontent.com/2251498/204779586-c1ed214a-e30c-42a9-915e-37dda13d15ec.png)

## Install

Add the dependency below to your **module's** `build.gradle` file:

```groovy
dependencies {
    implementation "com.touchlane:gridpad:1.1.0"
}
```

## Define the grid

Specifying the exact grid size is required, but specifying each row and column size is optional. The
following code initializes a 3x4 grid with rows and columns weights equal to 1:

```kotlin
GridPad(cells = GridPadCells(rowCount = 3, columnCount = 4)) {
    // content        
}
```

![simgle_define_grid_dark](https://user-images.githubusercontent.com/2251498/204765082-51283636-c85e-46a0-beb1-1b966193a215.png)

By default rows and columns have **weight** size equal to 1, but it's possible specify different
size to specific row or column.
The library support 2 types of sizes:

* **GridPadCellSize.Fixed** - fixed size in Dp, not change when the bounds of GridPad change.
* **GridPadCellSize.Weight** - a relative, depends on other weights, remaining space after placing
  fixed sizes, and the GridPad bounds.

To define a specific size for a row or column you need to use `GridPadCells.Builder` API:

```kotlin
GridPad(
    cells = GridPadCells.Builder(rowCount = 3, columnCount = 4)
        .rowSize(index = 0, size = GridPadCellSize.Weight(2f))
        .columnSize(index = 3, size = GridPadCellSize.Fixed(90.dp))
        .build()
) {
    // content
}
```

![custom_define_grid_dark](https://user-images.githubusercontent.com/2251498/204765232-8f0a7f53-536c-4f39-82f9-5b867d352ec0.png)

The algorithm for allocating available space between cells:

1. All fixed (**GridPadCellSize.Fixed**) values are subtracted from the available space.
2. The remaining space is allocated between the remaining cells according to their weight value.

## Place the items

A cell content should be wrapped in a `item` element:

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    item {
        // cell content
    }
}
```

Items in a GridPad can be placed **explicitly** and **implicitly**. In the example above items are
placed implicitly. Implicit placing placed the item **next to the last placed item** (including span
size). Placement direction and first placement position depend
on [`placementPolicy`](#placement-policy). By default, the first placing will be at position \[0;0] 
with a horizontal direction from start to end.

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 4, columnCount = 3)
) {
    item {
        // 1-st item, row = 0, column = 0
    }
    item {
        // 2-nd item, row = 0, column = 1
    }
    item {
        // 3-rd item, row = 0, column = 2
    }
    item {
        // 4-th item, row = 1, column = 0
    }
}
```

> :warning: When the placement reaches the last cell, the following items will be ignored.
> Placing items outside the grid is not allowed.

To place an item explicitly needs to specify both properties `row` and `column` in the item.
When defines `row` and `column` property it's also possible to place all items in a different order
without regard to the actual location.

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    item(row = 1, column = 2) {
        // cell content
    }
    item(row = 0, column = 1) {
        // cell content
    }
}
```

![place_items_specific_dark](https://user-images.githubusercontent.com/2251498/204765324-211e2044-593b-41aa-893e-ad62565c9ded.png)

> :warning: A cell can contain more than one item. The draw order will be the same as the place
> order. GridPad does not limit the item's size when the child has an explicit size. That means that
> the item can go outside the cell bounds.

## Placement policy

To define the direction of placement items in an implicit method used the `placementPolicy` 
property.

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4),
    placementPolicy = GridPadPlacementPolicy(
        mainAxis = GridPadPlacementPolicy.MainAxis.HORIZONTAL,
        horizontalDirection = GridPadPlacementPolicy.HorizontalDirection.START_END,
        verticalDirection = GridPadPlacementPolicy.VerticalDirection.TOP_BOTTOM
    )
) {
    // content
}
```

The `GridPadPlacementPolicy` class has three properties that allow controlling different aspects 
of placement items.

* `mainAxis` sets the axis along which the item will be placed. When the axis is filled to the end,
  the next item will be placed on the next axis. If `mainAxis` is `HORIZONTAL` then items will be
  placed sequentially one by one by horizontal line. If `mainAxis` is `VERTICAL` then items will be
  placed sequentially one by one by vertical line.
* `horizontalDirection` sets the direction of placement horizontally. When `mainAxis` is
  `HORIZONTAL` this property describes the direction of placement of the next item. When `mainAxis`
  is `VERTICAL` this property describes the direction of moving to the next axis. The `START_END`
  means that the direction of placement items or moving main axis will begin from the start layout
  direction and move to the end layout direction (depending on LTR or RTL). The `END_START` means
  the same but in the opposite order.
* `verticalDirection` sets the direction of placement vertically. When `mainAxis` is
  `VERTICAL` this property describes the direction of placement of the next item. When `mainAxis`
  is `HORIZONTAL` this property describes the direction of moving to the next axis. The `TOP_BOTTOM`
  means that the direction of placement items or moving main axis will begin from the top and
  move to the bottom. The `BOTTOM_TOP` means the same but in the opposite order.

![placement_policy](https://user-images.githubusercontent.com/2251498/209689448-3a89070b-7640-4a2d-8654-9873f960e747.png)

## Spans

By default, each item has a span of 1x1. To change it, specify one or both of the `rowSpan`
and `columnSpan` properties of the item.

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    item(rowSpan = 3, columnSpan = 2) {
        // row = 0, column = 0, rowSpan = 3, columnSpan = 2
    }
    item(rowSpan = 2, columnSpan = 2) {
        // row = 0, column = 2, rowSpan = 2, columnSpan = 1
    }
}
```

![spanned_dark](https://user-images.githubusercontent.com/2251498/204765367-86177508-5551-4076-b6a1-b48b8f183f9d.png)

When an item has a span that goes outside the grid, the item is skipped and doesn't draw at all.
You can handle skipping cases by [diagnostic logger](#diagnostic).

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    item(row = 1, column = 3, rowSpan = 1, columnSpan = 3) {
        // will be skipped in a drawing process because the item is placed in the column range [3;5] 
        // but the maximum allowable is 3
    }
}
```

> :warning: When you have a complex structure it's highly recommended to use an **explicit** method
> of placing all items to avoid unpredictable behavior and mistakes during the placement of the
> items.


## Anchor

When `rowSpan` or `columnSpan` is more than 1 then the content is placed relative to the implicit 
parameter - **anchor**. The anchor is the point in the corner from which the span expands. 
The value depends on `horizontalDirection` and `verticalDirection` values in the `placementPolicy` 
property.

![anchor](https://user-images.githubusercontent.com/2251498/209725897-ab8d840d-0744-4ff6-89a7-f786e1522203.png)

## Layout Direction

The library handles the parent's layout direction value. That means that placement in **RTL**
direction with `horizontalDirection = START_END` will have the same behavior as **LTR** direction
with `horizontalDirection = END_START`.

## Diagnostic

The library doesn't throw any exceptions when an item is tried to place outside of the defined grid. 
Instead, the library just sends a signal through the special class `GridPadDiagnosticLogger`, 
skipping this item and moving to the next one. This silent behavior might be not suitable during 
the development process, so there is a way to have more control - define a custom listener. 
As a dev solution, you can just redirect the message to the console log or throw an exception to 
fix it immediately.

```kotlin
GridPadDiagnosticLogger.skippingItemListener = { message -> 
    Log.w("GridPad", message)
}
```

# Performance

GridPad respect Jetpack Compose recommendations and avoid not necessary recompositions. You can
always check restartability and skippability by running the following command in the sample project:

```shell
./gradlew assembleRelease -P gridpad.enableComposeCompilerReports=true
```

Results will be placed under the directory: `build/compose_metrics`

```text
restartable skippable scheme("[androidx.compose.ui.UiComposable]") fun GridPad(
  stable cells: GridPadCells
  stable modifier: Modifier? = @static Companion
  stable content: @[ExtensionFunctionType] Function1<GridPadScope, Unit>
)
```

# Enjoy using this library?

Join [:dizzy:Stargazers](https://github.com/touchlane/gridpad-android/stargazers) to support future development.

# License

```
MIT License

Copyright (c) 2022 Touchlane LLC tech@touchlane.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
