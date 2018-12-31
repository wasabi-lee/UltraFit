package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitTimerSet;

public class RoundHelper {

    public static final String DEFAULT_TIMER_NAME = "HIIT Timer";


    public static HiitTimerSet getFinalizedHiitTimerSet(
            int warmupTime, int defaultWorkTime, int defaultRestTime,
            int defaultReps, int cooldownTime,
            String customWorkTime, String customRestTime, String customWorkoutNames,
            int customReps, boolean isAdvancedSettingSet) {

        return getFinalizedHiitTimerSet(warmupTime, defaultWorkTime, defaultRestTime,
                defaultReps, cooldownTime, customWorkTime, customRestTime, customWorkoutNames,
                customReps, isAdvancedSettingSet, DEFAULT_TIMER_NAME);
    }


    public static HiitTimerSet getFinalizedHiitTimerSet(
            int warmupTime, int defaultWorkTime, int defaultRestTime,
            int defaultReps, int cooldownTime,
            String customWorkTime, String customRestTime, String customWorkoutNames,
            int customReps, boolean isAdvancedSettingSet, String timerName) {


        // return an empty set if the input is invalid
        if (defaultReps == 0) {
            return new HiitTimerSet(0, 0, 0, 0, timerName,
                    null, null, null);
        }


        StringBuilder workoutNames = new StringBuilder();
        StringBuilder workSeconds = new StringBuilder();
        StringBuilder restSeconds = new StringBuilder();

        if (isAdvancedSettingSet) {

            workoutNames.append(customWorkoutNames);
            workSeconds.append(customWorkTime);
            restSeconds.append(customRestTime);

            if (defaultReps != customReps) {
                adjustWorkoutRounds(defaultReps, customReps, defaultWorkTime, defaultRestTime,
                        workoutNames, workSeconds, restSeconds);
            }

        } else {
            for (int i = 0; i < defaultReps; i++) {
                String delimiter = (i == defaultReps - 1 ? "" : "=");
                workoutNames.append("sprint").append(delimiter);
                workSeconds.append(defaultWorkTime).append(delimiter);
                restSeconds.append(defaultRestTime).append(delimiter);
            }
        }

        int total = calculateTotal(warmupTime, workSeconds.toString(), restSeconds.toString(), cooldownTime);

        return new HiitTimerSet(warmupTime, defaultReps, cooldownTime, total, timerName,
                workoutNames.toString(), workSeconds.toString(), restSeconds.toString());
    }


    /**
     * Adjusts the workout rounds based on the default rep count.
     * This method will be called when the user customized the workout in the advanced setting and
     * adjust the rep count in the main setting menu again.
     * <p>
     * This method is to address that difference between customizedReps and default(final)Reps.
     *
     * @param defaultReps     The rep count that will work as a standard.
     * @param customReps      The rep count that will be compared to the defaultReps.
     * @param defaultWorkTime The workout time that will be used when appending more rounds
     * @param defaultRestTime The rest time that will be used when appending more rounds
     * @param workoutNames    The stringbuilder that will hold the finalized workout names
     * @param workSecs        The stringbuilder that will hold the finalized workout seconds
     * @param restSecs        The stringbuilder that will hold the finalized rest seconds
     */
    private static void adjustWorkoutRounds(int defaultReps, int customReps, int defaultWorkTime, int defaultRestTime,
                                            StringBuilder workoutNames, StringBuilder workSecs, StringBuilder restSecs) {

        // Append more rounds
        if (defaultReps > customReps) {
            for (int i = 0; i < defaultReps - customReps; i++) {
                workoutNames.append("=Sprint");
                workSecs.append("=").append(defaultWorkTime);
                restSecs.append("=").append(defaultRestTime);
            }
        }

        // Delete rounds
        if (defaultReps < customReps) {
            for (int i = 0; i < customReps - defaultReps; i++) {
                workoutNames.delete(workoutNames.lastIndexOf("="), workoutNames.length());
                workSecs.delete(workSecs.lastIndexOf("="), workSecs.length());
                restSecs.delete(restSecs.lastIndexOf("="), restSecs.length());
            }
        }
    }


    public static int calculateTotal(int warmupTime, String workTimes, String restTimes, int cooldownTime) {
        String[] workTimesArr = workTimes.split("=");
        String[] restTimesArr = restTimes.split("=");

        int total = 0;

        try {
            for (int i = 0; i < workTimesArr.length; i++) {
                total += Integer.parseInt(workTimesArr[i]);
                total += Integer.parseInt(restTimesArr[i]);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }

        return total + warmupTime + cooldownTime;
    }

}
