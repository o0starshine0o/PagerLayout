package com.abelhu.guide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.abelhu.guide.Guide.LayoutParams.Companion.TYPE_STAY
import com.abelhu.guide.Guide.LayoutParams.Companion.TYPE_WINDOW
import java.util.*

class Guide @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    companion object {
        private val Tag = Guide::class.java.simpleName
    }

    var touchDismiss = true
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
     */
    private var backgroundBitmap: Bitmap? = null
    /**
     * 除去必要的空间，此控件内的子控件都需要移除
     */
    private val removeList = LinkedList<View>()

    fun addWindow(view: View): Guide {
        windows.add(Window(view, intArrayOf(0, 0)))
        return this
    }

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
        if (background == null) background = ColorDrawable(Color.argb(127, 0, 0, 0))
        // 设置布局铺满父控件
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
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
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackWithWindow(canvas)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 获取最后一个标记为touchWindow的child，设置其为window
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            when ((child.layoutParams as LayoutParams).type) {
                TYPE_WINDOW -> {
                    windows.add(Window(child, intArrayOf(0, 0)))
                    removeList.add(child)
                }
                TYPE_STAY -> Log.i(Tag, "stay child")
                else -> removeList.add(child)
            }
        }
        // 移除所有要被移除的children
        for (view in removeList) removeView(view)
    }

    @Suppress("DEPRECATION")
    private fun drawBackWithWindow(canvas: Canvas) {
        // 获取图层
        val layer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 绘制背景
        backgroundBitmap?.also { canvas.drawBitmap(it, 0f, 0f, paint) }
        // 去掉窗口区域
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        for (window in windows) {
            window.bitmap?.also { canvas.drawBitmap(it, window.left, window.top, paint) }
        }
        paint.xfermode = null
        // 应用图层
        canvas.restoreToCount(layer)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 窗口区域不拦截，可以直接点击
        for (window in windows) {
            if (RectF(window.left, window.top, window.left + window.view.width, window.top + window.view.height).contains(event.x, event.y)) return false
        }
        return super.onTouchEvent(event)
    }

    class Window(val view: View, private val location: IntArray) {
        var bitmap: Bitmap? = null
        val left
            get() = location[0].toFloat()
        val top
            get() = location[1].toFloat()

        init {
            view.getLocationInWindow(location)
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        }
    }

    class LayoutParams : ConstraintLayout.LayoutParams {
        companion object {
            const val TYPE_OTHER = 0
            const val TYPE_WINDOW = 1
            const val TYPE_STAY = 2
        }

        var type = TYPE_OTHER

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Guide_Layout)
            type = typedArray.getInt(R.styleable.Guide_Layout_layout_type, TYPE_OTHER)
            typedArray.recycle()
        }
    }
}