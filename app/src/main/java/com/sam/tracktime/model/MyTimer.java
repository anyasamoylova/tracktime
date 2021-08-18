package com.sam.tracktime.model;

import android.app.Application;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.sam.tracktime.data.ItemRepository;
import com.sam.tracktime.data.SprintRepository;

import org.joda.time.DateTimeZone;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Singleton;

@Singleton
public class MyTimer {
    private static final String TAG = "MyTimer";
    public static final Integer STATE_STOPPED = 0;
    public static final Integer STATE_RUNNING = 1;
    public static final Integer STATE_FINISHED = 2;

    private static MyTimer instance;
    private static Long lastTimeSetting;
    private static final MutableLiveData<Long> currentTimeMs = new MutableLiveData<>();
    private static final MutableLiveData<Integer> runningState = new MutableLiveData<>();
    private static Item connectedItem;
    private static boolean isCountDown;
    private static Integer tagId;
    private static Sprint runningSprint;
    private static ChronometerTask chronometerTask;
    private static Timer systemTimer;
    private static Ringtone ringtoneSound;

    private static ItemRepository itemRepository;
    private static SprintRepository sprintRepository;

    private MyTimer(Application application) {
        currentTimeMs.setValue(0L);
        lastTimeSetting = 0L;
        connectedItem = null;
        isCountDown = false;
        runningState.setValue(STATE_STOPPED);
        itemRepository = new ItemRepository(application);
        sprintRepository = new SprintRepository(application);
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtoneSound = RingtoneManager.getRingtone(application.getApplicationContext(), ringtoneUri);
    }

    public static MyTimer getInstance(Application application) {
        if (instance == null) {
            instance = new MyTimer(application);
        }
        return instance;
    }

    public static MutableLiveData<Long> getCurrentTimeMs() {
        return currentTimeMs;
    }

    public static void setCurrentTimeMs(Long currentTimeMs) {
        MyTimer.currentTimeMs.postValue(currentTimeMs);
    }

    public static Item getConnectedItem() {
        return connectedItem;
    }

    public static MutableLiveData<Integer> getRunningState() {
        return runningState;
    }

    public static void setRunning(Integer state) {
        runningState.setValue(state);
    }

    public static Integer getTagId() {
        return tagId;
    }

    public static Long getLastTimeSetting() {
        return lastTimeSetting;
    }

    public static void decrease() {
        if (currentTimeMs.getValue() == null)
            currentTimeMs.postValue(0L);
        currentTimeMs.postValue(currentTimeMs.getValue() - 1000);
        if (currentTimeMs.getValue()/1000 == 0) {

            runningState.postValue(STATE_FINISHED);
//            currentTimeMs.postValue(0L);

            if (ringtoneSound != null) {
                ringtoneSound.play();
            }
        }
    }

    public static void updateTime() {
        if (connectedItem != null) {
            setCurrentTimeMs(connectedItem.getTodayDuration());
        }

    }

    public static void setTimer(Long timeMsLeft, Integer tag, Item item) {
        if (ringtoneSound != null) {
            ringtoneSound.stop();
        }
        if (runningState.getValue() != null && (runningState.getValue().equals(STATE_RUNNING) ||
                runningState.getValue().equals(STATE_FINISHED))) {
            stopItem();
        }
        isCountDown = true;
        setCurrentTimeMs(timeMsLeft);
        lastTimeSetting = timeMsLeft;
        tagId = tag;
        if (item != null) {
           connectedItem = item;
        }
    }

    public void toggleItem(Item item) {
        isCountDown = false;
        tagId = null;
        if (connectedItem != null && runningState.getValue().equals(STATE_RUNNING)) {
            if (connectedItem.equals(item)) {
                //want to stop current chronometer
                stopItem();
            } else {
                //want to stop current chronometer and start new
                stopItem();
                runItem(item);
            }
        } else
            runItem(item);
    }

    public static void toggleTimer() {
        if ((runningState.getValue() == null || runningState.getValue().equals(STATE_STOPPED)) && connectedItem != null) {
            runItem(connectedItem);
        } else if (connectedItem != null) {
            stopItem();
        }
    }

    private static void runItem(Item item) {
        setRunning(STATE_RUNNING);
        connectedItem = item;
        runningSprint = new Sprint(item.getId(), System.currentTimeMillis(), MyTimer.getTagId());
        chronometerTask = new ChronometerTask();
        systemTimer = new Timer();
        systemTimer.schedule(chronometerTask, 1000L, 1000L);
        Log.i(TAG, "item with id " + connectedItem.getId() + "was started");
    }

    private static void stopItem() {
        setRunning(STATE_STOPPED);
        systemTimer.cancel();
        runningSprint.finishSprint(System.currentTimeMillis());
        sprintRepository.insert(runningSprint);
        Log.i(TAG, "item with id " + connectedItem.getId() + "was stopped");
    }

    public static void addTenMin() {
        setTimer(600000L, tagId, connectedItem);
    }

    public static String getTimeAsStr() {
        String ans = "";
        Long sec = currentTimeMs.getValue()/1000;
        if (sec < 0) {
            ans += "-";
            sec = Math.abs(sec);
        }
        ans += String.format(Locale.getDefault(), "%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, (sec % 60));
        return ans;
    }

    static class ChronometerTask extends TimerTask {
        @Override
        public void run() {
            connectedItem.increaseDuration();
            itemRepository.update(connectedItem);
            if (isCountDown) {
                decrease();
            } else {
                updateTime();
            }
        }
    }

}
