package dk.aau.student.b211.sleepattention;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Leon on 05.04.2016.
 */
public class RingtoneService extends Service {

    MediaPlayer media;
    String message;
    boolean isRunning;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("LocalService", "Receive start id" + startId + ":" + intent);

        //fetch the extra String Values
        String state = intent.getExtras().getString("extra", "alarm off");

        // assert = If there is a "NullPointerException", then do not go through the code below
        Log.e("Ringtone extra is", state);

        // assert state != null;
        //this converts the extra Strings from the intent to start ID's, value 0 or 1;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // if else - statements to either start or stop the ringtone
        //if there is no music playing, and the user pressed "Go to Sleep"
        //Music should start playing.
        if (!this.isRunning && startId == 1) {
            //Create an Instance of the MediaPlayer and start it
            media = MediaPlayer.create(this, R.raw.ringtone);
            media.start();
            this.isRunning = true;
            startId = 0;
            //notifications
            //set up the notifcation service
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //set up an Intent that goes to the SleepActivity
            Intent sleep_activity = new Intent(this.getApplicationContext(), SleepActivity.class);
            PendingIntent pending_intent_sleepactivity = PendingIntent.getActivity(this, 0, sleep_activity, 0);
            int icon = android.R.drawable.ic_popup_reminder;

            //make the notification parameters
            Notification notification_popup = new Notification.Builder(this)
                    .setContentTitle("Time to wake up!!!")
                    .setContentText("Press me!")
                    .setSmallIcon(icon)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                    .setContentIntent(pending_intent_sleepactivity)
                    .setAutoCancel(true)
                    .build();

            //set up the notification call command
            notificationmanager.notify(0, notification_popup);
        }

        //if there is music playing, and the user pressed "Wake up"
        //Music should stop playing.
        else if (this.isRunning && startId == 0) {
            Log.e("there is no music", "and it should not play");
            //stop the ringtone
            media.stop();
            media.reset();
            this.isRunning = false;
            startId = 0;
        }

        //these are if the user presses random buttons
        //bug-preventive

        //if there is no music playing, and the user pressed "Wake Up"
        //do nothing.
        else if (!this.isRunning && startId == 0) {
            Log.e("there is no music", "and it should not play");
            this.isRunning = false;
            startId = 0;
        }

        //if there is music playing, and the user pressed "Go to sleep"
        //do nothing.
        else if (this.isRunning && startId == 1) {
            Log.e("there is music", "and it should play");
            this.isRunning = true;
            startId = 1;
        }

        //catch the odd event.
        else {
            Log.e("else", "somehow you reached this");
        }

        return START_NOT_STICKY;
    }
}
