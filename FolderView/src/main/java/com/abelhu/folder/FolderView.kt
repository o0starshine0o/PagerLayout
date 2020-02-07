package com.abelhu.folder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

class FolderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle) {

    var title: TextView? = null
    /**
     * 起始的itemView位置：left, top, right, bottom, width, height
     */
    private val location = IntArray(6) { 0 }
    private var target: View? = null

    constructor(context: Context, backView: View?, blurRadius: Float = 8f, itemView: View? = null, targetView: View? = null) : this(context) {
        // 创建模糊背景的background
        backView?.apply {
            val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val tempBitmap = Bitmap.createBitmap(bitmap)
            val render = RenderScript.create(context)
            val blur = ScriptIntrinsicBlur.create(render, Element.U8_4(render))
            val tempIn = Allocation.createFromBitmap(render, bitmap)
            val tempOut = Allocation.createFromBitmap(render, tempBitmap)
            blur.setRadius(blurRadius)
            blur.setInput(tempIn)
            blur.forEach(tempOut)
            tempOut.copyTo(tempBitmap)
            this@FolderView.background = BitmapDrawable(resources, tempBitmap)
        }
        // 获取itemView的位置信息
        itemView?.getLocationInWindow(location)
        location[4] = itemView?.measuredWidth ?: 0
        location[5] = itemView?.measuredHeight ?: 0
        location[2] = location[0] + location[4]
        location[3] = location[1] + location[5]
        // 记录targetView
        target = targetView
        // target
        target?.layoutParams = ((if (target?.layoutParams == null) generateDefaultLayoutParams() else LayoutParams(layoutParams)) as LayoutParams).apply {
            width = location[4]
            height = location[5]
            leftMargin = location[0]
            topMargin = location[1]
            rightMargin = (background as BitmapDrawable).intrinsicWidth - location[2]
            bottomMargin = (background as BitmapDrawable).intrinsicHeight - location[3]
        }
        addView(target)
    }

    init {
        id = generateViewId()
        // titleGuide
        val titleGuide = Guideline(context).apply {
            id = generateViewId()
            layoutParams = generateDefaultLayoutParams()
        }
        (titleGuide.layoutParams as LayoutParams).apply {
            orientation = LayoutParams.HORIZONTAL
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            guidePercent = 0.1f
        }
        addView(titleGuide)
        // title
        title = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = generateDefaultLayoutParams()
            textSize = 30f
        }
        title?.setTextColor(Color.WHITE)
        (title?.layoutParams as LayoutParams).apply {
            orientation = LayoutParams.HORIZONTAL
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            topToBottom = titleGuide.id
        }
        addView(title)
    }

    /**
     * 展开函数
     * 用于从某一固定位置展开target
     */
    fun expend() {

    }
}