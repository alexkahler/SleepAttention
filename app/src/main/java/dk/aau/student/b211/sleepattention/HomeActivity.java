package dk.aau.student.b211.sleepattention;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private Context context;
    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
    }

    private void findViews() {
        Button sleepButton = (Button) findViewById(R.id.home_sleep_button);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SleepActivity.class);
                startActivity(i);
            }
        });

        Button attentionButton = (Button) findViewById(R.id.home_attention_button);
        attentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AttentionActivity.class);
                startActivity(i);
            }
        });

        Button statisticsButton = (Button) findViewById(R.id.home_statistics_button);
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, StatisticsActivity.class);
                startActivity(i);
            }
        });

        TextView welcomeText = (TextView) findViewById(R.id.home_welcome_text);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour < 5)
            welcomeText.setText(getString(R.string.home_welcome_goodnight_text));
        else if (currentHour < 10)
            welcomeText.setText(getString(R.string.home_welcome_morning_text));
        else if (currentHour < 14)
            welcomeText.setText(getString(R.string.home_welcome_good_day_text));
        else if (currentHour < 18)
            welcomeText.setText(getString(R.string.home_welcome_good_afternoon_text));
        else if (currentHour < 21)
            welcomeText.setText(getString(R.string.home_welcome_good_evening_text));
        else
            welcomeText.setText(getString(R.string.home_welcome_goodnight_text));

        if (new SleepRepository(context).getAllRecords().size() != 0) {
            TextView lastSleep = (TextView) findViewById(R.id.home_last_sleep_text);
            lastSleep.setText(String.format(getString(R.string.home_last_sleep_text),
                    TimeUnit.MILLISECONDS.toHours((long)(new SleepRepository(context).getLatestRecord().getDuration()))));

            TextView averageSleepText = (TextView) findViewById(R.id.home_average_sleep_text);
            double averageSleepTime = 0;
            List<Sleep> sleepList = new SleepRepository(context).getAllRecords();
            int entries = 0;
            for(int i = 0; i < 7 && i < sleepList.size(); i++) {
                averageSleepTime =+ sleepList.get(i).getDuration();
                entries++;
            }
            averageSleepTime = averageSleepTime/entries;
            averageSleepText.setText(String.format(getString(R.string.home_average_sleep_text),
                    entries,
                    TimeUnit.MILLISECONDS.toHours((long)averageSleepTime),
                    TimeUnit.MILLISECONDS.toMinutes((long)averageSleepTime) % TimeUnit.HOURS.toMinutes(1)));
        }
        if (new AttentionRepository(context).getAllRecords().size() != 0) {
            TextView lastScore = (TextView)findViewById(R.id.home_last_score_text);
            lastScore.setText(String.format(getString(R.string.home_last_score),
                    new AttentionRepository(context).getLatestRecord().getScore()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show();
            /*
            TODO: Settings Activity
            Intent i = new Intent(context, SettingsActivity.class);
            startActivity(i);
            return true;
            */
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"akahle15@student.aau.dk"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for SleepAttention");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = Uri.fromFile(getApplicationContext().getDatabasePath(DatabaseHelper.DATABASE_NAME));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            try {
                startActivityForResult(Intent.createChooser(intent, "Send some feedback..."), 1);
            } catch(ActivityNotFoundException e) {
                Toast.makeText(context, "No email client installed", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
