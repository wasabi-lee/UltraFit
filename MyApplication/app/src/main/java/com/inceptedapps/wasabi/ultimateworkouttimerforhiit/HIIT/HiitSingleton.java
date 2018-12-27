package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT;

import java.util.ArrayList;

/**
 * Created by Wasabi on 3/25/2016.
 */
public class HiitSingleton {

    private static HiitSingleton instance;
    private static ArrayList<HiitTimerSet> timers;

    private HiitSingleton() {
        timers = new ArrayList<>();
    }

    public static HiitSingleton getInstance() {
        if (instance == null) {
            instance = new HiitSingleton();
        }
        return instance;
    }

    public ArrayList<HiitTimerSet> getTimers() {
        return timers;
    }

    public int getTimerListSize() {
        return timers.size();
    }

    public void setTimers(ArrayList<HiitTimerSet> timers) {
        HiitSingleton.timers = timers;
    }

    public void addTimer(HiitTimerSet newTimer){
        timers.add(newTimer);
    }

    public void clearTimerList(){
        timers.clear();
    }
}
