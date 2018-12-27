package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.util.Log;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

/**
 * Created by Wasabi on 5/4/2016.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener{

    OnThemeChangedListener mCallback;
    private static final int TICK_REQUEST_CODE = 45655;
    private static final int BEEP_REQUEST_CODE = 75535;
    private int tickVal = 1;
    private int beepVal = 1;

    public interface OnThemeChangedListener{
        void onThemeChanged(int theme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnThemeChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    public SettingsFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        String tickKey = getResources().getString(R.string.SHARED_PREF_TICK_SOUND_SELECTION_KEY);
        String beepKey = getResources().getString(R.string.SHARED_PREF_BEEP_SOUND_SELECTION_KEY);
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tickVal = defaultPref.getInt(tickKey, 1);
        beepVal = defaultPref.getInt(beepKey, 1);

        Preference tickPref = findPreference(tickKey);
        Preference beepPref = findPreference(beepKey);
        tickPref.setOnPreferenceClickListener(this);
        beepPref.setOnPreferenceClickListener(this);
        tickPref.setSummary(tickNameSwitcher(tickVal));
        beepPref.setSummary(beepNameSwitcher(beepVal));

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getResources().getString(R.string.SHARED_PREF_TICK_SOUND_SELECTION_KEY))) {
            Intent intent = new Intent(getActivity(), TickSettingActivity.class);
            intent.putExtra(TickSettingActivity.IS_TICK_PREFERENCE_KEY, true);
            intent.putExtra(TickSettingActivity.ORIGINAL_VALUE, tickVal);
            startActivityForResult(intent, TICK_REQUEST_CODE);
        } else if (key.equals(getResources().getString(R.string.SHARED_PREF_BEEP_SOUND_SELECTION_KEY))) {
            Intent intent = new Intent(getActivity(), TickSettingActivity.class);
            intent.putExtra(TickSettingActivity.IS_TICK_PREFERENCE_KEY, false);
            intent.putExtra(TickSettingActivity.ORIGINAL_VALUE, beepVal);
            startActivityForResult(intent, BEEP_REQUEST_CODE);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TICK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String key = getResources().getString(R.string.SHARED_PREF_TICK_SOUND_SELECTION_KEY);
                int value = data.getIntExtra(TickSettingActivity.SELECTED_VALUE, 1);
                tickVal = value;
                String name = tickNameSwitcher(value);
                changePreference(key, value, name);
            }
        } else if (requestCode == BEEP_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String key = getResources().getString(R.string.SHARED_PREF_BEEP_SOUND_SELECTION_KEY);
                int value = data.getIntExtra(TickSettingActivity.SELECTED_VALUE, 1);
                beepVal = value;
                String name = beepNameSwitcher(value);
                changePreference(key, value, name);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changePreference(String key, int value, String name) {
        Preference pref = findPreference(key);
        SharedPreferences.Editor editor = pref.getEditor();
        editor.putInt(key, value);
        editor.commit();
        pref.setSummary(name);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(getClass().getSimpleName(), "RECEIVEING");
        if (key.equals(getResources().getString(R.string.SHARED_PREF_VIBRATION_KEY))){
            Preference pref = findPreference(key);
            if (sharedPreferences.getBoolean(key, false)) {
                pref.setSummary("Enabled");
            }else{
                pref.setSummary("Disabled");
            }
        }
        if (key.equals(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY))){
            mCallback.onThemeChanged(Integer.parseInt(sharedPreferences.getString(key, "1")));
        }
    }


    public String beepNameSwitcher(int beepTheme) {
        switch (beepTheme) {
            case 1:
                return "Basic Beep";
            case 2:
                return "Whistle";
            case 3:
                return "Machine Gun";
            case 4:
                return "Rifle Shot";
            case 5:
                return "Chime Bell";
            case 6:
                return "Click";
            case 7:
                return "Glass Click";
            case 8:
                return "Home Run";
            case 9:
                return "Xylophone Beep";
            default:
                return "Basic Beep";
        }
    }

    public String tickNameSwitcher(int tickTheme) {
        switch (tickTheme) {
            case 1:
                return "Basic Tick";
            case 2:
                return "Camera Click";
            case 3:
                return "Hint";
            case 4:
                return "Pistol";
            case 5:
                return "Shotgun Pump";
            case 6:
                return "Temple Block";
            case 7:
                return "Xylophone Tick";
            default:
                return "Basic Tick";
        }
    }
}