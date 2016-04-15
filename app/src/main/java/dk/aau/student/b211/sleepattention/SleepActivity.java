package dk.aau.student.b211.sleepattention;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {


    private AlarmManager alarmmanager;
    private TimePicker alarmtimepicker;
    private int hours;
    private int minutes;
    private PendingIntent pendingintent;
    private SleepRepository sp;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        sp = new SleepRepository(SleepActivity.this);
        date = new Date();

        //Create an Intent to the Receiver Class
        final Intent receiver = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmmanager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        alarmtimepicker = (TimePicker) findViewById(R.id.sleep_timepicker);
        alarmtimepicker.setIs24HourView(true);

        // Initializing the two Buttons in activity_sleep.xml
        Button startsleep = (Button) findViewById(R.id.sleep_startlog_button);
        Button endsleep = (Button) findViewById(R.id.sleep_endlog_button);
        final SleepRepository database = new SleepRepository(SleepActivity.this);
        final Date date = new Date();
//        List<Sleep> sleepList = database.getAllRecords();
//        Log.v("MOINMOINMOIN", "HALLO HALLO HALLO");
//        Log.v("SleepActivity", sleepList.toString());
                // onClickListener for the two Buttons in activity_sleep.xml
                startsleep.setOnClickListener(new View.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.M)

                    @Override
                    public void onClick(View v) {
                        database.insertRecord(33.0, date, 33);
                        //Setting Calendar instance with the Hour and Minute that we have picked on the TimePicker
                        calendar.set(Calendar.HOUR_OF_DAY, alarmtimepicker.getHour());
                        calendar.set(Calendar.MINUTE, alarmtimepicker.getMinute());
                        Log.e(".getCurrentHour()", "value of alarmtimepicker.getCurrentHour()=" + alarmtimepicker.getHour());
                        Log.e(".getCurrentMinute()", "value of alarmtimepicker.getCurrentMinute()=" + alarmtimepicker.getMinute());

                        //Put in extra String into Receiver Intent
                        //Tells the Clock that you have pressed the "Go to Bed" button
                        receiver.putExtra("extra", "alarm on");

                        //Create a pending Intent that delays the Intent until the specified Calendar-Time
                        pendingintent = PendingIntent.getBroadcast(SleepActivity.this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);

                        //Set the AlarmManager
                        alarmmanager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingintent);
                    }
                });

        endsleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Put in extra String into Receiver Intent
                //Tells the clock that you have pressed the "Wake Up" button
                receiver.putExtra("extra", "alarm off");
                //Stop the Ringtone
                sendBroadcast(receiver);
                alarmmanager.cancel(pendingintent);
            }
        });

//        morningButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                sp.insertRecord(1, date, 45);
//                Toast.makeText(getApplicationContext(), "Saved to DB!", Toast.LENGTH_LONG).show();
//                System.out.println(date.toString());
//
//           }
//        });
    }
}
