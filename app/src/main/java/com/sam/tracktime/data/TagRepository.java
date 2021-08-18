package com.sam.tracktime.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;

import com.sam.tracktime.model.Item;
import com.sam.tracktime.model.Tag;

import java.util.List;

public class TagRepository {
    private final TagDao tagDao;

    public TagRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        tagDao = database.getTagDao();
    }

    public void insert(Tag tag) {
        new InsertTagAsyncTask(tagDao).execute(tag);
    }

    public void update(Tag tag) {
        new UpdateTagAsyncTask(tagDao).execute(tag);
    }

    public void delete(Tag tag) {
        new DeleteTagAsyncTask(tagDao).execute(tag);
    }

    public LiveData<List<Tag>> getTagsByItemId (Integer itemId) {
        return tagDao.getTagsByItemId(itemId);
    }

    private static class InsertTagAsyncTask extends AsyncTask<Tag, Void, Void>{
        private final TagDao tagDao;

        public InsertTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            tagDao.insert(tags[0]);
            return null;
        }
    }

    private static class UpdateTagAsyncTask extends AsyncTask<Tag, Void, Void>{
        private final TagDao tagDao;

        public UpdateTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            tagDao.update(tags[0]);
            return null;
        }
    }

    private static class DeleteTagAsyncTask extends AsyncTask<Tag, Void, Void>{
        private final TagDao tagDao;

        public DeleteTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            tagDao.delete(tags[0]);
            return null;
        }
    }
}