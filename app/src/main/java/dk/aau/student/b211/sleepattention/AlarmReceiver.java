package dk.aau.student.b211.sleepattention;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to receive alarm state from the SleepActivity.
 * @author Group B211, Aalborg University on 05.04.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "We are in the receiver - yay");
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean alarmState = sharedPreferences.getBoolean(context.getString(R.string.misc_is_alarm_on_key), false);
        boolean notificationsEnabled = sharedPreferences.getBoolean(context.getString(R.string.preferences_notifications_enabled_key), false);
        Intent service_intent = new Intent(context, RingtoneService.class);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(alarmState) {
                long alarmTime = sharedPreferences.getLong(context.getString(R.string.misc_alarm_time_key), 0);
                if (alarmTime > 1) {
                    Log.d(TAG, "Boot completed - turning on alarm");
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    service_intent.putExtra("turn alarm on", alarmState);
                    PendingIntent alarmIntent = PendingIntent.getService(context, 0, service_intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
                }
            }
            setNotifications(notificationsEnabled);

        }

        else if (intent.getAction().equals(context.getString(R.string.ACTION_SET_ALARM))) {
            service_intent.putExtra("turn alarm on", alarmState);
            //Start the Ringtone Service
            context.startService(service_intent);
        }

        else if (intent.getAction()
                .equals(context.getString(R.string.ACTION_SET_NOTIFICATIONS))) {
            notificationsEnabled = sharedPreferences
                    .getBoolean(context.getString(R.string.preferences_notifications_enabled_key), false);
            setNotifications(notificationsEnabled);
        }
    }

    private void setNotifications(boolean enabled) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notificationIntent, 0);
        if (enabled) {
            alarmManager.cancel(pendingIntent);
            Calendar calendar = Calendar.getInstance();
            Calendar savedTime = Calendar.getInstance();
            savedTime.setTimeInMillis(sharedPreferences.getLong(context.getString(R.string.preferences_notification_time_key), 0));
            calendar.set(Calendar.HOUR_OF_DAY, savedTime.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, savedTime.get(Calendar.MINUTE));
            calendar.clear(Calendar.SECOND);
            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else {
            alarmManager.cancel(pendingIntent);
        }
    }
}
