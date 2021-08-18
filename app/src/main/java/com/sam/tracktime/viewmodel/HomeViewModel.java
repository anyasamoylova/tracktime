package com.sam.tracktime.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sam.tracktime.data.ItemRepository;
import com.sam.tracktime.data.TagRepository;
import com.sam.tracktime.model.Item;
import com.sam.tracktime.model.MyTimer;
import com.sam.tracktime.model.Tag;

import java.util.List;

//connected with Item.class
public class HomeViewModel extends AndroidViewModel {
    private final String TAG = "HomeViewModel";

    private final ItemRepository itemRepository;
    //private final SprintRepository sprintRepository;
    private final TagRepository tagRepository;

    private final LiveData<List<Item>> items;
    private final MyTimer timer;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        //sprintRepository = new SprintRepository(application);
        tagRepository = new TagRepository(application);
        items = itemRepository.getAllItems();
        timer = MyTimer.getInstance(application);
    }

    public void insert(Item item) {
        Log.i(TAG, "inserts item with id: " + item.getId());
        itemRepository.insert(item);
    }

    public void update(Item item) {
        Log.i(TAG, "updates item with id: " + item.getId());
        itemRepository.update(item);
    }

    public void deleteById(int id) {
        Log.i(TAG, "deletes item with id: " + id);
        itemRepository.deleteById(id);
    }

    public void insert(Tag tag) {
        Log.i(TAG, "insert tag with name: " + tag.getName());
        tagRepository.insert(tag);
    }

    public LiveData<List<Item>> getAllItems() {
        return items;
    }

    public void toggleItem(Item item) {
        timer.toggleItem(item);
    }

    public void toggleTimer() {
        if (MyTimer.getRunningState().getValue().equals(MyTimer.STATE_FINISHED)) {
            MyTimer.setTimer(MyTimer.getLastTimeSetting(), MyTimer.getTagId(), MyTimer.getConnectedItem());
        } else
            MyTimer.toggleTimer();
    }

    public void changeItemNameAndResId (int id, String itemName, int itemResId, long itemDuration) {
        Item item = new Item(id, itemName, itemResId, itemDuration);
        update(item);
    }

    public void removeIconOfItem(int itemId) {
        Item item = findItemById(itemId);
        item.setRemoved(true);
        update(item);
    }

    public Item findItemById(int itemId) {
        //TODO change to calling the database
        if (items.getValue() != null) {
            for (Item item : items.getValue()) {
                if (item.getId() == itemId) {
                    return item;
                }
            }
        }
        return null;
    }

    //TODO here we should take Item for connect
    public void setTimer(Long timeMsLeft, Integer itemId, Integer tagId) {
        Item item = findItemById(itemId);
        timer.setTimer(timeMsLeft, tagId, item);
    }

    public MutableLiveData<Long> getTimeMsLeft () {
        return MyTimer.getCurrentTimeMs();
    }

    public MutableLiveData<Integer> getTimerState() {
        return MyTimer.getRunningState();
    }

    public LiveData<List<Tag>> getAllTagsByItemId(Integer itemId) {
        return tagRepository.getTagsByItemId(itemId);
    }

    public String getTimeAsStr() {
         return MyTimer.getTimeAsStr();
    }

}
