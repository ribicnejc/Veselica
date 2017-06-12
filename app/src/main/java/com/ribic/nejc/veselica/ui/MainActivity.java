package com.ribic.nejc.veselica.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.SearchAdapter;
import com.ribic.nejc.veselica.adapters.SectionPagerAdapter;
import com.ribic.nejc.veselica.data.PartyContract;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;

import static com.ribic.nejc.veselica.fragments.MainEventsFragment.EXTRA_HREF;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchAdapterOnClickHandler {

    public static String TAG = "stuff in main activity";
    public SectionPagerAdapter.SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public MaterialSearchView mSearchView;
    public RecyclerView mRecyclerViewSearch;
    public SearchAdapter mSearchAdapter;
    public TabLayout tabLayout;
    public ArrayList<Party> mItems;
    public TextView mTextViewError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        mSectionsPagerAdapter = new SectionPagerAdapter.SectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mTextViewError = (TextView) findViewById(R.id.text_view_error_search);
        mRecyclerViewSearch = (RecyclerView) findViewById(R.id.recycler_view_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewSearch.setLayoutManager(layoutManager);

        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                readDataFromDatabase();
                mRecyclerViewSearch.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                mRecyclerViewSearch.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }
        });

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return search(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return search(newText);
            }
        });


        defaultAlarmNotification();
        favoriteEventsAlarm();
    }

    private void defaultAlarmNotification() {
        Intent intent = new Intent(NotificationUtils.ACTION_ALL_NOTIFICATION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), NotificationUtils.PARTY_REMINDER_INTENT_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // setup time for alarm
        Calendar alarmTime = Calendar.getInstance();

        // set time-part of alarm
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MINUTE, 25);
        alarmTime.set(Calendar.HOUR, 6);
        alarmTime.set(Calendar.AM_PM, Calendar.PM);
        //alarmTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        // calculate interval (7 days) in ms
//        int interval2 = 60000;//10s
        int interval = 1000 * 60 * 60 * 24;// * 7;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), interval, pendingIntent);
    }

    private void favoriteEventsAlarm() {
        Intent intent = new Intent(NotificationUtils.ACTION_FAVORITE_NOTIFICATION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), NotificationUtils.PARTY_REMINDER_INTENT_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MINUTE, 10);
        alarmTime.set(Calendar.HOUR, 4);
        alarmTime.set(Calendar.AM_PM, Calendar.PM);
        int interval = 1000 * 60 * 60 * 24;// * 7;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), interval, pendingIntent);
    }

    private boolean search(String newText){
        ArrayList<Party> items = new ArrayList<>();
        if (mItems == null) return false;
        for (Party party : mItems) {
            String main = party.getDate().toLowerCase() + " " + party.getPlace().toLowerCase();
            if (main.contains(newText)) {
                items.add(party);
            }
        }
        if (items.size() == 0) mTextViewError.setVisibility(View.VISIBLE);
        else mTextViewError.setVisibility(View.GONE);
        mSearchAdapter = new SearchAdapter(items, MainActivity.this);
        mRecyclerViewSearch.setAdapter(mSearchAdapter);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    public void readDataFromDatabase() {
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Cursor doInBackground(Void... voids) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, PartyContract.PartyEntry._ID);
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
                mSearchAdapter = new SearchAdapter(parties, MainActivity.this);
                mItems = parties;
                mRecyclerViewSearch.setAdapter(mSearchAdapter);
                super.onPostExecute(cursor);
            }
        }.execute();
    }

    @Override
    public void partyOnClick(int clickedItemIndex) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_HREF, mItems.get(clickedItemIndex).getHref());
        startActivity(intent);
    }
}




