package com.ribic.nejc.veselica.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ribic.nejc.veselica.data.PartyContract;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PartySyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.ribic.nejc.party.ACTION_DATA_UPDATED";
    private static final int PERIOD = 18000000;//300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private PartySyncJob() {
    }

    static void getParties(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = NetworkUtils.getUrlAll();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ContentValues> values = new ArrayList<>();
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String date = jsonObject.getString("date");
                                JSONArray jsonArray1 = jsonObject.getJSONArray("places");
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    String name = jsonObject1.getString("name");
                                    String href = jsonObject1.getString("href");
                                    String id = jsonObject1.getString("id");
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_DATE, date);
                                    contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_HREF, href);
                                    contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_ID, Integer.parseInt(id));
                                    contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_NAME, name);
                                    values.add(contentValues);
                                }
                            }
                            context.getContentResolver().bulkInsert(PartyContract.PartyEntry.CONTENT_URI,
                                    values.toArray(new ContentValues[values.size()]));

//                            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
//                            context.sendBroadcast(dataUpdatedIntent);
                        }catch (Exception e){
                            Log.e("ERROR: ", "Error " + e.getLocalizedMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //TODO call intialize on main activity
        queue.add(jsonArrayRequest);
    }


    private static void schedulePeriodic(Context context) {
        Log.i("Party sync job", "schedulePeriodic");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, PartyJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }


    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, PartyIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, PartyJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }
}
