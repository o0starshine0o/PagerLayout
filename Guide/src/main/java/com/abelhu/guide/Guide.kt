package com.abelhu.guide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import java.util.*

class Guide @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    var touchDismiss = false
    /**
     * 画笔
     */
    private var paint = Paint()
    /**
     * 需要扣除的窗口
     */
    private var windows = LinkedList<Window>()
    /**
     * 缓存背景
     * 由于需要做差集运算，必须使用bitmap
     */
    private var backgroundBitmap: Bitmap? = null

    /**
     * @param draw 如果传值，则替代透明区域
     * @param views 由哪些view组成透明区域
     */
    fun addWindow(draw: Bitmap? = null, vararg views: View): Window {
        // 记录窗口，用于穿透区域绘制和点击判定
        return Window(draw, *views).apply { windows.add(this) }
    }

    /**
     * 增加跳过按钮
     */
    fun addSkip(@LayoutRes skip: Int, top: Int = 120, params: LayoutParams? = null) {
        addSkip(LayoutInflater.from(context).inflate(skip, this, false), top, params)
    }

    /**
     * 增加跳过按钮
     */
    fun addSkip(skip: View, top: Int = 120, params: LayoutParams? = null) {
        skip.layoutParams = params ?: LayoutParams(LayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
            topToTop = id
            endToEnd = id
            topMargin = top
        }
        addView(skip)
    }

    /**
     * 关闭引导
     */
    fun dismiss() {
        val parent = parent as ViewGroup
        parent.removeView(this)
        parent.postInvalidate()
    }

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
        // 设置默认遮罩颜色
        if (background == null) background = ColorDrawable(Color.argb(191, 0, 0, 0))
        // 设置布局铺满父控件
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        // 设置此控件的id
        id = R.id.guide
        // 防止点击穿透
        isClickable = true
        // 设置点击事件
        if (touchDismiss) setOnClickListener { dismiss() }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(context, attrs)

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams = LayoutParams(p)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?) = p is LayoutParams

    override fun draw(canvas: Canvas?) {
        // 把背景转换成bitmap
        if (backgroundBitmap == null) backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        backgroundBitmap?.also {
            background.bounds = Rect(0, 0, width, height)
            background.draw(Canvas(it))
            // background 设置为空，拦截背景的绘制
            background = ColorDrawable(Color.TRANSPARENT)
        }
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackWithWindow(canvas)
    }

    @Suppress("DEPRECATION")
    private fun drawBackWithWindow(canvas: Canvas) {
        // 获取图层
        val layer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 绘制背景
        backgroundBitmap?.also { canvas.drawBitmap(it, 0f, 0f, paint) }
        // 去掉或者绘制窗口区域
        windows.forEach { it.bitmapDelegate.draw(canvas, context.resources, paint) }
//        for (window in windows) {
//            paint.xfermode = window.bitmapDelegate.mode
//            window.bitmapDelegate.bitmap.also { canvas.drawBitmap(it, window.left.toFloat(), window.top.toFloat(), paint) }
//        }
        paint.xfermode = null
        // 应用图层
        canvas.restoreToCount(layer)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 窗口区域不拦截，可以直接点击
        for (window in windows) if (Rect(window.left, window.top, window.right, window.bottom).contains(event.x.toInt(), event.y.toInt())) return false
        return super.onTouchEvent(event)
    }
}