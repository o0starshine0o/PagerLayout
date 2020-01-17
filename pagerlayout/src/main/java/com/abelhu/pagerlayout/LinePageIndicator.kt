package com.abelhu.pagerlayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet

@SuppressLint("CustomViewStyleable")
class LinePageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : PageIndicator(context, attrs, defStyleAttr) {
    private var normalColor = Color.rgb(172, 172, 172)
    private var selectColor = Color.rgb(127, 127, 127)
    private var indicatorWidth = 20f
    private var indicatorHeight = 20f
    private var isRound = false

     init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator)
        indicatorWidth = typedArray.getDimension(R.styleable.PageIndicator_indicatorWidth, indicatorWidth)
        indicatorHeight = typedArray.getDimension(R.styleable.PageIndicator_indicatorHeight, indicatorHeight)
        normalColor = typedArray.getColor(R.styleable.PageIndicator_normalColor, normalColor)
        selectColor = typedArray.getColor(R.styleable.PageIndicator_selectColor, selectColor)
        isRound = typedArray.getBoolean(R.styleable.LinePageIndicator_round, isRound)
        typedArray.recycle()
    }

    override fun drawNormal(canvas: Canvas, centerX: Float, centerY: Float) {
        paint.color = normalColor
        if (isRound) {
            paint.apply { canvas.drawRoundRect(getRect(centerX, centerY), Float.MAX_VALUE, Float.MAX_VALUE, this) }
        } else {
            paint.apply { canvas.drawRect(getRect(centerX, centerY), this) }
        }
    }

    override fun drawSelect(canvas: Canvas, centerX: Float, centerY: Float) {
        paint.color = selectColor
        if (isRound) {
            paint.apply { canvas.drawRoundRect(getRect(centerX, centerY), Float.MAX_VALUE, Float.MAX_VALUE, this) }
        } else {
            paint.apply { canvas.drawRect(getRect(centerX, centerY), this) }
        }
    }

    override fun indicatorWidth() = indicatorWidth

    override fun indicatorHeight() = indicatorHeight

    private fun getRect(centerX: Float, centerY: Float): RectF {
        return RectF().apply {
            left = centerX - indicatorWidth / 2 + paddingLeft
            top = centerY - indicatorHeight / 2 + paddingTop
            right = centerX + indicatorWidth / 2 - paddingRight
            bottom = centerY + indicatorHeight / 2 - paddingBottom
        }
    }
}