package com.ribic.nejc.veselica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.MainAdapter;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.sync.NotificationAlarmReceiver;
import com.ribic.nejc.veselica.utils.NetworkUtils;
import com.ribic.nejc.veselica.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

//TODO make notification
public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler{
    public ProgressBar mProgressBar;
    public RecyclerView mRecyclerView;
    public MainAdapter mMainAdapter;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_main_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_parties);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        //setUpCalender();
        alarmMethod();
        new FetchData().execute();
    }

    private void alarmMethod() {
        //TODO give option for particular party in settings
        Intent intent = new Intent("com.ribic.nejc.veselica.PUSH_NOTIFICATION");
        //Intent intent = new Intent(this, NotificationAlarmReceiver.class);
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
        //TODO set for weeks
        int interval = 1000 * 60 * 60 * 24 * 7;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), interval, pendingIntent);
    }

    @Override
    public void partyOnClick(int clickedItemIndex) {
        Toast.makeText(this, "Time to go to sleep!", Toast.LENGTH_SHORT).show();
    }

    public void testNotification(View view) {
        NotificationUtils.remindUserBecauseCharging(this);
    }

    private class FetchData extends AsyncTask<String, String, ArrayList<Party>> {

        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Party> doInBackground(String... strings) {
            URL url = NetworkUtils.buildUrl();
            ArrayList<Party> parties = new ArrayList<>();
            try {
                String result = NetworkUtils.getResponseFromHttpUrl(url);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("places");
                    for (int j = 0; j < jsonArray1.length(); j++){
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("name");
                        String href = jsonObject1.getString("href");
                        Party party = new Party();
                        party.setPlace(name);
                        party.setHref(href);
                        party.setDate(date);
                        parties.add(party);
                    }
                }

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
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mMainAdapter = new MainAdapter(parties, MainActivity.this);
            mRecyclerView.setAdapter(mMainAdapter);
        }
    }

}




