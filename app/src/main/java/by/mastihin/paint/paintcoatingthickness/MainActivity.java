package by.mastihin.paint.paintcoatingthickness;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends Activity implements MainView {

    public static final int REQUEST_CODE = 123;
    @BindView(R.id.state)
    TextView stateText;

    @BindView(R.id.phaseValue)
    TextView phaseValue;

    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.phase)
    SeekBar phase;

    Presenter presenter;
    private Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupPhase();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                showValues(msg.getData().getShortArray(Presenter.EXTRA_DATA));
            };
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        } else {
            presenter = new Presenter(this, this, handler);
        }
    }

    private void setupPhase() {
        phase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                presenter.setPhase(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter = new Presenter(this, this, handler);
                } else {
                    Toast.makeText(this, "No permissions", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void setValue(double newValue){
        phaseValue.setText(Double.toString(newValue));
    }

    @OnClick(R.id.play)
    void play(){
        presenter.play();
    }

    @OnClick(R.id.stop)
    void stop(){
        presenter.stop();
    }

    @OnClick(R.id.experiment)
    void experiment(){
        presenter.startExperiment();
    }

    @OnTextChanged(R.id.edit_text_right_frequency_value)
    void setRightValue(CharSequence s) {
        try {
            Integer value = Integer.valueOf(s.toString());
            presenter.setRightFrequency(value);
        }
        catch (NumberFormatException e){
            Toast.makeText(this, "Not a number", Toast.LENGTH_SHORT).show();
        }
    }

    @OnTextChanged(R.id.edit_text_left_frequency_value)
    void setLeftValue(CharSequence s) {
        try {
            Integer value = Integer.valueOf(s.toString());
            presenter.setLeftFrequency(value);
        }
        catch (NumberFormatException e){
            Toast.makeText(this, "Not a number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setStateText(String state) {
        stateText.setText(getString(R.string.stateText, state));
    }

    public void showValues(short[] data){
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < data.length; i++) {
            entries.add(new Entry(i, data[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, null); // add entries to dataset
        dataSet.setColor(Color.GRAY);
        dataSet.setLineWidth(1f);
        dataSet.setFillColor(Color.GRAY);
        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
