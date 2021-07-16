package com.bobczanki.cheatsheet;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static com.bobczanki.cheatsheet.SheetDataAdapter.COL_MESSAGE;
import static com.bobczanki.cheatsheet.SheetDataAdapter.ROW_MESSAGE;

public class MiniSheetDataAdapter extends RecyclerView.Adapter<MiniSheetDataAdapter.MyViewHolder> {
    private List<SheetData> sheetDataList;
    private int row;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        private MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.miniTitle);
        }
    }

    MiniSheetDataAdapter(List<SheetData> sheetDataList, int row, Activity activity) {
        this.sheetDataList = sheetDataList;
        this.row = row;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mini_sheet_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        initTitle(holder, position);
    }

    private void initTitle(MyViewHolder holder, int position) {
        SheetData sheetData = sheetDataList.get(position);
        holder.title.setText(sheetData.getTitle());
        holder.title.setTag(position);
        holder.title.setOnClickListener(onClickTitle);
    }

    private View.OnClickListener onClickTitle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, SheetActivity.class);
            intent.putExtra(ROW_MESSAGE, row);
            intent.putExtra(COL_MESSAGE, (int)v.getTag());
            activity.startActivity(intent);
            activity.finish();
        }
    };

    @Override
    public int getItemCount() {
        return sheetDataList.size();
    }
}
