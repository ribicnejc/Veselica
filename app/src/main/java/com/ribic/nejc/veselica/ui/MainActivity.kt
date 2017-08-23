package com.ribic.nejc.veselica.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.ribicnejc.party.R
import com.ribic.nejc.veselica.adapters.SearchAdapter
import com.ribic.nejc.veselica.adapters.SectionPagerAdapter
import com.ribic.nejc.veselica.data.PartyContract
import com.ribic.nejc.veselica.objects.Party
import com.ribic.nejc.veselica.utils.NotificationUtils

import java.util.ArrayList
import java.util.Calendar

import butterknife.ButterKnife
import com.ribic.nejc.veselica.fragments.MainEventsFragment


class MainActivity : AppCompatActivity(), SearchAdapter.SearchAdapterOnClickHandler {
    lateinit var mSectionsPagerAdapter: SectionPagerAdapter.SectionsPagerAdapter
    lateinit var mViewPager: ViewPager
    lateinit var mSearchView: MaterialSearchView
    lateinit var mRecyclerViewSearch: RecyclerView
    lateinit var mSearchAdapter: SearchAdapter
    lateinit var tabLayout: TabLayout
    var mItems: ArrayList<Party>? = null
    lateinit var mTextViewError: TextView
    private val mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        ButterKnife.bind(this)
        mSectionsPagerAdapter = SectionPagerAdapter.SectionsPagerAdapter(supportFragmentManager, this)
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager.adapter = mSectionsPagerAdapter
        tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        mTextViewError = findViewById(R.id.text_view_error_search) as TextView
        mRecyclerViewSearch = findViewById(R.id.recycler_view_search) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerViewSearch.layoutManager = layoutManager

        mSearchView = findViewById(R.id.search_view) as MaterialSearchView
        mSearchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                readDataFromDatabase()
                mRecyclerViewSearch.visibility = View.VISIBLE
                mViewPager.visibility = View.GONE
                tabLayout.visibility = View.GONE
            }

            override fun onSearchViewClosed() {
                mRecyclerViewSearch.visibility = View.GONE
                mViewPager.visibility = View.VISIBLE
                tabLayout.visibility = View.VISIBLE
            }
        })

        mSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return search(query)
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return search(newText)
            }
        })


        //        MobileAds.initialize(this, "ca-app-pub-9063494299873125~6564237697");
        //        mAdView = (AdView) findViewById(R.id.adView);
        //        AdRequest adRequest = new AdRequest.Builder()
        //                .addTestDevice("EF90B385FA7E86AC5D3194CEBEAB9E2E")
        //                .build();
        //        mAdView.loadAd(adRequest);


        defaultAlarmNotification()
        favoriteEventsAlarm()
    }

    private fun defaultAlarmNotification() {
        val intent = Intent(NotificationUtils.ACTION_ALL_NOTIFICATION_ALARM)
        val pendingIntent = PendingIntent.getBroadcast(this.applicationContext, NotificationUtils.PARTY_REMINDER_INTENT_ID, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // setup time for alarm
        val alarmTime = Calendar.getInstance()

        // set time-part of alarm
        alarmTime.set(Calendar.SECOND, 0)
        alarmTime.set(Calendar.MINUTE, 25)
        alarmTime.set(Calendar.HOUR, 6)
        alarmTime.set(Calendar.AM_PM, Calendar.PM)
        //alarmTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        // calculate interval (7 days) in ms
        //        int interval2 = 60000;//10s
        val interval = 1000 * 60 * 60 * 24// * 7;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, interval.toLong(), pendingIntent)
    }

    private fun favoriteEventsAlarm() {
        val intent = Intent(NotificationUtils.ACTION_FAVORITE_NOTIFICATION_ALARM)
        val pendingIntent = PendingIntent.getBroadcast(this.applicationContext, NotificationUtils.PARTY_REMINDER_INTENT_ID, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = Calendar.getInstance()
        alarmTime.set(Calendar.SECOND, 0)
        alarmTime.set(Calendar.MINUTE, 10)
        alarmTime.set(Calendar.HOUR, 4)
        alarmTime.set(Calendar.AM_PM, Calendar.PM)
        val interval = 1000 * 60 * 60 * 24// * 7;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, interval.toLong(), pendingIntent)
    }

    private fun search(newText: String): Boolean {
        val items = ArrayList<Party>()
        if (mItems == null) return false
        for (party in mItems!!) {
            val main = party.date!!.toLowerCase() + " " + party.place!!.toLowerCase()
            if (main.contains(newText)) {
                items.add(party)
            }
        }
        if (items.size == 0)
            mTextViewError.visibility = View.VISIBLE
        else
            mTextViewError.visibility = View.GONE
        mSearchAdapter = SearchAdapter(items, this@MainActivity)
        mRecyclerViewSearch.adapter = mSearchAdapter
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail, menu)

        val item = menu.findItem(R.id.action_search)
        mSearchView.setMenuItem(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        if (mSearchView.isSearchOpen) {
            mSearchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    fun readDataFromDatabase() {
        object : AsyncTask<Void, Void, Cursor>() {
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg voids: Void): Cursor {
                var cursor: Cursor? = null
                try {
                    cursor = contentResolver.query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, PartyContract.PartyEntry._ID)
                } catch (e: Exception) {
                    Log.v(TAG, "Failed to query data")
                    e.printStackTrace()
                }

                return cursor!!
            }

            override fun onPostExecute(cursor: Cursor?) {
                if (cursor == null) return
                val parties = ArrayList<Party>()
                if (cursor.moveToFirst()) {
                    do {
                        val date = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_DATE))
                        val href = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_HREF))
                        val id = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_ID))
                        val name = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_NAME))
                        val party = Party(date, name, href, id)
                        parties.add(party)
                    } while (cursor.moveToNext())
                }
                cursor.close()
                mSearchAdapter = SearchAdapter(parties, this@MainActivity)
                mItems = parties
                mRecyclerViewSearch.adapter = mSearchAdapter
                super.onPostExecute(cursor)
            }
        }.execute()
    }

    override fun partyOnClick(clickedItemIndex: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(MainEventsFragment.EXTRA_HREF, mItems!![clickedItemIndex].href)
        startActivity(intent)
    }

    companion object {

        var TAG = "stuff in main activity"
    }
}




