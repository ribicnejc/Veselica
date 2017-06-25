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

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.Constants;
import com.ribic.nejc.veselica.utils.PrefUtils;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder> {
    private ArrayList<Party> mParties;
    private final SearchAdapter.SearchAdapterOnClickHandler mClickHandler;

    public interface SearchAdapterOnClickHandler {
        void partyOnClick(int clickedItemIndex);
    }

    public SearchAdapter(ArrayList<Party> mParties, SearchAdapterOnClickHandler mClickHandler) {
        this.mParties = mParties;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public SearchAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new SearchAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchAdapterViewHolder holder, int position) {
        holder.mTextViewParty.setText(mParties.get(position).getPlace());
        holder.mTextViewDate.setText(mParties.get(position).getDate());
        if (mParties.get(position) != null) {
            Party party = mParties.get(position);
            if (PrefUtils.exitsts(party.toString(), holder.itemView.getContext()))
                holder.mImageViewFavorite.setImageResource(R.drawable.icon_stared);
            else
                holder.mImageViewFavorite.setImageResource(R.drawable.icon_unstared);
        }
        final Party party = mParties.get(position);
        holder.mImageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.fragmentContentChanged = !Constants.fragmentContentChanged;
                if (PrefUtils.exitsts(party.toString(), holder.itemView.getContext())) {
                    PrefUtils.remove(party.toString(), holder.itemView.getContext());
                    holder.mImageViewFavorite.setImageResource(R.drawable.icon_unstared);
                } else {
                    PrefUtils.saveName(party.toString(), holder.itemView.getContext());
                    holder.mImageViewFavorite.setImageResource(R.drawable.icon_stared);
                }
            }
        });
        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return mParties.size();
    }

    class SearchAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewDate;
        private TextView mTextViewParty;
        private ImageView mImageViewFavorite;

        private SearchAdapterViewHolder(View itemView) {
            super(itemView);
            mTextViewDate = (TextView) itemView.findViewById(R.id.tv_party_date);
            mTextViewParty = (TextView) itemView.findViewById(R.id.tv_party_name);
            mImageViewFavorite = (ImageView) itemView.findViewById(R.id.image_view_icon_favorite);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.partyOnClick(position);
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
