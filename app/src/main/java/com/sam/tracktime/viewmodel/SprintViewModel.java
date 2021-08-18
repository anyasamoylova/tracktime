package com.sam.tracktime.viewmodel;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.sam.tracktime.data.SprintRepository;
import com.sam.tracktime.data.TagRepository;
import com.sam.tracktime.model.Sprint;
import com.sam.tracktime.model.Tag;
import com.sam.tracktime.obj.DayWithDuration;
import com.sam.tracktime.obj.ItemOrTagWithDuration;
import com.sam.tracktime.utils.DateUtils;

import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

public class SprintViewModel extends AndroidViewModel {
    private final SprintRepository sprintRepository;
    private final TagRepository tagRepository;

    private final MutableLiveData<Long> sprintDate = new MutableLiveData<>();
    private final MutableLiveData<Long> sprintStartTime = new MutableLiveData<>();
    private final MutableLiveData<Long> sprintFinishTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> tagId = new MutableLiveData<>();
    private final MutableLiveData<Integer> itemId = new MutableLiveData<>();

    private final MutableLiveData<Pair<Long, Integer>> time = new MutableLiveData<>();
    private final LiveData<List<DayWithDuration>> daysWithDuration;
    private final LiveData<List<ItemOrTagWithDuration>> tags;

    public SprintViewModel(@NonNull Application application) {
        super(application);
        sprintRepository = new SprintRepository(application);
        tagRepository = new TagRepository(application);
        daysWithDuration = Transformations.switchMap(time, t -> sprintRepository.getDurationByDateAndId(t.first, t.second));
        tags = Transformations.switchMap(time, t -> sprintRepository.getTagWithDurationByStartTimeAndItemId(t.first, t.second));
    }

    public void insert(Tag tag) {
        tagRepository.insert(tag);
    }

    public LiveData<List<Tag>> getAllTagsByItemId(Integer itemId) {
        return tagRepository.getTagsByItemId(itemId);
    }

    public LiveData<List<Sprint>> getSprints(int itemId) {
        return sprintRepository.getSprintsByItemId(itemId);
    }

    public LiveData<Sprint> getSprintById(Integer id) {
        return sprintRepository.getSprintById(id);
    }

    public LiveData<List<ItemOrTagWithDuration>> getTagWithDuration() {
        return tags;
    }

    //keep NEW data of chosenSprint in local variable
    //old data keeps in sprint - object in order to cancel operation in any time
    public void updateData(Sprint sprint) {
        if (sprint != null) {
            sprintDate.setValue(sprint.getStartDay());
            sprintStartTime.setValue(sprint.getStartTime());
            sprintFinishTime.setValue(sprint.getFinishTime());
            tagId.setValue(sprint.getTagId());
            itemId.setValue(sprint.getItemId());
        }
    }

    public MutableLiveData<Long> getSprintDate() {
        return sprintDate;
    }

    public MutableLiveData<Long> getSprintStartTime() {
        return sprintStartTime;
    }

    public MutableLiveData<Long> getSprintFinishTime() {
        return sprintFinishTime;
    }

    public Integer getTagId() {
        return tagId.getValue();
    }

    public MutableLiveData<Integer> getItemId() {
        return itemId;
    }

    public void setTagId(Integer newTagId) {
        tagId.setValue(newTagId);
    }

    public void saveDate(DateTime newDate) {
        sprintDate.setValue(newDate.getMillis());
    }

    public void saveTime(boolean isStartTimerMode, Long newTime) {
        if (isStartTimerMode)
            sprintStartTime.setValue(newTime);
        else sprintFinishTime.setValue(newTime);
    }

    public void update(Sprint sprint) {
        sprint.setStartDay(sprintDate.getValue());
        sprint.setFinishDay(sprintDate.getValue());
        sprint.setStartTime(sprintStartTime.getValue());
        sprint.setFinishTime(sprintFinishTime.getValue());
        sprint.setTagId(tagId.getValue());
        sprintRepository.update(sprint);
    }

    public void setTime(Long time, Integer itemId) {
        this.time.setValue(Pair.create(time, itemId));
    }

    public LiveData<List<DayWithDuration>> getDaysWithDurationById() {
        return daysWithDuration;
    }

    public List<DayWithDuration> parseDayWithDuration(int n) {
        List<DayWithDuration> parsedDays = daysWithDuration.getValue();
        if (parsedDays != null) {
            for (Long day : DateUtils.getLastNDays(n)) {
                boolean hasDay = false;
                for (DayWithDuration dayFromDb : daysWithDuration.getValue()) {
                    if (day.equals(dayFromDb.startDay)) {
                        hasDay = true;
                        break;
                    }
                }
                if (!hasDay) {
                    parsedDays.add(new DayWithDuration(day));
                }
            }
        }
        return parsedDays.stream().sorted().collect(Collectors.toList());
    }
}
