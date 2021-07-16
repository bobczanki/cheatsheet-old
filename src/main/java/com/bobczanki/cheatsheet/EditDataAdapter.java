package com.bobczanki.cheatsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.Collections;
import java.util.List;

import it.sephiroth.android.library.xtooltip.Tooltip;

public class EditDataAdapter extends RecyclerView.Adapter<EditDataAdapter.MyViewHolder>  {
    private List<SheetData> sheetDataList;
    private int row;
    private Activity activity;
    private ItemTouchHelper touchHelper;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;
        private ImageView removeButton, img;
        private FloatingActionButton moveButton;
        private RelativeLayout addGroup;

        private MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.editTitle);
            text = view.findViewById(R.id.editText);
            img = view.findViewById(R.id.editImg);
            removeButton = view.findViewById(R.id.remove);
            addGroup = view.findViewById(R.id.addGroup);
            moveButton = view.findViewById(R.id.moveButton);
        }
    }

     EditDataAdapter(List<SheetData> sheetDataList, int row, Activity activity) {
        this.sheetDataList = sheetDataList;
        this.row = row;
        this.activity = activity;
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_data, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position == sheetDataList.size())
            initMenuButton(holder, position);
        else
            initSheetData(holder, position);
    }

    private FloatingActionMenu menu;

    void dispatchTouchEvent(MotionEvent ev) {
        if (menu != null)
            menu.dispatchTouchEvent(ev);
    }

    private boolean tooltipShown = false;
    private void initMenuButton(MyViewHolder holder, int position) {
        Views.setGone(holder.removeButton, holder.title, holder.text, holder.img, holder.moveButton);
        Views.setVisible(holder.addGroup);
        View view = holder.addGroup;
        menu = new FloatingActionMenu(activity,
                view.findViewById(R.id.addButton),
                view.findViewById(R.id.textButton),
                view.findViewById(R.id.imageButton));

        Tooltip tooltip = Views.createTooltip(activity, holder.addGroup, activity.getString(R.string.addTooltip));
        if (position == 0 && !tooltipShown)
            Views.showTooltip(tooltip, holder.addGroup, Tooltip.Gravity.BOTTOM);
        tooltipShown = true;
        menu.setListener((v, i) -> holder.addGroup.postDelayed(() -> {
            if (i == 1)
                addText();
            if (i == 2)
                pickImage();
            Views.changeFocus(holder.addGroup);
            tooltip.dismiss();
        }, 200));
    }

    private void addText() {
        sheetDataList.add(sheetDataList.size(), new SheetData("" + (sheetDataList.size() + 1), ""));
        notifyItemRangeChanged(sheetDataList.size() - 1, 2);
    }

    private void pickImage() {
        Images.pickMultipleImages((EditActivity)activity, (images) -> {
            for (Image img : images)
                sheetDataList.add(sheetDataList.size(),
                        SheetData.createImage("" + (sheetDataList.size() + 1), img.getPath()));
            notifyItemRangeChanged(sheetDataList.size() - images.size(), images.size());
        });
    }

    public void onItemMove(MyViewHolder holder, int fromPosition, int toPosition) {
        if (fromPosition >= sheetDataList.size() || toPosition >= sheetDataList.size())
            return;
        Collections.swap(sheetDataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void initSheetData(MyViewHolder holder, int position) {
        Views.setVisible(holder.removeButton, holder.title, holder.text, holder.img, holder.moveButton);
        Views.setGone(holder.addGroup);
        SheetData sheet = sheetDataList.get(position);
        holder.title.setText(sheet.getTitle());
        holder.title.setTag(sheet);
        holder.text.setTag(sheet);
        holder.removeButton.setTag(holder);
        holder.removeButton.setEnabled(true);
        Views.setOnDownListener(holder.moveButton, () -> startDrag(holder));
        setEditListeners(holder);

        if (sheet.isImage())
            initImage(holder, position);
        else
            initText(holder, sheet.getText(), position);
    }

    private void initImage(MyViewHolder holder, int position) {
        initImageEdit(holder, position);
        Views.setGone(holder.text);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initText(MyViewHolder holder, String text, int position) {
        holder.text.setText(text);
        Views.setGone(holder.img);
    }

    private void initImageEdit(MyViewHolder holder, int position) {
        SheetData sheetData = sheetDataList.get(position);
        Images.imageToView(activity, holder.img, sheetData.getImagePath(), 0.6f);
        holder.img.setOnClickListener((v) -> Images.cropImage((EditActivity)activity, sheetData.getImagePath(),
                (path) -> {
            sheetData.setImagePath(path);
            notifyItemChanged(position); }));
    }

    private void startDrag(MyViewHolder holder) {
        if (touchHelper != null)
            touchHelper.startDrag(holder);
    }

    private void setEditListeners(MyViewHolder holder) {
        holder.text.setOnFocusChangeListener((v, hasFocus) -> updateSheetData((EditText)v, false));
        holder.title.setOnFocusChangeListener((v, hasFocus) -> updateSheetData((EditText)v, true));
        Views.setOneTimeOnClickListener(holder.removeButton, removeSheet);
    }

    private void updateSheetData(EditText textField, boolean isTitle) {
        SheetData data = (SheetData)textField.getTag();
        if (isTitle)
            data.setTitle(textField.getText().toString());
        else
            data.setText(textField.getText().toString());
        saveEditData();
    }

    private View.OnClickListener removeSheet = view -> {
        MyViewHolder holder = (MyViewHolder)view.getTag();
        removeItem(holder.getAdapterPosition());

    };

    private void removeItem(int pos) {
        saveEditData();
        sheetDataList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, sheetDataList.size() + 1);
        saveEditData();
    }

    void saveEditData() {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preferences), Context.MODE_PRIVATE);
        SheetData.removeSheets(preferences, row);
        for (int i=0; i < sheetDataList.size(); i++)
            sheetDataList.get(i).save(preferences, row, i);
        SheetData.saveCount(preferences, row, sheetDataList.size());
    }

    @Override
    public int getItemCount() {
        return sheetDataList.size() + 1;
    }
}
