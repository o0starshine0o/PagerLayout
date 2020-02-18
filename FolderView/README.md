> combine PagerView + GridLayout to PagerLayout

![https://github.com/o0starshine0o/PagerLayout](../screenCaptures/logo_V2.jpg)

![Travis (.com)](https://img.shields.io/travis/com/o0starshine0o/PagerLayout)
![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/folderview)
[ ![Download](https://api.bintray.com/packages/beijingqicode/maven/FolderView/images/download.svg) ](https://bintray.com/beijingqicode/maven/FolderView/_latestVersion)
![GitHub](https://img.shields.io/github/license/o0starshine0o/PagerLayout)
![GitHub last commit](https://img.shields.io/github/last-commit/o0starshine0o/PagerLayout)

# FolderView

what we can do...
* just like iOS icon group

# Quick View

![https://github.com/o0starshine0o/PagerLayout/tree/master/FolderView/](../screenCaptures/folder.gif)

# Install
Please using latest version:

0. [FolderView](https://github.com/o0starshine0o/PagerLayout/tree/master/FolderView/):![Maven Central](https://img.shields.io/maven-central/v/com.github.qicodes/folderview)

```
// iOS风格文件夹
implementation 'com.github.qicodes:folderview:1.0.0'
```

# How To Use

1. create a `targetView` which will used in expand `FolderView`
```kotlin
val targetView = LayoutInflater.from(itemView.context).inflate(R.layout.folder, itemView.rootView as ViewGroup, false)
```
2. init `FolderView` with `targetView` and `itemView` which is shrink view 
```kotlin
val folder = FolderView(itemView.context, itemView.rootView, itemView.iconView, targetView)
```
3. add this `folder` to `rootView`
```kotlin
(itemView.rootView as ViewGroup).addView(folder.expend())
```

That's all!
Enjoy this easy folder view.


# Demo
This project can be run if download.
The main code in [Demo](https://github.com/o0starshine0o/PagerLayout/blob/master/app/src/main/java/com/abelhu/SlideAdapter.kt) in `createFolder` function.
