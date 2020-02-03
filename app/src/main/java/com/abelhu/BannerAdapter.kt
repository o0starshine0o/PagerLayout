package com.abelhu

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.item_color.view.*
import java.util.*

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerHolder>() {
    companion object {
        private val Tag = BannerAdapter::class.simpleName
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun getItemViewType(position: Int): Int = position % 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        Log.i(Tag, "onCreateViewHolder with type: $viewType")
        return BannerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: BannerHolder, position: Int) {
        Log.i(Tag, "onBindViewHolder with position: $position")
        holder.onBind(position)
    }

    override fun onViewRecycled(holder: BannerHolder) {
        Log.i(Tag, "onViewRecycled with position: ${holder.itemView.text.text}")
    }

    class BannerHolder(itemView: View, private val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { Toast.makeText(itemView.context, "click item ${itemView.text.text}", Toast.LENGTH_SHORT).show() }
        }

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            itemView.tag = position
            itemView.text.text = "[type:$viewType]$position"
            itemView.text.setBackgroundColor(Color.argb(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255), Random().nextInt(255)))
        }
    }
}