package com.sam.tracktime.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sam.tracktime.model.Tag;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter {
    private final Context context;
    private final int resource;
    private final List<Tag> objects;
    public static boolean flag = false;

    //TODO why constructor is never used?
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Tag> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, resource, null);
        if (flag) {
            TextView textView = (TextView) convertView;
            textView.setText(objects.get(position).getName());
        }
        return convertView;
    }
}
