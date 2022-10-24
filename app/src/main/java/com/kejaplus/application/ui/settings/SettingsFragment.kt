package com.kejaplus.application.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.kejaplus.application.R
import com.kejaplus.application.Support.PhoneThemes
import com.kejaplus.application.Support.ThemeHelper


class SettingsFragment :PreferenceFragmentCompat()  {



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Theme Settings
        val themePref = findPreference<ListPreference>(getString(R.string.pref_theme_key))
        themePref?.value?.let {
            themePref.summary = it
        }

        themePref?.let {
            it.setOnPreferenceChangeListener { preference, newValue ->
                val theme: PhoneThemes = when (newValue.toString()) {
                    "System Theme" -> PhoneThemes.DEFAULT
                    "Light Mode" -> PhoneThemes.LIGHT
                    "Dark Mode" -> PhoneThemes.DARK
                    "Battery Saver Mode" -> PhoneThemes.BATTERY_SAVER_MODE
                    else -> PhoneThemes.DEFAULT
                }

                ThemeHelper.applyTheme(theme)
                preference.summary = newValue.toString()
                return@setOnPreferenceChangeListener true
            }
        }
    }

        override fun onResume() {
            super.onResume()
            //preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        }

        override fun onPause() {
            super.onPause()
            //preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        }




}