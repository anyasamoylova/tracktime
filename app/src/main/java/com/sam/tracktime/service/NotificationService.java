package com.sam.tracktime.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.sam.tracktime.R;
import com.sam.tracktime.model.MyTimer;
import com.sam.tracktime.ui.MainActivity;

import java.util.Locale;

//updates timer's info in background
public class NotificationService extends Service {
    public static final String CHANNEL_ID = "Foreground Service";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String title = intent.getStringExtra("itemName");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        //opportunity to run app on notification click
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        MyTimer.getCurrentTimeMs().observeForever(aLong -> {
            updateNotificationInfo(pendingIntent);
        });
        MyTimer.getRunningState().observeForever(isRunning -> {
            updateNotificationInfo(pendingIntent);
        });

        Notification notification = createDefaultNotification(pendingIntent);
        startForeground(1, notification);
        stopForeground(false);
        return START_NOT_STICKY;

    }

    private void updateNotificationInfo(PendingIntent pendingIntent){
        Notification notification;
        if (MyTimer.getConnectedItem() == null) {
            notification = createDefaultNotification(pendingIntent);
        } else {
            //if timer's running, here will be one button start or stop
            if (MyTimer.getRunningState().getValue().equals(MyTimer.STATE_RUNNING) ||
                    MyTimer.getRunningState().getValue().equals(MyTimer.STATE_STOPPED)){
                String buttonName = MyTimer.getRunningState().getValue().equals(MyTimer.STATE_RUNNING) ? "Stop" : "Start";
                long sec = MyTimer.getCurrentTimeMs().getValue() / 1000;

                Intent startPauseIntent = new Intent(this, StartStopTimerBroadcastReceiver.class);
                PendingIntent startPausePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, startPauseIntent, 0);

                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(MyTimer.getConnectedItem().getName())
                        .setContentText(MyTimer.getTimeAsStr())
                        .setSmallIcon(MyTimer.getConnectedItem().getIconResId())
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_baseline_pause_circle_filled_24, buttonName, startPausePendingIntent)
                        .build();

            } else {
                //if timer is finished, user can stop it or add 10 min
                long sec = MyTimer.getCurrentTimeMs().getValue() / 1000;

                Intent stopIntent = new Intent(this, StartStopTimerBroadcastReceiver.class);
                PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopIntent, 0);

                Intent addTenMinIntent = new Intent(this, AddMinTimerBroadcastReceiver.class);
                PendingIntent addMinPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, addTenMinIntent, 0);

                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(MyTimer.getConnectedItem().getName())
                        .setContentText(MyTimer.getTimeAsStr())
                        .setSmallIcon(MyTimer.getConnectedItem().getIconResId())
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_baseline_pause_circle_filled_24, "Stop", stopPendingIntent)
                        .addAction(R.drawable.ic_baseline_add_24, "Add 10 min", addMinPendingIntent)
                        .build();
            }
        }
        startForeground(1, notification);
        if (MyTimer.getRunningState().getValue().equals(MyTimer.STATE_STOPPED))
            stopForeground(false);
    }

    private Notification createDefaultNotification(PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TrackTime is working")
                .setSmallIcon(R.drawable.ic_baseline_panorama_fish_eye_24)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null, null);
            serviceChannel.enableVibration(false);
            serviceChannel.enableLights(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}