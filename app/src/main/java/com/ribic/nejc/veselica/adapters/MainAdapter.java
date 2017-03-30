package com.ribic.nejc.veselica.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ribic.nejc.party.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainAdapterViewHolder> {


    @Override
    public MainAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MainAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MainAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTextViewDate;

        public MainAdapterViewHolder(View itemView) {
            super(itemView);
            mTextViewDate = (TextView) itemView.findViewById(R.id.tv_main_item_date);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
        }
    }




}
