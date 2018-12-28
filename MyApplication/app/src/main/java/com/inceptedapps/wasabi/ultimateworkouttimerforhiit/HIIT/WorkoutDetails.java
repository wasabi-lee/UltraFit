package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit;

/**
 * Created by Wasabi on 4/24/2016.
 */
public class WorkoutDetails {
    private String mWorkoutName;
    private int mWorkSecs;
    private int mRestSecs;

    public WorkoutDetails(String mWorkoutName, int mWorkSecs, int mRestSecs) {
        this.mWorkoutName = mWorkoutName;
        this.mWorkSecs = mWorkSecs;
        this.mRestSecs = mRestSecs;
    }

    public int getRestSecs() {
        return mRestSecs;
    }

    public void setRestSecs(int mRestSecs) {
        this.mRestSecs = mRestSecs;
    }

    public String getWorkoutName() {
        return mWorkoutName;
    }

    public void setWorkoutName(String mWorkoutName) {
        this.mWorkoutName = mWorkoutName;
    }

    public int getWorkSecs() {
        return mWorkSecs;
    }

    public void setWorkSecs(int mWorkSecs) {
        this.mWorkSecs = mWorkSecs;
    }
}
