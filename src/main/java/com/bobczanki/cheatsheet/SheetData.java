package com.bobczanki.cheatsheet;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SheetData {
    private String title;
    private String text;
    private String imagePath;
    private boolean image = false;

    /* Text */
    SheetData(String title, String text) {
        this.title = title;
        this.text = text;
    }

    /* Image */
    static SheetData createImage(String title, String imagePath) {
        SheetData sheetData = new SheetData(title, "");
        sheetData.imagePath = imagePath;
        sheetData.image = true;
        return sheetData;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isImage() {
        return image;
    }

    public void save(SharedPreferences preferences, int row, int column) {
        preferences.edit().putString("sheetTitle" + row + column, title).apply();
        preferences.edit().putString("sheetText" + row + column, text).apply();
        preferences.edit().putString("sheetImage" + row + column, imagePath).apply();
    }

    static void saveCount(SharedPreferences preferences, int row, int count) {
        preferences.edit().putInt("sheetCount" + row, count).apply();
    }

    static List<List<SheetData>> loadSheets(SharedPreferences preferences) {
        List<List<SheetData>> result = new ArrayList<>();
        for(int row = 0; row <= 2; row++)
            result.add(loadSheets(preferences, row));
        return result;
    }

    static List<SheetData> loadSheets(SharedPreferences preferences, int row) {
        List<SheetData> result = new ArrayList<>();
        int count = preferences.getInt("sheetCount" + row, 0);
        for(int col = 0; col < count; col++)
            result.add(loadSheet(preferences, row, col));
        return result;
    }

    static SheetData loadSheet(SharedPreferences preferences, int row, int col) {
        String title = preferences.getString("sheetTitle" + row + col, "");
        String text = preferences.getString("sheetText" + row + col, "");
        String image = preferences.getString("sheetImage" + row + col, "");
        if (image.isEmpty())
            return new SheetData(title, text);
        else
            return createImage(title, image);
    }

    static void removeSheets(SharedPreferences preferences, int row) {
        int count = preferences.getInt("sheetCount" + row, 0);
        for(int column = 0; column < count; column++) {
            preferences.edit().putString("sheetTitle" + row + column, "").apply();
            preferences.edit().putString("sheetText" + row + column, "").apply();
            preferences.edit().putString("sheetImage" + row + column, "").apply();
        }
        preferences.edit().putInt("sheetCount" + row, 0).apply();
    }
}
