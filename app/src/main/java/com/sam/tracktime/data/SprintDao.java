package com.sam.tracktime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sam.tracktime.obj.DayWithDuration;
import com.sam.tracktime.obj.ItemOrTagWithDuration;
import com.sam.tracktime.model.Sprint;

import java.util.List;

@Dao
public interface SprintDao {
    @Insert()
    void insert(Sprint sprint);

    @Update
    void update(Sprint sprint);

    @Update
    void delete(Sprint sprint);

    @Query("SELECT * FROM sprint")
    LiveData<List<Sprint>> getAllSprints();

    @Query("SELECT start_day, SUM(finish_time) - SUM(start_time) AS duration FROM sprint WHERE start_day > :timestamp AND item_id = :id GROUP BY start_day")
    LiveData<List<DayWithDuration>> getDurationByDate(Long timestamp, Integer id);

    @Query("SELECT * from sprint WHERE id = :id")
    LiveData<Sprint> getSprintById(Integer id);

    @Query("SELECT * FROM sprint WHERE item_id =:id ORDER BY start_day/1000 + start_time/1000")
    LiveData<List<Sprint>> getSprintsByItemId(int id);

    @Query("select tag.id, tag.name, sum(sprint.finish_time) - sum(sprint.start_time) as duration from tag left join sprint on sprint.tag_id = tag.id WHERE sprint.start_day + sprint.start_time > :time AND sprint.item_id = :id group by tag.id")
    LiveData<List<ItemOrTagWithDuration>> getTagWithDurationByStartTimeAndItemId(long time, Integer id);

}
