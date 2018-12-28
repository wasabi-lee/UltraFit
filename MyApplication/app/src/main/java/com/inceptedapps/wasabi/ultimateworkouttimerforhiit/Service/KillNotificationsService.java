package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Wasabi on 5/9/2016.
 */
public class KillNotificationsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(getClass().getSimpleName(), "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getSimpleName(), "onStartCommand");
        if (intent == null) {
            Log.d(getClass().getSimpleName(), "Killing notif");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(262);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }
}
