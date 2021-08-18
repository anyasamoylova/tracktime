package com.sam.tracktime.obj;

import androidx.room.ColumnInfo;

public class DayWithDuration implements Comparable<DayWithDuration>{
    @ColumnInfo(name = "start_day")
    public Long startDay;
    public Long duration;

    public DayWithDuration(Long startDay) {
        this.startDay = startDay;
        this.duration = 0L;
    }

    @Override
    public int compareTo(DayWithDuration anotherDay) {
        return startDay.compareTo(anotherDay.startDay);
    }
}
