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
import com.ajayu.newproyect.model.AnimationModel


class AnimationsViewAdapter(
    val context: Context,
    private var clickListener: OnProyectClickListener, private val animationsList: List<AnimationModel>):
        RecyclerView.Adapter<AnimationsViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.proyect_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return animationsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialize(animationsList[position],clickListener)

    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private var title :TextView = itemView.findViewById(R.id.text_view1)
        private var img :ImageView = itemView.findViewById(R.id.image_view1)



        fun initialize(animations: AnimationModel, action: OnProyectClickListener){
            title.text= animations.title

            Glide.with(context)
                .load(animations.imagePath)
                .into(img)

            itemView.setOnClickListener {
                action.onItemClick(animations, adapterPosition,itemView)

            }
            itemView.setOnLongClickListener {
                action.onItemLongClick(animations,adapterPosition,itemView)
                return@setOnLongClickListener true
            }

        }
    }

    interface OnProyectClickListener{
        fun onItemClick(item : AnimationModel, position: Int, itemView: View)
        fun onItemLongClick(item: AnimationModel, position: Int, itemView: View)
    }

}