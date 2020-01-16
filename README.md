![https://github.com/o0starshine0o/ArcProgressBar](screenCaptures/logo.jpg)

![Travis (.com)](https://img.shields.io/travis/com/o0starshine0o/ArcProgressBar)
![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/arcprogressbar)
![GitHub](https://img.shields.io/github/license/o0starshine0o/ArcProgressBar)
![GitHub last commit](https://img.shields.io/github/last-commit/o0starshine0o/ArcProgressBar)

# ArcProgressBar

what we can do...
* a arc progress bar with title, title-desc, sub-title, sub-title-desc
* background with bezier path mask
* two different progress in one view
* bubbles around progress bar
* dynamic control the scale count and the special scale
* fluent animation

# Quick View

![https://github.com/o0starshine0o/ArcProgressBar](screenCaptures/progress.gif)
![https://github.com/o0starshine0o/ArcProgressBar](screenCaptures/bubble.gif)
# Install
Please using latest version:

0. arcprogressbar:![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/arcprogressbar)
1. beziermask:![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/beziermask)
2. bubbleview:![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/bubbleview)
3. utils:![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/utils)
```xml
// 圆弧进度条
implementation 'com.github.qicodes:arcprogressbar:1.2.7'
// 贝塞尔曲线遮罩
implementation 'com.github.qicodes:beziermask:1.2.7'
// 气泡产生
implementation 'com.github.qicodes:bubbleview:1.2.7'
// 工具类
implementation 'com.github.qicodes:utils:1.2.7'
```

# Functions

## ProgressBar

### Custom progress size
> the Anchor is the `center` = ((left + right) / 2, (top + bottom) / 2)

The `progress` can be modified by xml config or java code:
```xml
app:scaleInsidePercent="70%"
app:scaleOutsidePercent="78%"
app:scaleOutsideSpecialPercent="81%"
```

```java
// 刻度线半径百分比
scaleInsidePercent = 0.7f
scaleOutsidePercent = 0.78f
scaleOutsideSpecialPercent = 0.81f
``` 
Also the `subProgress`:
```xml
app:progressBarPercent="65%"
```
```java
// 进度条半径百分比
progressBarPercent = 0.65f
```
And the size of the paint
```xml
app:paintColor="@android:color/white"
app:paintWidth="4dp"
```
```java
// 画笔宽度
paintWidth = 5.dp
// 画笔颜色
paintColor = Color.WHITE
``` 
what's more , we can control the angle of the progress
```xml
app:drawAngle="1.33"  // means 4/3π, the angle of progress
app:startAngle="0.83" // means 5/6π, the start angle of progress from x positive 
```
```java
// 角度
startAngle = 0.83f
drawAngle = 1.33f
```

### Custom progress color

the progress's color can be gradient, but only can be set by java code:
```java
// 背景渐变色
backColors = context.resources.getIntArray(com.qicode.arcprogressbar.R.array.color_gradient)
// 背景渐变色分布(默认平均分布)
backColorPositions = context.resources.getStringArray(com.qicode.arcprogressbar.R.array.position_gradient).map { it.toFloat() }.toFloatArray()
```

### Custom text in progress bar
> The text from top to bottom is : `title`, `titleDesc`, `subTitle`, `subTitleDesc`, `scaleValue`

> We can control those text with `text`, `size`, `color`, `position`(`radio`, `angle`)

Here is a demo for `title`:

```xml
app:title="@string/title"
app:titleColor="@android:color/black"
app:titleAngle="180"
app:titleDesc="8888"
app:titlePercent="30%"
app:titleSize="18sp"
```
```java
title = DrawString("今日步数", Color.BLACK, 180f, 0.30f, 20.sp)
```
### More
Control the scale's count:
```xml
app:scaleCount="59"
```
```java
// 刻度线数量
scaleCount = 59
```

Which scale should be ##Special##:
```xml
app:specialScales="9,29,49"
```
```java
// 特殊刻度
specialScales = listOf(9, 29, 49)
``` 

### Animate
> We can set progress or subProgress by java code

```java
// 500ms到达指定位置
progressBar.progress(Random().nextInt(progressBar.progressMax - progressBar.progressMin + 1) + progressBar.progressMin, false, 500)
``` 

We can reach to the max value by change second param to `true`.We can also change the animation during time by change the third param.

## Bubbles
> The bubbles use `RecyclerView`, we override it's `LayoutManager` by `BubbleLayoutManager`, which can random bubble's position.

> We override the `BezierMaskView`'s `onLayout` to get the `Path` from all it's children, then exclude the bubbles position from those `Path`.

### Step 0:
Contains a `RecyclerView` inside `BezierMaskView`, the set `RecyclerView`'s `layoutManage` to `BubbleLayoutManager`:
```xml
<com.qicode.beziermask.BezierMaskView>
    <android.support.v7.widget.RecyclerView
        tools:layoutManager="com.qicode.bubbleview.BubbleLayoutManager" />
</com.qicode.beziermask.BezierMaskView>
```
### Step 1:
Use a adapter to add bubbles:
```java
bubbleView.layoutManager = BubbleLayoutManager(50)
bubbleView.adapter = BubbleAdapter()
```
## More
For more, read the demo.

# Donate
If this Weight helps you , I'll happy if you notify me by donate:

![https://github.com/o0starshine0o/ArcProgressBar](screenCaptures/AliReceive.jpg)
![https://github.com/o0starshine0o/ArcProgressBar](screenCaptures/WechatReceive.jpg)
