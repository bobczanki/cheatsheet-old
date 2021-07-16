package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bobczanki.cheatsheet.settings.FontFamilySetting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SheetDataAdapter extends RecyclerView.Adapter<SheetDataAdapter.MyViewHolder> {
    static final String ROW_MESSAGE = "THIS_MESSAGE_CONTAINS_ROW_OF_CLICKED_SHEET";
    static final String COL_MESSAGE = "THIS_MESSAGE_CONTAINS_COL_OF_CLICKED_SHEET";
    static final String EDIT_MESSAGE = "THIS_MESSAGE_CONTAINS_EDITED_ROW";
    static final String LICENSE_MESSAGE = "THIS_MESSAGE_SAYS_IF_APP_IS_LICENSED";
    private List<SheetData> sheetDataList;
    private int row;
    private Activity activity;
    private Billing billing;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, text;
        private FrameLayout textForeground;
        private ImageView img;
        private FloatingActionButton editButton;

        private MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            text = view.findViewById(R.id.text);
            editButton = view.findViewById(R.id.edit);
            img = view.findViewById(R.id.img);
            textForeground = view.findViewById(R.id.textForeground);
        }
    }

    SheetDataAdapter(List<SheetData> sheetDataList, int row, Activity activity, Billing billing) {
        this.sheetDataList = sheetDataList;
        this.row = row;
        this.activity = activity;
        this.billing = billing;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sheet_data, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == sheetDataList.size())
            initEditButton(holder);
        else
            initSheetData(holder, position);
    }

    private void initEditButton(MyViewHolder holder) {
        Views.setGone(holder.text, holder.title, holder.img, holder.textForeground);
        Views.setVisible(holder.editButton);
        holder.editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EditActivity.class);
            intent.putExtra(EDIT_MESSAGE, row);
            intent.putExtra(LICENSE_MESSAGE, billing.isLicensed());
            activity.startActivity(intent);
        });
    }

    private void initSheetData(MyViewHolder holder, int position) {
        Views.setGone(holder.editButton);
        Views.setVisible(holder.text, holder.title, holder.img, holder.textForeground);
        SheetData sheet = sheetDataList.get(position);
        holder.title.setText(sheet.getTitle());
        setUpTextField(holder, sheet.getText(), position);
        Images.imageToView(activity, holder.img, sheet.getImagePath());
    }

    private void setUpTextField(MyViewHolder holder, String text, int position) {
        holder.text.setTypeface(FontFamilySetting.getFamily(activity));
        holder.text.setText(text);
        holder.text.setTag(position);
        holder.text.setOnClickListener(onClickText);
    }

    private View.OnClickListener onClickText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, SheetActivity.class);
            intent.putExtra(ROW_MESSAGE, row);
            intent.putExtra(COL_MESSAGE, (int)v.getTag());
            activity.startActivity(intent);
        }
    };

    void refresh(SharedPreferences preferences) {
        sheetDataList = SheetData.loadSheets(preferences, row);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sheetDataList.size() + 1;
    }
}
