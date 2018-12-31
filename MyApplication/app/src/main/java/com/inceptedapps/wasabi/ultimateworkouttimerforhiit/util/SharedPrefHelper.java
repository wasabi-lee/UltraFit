package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

public class SharedPrefHelper {

    private final static String PREF_FILE = "OPEN";

    public static boolean isShuffleEnabled(Context context) {
        return getSharedPreferenceBoolean(context,
                context.getString(R.string.shared_pref_shuffle_key),
                false);
    }

    public static void setShuffle(Context context, boolean enable) {
        setSharedPreferenceBoolean(context,
                context.getString(R.string.shared_pref_shuffle_key),
                enable);
    }

    public static boolean isPremium(Context context) {
        return getSharedPreferenceBoolean(context,
                context.getString(R.string.shared_pref_premium_key),
                false);
    }

    public static boolean isFirstLaunch(Context context) {
        return getSharedPreferenceBoolean(context,
                context.getString(R.string.shared_pref_first_launch_key),
                false);
    }

    public static String getSavedSongIds(Context context) {
        return getSharedPreferenceString(context,
                context.getString(R.string.shared_pref_songs_key),
                "-1");
    }

    public static boolean shouldDisableRateDialog(Context context) {
        return getSharedPreferenceBoolean(context,
                context.getString(R.string.shared_pref_dont_show_num_key),
                false);
    }

    public static int getCompletedNumOfWorkout(Context context) {
        return getSharedPreferenceInt(context,
                context.getString(R.string.shared_pref_completed_workouts_num_key),
                1);
    }

    public static int getBeepSoundId(Context context) {
        return getDefaultSharedPreferenceInt(context,
                context.getString(R.string.SHARED_PREF_BEEP_SOUND_SELECTION_KEY),
                1);
    }

    public static int getTickSoundId(Context context) {
        return getDefaultSharedPreferenceInt(context,
                context.getString(R.string.SHARED_PREF_TICK_SOUND_SELECTION_KEY),
                1);
    }

    public static boolean shouldMusicOverlapCue(Context context) {
        return getDefaultSharedPreferenceBoolean(context,
                context.getString(R.string.SHARED_PREF_CUE_OVERLAP_KEY),
                false);
    }

    public static boolean isVibrationEnabled(Context context) {
        return getDefaultSharedPreferenceBoolean(context,
                context.getString(R.string.SHARED_PREF_VIBRATION_KEY),
                false);
    }

    public static void getChartViewDays() {

    }

    public static String getTickCounts(Context context) {
        return getDefaultSharedPreferenceString(context,
                context.getString(R.string.SHARED_PREF_TRANSITION_TICKS_KEY),
                "3");
    }

    public static String getThemeId(Context context) {
        return getDefaultSharedPreferenceString(context,
                context.getString(R.string.SHARED_PREF_COLOR_THEME_KEY),
                "1");
    }


    private static void setDefaultSharedPreferenceString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void setDefaultSharedPreferenceInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static void setDefaultSharedPreferenceBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static String getDefaultSharedPreferenceString(Context context, String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return settings.getString(key, defValue);
    }

    private static int getDefaultSharedPreferenceInt(Context context, String key, int defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return settings.getInt(key, defValue);
    }

    private static boolean getDefaultSharedPreferenceBoolean(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defValue);
    }


    private static void setSharedPreferenceString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void setSharedPreferenceInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static void setSharedPreferenceBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static String getSharedPreferenceString(Context context, String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return settings.getString(key, defValue);
    }

    private static int getSharedPreferenceInt(Context context, String key, int defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return settings.getInt(key, defValue);
    }

    private static boolean getSharedPreferenceBoolean(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defValue);
    }

}
