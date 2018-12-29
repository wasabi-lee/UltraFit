package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.db;

import android.util.Log;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.TimerLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmHelper {

    public static void printUpdatedDatabase(Realm realm) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM dd, yyyy, EEE", Locale.getDefault());
        RealmResults<WorkoutLog> updatedLogs = realm.where(WorkoutLog.class).findAll();
        Log.d("CURRENT_DATABASE", updatedLogs.size() + "");
        for (int i = 0; i < updatedLogs.size(); i++) {
            Log.d("CURRENT_DATABASE", "DATE: " + simpleDateformat.format(updatedLogs.get(i).getmDate()));
            RealmList<TimerLog> updatedTimerLogs = updatedLogs.get(i).getTimerLogs();
            for (int j = 0; j < updatedTimerLogs.size(); j++) {
                Log.d("CURRENT_DATABASE", ", WARMUP: "
                        + updatedTimerLogs.get(j).getWarmup() + ", WORK: "
                        + updatedTimerLogs.get(j).getTotalWorkOrRestSeconds(updatedTimerLogs.get(j).getWorkSecs()) + ", RESTS: "
                        + updatedTimerLogs.get(j).getTotalWorkOrRestSeconds(updatedTimerLogs.get(j).getRestSecs()) + ", REPS: "
                        + updatedTimerLogs.get(j).getReps() + ", COOL DOWN: "
                        + updatedTimerLogs.get(j).getCooldown() + ", TOTAL: "
                        + updatedTimerLogs.get(j).getTotal() + ", WORKOUT NAMES: "
                        + updatedTimerLogs.get(j).getWorkoutNames());
            }

        }
    }

}
