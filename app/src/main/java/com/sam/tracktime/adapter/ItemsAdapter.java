
package com.sam.tracktime.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.tracktime.R;
import com.sam.tracktime.model.Item;
import com.sam.tracktime.model.MyTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemsAdapter extends RecyclerView.Adapter {
    private List<Item> items = new ArrayList<>();
    private final LayoutInflater inflater;
    private OnItemClickListener clickListener;
    private OnLongItemClickListener longClickListener;
    private OnDropAtItemListener dropListener;
    private boolean isEditMode;
    private final Context context;

    public ItemsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }

    public void setItems(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public Item getItemAt(int position) {
        return items.get(position);
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public interface OnLongItemClickListener {
        void onLongItemClicked(int position);
    }

    public interface OnDropAtItemListener {
        void onDropAtItemListener(int position);
    }

    public void setOnItemClickListener (OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnLongItemClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setOnDropAtItemListener (OnDropAtItemListener dropListener) {
        this.dropListener = dropListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(v, clickListener, longClickListener, dropListener, isEditMode, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemsAdapter.ViewHolder myHolder = (ViewHolder) holder;
        myHolder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView icon;
        private final TextView chronometer;
        private final boolean isEditMode;
        private final ImageView editIcon;
        private final ImageView circle;
        private final Context context;

        public ViewHolder(
                @NonNull View itemView,
                final OnItemClickListener clickListener,
                final OnLongItemClickListener longClickListener,
                final OnDropAtItemListener dropListener,
                boolean isEditMode,
                final Context context) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.icon = itemView.findViewById(R.id.icon);
            this.chronometer = itemView.findViewById(R.id.chronometer);
            this.isEditMode = isEditMode;
            this.editIcon = itemView.findViewById(R.id.editIcon);
            this.circle = itemView.findViewById(R.id.circle);
            this.context = context;
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClicked(position);
                    }
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onLongItemClicked(position);
                        return true;
                    }
                }
                return false;
            });
            itemView.setOnDragListener((v, event) -> {
                if (dropListener != null) {
                    int action = event.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                        case DragEvent.ACTION_DRAG_LOCATION:
                            return true;
                        case DragEvent.ACTION_DRAG_ENTERED:
                        case DragEvent.ACTION_DRAG_EXITED:
                        case DragEvent.ACTION_DRAG_ENDED:
                            v.invalidate();
                            return true;
                        case DragEvent.ACTION_DROP:
                            v.invalidate();
                            int position = getBindingAdapterPosition();
                            dropListener.onDropAtItemListener(position);
                            return true;
                    }
                }
                return false;
            });
        }

        public void bind (Item item) {
            //add edit icon if user's in edit mode
            if (isEditMode) {
                editIcon.setVisibility(View.VISIBLE);
            }
            //change color of running item
            if (MyTimer.getConnectedItem() != null && MyTimer.getConnectedItem().equals(item))
                circle.setColorFilter(context.getColor(R.color.colorLightRed));
            else circle.setColorFilter(context.getColor(R.color.colorLightBlue));
            name.setText(item.getName());
            icon.setImageResource(item.getIconResId());
            long sec = item.getTodayDuration()/1000;
            chronometer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, (sec % 60)));
        }
    }
}
