package dk.aau.student.b211.sleepattention;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SleepActivity extends AppCompatActivity {


    private AlarmManager alarmmanager;
    private TimePicker alarmtimepicker;
    private PendingIntent pendingintent;
    private SleepRepository sleepRepository;
    private Calendar currentCalendar, alarmTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        sleepRepository = new SleepRepository(getApplicationContext());
        //Create an Intent to the Receiver Class
        final Intent alarmReceiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmmanager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmTime = Calendar.getInstance(); //Get current time to set alarm from.
        alarmTime.clear(Calendar.SECOND);  //Sanitize seconds so alarm starts at whole minutes.
        alarmtimepicker = (TimePicker) findViewById(R.id.sleep_timepicker);
        alarmtimepicker.setIs24HourView(true);

        // Initializing the two Buttons in activity_sleep.xml
        Button startSleep = (Button) findViewById(R.id.sleep_startlog_button);
        startSleep.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                currentCalendar = Calendar.getInstance();
                //Setting Calendar instance with the Hour and Minute that we have picked on the TimePicker
                alarmTime.set(Calendar.HOUR_OF_DAY, alarmtimepicker.getCurrentHour());
                alarmTime.set(Calendar.MINUTE, alarmtimepicker.getCurrentMinute());
                if(alarmTime.before(currentCalendar)) { //In case that the alarm has been set for the next day.
                    alarmTime.add(Calendar.HOUR_OF_DAY, 24);
                }

                Log.d(".getCurrentHour()", "value of alarmtimepicker.getCurrentHour()=" + alarmtimepicker.getCurrentHour());
                Log.d(".getCurrentMinute()", "value of alarmtimepicker.getCurrentMinute()=" + alarmtimepicker.getCurrentMinute());

                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.putLong("startTime", Calendar.getInstance().getTimeInMillis());
                preferencesEditor.apply();

                //Put in extra String into Receiver Intent
                //Tells the Clock that you have pressed the "Go to Bed" button
                alarmReceiverIntent.putExtra("turn alarm on", true);
                //Create a pending Intent that delays the Intent until the specified Calendar-Time
                pendingintent = PendingIntent.getBroadcast(SleepActivity.this, 0, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Set the AlarmManager
                alarmmanager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingintent);

                //Show a message to user, stating time to alarm goes off.
                long millis = alarmTime.getTimeInMillis() - currentCalendar.getTimeInMillis();
                String message = String.format(new Locale("da", "DK"), "Alarm set to %02d:%02d:%02d from now.", //TODO: Use Strings resources for localization.
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        Button endSleep = (Button) findViewById(R.id.sleep_endlog_button);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Put in extra String into Receiver Intent
                //Tells the clock that you have pressed the "Wake Up" button
                alarmReceiverIntent.putExtra("turn alarm on", false);
                //Stop the Ringtone
                sendBroadcast(alarmReceiverIntent);
                alarmmanager.cancel(pendingintent);

                currentCalendar = Calendar.getInstance();
                long endTime = currentCalendar.getTimeInMillis();
                long duration = endTime - getPreferences(Context.MODE_PRIVATE).getLong("startTime",0);
                sleepRepository.insertRecord(duration, currentCalendar.getTime(), 0);

                //Show a message to user stating time slept.
                String message = String.format(new Locale("da", "DK"), "Logging stopped! You've slept for %02d:%02d:%02d.", //TODO: Use Strings resources for localization.
                        TimeUnit.MILLISECONDS.toHours(duration),
                        TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
