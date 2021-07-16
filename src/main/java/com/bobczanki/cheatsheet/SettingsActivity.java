package com.bobczanki.cheatsheet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.bobczanki.cheatsheet.settings.FontAlignmentSetting;
import com.bobczanki.cheatsheet.settings.FontDimSetting;
import com.bobczanki.cheatsheet.settings.FontFamilySetting;
import com.bobczanki.cheatsheet.settings.FontSizeSetting;
import com.bobczanki.cheatsheet.settings.ScreenRotationSetting;
import com.bobczanki.cheatsheet.settings.ShakeToHideSetting;
import com.bobczanki.cheatsheet.settings.TutorialSetting;

import static com.bobczanki.cheatsheet.PurchasePopup.PURCHASE_MESSAGE;

public class SettingsActivity extends ShakeDetector {
    private ShakeToHideSetting shakeSetting;
    //private Billing billing;
    //private Button purchaseButton;
    private ImageView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //billing = new Billing(this, this);
        //purchaseButton = findViewById(R.id.btn_purchase);
        version = findViewById(R.id.versionImg);
        setToolbar();
        setUpListeners();

    }

    private void setUpListeners() {
        findViewById(R.id.btn_size).setOnClickListener(v -> new FontSizeSetting(this));
        findViewById(R.id.btn_font).setOnClickListener(v -> new FontFamilySetting(this));
        findViewById(R.id.btn_dim).setOnClickListener(v -> new FontDimSetting(this));
        findViewById(R.id.btn_align).setOnClickListener(v -> new FontAlignmentSetting(this));
        findViewById(R.id.btn_hide).setOnClickListener(v -> shakeSetting = new ShakeToHideSetting(this, this));
        findViewById(R.id.btn_rotation).setOnClickListener(v -> new ScreenRotationSetting(this));
        findViewById(R.id.btn_tutorial).setOnClickListener(v -> new TutorialSetting(this));
        findViewById(R.id.btn_contact).setOnClickListener(v -> contactUs());
        //findViewById(R.id.btn_purchase).setOnClickListener(v -> billing.purchaseLicense());
    }

    private void setToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myToolbar.inflateMenu(R.menu.settings_menu);
    }

    private boolean isBuyingLicense() {
        Intent intent = getIntent();
        return intent.getBooleanExtra(PURCHASE_MESSAGE, false);
    }

    private void contactUs() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","reversedev.contact@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CheatSheet feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, "E-mail: "));
    }

    /*
    @Override
    public void onBillingReady() {
        if (isBuyingLicense())
            billing.purchaseLicense();
        if (billing.isLicensed()) {
            purchaseButton.setVisibility(View.GONE);
            version.setImageResource(R.drawable.vec_version_full);
        } else
            version.setImageResource(R.drawable.vec_version_demo);
    }
    */
/*
    @Override
    public void onBillingError() {
        Views.showToast(this, R.string.notfinalizedText, Toast.LENGTH_LONG);
    }
*/
    /*
    @Override
    public void onPurchase(String sku) {
        if (sku.equals(Billing.LICENSE_SKU)) {
            purchaseButton.setVisibility(View.GONE);
            version.setImageResource(R.drawable.vec_version_full);
        }
    }
    */

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onShake() {
        if (shakeSetting != null)
            shakeSetting.onShake();
    }

    @Override
    protected void onStopShake() {
        if (shakeSetting != null)
            shakeSetting.onStopShake();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!billing.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
*/
    @Override
    public void onDestroy() {
        //billing.onDestroy();
        super.onDestroy();
    }
}
