package com.funtadehu.autosugestion.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.funtadehu.autosugestion.data.AutosuggestionPreferences
import com.funtadehu.autosugestion.scheduler.NotificationScheduler

class AutosuggestionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = AutosuggestionPreferences(context)
        if (!prefs.isRestMode()) {
            val randomPhrase = prefs.getRandomPhrase()
            NotificationScheduler.showNotification(context, randomPhrase)
            NotificationScheduler.scheduleNext(context)
        }
    }
}

