package dk.aau.student.b211.sleepattention;

import android.content.Intent;
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

    private final Handler handler = new Handler();
    private final Timer t = new Timer();
    private AttentionRepository ar;
    private ImageButton imageButton;
    private Button button;
    private TextView textView;

    private boolean testRunning;
    private boolean isActivated;
    private boolean isWaiting;
    private long startTime;
    private final long[] reactionTime = new long[5];
    private int currentTest = 0;

    private static final int MIN_TEST_DELAY = 3000;
    private static final int MAX_TEST_DELAY = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        ar = new AttentionRepository(getApplicationContext());
        button = (Button)findViewById(R.id.attention_starttest_button);
        textView = (TextView)findViewById(R.id.textView);
        imageButton = (ImageButton)findViewById(R.id.attention_circle_imageButton);
        textView.setText("");
        imageButton.setVisibility(View.INVISIBLE);
        isActivated = false;
        isWaiting = false;
        testRunning = false;
    }


    private void pvtGame() {
        if (!isActivated && !isWaiting) {
            button.setText(R.string.attention_goBack_button);
            isWaiting = true;
            currentTest++;
            delayedActivate();
        }
    }

    private void startTest(View view) {
        if (!testRunning) {
            pvtGame();
            //button.setBackgroundColor(Color.GRAY);
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
            } else {
                s = getString(R.string.attention_reaction_time_prefix_text) + reactionTime[currentTest - 1] + getString(R.string.attention_reaction_time_suffix_text);
            }
            textView.setText(s);

            isWaiting = false;
            isActivated = false;

            if (currentTest < reactionTime.length) {
                pvtGame();
            } else {
                long sum = 0;
                for (long aReactionTime : reactionTime) {
                    sum += aReactionTime;
                }
                int averageReactionTime = ((int)sum/reactionTime.length);

                // User Feedback on reaction time.
                if (averageReactionTime < 200) {
                    s = getString(R.string.attention_score_200_prefix) + averageReactionTime + getString(R.string.attention_score_suffix);
                } else if (averageReactionTime >= 200 && averageReactionTime < 300) {
                    s = getString(R.string.attention_score_300_prefix) + averageReactionTime + getString(R.string.attention_score_suffix);
                } else if (averageReactionTime >= 300 && averageReactionTime < 400) {
                    s = getString(R.string.attention_score_400_prefix) + averageReactionTime + getString(R.string.attention_score_suffix);
                } else if (averageReactionTime >= 400 && averageReactionTime < 500) {
                    s = getString(R.string.attention_score_500_prefix) + averageReactionTime + getString(R.string.attention_score_suffix);
                } else if (averageReactionTime >= 500) {
                    s = getString(R.string.attention_score_above500_prefix) + averageReactionTime + getString(R.string.attention_score_suffix);
                }

                textView.setText(s);
                ar.insertRecord(new Date(), averageReactionTime);
            }
        }
    }

    private int randomGenerator(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(min) + (max-min);
    }

    private void delayedActivate(){

        TimerTask mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // Insert what the task should do.
                        textView.setVisibility(View.INVISIBLE);
                        imageButton.setVisibility(View.VISIBLE);

                        startTime = System.currentTimeMillis();
                        isActivated = true;
                    }
                });
            }
        };
        t.schedule(mTimerTask, randomGenerator(MIN_TEST_DELAY, MAX_TEST_DELAY));

    }
}