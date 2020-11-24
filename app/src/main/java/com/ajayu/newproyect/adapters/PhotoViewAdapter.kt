package com.ajayu.newproyect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ajayu.newproyect.R
import com.ajayu.newproyect.model.PhotoModel
import com.ajayu.newproyect.otros.AnimateView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class PhotoViewAdapter(val context: Context, var clickListener: OnPhotoClickListener, val photoList: List<PhotoModel>)
        : RecyclerView.Adapter<PhotoViewAdapter.PhotoViewHolder>() {

    private var positionSelected = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item,parent,false)
        return PhotoViewHolder(
            context,
            view
        )
    }

    override fun getItemCount(): Int = photoList.size


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo : PhotoModel = photoList[position]
        holder.bind(photo,clickListener,position)

        if (positionSelected == position)holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorVerdeAjayu))
        else holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent))
    }


    inner class PhotoViewHolder (val context: Context, view: View):
        RecyclerView.ViewHolder(view){
        private var mtitle : TextView? = null
        private var mpath : ImageView
        private var mLinear_item: LinearLayout
        private var rAjayu=0
        private var status=true
        val animateView = AnimateView()

        init {
            mtitle = itemView.findViewById(R.id.list_title)
            mpath = itemView.findViewById(R.id.list_path)
            mLinear_item = itemView.findViewById(R.id.linear_item)
            rAjayu = ContextCompat.getColor(context,
                R.color.colorRojoAjayu
            )

        }

        fun bind(photo: PhotoModel, action: OnPhotoClickListener, position: Int){
            mtitle?.text =(position+1).toString()
            mtitle?.setBackgroundColor(0)

            var estado = photo.status
            when(estado){
                true->{
                    action.onPhotoClick(photo,adapterPosition)
                    mtitle?.setBackgroundColor(rAjayu)
                }
                false->mtitle?.setBackgroundColor(0)
            }
            Glide.with(context)
                .load(photo.path)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mpath)

            itemView.setOnClickListener{

                if(position != 0){
                    action.onPhotoClick(photo,adapterPosition)
                    var estado = photo.status
                    when(estado){
                        true->mtitle?.setBackgroundColor(rAjayu)
                        false->mtitle?.setBackgroundColor(0)
                    }
                }
            }
            itemView.setOnLongClickListener{
                animateView.scaleView(itemView,1f,1.25f)
                animateView.scaleView(itemView,1.25f,1f)
                action.onPhotoLongClick(photo,adapterPosition)

                notifyItemChanged(positionSelected)
                positionSelected = adapterPosition
                notifyItemChanged(positionSelected)

                return@setOnLongClickListener true
            }



        }

    }
    interface OnPhotoClickListener{
        fun onPhotoClick(item: PhotoModel, position: Int)
        fun onPhotoLongClick(item: PhotoModel, position: Int)
    }


}