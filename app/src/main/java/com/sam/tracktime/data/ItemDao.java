package com.sam.tracktime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sam.tracktime.model.Item;
import com.sam.tracktime.obj.ItemOrTagWithDuration;
import com.sam.tracktime.obj.ItemWithSprints;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("DELETE FROM item WHERE id=:id")
    void deleteById(int id);

    @Query("SELECT * FROM item WHERE id=:id")
    Item getItemById (int id);

    //TODO добавить order by
    @Query("SELECT * FROM item WHERE NOT is_removed")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT id, name FROM item")
    LiveData<List<ItemWithSprints>> getItemsWithSprints();

    @Query("select item.id, item.name, sum(sprint.finish_time) - sum(sprint.start_time) as duration from item left join sprint on sprint.item_id = item.id WHERE sprint.start_day + sprint.start_time > :time group by item.id")
    LiveData<List<ItemOrTagWithDuration>> getItemWithDuration(long time);

    @Query("UPDATE item SET time_ms = 0")
    void resetTodayDuration();
}
