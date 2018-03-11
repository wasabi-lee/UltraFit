package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Wasabi on 3/30/2016.
 */
public abstract class PreciseCountdown extends Timer {
    private long totalTime, interval, delay;
    private TimerTask task;
    private long startTime = -1;
    private boolean restart = false, wasCancelled = false, wasStarted = false, resume = false;
    private long remainingTime, remainingTicks, remainingDelay;

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public PreciseCountdown(long totalTime, long interval) {
        this(totalTime, interval, 0);
    }

    public PreciseCountdown(long totalTime, long interval, long delay) {
        super("PreciseCountdown", true);
        this.delay = delay;
        this.interval = interval;
        this.totalTime = totalTime;
        this.task = getTask(totalTime);
    }


    public void start() {
        wasStarted = true;
        this.scheduleAtFixedRate(task, delay, interval);
    }

    public void restart() {
        if (!wasStarted) {
            this.task = getTask(totalTime);
            start();
        } else if (wasCancelled) {
            wasCancelled = false;
            this.task = getTask(totalTime);
            start();
        } else {
            this.restart = true;
        }
    }

    public void pause(long remainingTime, long remainingDelay) {
        this.remainingTime = remainingTime;
        this.remainingDelay = remainingDelay;
        this.task.cancel();
    }

    public void resume() {
        resume = true;
        wasStarted = true;
        this.task = getTask(remainingTime - 1000);
        this.scheduleAtFixedRate(task, remainingDelay, interval);
    }

    public void stop() {
        this.wasCancelled = true;
        this.task.cancel();
    }

    // Call this when there's no further use for this timer
    public void dispose() {
        cancel();
        purge();
    }

    private TimerTask getTask(final long totalTime) {
        return new TimerTask() {
            @Override
            public void run() {
                if (totalTime == 0) {
                    this.cancel();
                    startTime = -1;
                    onFinished();
                    return;
                }
                long timeLeft;
                if (startTime < 0 || restart || resume) {
                    startTime = scheduledExecutionTime();
                    timeLeft = totalTime;
                    restart = false;
                    resume = false;
                } else {
                    timeLeft = totalTime - (scheduledExecutionTime() - startTime);
                    if (timeLeft <= 0) {
                        this.cancel();
                        startTime = -1;
                        onFinished();
                        return;
                    }
                }
                onTick(timeLeft);

            }
        };
    }

    public abstract void onTick(long timeLeft);

    public abstract void onFinished();
}