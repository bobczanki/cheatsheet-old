package com.bobczanki.cheatsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.lang.reflect.Field;

import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

//Helper class
public class Views {
    public static void setVisible(View... views) {
        for (View v : views)
            v.setVisibility(View.VISIBLE);
    }

    public static void setGone(View... views) {
        for (View v : views)
            v.setVisibility(View.GONE);
    }

    static void changeFocus(View view) {
        ((View) view.getParent()).setFocusableInTouchMode(true);
        ((View) view.getParent()).requestFocus();
    }

    static Tooltip createTooltip(Context context, View view, String text) {
        ClosePolicy closePolicy = new ClosePolicy.Builder()
                .inside(true)
                .outside(true)
                .consume(false)
                .build();
        return new Tooltip.Builder(context)
                .anchor(view, 0, 0, true)
                .text(text)
                .closePolicy(closePolicy)
                .create();
    }

    static void showTooltip(Tooltip tooltip, View view, Tooltip.Gravity gravity) {
        view.post(() -> tooltip.show(view, gravity, true));
    }

    static void showToast(Activity activity, int text, int length) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(activity, text, length).show());
    }

    public static void setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        try{
            Field selectorWheelPaintField = numberPicker.getClass()
                    .getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText)
                ((EditText)child).setTextColor(color);
        }
        numberPicker.invalidate();
    }

    public static void setPickerDividerColor(NumberPicker picker, int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    interface OnDownListener {
        void onDown();
    }

    public static void setOnDownListener(View view, OnDownListener listener) {
        view.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                view.setPressed(true);
                listener.onDown();
                return true;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP)
                view.performClick();
            return false;
        });
    }

    public static void setOneTimeOnClickListener(View view, View.OnClickListener listener) {
        view.setOnClickListener((v) -> {
            listener.onClick(v);
            v.setEnabled(false);
        });
    }

    interface OnLongPressListener {
        void onLongPress();
    }

    public static GestureDetector createLongPressDetector(Context context, OnLongPressListener listener) {
        return new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                listener.onLongPress();
            }
        });
    }

    interface ChildrenFor {
        void forChild(View child);
    }

    public static void foreachChild(ViewGroup parent, ChildrenFor childrenFor) {
        for (int i = 0; i < parent.getChildCount(); i++)
            childrenFor.forChild(parent.getChildAt(i));
    }
}
