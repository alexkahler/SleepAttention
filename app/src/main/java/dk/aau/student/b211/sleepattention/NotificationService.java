package dk.aau.student.b211.sleepattention;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("NotificationService", "Received onStart with intent: " + intent + " and startId " + startId);

        if(startId == 1) {
            startId = 0;
            //notifications
            //set up the notification service
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //set up an Intent that goes to the SleepActivity
            Intent attentionActivity = new Intent(this.getApplicationContext(), AttentionActivity.class);

            //Build stack for back button navigation.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(AttentionActivity.class);
            stackBuilder.addNextIntent(attentionActivity);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            //make the notification parameters
            Notification notification_popup = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.notification_reminder_title))
                    .setContentText(getString(R.string.notification_reminder_text))
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .build();

            //set up the notification call command
            notificationmanager.notify(1, notification_popup);
        }
        return START_NOT_STICKY;
    }
}