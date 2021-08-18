package com.sam.tracktime.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.sam.tracktime.R;
import com.sam.tracktime.chart.ConfigLineChart;
import com.sam.tracktime.chart.ConfigPieChart;
import com.sam.tracktime.obj.DayWithDuration;
import com.sam.tracktime.obj.ItemOrTagWithDuration;
import com.sam.tracktime.utils.Constant;
import com.sam.tracktime.viewmodel.SprintViewModel;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

public class ItemStatisticFragment extends Fragment {
    private int itemId;
    private String itemName;
    private Long time;
    private SprintViewModel sprintViewModel;
    private RadioGroup radioGroup;

    //objects for lineChart
    private List<DayWithDuration> daysWithDuration;
    int n = 7;
    private ConfigLineChart lineChart;

    //objects for pieChart
    private List<ItemOrTagWithDuration> tagsWithDuration;
    private ConfigPieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_statistic, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            itemId = bundle.getInt(Constant.ITEM_ID, -1);
            itemName = bundle.getString(Constant.ITEM_NAME);
        }
        toolbar.setTitle(itemName.isEmpty()? "Statistic" : itemName + " statistic");

        sprintViewModel = new ViewModelProvider(
                getActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
        ).get(SprintViewModel.class);

        radioGroup = root.findViewById(R.id.rg);
        time = new DateTime(System.currentTimeMillis()).minus(Period.days(7)).getMillis();
        sprintViewModel.setTime(time, itemId);

        LineChart rawLineChart = root.findViewById(R.id.lineChart);
        lineChart = new ConfigLineChart(rawLineChart, getContext());

        RecyclerView rv = root.findViewById(R.id.rv);
        PieChart rawPieChart = root.findViewById(R.id.pieChart);
        pieChart = new ConfigPieChart(rv, rawPieChart, getContext());

        setRadioGroupCheckListener();

        sprintViewModel.getDaysWithDurationById().observe(getActivity(), dayWithDurations -> {
            daysWithDuration = sprintViewModel.parseDayWithDuration(n);
            updateData();
        });

        sprintViewModel.getTagWithDuration().observe(getActivity(), tagWithDuration -> {
            tagsWithDuration = tagWithDuration;
            updateData();
        });

        updateData();

        return root;
    }

    public void updateData() {
        lineChart.clearEntries();
        pieChart.clearEntries();
        //parse data
        if (daysWithDuration != null) {
            for (DayWithDuration day : daysWithDuration) {
                Entry entry = new Entry((float) day.startDay / 3600000L, (float) day.duration / 60000);
                lineChart.addEntry(entry);
            }
            lineChart.updateData(itemName);
        }
        if (tagsWithDuration != null) {
            for (ItemOrTagWithDuration tag : tagsWithDuration) {
                if (tag.duration != null) {
                    pieChart.addEntry(new PieEntry((float) (tag.duration / 1000L), tag.name));
                }
            }
            pieChart.updateData();
        }

    }

    private void setRadioGroupCheckListener() {
        //update time in sprintViewModel when checked
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            DateTime dt = new DateTime(System.currentTimeMillis());
            switch (checkedId) {
                case R.id.radio_7d:
                    time = dt.minus(Period.days(7)).getMillis();
                    n = 7;
                    break;
                case R.id.radio_1m:
                    time = dt.minus(Period.months(1)).getMillis();
                    n = 30;
                    break;
                case R.id.radio_3m:
                    time = dt.minus(Period.months(3)).getMillis();
                    n = 90;
                    break;
                case R.id.radio_6m:
                    time = dt.minus(Period.months(6)).getMillis();
                    n = 185;
                    break;
                case R.id.radio_y:
                    time = dt.minus(Period.years(1)).getMillis();
                    n = 365;
                    break;
            }
            sprintViewModel.setTime(time, itemId);
        });
    }
}
