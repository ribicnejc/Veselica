package com.ribic.nejc.veselica.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log

import com.ribic.nejc.veselica.data.PartyContract
import com.ribic.nejc.veselica.utils.NotificationUtils
import com.ribic.nejc.veselica.utils.PrefUtils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class NotificationAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Broadcast Received, notification is setting up")

        if (!PrefUtils.notificationsEnabled(context)) return

        if (intent.action == NotificationUtils.ACTION_FAVORITE_NOTIFICATION_ALARM) {
            val msg = getMessageFavorites(context)
            if (msg != "")
                NotificationUtils.remindUserForFavoriteEvent(context, msg)
        } else {
            val msg = getMessage(context)
            if (msg != "")
                NotificationUtils.remindUserForEvents(context, msg)
        }
    }


    private fun getMessageFavorites(context: Context): String {
        val tmp = PrefUtils.getNames(context)
        val date = date
        val favoriteEvents = ArrayList<String>()
        for (elt in tmp) {
            val tmp2 = elt.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val tmp3 = tmp2[0].split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (tmp3[1] == date) {
                favoriteEvents.add(tmp2[1])
            }
        }
        var msg = ""
        for (elt in favoriteEvents) {
            msg += elt + "\n\n"
        }
        return msg
    }

    private fun getMessage(context: Context): String {
        val cursor = context.contentResolver.query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, PartyContract.PartyEntry._ID)
        val todayDate = date
        if (cursor == null) return ":)"
        val parties = ArrayList<String>()
        if (cursor.moveToFirst()) {
            do {
                val date2 = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_DATE))
                val name = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_NAME))
                if (todayDate == date2.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]) {
                    parties.add(name + "\n")
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        var msg = ""
        for (elt in parties) {
            msg += elt + "\n\n"
        }
        return msg
    }

    private val date: String
        get() {
            val dateFormat = SimpleDateFormat("dd. MM yyyy", Locale.US)
            val date = Date()
            val sDate = dateFormat.format(date)
            val parts = sDate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val month: String
            when (parts[1]) {
                "01" -> month = "januar"
                "02" -> month = "februar"
                "03" -> month = "marec"
                "04" -> month = "april"
                "05" -> month = "maj"
                "06" -> month = "junij"
                "07" -> month = "julij"
                "08" -> month = "avgust"
                "09" -> month = "september"
                "10" -> month = "oktober"
                "11" -> month = "november"
                "12" -> month = "december"
                else -> month = "januar"
            }
            if (parts[0].split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] == "0") {
                parts[0] = parts[0].substring(1)
                parts[0] = " " + parts[0]
            }
            return String.format("%s %s %s", parts[0], month, parts[2])
        }

    companion object {
        private val TAG = "broadcast receiver"
    }
}
