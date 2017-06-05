package com.ribic.nejc.veselica.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.ui.MainActivity;
import com.ribic.nejc.veselica.notification.PartyReminderIntentService;
import com.ribic.nejc.veselica.notification.ReminderTask;


public final class NotificationUtils {
    public static final int PARTY_REMINDER_INTENT_ID = 443;
    public static final int PARTY_REMINDER_FAVORITE_INTENT_ID = 444;
    public static final int ACTION_IGNORE_PENDING_INTENT_ID = 3434;
    public static final int ACTION_APPROVE_PENDING_INTENT_ID = 3343434;

    public static final String ACTION_ALL_NOTIFICATION_ALARM = "com.ribic.nejc.veselica.PUSH_NOTIFICATION";
    public static final String ACTION_FAVORITE_NOTIFICATION_ALARM = "com.ribic.nejc.veselica.PUSH_NOTIFICATION_FAVORITE";


    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                PARTY_REMINDER_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.trees);
        return largeIcon;
    }

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserForEvents(Context context, String message){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.margarita)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
                //.addAction(approveReminderAction(context))
                //.addAction(ignoreReminderAction(context))

        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PARTY_REMINDER_INTENT_ID, notificationBuilder.build());
    }

    public static void remindUserForFavoriteEvent(Context context, String message){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.margarita)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_title_favorite))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        //.addAction(approveReminderAction(context))
        //.addAction(ignoreReminderAction(context))

        //TODO add option for remind in in one hour

        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PARTY_REMINDER_FAVORITE_INTENT_ID, notificationBuilder.build());

    }


    private static NotificationCompat.Action ignoreReminderAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, PartyReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTask.ACTION_DISMISSED_PARTY);

        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.mipmap.ic_launcher,
                "No thanks!",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static NotificationCompat.Action approveReminderAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, PartyReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTask.ACTION_APPROVED_PARTY);

        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_APPROVE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.mipmap.ic_launcher,
                "Yes please",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }


}
