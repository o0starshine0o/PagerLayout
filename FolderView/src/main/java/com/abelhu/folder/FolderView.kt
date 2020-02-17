package com.abelhu.folder

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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
import android.view.ViewGroup
import android.widget.TextView


class FolderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle) {
    companion object {
        private val Tag = FolderView::class.java.simpleName
    }

    var title: TextView? = null
    /**
     * 起始的itemView位置：left, top, right, bottom, width, height
     */
    private val shrinkRect = Rect()
    /**
     * 展开的itemView位置：left, top, right, bottom, width, height
     */
    private val expandRect = Rect()
    /**
     * 缩放的最小值
     */
    private var targetScale = 0f

    /**
     * 最终需要展示的view
     */
    private var target: View? = null
    /**
     * target的边距
     */
    private var expandMargin = -1f

    /**
     * @param context 上下文
     * @param backView 用于创建模糊背景
     * @param blurRadius 模糊背景的模糊半径
     * @param itemView 用于计算FolderView缩小后的位置
     * @param targetView 用于替换itemView， 最终展示在FolderView中的view
     */
    constructor(context: Context, backView: View, itemView: View? = null, targetView: View? = null, blurRadius: Float = 4f) : this(context) {
        // 创建模糊背景的background
        backView.apply {
            val start = System.currentTimeMillis()
            // 获取背景
            val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            draw(canvas)
            canvas.drawColor(Color.argb(128, 0, 0, 0))
            // 使用缩小的bitmap进行高斯模糊，加快渲染速度
            val tempBitmap = Bitmap.createScaledBitmap(bitmap, measuredWidth.shr(4), measuredHeight.shr(4), false)
            // 获取渲染脚本
            val render = RenderScript.create(context)
            val blur = ScriptIntrinsicBlur.create(render, Element.U8_4(render))
            blur.setRadius(blurRadius)
            // 设置入参和回参
            val tempIn = Allocation.createFromBitmap(render, tempBitmap)
            val tempOut = Allocation.createFromBitmap(render, tempBitmap)
            blur.setInput(tempIn)
            blur.forEach(tempOut)
            // 设置最终图片
            tempOut.copyTo(tempBitmap)
            // 回收渲染脚本
            render.destroy()
            // 设置背景
            this@FolderView.background = BitmapDrawable(resources, tempBitmap)
            this@FolderView.background.alpha = 0
            Log.i(Tag, "[${Thread.currentThread().name}] init background bitmap using time : ${System.currentTimeMillis() - start}")
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
        // 计算缩放值
        targetScale = shrinkRect.width().toFloat() / expandRect.width()
        // target
        target = targetView
        target?.layoutParams = generateDefaultLayoutParams().apply {
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            topToTop = this@FolderView.id
            bottomToBottom = this@FolderView.id
            width = expandRect.width()
            height = expandRect.height()
        }
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
        Log.i(Tag, "add title guide")
        // title
        title = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = generateDefaultLayoutParams()
            textSize = 30f
        }
        title?.setTextColor(Color.WHITE)
        title?.alpha = 0f
        (title?.layoutParams as LayoutParams).apply {
            orientation = LayoutParams.HORIZONTAL
            startToStart = this@FolderView.id
            endToEnd = this@FolderView.id
            topToBottom = titleGuide.id
        }
        addView(title)
        Log.i(Tag, "add title")
    }

    /**
     * 展开函数
     * 用于从某一固定位置展开target
     */
    fun expend(during: Long = 200) {
        this.post {
            this@FolderView.addView(target)
            ValueAnimator.ofFloat(targetScale, 1f).setDuration(during).apply { addUpdateListener { animation -> updateTarget(animation) } }.start()
        }

    }

    /**
     * 缩小函数
     * 把target缩小到itemView
     */
    fun shrink(during: Long = 200) {
        this.post {
            ValueAnimator.ofFloat(1f, targetScale).setDuration(during).apply {
                addUpdateListener { animation -> updateTarget(animation) }
                addUpdateListener { animation ->
                    if ((animation.animatedValue as Float) <= targetScale + 0.0001 && parent != null) {
                        (parent as ViewGroup).apply {
                            removeView(this@FolderView)
                            postInvalidate()
                        }
                    }
                }
            }.start()
        }
    }

    private fun updateTarget(animator: ValueAnimator) {
        val percent = animator.animatedValue as Float
        // 修改缩放值
        target?.scaleX = percent
        target?.scaleY = percent
        // 修改移动值
        target?.translationX = (shrinkRect.exactCenterX() - expandRect.exactCenterX()) * (percent - 1) / (targetScale - 1)
        target?.translationY = (shrinkRect.exactCenterY() - expandRect.exactCenterY()) * (percent - 1) / (targetScale - 1)
        // 修改背景的透明度
        background?.alpha = 255 - (255 * (percent - 1) / (targetScale - 1)).toInt()
        // 修改title的透明度
        title?.alpha = 1 - (percent - 1) / (targetScale - 1)
    }
}