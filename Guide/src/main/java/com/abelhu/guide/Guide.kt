package com.abelhu.guide

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class Guide @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    /**
     * 画笔
     */
    private var paint = Paint()

    var window: View? = null
    var windowBitmap: Bitmap? = null

    var backgroundBitmap: Bitmap? = null

    init {
        // 获取定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Guide)
        // 设置画笔
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.WHITE
        // 回收
        typedArray.recycle()
    }

    override fun generateLayoutParams(attrs: AttributeSet): ConstraintLayout.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    override fun draw(canvas: Canvas?) {
        // 把背景转换成bitmap
        if (backgroundBitmap == null) backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        backgroundBitmap?.also {
            background.bounds = Rect(0, 0, width, height)
            background.draw(Canvas(it))
            // background 设置为空，拦截背景的绘制
            background = ColorDrawable(Color.TRANSPARENT)
        }
        // 窗口区域使用新的bitmap
        window?.also {
            if (windowBitmap == null) windowBitmap = Bitmap.createBitmap(it.measuredWidth, it.measuredHeight, Bitmap.Config.ARGB_8888)
        }
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackWithWindow(canvas)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // 获取最后一个标记为touchWindow的child，设置其为window
        if (window == null && childCount > 0) {
            for (i in 0 until childCount) {
                if ((getChildAt(i).layoutParams as LayoutParams).touchWindow) window = getChildAt(i)
            }
        }
        super.onLayout(changed, left, top, right, bottom)
        // 移除所有view
        removeAllViews()
        //
    }

    @Suppress("DEPRECATION")
    private fun drawBackWithWindow(canvas: Canvas) {
        // 获取图层
        val layer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 绘制背景
        backgroundBitmap?.also { canvas.drawBitmap(it, 0f, 0f, paint) }
        // 去掉窗口区域
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        val location = intArrayOf(0, 0)
        window?.getLocationInWindow(location)
        windowBitmap?.also { canvas.drawBitmap(it, location[0].toFloat(), location[1].toFloat(), paint) }
        paint.xfermode = null
        // 应用图层
        canvas.restoreToCount(layer)
    }

    class LayoutParams : ConstraintLayout.LayoutParams {
        var touchWindow = false

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Guide_Layout)
            touchWindow = typedArray.getBoolean(R.styleable.Guide_Layout_layout_touchWindow, touchWindow)
            typedArray.recycle()
        }
    }
}