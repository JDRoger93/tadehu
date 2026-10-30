package com.funtadehu.autosugestion.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.funtadehu.autosugestion.data.AutosuggestionPreferences
import com.funtadehu.autosugestion.scheduler.NotificationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val prefs = AutosuggestionPreferences(context)
            if (!prefs.isRestMode()) {
                NotificationScheduler.scheduleNext(context)
            }
        }
    }
}

