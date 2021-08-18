package com.sam.tracktime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.tracktime.R;

import java.util.List;

public class IconsAdapter extends RecyclerView.Adapter {
    private final List<Integer> icons;
    private final LayoutInflater inflater;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public IconsAdapter(Context context, List<Integer> icons) {
        this.icons = icons;
        this.inflater = LayoutInflater.from(context);
    }

    public int getIconResId(int position) {
        return icons.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.icon, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IconsAdapter.ViewHolder myHolder = (ViewHolder) holder;
        myHolder.bind(icons.get(position));
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.image_icon);
            itemView.setOnClickListener(view -> {
                if (clickListener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClicked(position);
                    }
                }
            });
        }

        public void bind (Integer iconId) {
            icon.setImageResource(iconId);
        }
    }
}
