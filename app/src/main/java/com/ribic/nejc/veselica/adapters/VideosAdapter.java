package com.ribic.nejc.veselica.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ribicnejc.party.R;
import com.ribic.nejc.veselica.objects.Video;

import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private ArrayList<Video> mVideos;
    private final VideosAdapter.TrailersAdapterOnClickHandler mClickHandler;

    public interface TrailersAdapterOnClickHandler {
        void trailersOnClick(int clickedItemIndex);
    }


    public VideosAdapter(ArrayList<Video> mTrailers, TrailersAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
        this.mVideos = mTrailers;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String imgUrl = mVideos.get(position).getImgUrl();
        Glide.with(holder.itemView.getContext()).load(imgUrl).into(holder.mVideoImage);
        holder.mVideoTitle.setText(mVideos.get(position).getTitle());
        setAnimation(holder.itemView, position);
    }



    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mVideoTitle;
        ImageView mVideoImage;

        private VideoViewHolder(View itemView) {
            super(itemView);
            mVideoTitle = (TextView) itemView.findViewById(R.id.text_view_video_title_item);
            mVideoImage = (ImageView) itemView.findViewById(R.id.image_view_item_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.trailersOnClick(clickedPosition);
        }
    }



    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_up_anim);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }
}
