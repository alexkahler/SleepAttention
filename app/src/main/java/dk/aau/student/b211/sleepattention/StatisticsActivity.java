package dk.aau.student.b211.sleepattention;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        CombinedChart combinedChart = (CombinedChart) findViewById(R.id.chart);

        CombinedData data = new CombinedData(getXAxisValues());
        data.setData(barData());
        data.setData(lineData());
        combinedChart.setData(data);
        combinedChart.setDescription("Sleep VS Attention");


    }
    // creating list of x-axis labels to show the date of the entry.

    private ArrayList<String> getXAxisValues() {

        ArrayList<String> labels = new ArrayList<>();

        SleepRepository sp = new SleepRepository(this);
        AttentionRepository ap = new AttentionRepository(this);

        // creating the list of x-axis labels based on the repository with the highest number of records/entries.
        if(sp.getAllRecords().size() <= ap.getAllRecords().size()){
            for(int i = 0; i < (sp.getAllRecords().size()) ; i++){
                labels.add(sp.getRecord(i).getDate()+"");
            }
        }
        else{
            for(int i = 0; i < (ap.getAllRecords().size()) ; i++){
                labels.add(ap.getRecord(i).getDate()+"");
                }
            }

        return labels;

    }

    // this method is used to create data for line graph representing PVT score
    public LineData lineData(){

        ArrayList<Entry> line = new ArrayList<>();

        AttentionRepository ap = new AttentionRepository(this);

        // Checks the date of the previous record to see if it's equal to this. Excluding HH:mm:ss
        for(int i = 0; i < ap.getAllRecords().size(); i++) {
                if ((ap.getRecord(i).getDate().toString().regionMatches(0, ap.getRecord(i - 1).getDate().toString(), 0, 10)) && (i != 0)) {
                    ap.deleteRecord(i-1);
//                    int updatedScore = (ap.getRecord(i).getScore() + ap.getRecord(i - 1).getScore()) / 2;
//                    ap.getRecord(i).setScore(updatedScore);
//                    ArrayList<Integer> sameDateScores = new ArrayList<>();
//                    sameDateScores.add(ap.getRecord(i).getScore());
                }
            }

            // add the score entries to the line graph dataset.
        for(int j = 0; j < ap.getAllRecords().size() ; j++){
            line.add(new Entry(ap.getRecord(j).getScore(),j));

        }

        LineDataSet lineDataSet = new LineDataSet(line, "PVT score");
        lineDataSet.setColor(Color.rgb(0, 0, 225));
        lineDataSet.setCircleColor(Color.rgb(0, 0, 255));
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setValueTextSize(8f);

        LineData lineData = new LineData(getXAxisValues(),lineDataSet);

        return lineData;

    }
    // this method is used to create data for Bar graph representing hours slept
    public BarData barData(){

        ArrayList<BarEntry> sleepBar = new ArrayList<>();

        SleepRepository sp = new SleepRepository(this);

        for(int i = 0; i < (sp.getAllRecords().size()) ; i++){
            float sleepDur = (float)sp.getRecord(i).getDuration();
            sleepBar.add(new BarEntry(sleepDur,i));
        }

        BarDataSet barDataSet = new BarDataSet(sleepBar, "Hours Slept");
        barDataSet.setColor(Color.rgb(0, 225, 0));
        barDataSet.setValueTextSize(8f);

        BarData barData = new BarData(getXAxisValues(),barDataSet);

        return barData;

    }
}
