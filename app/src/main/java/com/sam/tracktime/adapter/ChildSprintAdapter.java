package com.sam.tracktime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.tracktime.R;
import com.sam.tracktime.model.Sprint;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodParser;
import org.joda.time.format.PeriodPrinter;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class ChildSprintAdapter extends RecyclerView.Adapter {
    private List<Sprint> sprints = new ArrayList<>(50);
    private OnSprintClickListener clickListener;

    public interface OnSprintClickListener {
        void onSprintClicked(Sprint sprint);
    }

    public void setOnSprintClickListener(OnSprintClickListener listener) {
        this.clickListener = listener;
    }

    public ChildSprintAdapter() {}

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.sprint, parent, false);
        return new ViewHolder(v, clickListener, sprints);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChildSprintAdapter.ViewHolder myHolder = (ViewHolder) holder;
        myHolder.bind(sprints.get(position));
    }

    @Override
    public int getItemCount() {
        return sprints.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTimeInterval;
        private final TextView tvDuration;

        public ViewHolder(@NonNull View sprintView, final OnSprintClickListener clickListener, List<Sprint> sprints) {
            super(sprintView);
            this.tvTimeInterval = sprintView.findViewById(R.id.tvTimeInterval);
            this.tvDuration = sprintView.findViewById(R.id.tvDuration);
            sprintView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onSprintClicked(sprints.get(position));
                    }
                }
            });

        }

        public void bind(Sprint sprint) {
            long duration = sprint.getFinishTime() - sprint.getStartTime();
            tvTimeInterval.setText(parseTimeInterval(sprint.getStartTime(), sprint.getFinishTime()));
            tvDuration.setText(parseTime(duration));;
        }

        private String parseTime(Long time) {
            Duration duration = new Duration(time);
            long hour = duration.getStandardHours();
            long min = duration.getStandardMinutes() - hour * (60);
            long sec = duration.getStandardSeconds() - hour * (60 * 60) - min * 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);



//            DateTime dt = new DateTime(time);
//            return dt.toString("HH:mm:ss", Locale.);
        }

        private String parseTimeInterval(Long startTime, Long finishTime) {
            return parseTime(startTime) + " - " + parseTime(finishTime);
        }


    }
}
