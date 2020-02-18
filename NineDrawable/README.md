> combine PagerView + GridLayout to PagerLayout

![https://github.com/o0starshine0o/PagerLayout](../screenCaptures/logo_V2.jpg)

![Travis (.com)](https://img.shields.io/travis/com/o0starshine0o/PagerLayout)
![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/ninedrawable)
[ ![Download](https://api.bintray.com/packages/beijingqicode/maven/NineDrawable/images/download.svg) ](https://bintray.com/beijingqicode/maven/NineDrawable/_latestVersion)
![GitHub](https://img.shields.io/github/license/o0starshine0o/PagerLayout)
![GitHub last commit](https://img.shields.io/github/last-commit/o0starshine0o/PagerLayout)

# FolderView

what we can do...
* just like iOS icon group

# Quick View

![https://github.com/o0starshine0o/PagerLayout/tree/master/FolderView/](../screenCaptures/folder.gif)

# Install
Please using latest version:

0. [NineDrawable](https://github.com/o0starshine0o/PagerLayout/tree/master/NineDrawable/):![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/ninedrawable)
```
// 生成九宫格的drawable
implementation 'com.github.qicodes:ninedrawable:1.0.0'
```

# How To Use

1. init a `GridDrawable` with resource
```kotlin
val drawable = GridDrawable(GridDrawable.THREE, 10, this@SlideHolder).addRes(*Array(100) { resourceId })
```
2. implement the `Generate<T>` interface in `GridDrawable<T>` to create drawable from resource
```kotlin
override fun generateResource(obj: Int) = AppCompatResources.getDrawable(itemView.context, obj)
```
3. set `PagerSnapHelper` to scroll like a `PageView`
```kotlin
ImageView.setImageDrawable(drawable)
```

That's all!
Enjoy this easy nine grid drawable.


# Demo
This project can be run if download.
The main code in [Demo](https://github.com/o0starshine0o/PagerLayout/blob/master/app/src/main/java/com/abelhu/SlideAdapter.kt) in `SlideHolder` class.
