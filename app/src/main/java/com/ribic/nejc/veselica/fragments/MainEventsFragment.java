package com.ribic.nejc.veselica.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.MainAdapter;
import com.ribic.nejc.veselica.data.PartyContract;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.ui.DetailActivity;
import com.ribic.nejc.veselica.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainEventsFragment extends Fragment implements MainAdapter.MainAdapterOnClickHandler, SwipeRefreshLayout.OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String EXTRA_HREF = "com.nejc.ribic.veselica.href";
    private static final String ARG_SECTION_NUMBER = "section_number";
    public RecyclerView mRecyclerView;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public MainAdapter mMainAdapter;
    public String TAG = MainEventsFragment.this.getTag();
    public TextView mTextViewError;
    public ArrayList<Party> mParties;

    public MainEventsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public MainEventsFragment newInstance(int sectionNumber) {
        MainEventsFragment fragment = new MainEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_parties);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mTextViewError = (TextView) rootView.findViewById(R.id.text_view_error);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        onRefresh();
        return rootView;
    }

    @Override
    public void onRefresh() {
        if (networkUp()) {
            mSwipeRefreshLayout.setRefreshing(true);
            new FetchData().execute();
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
            Toast.makeText(getContext(), "Will refresh when internet come back", Toast.LENGTH_SHORT).show();
            readDataFromDatabase();
        }
    }


    @Override
    public void partyOnClick(int clickedItemIndex) {
        Toast.makeText(getContext(), "Works", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(EXTRA_HREF, mParties.get(clickedItemIndex).getHref());
        startActivity(intent);
    }

    private class FetchData extends AsyncTask<String, String, ArrayList<Party>> {

        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Party> doInBackground(String... strings) {
            URL url = NetworkUtils.buildUrl();
            ArrayList<Party> parties = new ArrayList<>();
            List<ContentValues> values = new ArrayList<>();
            try {
                String result = NetworkUtils.getResponseFromHttpUrl(url);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("places");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("name");
                        String href = jsonObject1.getString("href");
                        String id = jsonObject1.getString("id");
                        Party party = new Party();
                        party.setPlace(name);
                        party.setHref(href);
                        party.setDate(date);
                        party.setId(id);
                        parties.add(party);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_DATE, party.getDate());
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_HREF, party.getHref());
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_ID, Integer.parseInt(party.getId()));
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_NAME, party.getPlace());
                        values.add(contentValues);
                    }
                }
                getContext().getContentResolver().bulkInsert(PartyContract.PartyEntry.CONTENT_URI,
                        values.toArray(new ContentValues[values.size()]));
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "problem appeared when parsing JSON");
            }
            Log.v(TAG, "result get from internet");
            return parties;
        }

        @Override
        protected void onPostExecute(ArrayList<Party> parties) {
            super.onPostExecute(parties);
            mRecyclerView.setVisibility(View.VISIBLE);
            mParties = parties;
            MainAdapter mMainAdapter = new MainAdapter(parties, MainEventsFragment.this);
            mRecyclerView.setAdapter(mMainAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void readDataFromDatabase() {
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected void onPreExecute() {
                mSwipeRefreshLayout.setRefreshing(true);
                super.onPreExecute();
            }

            @Override
            protected Cursor doInBackground(Void... voids) {
                Cursor cursor = null;
                try {
                    cursor = getContext().getContentResolver().query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, PartyContract.PartyEntry._ID);
                } catch (Exception e) {
                    Log.v(TAG, "Failed to query data");
                    e.printStackTrace();
                }
                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                if (cursor == null) return;
                ArrayList<Party> parties = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        String date = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_DATE));
                        String href = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_HREF));
                        String id = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_ID));
                        String name = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_NAME));
                        Party party = new Party(date, name, href, id);
                        parties.add(party);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                mMainAdapter = new MainAdapter(parties, MainEventsFragment.this);
                mSwipeRefreshLayout.setRefreshing(false);
                mParties = parties;
                mRecyclerView.setAdapter(mMainAdapter);
                super.onPostExecute(cursor);
            }
        }.execute();
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
