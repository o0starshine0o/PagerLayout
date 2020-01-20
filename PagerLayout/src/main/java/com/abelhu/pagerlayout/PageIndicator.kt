package com.abelhu.pagerlayout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View

abstract class PageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var totalPage = 0
    private var pageWidth = 0
    private var scrollDistance = 0
    private var empty = 0
    val paint = Paint()
    /**
     * normal状态下的图片
     */
    private var indicatorBitmap: Bitmap? = null

    init {
        // 设置画笔
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
        paint.style = Paint.Style.FILL
    }

    /**
     * 根据indicatorSize计算出精确的宽高
     * 目前只强制设置高度，宽度只支持match_parent
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasure = MeasureSpec.makeMeasureSpec(totalPage * indicatorWidth().toInt(), MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(indicatorHeight().toInt(), MeasureSpec.EXACTLY)
        setMeasuredDimension(widthMeasureSpec, heightMeasure)
        super.onMeasure(widthMeasureSpec, heightMeasure)
        Log.i("PageIndicator", "onMeasure(${MeasureSpec.getSize(widthMeasure)}, ${MeasureSpec.getSize(heightMeasure)})")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 计算两边留白
        empty = (measuredWidth - totalPage * indicatorWidth().toInt()) / 2
        // 绘制正常情况的indicator
        drawNormal(canvas)
        // 绘制选中情况额indicator
        drawSelect(canvas)
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        // 保存recycleView的宽度用于计算
        recyclerView.post { pageWidth = recyclerView.layoutManager.width }
        // 当PagerLayoutManager的frames计算完成便获取了所有的page，这里添加回调进行保存
        (recyclerView.layoutManager as? PagerLayoutManager)?.onLayoutComplete {
            totalPage = it
        }
        // 设置滑动监听，动态绘制选中的Indicator
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                scrollDistance += dx
                postInvalidate()
            }
        })
    }

    @Suppress("DEPRECATION")
    private fun drawNormal(canvas: Canvas) {
        // 图层
        val normalLayer = canvas.saveLayer(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        // 如果indicatorBitmap有就不用绘制，否则生成和控件一样大小的bitmap
        if (indicatorBitmap == null) {
            indicatorBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            indicatorBitmap?.also {
                // 使用新的canvas保存indicatorBitmap
                val indicatorCanvas = Canvas(it)
                // 绘制全部常态的indicator
                for (i in 0 .. totalPage) drawNormal(indicatorCanvas, empty + i* indicatorWidth(), indicatorHeight() / 2)
            }
        }
        // 绘制indicatorBitmap
        indicatorBitmap?.also { canvas.drawBitmap(it, 0f, 0f, paint) }
        // 恢复图层
        canvas.restoreToCount(normalLayer)
    }

    @Suppress("DEPRECATION")
    private fun drawSelect(canvas: Canvas) {
        val selectLayer = canvas.saveLayer(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint, Canvas.ALL_SAVE_FLAG)
        drawSelect(canvas, empty + indicatorWidth() * scrollDistance / pageWidth, measuredHeight.toFloat() / 2)
        canvas.restoreToCount(selectLayer)
    }

    abstract fun drawNormal(canvas: Canvas, centerX: Float, centerY: Float)
    abstract fun drawSelect(canvas: Canvas, centerX: Float, centerY: Float)
    abstract fun indicatorWidth(): Float
    abstract fun indicatorHeight(): Float
}