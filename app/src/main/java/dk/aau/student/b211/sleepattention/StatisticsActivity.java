package dk.aau.student.b211.sleepattention;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private List<Sleep> sleepList = new ArrayList<>();
    private List<Attention> attentionList = new ArrayList<>();
    private static final int MILLIS_PR_HOUR = 3600000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        SleepRepository sleepRepository = new SleepRepository(this);
        AttentionRepository attentionRepository = new AttentionRepository(this);
        sleepList = sleepRepository.getAllRecords();
        attentionList = attentionRepository.getAllRecords();
        if(sleepList.size() == 0 ||attentionList.size() == 0) {
            return; //TODO: Implement more elegant solution of showing no available data.
        }

        CombinedChart combinedChart = (CombinedChart) findViewById(R.id.chart);

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f);

        CombinedData data = new CombinedData(getXAxisValues());
        data.setData(barData());
        data.setData(lineData());
        combinedChart.setData(data);
        combinedChart.setDescription(getString(R.string.statistics_chart_description));


    }

    /**
     *
     * @return
     */
    private ArrayList<String> getXAxisValues() {

        ArrayList<String> labels = new ArrayList<>();

        // creating the list of x-axis labels based on the repository with the highest number of records/entries.
        if(sleepList.size() >= attentionList.size()){
            for(Sleep s : sleepList) {
                labels.add(s.getDate().toString());
            }
        }
        else {
            for (Attention a : attentionList) {
                labels.add(a.getDate().toString());
            }
        }
        return labels;
    }

    /**
     *
     * @return
     */
    private LineData lineData(){
        ArrayList<Entry> line = new ArrayList<>();
        // add the score entries to the line graph dataset.
        int i = 0;
        for(Attention a : attentionList) {
            line.add(new Entry(a.getScore(), i));
            i++;
        }
        LineDataSet lineDataSet = new LineDataSet(line, getString(R.string.statistics_line_description));
        lineDataSet.setColor(Color.rgb(0, 0, 225));
        lineDataSet.setCircleColor(Color.rgb(0, 0, 255));
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setValueTextSize(8f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        return new LineData(getXAxisValues(),lineDataSet);

    }

    /**
     *
     * @return
     */
    private BarData barData(){
        ArrayList<BarEntry> sleepBar = new ArrayList<>();
        int i = 0;
        for(Sleep s : sleepList){
            /*sleepBar.add(new BarEntry(
                    TimeUnit.MILLISECONDS.toHours((long)s.getDuration()), i));*/
            sleepBar.add(new BarEntry((float)s.getDuration() / MILLIS_PR_HOUR, i)); //Divide by 3.6 million, as that is the amount of millis pr. hour.
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(sleepBar, getString(R.string.statistics_bar_description));
        barDataSet.setColor(Color.rgb(0, 225, 0));
        barDataSet.setValueTextSize(8f);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return new BarData(getXAxisValues(),barDataSet);

    }
}
