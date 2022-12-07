<h1 align="center">GridPad Jetpack Compose layout</h1>

![grid_pad_logo](https://user-images.githubusercontent.com/2251498/204759114-b900efa7-8cf8-40bd-b221-5e4b6fb55013.png)

:construction: The library is still under construction and the API may change a bit, stay tuned and
suggest ideas. :construction:

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
    implementation "com.touchlane:gridpad:0.0.2"
}
```

## Define the grid

Specifying exact grid size is required but specifying each row and column size is optional.
The following code initializes 3x4 grid with rows and columns weights equal to 1:

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
* **GridPadCellSize.Weight** - relative, depends on other weights, remaining space after placing
  fixed sizes and the GridPad bounds.

To define specific for row or column you need to use `GridPadCells.Builder` API:

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

Items in a GridPad can be placed explicitly and implicitly. In example above item placed implicitly.
Implicit placing placed the item **next after the last placed item** (including span size) in the
same row. First placing will be at position \[0;0].

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    item {
        // row = 0, column = 0
    }
    item {
        // row = 0, column = 1
    }
}
```

When an item is placed at the last column in a row then the next items start placed at the next line
from the first column.

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    // 2 items 
    item {
        // 3-rd item, row = 0, column = 2
    }
    item {
        // 4-th item, row = 1, column = 0
    }
}
```

![place_items_dark](https://user-images.githubusercontent.com/2251498/204765288-5c55bc60-4053-4556-b68a-d257944435c6.png)

> :warning: When the placement reaches the last row and column, the following items will be ignored.
> Placing items outside the grid is not allowed.

To place an item explicitly needs to specify one or both properties `row` and `column` in the item.
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

When specified only one of `row` and `column` properties the logic will be following:

* If the `row` property is skipped, the row will be equal to the last placed item's row.
* If the `column` property is skipped, the row will be next after the last placed item (including
  span size). When the last item is placed at the last column in a row then the next items start
  placed at the next line from the first column.

> :warning: A cell can contain more than one item. The draw order will be the same as the place
> order. GridPad does not limit the item's size when the child has an explicit size. That means that
> the item can go outside the cell bounds.

## Spans

By default each item has a span of 1x1. To change it, specify one or both of the `rowSpan`
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

```kotlin
GridPad(
    cells = GridPadCells(rowCount = 3, columnCount = 4)
) {
    item(row = 1, column = 3, rowSpan = 1, columnSpan = 3) {
        // will be skipped in a drawing process because the item is placed in the column range [3;5] 
        // but the maximum allowable 3
    }
}
```

> :warning: When you have a complex structure it's highly recommended to use an **explicit** method
> of placing all items to avoid unpredictable behavior and mistakes during the placement of the
> items.

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
