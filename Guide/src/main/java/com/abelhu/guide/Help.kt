package com.abelhu.guide

import android.graphics.Rect
import android.support.annotation.IntDef
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT


/**
 * @param help 用于帮助的view
 * @param offset help距离透明区域有多远
 * @param position help位于透明区域什么方位
 * @param area 透明区域
 */
class Help(val help: View, @POSITION position: Int, offset: Int = 0, area: Rect) {
    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(LEFT, TOP, RIGHT, BOTTOM)
        annotation class POSITION

        const val LEFT = 1
        const val TOP = 2
        const val RIGHT = 3
        const val BOTTOM = 4
    }

    /**
     * 主轴
     */
    var mainGuideLine = Guideline(help.context)
    /**
     * 交叉轴
     */
    var crossGuideLine = Guideline(help.context)

    init {
        // 初始化主轴和交叉轴
        mainGuideLine.id = generateViewId()
        val mainGuideLayout = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        crossGuideLine.id = generateViewId()
        val crossGuideLayout = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        // 根据position设置mainGuideLine和crossGuideLine的约束条件
        when (position) {
            LEFT -> {
                mainGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                mainGuideLayout.guideBegin = area.left + offset
                mainGuideLayout.topToTop = R.id.guide
                mainGuideLayout.bottomToBottom = R.id.guide
                crossGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                crossGuideLayout.guideBegin = area.height() / 2 + area.top
                crossGuideLayout.startToStart = R.id.guide
                crossGuideLayout.endToEnd = R.id.guide
            }
            TOP -> {
                mainGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                mainGuideLayout.guideBegin = area.top + offset
                mainGuideLayout.startToStart = R.id.guide
                mainGuideLayout.endToEnd = R.id.guide
                crossGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                crossGuideLayout.guideBegin = area.width() / 2 + area.left
                crossGuideLayout.topToTop = R.id.guide
                crossGuideLayout.bottomToBottom = R.id.guide
            }
            RIGHT -> {
                mainGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                mainGuideLayout.guideBegin = area.right + offset
                mainGuideLayout.topToTop = R.id.guide
                mainGuideLayout.bottomToBottom = R.id.guide
                crossGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                crossGuideLayout.guideBegin = area.height() / 2 + area.top
                crossGuideLayout.startToStart = R.id.guide
                crossGuideLayout.endToEnd = R.id.guide
            }
            BOTTOM -> {
                mainGuideLayout.orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                mainGuideLayout.guideBegin = area.bottom + offset
                mainGuideLayout.startToStart = R.id.guide
                mainGuideLayout.endToEnd = R.id.guide
                crossGuideLayout.orientation = ConstraintLayout.LayoutParams.VERTICAL
                crossGuideLayout.guideBegin = area.width() / 2 + area.left
                crossGuideLayout.topToTop = R.id.guide
                crossGuideLayout.bottomToBottom = R.id.guide
            }
        }
        mainGuideLine.layoutParams = mainGuideLayout
        crossGuideLine.layoutParams = crossGuideLayout
        // 设置help的位置
        val helpLayout = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            when (position) {
                LEFT -> {
                    endToStart = mainGuideLine.id
                    topToTop = crossGuideLine.id
                    bottomToBottom = crossGuideLine.id
                }
                TOP -> {
                    bottomToTop = mainGuideLine.id
                    startToStart = crossGuideLine.id
                    endToEnd = crossGuideLine.id
                }
                RIGHT -> {
                    startToEnd = mainGuideLine.id
                    topToTop = crossGuideLine.id
                    bottomToBottom = crossGuideLine.id
                }
                BOTTOM -> {
                    topToBottom = mainGuideLine.id
                    startToStart = crossGuideLine.id
                    endToEnd = crossGuideLine.id
                }
            }
        }
        help.layoutParams = helpLayout
    }
}