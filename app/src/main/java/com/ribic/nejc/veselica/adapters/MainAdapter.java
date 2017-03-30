package com.ribic.nejc.veselica.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.objects.Party;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainAdapterViewHolder> {
    private ArrayList<Party> mParties;
    private final MainAdapter.MainAdapterOnClickHandler mClickHandler;

    public interface MainAdapterOnClickHandler{
        void partyOnClick(int clickedItemIndex);
    }

    public MainAdapter(ArrayList<Party> mParties, MainAdapterOnClickHandler mClickHandler){
        this.mParties = mParties;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public MainAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new MainAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainAdapterViewHolder holder, int position) {
        holder.mTextViewParty.setText(mParties.get(position).getPlace());
        holder.mTextViewDate.setText(mParties.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mParties.size();
    }

    class MainAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewDate;
        private TextView mTextViewParty;

        private MainAdapterViewHolder(View itemView) {
            super(itemView);
            mTextViewDate = (TextView) itemView.findViewById(R.id.tv_party_date);
            mTextViewParty = (TextView) itemView.findViewById(R.id.tv_party_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.partyOnClick(position);
        }
    }


}
