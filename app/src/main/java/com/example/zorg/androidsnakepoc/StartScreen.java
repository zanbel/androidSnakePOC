package com.example.zorg.androidsnakepoc;

import com.example.zorg.androidsnakepoc.SnakeApp;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;

public class StartScreen extends Activity {

    // track movement order
    private int _start = 0;

    // acc visualizer
    private AccVisualizer accView;

    // speed spinner
    private Spinner speedSpinner;

    // threshold edit text
    private EditText thresholdText;

    // range edit text
    private EditText rangetext;

    // for acc
    private SensorManager mSensorManager;
    private Sensor mAccSensor;

    // for sound
    private static MediaPlayer mpStart_1;
    private static MediaPlayer mpStart_2;
    private static MediaPlayer mpStart_3;
    private static MediaPlayer mpStart_4;
    private static MediaPlayer mpLetsGo;

    // frame view for acc
    private FrameLayout accFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        // acc view init
        accView = new AccVisualizer(this);
        accFrame = (FrameLayout) findViewById(R.id.accFrame);
        accFrame.addView(accView);

        // acc init
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // spinner init
        speedSpinner = (Spinner) findViewById(R.id.speedSpinner);
        String[] items = new String[] { "1", "2", "3", "4" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        speedSpinner.setAdapter(adapter);

        // threshold edit text init
        thresholdText = (EditText) findViewById(R.id.threasholdEditText);
        rangetext = (EditText) findViewById(R.id.rangeEditText);

        // create media players
        mpStart_1 = MediaPlayer.create(this, R.raw.start_game_1);
        mpStart_2 = MediaPlayer.create(this, R.raw.start_game_2);
        mpStart_3 = MediaPlayer.create(this, R.raw.start_game_3);
        mpStart_4 = MediaPlayer.create(this, R.raw.start_game_4);
        mpLetsGo = MediaPlayer.create(this, R.raw.lets_go);

        this.start();
    }

    // listener for acc
    private SensorEventListener mAccListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;
            float x = values[0];
            float y = values[1];

            // send to move event
            move(x, y);
            // send to view set acc
            accView.setAcceleration(-x, y);
        }
    };

    // indication to start game
    public void move(float x, float y) {

        try {
            if (y < -1.5 && _start == 0) {
                mpStart_1.release();
                mpStart_2.start();
                _start++;
            } else if (y > 1.5 && _start == 1) {
                mpStart_2.release();
                mpStart_3.start();
                _start++;
            } else if (x < -1.5 && _start == 2) {
                mpStart_3.release();
                mpStart_4.start();
                _start++;
            } else if (x > 1.5 && _start == 3) {
                _start++;

                setAppconfig();
                setSpeed();
                mpLetsGo.start();
                Thread.currentThread();
                Thread.sleep(3500);
                mpLetsGo.release();
                mpStart_4.release();

                mSensorManager.unregisterListener(mAccListener);
                Intent intent = new Intent(this, SnakeScreen.class);
                startActivity(intent);
                this.finish();
            }

        } catch (Exception e) {

            Log.println(0, "ERROR", e.toString());
        }
    }

    private void start() {
        try {
            mSensorManager.registerListener(mAccListener, mAccSensor,
                    SensorManager.SENSOR_DELAY_UI);
            mpStart_1.start();
        } catch (Exception e) {
            Log.println(0, "ERROR", e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mAccListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mAccListener, mAccSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    private void setSpeed() {

        int item = speedSpinner.getSelectedItemPosition();

        if (item == 0) {
            SnakeApp.setDelay(400);
            SnakeApp.setSpeed(1);
        } else if (item == 1) {
            SnakeApp.setDelay(300);
            SnakeApp.setSpeed(2);
        } else if (item == 2) {
            SnakeApp.setDelay(200);
            SnakeApp.setSpeed(3);
        } else {
            SnakeApp.setDelay(100);
            SnakeApp.setSpeed(4);
        }

    }

    private void setAppconfig() {

        try {
            double threshold = Double.parseDouble(thresholdText.getText()
                    .toString());
            SnakeApp.setThreshold(threshold);

            double range = Double.parseDouble(rangetext.getText().toString());
            SnakeApp.setRange(range);

        } catch (Exception e) {
        }
    }

	public void statButtonClicked(View view){

		Intent intent = new Intent(this, DatePickerScreen.class);
		startActivity(intent);
	}
}
