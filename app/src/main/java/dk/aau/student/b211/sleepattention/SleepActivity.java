package dk.aau.student.b211.sleepattention;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SleepActivity extends AppCompatActivity {

    private static final String TAG = SleepActivity.class.getSimpleName();

    private PendingIntent pendingintent;
    private Calendar currentTime, alarmTime;
    private boolean isAlarmOn;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        //Create an Intent to the Receiver Class
        final Intent alarmReceiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final ComponentName receiver = new ComponentName(SleepActivity.this, AlarmReceiver.class);
        final PackageManager packageManager = SleepActivity.this.getPackageManager();
        final TimePicker alarmTimePicker = (TimePicker) findViewById(R.id.sleep_timepicker);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SleepActivity.this);
        isAlarmOn = sharedPreferences.getBoolean(getString(R.string.misc_is_alarm_on_key), false);

        alarmTime = Calendar.getInstance();
        alarmTime.clear(Calendar.SECOND);  //Sanitize seconds so alarm starts at whole minutes.
        alarmTime.add(Calendar.HOUR_OF_DAY, 8);
        alarmTimePicker.setCurrentHour(alarmTime.get(Calendar.HOUR_OF_DAY));
        alarmTimePicker.setCurrentMinute(alarmTime.get(Calendar.MINUTE));
        alarmTimePicker.setIs24HourView(true);

        // Initializing the two Buttons in activity_sleep.xml
        Button startSleep = (Button) findViewById(R.id.sleep_startlog_button);
        assert startSleep != null;
        final Button endSleep = (Button) findViewById(R.id.sleep_endlog_button);
        assert endSleep != null;

        if(!isAlarmOn) {
            //Disable endSleep button if there's no alarm set.
            endSleep.setEnabled(false);
        }

        startSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentTime = Calendar.getInstance();
                currentTime.clear(Calendar.SECOND);
                alarmTime = Calendar.getInstance();
                alarmTime.clear(Calendar.SECOND);
                //Setting Calendar instance with the Hour and Minute that we have picked on the TimePicker
                alarmTime.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                alarmTime.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                if (alarmTime.before(currentTime)) { //In case that the alarm has been set for the next day.
                    alarmTime.add(Calendar.HOUR_OF_DAY, 24);
                }

                //Save alarm time and sleep time to persistent cache in case app or device shutdown.
                int sleepOffset = Integer.parseInt(sharedPreferences.getString(getString(R.string.preferences_time_to_sleep_key), "15"));
                Log.d(TAG, "SleepOffset is: " + sleepOffset);
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                currentTime.add(Calendar.MINUTE, sleepOffset);
                preferencesEditor.putLong(getString(R.string.misc_sleep_time_key), currentTime.getTimeInMillis());
                preferencesEditor.putLong(getString(R.string.misc_alarm_time_key), alarmTime.getTimeInMillis());
                isAlarmOn = true;
                preferencesEditor.putBoolean(getString(R.string.misc_is_alarm_on_key), isAlarmOn);
                preferencesEditor.apply();

                //Tells the Clock that you have pressed the "Go to Bed" button
                alarmReceiverIntent.setAction(getString(R.string.ACTION_SET_ALARM));
                //Enable permission to receive boot completed broadcast
                packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


                //Create a pending Intent that delays the Intent until the specified Calendar-Time
                pendingintent = PendingIntent.getBroadcast(SleepActivity.this, 0, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //Set the AlarmManager
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingintent);

                //Show a message to user, stating eta to alarm goes off.
                long millis = alarmTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                String message = String.format(getString(R.string.sleep_alarm_set),
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                endSleep.setEnabled(true);
            }
        });

        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSleep.setEnabled(false);

                //Clear the alarm time in the persistent cache.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(getString(R.string.misc_alarm_time_key), 0);
                isAlarmOn = false;
                editor.putBoolean(getString(R.string.misc_is_alarm_on_key), isAlarmOn);
                editor.apply();

                //Tells the clock that you have pressed the "Wake Up" button
                alarmReceiverIntent.setAction(getString(R.string.ACTION_SET_ALARM));
                //Stop the Ringtone
                sendBroadcast(alarmReceiverIntent);
                alarmManager.cancel(pendingintent);
                //Disable permission to receive boot completed broadcast
                packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                //Calculate time slept
                currentTime = Calendar.getInstance();
                long duration = currentTime.getTimeInMillis() - sharedPreferences.getLong(getString(R.string.misc_sleep_time_key), 0);
                if (duration > 0) {
                    //Save to database.
                    new SleepRepository(SleepActivity.this).insertRecord(duration, currentTime.getTime(), 0);
                    //Show a message to user stating time slept.
                    String message = String.format(getString(R.string.sleep_log_stop),
                            TimeUnit.MILLISECONDS.toHours(duration),
                            TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SleepActivity.this, getString(R.string.sleep_negative_sleep), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
