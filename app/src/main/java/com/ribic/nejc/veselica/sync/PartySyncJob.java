package com.ribic.nejc.veselica.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PartySyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.ribic.nejc.party.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private PartySyncJob(){
    }

    static void getParties(Context context){

        //TODO call intialize on main activity

        //TODO sync data
        //TODO fetch data
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
