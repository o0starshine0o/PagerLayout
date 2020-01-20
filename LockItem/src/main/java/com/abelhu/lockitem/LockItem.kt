package com.abelhu.lockitem

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class LockItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    /**
     * 画笔
     */
    private var paint = Paint()

    /**
     * 锁
     */
    var lock: Drawable? = null
    var showLock = false
    var lockWidth = 42f
    var lockHeight = 47f
    var lockBackgroundColor = Color.argb(127, 0, 0, 0)
    var lockText = ""
    var lockTextColor = Color.WHITE
    var lockTextSize = 20f
    var lockTextMargin = 0f

    /**
     * 圆角
     */
    var cornerSize = 0f
    private var cornerBitmap: Bitmap? = null

    /**
     * 红点
     */
    var dotBigRadio = 40f
    var dotNormalRadio = 20f
    var dotBackgroundColor = Color.WHITE
    var dotBackgroundRadio = 1f
    var showNumber = true
    var dotNumber = 99
    var dotColor = Color.RED
    var dotTextSize = 20f

    init {
        // 获取定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LockItem)
        // 锁
        lockBackgroundColor = typedArray.getColor(R.styleable.LockItem_lockBackgroundColor, lockBackgroundColor)
        val lockRes = typedArray.getResourceId(R.styleable.LockItem_lock, -1)
        if (lockRes > 0) lock = AppCompatResources.getDrawable(context, lockRes)
        showLock = typedArray.getBoolean(R.styleable.LockItem_showLock, showLock)
        lockWidth = typedArray.getDimension(R.styleable.LockItem_lockWidth, lockWidth)
        lockHeight = typedArray.getDimension(R.styleable.LockItem_lockHeight, lockHeight)
        lockText = typedArray.getString(R.styleable.LockItem_lockText) ?: lockText
        lockTextColor = typedArray.getColor(R.styleable.LockItem_lockTextColor, lockTextColor)
        lockTextSize = typedArray.getDimension(R.styleable.LockItem_lockTextSize, lockTextSize)
        lockTextMargin = typedArray.getDimension(R.styleable.LockItem_lockTextMargin, lockTextMargin)
        // 圆角
        cornerSize = typedArray.getDimension(R.styleable.LockItem_cornerSize, cornerSize)
        // 红点
        dotBigRadio = typedArray.getDimension(R.styleable.LockItem_dotBigRadio, dotBigRadio)
        dotNormalRadio = typedArray.getDimension(R.styleable.LockItem_dotNormalRadio, dotNormalRadio)
        showNumber = typedArray.getBoolean(R.styleable.LockItem_showNumber, showNumber)
        dotNumber = typedArray.getInt(R.styleable.LockItem_dotNumber, dotNumber)
        dotColor = typedArray.getColor(R.styleable.LockItem_dotColor, dotColor)
        dotTextSize = typedArray.getDimension(R.styleable.LockItem_dotTextSize, dotTextSize)
        dotBackgroundColor = typedArray.getColor(R.styleable.LockItem_dotBackgroundColor, dotBackgroundColor)
        dotBackgroundRadio = typedArray.getDimension(R.styleable.LockItem_dotBackgroundRadio, dotBackgroundRadio)
        // 设置画笔
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        // 回收
        typedArray.recycle()
    }

    @Suppress("DEPRECATION")
    override fun onDraw(canvas: Canvas) {
        // 完成需要裁减的绘制
        val normalLayer = canvas.saveLayer(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        super.onDraw(canvas)
        if (showLock) drawLock(canvas)
        drawCorners(canvas)
        // 恢复图层
        canvas.restoreToCount(normalLayer)
        // 绘制不受圆角影响的圆点
        if (dotNumber > 0) drawDot(canvas)
    }

    @Suppress("DEPRECATION")
    private fun drawLock(canvas: Canvas) {
        // 图层
        val normalLayer = canvas.saveLayer(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 绘制锁的背景色
        paint.color = lockBackgroundColor
        canvas.drawRect(paddingLeft.toFloat(), paddingTop.toFloat(), width - paddingRight.toFloat(), height - paddingBottom.toFloat(), paint)
        // 绘制锁
        val left = (measuredWidth - lockWidth).toInt() / 2
        val top = (measuredHeight - lockHeight).toInt() / 2
        lock?.bounds = Rect(left, top, left + lockWidth.toInt(), top + lockHeight.toInt())
        lock?.draw(canvas)
        // 绘制锁下方文字
        val bounds = Rect()
        paint.getTextBounds(lockText, 0, lockText.length, bounds)
        paint.apply {
            color = lockTextColor
            textSize = lockTextSize
            val x = (measuredWidth - paddingLeft - paddingRight - bounds.width().toFloat()) / 2 + paddingLeft + bounds.width() / 2
            val y = (measuredHeight - bounds.height().toFloat() - lockHeight) / 2 + bounds.height() + lockHeight + lockTextMargin
            canvas.drawText(lockText, x, y, this)
        }
        // 恢复图层
        canvas.restoreToCount(normalLayer)
    }

    private fun drawCorners(canvas: Canvas) {
        // 初始化圆角图片
        if (cornerBitmap == null) {
            cornerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            cornerBitmap?.also {
                val rect = RectF(paddingLeft.toFloat(), paddingTop.toFloat(), width - paddingRight.toFloat(), height - paddingBottom.toFloat())
                Canvas(it).drawRoundRect(rect, cornerSize, cornerSize, paint)
            }
        }
        // 绘制圆角
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        cornerBitmap?.also { canvas.drawBitmap(it, 0f, 0f, paint) }
        paint.xfermode = null
    }

    @Suppress("DEPRECATION")
    private fun drawDot(canvas: Canvas) {
        // 图层
        val normalLayer = canvas.saveLayer(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 绘制红点
        val dotRadio = if (showNumber) dotBigRadio else dotNormalRadio
        paint.color = dotBackgroundColor
        canvas.drawCircle(measuredWidth - paddingRight.toFloat(), paddingTop.toFloat(), dotRadio + dotBackgroundRadio, paint)
        paint.color = dotColor
        canvas.drawCircle(measuredWidth - paddingRight.toFloat(), paddingTop.toFloat(), dotRadio, paint)
        // 绘制红点数据
        if (showNumber) {
            val number = if (dotNumber > 99) "···" else dotNumber.toString()
            paint.color = Color.WHITE
            paint.textSize = dotTextSize
            val x = measuredWidth - paddingRight.toFloat()
            val y = paddingTop.toFloat() + (paint.fontMetrics.descent - paint.fontMetrics.ascent) / 2 - paint.fontMetrics.descent
            canvas.drawText(number, x, y, paint)
        }
        // 恢复图层
        canvas.restoreToCount(normalLayer)
    }
}