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

import com.ribicnejc.party.R
import com.ribic.nejc.veselica.objects.Party
import com.ribic.nejc.veselica.utils.Constants
import com.ribic.nejc.veselica.utils.PrefUtils

import java.util.ArrayList

class SearchAdapter(private val mParties: ArrayList<Party>, private val mClickHandler: SearchAdapter.SearchAdapterOnClickHandler) : RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder>() {

    interface SearchAdapterOnClickHandler {
        fun partyOnClick(clickedItemIndex: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapterViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return SearchAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchAdapterViewHolder, position: Int) {
        holder.mTextViewParty.text = mParties[position].place
        holder.mTextViewDate.text = mParties[position].date
        if (mParties[position] != null) {
            val party = mParties[position]
            if (PrefUtils.exitsts(party.toString(), holder.itemView.context))
                holder.mImageViewFavorite.setImageResource(R.drawable.icon_stared)
            else
                holder.mImageViewFavorite.setImageResource(R.drawable.icon_unstared)
        }
        val party = mParties[position]
        holder.mImageViewFavorite.setOnClickListener {
            Constants.fragmentContentChanged = !Constants.fragmentContentChanged
            if (PrefUtils.exitsts(party.toString(), holder.itemView.context)) {
                PrefUtils.remove(party.toString(), holder.itemView.context)
                holder.mImageViewFavorite.setImageResource(R.drawable.icon_unstared)
            } else {
                PrefUtils.saveName(party.toString(), holder.itemView.context)
                holder.mImageViewFavorite.setImageResource(R.drawable.icon_stared)
            }
        }
        setAnimation(holder.itemView, position)

    }

    override fun getItemCount(): Int {
        return mParties.size
    }

    inner class SearchAdapterViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val mTextViewDate: TextView
        val mTextViewParty: TextView
        val mImageViewFavorite: ImageView

        init {
            mTextViewDate = itemView.findViewById(R.id.tv_party_date) as TextView
            mTextViewParty = itemView.findViewById(R.id.tv_party_name) as TextView
            mImageViewFavorite = itemView.findViewById(R.id.image_view_icon_favorite) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            mClickHandler.partyOnClick(position)
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
