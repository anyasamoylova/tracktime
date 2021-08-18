package com.sam.tracktime.model;

import androidx.annotation.DrawableRes;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.Nullable;

@Entity (tableName = "item")
public class Item {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "time_ms")
    private Long todayDuration;
    @ColumnInfo(name = "icon_res_id")
    @DrawableRes
    private Integer iconResId;
    @ColumnInfo(name = "is_removed")
    private Boolean isRemoved;
    //index in list of items on home screen
//    @ColumnInfo(name = "index")
//    private Integer index;

    public Item() {
    }

    @Ignore
    public Item(String name, Integer iconId) {
        this.name = name;
        this.iconResId = iconId;
        todayDuration = 0L;
        isRemoved = false;
    }

    @Ignore
    public Item(Integer id, String name, Integer iconId, Long duration) {
        this.id = id;
        this.name = name;
        this.iconResId = iconId;
        this.todayDuration = duration;
        isRemoved = false;
    }

    public String getName() {
        return name;
    }

    public Long getTodayDuration() {
        return todayDuration;
    }

    public Integer getIconResId() {
        return iconResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTodayDuration(Long todayDuration) {
        this.todayDuration = todayDuration;
    }

    public void setIconResId(Integer iconResId) {
        this.iconResId = iconResId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void increaseDuration(){
        todayDuration += 1000;
    }

    public Boolean getRemoved() {
        return isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Item))
            return false;
        else return ((Item) obj).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }
}
