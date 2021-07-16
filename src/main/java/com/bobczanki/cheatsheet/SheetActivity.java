package com.bobczanki.cheatsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobczanki.cheatsheet.settings.FontAlignmentSetting;
import com.bobczanki.cheatsheet.settings.FontDimSetting;
import com.bobczanki.cheatsheet.settings.FontFamilySetting;
import com.bobczanki.cheatsheet.settings.FontSizeSetting;
import com.bobczanki.cheatsheet.settings.ScreenRotationSetting;
import com.bobczanki.cheatsheet.settings.ShakeToHideSetting;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SheetActivity extends ShakeDetector {
    private TextView sheetText;
    private ImageView sheetImage;
    private ImageView resizeButton;
    private View hideTails;
    private ConstraintLayout contentPane;
    private SharedPreferences preferences;
    private RecyclerView miniSheetRecycler;
    private Button leftBtn;
    private Button rightBtn;
    private int visibleLines;
    private int lineHeight;
    private int row;
    private int col;
    private int lineScroll;
    private int imageScroll;
    private boolean afterLongTouch;
    private SheetData sheet;
    private boolean hidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheet);
        preferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        contentPane = findViewById(R.id.contentPane);
        miniSheetRecycler = findViewById(R.id.miniSheetsRecycler);
        FullscreenActivity.setFullscreen(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!ScreenRotationSetting.isOn(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sheet = getExtraSheet();
        initSheetText();
        initSheetImage();
        initResizeButton();
        initPageButtons();
        initSheetRecycler();
        initContentPaneListener();
        setThreshold(ShakeToHideSetting.getThreshold(this));
        contentPane.post(this::afterCreate);
    }

    private void initSheetText() {
        sheetText = findViewById(R.id.sheet);
        sheetText.setText(sheet.getText());
        sheetText.setTextSize(FontSizeSetting.getSize(this));
        sheetText.setTypeface(FontFamilySetting.getFamily(this));
        sheetText.setAlpha(FontDimSetting.getAlpha(this));
        sheetText.setGravity(FontAlignmentSetting.getGravity(this));
    }

    private void initSheetImage() {
        sheetImage = findViewById(R.id.sheetImage);
        if (sheet.isImage())
            Images.imageToView(this, sheetImage, sheet.getImagePath());
    }

    private SheetData getExtraSheet() {
        Intent intent = getIntent();
        row = intent.getIntExtra(SheetDataAdapter.ROW_MESSAGE, 0);
        col = intent.getIntExtra(SheetDataAdapter.COL_MESSAGE, 0);
        return SheetData.loadSheet(preferences, row, col);
    }

    private void afterCreate() {
        sheetText.setScrollY(0);
        lineHeight = getLineHeight();
        setLinesNum(preferences.getInt(getString(R.string.linesPreference), getResources().getInteger(R.integer.defaultLines)));
        initHideTails();
    }

    private void initHideTails() {
        hideTails = findViewById(R.id.hideTails);
        ViewGroup.LayoutParams params = hideTails.getLayoutParams();
        params.height = lineHeight / 6;
        hideTails.setLayoutParams(params);
    }

    private float dY;
    @SuppressLint("ClickableViewAccessibility")
    private void initResizeButton() {
        resizeButton = findViewById(R.id.resize);
        resizeButton.setOnTouchListener((view, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dY = view.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    setLinesNum((int)(getScreenHeight() - (event.getRawY() + dY + view.getHeight())) / lineHeight);
                    break;
            }
            return false;
        });
    }

    private void setLinesNum(int lines) {
        lines = restrictShownLines(lines);
        setBottomMargin(lineHeight * lines);
        preferences.edit().putInt(getString(R.string.linesPreference), lines).apply();
        visibleLines = lines;
        refreshScroll();
    }

    private int restrictShownLines(int lines) {
        lines = Math.min((getScreenHeight() - resizeButton.getHeight()) / lineHeight, lines);
        lines = Math.max(1, lines);
        return lines;
    }

    private void setBottomMargin(int margin) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) resizeButton.getLayoutParams();
        params.bottomMargin = margin;
        resizeButton.setLayoutParams(params);
    }

    private void refreshScroll() {
        contentPane.post(() -> sheetText.setScrollY(lineScroll * lineHeight));
    }

    private void initPageButtons() {
        leftBtn = findViewById(R.id.left);
        rightBtn = findViewById(R.id.right);
        rightBtn.setOnClickListener(view -> {
            if (afterLongTouch)
                afterLongTouch = false;
            else
                scrollDown();
        });
        rightBtn.setOnLongClickListener((v) -> {
            Animations.fadeOutAnimation(resizeButton, rightBtn, leftBtn, sheetText, hideTails, sheetImage);
            Animations.slideInAnimation(this, miniSheetRecycler);
            afterLongTouch = true;
            return true;
        });
        leftBtn.setOnClickListener(view -> scrollUp());
        leftBtn.setOnLongClickListener((v) -> {
            finish();
            return true;
        });
    }

    private void scrollDown() {
        if (sheet.isImage())
            scrollImage(imageScroll + sheetImage.getHeight());
        else
            scrollText(lineScroll + visibleLines);
    }

    private void scrollUp() {
        if (sheet.isImage())
            scrollImage(imageScroll - sheetImage.getHeight());
        else
            scrollText(lineScroll - visibleLines);
    }

    private void scrollImage(int px) {
        if (sheetImage.getDrawable() == null)
            return;
        px = Math.min(sheetImage.getDrawable().getIntrinsicHeight() - sheetImage.getHeight(), px);
        px = Math.max(0, px);
        imageScroll = px;
        Animations.animateScroll(sheetImage, px);
    }

    private void scrollText(int lines) {
        lines = Math.min(sheetText.getLineCount() - visibleLines, lines);
        lines = Math.max(0, lines);
        lineScroll = lines;
        Animations.animateScroll(sheetText, lineScroll * lineHeight);
    }

    private int getLineHeight() {
        int result = 0;
        for (int i = 0; i < sheetText.getLineCount(); i++) {
            Rect bounds = new Rect();
            sheetText.getLineBounds(i, bounds);
            result += bounds.height();
        }
        return result / sheetText.getLineCount();
    }

    private int getScreenHeight() {
        return contentPane.getHeight();
    }

    private void initSheetRecycler() {
        List<SheetData> sheetData = SheetData.loadSheets(preferences, row);
        miniSheetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        miniSheetRecycler.setItemAnimator(new DefaultItemAnimator());
        miniSheetRecycler.setAdapter(new MiniSheetDataAdapter(sheetData, row, this));
    }

    private void initContentPaneListener() {
        contentPane.setOnClickListener(view -> {
            Animations.fadeInAnimation(resizeButton, rightBtn, leftBtn, hideTails, sheetImage);
            Animations.fadeInAnimation(sheetText, FontDimSetting.getAlpha(this));
            Animations.slideOutAnimation(this, miniSheetRecycler);
        });
        contentPane.setOnLongClickListener(v ->  {
            Animations.fadeInAnimation(resizeButton, rightBtn, leftBtn, hideTails, sheetImage);
            Animations.fadeInAnimation(sheetText, FontDimSetting.getAlpha(this));
            Animations.slideOutAnimation(this, miniSheetRecycler);
            return true;
        });
    }

    private GestureDetector gestureDetector;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector == null)
            gestureDetector = Views.createLongPressDetector(this, () -> {
                if (hidden)
                    unhide();
            });
        gestureDetector.onTouchEvent(ev);
        if (hidden)
            return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onShake() {
        if (ShakeToHideSetting.isOn(this) && !hidden)
            hide();
    }

    private void unhide() {
        Views.foreachChild(contentPane, (child) -> Animations.alpha(child, 1));
        Animations.alpha(sheetText, FontDimSetting.getAlpha(this));
        setScreenBrightness(-1);
        hidden = false;
    }

    private void hide() {
        Views.foreachChild(contentPane, (child) -> Animations.alpha(child, 0));
        setScreenBrightness(0);
        hidden = true;
    }

    private void setScreenBrightness(int brightness) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            FullscreenActivity.setFullscreen(this);
    }

    @Override
    public void onPause()
    {
        if (hidden)
            unhide();
        if (isScreenLocked())
            homeButton();
        super.onPause();
    }

    private boolean isScreenLocked() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        return  powerManager != null && !powerManager.isInteractive();
    }

    private void homeButton() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
