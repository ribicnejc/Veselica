package com.ribic.nejc.veselica.notification

import android.content.Context

import com.ribic.nejc.veselica.utils.NotificationUtils

object ReminderTask {
    val ACTION_APPROVED_PARTY = "party-action-approved"
    val ACTION_DISMISSED_PARTY = "party-action-dismiss"

    fun executeTask(context: Context, action: String) {
        if (ACTION_APPROVED_PARTY == action) {
            System.out.printf("task was executed")
            NotificationUtils.clearAllNotifications(context)
        } else if (ACTION_DISMISSED_PARTY == action)
            NotificationUtils.clearAllNotifications(context)
    }
}
