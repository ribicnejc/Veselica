package com.ribic.nejc.veselica.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat

import com.ribicnejc.party.R
import com.ribic.nejc.veselica.ui.MainActivity
import com.ribic.nejc.veselica.notification.PartyReminderIntentService
import com.ribic.nejc.veselica.notification.ReminderTask


object NotificationUtils {
    val PARTY_REMINDER_INTENT_ID = 443
    val PARTY_REMINDER_FAVORITE_INTENT_ID = 444
    val ACTION_IGNORE_PENDING_INTENT_ID = 3434
    val ACTION_APPROVE_PENDING_INTENT_ID = 3343434

    val ACTION_ALL_NOTIFICATION_ALARM = "com.ribic.nejc.veselica.PUSH_NOTIFICATION"
    val ACTION_FAVORITE_NOTIFICATION_ALARM = "com.ribic.nejc.veselica.PUSH_NOTIFICATION_FAVORITE"


    private fun contentIntent(context: Context): PendingIntent {
        val startActivityIntent = Intent(context, MainActivity::class.java)

        return PendingIntent.getActivity(
                context,
                PARTY_REMINDER_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun largeIcon(context: Context): Bitmap {
        val res = context.resources
        val largeIcon = BitmapFactory.decodeResource(res, R.mipmap.icon)
        return largeIcon
    }

    fun clearAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun remindUserForEvents(context: Context, message: String) {
        val notificationBuilder = NotificationCompat.Builder(context)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.margarita)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true)
        //.addAction(approveReminderAction(context))
        //.addAction(ignoreReminderAction(context))

        notificationBuilder.priority = Notification.PRIORITY_HIGH
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(PARTY_REMINDER_INTENT_ID, notificationBuilder.build())
    }

    fun remindUserForFavoriteEvent(context: Context, message: String) {
        val notificationBuilder = NotificationCompat.Builder(context)
                // .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.icon_stared)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_title_favorite))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true)
        //.addAction(approveReminderAction(context))
        //.addAction(ignoreReminderAction(context))

        //TODO add option for remind in in one hour

        notificationBuilder.priority = Notification.PRIORITY_HIGH
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(PARTY_REMINDER_FAVORITE_INTENT_ID, notificationBuilder.build())

    }


    private fun ignoreReminderAction(context: Context): NotificationCompat.Action {
        val ignoreReminderIntent = Intent(context, PartyReminderIntentService::class.java)
        ignoreReminderIntent.action = ReminderTask.ACTION_DISMISSED_PARTY

        val ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val ignoreReminderAction = NotificationCompat.Action(R.mipmap.ic_launcher,
                "No thanks!",
                ignoreReminderPendingIntent)
        return ignoreReminderAction
    }

    private fun approveReminderAction(context: Context): NotificationCompat.Action {
        val ignoreReminderIntent = Intent(context, PartyReminderIntentService::class.java)
        ignoreReminderIntent.action = ReminderTask.ACTION_APPROVED_PARTY

        val ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_APPROVE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )

        val ignoreReminderAction = NotificationCompat.Action(R.mipmap.ic_launcher,
                "Yes please",
                ignoreReminderPendingIntent)
        return ignoreReminderAction
    }


}
