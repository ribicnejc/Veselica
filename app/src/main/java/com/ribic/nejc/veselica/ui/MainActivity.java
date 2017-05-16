package com.ribic.nejc.veselica.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.MainAdapter;
import com.ribic.nejc.veselica.data.PartyContract;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.NetworkUtils;
import com.ribic.nejc.veselica.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//TODO sync when internet come
//TODO same as in QuoteSyncJob
public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler,
SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.rv_parties)
    public RecyclerView mRecyclerView;

    public SwipeRefreshLayout mSwipeRefreshLayout;

    public MainAdapter mMainAdapter;
    public static final String TAG = MainActivity.class.getSimpleName();

    //TODO butterknife
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_parties);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        alarmMethod();
        onRefresh();


    }

    private void alarmMethod() {
        //TODO give option for particular party in settings
        Intent intent = new Intent("com.ribic.nejc.veselica.PUSH_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), NotificationUtils.PARTY_REMINDER_INTENT_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // setup time for alarm
        Calendar alarmTime = Calendar.getInstance();

        // set time-part of alarm
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MINUTE, 15);
        alarmTime.set(Calendar.HOUR, 6);
        alarmTime.set(Calendar.AM_PM, Calendar.PM);
        alarmTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        // calculate interval (7 days) in ms
//        int interval2 = 60000;//10s
        int interval = 1000 * 60 * 60 * 24 * 7;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), interval, pendingIntent);
    }

    @Override
    public void partyOnClick(int clickedItemIndex) {
        Toast.makeText(this, "Time to go to sleep!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        if (networkUp()){
            mSwipeRefreshLayout.setRefreshing(true);
            new FetchData().execute();
        }else{
            mSwipeRefreshLayout.setRefreshing(true);
            readDataFromDatabase();
        }
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
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_ID, party.getId());
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_NAME, party.getPlace());
                        values.add(contentValues);
                    }
                }
                getContentResolver().bulkInsert(PartyContract.PartyEntry.CONTENT_URI,
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
            mMainAdapter = new MainAdapter(parties, MainActivity.this);
            mRecyclerView.setAdapter(mMainAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void readDataFromDatabase(){
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected void onPreExecute() {
                mSwipeRefreshLayout.setRefreshing(true);
                super.onPreExecute();
            }

            @Override
            protected Cursor doInBackground(Void... voids) {
                Cursor cursor = null;
                try{
                    cursor = getContentResolver().query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, null);
                }catch (Exception e){
                    Log.v(TAG, "Failed to query data");
                    e.printStackTrace();
                }
                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                if (cursor == null) return;
                ArrayList<Party> parties = new ArrayList<>();
                if (cursor.moveToFirst()){
                    do{
                        String date = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_DATE));
                        String href = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_HREF));
                        String id = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_ID));
                        String name = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_NAME));
                        Party party = new Party(date, name, href, id);
                        parties.add(party);
                    }while (cursor.moveToNext());
                }
                mMainAdapter = new MainAdapter(parties, MainActivity.this);
                mSwipeRefreshLayout.setRefreshing(false);
                super.onPostExecute(cursor);
            }
        }.execute();
    }


}




