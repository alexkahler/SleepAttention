package dk.aau.student.b211.sleepattention;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AttentionActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private Button startTestButton;
    private TextView textView;

    private boolean testRunning;
    private boolean isActivated;
    private boolean isWaiting;
    private long startTime;
    private int currentTest = 0;

    private final long[] reactionTime = new long[5];
    private static final int MIN_TEST_DELAY = 3000;
    private static final int MAX_TEST_DELAY = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        textView = (TextView)findViewById(R.id.textView);
        imageButton = (ImageButton)findViewById(R.id.attention_circle_imageButton);
        textView.setText(getString(R.string.attention_textview_default));
        imageButton.setVisibility(View.INVISIBLE);
        isActivated = false;
        isWaiting = false;
        testRunning = false;
        startTestButton = (Button)findViewById(R.id.attention_starttest_button);
        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
    }


    private void pvtGame() {
        if (!isActivated && !isWaiting) {
            startTestButton.setText(R.string.attention_goBack_button);
            isWaiting = true;
            currentTest++;
            delayedActivate();
        }
    }

    private void startTest() {
        if (!testRunning) {
            textView.setText(getString(R.string.attention_get_ready_text));
            pvtGame();
            testRunning = true;
        } else {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        }
    }

    public void onClick(View view) { //TODO: Make user feedback for starting test eg. "Get ready".
        if (isActivated) {
            imageButton.setVisibility(View.INVISIBLE);
            long endTime = System.currentTimeMillis();
            reactionTime[currentTest - 1] = endTime - startTime;
            textView.setVisibility(View.VISIBLE);
            String s;
            if (reactionTime[currentTest - 1] <= 100) {
                currentTest--;
                s = getString(R.string.attention_reaction_ignored_text);
            }
            else {
                s = String.format(getString(R.string.attention_reaction_time_text), reactionTime[currentTest - 1]);
            }
            textView.setText(s);

            isWaiting = false;
            isActivated = false;

            if (currentTest < reactionTime.length) {
                pvtGame();
            }
            else {
                long sum = 0;
                for (long aReactionTime : reactionTime) {
                    sum += aReactionTime;
                }
                int averageReactionTime = ((int)sum/reactionTime.length);

                // User Feedback on reaction time.
                if (averageReactionTime < 200) {
                    s = String.format(getString(R.string.attention_score_200), averageReactionTime);
                } else if (averageReactionTime >= 200 && averageReactionTime < 300) {
                    s = String.format(getString(R.string.attention_score_300), averageReactionTime);
                } else if (averageReactionTime >= 300 && averageReactionTime < 400) {
                    s = String.format(getString(R.string.attention_score_400), averageReactionTime);
                } else if (averageReactionTime >= 400 && averageReactionTime < 500) {
                    s = String.format(getString(R.string.attention_score_500), averageReactionTime);
                } else if (averageReactionTime >= 500) {
                    s = String.format(getString(R.string.attention_score_above500), averageReactionTime);
                }

                textView.setText(s);
                new AttentionRepository(getApplicationContext()).insertRecord(new Date(), averageReactionTime);
            }
        }
    }

    private int randomGenerator(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(min) + (max-min);
    }

    private void delayedActivate(){
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setVisibility(View.INVISIBLE);
                        imageButton.setVisibility(View.VISIBLE);
                    }
                });
                startTime = System.currentTimeMillis();
                isActivated = true;
                t.cancel();
            }
        }, randomGenerator(MIN_TEST_DELAY, MAX_TEST_DELAY));
    }
}