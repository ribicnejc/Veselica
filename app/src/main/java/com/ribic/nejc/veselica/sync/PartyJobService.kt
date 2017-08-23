package com.ribic.nejc.veselica.sync


import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log

class PartyJobService : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.d("Party Job Service", "service started")
        val nowIntent = Intent(applicationContext, PartyIntentService::class.java)
        applicationContext.startService(nowIntent)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }


}
