package com.ribic.nejc.veselica.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Nejc on 14. 05. 2017.
 */

public class PartyReminderIntentService extends IntentService {
    public PartyReminderIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        ReminderTask.executeTask(this, action);
    }
}
