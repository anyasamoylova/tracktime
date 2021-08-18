package com.sam.tracktime.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "tag", foreignKeys = {
        @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "item_id", onDelete = ForeignKey.CASCADE)
})
public class Tag {
    @PrimaryKey (autoGenerate = true)
    private Integer id;
    @NonNull
    @ColumnInfo(name = "item_id")
    private Integer itemId;
    @ColumnInfo(name = "name")
    String name;

    public Tag() {
    }

    @Ignore
    public Tag(@NotNull Integer itemId, String name) {
        this.itemId = itemId;
        this.name = name;
    }

    @Ignore
    public Tag(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NotNull
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(@NotNull Integer itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
