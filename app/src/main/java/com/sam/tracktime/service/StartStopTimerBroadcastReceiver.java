package com.sam.tracktime.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sam.tracktime.model.MyTimer;
import com.sam.tracktime.utils.WakeLockManager;

public class StartStopTimerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        changeWakeLock(context);
        changeTimerState();

    }

    private void changeWakeLock(Context context) {
        //if timer is running or finished, when it stops, wakeLock mode should be stopped too
        if (MyTimer.getRunningState().getValue().equals(MyTimer.STATE_RUNNING) ||
                MyTimer.getRunningState().getValue().equals(MyTimer.STATE_FINISHED)) {
            WakeLockManager.getWakeLock(context).release(); }
        //if timer is stopped, wakeLock should be acquired
        else {
            WakeLockManager.getWakeLock(context).acquire();
        }

    }

    private void changeTimerState() {
        //if timer running or stop - toggle timer
        if (MyTimer.getRunningState().getValue().equals(MyTimer.STATE_RUNNING) ||
                MyTimer.getRunningState().getValue().equals(MyTimer.STATE_STOPPED)) {
            MyTimer.toggleTimer();
        } else { //if timer is finished
            MyTimer.setTimer(MyTimer.getLastTimeSetting(), MyTimer.getTagId(), MyTimer.getConnectedItem());
        }
    }
}
