package com.sam.tracktime.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class YAxisFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float hours, AxisBase axis) {
        int answer = (int) hours;
        return "" + answer ;
    }
}
