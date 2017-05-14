package com.ribic.nejc.veselica.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.MainActivity;
import com.ribic.nejc.veselica.sync.PartyReminderIntentService;
import com.ribic.nejc.veselica.sync.ReminderTask;


public class NotificationUtils {
    public static final int PARTY_REMINDER_INTENT_ID = 443;
    public static final int ACTION_IGNORE_PENDING_INTENT_ID = 3434;
    public static final int ACTION_APPROVE_PENDING_INTENT_ID = 3343434;
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
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        return largeIcon;
    }

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserBecauseCharging(Context context){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_big_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(approveReminderAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PARTY_REMINDER_INTENT_ID, notificationBuilder.build());
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
