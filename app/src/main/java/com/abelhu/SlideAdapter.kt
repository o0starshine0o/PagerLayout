package com.abelhu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import kotlinx.android.synthetic.main.item_icon.view.*

class SlideAdapter(context: Context) : RecyclerView.Adapter<SlideAdapter.SlideHolder>() {
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
        return SlideHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lock, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            in 56..58 -> TYPE_3
            in 46..51 -> TYPE_6
            37 -> TYPE_1
            0, 1 -> TYPE_2
            18, 19, 20 -> TYPE_3
            else -> TYPE_4
        }
    }

    override fun getItemCount(): Int {
        return 200
    }

    override fun onBindViewHolder(holder: SlideHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder with position: $position")
        holder.initHolder(position, iconList[position % iconList.size])
    }

    override fun onViewRecycled(holder: SlideHolder) {
        super.onViewRecycled(holder)
        Log.i(TAG, "onViewRecycled with position: ${holder.recycleHolder()}")
    }

    class SlideHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GridDrawable.Generate<Int> {
        private var index = 0

        override fun generateResource(obj: Int) = AppCompatResources.getDrawable(itemView.context, obj)

        fun initHolder(position: Int, resourceId: Int) {
            Log.i(TAG, "init holder: $position")
            index = position
            itemView.tag = index
            itemView.setOnClickListener {
                when (index) {
                    3 -> (itemView.rootView as ViewGroup).addView(
                        FolderView(
                            itemView.context,
                            itemView.rootView,
                            8f,
                            itemView,
                            View(itemView.context).apply { background = ColorDrawable(Color.RED) })
                    )
                    else -> Toast.makeText(itemView.context, "click item view:$position", Toast.LENGTH_SHORT).show()
                }
            }
            itemView.nameView.text = position.toString()
            (itemView.iconView as LockItem).apply {
                when (index) {
                    3 -> setImageDrawable(GridDrawable(GridDrawable.TWO, 10, this@SlideHolder).addRes(*Array(10) { resourceId }))
                    5 -> setImageDrawable(GridDrawable(GridDrawable.THREE, 10, this@SlideHolder).addRes(*Array(10) { resourceId }))
                    7 -> setImageDrawable(GridDrawable(GridDrawable.FOUR, 10, this@SlideHolder).addRes(*Array(10) { resourceId }))
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

        fun recycleHolder() = index

    }
}