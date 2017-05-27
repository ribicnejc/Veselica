package com.ribic.nejc.veselica.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.SectionPagerAdapter;
import com.ribic.nejc.veselica.utils.NotificationUtils;

import java.util.Calendar;

import butterknife.ButterKnife;

//TODO sync when internet come
//TODO same as in QuoteSyncJob
//TODO add real data to notification
public class MainActivity extends AppCompatActivity {

    //public static final String TAG = MainActivity.class.getSimpleName();
    public SectionPagerAdapter.SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;

    //TODO butterknife
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        mSectionsPagerAdapter = new SectionPagerAdapter.SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        alarmMethod();
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
        alarmTime.set(Calendar.MINUTE, 25);
        alarmTime.set(Calendar.HOUR, 6);
        alarmTime.set(Calendar.AM_PM, Calendar.PM);
        //alarmTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        // calculate interval (7 days) in ms
//        int interval2 = 60000;//10s
        int interval = 1000 * 60 * 60 * 24;// * 7;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), interval, pendingIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}




