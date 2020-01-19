package com.abelhu.lockitem

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.widget.ImageView

class LockItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {
    /**
     * 画笔
     */
    private var paint = Paint()
    /**
     * 锁
     */
    private var lock: Drawable? = null
    /**
     * 锁的背景色
     */
    private var lockColor = Color.argb(127, 0, 0, 0)

    /**
     * 锁的宽高
     */
    private var lockWidth = 42f
    private var lockHeight = 47f

    /**
     * 锁的文本
     */
    private var lockText = ""
    private var lockTextColor = Color.WHITE
    private var lockTextSize = 20f
    private var lockTextMargin = 0f

    init {
        // 获取定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LockItem)
        lockColor = typedArray.getColor(R.styleable.LockItem_lockColor, lockColor)
        val lockRes = typedArray.getResourceId(R.styleable.LockItem_lock, -1)
        if (lockRes > 0) lock = AppCompatResources.getDrawable(context, lockRes)
        lockWidth = typedArray.getDimension(R.styleable.LockItem_lockWidth, lockWidth)
        lockHeight = typedArray.getDimension(R.styleable.LockItem_lockHeight, lockHeight)
        lockText = typedArray.getString(R.styleable.LockItem_lockText) ?: lockText
        lockTextColor = typedArray.getColor(R.styleable.LockItem_lockTextColor, lockTextColor)
        lockTextSize = typedArray.getDimension(R.styleable.LockItem_lockTextSize, lockTextSize)
        lockTextMargin = typedArray.getDimension(R.styleable.LockItem_lockTextMargin, lockTextMargin)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLock(canvas)
    }

    @Suppress("DEPRECATION")
    private fun drawLock(canvas: Canvas) {
        // 图层
        val normalLayer = canvas.saveLayer(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 绘制锁的背景色
        canvas.drawColor(lockColor)
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
            val x = (measuredWidth - bounds.width().toFloat()) / 2
            val y = (measuredHeight - bounds.height().toFloat() - lockHeight) / 2 + bounds.height() + lockHeight + lockTextMargin
            canvas.drawText(lockText, x, y, this)
        }
        // 恢复图层
        canvas.restoreToCount(normalLayer)

    }
}