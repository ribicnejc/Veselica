package com.ribic.nejc.veselica.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class PartyIntentService extends IntentService{

    public PartyIntentService(){
        super(PartyIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PartySyncJob.getParties(getApplicationContext());
    }
}
