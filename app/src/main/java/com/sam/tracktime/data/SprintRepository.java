package com.sam.tracktime.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.sam.tracktime.obj.DayWithDuration;
import com.sam.tracktime.model.Sprint;
import com.sam.tracktime.obj.ItemOrTagWithDuration;

import java.util.List;

public class SprintRepository {
    private final SprintDao sprintDao;

    public SprintRepository (Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        sprintDao = database.getSprintDao();
    }

    public void insert(Sprint sprint) {
        new InsertSprintAsyncTask(sprintDao).execute(sprint);
    }

    public void update(Sprint sprint) {
        new UpdateSprintAsyncTask(sprintDao).execute(sprint);
    }

    public void delete(Sprint sprint) {
        new DeleteSprintAsyncTask(sprintDao).execute(sprint);
    }

    public LiveData<List<Sprint>> getAllSprints() {
        return sprintDao.getAllSprints();
    }

    public LiveData<List<DayWithDuration>> getDurationByDateAndId (long timestamp, int id) {
        return sprintDao.getDurationByDate(timestamp, id);
    }

    public LiveData<Sprint> getSprintById(Integer id) {
        return sprintDao.getSprintById(id);
    }

    public LiveData<List<Sprint>> getSprintsByItemId (int id) {
        return sprintDao.getSprintsByItemId(id);
    }

    public LiveData<List<ItemOrTagWithDuration>> getTagWithDurationByStartTimeAndItemId(long time, int id) {
        return sprintDao.getTagWithDurationByStartTimeAndItemId(time, id);
    }

    private static class InsertSprintAsyncTask extends AsyncTask<Sprint, Void, Void> {
        private final SprintDao sprintDao;

        private InsertSprintAsyncTask (SprintDao sprintDao){
            this.sprintDao = sprintDao;
        }

        @Override
        protected Void doInBackground(Sprint... sprints) {
            sprintDao.insert(sprints[0]);
            return null;
        }
    }

    private static class UpdateSprintAsyncTask extends AsyncTask<Sprint, Void, Void> {
        private final SprintDao sprintDao;

        private UpdateSprintAsyncTask (SprintDao sprintDao){
            this.sprintDao = sprintDao;
        }

        @Override
        protected Void doInBackground(Sprint... sprints) {
            sprintDao.update(sprints[0]);
            return null;
        }
    }

    private static class DeleteSprintAsyncTask extends AsyncTask<Sprint, Void, Void> {
        private final SprintDao sprintDao;

        private DeleteSprintAsyncTask (SprintDao sprintDao){
            this.sprintDao = sprintDao;
        }

        @Override
        protected Void doInBackground(Sprint... sprints) {
            sprintDao.delete(sprints[0]);
            return null;
        }
    }
}
