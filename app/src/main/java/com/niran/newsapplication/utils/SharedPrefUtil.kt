package com.niran.newsapplication.utils

import android.content.Context

class SharedPrefUtil {
    companion object {
        fun Context.setSharedPrefString(prefsName: String, key: String, value: String) =
            getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit()
                .apply { putString(key, value); apply() }

        fun Context.getSharedPrefString(prefsName: String, key: String, defaultValue: String) =
            with(getSharedPreferences(prefsName, Context.MODE_PRIVATE)) {
                getString(key, defaultValue) ?: defaultValue
            }
    }
}