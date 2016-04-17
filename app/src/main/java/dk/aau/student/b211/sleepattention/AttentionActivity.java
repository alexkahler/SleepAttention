package dk.aau.student.b211.sleepattention;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
TODO: Give user score feedback based on reaction time. Eg: "Great! Your average reaction time was 320ms!"

DONE:
and then give user button to Exit PVT activity.
 */

public class AttentionActivity extends AppCompatActivity {

    private TimerTask mTimerTask;
    private final Handler handler = new Handler();
    private Timer t = new Timer();
    private AttentionRepository ar;
    private ImageButton imageButton;
    private Button button;
    private TextView textView;

    private boolean testRunning;
    private boolean isActivated;
    private boolean isWaiting;
    private long startTime;
    private long endTime;
    private long[] reactionTime = new long[5];
    private int currentTest = 0;

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


    public void pvtGame() {

        if (!isActivated && !isWaiting) {
            button.setText(R.string.attentionactivity_button_goback);
            isWaiting = true;
            currentTest++;
            delayedActivate();
        }
    }

    public void startTest(View view) {
        if (!testRunning) {
            pvtGame();
            button.setBackgroundColor(Color.GRAY);
            testRunning = true;
        } else {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        }
    }

    public void onClick(View view) {
        if (isActivated) {
            imageButton.setVisibility(View.INVISIBLE);
            endTime = System.currentTimeMillis();
            reactionTime[currentTest - 1] = endTime - startTime;
            textView.setVisibility(View.VISIBLE);
            String s;
            if (reactionTime[currentTest - 1] <= 100) {
                currentTest--;
                s = "Reaction Time: Ignored";
            } else {
                s = "Reaction Time: " + reactionTime[currentTest - 1] + "ms";
            }
            textView.setText(s);

            isWaiting = false;
            isActivated = false;
            if (currentTest < reactionTime.length) {
                pvtGame();
            } else {
                long sum = 0;
                for (int i = 0; i < reactionTime.length; i++){
                    sum += reactionTime[i];
                }
                int averageReactionTime = ((int)sum/reactionTime.length);

                // User Feedback on reaction time.
                if (averageReactionTime < 200) {
                    s = "Excellent job! Your average reaction time was " + averageReactionTime + "ms!";
                } else if (averageReactionTime >= 200 && averageReactionTime < 300) {
                    s = "Great job! Your average reaction time was " + averageReactionTime + "ms!";
                } else if (averageReactionTime >= 300 && averageReactionTime < 400) {
                    s = "Well done! Your average reaction time was " + averageReactionTime + "ms!";
                } else if (averageReactionTime >= 400 && averageReactionTime < 500) {
                    s = "You did okay. Your average reaction time was " + averageReactionTime + "ms!";
                } else if (averageReactionTime >= 500) {
                    s = "Are you sure you are getting enough sleep? Your average reaction time was " + averageReactionTime + "ms!";
                }

                textView.setText(s);
                button.setBackgroundColor(Color.WHITE);
                ar.insertRecord(0, new Date(), averageReactionTime);

            }

        }
    }

    public int randomGenerator(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(min) + (max-min);
    }

    public void delayedActivate(){

        mTimerTask = new TimerTask() {
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
            }};
        t.schedule(mTimerTask, randomGenerator(3000,7000));  //

    }
}