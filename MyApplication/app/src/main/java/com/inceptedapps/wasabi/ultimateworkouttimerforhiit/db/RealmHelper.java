package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.db;

import android.util.Log;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.TimerLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.WorkoutLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmHelper {

    /**
     * Inserting empty rows on dates that have no workout history.
     * Doing this makes feeding data to LineChart(in ProgressActivity.class) more handy
     */
    public static void insertEmptyRows() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<WorkoutLog> results = realm.where(WorkoutLog.class).findAll();

        if (results.size() == 0) {
            realm.close();
            return;
        }

        WorkoutLog lastWorkoutLog = results.last();

        //Find out if today is another day from the last launch date
        if (DateHelper.isAnotherDayFromLastWorkout(lastWorkoutLog)) {

            // Insert empty rows to the database from last workout day till today
            Calendar c = Calendar.getInstance();
            c.setTime(lastWorkoutLog.getmDate());
            c.add(Calendar.DATE, 1);


            realm.beginTransaction();
            while (DateHelper.isAnotherDayFromLastWorkout(c.getTime())) {
                WorkoutLog newWorkoutLog = new WorkoutLog();
                RealmList<TimerLog> newLogList = new RealmList<>();
                newLogList.add(new TimerLog(0, "0", "0", 1, 0, 0, "NO WORKOUT FOUND"));
                newWorkoutLog.setmDate(c.getTime());
                newWorkoutLog.setTimerLogs(newLogList);
                realm.insert(newWorkoutLog);
                c.add(Calendar.DATE, 1);
            }
            realm.commitTransaction();

        }
        RealmHelper.printUpdatedDatabase(realm);
        realm.close();
    }


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
