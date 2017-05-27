package com.ribic.nejc.veselica.sync;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class PartyJobService extends JobService{
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("Party Job Service", "service started");
        Intent nowIntent = new Intent(getApplicationContext(), PartyIntentService.class);
        getApplicationContext().startService(nowIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }



}
