package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.HiitTimerActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.TimerThread;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import androidx.fragment.app.Fragment;

public class TimerFragment extends Fragment implements TimerThread.TimerActionCallback {

    public interface TimerOnTickListener {
        void onNewRound(int duration, int action, int reps);
        void onTick(int remainingSeconds);
        void onTimerFinished();
    }

    private static final String TAG = TimerFragment.class.getSimpleName();
    private TimerOnTickListener mCallback;

    private int[] workSeconds, restSeconds;
    private int warmupSeconds, cooldownSeconds, reps, currentReps;
    private int action;
    private long pausedPoint;

    public static final int ACTION_WARMUP = 0;
    public static final int ACTION_WORK = 1;
    public static final int ACTION_REST = 2;
    public static final int ACTION_COOLDOWN = 3;

    private TimerThread timerThread;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (TimerOnTickListener) context;
            retrieveArguments();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TimerOnTickListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (TimerOnTickListener) activity;
            retrieveArguments();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TimerOnTickListener");
        }
    }

    private void retrieveArguments() {
        warmupSeconds = getArguments().getInt(HiitTimerActivity.WARMUP_SECONDS_KEY);
        workSeconds = getArguments().getIntArray(HiitTimerActivity.WORKOUT_SECONDS_KEY);
        restSeconds = getArguments().getIntArray(HiitTimerActivity.RECOVERY_SECONDS_KEY);
        cooldownSeconds = getArguments().getInt(HiitTimerActivity.COOLDOWN_SECONDS_KEY);
        reps = workSeconds.length;
    }

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        action = ACTION_WARMUP;
        mCallback.onNewRound(warmupSeconds, action, currentReps);
        initiateTimerThread(warmupSeconds, 0);
        timerThread.start();
    }

    private void initiateTimerThread(int duration, long delay) {
        timerThread = new TimerThread(duration, this, new Handler(), delay);
    }

    @Override
    public void onTick(int seconds) {
        if (seconds == 0) {
            //TRANSITION
            performNextRound();
        } else {
            mCallback.onTick(seconds);
        }
    }

    private void performNextRound() {
        int newDuration = 0;
        switch (action) {
            case ACTION_WARMUP:
                currentReps++;
                //go to work round
                newDuration = workSeconds[currentReps-1];
                action = ACTION_WORK;
                break;
            case ACTION_WORK:
                //go to rest round
                newDuration = restSeconds[currentReps-1];
                action = ACTION_REST;
                break;
            case ACTION_REST:
                currentReps++;
                //if currentReps is bigger than reps, go to cooldown. Otherwise, perform next work round.
                if (currentReps > reps) {
                    newDuration = cooldownSeconds;
                    action = ACTION_COOLDOWN;
                } else {
                    newDuration = workSeconds[currentReps-1];
                    action = ACTION_WORK;
                }
                break;
            case ACTION_COOLDOWN:
                mCallback.onTimerFinished();
                return;
        }
        Log.d(TAG, "performNextRound: Action: "+ actionSwitcher(action) + ", CurrentReps: "+currentReps);
        mCallback.onNewRound(newDuration, action, currentReps);
        initiateTimerThread(newDuration, 0);
        timerThread.start();
    }

    @Override
    public void onPaused(long remainingMillis) {
        Log.d(TAG, "onPaused:Thread Paused milliseconds: "+remainingMillis);
        pausedPoint = remainingMillis;
    }

    public void pauseTimer() {
        timerThread.interrupt();
    }

    public void resumeTimer() {
        Log.d(TAG, "resumeTimer: Resume point: "+(int)pausedPoint/1000 + ", Delay: "+pausedPoint%1000);
        initiateTimerThread((int)pausedPoint/1000, pausedPoint%1000);
        timerThread.start();
    }

    public String actionSwitcher(int action) {
        switch (action) {
            case 0:
                return "WARM UP";
            case 1:
                return "WORK";
            case 2:
                return "REST";
            case 3:
                return "COOLDOWN";
        }
        return "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerThread != null) {
            timerThread.interrupt();
            timerThread = null;
        }
    }
}
