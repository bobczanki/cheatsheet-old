package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class Images {
    interface ImagePickerListener {
        void onResult(List<Image> images);
    }
    private static ImagePickerListener pickerListener;

    static void pickMultipleImages(EditActivity activity, ImagePickerListener listener) {
        pickerListener = listener;
        ImagePicker.with(activity)
                .setMultipleMode(true)
                .setFolderMode(true)
                .start();
    }

    interface ImageCropListener {
        void onResult(String path);
    }
    private static ImageCropListener cropListener;

    static void cropImage(EditActivity activity, String imgPath, ImageCropListener listener) {
        cropListener = listener;

        UCrop.of(Uri.fromFile(new File(imgPath)),
                Uri.fromFile(new File(makeUniquePath(imgPath))))
                .start(activity);
    }

    private static String makeUniquePath(String path) {
        String[] split = path.split("\\.");
        String extension = split[split.length - 1];
        Format formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        return path.substring(0, path.length() - extension.length() - 1) + formatter.format(new Date()) + "." + extension;
    }

    static void onResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK
                && data != null && pickerListener != null) {
            List<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            if (images.size() > 0)
                pickerListener.onResult(images);
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = null;
            if (data != null)
                resultUri = UCrop.getOutput(data);
            if (resultUri != null)
                cropListener.onResult(resultUri.getPath());
        }
    }

    static void imageToView(Activity activity, ImageView view, String imgPath) {
        Glide.with(activity).load(imgPath).into(view);
    }

    static void imageToView(Activity activity, ImageView view, String imgPath, float sizeMultiplier) {
        Glide.with(activity).load(imgPath).sizeMultiplier(sizeMultiplier).into(view);
    }
}
