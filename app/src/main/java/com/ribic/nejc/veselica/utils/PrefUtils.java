package com.ribic.nejc.veselica.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ribic.nejc.party.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtils {

    private PrefUtils(){}

    public static Set<String> getNames(Context context){
        String namesKey = context.getString(R.string.pref_favorite_names_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = prefs.getStringSet(namesKey, new HashSet<String>());
        return set;
    }

    public static void saveNames(Set<String> elts, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String namesKey = context.getString(R.string.pref_favorite_names_key);
        Set<String> set = new HashSet<>();
        set.addAll(elts);
        editor.putStringSet(namesKey, set);
        editor.apply();
    }

    public static void saveName(String name, Context context){
        Set<String> tmp = getNames(context);
        tmp.add(name);
        saveNames(tmp, context);
    }
}
