package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bobczanki.cheatsheet.R;
import com.bobczanki.cheatsheet.Views;

public class FontFamilySetting extends AbstractSetting {
    private SharedPreferences preferences;
    private Activity activity;
    private final static String[] fonts = { "casual", "cursive", "monospace", "sans-serif", "sans-serif-black",
            "sans-serif-condensed", "sans-serif-condensed-light", "sans-serif-light", "sans-serif-medium",
            "sans-serif-smallcaps", "sans-serif-thin", "serif", "serif-monospace"};

    public FontFamilySetting(Activity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        createAlert(activity, R.layout.settings_font_family, R.id.fontFamilyContentPane);
    }

    public static Typeface getFamily(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        int fontFamily = preferences.getInt(activity.getString(R.string.fontFamily), activity.getResources().getInteger(R.integer.defaultFontFamily));
        return Typeface.create(fonts[fontFamily], Typeface.NORMAL);
    }

    @Override
    protected void setUpAlert() {
        TextView exampleText = findViewById(R.id.fontFamilyText);
        exampleText.setTypeface(getFamily(activity));
        exampleText.setTextSize(FontSizeSetting.getSize(activity));
        NumberPicker picker = findViewById(R.id.fontPicker);
        Views.setNumberPickerTextColor(picker, Color.WHITE);
        Views.setPickerDividerColor(picker, Color.rgb(68, 156, 99));
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setMinValue(0);
        picker.setMaxValue(fonts.length - 1);
        int fontFamily = preferences.getInt(activity.getString(R.string.fontFamily), activity.getResources().getInteger(R.integer.defaultFontFamily));
        picker.setValue(fontFamily);
        picker.setDisplayedValues( fonts );
        picker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> exampleText.setTypeface(Typeface.create(fonts[newVal], Typeface.NORMAL)));
        setTitle(activity.getString(R.string.FontFamilyText));
        setOnDismissListener((d) ->  preferences.edit().putInt(activity.getString(R.string.fontFamily), picker.getValue()).apply());
    }
}
