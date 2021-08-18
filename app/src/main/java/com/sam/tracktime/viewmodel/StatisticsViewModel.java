package com.sam.tracktime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.sam.tracktime.data.ItemRepository;
import com.sam.tracktime.data.SprintRepository;
import com.sam.tracktime.obj.ItemOrTagWithDuration;

import java.util.List;

public class StatisticsViewModel extends AndroidViewModel {
    private ItemRepository itemRepository;
    private MutableLiveData<Long> time = new MutableLiveData<>();
    private LiveData<List<ItemOrTagWithDuration>> items;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        items = Transformations.switchMap(time, t -> itemRepository.getItemWithDuration(t));
    }

    public LiveData<List<ItemOrTagWithDuration>> getItemsWithDuration() {
        return items;
    }

    public void setTime(Long time) {
        this.time.setValue(time);
    }

    public Long getTime() {
        return this.time.getValue();
    }

}
