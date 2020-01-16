package com.abelhu

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_icon.view.*
import java.util.*

class SlideAdapter : RecyclerView.Adapter<SlideAdapter.SlideHolder>() {
    companion object {
        private val TAG = SlideAdapter::class.simpleName
        const val TYPE_6 = 2
        const val TYPE_4 = 3
        const val TYPE_3 = 4
        const val TYPE_2 = 6
        const val TYPE_1 = 12
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideHolder {
        Log.i(TAG, "onCreateViewHolder with type: $viewType")
        return SlideHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false))
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
        holder.initHolder(position)
    }

    override fun onViewRecycled(holder: SlideHolder) {
        super.onViewRecycled(holder)
        Log.i(TAG, "onViewRecycled with position: ${holder.recycleHolder()}")
    }

    class SlideHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var index = 0
        fun initHolder(position: Int) {
            index = position
            itemView.iconView.setBackgroundColor(Color.rgb(Random().nextInt(256), Random().nextInt(256), Random().nextInt(256)))
            itemView.nameView.text = position.toString()
            itemView.tag = position
            Log.i(TAG, "init holder: $position")
        }

        fun recycleHolder() = index
    }
}