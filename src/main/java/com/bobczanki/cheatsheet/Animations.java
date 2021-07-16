package com.bobczanki.cheatsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

public class Animations {

    static void animateScroll(View view, int scroll) {
        ObjectAnimator yScroll = ObjectAnimator.ofInt(view, "scrollY", scroll);
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(300);
        animators.play(yScroll);
        animators.start();
    }

    static void slideInAnimation(Activity activity, View view) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    static void slideOutAnimation(Activity activity, View view) {
        if (view.getVisibility() != View.VISIBLE)
            return;
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out);
        view.startAnimation(animation);
        view.setVisibility(View.INVISIBLE);
    }

    static void fadeInAnimation(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
            v.animate().alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    v.clearAnimation();
                    v.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    static void fadeInAnimation(View view, float alpha) {
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(alpha).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.clearAnimation();
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    static void fadeOutAnimation(View... views) {
        for (View v : views) {
            v.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    v.clearAnimation();
                    v.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public static void fadeOutAndIn(View... views) {
        for (View view : views) {
            view.animate().alpha(0).setDuration(200).setStartDelay(0).withEndAction(() ->
                    view.animate().alpha(1).setDuration(200).setStartDelay(200));
        }
    }

    static void rotate(View view, int by, Interpolator interpolator) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", by);
        animator.setInterpolator(interpolator);
        animator.setDuration(300);
        animator.start();
    }

    static void rotate(View view, int by) {
        rotate(view, by, new OvershootInterpolator());
    }

    static void translate(View view, int x, int y, Interpolator interpolator) {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(view, "translationX", x);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(view, "translationY", y);
        animSet.playTogether(xAnimator, yAnimator);
        animSet.setInterpolator(interpolator);
        animSet.setDuration(300);
        animSet.start();
    }

    static void alpha(View view, float alpha) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha);
        animator.setDuration(300);
        animator.start();
    }

    static void translate(View view, int x, int y) {
        translate(view, x, y, new OvershootInterpolator());
    }

}
