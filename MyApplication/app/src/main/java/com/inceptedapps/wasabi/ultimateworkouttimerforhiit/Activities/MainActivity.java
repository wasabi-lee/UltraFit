package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitTimerListActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.IsPremiumSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.ProgressActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.TimerLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.WorkoutLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.IabHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.IabResult;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.Inventory;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.Purchase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mHiitButton, mPresetLoadButton, mTrackProgressButton;
    private ImageView mSettingsButton;
    private TextView mGreetingText;
    private boolean isAnotherDay, isTheVeryFirstLaunch;
    private boolean isPremium = false;

    public static final String sharedPrefOpenKey = "OPEN";
    public static final String sharedPrefFirstKey = "VERY_FIRST_LAUNCH";
    public static final String sharedPrefSongsKey = "SAVED_SONGS";
    public static final String sharedPrefShuffleKey = "SHUFFLE_KEY";
    public static final String sharedPrefPremiumKey = "PREMIUM_KEY";
    public static final String sharedPrefCompletedWorkoutsNumKey = "COMPLETED_NUM_KEY";
    public static final String sharedPrefDontShowNumKey = "DONT_SHOW_KEY";

    private Calendar c;
    private SharedPreferences sharedPref;
    private AdView mAdView;

    IabHelper mHelper;
    static final String PREMIUM_SKU = "com.inceptedapps.ultrafit.pro";
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    private BroadcastReceiver myPromoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = MainActivity.this.getSharedPreferences(sharedPrefOpenKey, Context.MODE_PRIVATE);

        String base64EncodedPublicKey = getResources().getString(R.string.app_license_key);
        mAdView = (AdView) findViewById(R.id.adView);
        myPromoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        };

        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (mHelper == null) {
                    Log.d(getClass().getSimpleName(), "Helper is null");
                    return;
                }

                if (result.isFailure()) {
                    Log.d(getClass().getSimpleName(), "Inventory Failure: " + result.toString());
                    sharedPref.getBoolean(sharedPrefPremiumKey, false);
                    return;
                }

                Log.d(getClass().getSimpleName(), "Query successful");

                Purchase premiumPurchase = inv.getPurchase(PREMIUM_SKU);
                isPremium = (premiumPurchase != null);
                IsPremiumSingleton.getInstance().setPremium(isPremium);
                Log.d(getClass().getSimpleName(), "This user is " + (isPremium ? "premium" : "basic") + " user");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(sharedPrefPremiumKey, isPremium);
                editor.commit();
                Log.d(getClass().getSimpleName(),sharedPref.getBoolean(sharedPrefPremiumKey, false) + "");

                if (!isPremium){
                    activateAds();
                } else {
                    if (mAdView != null) {
                        mAdView.setVisibility(View.GONE);
                    }
                }
            }
        };

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (mHelper == null) {
                    return;
                }
                if (!result.isSuccess()){
                    Log.d(getClass().getSimpleName(), "In-app billing setup failed: "+result);
                    return;
                }

                Log.d(getClass().getSimpleName(), "In-app billing is set up OK");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        prepareDatabase();
        initializeData();

    }

    private void activateAds(){
        if (isPremium){
            mAdView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void retrieveSelectedSongs() {
        String songIds = sharedPref.getString(sharedPrefSongsKey, "-1");
        Log.d("SONG_", "retrieveSelectedSongs: MainActivity: "+songIds);
        if (!songIds.equals("-1")) {
            SongSingleton.getInstance().setSelectedSongIds(songIds.split("~"));
            SongSingleton.getInstance().prepSavedSongs(this);
        } else {
            SongSingleton.getInstance().getSelectedSongs().clear();
            SongSingleton.getInstance().setSelectedSongIds(null);
        }
    }

    private void prepareDatabase() {
        isTheVeryFirstLaunch = sharedPref.getBoolean(sharedPrefFirstKey, true);
        Log.d("FIRST_LAUNCH", "Is first launch: " + isTheVeryFirstLaunch);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d("FIRST_LAUNCH", "is first launch: " + isTheVeryFirstLaunch);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<WorkoutLog> results = realm.where(WorkoutLog.class).findAll();

        if (!isTheVeryFirstLaunch && (results.size() != 0)) {
            //Find out if today is another day from the last launch date
            Date todaysTimeStamp = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.getDefault());
            String todaysDate = simpleDateFormat.format(todaysTimeStamp);

            WorkoutLog lastWorkoutLog = realm.where(WorkoutLog.class).findAll().last();

            if (lastWorkoutLog != null) {
                Date lastWorkoutDateTimeStamp = lastWorkoutLog.getmDate();
                String lastWorkoutDate = simpleDateFormat.format(lastWorkoutDateTimeStamp);
                simpleDateFormat = null;

                isAnotherDay = !(todaysDate.equals(lastWorkoutDate));
                Log.d("IS ANOTHER_DAY", isAnotherDay + "");

                if (isAnotherDay) {
                    //Insert empty rows to the database
                    long timeDifference = todaysTimeStamp.getTime() - lastWorkoutDateTimeStamp.getTime();
                    c = Calendar.getInstance();
                    c.setTime(lastWorkoutDateTimeStamp);
                    c.add(Calendar.DATE, 1);
                    c.add(Calendar.HOUR_OF_DAY, -c.get(Calendar.HOUR_OF_DAY));
                    c.add(Calendar.MINUTE, -c.get(Calendar.MINUTE));
                    c.add(Calendar.SECOND, -c.get(Calendar.SECOND));
                    c.add(Calendar.MILLISECOND, -c.get(Calendar.MILLISECOND));
                    long timeDiffBtwLastTimeAndMidnight = c.getTimeInMillis() - lastWorkoutDateTimeStamp.getTime();
                    long timeUntilNextMidnight = 1000 * 60 * 60 * 24 + timeDiffBtwLastTimeAndMidnight;

                    if (timeUntilNextMidnight < timeDifference) {
                        realm.beginTransaction();
                        long numberOfEmptyRows = (todaysTimeStamp.getTime() - c.getTimeInMillis());
                        c.setTime(new Date());
                        c.add(Calendar.DATE, -(int) (numberOfEmptyRows / (1000 * 60 * 60 * 24)) - 1);
                        while (numberOfEmptyRows > 1000 * 60 * 60 * 24) {
                            c.add(Calendar.DATE, 1);
                            WorkoutLog newWorkoutLog = realm.createObject(WorkoutLog.class);
                            RealmList<TimerLog> newLogList = new RealmList<>();
                            newLogList.add(new TimerLog(0, "0", "0", 1, 0, 0, "NO WORKOUT FOUND"));
                            newWorkoutLog.setmDate(c.getTime());
                            newWorkoutLog.setTimerLogs(newLogList);
                            numberOfEmptyRows -= 1000 * 60 * 60 * 24;
                        }
                        realm.commitTransaction();
                    }
                }
                printUpdatedDatabase(realm);
            }
        } else {
            results = null;
            editor.putBoolean(sharedPrefFirstKey, false);
            editor.apply();
            isTheVeryFirstLaunch = sharedPref.getBoolean(sharedPrefFirstKey, true);
            Log.d("FIRST_LAUNCH", "Is first launch: " + isTheVeryFirstLaunch);
        }
    }

    private void printUpdatedDatabase(Realm realm) {
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
        updatedLogs = null;
        simpleDateformat = null;
        realm.close();

    }

    private void initializeData() {
        mHiitButton = (Button) findViewById(R.id.main_hitt_timer_button);
        mPresetLoadButton = (Button) findViewById(R.id.main_load_preset_button);
        mTrackProgressButton = (Button) findViewById(R.id.main_track_progress_button);
        mSettingsButton = (ImageView) findViewById(R.id.main_settings_icon);

        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));
        if (theme == 3 || theme == 5){
            mSettingsButton.setImageResource(R.drawable.ic_settings);
        }
        mSettingsButton.setOnClickListener(this);

        mHiitButton.setOnClickListener(this);
        mPresetLoadButton.setOnClickListener(this);
        mTrackProgressButton.setOnClickListener(this);

        mGreetingText = (TextView) findViewById(R.id.main_greeting_text);

        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        if (time <= 10 & time >= 6) {
            mGreetingText.setText(getResources().getString(R.string.greeting_morning));
        } else if (time <= 13) {
            mGreetingText.setText(getResources().getString(R.string.greeting_before_noon));
        } else if (time <= 18) {
            mGreetingText.setText(getResources().getString(R.string.greeting_afternoon));
        } else if (time <= 20) {
            mGreetingText.setText(getResources().getString(R.string.greeting_evening));
        } else if (time <= 24) {
            mGreetingText.setText(getResources().getString(R.string.greeting_midnight));
        } else if (time > 0 && time < 6) {
            mGreetingText.setText(getResources().getString(R.string.greeting_dawn));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_hitt_timer_button:
                Intent timerIntent = new Intent(MainActivity.this, HiitSettingActivity.class);
                startActivity(timerIntent);
                break;
            case R.id.main_load_preset_button:
                Intent presetListIntent = new Intent(MainActivity.this, HiitTimerListActivity.class);
                startActivity(presetListIntent);
                break;
            case R.id.main_track_progress_button:
                Intent calendarIntent = new Intent(MainActivity.this, ProgressActivity.class);
                startActivity(calendarIntent);
                break;
            case R.id.main_settings_icon:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 0);
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        retrieveSelectedSongs();
        if (sharedPref.getBoolean(sharedPrefPremiumKey, false) && mAdView != null){
            mAdView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
