package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Wasabi on 8/7/2016.
 */
public class TimerThread extends Thread {

    public interface TimerActionCallback {
        void onTick(int seconds);
        void onPaused(long remainingMillis);
    }

    private static final String TAG = TimerThread.class.getSimpleName();
    private Handler responseHandler;
    private TimerActionCallback mCallback;
    private int totalSecconds;
    private long delay;

    public TimerThread(int totalSecconds, TimerActionCallback mCallback, Handler responseHandler, long delay) {
        this.mCallback = mCallback;
        this.responseHandler = responseHandler;
        this.totalSecconds = totalSecconds;
        this.delay = delay;
    }

    @Override
    public void run() {
        if (totalSecconds <= 0 && delay <= 0) {
            fireTickCallback(0);
            return;
        }
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "run: Delay : "+delay);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
                fireTickCallback(totalSecconds);
                if (totalSecconds <= 0) return;
            } catch (InterruptedException e) {
                long stoppedTime = System.currentTimeMillis();
                long elapsedTime =  stoppedTime - startTime;
                Log.d(TAG, "run: Interrupted during thread sleeping. "+elapsedTime);
                firePauseCallback((totalSecconds*1000) + elapsedTime);
                return;
            }
        }

        boolean hasCycleTimeElapsed = false;
        int elapsedSeconds = 0;
        int cycleSecondsLeft = totalSecconds;
        long previousTick = System.currentTimeMillis();

        while (!hasCycleTimeElapsed && !isInterrupted()) {
            long currentTime = System.currentTimeMillis();
            if (checkForValidTime(previousTick, currentTime)) {
                previousTick = currentTime;
                elapsedSeconds = elapsedSeconds + 1;
                cycleSecondsLeft = totalSecconds - (elapsedSeconds);
                fireTickCallback(cycleSecondsLeft);
                hasCycleTimeElapsed = hasCycleTimeElapsed(cycleSecondsLeft);
            }
        }

        firePauseCallback((cycleSecondsLeft*1000)-1000 + (1000 - ((System.currentTimeMillis() - startTime) % 1000)));
        Log.d(TAG, "run: interrupted");
    }

    private void fireTickCallback(final int cycleSeconsLeft) {
        Log.d(TAG, "fireTickCallback: "+cycleSeconsLeft);
        responseHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onTick(cycleSeconsLeft);
            }
        });
    }

    private void firePauseCallback(final long currentMillis) {
        responseHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onPaused(currentMillis);
            }
        });
    }

    private boolean hasCycleTimeElapsed(int roundSecondsLeft) {
        return roundSecondsLeft <= 0;
    }

    private boolean checkForValidTime(long previousTick, long currentTime) {
        return currentTime - previousTick >= 1000;
    }
}
