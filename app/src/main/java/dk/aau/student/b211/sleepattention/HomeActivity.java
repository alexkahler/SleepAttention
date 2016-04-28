package dk.aau.student.b211.sleepattention;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private Context context;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int EXTERNAL_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
    }

    private void findViews() {
        Thread loadRecordsThread = new Thread(){
            @Override
            public void run() {
                List<Sleep> sleepList = new SleepRepository(context).getAllRecords();
                if (sleepList.size() != 0) {
                    TextView lastSleepText = (TextView) findViewById(R.id.home_last_sleep_text);
                    setText(lastSleepText, String.format(getString(R.string.home_last_sleep_text),
                            TimeUnit.MILLISECONDS.toHours((long)(new SleepRepository(context).getLatestRecord().getDuration()))));
                    TextView averageSleepText = (TextView) findViewById(R.id.home_average_sleep_text);
                    double averageSleepTime = 0;
                    int entries = 0;
                    for(int i = 0; i < 7 && i < sleepList.size(); i++) {
                        averageSleepTime =+ sleepList.get(i).getDuration();
                        entries++;
                    }
                    averageSleepTime = averageSleepTime/entries;
                    setText(averageSleepText, String.format(getString(R.string.home_average_sleep_text),
                            entries,
                            TimeUnit.MILLISECONDS.toHours((long)averageSleepTime),
                            TimeUnit.MILLISECONDS.toMinutes((long)averageSleepTime) % TimeUnit.HOURS.toMinutes(1)));
                }

            }
        };
        loadRecordsThread.setName("Load Sleep records");
        loadRecordsThread.start();

        Thread loadAttentionThread = new Thread(){
            @Override
            public void run() {
                if (new AttentionRepository(context).getAllRecords().size() != 0) {
                    TextView lastScore = (TextView)findViewById(R.id.home_last_score_text);
                    setText(lastScore, String.format(getString(R.string.home_last_score),
                            new AttentionRepository(context).getLatestRecord().getScore()));
                }
            }
        };
        loadAttentionThread.setName("Load Attention records");
        loadAttentionThread.start();

        Button sleepButton = (Button) findViewById(R.id.home_sleep_button);
        assert sleepButton != null;
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SleepActivity.class);
                startActivity(i);
            }
        });

        Button attentionButton = (Button) findViewById(R.id.home_attention_button);
        assert attentionButton != null;
        attentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AttentionActivity.class);
                startActivity(i);
            }
        });

        Button statisticsButton = (Button) findViewById(R.id.home_statistics_button);
        assert statisticsButton != null;
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, StatisticsActivity.class);
                startActivity(i);
            }
        });

        TextView welcomeText = (TextView) findViewById(R.id.home_welcome_text);
        assert welcomeText != null;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback: {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_PERMISSION_REQUEST_CODE);
                    }
                    else {
                        sendDatabase();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.home_error_writing_external), Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.action_settings: {
                Intent i = new Intent(context, SettingsActivity.class);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    sendDatabase();
                else
                    Toast.makeText(HomeActivity.this, getString(R.string.home_error_permissions), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendDatabase() {
        final File currentDB = getApplication().getDatabasePath(DatabaseHelper.DATABASE_NAME).getAbsoluteFile();
        final File backupDB = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DatabaseHelper.DATABASE_NAME);
        if (currentDB.exists()) {
            Thread moveDatabaseThread = new Thread() {
                @Override
                public void run() {
                    try {
                        FileChannel source = new FileInputStream(currentDB).getChannel();
                        FileChannel destination = new FileOutputStream(backupDB).getChannel();
                        destination.transferFrom(source, 0, source.size());
                        source.close();
                        destination.close();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Couldn't find the file: " + e.getMessage());
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, "IOException: " + e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                }
            };
            moveDatabaseThread.start();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"akahle15@student.aau.dk"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Database for PVT");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backupDB));
            try {
                moveDatabaseThread.join();
                startActivity(Intent.createChooser(intent, getString(R.string.home_choose_intent_resolver)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.home_error_no_email_client), Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread got interrupted: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(), getString(R.string.home_error_no_database), Toast.LENGTH_SHORT).show();
    }

    private void setText(final TextView textView, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) {
                    textView.setText(text);
                }
            }
        });
    }
}
