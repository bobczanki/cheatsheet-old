package com.bobczanki.cheatsheet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FullscreenActivity {
    private List<List<SheetData>> sheetDataLists = new ArrayList<>();
    private List<SheetDataAdapter> sheetDataAdapters = new ArrayList<>();
    private SharedPreferences preferences;
    private Billing billing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        preferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        //preferences.edit().clear().commit(); //debug only (resets app data)
        billing = new Billing(this);
        if (isFirstRun())
            onFirstRun();
        setUpRecyclers();
        setSettingsButtonListener();
        openCheatSheetProAlert();
    }

    private void openCheatSheetProAlert() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.proNameText))
                .setMessage(getString(R.string.proText))
                .setPositiveButton(getString(R.string.downloadText), ((dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.bobczanki.cheat_sheet_pro")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bobczanki.cheat_sheet_pro")));
                    }
                }))
                .setIcon(R.drawable.iconnn)
                .show();
    }

    private boolean isFirstRun() {
        boolean result = preferences.getBoolean(getString(R.string.runPreference), true);
        preferences.edit().putBoolean(getString(R.string.runPreference), false).apply();
        return result;
    }

    private void onFirstRun() {
        SheetData sheet = new SheetData(getString(R.string.exampleTitle), getString(R.string.exampleContent));
        sheet.save(preferences, 2, 0);
        SheetData.saveCount(preferences, 2, 1);
    }

    private void setUpRecyclers() {
        sheetDataLists = SheetData.loadSheets(preferences);
        initRecyclers();
        refreshRecyclers();
    }

    private void initRecyclers() {
        List<RecyclerView> recyclers = new ArrayList<>();
        recyclers.add((RecyclerView)findViewById(R.id.recyclerView1));
        recyclers.add((RecyclerView)findViewById(R.id.recyclerView2));
        recyclers.add((RecyclerView)findViewById(R.id.recyclerView3));

        int i = 0;
        for (RecyclerView recycler : recyclers) {
            sheetDataAdapters.add(new SheetDataAdapter(sheetDataLists.get(i), i, this, billing));
            recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
            recycler.setItemAnimator(new DefaultItemAnimator());
            recycler.setAdapter(sheetDataAdapters.get(i));
            i++;
        }
    }

    private void refreshRecyclers() {
        for (SheetDataAdapter adapter : sheetDataAdapters)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        for (SheetDataAdapter adapter : sheetDataAdapters)
            adapter.refresh(preferences);
        billing.refresh();
    }

    private void setSettingsButtonListener() {
        findViewById(R.id.settings).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!billing.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        billing.onDestroy();
        super.onDestroy();
    }
}
