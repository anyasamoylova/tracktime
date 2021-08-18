package com.sam.tracktime.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sam.tracktime.model.Item;
import com.sam.tracktime.model.Sprint;
import com.sam.tracktime.model.Tag;

import javax.inject.Singleton;

@Singleton
@Database(entities = {Item.class, Sprint.class, Tag.class}, version = 21)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract ItemDao getItemDao();
    public abstract SprintDao getSprintDao();
    public abstract TagDao getTagDao();

    //only one thread can get access to this method
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "app_database")
                        //delete old version of db
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }


}
