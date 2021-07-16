package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;


public class Billing implements BillingProcessor.IBillingHandler {
    interface BillingListener {
        void onPurchase(String sku);
        void onBillingReady();
        void onBillingError();
    }

    private final static String BILLING_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkaUbCpV3PF5jnRSzc6Qr5EQsBlI6CR78p0YcD1R9EHG0GwMhKIb1Pfpig9bk2X5ADxfMDdhx16vyLoUQBy0vTJWa1nyeKJM1gIuevRnZgu//As7PZf0u4Rwl6j4C3OAOJv7QiC8UD1P7fEp/8Ppt7vl8D5v5JB3rf6iOM9AzK94hOY+CXPoPJ9/aJSxTAy8oGjFg6Kh/+WZhXDMlQvFm5zvpZXE5IJxby+nrD3H3DtULaYR1neP+5/oI7SxOFhOQMXraOv32f9n2zfpFK+eJ8iY/6duURqJZi6agrpANrw5S8LA+WG3tYE7nYZC4o4f918H3y5dpyAU+ddZl/DcHowIDAQAB";
    public final static String LICENSE_SKU = "com.bobczanki.full";
    private BillingProcessor billingProcessor;
    private BillingListener billingListener = null;
    private Activity activity;

    Billing(Activity activity) {
        this.activity = activity;
        billingProcessor = new BillingProcessor(activity, BILLING_KEY, this);
        billingProcessor.initialize();
    }

    Billing(Activity activity, BillingListener listener) {
        this(activity);
        billingListener = listener;
    }

    private boolean isInitialized() {
        return billingProcessor != null && billingProcessor.isInitialized();
    }

    private boolean canPurchase() {
        return BillingProcessor.isIabServiceAvailable(activity) &&
                billingProcessor.isOneTimePurchaseSupported();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        if (billingListener != null)
            billingListener.onPurchase(productId);
        Views.showToast(activity, R.string.finalizedText, Toast.LENGTH_LONG);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (billingListener != null)
            billingListener.onBillingError();
    }

    @Override
    public void onBillingInitialized() {
        billingProcessor.loadOwnedPurchasesFromGoogle();
        if (billingListener != null)
            billingListener.onBillingReady();
    }

    public boolean isLicensed() {
        return true;
        /*
        if (isInitialized())
            for (String sku : billingProcessor.listOwnedProducts())
                if (sku.equals(LICENSE_SKU))
                    return true;
        return false;
        */
    }

    public void purchaseLicense() {
        if (isInitialized() && canPurchase())
            billingProcessor.purchase(activity, LICENSE_SKU);
    }

    public void refresh() {
        if (isInitialized()) {
            billingProcessor.release();
            billingProcessor.initialize();
        }
    }

    public void restorePurchases() {
        if (isInitialized())
            billingProcessor.loadOwnedPurchasesFromGoogle();
    }

    // Call it in your activity
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();
    }

    // Call it in your activity, call super method if false
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return billingProcessor.handleActivityResult(requestCode, resultCode, data);
    }
}
