package com.abelhu.pagerlayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet

@SuppressLint("CustomViewStyleable")
class DrawablePageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : PageIndicator(context, attrs, defStyleAttr) {
    //    private var normalColor = Color.rgb(172, 172, 172)
//    private var selectColor = Color.rgb(127, 127, 127)
    private var indicatorWidth = 20f
    private var indicatorHeight = 20f
    //    private var isRound = false
    private var normal: Drawable? = null
    private var select: Drawable? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator)
        indicatorWidth = typedArray.getDimension(R.styleable.PageIndicator_indicatorWidth, indicatorWidth)
        indicatorHeight = typedArray.getDimension(R.styleable.PageIndicator_indicatorHeight, indicatorHeight)
        typedArray.recycle()
        val drawableTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawablePageIndicator)
//        normalColor = typedArray.getColor(R.styleable.PageIndicator_normalColor, normalColor)
//        selectColor = typedArray.getColor(R.styleable.PageIndicator_selectColor, selectColor)
//        isRound = typedArray.getBoolean(R.styleable.LinePageIndicator_round, isRound)
        normal = AppCompatResources.getDrawable(context, drawableTypedArray.getResourceId(R.styleable.DrawablePageIndicator_normal, -1))
        select = AppCompatResources.getDrawable(context, drawableTypedArray.getResourceId(R.styleable.DrawablePageIndicator_select, -1))
        drawableTypedArray.recycle()
    }

    override fun drawNormal(canvas: Canvas, centerX: Float, centerY: Float) {
        normal?.bounds = getRect(centerX, centerY)
        normal?.draw(canvas)
//        paint.color = normalColor
//        canvas.draw
//        if (isRound) {
//            paint.apply { canvas.drawRoundRect(getRect(centerX, centerY), Float.MAX_VALUE, Float.MAX_VALUE, this) }
//        } else {
//            paint.apply { canvas.drawRect(getRect(centerX, centerY), this) }
//        }
    }

    override fun drawSelect(canvas: Canvas, centerX: Float, centerY: Float) {
        select?.bounds = getRect(centerX, centerY)
        select?.draw(canvas)
//        paint.color = selectColor
//        if (isRound) {
//            paint.apply { canvas.drawRoundRect(getRect(centerX, centerY), Float.MAX_VALUE, Float.MAX_VALUE, this) }
//        } else {
//            paint.apply { canvas.drawRect(getRect(centerX, centerY), this) }
//        }
    }

    override fun indicatorWidth() = indicatorWidth

    override fun indicatorHeight() = indicatorHeight

    private fun getRect(centerX: Float, centerY: Float): Rect {
        return Rect().apply {
            left = (centerX - indicatorWidth / 2 + paddingLeft).toInt()
            top = (centerY - indicatorHeight / 2 + paddingTop).toInt()
            right = (centerX + indicatorWidth / 2 - paddingRight).toInt()
            bottom = (centerY + indicatorHeight / 2 - paddingBottom).toInt()
        }
    }
}