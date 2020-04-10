package com.abelhu.guide

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abelhu.guide.NinePatchBitmapFactory.createNinePatchDrawable

// 使用一个简单的策略模式支持.9图片的绘制
interface WindowDraw {
    fun draw(canvas: Canvas, res: Resources, paint: Paint)
}

@SuppressLint("Range")
class Window(draw: Bitmap? = null, ninePatch: Boolean = false, width: Int = 0, height: Int = 0, vararg views: View) {

    /**
     * 要做差集运算，这边先缓存此bitmap
     */
    var drawDelegate: WindowDraw

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
        // 设定了宽高，寻找中点，重新计算location
        if (width * height != 0) {
            val x = (location[0] + location[2]) / 2
            val y = (location[1] + location[3]) / 2
            location[0] = x - width / 2
            location[1] = y - height / 2
            location[2] = x + width / 2
            location[3] = y + height / 2
        }
        drawDelegate = when {
            draw == null -> BitmapDelegate(Bitmap.createBitmap(location[2] - location[0], location[3] - location[1], Bitmap.Config.ALPHA_8))
            ninePatch -> NinePatchDelegate(draw)
            else -> BitmapDelegate(draw, null)
        }
    }

    /**
     * 增加帮助view
     *
     * @param parent 父容器
     * @param helpId 帮助的view的资源
     * @param position 位于区域的什么位置
     * @param offset 偏移量
     * @param crossOffset 交叉轴的偏移量
     */
    fun addHelp(parent: ViewGroup, @LayoutRes helpId: Int, @Help.Companion.POSITION position: Int, offset: Int = 0, crossOffset: Int = 0): Window {
        return addHelp(parent, LayoutInflater.from(parent.context).inflate(helpId, parent, false), position, offset, crossOffset)
    }

    /**
     * 增加帮助view
     *
     * @param parent 父容器
     * @param help 帮助的view
     * @param position 位于区域的什么位置
     * @param offset 偏移量
     * @param crossOffset 交叉轴的偏移量
     */
    fun addHelp(parent: ViewGroup, help: View, @Help.Companion.POSITION position: Int, offset: Int = 0, crossOffset: Int = 0): Window {
        val newHelp = Help(help, position, offset, crossOffset, Rect(left, top, right, bottom))
        parent.addView(newHelp.mainGuideLine)
        parent.addView(newHelp.crossGuideLine)
        parent.addView(newHelp.help)
        return this
    }

    inner class BitmapDelegate(private val bitmap: Bitmap, private val mode: PorterDuffXfermode? = PorterDuffXfermode(PorterDuff.Mode.DST_IN)) : WindowDraw {
        override fun draw(canvas: Canvas, res: Resources, paint: Paint) {
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

    inner class NinePatchDelegate(private val bitmap: Bitmap) : WindowDraw {
        override fun draw(canvas: Canvas, res: Resources, paint: Paint) {
            createNinePatchDrawable(res, bitmap).apply {
                setBounds(left, top, right, bottom)
                draw(canvas)
            }
        }

    }
}