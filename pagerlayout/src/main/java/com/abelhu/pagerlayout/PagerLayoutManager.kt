package com.abelhu.pagerlayout

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.support.v7.widget.RecyclerView
import android.util.SparseIntArray
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import kotlin.math.max


class PagerLayoutManager(private val spanCount: Int = 12, private val spanSizeLookup: (position: Int) -> Int = { _ -> 12 }) : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider {
    /**
     * 记录滚动的距离
     */
    private var scrollDistance = 0
    /**
     * 记录最大的滚动距离
     */
    private var maxScrollDistance = width
    /**
     * 记录每种span对应的高度
     */
    private val spanHeight = SparseIntArray()
    /**
     * 记录所有child的frame，用于判断frame是否位于可显示区域
     */
    var frames = arrayListOf<VisibleRect>()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        // 如果没有children，不进行布局
        if (itemCount <= 0 || state.isPreLayout) return
        // 排除所有已经布置完成的children后剩余的空间
        var remainWidth = width
        var remainHeight = height
        // 记录每一层child的最大高度
        var maxHeight = 0
        // 记录总共需要多少页
        var page = 0
        // 计算每一个child的frame
        frames.clear()
        for (position in 0 until itemCount) {
            // 获取child的宽度（因为是横向滑动，所以宽度是根据span计算而来
            val childWidth = spanSizeLookup.invoke(position) * width / spanCount
            // 获取child的高度，如果有缓存，就直接使用缓存的数据（这里认为span相同的child高度也相同）

            val childHeight = if (spanHeight[childWidth, Int.MIN_VALUE] != Int.MIN_VALUE) {
                spanHeight[childWidth]
            } else {
                // 获取child
                val child = recycler.getViewForPosition(position)
                // 根据spanSizeLookup动态分配child的宽度
                assignSpans(child, position)
                // 测量child的宽高
                measureChildWithMargins(child, 0, 0)
                // 缓存child的高度
                spanHeight.put(childWidth, getDecoratedMeasuredHeight(child))
                // 缓存此child
                removeAndRecycleView(child, recycler)
                // 返回child的高度
                spanHeight[childWidth]
            }
            // 判断水平方向是否有足够的空间给此child使用
            if (remainWidth >= childWidth) {
                // 保存此child的frame
                frames.add(VisibleRect().apply {
                    left = page * width + width - remainWidth.toFloat()
                    top = height - remainHeight.toFloat()
                    right = left + childWidth
                    bottom = top + childHeight
                    this.page = page
                })
                // 计算本层使用的最大高度
                maxHeight = max(maxHeight, childHeight)
                // 更新剩余空间的可用宽度
                remainWidth -= childWidth
            } else {
                // 空间不够，尝试向下申请空间， 首先计算剩余空间的高度
                remainHeight -= maxHeight
                // 剩余空间的高度不足以放下child，开启新的一页，用以放置child
                if (remainHeight < childHeight) {
                    // 剩余空间的高度不足以放下child，开启新的一页，用以放置child
                    page += 1
                    // 重置可用宽高
                    remainHeight = height
                    // 最大滚动区域加一个屏幕宽度
                    maxScrollDistance += width
                }
                // 重置剩余空间的宽度
                remainWidth = width
                // 保存此child的frame
                frames.add(VisibleRect().apply {
                    left = page * width + width - remainWidth.toFloat()
                    top = height - remainHeight.toFloat()
                    right = left + childWidth
                    bottom = top + childHeight
                    this.page = page
                })
                // 计算本层使用的最大高度，因为是新使用的一层，所以从0开始计算
                maxHeight = max(0, childHeight)
                // 更新剩余空间的可用宽度
                remainWidth -= childWidth
            }
        }
        // 填充所有可见的child
        fill(recycler, state)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val distance = if (scrollDistance <= 0 && dx <= 0 || scrollDistance >= maxScrollDistance && dx >= 0) 0
        else if (scrollDistance + dx < 0) -scrollDistance
        else if (scrollDistance + dx > maxScrollDistance) maxScrollDistance - scrollDistance
        else dx
        // 填充所有可见child
        fill(recycler, state)
        // 设置所有children的水平偏移
        offsetChildrenHorizontal(-distance)
        // 记录滚动的距离
        scrollDistance += distance
        // 回收所有不可见的child
        recycleViewsOutOfBounds(recycler)
        // 返回消耗的距离
        return distance
    }

    /**
     * 根据目标位置，计算滚动向量
     * 我们这里完全可以在这里完成对滚动量的计算
     */
    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        val frame = frames[targetPosition]
        // 如果目标frame在可见区域，不需要,否则，计算frame左边距和当前滚动距离的差值
        return if (frame.visible) PointF(0f, 0f) else PointF(frame.left - scrollDistance, 0f)
    }

    /**
     * 获取下一页的第一个item
     */
    fun nextPageItemPosition(): Int {
        for ((i, frame) in frames.withIndex()) {
            if (frame.page == scrollDistance / width + 1) return i
        }
        return 0
    }

    /**
     * 获取上一页的第一个item
     * 这里scrollDistance会比静止情况下下，所以就不在scrollDistance / width - 1了
     */
    fun prePageItemPosition(): Int {
        for ((i, frame) in frames.withIndex()) {
            if (frame.page == scrollDistance / width) return i
        }
        return 0
    }

    /**
     * 为child分配宽度
     */
    private fun assignSpans(view: View, position: Int) {
        val width = spanSizeLookup.invoke(position) * width / spanCount
        view.layoutParams.width = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
    }

    /**
     * 填充所有可见的child
     */
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) return
        // 根据滑动距离，计算显示区域
        val displayRect = Rect().apply {
            left = scrollDistance
            top = 0
            right = left + width
            bottom = height
        }
        // 判断child是否和显示区域有交集，如果有就要显示
        for (i in 0 until itemCount) {
            // frame在不可显示时才需要加入
            if (!frames[i].visible && Rect.intersects(displayRect, frames[i].rect())) {
                // 获取child
                val child = recycler.getViewForPosition(i)
                // 根据spanSizeLookup动态分配child的宽度
                assignSpans(child, i)
                // 测量child的宽高
                measureChildWithMargins(child, 0, 0)
                // 添加进来
                addView(child)
                // 设置child的布局
                frames[i].apply { visible = true }.rect().apply {
                    layoutDecoratedWithMargins(child, left - scrollDistance, top, right - scrollDistance, bottom)
                }
            }
            // 如果frame不在可见区域，设置其不可见的属性
            if (frames[i].visible && !Rect.intersects(displayRect, frames[i].rect())) {
                frames[i].visible = false
            }
        }
    }

    private fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler) {
        // 根据滑动距离，计算显示区域
        val displayRect = Rect().apply {
            left = scrollDistance
            top = 0
            right = left + width
            bottom = height
        }
        // 回收所有不可见的child，注意每次回收都会引起childCount的变化，添加参数r，是为了抵消这个影响
        var r = 0
        for (i in 0 until childCount) {
            getChildAt(i - r)?.also {
                val rect = Rect().apply {
                    left = getDecoratedLeft(it) + scrollDistance
                    top = getDecoratedTop(it)
                    right = getDecoratedRight(it) + scrollDistance
                    bottom = getDecoratedBottom(it)
                }
                if (!Rect.intersects(displayRect, rect)) {
                    removeAndRecycleView(it, recycler)
                    r++
                }
            }
        }
    }

    /**
     * 因为Rect是final类型的，这里只能继承RectF
     * 额外添加page，用于记录frame位于哪一页
     */
    class VisibleRect : RectF() {
        var visible = false
        var page = 0
        fun rect() = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
}