package com.ribic.nejc.veselica.sync

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ribic.nejc.veselica.data.PartyContract
import com.ribic.nejc.veselica.objects.Party
import com.ribic.nejc.veselica.utils.NetworkUtils

import org.json.JSONArray
import org.json.JSONObject

import java.util.ArrayList

object PartySyncJob {

    private val ONE_OFF_ID = 2
    val ACTION_DATA_UPDATED = "com.ribic.nejc.party.ACTION_DATA_UPDATED"
    private val PERIOD = 18000000//300000;
    private val INITIAL_BACKOFF = 10000
    private val PERIODIC_ID = 1
    private val YEARS_OF_HISTORY = 2

    internal fun getParties(context: Context) {
        val queue = Volley.newRequestQueue(context)
        val url = NetworkUtils.urlAll
        val jsonArrayRequest = JsonArrayRequest(url, Response.Listener<JSONArray> { response ->
            val values = ArrayList<ContentValues>()
            try {
                for (i in 0..response.length() - 1) {
                    val jsonObject = response.getJSONObject(i)
                    val date = jsonObject.getString("date")
                    val jsonArray1 = jsonObject.getJSONArray("places")
                    for (j in 0..jsonArray1.length() - 1) {
                        val jsonObject1 = jsonArray1.getJSONObject(j)
                        val name = jsonObject1.getString("name")
                        val href = jsonObject1.getString("href")
                        val id = jsonObject1.getString("id")
                        val contentValues = ContentValues()
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_DATE, date)
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_HREF, href)
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_ID, Integer.parseInt(id))
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_NAME, name)
                        values.add(contentValues)
                    }
                }
                context.contentResolver.bulkInsert(PartyContract.PartyEntry.CONTENT_URI,
                        values.toTypedArray())

                //                            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
                //                            context.sendBroadcast(dataUpdatedIntent);
            } catch (e: Exception) {
                Log.e("ERROR: ", "Error " + e.message)
            }
        }, Response.ErrorListener { })
        //TODO call intialize on main activity
        queue.add(jsonArrayRequest)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun schedulePeriodic(context: Context) {
        Log.i("Party sync job", "schedulePeriodic")


        val builder = JobInfo.Builder(PERIODIC_ID, ComponentName(context, PartyJobService::class.java))


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD.toLong())
                .setBackoffCriteria(INITIAL_BACKOFF.toLong(), JobInfo.BACKOFF_POLICY_EXPONENTIAL)


        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        scheduler.schedule(builder.build())
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Synchronized fun initialize(context: Context) {

        schedulePeriodic(context)
        syncImmediately(context)

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Synchronized fun syncImmediately(context: Context) {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            val nowIntent = Intent(context, PartyIntentService::class.java)
            context.startService(nowIntent)
        } else {

            val builder = JobInfo.Builder(ONE_OFF_ID, ComponentName(context, PartyJobService::class.java))


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF.toLong(), JobInfo.BACKOFF_POLICY_EXPONENTIAL)


            val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            scheduler.schedule(builder.build())


        }
    }
}
