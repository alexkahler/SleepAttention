package dk.aau.student.b211.sleepattention;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class is used to receive alarm state from the SleepActivity.
 * @author Group B211, Aalborg University on 05.04.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(TAG, "We are in the receiver - yay");

        //fetch extra boolean from intent
        boolean alarmState = intent.getExtras().getBoolean("turn alarm on");

        Log.v(TAG, "Alarm state is: " + alarmState);

        //Create an Intent for the RingtoneService
        Intent service_intent = new Intent(context, RingtoneService.class);

        //pass the extra Strings from SleepActivity to the Ringtone Playing Service
        service_intent.putExtra("turn alarm on", alarmState);

        //Start the Ringtone Service
        context.startService(service_intent);
    }
}
