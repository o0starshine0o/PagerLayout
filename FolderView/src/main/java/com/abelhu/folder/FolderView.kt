package com.abelhu.folder

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView


class FolderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle) {
    companion object {
        private val Tag = FolderView::class.java.simpleName
    }

    private var title: TextView? = null
    /**
     * 起始的itemView位置：left, top, right, bottom, width, height
     */
    private val shrinkRect = Rect()
    /**
     * 起始的itemView位置：left, top, right, bottom, width, height
     */
    private val expandRect = Rect()

    private var target: View? = null

    var expandMargin = -1f

    constructor(context: Context, backView: View, blurRadius: Float = 8f, itemView: View? = null, targetView: View? = null) : this(context) {
        // 创建模糊背景的background
        backView.apply {
            val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val tempBitmap = Bitmap.createBitmap(bitmap)
            val render = RenderScript.create(context)
            val blur = ScriptIntrinsicBlur.create(render, Element.U8_4(render))
            val tempIn = Allocation.createFromBitmap(render, bitmap)
            val tempOut = Allocation.createFromBitmap(render, tempBitmap)
            blur.setRadius(blurRadius)
            blur.setInput(tempIn)
            blur.forEach(tempOut)
            tempOut.copyTo(tempBitmap)
            this@FolderView.background = BitmapDrawable(resources, tempBitmap)
        }
        // 获取itemView的位置信息
        intArrayOf(0, 0).also { itemView?.getLocationInWindow(it) }.also {
            shrinkRect.apply {
                left = it[0]
                top = it[1]
                right = left + (itemView?.measuredWidth ?: 0)
                bottom = top + (itemView?.measuredHeight ?: 0)
            }
        }
        // 计算展开后的位置
        expandMargin = if (expandMargin < 0) backView.measuredWidth * 0.12f else expandMargin
        expandRect.apply {
            left = expandMargin.toInt()
            top = ((backView.measuredHeight - (backView.measuredWidth - 2 * expandMargin)) / 2).toInt()
            right = (backView.measuredWidth - expandMargin).toInt()
            bottom = backView.measuredHeight - top
        }
        // target
        target = targetView
        target?.layoutParams = ((if (target?.layoutParams == null) generateDefaultLayoutParams() else LayoutParams(layoutParams)) as LayoutParams).apply {
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            topToTop = this@FolderView.id
            bottomToBottom = this@FolderView.id
            width = shrinkRect.width()
            height = shrinkRect.height()
            leftMargin = shrinkRect.left
            topMargin = shrinkRect.top
            rightMargin = backView.measuredWidth - shrinkRect.right
            bottomMargin = backView.measuredHeight - shrinkRect.bottom
        }
        addView(target)
    }

    init {
        id = generateViewId()
        // 获取定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FolderView)
        expandMargin = typedArray.getDimension(R.styleable.FolderView_expandMargin, expandMargin)
        typedArray.recycle()
        // titleGuide
        val titleGuide = Guideline(context).apply {
            id = generateViewId()
            layoutParams = generateDefaultLayoutParams()
        }
        (titleGuide.layoutParams as LayoutParams).apply {
            orientation = LayoutParams.HORIZONTAL
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            guidePercent = 0.1f
        }
        addView(titleGuide)
        // title
        title = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = generateDefaultLayoutParams()
            textSize = 30f
        }
        title?.setTextColor(Color.WHITE)
        (title?.layoutParams as LayoutParams).apply {
            orientation = LayoutParams.HORIZONTAL
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            topToBottom = titleGuide.id
        }
        addView(title)
    }

    /**
     * 展开函数
     * 用于从某一固定位置展开target
     */
    fun expend(during: Long = 300) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = during
            addUpdateListener { animation ->
                val percent = animation.animatedValue as Float
                (target?.layoutParams as LayoutParams).apply {
                    width = ((expandRect.width() - shrinkRect.width()) * percent + shrinkRect.width()).toInt()
                    height = ((expandRect.height() - shrinkRect.height()) * percent + shrinkRect.height()).toInt()
                    leftMargin = (((expandRect.left - shrinkRect.left) * percent) + shrinkRect.left).toInt()
                    topMargin = (((expandRect.top - shrinkRect.top) * percent) + shrinkRect.top).toInt()
                    rightMargin = (this@FolderView.measuredWidth - ((expandRect.right - shrinkRect.right) * percent) - shrinkRect.right).toInt()
                    bottomMargin = (this@FolderView.measuredHeight - ((expandRect.bottom - shrinkRect.bottom) * percent) - shrinkRect.bottom).toInt()
                    Log.i(Tag, "($width, $height)[$leftMargin, $topMargin, $rightMargin, $bottomMargin]")
                }
                target?.requestLayout()
            }
        }.start()
    }
}