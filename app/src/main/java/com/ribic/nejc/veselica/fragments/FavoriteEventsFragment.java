package com.ribic.nejc.veselica.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.FavoriteAdapter;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.ui.DetailActivity;
import com.ribic.nejc.veselica.utils.PrefUtils;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static com.ribic.nejc.veselica.fragments.MainEventsFragment.EXTRA_HREF;

public class FavoriteEventsFragment extends Fragment implements FavoriteAdapter.FavoriteAdapterOnClickHandler, SwipeRefreshLayout.OnRefreshListener {

    public static final int CHECK_FOR_CHANGE_REQUEST = 2;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public RecyclerView mRecyclerView;
    public FavoriteAdapter mAdapter;
    public ArrayList<Party> mParties;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public TextView mTextViewError;

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
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_favorites);
        mTextViewError = (TextView) rootView.findViewById(R.id.text_view_error_favorite);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.colorSwipeRefresh1),
                ContextCompat.getColor(getContext(), R.color.colorSwipeRefresh2),
                ContextCompat.getColor(getContext(), R.color.colorSwipeRefresh3)
                );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Party party = mParties.remove(viewHolder.getAdapterPosition());
                PrefUtils.remove(party.toString(), getContext());
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                checkIfEmpty();
                //TODO notify all data even on the other fragment

            }
        }).attachToRecyclerView(mRecyclerView);


        onRefresh();
        return rootView;
    }

    public void readData() {
        mSwipeRefreshLayout.setRefreshing(true);
        ArrayList<Party> elts = new ArrayList<>();
        Set<String> tmp = PrefUtils.getNames(getContext());
        for (String elt : tmp) {
            String[] tmp2 = elt.split("@");
            String date = tmp2[0];
            String place = tmp2[1];
            String href = tmp2[2];
            String id = tmp2[3];
            Party party = new Party(date, place, href, id);
            elts.add(party);
        }
        mParties = elts;

        checkIfEmpty();

        mAdapter = new FavoriteAdapter(elts, this);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void partyOnClick(int clickedItemIndex) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(EXTRA_HREF, mParties.get(clickedItemIndex).getHref());
        startActivityForResult(intent, CHECK_FOR_CHANGE_REQUEST);
    }

    @Override
    public void onRefresh() {
        readData();
    }


    private void checkIfEmpty() {
        if (mParties.size() == 0) {
            mTextViewError.setVisibility(View.VISIBLE);
        } else mTextViewError.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_FOR_CHANGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                readData();
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (Constants.fragmentContentChanged) {
//            Constants.fragmentContentChanged = false;
//            readData();
//        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
