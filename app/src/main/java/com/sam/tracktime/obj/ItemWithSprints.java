package com.sam.tracktime.obj;

import androidx.room.Relation;

import com.sam.tracktime.model.Sprint;

import java.util.List;

public class ItemWithSprints {
    public Integer id;
    public String name;

    @Relation(parentColumn = "id", entityColumn = "item_id")
    public List<Sprint> sprints;

}
