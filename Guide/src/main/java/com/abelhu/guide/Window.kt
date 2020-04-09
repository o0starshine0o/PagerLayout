package com.abelhu.guide

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

@SuppressLint("Range")
class Window(draw: Bitmap? = null, vararg views: View) {

    /**
     * 要做差集运算，这边先缓存此bitmap
     */
    var bitmapDelegate: BitmapDelegate

    val left
        get() = location[0]
    val top
        get() = location[1]
    val right
        get() = location[2]
    val bottom
        get() = location[3]

    /**
     * 最终的区域
     */
    private val location = intArrayOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

    init {
        if (views.isEmpty()) throw Exception("views size must > 0")
        // 上下文
        val context = views[0].context
        // 遍历所有view,记录需要空出来的位置
        for (view in views) {
            val tempLocation = intArrayOf(0, 0, 0, 0)
            view.getLocationInWindow(tempLocation)
            tempLocation[2] = tempLocation[0] + view.width
            tempLocation[3] = tempLocation[1] + view.height
            if (tempLocation[0] < location[0]) location[0] = tempLocation[0]
            if (tempLocation[1] < location[1]) location[1] = tempLocation[1]
            if (tempLocation[2] > location[2]) location[2] = tempLocation[2]
            if (tempLocation[3] > location[3]) location[3] = tempLocation[3]
        }
        // 绘制需要的图形
        bitmapDelegate = if (draw == null) {
            BitmapDelegate(Bitmap.createBitmap(location[2] - location[0], location[3] - location[1], Bitmap.Config.ALPHA_8))
        } else {
            BitmapDelegate(draw, null)
        }
    }

    /**
     * 增加帮助view
     *
     * @param parent 父容器
     * @param helpId 帮助的view的资源
     * @param position 位于区域的什么位置
     * @param offset 偏移量
     */
    fun addHelp(parent: ViewGroup, @LayoutRes helpId: Int, @Help.Companion.POSITION position: Int, offset: Int = 0): Window {
        return addHelp(parent, LayoutInflater.from(parent.context).inflate(helpId, parent, false), position, offset)
    }

    /**
     * 增加帮助view
     *
     * @param parent 父容器
     * @param help 帮助的view
     * @param position 位于区域的什么位置
     * @param offset 偏移量
     */
    fun addHelp(parent: ViewGroup, help: View, @Help.Companion.POSITION position: Int, offset: Int = 0): Window {
        val newHelp = Help(help, position, offset, Rect(left, top, right, bottom))
        parent.addView(newHelp.mainGuideLine)
        parent.addView(newHelp.crossGuideLine)
        parent.addView(newHelp.help)
        return this
    }

    inner class BitmapDelegate(val bitmap: Bitmap, val mode: PorterDuffXfermode? = PorterDuffXfermode(PorterDuff.Mode.DST_IN)) {
        fun draw(canvas: Canvas, res: Resources, paint: Paint) {
            if (mode == null) {
                BitmapDrawable(res, bitmap).apply {
                    setBounds(left, top, right, bottom)
                    draw(canvas)
                }
            } else {
                paint.xfermode = mode
                canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), paint)
            }
        }
    }
}