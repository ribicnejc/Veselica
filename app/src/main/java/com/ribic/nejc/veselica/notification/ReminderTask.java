package com.ribic.nejc.veselica.notification;

import android.content.Context;

import com.ribic.nejc.veselica.utils.NotificationUtils;

public class ReminderTask {
    public static final String ACTION_APPROVED_PARTY = "party-action-approved";
    public static final String ACTION_DISMISSED_PARTY = "party-action-dismiss";

    public static void executeTask(Context context, String action){
        if (ACTION_APPROVED_PARTY.equals(action)){
            System.out.printf("task was executed");
            NotificationUtils.clearAllNotifications(context);
        }
        else if (ACTION_DISMISSED_PARTY.equals(action))
            NotificationUtils.clearAllNotifications(context);
    }
}
