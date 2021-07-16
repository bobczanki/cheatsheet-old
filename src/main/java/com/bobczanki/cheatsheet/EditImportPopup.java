package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.widget.EditText;

import com.bobczanki.cheatsheet.settings.AbstractSetting;

import java.util.ArrayList;
import java.util.List;

public class EditImportPopup extends AbstractSetting {
    public interface ImportResultListener {
        void onImport(List<SheetData> result);
    }

    private Activity activity;
    private ImportResultListener listener;
    private int i;

    public EditImportPopup(Activity activity, int title, ImportResultListener listener) {
        this.activity = activity;
        this.listener = listener;
        i = title;
        createAlert(activity, R.layout.edit_import, R.id.importContentPane);
    }

    @Override
    protected void setUpAlert() {
        EditText imported= findViewById(R.id.importEditText);
        setTitle(activity.getString(R.string.importEdit));
        setSaveButton(activity.getString(R.string.importText),
                (d, i) ->  listener.onImport(parseText(imported.getText().toString())));
    }

    private List<SheetData> parseText(String text) {
        List<SheetData> result = new ArrayList<>();
        for (String s : text.split("\n*;\n*"))
                result.add(new SheetData("" + i++, s));

        return  result;
    }
}
