package com.abelhu.smoothlayout

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

class ForbidTouchMoveRecycleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return when (e.actionMasked) {
            MotionEvent.ACTION_MOVE -> false
            else -> super.onInterceptTouchEvent(e)
        }
    }
}