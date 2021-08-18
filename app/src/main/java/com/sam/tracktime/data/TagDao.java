package com.sam.tracktime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sam.tracktime.model.Tag;

import java.util.List;

@Dao
public interface TagDao {
    @Insert
    void insert(Tag tag);

    @Delete
    void delete(Tag tag);

    @Update
    void update(Tag tag);

    @Query("SELECT * FROM tag WHERE item_id=:id")
    LiveData<List<Tag>> getTagsByItemId(Integer id);


}
