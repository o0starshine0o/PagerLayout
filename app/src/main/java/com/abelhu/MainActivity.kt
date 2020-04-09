package com.abelhu

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.abelhu.guide.Guide
import com.abelhu.guide.Help
import com.abelhu.guide.Help.Companion.BOTTOM
import com.abelhu.guide.Help.Companion.LEFT
import com.abelhu.guide.Help.Companion.RIGHT
import com.abelhu.guide.Help.Companion.TOP
import com.abelhu.pagerlayout.PagerLayoutManager
import com.abelhu.pagerlayout.PagerSnapHelper
import com.abelhu.smoothlayout.SmoothLinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_skip.view.*
import kotlin.math.max

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
        showGuide()
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
        icons.adapter = SlideAdapter(baseContext, icons.recycledViewPool)
        // 离屏缓存，并不会放入回收池，在反向滑动的时候保证item**不会**经过onBindViewHolder过程直接显示出来
        icons.setItemViewCacheSize(0)
        // 根据每屏最多显示的item数量，设置其缓存阈值
        icons.recycledViewPool.setMaxRecycledViews(0, 30)
        // 设置不同的item占据的不同空间
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

    private fun showGuide(@Help.Companion.POSITION position: Int = BOTTOM) {
        icons.post {
            val itemIndex = when (position) {
                LEFT -> 1
                TOP -> 3
                RIGHT -> 0
                else -> 4
            }
            val v0 = icons.findViewHolderForAdapterPosition(itemIndex).itemView
            val v1 = icons.findViewHolderForAdapterPosition(max(0, itemIndex - 1)).itemView
            v1.isDrawingCacheEnabled = true
            v1.buildDrawingCache()
            val guide = when (position) {
                // 多窗口演示
                LEFT -> Guide(baseContext).apply {
                    addWindow(null, v0).addHelp(this, R.layout.item_guide_left, LEFT, 100)
                    addWindow(null, v1).addHelp(this, R.layout.item_guide_bottom, BOTTOM, -100)
                }
                // 绘制bitmap演示
                TOP -> Guide(baseContext).apply {
                    val draw = BitmapFactory.decodeResource(context.resources, R.mipmap.mask_guide)
                    addWindow(draw, true, v0).addHelp(this, R.layout.item_guide_top, TOP, 100)
                }
                // 多help演示
                RIGHT -> Guide(baseContext).apply {
                    addWindow(null, v0)
                        .addHelp(this, R.layout.item_guide_right, RIGHT, -100)
                        .addHelp(this, R.layout.item_guide_bottom, BOTTOM, -100)
                }
                // 合并多窗口演示
                else -> Guide(baseContext).apply { addWindow(null, v0, v1).addHelp(this, R.layout.item_guide_bottom, BOTTOM, -100) }
            }
            guide.addSkip(R.layout.item_skip)
            guide.skip.setOnClickListener {
                Toast.makeText(baseContext, "click skip", Toast.LENGTH_SHORT).show()
                guide.dismiss()
                when (position) {
                    BOTTOM -> showGuide(LEFT)
                    LEFT -> showGuide(TOP)
                    TOP -> showGuide(RIGHT)
                }
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
