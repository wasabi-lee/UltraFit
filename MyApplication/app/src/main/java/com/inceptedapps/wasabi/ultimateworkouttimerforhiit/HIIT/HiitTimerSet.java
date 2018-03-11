package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by Wasabi on 3/25/2016.
 */
@RealmClass
public class HiitTimerSet extends RealmObject{

    //TODO Make timer name dialog and revise the constructor!

    private String timerName;
    private int warmup;
    private int work;
    private int rest;
    private int reps;
    private int cooldown;
    private int total;
    private String workoutNames;
    private String workSeconds;
    private String restSeconds;

    public HiitTimerSet() {
    }

    public HiitTimerSet(int warmup, int work, int rest, int reps, int cooldown, int total, String timerName, String workoutNames, String workSeconds, String restSeconds) {
        this.warmup = warmup;
        this.work = work;
        this.rest = rest;
        this.reps = reps;
        this.cooldown = cooldown;
        this.total = total;
        this.timerName = timerName;
        this.workoutNames = workoutNames;
        this.workSeconds = workSeconds;
        this.restSeconds = restSeconds;
    }

    public String getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(String restSeconds) {
        this.restSeconds = restSeconds;
    }

    public String getWorkoutNames() {
        return workoutNames;
    }

    public void setWorkoutNames(String workoutNames) {
        this.workoutNames = workoutNames;
    }

    public String getWorkSeconds() {
        return workSeconds;
    }

    public void setWorkSeconds(String workSeconds) {
        this.workSeconds = workSeconds;
    }

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
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

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getWarmup() {
        return warmup;
    }

    public void setWarmup(int warmup) {
        this.warmup = warmup;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
