package com.sam.tracktime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.PieEntry;
import com.sam.tracktime.R;

import java.util.List;
import java.util.Locale;

public class PieChartAdapter extends RecyclerView.Adapter {
    private List<PieEntry> entries;
    private int[] colors;
    LayoutInflater inflater;
    Context context;

    public PieChartAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setEntries(List<PieEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.sprint, parent, false);
        return new PieChartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PieChartAdapter.ViewHolder myHolder = (PieChartAdapter.ViewHolder) holder;
        myHolder.bind(entries.get(position), colors[position % colors.length], context);
    }

    @Override
    public int getItemCount() {
        return entries == null? 0 : entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView duration;

        public ViewHolder(@NonNull View sprintView) {
            super(sprintView);
            this.itemName = sprintView.findViewById(R.id.tvTimeInterval);
            this.duration = sprintView.findViewById(R.id.tvDuration);
        }

        public void bind(PieEntry entry, int color, Context context) {
            int sec = (int) entry.getValue();
            String durationStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", sec / 3600, sec % 3600 / 60, sec % 60);
            itemName.setText(entry.getLabel());
            duration.setText(durationStr);
            itemName.setTextColor(ContextCompat.getColor(context, color));
            duration.setTextColor(ContextCompat.getColor(context, color));
        }

    }
}
