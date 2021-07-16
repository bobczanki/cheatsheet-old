package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.content.Intent;

import com.bobczanki.cheatsheet.settings.AbstractSetting;

public class PurchasePopup extends AbstractSetting {
    public static final String PURCHASE_MESSAGE = "THIS_MESSAGE_SAYS_IF_USER_CLICKED_PURCHASE";
    private Activity activity;

    PurchasePopup(Activity activity) {
        this.activity = activity;
        createAlert(activity, R.layout.edit_purchase, R.id.purchaseContentPane);
    }

    @Override
    protected void setUpAlert() {
        setTitle(activity.getString(R.string.demoText));
        setSaveButton(activity.getString(R.string.purchaseText),
                (d, i) ->  {
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    intent.putExtra(PURCHASE_MESSAGE, true);
                    activity.startActivity(intent);
                    activity.finish(); });
    }
}
