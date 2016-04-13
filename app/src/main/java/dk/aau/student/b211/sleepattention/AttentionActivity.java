package dk.aau.student.b211.sleepattention;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AttentionActivity extends AppCompatActivity {

    // Don't touch those
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();

    // Change to imageButton or w/e.
    ImageButton imageButton;
    Button button;
    TextView textView;

    boolean testRunning;
    boolean isActivated;
    boolean isWaiting;
    long startTime;
    long endTime;
    long[] reactionTime = new long[5];
    int currentTest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        button = (Button)findViewById(R.id.attention_starttest_button);
        textView = (TextView)findViewById(R.id.textView);
        imageButton = (ImageButton)findViewById(R.id.attention_circle_imageButton);

        textView.setText("");
        imageButton.setVisibility(View.INVISIBLE);

        isActivated = false;
        isWaiting = false;
    }


    public void pvtGame() {

        if (!isActivated && !isWaiting) {
            isWaiting = true;
            currentTest++;
            delayedActivate();
        }
    }

    public void startTest(View view) {
        if (!testRunning) {
            pvtGame();
            button.setVisibility(View.INVISIBLE);
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
                s = "Average Reaction Time: " + sum/reactionTime.length + "ms";
                textView.setText(s);
            }

        }
    }

    //Insert random number gen.
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

        // public void schedule (TimerTask task, long delay)
        t.schedule(mTimerTask, randomGenerator(3000,7000));  //

    }

    // What to do when stopping the task.
    public void stopTask(){
        /*
        if(mTimerTask!=null){

        }
         */
    }
}