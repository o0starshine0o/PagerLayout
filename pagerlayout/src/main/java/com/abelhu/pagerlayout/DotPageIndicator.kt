package com.abelhu.pagerlayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import kotlin.math.max
import kotlin.math.min

class DotPageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : PageIndicator(context, attrs, defStyleAttr) {
    private var normalColor = Color.rgb(172, 172, 172)
    private var selectColor = Color.rgb(127, 127, 127)
    private var indicatorWidth = 20f
    private var indicatorHeight = 20f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotPageIndicator)
        indicatorWidth = typedArray.getDimension(R.styleable.DotPageIndicator_indicatorWidth, indicatorWidth)
        indicatorHeight = typedArray.getDimension(R.styleable.DotPageIndicator_indicatorHeight, indicatorHeight)
        normalColor = typedArray.getColor(R.styleable.DotPageIndicator_normalColor, normalColor)
        selectColor = typedArray.getColor(R.styleable.DotPageIndicator_selectColor, selectColor)
        typedArray.recycle()
    }

    override fun drawNormal(canvas: Canvas, centerX: Float, centerY: Float) {
        val padding = max((paddingBottom + paddingTop) / 2, (paddingLeft + paddingRight) / 2)
        paint.color = normalColor
        paint.apply { canvas.drawCircle(centerX, centerY, min(indicatorWidth, indicatorHeight) / 2 - padding, this) }
    }

    override fun drawSelect(canvas: Canvas, centerX: Float, centerY: Float) {
        val padding = max((paddingBottom + paddingTop) / 2, (paddingLeft + paddingRight) / 2)
        paint.color = selectColor
        paint.apply { canvas.drawCircle(centerX, centerY, min(indicatorWidth, indicatorHeight) / 2 - padding, this) }
    }

    override fun indicatorWidth() = indicatorWidth

    override fun indicatorHeight() = indicatorHeight
}