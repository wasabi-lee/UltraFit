package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker;

import io.realm.RealmObject;

/**
 * Created by Wasabi on 4/21/2016.
 */
public class TimerLog extends RealmObject{
    private int warmup;
    private String workSecs;
    private String restSecs;
    private int reps;
    private int cooldown;
    private int total;
    private String workoutNames;

    public TimerLog() {
    }

    public TimerLog(int warmup,String workSecs, String restSecs, int reps, int cooldown, int total, String workoutNames) {
        this.warmup = warmup;
        this.reps = reps;
        this.cooldown = cooldown;
        this.total = total;
        this.workSecs = workSecs;
        this.restSecs = restSecs;
        this.workoutNames = workoutNames;
    }

    public int getTotalWorkOrRestSeconds(String rawSeconds){
            String[] splitWorkSecs = rawSeconds.split("=");
            int calculatedTotalSecs = 0;
            for (int i = 0; i < reps; i++) {
                calculatedTotalSecs += Integer.parseInt(splitWorkSecs[i]);
            }
            return calculatedTotalSecs;
    }

    public String getRestSecs() {
        return restSecs;
    }

    public void setRestSecs(String restSecs) {
        this.restSecs = restSecs;
    }

    public String getWorkoutNames() {
        return workoutNames;
    }

    public void setWorkoutNames(String workoutNames) {
        this.workoutNames = workoutNames;
    }

    public String getWorkSecs() {
        return workSecs;
    }

    public void setWorkSecs(String workSecs) {
        this.workSecs = workSecs;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWarmup() {
        return warmup;
    }

    public void setWarmup(int warmup) {
        this.warmup = warmup;
    }
}
