package com.abelhu.nine

import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.util.Log
import java.util.*
import kotlin.math.min

class GridDrawable<T>(@GRID private val grid: Int = THREE, private val itemPadding: Int = 0, private val generator: Generate<T>? = null) : Drawable() {

    interface Generate<T> {
        fun generateResource(obj: T): Any?
    }

    private val resList = LinkedList<T>()

    private var bitmap: Bitmap? = null

    private val paint = Paint()

    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(TWO, THREE, FOUR)
        annotation class GRID

        const val TWO = 2
        const val THREE = 3
        const val FOUR = 4

        private val Tag = GridDrawable::class.java.simpleName
    }

    init {
        // 设置画笔
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
    }

    fun addRes(vararg resources: T): GridDrawable<T> {
        resList.addAll(resources.asList())
        invalidateSelf()
        return this
    }

    override fun draw(canvas: Canvas) {
        if (bitmap == null && resList.size > 0) {
            val start = System.currentTimeMillis()
            bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
            val tempCanvas = Canvas(bitmap!!)
            // 设置背景色
            tempCanvas.drawColor(Color.argb(127, 0, 0, 0))
            // 计算需要的尺寸
            val itemWidth = (bounds.width() - (1 + grid) * itemPadding) / grid
            val itemHeight = (bounds.height() - (1 + grid) * itemPadding) / grid
            for (i in 0 until min(grid * grid, resList.size)) {
                // 根据记录的资源值，获取bitmap或者drawable
                generator?.generateResource(resList[i])?.also {
                    // 计算中心值
                    val column = i % grid
                    val row = i / grid
                    val left = itemPadding * (column + 1) + itemWidth * column
                    val top = itemPadding * (row + 1) + itemHeight * row
                    // 计算填充区域
                    val rect = Rect(left, top, left + itemWidth, top + itemHeight)
                    // 绘制
                    when (it) {
                        // 绘制bitmap
                        is Bitmap -> tempCanvas.drawBitmap(it, null, rect, paint)
                        // 绘制drawable
                        is Drawable -> {
                            it.bounds = rect
                            it.draw(tempCanvas)
                        }
                        else -> Log.e(Tag, "the resource can't be drawn")
                    }
                }
            }
            Log.i(Tag, "create bitmap use ${System.currentTimeMillis() - start} milliseconds")
        }
        bitmap?.also { canvas.drawBitmap(it, null, bounds, paint) }
    }

    override fun setAlpha(alpha: Int) {
        if (alpha != paint.alpha) {
            paint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }
}