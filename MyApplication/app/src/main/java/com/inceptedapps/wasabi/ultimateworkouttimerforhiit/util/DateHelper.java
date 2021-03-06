package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util;

import android.content.Context;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static CurrentTime getCurrentTimeFrame() {
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);

        if (time <= 10 && time >= 6) {
            return CurrentTime.MORNING;
        } else if (time <= 13) {
            return CurrentTime.BEFORE_NOON;
        } else if (time <= 18) {
            return CurrentTime.AFTERNOON;
        } else if (time <= 20) {
            return CurrentTime.EVENING;
        } else if (time <= 24) {
            return CurrentTime.MIDNIGHT;
        } else  {
            return CurrentTime.DAWN;
        }
    }

    public static String getCurrentTimeText(Context context) {
        CurrentTime currentTime = getCurrentTimeFrame();

        switch (currentTime) {
            case MORNING:
                return context.getResources().getString(R.string.greeting_morning);
            case BEFORE_NOON:
                return context.getResources().getString(R.string.greeting_before_noon);
            case AFTERNOON:
                return context.getResources().getString(R.string.greeting_afternoon);
            case EVENING:
                return context.getResources().getString(R.string.greeting_evening);
            case MIDNIGHT:
                return context.getResources().getString(R.string.greeting_midnight);
            case DAWN:
                return context.getResources().getString(R.string.greeting_dawn);
            default:
                return context.getResources().getString(R.string.greeting_before_noon);
        }
    }


    public static boolean isAnotherDayFromLastWorkout(WorkoutLog lastWorkoutLog) {
        Date todayDate = Calendar.getInstance().getTime();
        Date lastWorkoutTimestamp = lastWorkoutLog.getmDate();
        return !isSameDate(todayDate, lastWorkoutTimestamp);
    }

    public static boolean isAnotherDayFromLastWorkout(Date lastWorkoutDate) {
        Date todayDate = Calendar.getInstance().getTime();
        return !isSameDate(todayDate, lastWorkoutDate);
    }


    public static boolean isSameDate(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date1).equals(sdf.format(date2));
    }


}
