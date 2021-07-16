package com.bobczanki.cheatsheet.settings;

import androidx.appcompat.app.AppCompatActivity;

import com.bobczanki.cheatsheet.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class TutorialSetting extends AbstractSetting {
    private AppCompatActivity activity;
    private static boolean isDialogOpen;

    public TutorialSetting(AppCompatActivity activity) {
        this.activity = activity;
        if (!isDialogOpen) {
            createAlert(activity, R.layout.settings_tutorial, R.id.tutorialContentPane);
            isDialogOpen = true;
        }
    }

    @Override
    protected void setUpAlert() {
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        activity.getLifecycle().addObserver(youTubePlayerView);
        setOnDismissListener((e) -> isDialogOpen = false);
    }
}
