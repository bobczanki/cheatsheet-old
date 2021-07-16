package com.bobczanki.cheatsheet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EditActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private RecyclerView recycler;
    private EditDataAdapter adapter;
    private int row;
    private List<SheetData> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        Intent intent = getIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recycler = findViewById(R.id.recycler);
        row = intent.getIntExtra(SheetDataAdapter.EDIT_MESSAGE, 0);
        preferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        setFullscreen();
        initRecycler();
        attachTouchHelper();
    }

    public void setFullscreen()
    {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                getWindow().getDecorView().setSystemUiVisibility(flags);
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    private void initRecycler() {
        dataList = SheetData.loadSheets(preferences, row);
        adapter = new EditDataAdapter(dataList, row, this);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void attachTouchHelper() {
        ItemTouchHelper.Callback callback =
                new EditDataTouchHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recycler);
        adapter.setTouchHelper(touchHelper);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (adapter != null)
            adapter.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Images.onResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Views.changeFocus(recycler);
        adapter.saveEditData();
    }
}
