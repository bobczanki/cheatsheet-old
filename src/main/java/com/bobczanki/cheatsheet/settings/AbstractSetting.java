package com.bobczanki.cheatsheet.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.bobczanki.cheatsheet.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public abstract class AbstractSetting {
    private View inflatedPane;
    private AlertDialog.Builder builder;
    private Activity activity;


    /* Constructor */
    protected void createAlert(Activity activity, int layout, int contentPane) {
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        inflatedPane = inflater.inflate(layout, activity.findViewById(contentPane));
        builder = new AlertDialog.Builder(activity, R.style.CustomDialogTheme);
        builder.setView(inflatedPane);
        setUpAlert();
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected abstract void setUpAlert();

    protected void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        builder.setOnDismissListener(listener);
    }

    protected void setSaveButton(DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(activity.getString(R.string.saveText), listener);
    }

    protected void setSaveButton(String text, DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(text, listener);
    }

    protected void setTitle(String title) {
        builder.setTitle(title);
    }

    protected <T extends View> T findViewById(int id) {
        return inflatedPane.findViewById(id);
    }
}
