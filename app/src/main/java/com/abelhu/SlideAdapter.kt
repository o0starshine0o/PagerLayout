package com.abelhu

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abelhu.folder.FolderView
import com.abelhu.lockitem.LockItem
import com.abelhu.nine.GridDrawable
import com.abelhu.pagerlayout.PagerLayoutManager
import com.abelhu.pagerlayout.PagerSnapHelper
import kotlinx.android.synthetic.main.folder.view.*
import kotlinx.android.synthetic.main.item_icon.view.*

class SlideAdapter(context: Context, private val recycledViewPool: RecyclerView.RecycledViewPool) : RecyclerView.Adapter<SlideAdapter.SlideHolder>() {
    private var iconList: MutableList<Int>

    companion object {
        private val TAG = SlideAdapter::class.simpleName
        const val TYPE_6 = 2
        const val TYPE_4 = 3
        const val TYPE_3 = 4
        const val TYPE_2 = 6
        const val TYPE_1 = 12
    }

    init {
        val typedArray = context.resources.obtainTypedArray(R.array.icon_list)
        iconList = MutableList(typedArray.length()) { i -> typedArray.getResourceId(i, -1) }
        typedArray.recycle()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideHolder {
        Log.i(TAG, "onCreateViewHolder with type: $viewType")
        return SlideHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lock, parent, false), recycledViewPool)
    }

    override fun getItemCount() = 200

    override fun onBindViewHolder(holder: SlideHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder with position: $position")
        holder.initHolder(position, iconList[position % iconList.size])
    }

    override fun onViewRecycled(holder: SlideHolder) {
        super.onViewRecycled(holder)
        Log.i(TAG, "onViewRecycled with position: ${holder.recycleHolder()}")
    }

    class SlideHolder(itemView: View, private val recycledViewPool: RecyclerView.RecycledViewPool) : RecyclerView.ViewHolder(itemView),
        GridDrawable.Generate<Int> {
        private var index = 0

        override fun generateResource(obj: Int) = AppCompatResources.getDrawable(itemView.context, obj)

        fun recycleHolder() = index

        fun initHolder(position: Int, resourceId: Int) {
            Log.i(TAG, "init holder: $position")
            index = position
            itemView.tag = index
            itemView.setOnClickListener {
                when (index) {
                    3 -> createFolder(resourceId, GridDrawable.TWO, index)
                    5 -> createFolder(resourceId, GridDrawable.THREE, index)
                    7 -> createFolder(resourceId, GridDrawable.FOUR, index)
                    else -> Toast.makeText(itemView.context, "click item view:$position", Toast.LENGTH_SHORT).show()
                }
            }
            itemView.nameView.text = position.toString()
            (itemView.iconView as LockItem).apply {
                when (index) {
                    3 -> setImageDrawable(GridDrawable(GridDrawable.TWO, 10, this@SlideHolder).addRes(*Array(100) { resourceId }))
                    5 -> setImageDrawable(GridDrawable(GridDrawable.THREE, 10, this@SlideHolder).addRes(*Array(100) { resourceId }))
                    7 -> setImageDrawable(GridDrawable(GridDrawable.FOUR, 10, this@SlideHolder).addRes(*Array(100) { resourceId }))
                    else -> setImageResource(resourceId)
                }
                if (position % 2 == 0) {
                    showLock = true
                    if (position % 4 == 0) {
                        showNumber = true
                        dotNumber = position
                    } else {
                        showNumber = false
                        dotNumber = position
                    }
                } else {
                    showNumber = false
                    dotNumber = -1
                    showLock = false
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun createFolder(resourceId: Int, grid: Int, index: Int) {
            // 设置文件夹的RecycleView
            val targetView = LayoutInflater.from(itemView.context).inflate(R.layout.folder, itemView.rootView as ViewGroup, false)
            targetView.icons.adapter = FolderAdapter(List(20) { resourceId }, grid)
            targetView.icons.layoutManager = PagerLayoutManager { 12 / grid }
            targetView.icons.setItemViewCacheSize(0)
            // 因为item基本都是一样的，这里直接共用recycledViewPool
            targetView.icons.recycledViewPool = recycledViewPool
            targetView.icons.recycledViewPool.setMaxRecycledViews(targetView.icons.adapter.getItemViewType(0), grid * grid)
            // 设置PagerSnap保证滑动对齐
            PagerSnapHelper().attachToRecyclerView(targetView.icons)
            // 设置recyclerView的indicator
            targetView.dotIndicator.attachToRecyclerView(targetView.icons)
            val folder = FolderView(itemView.context, itemView.rootView, itemView.iconView, targetView)
            folder.closeListener = {
                // 文件夹关闭，回收所有item
                val clazz = targetView.icons::class.java
                val method = clazz.getDeclaredMethod("removeAndRecycleViews")
                method.isAccessible = true
                method.invoke(targetView.icons)
            }
            folder.title?.text = "Folder View $index"
            folder.setOnClickListener { folder.shrink() }
            (itemView.rootView as ViewGroup).addView(folder)
            folder.expend()
        }

    }
}