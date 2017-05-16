package com.ribic.nejc.veselica.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ribic.nejc.veselica.utils.NotificationUtils;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "broadcast receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast Received");
        NotificationUtils.remindUserBecauseCharging(context);
    }
}
