package com.bobczanki.cheatsheet;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ShakeDetector extends AppCompatActivity implements SensorEventListener  {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long shakeTimestamp;
    private float shakeThreshold = 1.4F;
    private static final int SHAKE_SLOP_TIME_MS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setThreshold(float threshold) {
        shakeThreshold = threshold;
    }

    protected abstract void onShake();
    protected void onStopShake() {}

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null)
            sensorManager.registerListener(this, accelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / android.hardware.SensorManager.GRAVITY_EARTH;
        float gY = y / android.hardware.SensorManager.GRAVITY_EARTH;
        float gZ = z / android.hardware.SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > shakeThreshold) {
            shakeTimestamp = System.currentTimeMillis();
            onShake();
        }
        else {
            final long now = System.currentTimeMillis();
            if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now)
                return;
            shakeTimestamp = now;
            onStopShake();
        }
    }
}