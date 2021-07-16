package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Switch;

import com.bobczanki.cheatsheet.R;

public class ScreenRotationSetting extends AbstractSetting {
    private SharedPreferences preferences;
    private Activity activity;

    public ScreenRotationSetting(Activity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        createAlert(activity, R.layout.settings_rotation, R.id.rotationContentPane);
    }

    public static boolean isOn(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        return preferences.getBoolean(activity.getString(R.string.rotationOn), false);
    }

    @Override
    protected void setUpAlert() {
        Switch isRotationOn = findViewById(R.id.rotationOn);
        isRotationOn.setChecked(isOn(activity));
        setTitle(activity.getString(R.string.screenRotationText));
        setOnDismissListener((d) -> preferences.edit().putBoolean(activity.getString(R.string.rotationOn), isRotationOn.isChecked()).apply());
    }
}
