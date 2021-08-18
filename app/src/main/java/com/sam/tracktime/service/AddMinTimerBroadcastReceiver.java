package com.sam.tracktime.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.sam.tracktime.model.MyTimer;
import com.sam.tracktime.utils.WakeLockManager;

public class AddMinTimerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //stop the wakeLock mode
        WakeLockManager.getWakeLock(context).release();
        MyTimer.addTenMin();
    }
}
