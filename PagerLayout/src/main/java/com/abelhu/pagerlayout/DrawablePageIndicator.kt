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
    private var indicatorWidth = 20f
    private var indicatorHeight = 20f
    private var normal: Drawable? = null
    private var select: Drawable? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator)
        indicatorWidth = typedArray.getDimension(R.styleable.PageIndicator_indicatorWidth, indicatorWidth)
        indicatorHeight = typedArray.getDimension(R.styleable.PageIndicator_indicatorHeight, indicatorHeight)
        typedArray.recycle()
        val drawableTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawablePageIndicator)
        val normalRes = drawableTypedArray.getResourceId(R.styleable.DrawablePageIndicator_normal, -1)
        if (normalRes > 0) normal = AppCompatResources.getDrawable(context, normalRes)
        val selectRes = drawableTypedArray.getResourceId(R.styleable.DrawablePageIndicator_select, -1)
        if (selectRes > 0) select = AppCompatResources.getDrawable(context, selectRes)
        drawableTypedArray.recycle()
    }

    override fun drawNormal(canvas: Canvas, centerX: Float, centerY: Float) {
        normal?.bounds = getRect(centerX, centerY)
        normal?.draw(canvas)
    }

    override fun drawSelect(canvas: Canvas, centerX: Float, centerY: Float) {
        select?.bounds = getRect(centerX, centerY)
        select?.draw(canvas)
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