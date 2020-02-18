> combine PagerView + GridLayout to PagerLayout

![https://github.com/o0starshine0o/PagerLayout](../screenCaptures/logo_V2.jpg)

![Travis (.com)](https://img.shields.io/travis/com/o0starshine0o/PagerLayout)
![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/guide)
[ ![Download](https://api.bintray.com/packages/beijingqicode/maven/Guide/images/download.svg) ](https://bintray.com/beijingqicode/maven/Guide/_latestVersion)
![GitHub](https://img.shields.io/github/license/o0starshine0o/PagerLayout)
![GitHub last commit](https://img.shields.io/github/last-commit/o0starshine0o/PagerLayout)

# Guide

what we can do...
* convert a gray transparent layer on window
* set a view as except area in layer , also it can be clicked
* show guide content in four direction of this view

# Quick View

![https://github.com/o0starshine0o/PagerLayout/tree/master/Guide/](../screenCaptures/guide.gif)

# Install
Please using latest version:

2. [Guide](https://github.com/o0starshine0o/PagerLayout/tree/master/Guide/):![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/guide)
```
// 新手引导
implementation 'com.github.qicodes:guide:1.0.0'
```

# How To Use

1. find a `view` , maybe from `RecycleView`
```kotlin
val itemView = icons.findViewHolderForAdapterPosition(itemIndex).itemView
```
2. using this `view` to init a `Guide` with guide content from xml
```kotlin
val guide = Guide(baseContext).addWindow(itemView, R.layout.item_guide_left, 100, position)
```
3. add this `guide` to `decorView`
```kotlin
(window.decorView as ViewGroup).addView(guide)
```

That's all!
Enjoy this simple `Guide`

# Demo
This project can be run if download.
The main code in [Demo](https://github.com/o0starshine0o/PagerLayout/blob/master/app/src/main/java/com/abelhu/MainActivity.kt) in `showGuide` function.
