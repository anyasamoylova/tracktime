package com.sam.tracktime.service;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sam.tracktime.data.AppDatabase;
import com.sam.tracktime.data.ItemRepository;
import com.sam.tracktime.model.MyTimer;

import java.util.Timer;

//triggered when alarm goes off
public class AlarmReceiver extends BroadcastReceiver {
    //updates item's today duration every day at 00:00
    //if timer's running now, splits sprint at 00:00
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean toggled = false;
        if (MyTimer.getConnectedItem() != null && MyTimer.getRunningState().getValue() != null &&
                MyTimer.getRunningState().getValue().equals(MyTimer.STATE_RUNNING)
        ) {
            MyTimer.toggleTimer();
            toggled = true;
            MyTimer.getConnectedItem().setTodayDuration(0L);
        }

        ItemRepository itemRepository = new ItemRepository(context);
        itemRepository.resetTodayDuration();

        if (toggled) {
            MyTimer.toggleTimer();
        }
    }
}
