package com.sam.tracktime.data;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.sam.tracktime.model.Item;
import com.sam.tracktime.obj.ItemWithSprints;
import com.sam.tracktime.obj.ItemOrTagWithDuration;

import java.util.List;

public class ItemRepository {
    private final ItemDao itemDao;
    private final LiveData<List<Item>> items;

    public ItemRepository (Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        itemDao = database.getItemDao();
        items = itemDao.getAllItems();
    }

    public ItemRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        itemDao = database.getItemDao();
        items = itemDao.getAllItems();
    }

    public void insert(Item item) {
        new InsertItemAsyncTask(itemDao).execute(item);
    }

    public void update(Item item) {
        new UpdateItemAsyncTask(itemDao).execute(item);
    }

    public void delete (Item item) {
        new DeleteItemAsyncTask(itemDao).execute(item);
    }

    public void deleteById(int id) {
        new DeleteItemByIdAsyncTask(itemDao).execute(id);
    }

    public LiveData<List<Item>> getAllItems() {
        return items;
    }

    public LiveData<List<ItemOrTagWithDuration>> getItemWithDuration(long time) {
        return itemDao.getItemWithDuration(time);
    }

    public LiveData<List<ItemWithSprints>> getItemsWithSprints() {
        return itemDao.getItemsWithSprints();
    }

    public void resetTodayDuration() {
        new ResetItemsTodayDurationAsyncTask(itemDao).execute();
    };

    private static class InsertItemAsyncTask extends AsyncTask<Item, Void, Void> {
        private final ItemDao itemDao;

        private InsertItemAsyncTask(ItemDao itemDao){
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            itemDao.insert(items[0]);
            return null;
        }
    }

    private static class UpdateItemAsyncTask extends AsyncTask <Item, Void, Void> {
        private final ItemDao itemDao;

        private UpdateItemAsyncTask(ItemDao itemDao){
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            itemDao.update(items[0]);
            return null;
        }
    }

    private static class DeleteItemAsyncTask extends AsyncTask <Item, Void, Void> {
        private final ItemDao itemDao;

        private DeleteItemAsyncTask(ItemDao itemDao){
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            itemDao.delete(items[0]);
            return null;
        }
    }

    private static class DeleteItemByIdAsyncTask extends AsyncTask <Integer, Void, Void> {
        private final ItemDao itemDao;

        private DeleteItemByIdAsyncTask(ItemDao itemDao){
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            itemDao.deleteById(integers[0]);
            return null;
        }
    }

    private static class ResetItemsTodayDurationAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ItemDao itemDao;

        private ResetItemsTodayDurationAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            itemDao.resetTodayDuration();
            return null;
        }
    }

}
