package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bobczanki.cheatsheet.R;

public class FontAlignmentSetting extends AbstractSetting {
    private SharedPreferences preferences;
    private Activity activity;

    public FontAlignmentSetting(Activity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        createAlert(activity, R.layout.settings_font_alignment, R.id.fontAlignmentContentPane);
    }

    public static int getGravity(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        return preferences.getInt(activity.getString(R.string.textAlignment), Gravity.START);
    }

    private int gravity;
    private ImageView left;
    private ImageView center;
    private ImageView right;

    private final static int CHECKED_BACKGROUND = Color.rgb(98, 186, 129);

    @Override
    protected void setUpAlert() {
        gravity = getGravity(activity);
        left = findViewById(R.id.alignLeft);
        center = findViewById(R.id.alignCenter);
        right = findViewById(R.id.alignRight);
        left.setOnClickListener((v) -> onClick(Gravity.START, v));
        center.setOnClickListener((v) -> onClick(Gravity.CENTER_HORIZONTAL, v));
        right.setOnClickListener((v) -> onClick(Gravity.END, v));
        if (gravity == Gravity.START)
            left.setBackgroundColor(CHECKED_BACKGROUND);
        if (gravity == Gravity.CENTER_HORIZONTAL)
            center.setBackgroundColor(CHECKED_BACKGROUND);
        if (gravity == Gravity.END)
            right.setBackgroundColor(CHECKED_BACKGROUND);
        setTitle(activity.getString(R.string.alignText));
        setOnDismissListener((d) ->  preferences.edit().putInt(activity.getString(R.string.textAlignment), gravity).apply());
    }

    private void onClick(int gravity, View v) {
        this.gravity = gravity;
        left.setBackgroundColor(Color.TRANSPARENT);
        center.setBackgroundColor(Color.TRANSPARENT);
        right.setBackgroundColor(Color.TRANSPARENT);
        v.setBackgroundColor(CHECKED_BACKGROUND);
    }
}
