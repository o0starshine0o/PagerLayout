package com.abelhu.pagerlayout

import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.util.Log
import android.view.View
import kotlin.math.abs

class PagerSnapHelper : SnapHelper() {
    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {
        val x = if (layoutManager.canScrollVertically()) 0
        else distanceToStart(targetView, getOrientationHelper(layoutManager))
        val y = if (layoutManager.canScrollHorizontally()) 0
        else distanceToStart(targetView, getOrientationHelper(layoutManager))
        return intArrayOf(x, y)
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        if (layoutManager is PagerLayoutManager) {
            return if (velocityX > 0) layoutManager.nextPageItemPosition() else layoutManager.prePageItemPosition()
        }
        return 0
    }

    /**
     * 寻找要对其的view
     * 0、计算所有child到中心线的距离，找出最小的那个
     * 1、获取这个child位于哪一个page
     * 2、找到这个page的第一个view并返回
     */
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        var minDistance = Int.MAX_VALUE
        var minChildIndex = Int.MAX_VALUE
        val helper = getOrientationHelper(layoutManager)
        val center = (helper?.totalSpace ?: 0) / 2
        // 计算所有child到中心的距离，找出最小的那个
        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i)
            val start = helper?.getDecoratedStart(child) ?: 0
            val end = helper?.getDecoratedEnd(child) ?: 0
            val distance = abs((start + end) / 2 - center)
            if (distance < minDistance) {
                minDistance = distance
                minChildIndex = i
            }
            Log.i("PagerSnapHelper", "findSnapView[$i] with start:$start")
        }
        // 获取这个child位于哪一个page
        var page = 0
        layoutManager.getChildAt(minChildIndex)?.apply {
            val position = layoutManager.getPosition(this)
            page = (layoutManager as PagerLayoutManager).frames[position].page
        }
        // 找到这个page的第一个view并返回
        for ((i, frame) in (layoutManager as PagerLayoutManager).frames.withIndex()) {
            if (frame.page == page) return layoutManager.findViewByPosition(i)
        }
        return null
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper?): Int {
        return helper?.getDecoratedStart(targetView) ?: 0 - (helper?.startAfterPadding ?: 0)
    }

    private fun getOrientationHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        return when {
            layoutManager.canScrollVertically() -> getVerticalHelper(layoutManager)
            layoutManager.canScrollHorizontally() -> getHorizontalHelper(layoutManager)
            else -> null
        }
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (mVerticalHelper?.layoutManager != layoutManager) mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        return mVerticalHelper
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (mHorizontalHelper?.layoutManager !== layoutManager) mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        return mHorizontalHelper
    }
}