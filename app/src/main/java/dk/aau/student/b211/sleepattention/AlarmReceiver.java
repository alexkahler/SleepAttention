package dk.aau.student.b211.sleepattention;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Leon on 05.04.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(TAG, "We are in the receiver - yay");


        //fetch extra Strings from intent
        String get_your_string = intent.getExtras().getString("extra");


        Log.e(TAG, get_your_string);


        //Create an Intent for the RingtoneService
        Intent service_intent = new Intent(context, RingtoneService.class);


        //pass the extra Strings from SleepActivity to the Ringtone Playing Service
        service_intent.putExtra("extra", get_your_string);


        //Start the Ringtone Service
        context.startService(service_intent);

    }
}
