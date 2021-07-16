package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bobczanki.cheatsheet.R;

public class FontDimSetting extends AbstractSetting {
    private SharedPreferences preferences;
    private Activity activity;
    private final static int MAX_DIM = 50;

    public FontDimSetting(Activity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        createAlert(activity, R.layout.settings_font_size, R.id.fontSizeContentPane);
    }

    public static float getAlpha(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        return preferences.getFloat(activity.getString(R.string.fontAlpha), 1.0F);
    }

    @Override
    protected void setUpAlert() {
        final TextView exampleText = findViewById(R.id.fontSizeText);
        SeekBar seekBar = findViewById(R.id.fontSizeSlider);
        float fontAlpha = preferences.getFloat(activity.getString(R.string.fontAlpha), 1.0F);
        seekBar.setMax(MAX_DIM);
        seekBar.setProgress((int)((1.0F - fontAlpha) * 100));
        exampleText.setAlpha(fontAlpha);
        exampleText.setTypeface(FontFamilySetting.getFamily(activity));
        exampleText.setTextSize(preferences.getInt(activity.getString(R.string.fontSize), activity.getResources().getInteger(R.integer.defaultFontSize)));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                exampleText.setAlpha(1.0F - (float)progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setTitle(activity.getString(R.string.textDimmerText));
        setOnDismissListener((d) ->  preferences.edit().putFloat(activity.getString(R.string.fontAlpha), 1.0F - (float)seekBar.getProgress() / 100).apply());
    }
}
