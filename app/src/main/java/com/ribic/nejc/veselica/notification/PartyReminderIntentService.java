package com.ribic.nejc.veselica.notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


public class PartyReminderIntentService extends IntentService {
    public PartyReminderIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        ReminderTask.executeTask(this, action);
    }
}
