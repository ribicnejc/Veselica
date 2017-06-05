package com.ribic.nejc.veselica.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.ribic.nejc.veselica.data.PartyContract;
import com.ribic.nejc.veselica.utils.NotificationUtils;
import com.ribic.nejc.veselica.utils.PrefUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "broadcast receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast Received, notification is setting up");
        String msg = getMessage(context);

        if (intent.getAction().equals(NotificationUtils.ACTION_FAVORITE_NOTIFICATION_ALARM)) {
            NotificationUtils.remindUserForFavoriteEvent(context, "favorite event");
        }
        //if (!msg.equals(""))
            NotificationUtils.remindUserForEvents(context, msg);
    }


    private String getMessageFavorites(Context context){
        Set<String> tmp = PrefUtils.getNames(context);
        for (String elt : tmp){
            String[] tmp2 = elt.split("@");
            String date = getDate(tmp2[0].split(" "));
        }
        return null;
        //TODO check if its todays date and push notification
    }

    private String getDate(String[] parts){
        String month;
        switch (parts[1]) {
            case "01":
                month = "januar";
                break;
            case "02":
                month = "februar";
                break;
            case "03":
                month = "marec";
                break;
            case "04":
                month = "april";
                break;
            case "05":
                month = "maj";
                break;
            case "06":
                month = "junij";
                break;
            case "07":
                month = "julij";
                break;
            case "08":
                month = "avgust";
                break;
            case "09":
                month = "september";
                break;
            case "10":
                month = "oktober";
                break;
            case "11":
                month = "november";
                break;
            case "12":
                month = "december";
                break;
            default:
                month = "januar";
                break;
        }
        return String.format("%s %s %s", parts[0], month, parts[2]);
    }

    private String getMessage(Context context) {
        Cursor cursor = context.getContentResolver().query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, PartyContract.PartyEntry._ID);
        DateFormat dateFormat = new SimpleDateFormat("dd. MM yyyy", Locale.US);
        Date date = new Date();
        String sDate = dateFormat.format(date);
        String[] parts = sDate.split(" ");
        String month;
        switch (parts[1]) {
            case "01":
                month = "januar";
                break;
            case "02":
                month = "februar";
                break;
            case "03":
                month = "marec";
                break;
            case "04":
                month = "april";
                break;
            case "05":
                month = "maj";
                break;
            case "06":
                month = "junij";
                break;
            case "07":
                month = "julij";
                break;
            case "08":
                month = "avgust";
                break;
            case "09":
                month = "september";
                break;
            case "10":
                month = "oktober";
                break;
            case "11":
                month = "november";
                break;
            case "12":
                month = "december";
                break;
            default:
                month = "januar";
                break;
        }

        String todayDate = String.format("%s %s %s", parts[0], month, parts[2]);

        if (cursor == null) return ":)";
        ArrayList<String> parties = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String date2 = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_DATE));
                //String href = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_HREF));
                //String id = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_ID));
                String name = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_NAME));
                if (todayDate.equals(date2.split(", ")[1])) {
                    parties.add(name + "\n");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        String msg = "";
        for (String elt : parties) {
            msg += elt + "\n\n";
        }

        return msg;
    }

}
