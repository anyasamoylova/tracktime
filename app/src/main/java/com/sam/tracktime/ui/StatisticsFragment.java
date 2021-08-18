package com.sam.tracktime.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.sam.tracktime.R;
import com.sam.tracktime.chart.ConfigPieChart;
import com.sam.tracktime.obj.ItemOrTagWithDuration;
import com.sam.tracktime.viewmodel.StatisticsViewModel;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

public class StatisticsFragment extends Fragment {
    private StatisticsViewModel statisticsViewModel;
    private RadioGroup radioGroup;
    private List<ItemOrTagWithDuration> itemsWithDuration;
    private Long time;
    private ConfigPieChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        statisticsViewModel = new ViewModelProvider(
                getActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
        ).get(StatisticsViewModel.class);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Statistics");


        DateTime dt = new DateTime(System.currentTimeMillis());
        statisticsViewModel.setTime(dt.minus(Period.days(7)).getMillis());

        RecyclerView recyclerView = root.findViewById(R.id.rv);
        PieChart pieChart = root.findViewById(R.id.pieChart);
        chart = new ConfigPieChart(recyclerView, pieChart, getContext());

        radioGroup = root.findViewById(R.id.rg);

        setRadioGroupCheckListener();

        statisticsViewModel.getItemsWithDuration().observe(getActivity(), itemWithDurations -> {
            itemsWithDuration = itemWithDurations;
            updateData();
        });

        updateData();
        return root;
    }

    private void updateData() {
        chart.clearEntries();
        if (itemsWithDuration != null) {
            for (ItemOrTagWithDuration item : itemsWithDuration) {
                if (item.duration != null) {
                    chart.addEntry(new PieEntry((float) (item.duration/1000L), item.name));
                }
            }

            chart.updateData();
        }
    }

    private void setRadioGroupCheckListener() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            DateTime dt1 = new DateTime(System.currentTimeMillis());
            switch (checkedId) {
                case R.id.radio_7d:
                    time = dt1.minus(Period.days(7)).getMillis();
                    break;
                case R.id.radio_1m:
                    time = dt1.minus(Period.months(1)).getMillis();
                    break;
                case R.id.radio_3m:
                    time = dt1.minus(Period.months(3)).getMillis();
                    break;
                case R.id.radio_6m:
                    time = dt1.minus(Period.months(6)).getMillis();
                    break;
                case R.id.radio_y:
                    time = dt1.minus(Period.years(1)).getMillis();
                    break;
                case R.id.radio_all:
                    time = 0L;
                    break;
            }
            statisticsViewModel.setTime(time);
        });
    }
}
