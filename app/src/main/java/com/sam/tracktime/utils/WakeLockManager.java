package com.sam.tracktime.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

public class WakeLockManager {
    private static PowerManager.WakeLock wakeLock;

    public static PowerManager.WakeLock getWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "TrackTime::WakeLock");
        }
        return wakeLock;
    }
}
