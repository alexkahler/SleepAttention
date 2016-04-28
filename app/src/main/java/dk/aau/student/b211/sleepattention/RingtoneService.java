package dk.aau.student.b211.sleepattention;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Leon on 05.04.2016.
 */
public class RingtoneService extends Service {

    private MediaPlayer media;
    private boolean isRunning = false;
    private static final String TAG = RingtoneService.class.getSimpleName();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int alarmStart) {

        Log.i(TAG, "Receive start id " + alarmStart + ":" + intent);

        //fetch the extra String Values
        boolean alarmState = intent.getExtras().getBoolean("turn alarm on");

        // assert = If there is a "NullPointerException", then do not go through the code below
        Log.d(TAG, "Alarm state is: " + alarmState);

        // assert state != null;
        //this converts the extra Strings from the intent to start ID's, value 0 or 1;
        if (alarmState) {
            alarmStart = 1;
        } else {
            alarmStart = 0;
        }

        // if else - statements to either start or stop the ringtone
        //if there is not running, and the alarm should start.
        if (!this.isRunning && alarmStart == 1) {
            //Create an Instance of the MediaPlayer and start it
            media = MediaPlayer.create(this, R.raw.ringtone);
            media.start();
            this.isRunning = true;
            alarmStart = 0;
            //notifications
            //set up the notification service
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //set up an Intent that goes to the SleepActivity
            Intent sleep_activity = new Intent(this.getApplicationContext(), SleepActivity.class);
            PendingIntent pending_intent_sleepActivity = PendingIntent.getActivity(this, 0, sleep_activity, 0);
            int icon = android.R.drawable.ic_popup_reminder;

            //make the notification parameters
            Notification notification_popup = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.ringtone_notification_title))
                    .setContentText(getString(R.string.ringtone_notification_text))
                    .setSmallIcon(icon)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                    .setContentIntent(pending_intent_sleepActivity)
                    .setAutoCancel(true)
                    .build();

            //set up the notification call command
            notificationmanager.notify(0, notification_popup);
        }
        //if there is alarm running, and the user pressed "Wake up"
        //Alarm should stop playing.
        else if (this.isRunning && alarmStart == 0) {
            Log.d(TAG, "Stopping alarm");
            //stop the ringtone
            media.stop();
            media.reset();
            this.isRunning = false;
        }

        //these are if the user presses random buttons
        //bug-preventive

        //if there is no alarm running, and the user pressed "Wake Up"
        //do nothing.
        else if (!this.isRunning && alarmStart == 0) {
            Log.d(TAG, "there is no logging and it should not run");
        }

        //if there is alarm running, and the user pressed "Go to sleep"
        //do nothing.
        else if (this.isRunning && alarmStart == 1) {
            Log.d(TAG, "Logging is running and it should");
        }

        //catch the odd event.
        else {
            Log.e(TAG, "Error happened: somehow we reached else-statement");
        }

        return START_NOT_STICKY;
    }
}
