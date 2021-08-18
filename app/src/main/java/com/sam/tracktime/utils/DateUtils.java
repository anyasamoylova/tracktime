package com.sam.tracktime.utils;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;

public class DateUtils {
    public final static long MILLIS_IN_DAY = 86400000L;
    public static  List<Long> getLastNDays(int n) {
        List<Long> days = new ArrayList<>(n);
        long today = System.currentTimeMillis();
        today = today - (today % MILLIS_IN_DAY);
        for (int i = 0; i < n; i++) {
            days.add(today);
            today -= MILLIS_IN_DAY;
        }
        return days;
    }
}
