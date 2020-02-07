package com.abelhu

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abelhu.lockitem.LockItem
import kotlinx.android.synthetic.main.item_icon.view.*

class FolderAdapter(private val iconList: List<Int> = List(0) { 0 }) : RecyclerView.Adapter<FolderAdapter.FolderHolder>() {

    companion object {
        private val Tag = FolderAdapter::class.simpleName
    }

    override fun getItemCount() = iconList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        Log.i(Tag, "onCreateViewHolder with type: $viewType")
        return FolderHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lock, parent, false))
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        Log.i(Tag, "onBindViewHolder with position: $position")
        holder.initHolder(position, iconList[position % iconList.size])
    }

    override fun onViewRecycled(holder: FolderHolder) {
        super.onViewRecycled(holder)
        Log.i(Tag, "onViewRecycled with position: ${holder.recycleHolder()}")
    }

    class FolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var index = 0

        fun initHolder(position: Int, resourceId: Int) {
            Log.i(Tag, "init holder: $position")
            index = position
            itemView.tag = index
            itemView.setOnClickListener { Toast.makeText(itemView.context, "click item view:$position", Toast.LENGTH_SHORT).show() }
            itemView.nameView.text = position.toString()
            itemView.nameView.setTextColor(Color.WHITE)
            (itemView.iconView as LockItem).apply {
                setImageResource(resourceId)
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