package com.fei.pavement

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SeeAdapter(val list: ArrayList<See>) :
    RecyclerView.Adapter<SeeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.member_text_jing)
        val position: TextView = view.findViewById(R.id.member_text_wei)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member ,parent, false)

        val viewHolder= ViewHolder(view)

        return viewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = list[position]
        holder.name.text= friend.jingdu.toString()
        holder.position.text= friend.weidu.toString()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}