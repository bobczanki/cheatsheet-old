package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bobczanki.cheatsheet.R;
import com.bobczanki.cheatsheet.ShakeDetector;

public class ShakeToHideSetting extends AbstractSetting {
    private SharedPreferences preferences;
    private Activity activity;
    private ShakeDetector shakeDetector;
    private TextView isShakingText;
    private final static float MIN_THRESHOLD = 1.1F;
    private final static float MAX_THRESHOLD = 2.5F;

    public ShakeToHideSetting(Activity activity, ShakeDetector detector) {
        this.activity = activity;
        shakeDetector = detector;
        preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        createAlert(activity, R.layout.settings_shake, R.id.shakeContentPane);
    }

    public static boolean isOn(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        return preferences.getBoolean(activity.getString(R.string.shakeOn), false);
    }

    public static float getThreshold(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        return preferences.getFloat(activity.getString(R.string.shakeThreshold), 1.4F);
    }

    @Override
    protected void setUpAlert() {
        isShakingText = findViewById(R.id.shakeText);
        Switch isShakeOn = findViewById(R.id.shakeOn);
        isShakeOn.setChecked(isOn(activity));
        SeekBar seekBar = findViewById(R.id.shakeSlider);
        seekBar.setMax((int)(MAX_THRESHOLD * 100) - (int)(MIN_THRESHOLD * 100));
        seekBar.setProgress((int)(getThreshold(activity) * 100) - (int)(MIN_THRESHOLD * 100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress += (int)(MIN_THRESHOLD * 100);
                shakeDetector.setThreshold((float)progress / 100.0F);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setOnDismissListener((d) ->
        { preferences.edit().putFloat(activity.getString(R.string.shakeThreshold), (float)seekBar.getProgress() / 100.0F + MIN_THRESHOLD).apply();
        preferences.edit().putBoolean(activity.getString(R.string.shakeOn), isShakeOn.isChecked()).apply(); });
        setTitle(activity.getString(R.string.shakeToHideText));
    }

    public void onShake() {
        isShakingText.setText(activity.getString(R.string.isShakingText));
        isShakingText.setTextColor(Color.GREEN);
    }

    public void onStopShake() {
        isShakingText.setText(activity.getString(R.string.isntShakingText));
        isShakingText.setTextColor(Color.RED);
    }
}
