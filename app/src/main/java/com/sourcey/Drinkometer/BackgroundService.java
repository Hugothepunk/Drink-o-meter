package com.sourcey.Drinkometer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BackgroundService extends Service {

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            // Return this instance of BackgroundService so clients can call public methods
            return BackgroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
