package com.funtadehu.autosugestion.scheduler

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.funtadehu.autosugestion.MainActivity
import com.funtadehu.autosugestion.R
import com.funtadehu.autosugestion.data.AutosuggestionPreferences
import com.funtadehu.autosugestion.receiver.AutosuggestionReceiver
import kotlin.random.Random

object NotificationScheduler {

    const val CHANNEL_ID = "autosuggestion_channel"
    private const val ALARM_REQUEST_CODE = 1001
    private const val NOTIFICATION_ID = 2001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Frases de Autosugestión"
            val descriptionText = "Notificaciones aleatorias durante el día con frases de autosugestión"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNext(context: Context) {
        val prefs = AutosuggestionPreferences(context)
        if (prefs.isRestMode()) {
            cancel(context)
            return
        }

        val intervalMin = prefs.getIntervalMinutes()
        val delayMillis: Long = if (intervalMin <= 1) {
            // Test mode: 30 seconds
            30_000L
        } else {
            // Random jitter around base interval (+/- 30%)
            val minMin = (intervalMin * 0.7).toInt().coerceAtLeast(3)
            val maxMin = (intervalMin * 1.3).toInt()
            val randomMin = Random.nextInt(minMin, maxMin + 1)
            randomMin * 60 * 1000L
        }

        val triggerAt = SystemClock.elapsedRealtime() + delayMillis
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutosuggestionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Fallback for strict alarm permission policies
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutosuggestionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(context: Context, phrase: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.logo_funtadehu)
        } catch (e: Exception) {
            null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_funtadehu)
            .setLargeIcon(largeIcon)
            .setContentTitle("Autosugestión FunTaDeHu ✨")
            .setContentText(phrase)
            .setStyle(NotificationCompat.BigTextStyle().bigText(phrase))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}

