package com.ribic.nejc.veselica.notification

import android.app.IntentService
import android.content.Intent


class PartyReminderIntentService : IntentService("") {

    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        ReminderTask.executeTask(this, action)
    }
}
