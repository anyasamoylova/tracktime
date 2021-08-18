package com.sam.tracktime.chart;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sam.tracktime.R;
import com.sam.tracktime.adapter.PieChartAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConfigPieChart {
    private final PieChart pieChart;
    private final PieChartAdapter adapter;
    private final List<PieEntry> entries;
    private final Context context;

    private static final int[] pieChartColors = new int[] {
            R.color.colorGraphLilac,
            R.color.colorGraphDarkBlue,
            R.color.colorGraphBlue,
            R.color.colorGraphDarkGreen,
            R.color.colorGraphGreen,
            R.color.colorGraphHerbal,
            R.color.colorGraphHerbalYellow,
            R.color.colorGraphYellow,
            R.color.colorGraphOrange,
            R.color.colorGraphDarkOrange
    };

    public ConfigPieChart(RecyclerView rv, PieChart pieChart, Context context) {
        this.pieChart = pieChart;
        this.context = context;

        //create recycler view
        this.adapter = new PieChartAdapter(context);
        rv.setAdapter(adapter);
        adapter.setColors(pieChartColors);

        //create pie chart
        entries = new ArrayList<>();
        createPieChart();
    }

    private void createPieChart() {
        //config pie chart
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setExtraOffsets(25,5,25,5);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.getDescription().setEnabled(false);
    }

    public void updateData() {
        adapter.setEntries(entries);
        PieDataSet set = new PieDataSet(entries, "Statistic");
        set.setSliceSpace(2);
        set.setSelectionShift(3);
        set.setColors(pieChartColors, context);
        set.setValueLinePart1OffsetPercentage(70.f);
        set.setValueLinePart1Length(0.45f);
        set.setValueLinePart2Length(0.35f);
        set.setValueLineWidth(3f);
        set.setValueLineVariableLength(true);
        set.setUsingSliceColorAsValueLineColor(true);
        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(set);
        data.setValueTextSize(14f);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(900);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
    }

    public void clearEntries() {
        entries.clear();
    }

    public void addEntry(PieEntry pieEntry) {
        entries.add(pieEntry);
    }
}
