package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom;

import java.util.concurrent.TimeUnit;

/**
 * Created by Wasabi on 8/6/2016.
 */
public class TimerUtils {

    public TimerUtils() {}

    /***
     *
     * Changes a raw second data (67 seconds) into a four digit string form (01:07.)
     *
     * @param rawTotalSec The raw second to trim.
     * @return String[] A String that contains total minutes and total seconds.
     */
    public static String convertRawSecIntoString(int rawTotalSec) {
        if (rawTotalSec <= 0) {
            return "00:00";
        }
        int min = rawTotalSec / 60;
        int sec = rawTotalSec % 60;
        int hour = min / 60;
        min = min % 60;

        if (hour != 0) {
            return makeIntoTwoDigits(hour) + ":" +
                    makeIntoTwoDigits(min) + ":" +
                    makeIntoTwoDigits(sec);
        }
        return makeIntoTwoDigits(min) + ":" + makeIntoTwoDigits(sec);
    }

    /**
     *
     *Changes a raw second data (67 seconds) into a four digit string array form (01:07.)
     *In this case, the minute and the second become elements of the array.
     * @param rawTotalSec The raw second to trim.
     * @return The trimmed string array of the time. The first element is the minute and the second is the second.
     */
    public static String[] convertRawSecIntoStringArr(int rawTotalSec) {
        if (rawTotalSec <= 0) {
            return new String[] {"00", "00"};
        }
        int min = rawTotalSec / 60;
        int sec = rawTotalSec % 60;
        return new String[] {makeIntoTwoDigits(min), makeIntoTwoDigits(sec)};
    }

    /**
     * Changes the time that is only 1 digit (a number that is between -1 to 10) into a two digit form.
     * For example, this method returns 1 into 01.
     * @param time The raw second to trim.
     * @return The trimmed String.
     */
    public static String makeIntoTwoDigits(int time) {
        if (time < 10) {
            return "0"+time;
        }
        return time+"";
    }


    /**
     * Changes the String time data (01:07) into the raw second form (67).
     * @param rawTime The String time data
     * @return The calculated raw second integer.
     */
    public static int stringTimeToSeconds(String rawTime) {
        if (rawTime.isEmpty()) {
            return 0;
        }
        String[] rawMinSec = rawTime.split(":");
        if (rawMinSec.length == 2) {
            int rawMin = Integer.parseInt(rawMinSec[0]);
            int rawSec = Integer.parseInt(rawMinSec[1]);
            int minIntoSec = (int) TimeUnit.MINUTES.toSeconds(rawMin);
            return minIntoSec + rawSec;
        } else if(rawMinSec.length == 1){
            return Integer.parseInt(rawMinSec[0]);
        }
        return -1;
    }
}
