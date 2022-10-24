package com.kejaplus.application.Support

import androidx.appcompat.app.AppCompatDelegate

enum class PhoneThemes {
        LIGHT, DARK, BATTERY_SAVER_MODE, DEFAULT
    }


    object ThemeHelper {
        fun applyTheme(theme: PhoneThemes) {
            when (theme) {
                PhoneThemes.LIGHT
                -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                PhoneThemes.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                PhoneThemes.BATTERY_SAVER_MODE -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                )
                PhoneThemes.DEFAULT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
