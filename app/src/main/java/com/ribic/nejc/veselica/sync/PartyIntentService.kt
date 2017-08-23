package com.ribic.nejc.veselica.sync

import android.app.IntentService
import android.content.Intent

class PartyIntentService : IntentService(PartyIntentService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        PartySyncJob.getParties(applicationContext)
    }
}
