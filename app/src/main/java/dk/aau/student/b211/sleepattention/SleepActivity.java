package dk.aau.student.b211.sleepattention;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;

public class SleepActivity extends AppCompatActivity {
    Date today;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        new SleepRepository(SleepActivity.this).insertRecord(10,today,5);


    }

}
