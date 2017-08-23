package com.ribic.nejc.veselica.utils


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.ribicnejc.party.R
import java.util.ArrayList
import java.util.HashSet

object PrefUtils {

    fun getNames(context: Context): MutableSet<String> {
        val namesKey = context.getString(R.string.pref_favorite_names_key)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getStringSet(namesKey, HashSet<String>())
    }

    fun saveNames(elts: Set<String>, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        val namesKey = context.getString(R.string.pref_favorite_names_key)
        val set = HashSet<String>()
        set.addAll(elts)
        editor.remove(namesKey)
        editor.apply()
        //        editor.clear();
        editor.putStringSet(namesKey, set)
        editor.apply()
    }

    fun saveName(name: String, context: Context) {
        val tmp = getNames(context)
        tmp.add(name)
        saveNames(tmp, context)
    }

    fun exitsts(name: String, context: Context): Boolean {
        val set = getNames(context)
        return set.contains(name)
    }

    fun remove(name: String, context: Context) {
        val tmp = getNames(context)
        tmp.remove(name)
        saveNames(tmp, context)
    }

    fun notificationsEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = context.getString(R.string.pref_notification_key)
        return prefs.getBoolean(key, true)
    }
}
