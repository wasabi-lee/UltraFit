package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerSet;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Wasabi on 3/31/2016.
 */
public class WorkoutLog extends RealmObject {
    private Date mDate;
    private RealmList<TimerLog> timerLogs;

    public WorkoutLog() {
    }

    public WorkoutLog(Date mDate, RealmList<TimerLog> timerLogs) {
        this.mDate = mDate;
        this.timerLogs = timerLogs;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public RealmList<TimerLog> getTimerLogs() {
        return timerLogs;
    }

    public void setTimerLogs(RealmList<TimerLog> timerLogs) {
        this.timerLogs.clear();
        this.timerLogs.addAll(timerLogs);
    }
}
