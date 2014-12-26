package com.example.zorg.androidsnakepoc;

/**
 * Created by roy on 26-Dec-14.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccManager {

    private SnakeScreen snakeActivity;

    // for acc
    private SensorManager sm;
    private Sensor accSensor;

    // ramp-speed - play with this value until satisfied
    final float HIGHPASS_FILTER = 0.1f;
    final float LOWPASS_FILTER = 0.9f;

    // listener for acc
    private SensorEventListener accListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;
            float x = values[0];
            float y = values[1];

            float[] resultsHigh = highPass(x, y);
            float[] resultsLow = lowPass(resultsHigh[0], resultsHigh[1]);

            // write values to activity view
            snakeActivity.writeValuesToViews(resultsLow[0], resultsLow[1]);

            // send to move event
            snakeActivity.move(resultsLow[0], resultsLow[1]);
        }
    };

    public AccManager(SnakeScreen snakeActivity, double threshold, SensorManager sm){

        this.snakeActivity = snakeActivity;
        this.sm = sm;
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.registerListener();
    }

    public float[] highPass(float x, float y) {

        float accel[] = new float[2];
        float result[] = new float[2];

        accel[0] = x * HIGHPASS_FILTER + accel[0] * (1.0f - HIGHPASS_FILTER);
        accel[1] = y * HIGHPASS_FILTER + accel[1] * (1.0f - HIGHPASS_FILTER);
        result[0] = x - accel[0];
        result[1] = y - accel[1];

        return result;

    }

    public float[] lowPass(float x, float y) {

        float[] filteredValues = new float[3];		filteredValues[0] = x * LOWPASS_FILTER + filteredValues[0] * (1.0f - LOWPASS_FILTER);
        filteredValues[1] = y * LOWPASS_FILTER + filteredValues[1] * (1.0f - LOWPASS_FILTER);

        return filteredValues;

    }

    public void registerListener(){

        sm.registerListener(accListener, accSensor,
                SensorManager.SENSOR_DELAY_UI);

    }

    public void unregisterListener(){

        sm.unregisterListener(accListener);

    }

}
