package com.example.final_puffandpoof

import android.content.Context
import android.content.SharedPreferences

object SyncStatus {
    private const val PREF_NAME = "sync_status"
    private const val KEY_SYNCED = "synced"

    fun setSynced(context: Context, synced: Boolean) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_SYNCED, synced).apply()
    }

    fun isSynced(context: Context): Boolean {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_SYNCED, false)
    }
}
