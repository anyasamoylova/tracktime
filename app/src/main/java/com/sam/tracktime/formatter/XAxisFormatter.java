package com.sam.tracktime.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.joda.time.DateTime;

public class XAxisFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        DateTime dt = new DateTime((long) value * 3600000L);
        return dt.getDayOfMonth() + "." + dt.getMonthOfYear();
    }
}
