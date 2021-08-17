package com.niran.newsapplication.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

fun Context.setSharedPrefString(prefsName: String, key: String, value: String) {
    getSharedPreferences(prefsName, MODE_PRIVATE).edit().apply { putString(key, value); apply() }
}

fun Context.getSharedPrefString(prefsName: String, key: String, defaultValue: String): String =
    with(getSharedPreferences(prefsName, MODE_PRIVATE)) {
        getString(key, defaultValue) ?: defaultValue
    }