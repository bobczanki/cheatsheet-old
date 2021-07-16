package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bobczanki.cheatsheet.R;

public class FontSizeSetting extends AbstractSetting {
    private SharedPreferences preferences;
    private Activity activity;
    private final static int MIN_FONT = 5;
    private final static int MAX_FONT = 30;

    public FontSizeSetting(Activity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        createAlert(activity, R.layout.settings_font_size, R.id.fontSizeContentPane);
    }

    public static int getSize(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        return preferences.getInt(activity.getString(R.string.fontSize), activity.getResources().getInteger(R.integer.defaultFontSize));
    }

    @Override
    protected void setUpAlert() {
        final TextView exampleText = findViewById(R.id.fontSizeText);
        SeekBar seekBar = findViewById(R.id.fontSizeSlider);
        int fontSize = preferences.getInt(activity.getString(R.string.fontSize), activity.getResources().getInteger(R.integer.defaultFontSize));
        seekBar.setMax(MAX_FONT - MIN_FONT);
        seekBar.setProgress(fontSize - MIN_FONT);
        exampleText.setTextSize(fontSize);
        exampleText.setTypeface(FontFamilySetting.getFamily(activity));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress += MIN_FONT;
                exampleText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setTitle(activity.getString(R.string.fontSizeText));
        setOnDismissListener((d) ->  preferences.edit().putInt(activity.getString(R.string.fontSize), seekBar.getProgress() + MIN_FONT).apply());
    }
}
