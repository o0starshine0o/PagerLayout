package com.abelhu.guide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.annotation.IntDef
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.Guideline
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.abelhu.guide.Guide.LayoutParams.Companion.TYPE_STAY
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

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

    fun addWindow(view: View, @LayoutRes help: Int, offset: Int = 0, @Window.Companion.POSITION position: Int = Window.BOTTOM): Guide {
        return addWindow(view, LayoutInflater.from(context).inflate(help, this, false), offset, position)
    }

    fun addWindow(view: View, help: View? = null, offset: Int = 0, @Window.Companion.POSITION position: Int = Window.BOTTOM): Guide {
        val window = Window(view, intArrayOf(0, 0, 0, 0), position, help, offset)
        addView(window.mainGuideLine)
        addView(window.crossGuideLine)
        addView(window.help)
        // 记录窗口，用于穿透区域绘制和点击判定
        windows.add(window)
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
        if (background == null) background = ColorDrawable(Color.argb(191, 0, 0, 0))
        // 设置布局铺满父控件
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        // 设置此控件的id
        id = R.id.guide
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 窗口区域不拦截，可以直接点击
        for (window in windows) if (RectF(window.left, window.top, window.right, window.bottom).contains(event.x, event.y)) return false
        return super.onTouchEvent(event)
    }

    class Window(view: View, private val location: IntArray, @POSITION position: Int = BOTTOM, help: View? = null, offset: Int = 0) {
        companion object {
            @Retention(AnnotationRetention.SOURCE)
            @IntDef(LEFT, TOP, RIGHT, BOTTOM)
            annotation class POSITION

            const val LEFT = 1
            const val TOP = 2
            const val RIGHT = 3
            const val BOTTOM = 4
        }

        /**
         * 要做差集运算，这边先缓存此bitmap
         */
        var bitmap: Bitmap? = null
        /**
         * 主轴
         */
        var mainGuideLine: Guideline? = null
        /**
         * 交叉轴
         */
        var crossGuideLine: Guideline? = null
        /**
         * 显示提示文案的view
         */
        var help: View? = null

        val left
            get() = location[0].toFloat()
        val top
            get() = location[1].toFloat()
        val right
            get() = location[2].toFloat()
        val bottom
            get() = location[3].toFloat()

        init {
            // 记录view的位置
            view.getLocationInWindow(location)
            location[2] = location[0] + view.width
            location[3] = location[1] + view.height
            // 绘制需要裁剪的图形
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ALPHA_8)
            // 根据position设置mainGuideLine和crossGuideLine
            mainGuideLine = Guideline(view.context)
            mainGuideLine?.id = generateViewId()
            val mainGuideLayout = LayoutParams(LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            mainGuideLayout.type = TYPE_STAY
            crossGuideLine = Guideline(view.context)
            crossGuideLine?.id = generateViewId()
            val crossGuideLayout = LayoutParams(LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            crossGuideLayout.type = TYPE_STAY
            when (position) {
                LEFT -> {
                    mainGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                    mainGuideLayout.guideBegin = left.toInt() + offset
                    mainGuideLayout.topToTop = R.id.guide
                    mainGuideLayout.bottomToBottom = R.id.guide
                    crossGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                    crossGuideLayout.guideBegin = ((top + bottom) / 2).toInt()
                    crossGuideLayout.startToStart = R.id.guide
                    crossGuideLayout.endToEnd = R.id.guide
                }
                TOP -> {
                    mainGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                    mainGuideLayout.guideBegin = top.toInt() + offset
                    mainGuideLayout.startToStart = R.id.guide
                    mainGuideLayout.endToEnd = R.id.guide
                    crossGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                    crossGuideLayout.guideBegin = ((left + right) / 2).toInt()
                    crossGuideLayout.topToTop = R.id.guide
                    crossGuideLayout.bottomToBottom = R.id.guide
                }
                RIGHT -> {
                    mainGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                    mainGuideLayout.guideBegin = right.toInt() + offset
                    mainGuideLayout.topToTop = R.id.guide
                    mainGuideLayout.bottomToBottom = R.id.guide
                    crossGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                    crossGuideLayout.guideBegin = ((top + bottom) / 2).toInt()
                    crossGuideLayout.startToStart = R.id.guide
                    crossGuideLayout.endToEnd = R.id.guide
                }
                BOTTOM -> {
                    mainGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                    mainGuideLayout.guideBegin = bottom.toInt() + offset
                    mainGuideLayout.startToStart = R.id.guide
                    mainGuideLayout.endToEnd = R.id.guide
                    crossGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                    crossGuideLayout.guideBegin = ((left + right) / 2).toInt()
                    crossGuideLayout.topToTop = R.id.guide
                    crossGuideLayout.bottomToBottom = R.id.guide
                }
            }
            mainGuideLine?.layoutParams = mainGuideLayout
            crossGuideLine?.layoutParams = crossGuideLayout
            // 设置help的位置
            val helpLayout = LayoutParams(help?.layoutParams ?: LayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
                type = TYPE_STAY
                when (position) {
                    LEFT -> {
                        endToStart = mainGuideLine?.id ?: R.id.guide
                        topToTop = crossGuideLine?.id ?: R.id.guide
                        bottomToBottom = crossGuideLine?.id ?: R.id.guide
                    }
                    TOP -> {
                        bottomToTop = mainGuideLine?.id ?: R.id.guide
                        startToStart = crossGuideLine?.id ?: R.id.guide
                        endToEnd = crossGuideLine?.id ?: R.id.guide
                    }
                    RIGHT -> {
                        startToEnd = mainGuideLine?.id ?: R.id.guide
                        topToTop = crossGuideLine?.id ?: R.id.guide
                        bottomToBottom = crossGuideLine?.id ?: R.id.guide
                    }
                    BOTTOM -> {
                        topToBottom = mainGuideLine?.id ?: R.id.guide
                        startToStart = crossGuideLine?.id ?: R.id.guide
                        endToEnd = crossGuideLine?.id ?: R.id.guide
                    }
                }
            }
            help?.layoutParams = helpLayout
            this.help = help
        }

        private fun generateViewId(): Int {
            while (true) {
                // 通过反射获取id生成器
                val clazz = View::class.java
                val field = clazz.getDeclaredField("sNextGeneratedId")
                field.isAccessible = true
                val nextGeneratedId = field.get(null) as AtomicInteger
                val result: Int = nextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (nextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }
    }

    class LayoutParams : ConstraintLayout.LayoutParams {
        companion object {
            @Retention(AnnotationRetention.SOURCE)
            @IntDef(TYPE_OTHER, TYPE_WINDOW, TYPE_STAY)
            annotation class TYPE

            const val TYPE_OTHER = 0
            const val TYPE_WINDOW = 1
            const val TYPE_STAY = 2
        }

        @TYPE
        var type = TYPE_OTHER

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Guide_Layout)
            type = typedArray.getInt(R.styleable.Guide_Layout_layout_type, TYPE_OTHER)
            typedArray.recycle()
        }
    }
}