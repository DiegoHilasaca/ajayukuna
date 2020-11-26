package com.ajayu.newproyect.adapters

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ajayu.newproyect.R
import com.ajayu.newproyect.model.VideoModel


class VideoViewAdapter(
    val context: Context,
    private var clickListener: OnVideoClickListener, private val videosList: List<VideoModel>):
        RecyclerView.Adapter<VideoViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.proyect_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videosList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialize(videosList[position],clickListener)

    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private var title :TextView = itemView.findViewById(R.id.text_view1)
        private var img :ImageView = itemView.findViewById(R.id.image_view1)



        fun initialize(videos: VideoModel, action: OnVideoClickListener){
            title.text= videos.title

            Glide.with(context)
                .load(videos.path)
                .into(img)

            itemView.setOnClickListener() {
                action.onItemClick(videos, adapterPosition,itemView)

            }
            itemView.setOnLongClickListener {
                action.onItemLongClick(videos,adapterPosition,itemView)
                return@setOnLongClickListener true
            }

        }
    }

    interface OnVideoClickListener{
        fun onItemClick(item : VideoModel, position: Int, itemView: View)
        fun onItemLongClick(item: VideoModel, position: Int, itemView: View)
    }

}