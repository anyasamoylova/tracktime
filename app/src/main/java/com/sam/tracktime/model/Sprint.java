package com.sam.tracktime.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity (tableName = "sprint", foreignKeys = {
        @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "item_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Tag.class, parentColumns = "id", childColumns = "tag_id", onDelete = ForeignKey.SET_NULL)
})
public class Sprint {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    @ColumnInfo(name = "item_id")
    private Integer itemId;
    @ColumnInfo(name = "tag_id")
    private Integer tagId;
    @NonNull
    //TODO if sprint starts in one day and finishes in other we should split it in statistic
    @ColumnInfo(name = "start_day")
    private Long startDay;
    @ColumnInfo(name = "finish_day")
    private Long finishDay;
    @ColumnInfo(name = "start_time")
    private Long startTime;
    @ColumnInfo(name = "finish_time")
    private Long finishTime;
    //TODO add category
    @ColumnInfo(name = "comment")
    private String comment;

    public Sprint() {}

    @Ignore
    public Sprint(@NotNull Integer itemId, Long timestamp, Integer tagId) {
        this.itemId = itemId;
        this.startTime = timestamp % (86400000L);
        this.startDay = timestamp - startTime;
        this.finishDay = startDay;
        this.finishTime = startTime;
        this.tagId = tagId;
    }

    @Ignore
    public Sprint(@NotNull Integer itemId, Long timestampS, Long timestampF) {
        this.itemId = itemId;
        this.startTime = timestampS % (86400000L);
        this.startDay = timestampS - startTime;
        this.finishTime = timestampF % (86400000L);
        this.finishDay = timestampF - finishTime;
        this.tagId = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Long getStartDay() {
        return startDay;
    }

    public void setStartDay(Long startDay) {
        this.startDay = startDay;
    }

    public Long getFinishDay() {
        return finishDay;
    }

    public void setFinishDay(Long finishDay) {
        this.finishDay = finishDay;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void finishSprint(long timestamp) {
        this.finishTime = timestamp % (86400000L);
        this.finishDay = timestamp - finishTime;
    }

}
