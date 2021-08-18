package com.sam.tracktime.chart;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam.tracktime.R;
import com.sam.tracktime.formatter.XAxisFormatter;
import com.sam.tracktime.formatter.YAxisFormatter;

import java.util.ArrayList;
import java.util.List;

public class ConfigLineChart {
    private final LineChart lineChart;
    private final Context context;
    private final List<Entry> entries;

    public ConfigLineChart(LineChart lineChart, Context context) {
        this.lineChart = lineChart;
        this.context = context;
        entries = new ArrayList<>();
        createLineChart();
    }

    private void createLineChart() {
        //config line chart
        lineChart.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawBorders(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new XAxisFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);

        YAxis yAxis = lineChart.getAxisLeft();
        lineChart.getAxisRight().setEnabled(false);
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(3, false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setValueFormatter(new YAxisFormatter());
    }

    public void updateData(String itemName) {
        //dataset config
        LineDataSet dataSet = new LineDataSet(entries, itemName);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.RED);
        dataSet.setLineWidth(1.8f);
        dataSet.setDrawCircles(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setFillFormatter((dataSet1, dataProvider) -> lineChart.getAxisLeft().getAxisMinimum());

        //update lineChart
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.animateX(500);
    }

    public void clearEntries() {
        entries.clear();
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }
}
