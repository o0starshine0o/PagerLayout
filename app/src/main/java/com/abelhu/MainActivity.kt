package com.abelhu

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.abelhu.guide.Guide
import com.abelhu.pagerlayout.PagerLayoutManager
import com.abelhu.pagerlayout.PagerSnapHelper
import com.abelhu.smoothlayout.SmoothLinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val Tag = MainActivity::class.simpleName
    }

    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBanner()
        initIcons()
        initGuide()
    }

    private fun initBanner() {
        banner.adapter = BannerAdapter()
        // 离屏缓存，并不会放入回收池，在反向滑动的时候保证item**不会**经过onBindViewHolder过程直接显示出来
        banner.setItemViewCacheSize(0)
        // 设置平滑滚动的LinearLayoutManager
        banner.layoutManager = SmoothLinearLayoutManager(baseContext)
        // 设置PagerSnap保证滑动对齐
        android.support.v7.widget.PagerSnapHelper().attachToRecyclerView(banner)
        // 开启自动翻页功能
        flipBanner()
    }

    private fun initIcons() {
        icons.adapter = SlideAdapter(baseContext)
        // 离屏缓存，并不会放入回收池，在反向滑动的时候保证item**不会**经过onBindViewHolder过程直接显示出来
        icons.setItemViewCacheSize(0)
        // 根据每屏最多显示的item数量，设置其缓存阈值
        icons.recycledViewPool.setMaxRecycledViews(SlideAdapter.TYPE_6, 20)
        icons.recycledViewPool.setMaxRecycledViews(SlideAdapter.TYPE_4, 20)
        icons.recycledViewPool.setMaxRecycledViews(SlideAdapter.TYPE_3, 4)
        icons.recycledViewPool.setMaxRecycledViews(SlideAdapter.TYPE_2, 4)
        icons.recycledViewPool.setMaxRecycledViews(SlideAdapter.TYPE_1, 4)
        icons.layoutManager = PagerLayoutManager(12) {
            when (it) {
                37 -> SlideAdapter.TYPE_1
                in 0..1 -> SlideAdapter.TYPE_2
                in 18..20 -> SlideAdapter.TYPE_3
                in 46..51 -> SlideAdapter.TYPE_6
                in 56..58 -> SlideAdapter.TYPE_3
                else -> SlideAdapter.TYPE_4
            }
        }
        // 设置PagerSnap保证滑动对齐
        PagerSnapHelper().attachToRecyclerView(icons)
        // 设置recyclerView的indicator
        dotIndicator.attachToRecyclerView(icons)
        lineIndicator.attachToRecyclerView(icons)
        lineIndicator2.attachToRecyclerView(icons)
        drawIndicator.attachToRecyclerView(icons)
    }

    private fun initGuide() {
        icons.post {
            val guide = Guide(baseContext).addWindow(icons.findViewHolderForAdapterPosition(0).itemView)
            guide.setOnClickListener {
                Toast.makeText(baseContext, "click guide", Toast.LENGTH_SHORT).show()
                guide.dismiss()
            }
            (window.decorView as ViewGroup).addView(guide)
        }
    }

    private fun flipBanner() {
        Handler(Looper.getMainLooper()).postDelayed({
            position++
            banner.smoothScrollToPosition(position)
            Log.i(Tag, "[${Thread.currentThread().name}]smoothScrollToPosition: $position")
            flipBanner()
        }, 3000)
    }
}
