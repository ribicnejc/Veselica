package com.ribic.nejc.veselica.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen

import com.ribicnejc.party.R

class SettingsFragment : PreferenceFragmentCompat(),
        // COMPLETED (10) Implement OnSharedPreferenceChangeListener from SettingsFragment
        SharedPreferences.OnSharedPreferenceChangeListener {

    // COMPLETED (8) Create a method called setPreferenceSummary that accepts a Preference and an Object and sets the summary of the preference
    private fun setPreferenceSummary(preference: Preference, value: Any) {
        val stringValue = value.toString()
        val key = preference.key
        //
        if (preference is ListPreference) {
            /* For list preferences, look up the correct display value in */
            /* the preference's 'entries' list (since they have separate labels/values). */
            val listPreference = preference
            val prefIndex = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.entries[prefIndex])
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.summary = stringValue
        }
    }

    // COMPLETED (5) Override onCreatePreferences and add the preference xml file using addPreferencesFromResource
    override fun onCreatePreferences(bundle: Bundle, s: String) {
        /* Add 'general' preferences, defined in the XML file */
        addPreferencesFromResource(R.xml.pref_general)

        // COMPLETED (9) Set the preference summary on each preference that isn't a CheckBoxPreference
        val sharedPreferences = preferenceScreen.sharedPreferences
        val prefScreen = preferenceScreen
        val count = prefScreen.preferenceCount
        for (i in 0..count - 1) {
            val p = prefScreen.getPreference(i)
            if (p !is CheckBoxPreference) {
                val value = sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value)
            }
        }
    }

    // COMPLETED (13) Unregister SettingsFragment (this) as a SharedPreferenceChangedListener in onStop
    override fun onStop() {
        super.onStop()
        /* Unregister the preference change listener */
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    // COMPLETED (12) Register SettingsFragment (this) as a SharedPreferenceChangedListener in onStart
    override fun onStart() {
        super.onStart()
        /* Register the preference change listener */
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    // COMPLETED (11) Override onSharedPreferenceChanged to update non CheckBoxPreferences when they are changed
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference(key)
        if (null != preference) {
            if (preference !is CheckBoxPreference) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""))
            }
        }
    }
}
