package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FloatingActionMenu {
    private boolean menuOpened;
    private View mainButton;
    private List<View> actionButtons;
    private Context context;
    private FloatingActionClickListener listener;
    private int buttonsDistanceDP = 15;

    FloatingActionMenu(Context context, View mainButton, View... actionButtons) {
        this.context = context;
        this.actionButtons = new ArrayList<>(Arrays.asList(actionButtons));
        this.mainButton = mainButton;
        setMainButtonListener();
        setActionButtonsListeners();
    }

    public void setListener(FloatingActionClickListener listener) {
        this.listener = listener;
    }

    public void openMenu() {
        Animations.rotate(mainButton, 45);
        Animations.translate(mainButton, -mainButton.getWidth(), 0);
        int i = 0;
        for (View view : actionButtons) {
            Animations.translate(view, mainButton.getWidth() * i + dp2px(buttonsDistanceDP * (i + 1)), 0);
            i++;
        }

        menuOpened = true;
    }

    public void closeMenu() {
        Animations.rotate(mainButton, 0, new AccelerateDecelerateInterpolator());
        Animations.translate(mainButton, 0, 0, new AccelerateDecelerateInterpolator());
        for (View view : actionButtons)
            Animations.translate(view, 0, 0, new DecelerateInterpolator());
        menuOpened = false;
    }

    public void dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Rect rect = new Rect();
            mainButton.getGlobalVisibleRect(rect);
            if (!rect.contains((int)ev.getRawX(), (int)ev.getRawY()))
                closeMenu();
        }
    }

    private void setActionButtonsListeners() {
        for (View view : actionButtons)
            view.setOnClickListener((v) -> callListener(v, actionButtons.indexOf(v) + 1));
    }

    private void setMainButtonListener() {
        mainButton.setOnClickListener((v) -> {
            if (menuOpened)
                closeMenu();
            else
                openMenu();
            callListener(v, 0);
        });
    }

    private int dp2px(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    interface FloatingActionClickListener {
        void onClick(View view, int index);
    }

    private void callListener(View view, int index) {
        if (listener != null)
            listener.onClick(view, index);
    }
}
