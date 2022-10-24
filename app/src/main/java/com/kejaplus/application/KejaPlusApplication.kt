package com.kejaplus.application

import android.app.Application
import androidx.preference.PreferenceManager
import com.kejaplus.application.Support.PhoneThemes
import com.kejaplus.application.Support.ThemeHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KejaPlusApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        setupAppTheme()
    }

    private fun initTimber(){
        Timber.plant(Timber.DebugTree())
    }

    private fun setupAppTheme() {
        val themeString = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(resources.getString(R.string.pref_theme_key), "")
        val theme: PhoneThemes = when (themeString) {
            "System Theme" -> PhoneThemes.DEFAULT
            "Light Mode" -> PhoneThemes.LIGHT
            "Dark Mode" -> PhoneThemes.DARK
            "Battery Saver Mode" -> PhoneThemes.BATTERY_SAVER_MODE
            else -> PhoneThemes.DEFAULT
        }
        ThemeHelper.applyTheme(theme)
    }

}