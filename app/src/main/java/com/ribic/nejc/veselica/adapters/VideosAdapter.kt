package com.ribic.nejc.veselica.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.ribicnejc.party.R
import com.ribic.nejc.veselica.objects.Video

import java.util.ArrayList

class VideosAdapter(private val mVideos: ArrayList<Video>, private val mClickHandler: VideosAdapter.TrailersAdapterOnClickHandler) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {

    interface TrailersAdapterOnClickHandler {
        fun trailersOnClick(clickedItemIndex: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val imgUrl = mVideos[position].imgUrl
        Glide.with(holder.itemView.context).load(imgUrl).into(holder.mVideoImage)
        holder.mVideoTitle.text = mVideos[position].title
        setAnimation(holder.itemView, position)
    }


    override fun getItemCount(): Int {
        return mVideos.size
    }

    inner class VideoViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var mVideoTitle: TextView
        var mVideoImage: ImageView

        init {
            mVideoTitle = itemView.findViewById(R.id.text_view_video_title_item) as TextView
            mVideoImage = itemView.findViewById(R.id.image_view_item_thumbnail) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val clickedPosition = adapterPosition
            mClickHandler.trailersOnClick(clickedPosition)
        }
    }


    private var lastPosition = -1

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_up_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }

    }
}
