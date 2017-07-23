package com.ribic.nejc.veselica.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ribicnejc.party.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtils {

    private PrefUtils(){}

    public static Set<String> getNames(Context context){
        String namesKey = context.getString(R.string.pref_favorite_names_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(namesKey, new HashSet<String>());
    }

    public static void saveNames(Set<String> elts, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String namesKey = context.getString(R.string.pref_favorite_names_key);
        Set<String> set = new HashSet<>();
        set.addAll(elts);
        editor.remove(namesKey);
        editor.apply();
//        editor.clear();
        editor.putStringSet(namesKey, set);
        editor.apply();
    }

    public static void saveName(String name, Context context){
        Set<String> tmp = getNames(context);
        tmp.add(name);
        saveNames(tmp, context);
    }

    public static boolean exitsts(String name, Context context){
        Set<String> set = getNames(context);
        return set.contains(name);
    }

    public static void remove(String name, Context context){
        Set<String> tmp = getNames(context);
        tmp.remove(name);
        saveNames(tmp, context);
    }

    public static boolean notificationsEnabled(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_notification_key);
        return prefs.getBoolean(key, true);
    }
}
