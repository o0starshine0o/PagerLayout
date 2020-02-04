package com.abelhu

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abelhu.lockitem.LockItem
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

    class SlideHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var index = 0
        fun initHolder(position: Int, resourceId: Int) {
            index = position
            val lockItem = itemView.iconView as LockItem
            itemView.nameView.text = position.toString()
            lockItem.setImageResource(resourceId)
            if (position % 2 == 0) {
                lockItem.showLock = true
                if (position % 4 == 0) {
                    lockItem.showNumber = true
                    lockItem.dotNumber = position
                } else {
                    lockItem.showNumber = false
                    lockItem.dotNumber = position
                }
            } else {
                lockItem.showNumber = false
                lockItem.dotNumber = -1
                lockItem.showLock = false
            }
            itemView.tag = position
            Log.i(TAG, "init holder: $position")
            itemView.setOnClickListener { Toast.makeText(itemView.context, "click item view:$position", Toast.LENGTH_SHORT).show() }
        }

        fun recycleHolder() = index
    }
}