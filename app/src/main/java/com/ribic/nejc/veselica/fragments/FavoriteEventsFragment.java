package com.ribic.nejc.veselica.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.MainAdapter;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.PrefUtils;

import java.util.ArrayList;
import java.util.Set;

public class FavoriteEventsFragment extends Fragment implements MainAdapter.MainAdapterOnClickHandler{

    private static final String ARG_SECTION_NUMBER = "section_number";
    public RecyclerView mRecyclerView;
    public MainAdapter mAdapter;
    public ArrayList<Party> mParties;
    public FavoriteEventsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public FavoriteEventsFragment newInstance(int sectionNumber) {
        FavoriteEventsFragment fragment = new FavoriteEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_favorites);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        readData();
        return rootView;
    }

    public void readData(){
        ArrayList<Party> elts = new ArrayList<>();
        Set<String> tmp = PrefUtils.getNames(getContext());
        for (String elt : tmp){
            String[] tmp2 = elt.split("@");
            //this.date, this.place, this.href, this.id
            String date = tmp2[0];
            String place = tmp2[1];
            String href = tmp2[2];
            String id = tmp2[3];
            Party party = new Party(date, place, href, id);
            elts.add(party);
        }

        mAdapter = new MainAdapter(elts, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void partyOnClick(int clickedItemIndex) {

    }
}
