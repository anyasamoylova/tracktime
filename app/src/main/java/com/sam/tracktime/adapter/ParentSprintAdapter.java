package com.sam.tracktime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.tracktime.R;
import com.sam.tracktime.model.Sprint;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParentSprintAdapter extends RecyclerView.Adapter {
    private Map<Long, List<Sprint>> sprintsByDate;
    private List<Long> dates;
    private ChildSprintAdapter.OnSprintClickListener listener;
    private DividerItemDecoration decoration;

    public ParentSprintAdapter(ChildSprintAdapter.OnSprintClickListener listener, DividerItemDecoration decoration) {
        this.listener = listener;
        this.decoration = decoration;
    }

    public void setSprints(Map<Long, List<Sprint>> sprintsByDate) {
        this.sprintsByDate = sprintsByDate;
        dates = new ArrayList<>();
        //reverse sorting
        dates.addAll(sprintsByDate.keySet().stream().sorted((o1, o2) -> o2.compareTo(o1)).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.day_sprint, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ParentSprintAdapter.ViewHolder myHolder = (ViewHolder) holder;
        myHolder.bind(dates.get(position), sprintsByDate.get(dates.get(position)), listener, decoration);
    }

    @Override
    public int getItemCount() {
        return sprintsByDate == null ? 0 : sprintsByDate.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textDate;
        private final RecyclerView rv;

        public ViewHolder(@NonNull View sprintView) {
            super(sprintView);
            this.textDate = sprintView.findViewById(R.id.tvDate);
            this.rv = sprintView.findViewById(R.id.list_sprint);
        }

        public void bind(Long date, List<Sprint> sprints, ChildSprintAdapter.OnSprintClickListener listener, DividerItemDecoration decoration) {
            DateTime dt = new DateTime(date);
            textDate.setText(dt.toString("dd.MM"));
            ChildSprintAdapter childAdapter = new ChildSprintAdapter();

            //set item decoration and delete last one
            rv.addItemDecoration(decoration);
            //rv.removeItemDecorationAt(sprints.size() - 1);

            rv.setAdapter(childAdapter);
            childAdapter.setSprints(sprints);
            childAdapter.setOnSprintClickListener(listener);
        }
    }
}
