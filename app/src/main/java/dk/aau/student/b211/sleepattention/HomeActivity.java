package dk.aau.student.b211.sleepattention;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private Context context;
    private Button sleepButton, attentionButton, statisticsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        findViews();

    }

    private void findViews() {
        sleepButton = (Button)findViewById(R.id.home_sleep_button);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SleepActivity.class);
                startActivity(i);
            }
        });

        attentionButton = (Button)findViewById(R.id.home_attention_button);
        attentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AttentionActivity.class);
                startActivity(i);
            }
        });

        statisticsButton = (Button)findViewById(R.id.home_statistics_button);
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, StatisticsActivity.class);
                startActivity(i);
            }
        });
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
            Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT).show();
            /*
            Intent i = new Intent(context, SettingsActivity.class);
            startActivity(i);
            return true;
            */
        }
        return super.onOptionsItemSelected(item);
    }
}
