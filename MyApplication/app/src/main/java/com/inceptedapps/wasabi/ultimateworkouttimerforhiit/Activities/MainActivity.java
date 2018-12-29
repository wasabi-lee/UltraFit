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
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.db.RealmHelper;
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
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.main_hitt_timer_button)
    Button mHiitButton;

    @BindView(R.id.main_load_preset_button)
    Button mPresetLoadButton;

    @BindView(R.id.main_track_progress_button)
    Button mTrackProgressButton;

    @BindView(R.id.main_settings_icon)
    ImageView mSettingsButton;

    @BindView(R.id.main_greeting_text)
    TextView mGreetingText;

    @BindView(R.id.adView)
    AdView mAdView;

    private boolean isAnotherDay, isFirstLaunch;
    private boolean isPremium = false;

    private Calendar c;
    private SharedPreferences sharedPref;

    IabHelper mHelper;
    static final String PREMIUM_SKU = "com.inceptedapps.ultrafit.pro";
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.shared_pref_open_key), Context.MODE_PRIVATE);

        // in-app billing
        setupIab();

        // Setting main header text
        setHeaderText();

        prepareDatabase();
        initializeData();

    }

    private void setupIab() {
        String base64EncodedPublicKey = getResources().getString(R.string.app_license_key);

        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (mHelper == null) {
                    Log.d(getClass().getSimpleName(), "Helper is null");
                    return;
                }

                if (result.isFailure()) {
                    Log.d(getClass().getSimpleName(), "Inventory Failure: " + result.toString());
                    sharedPref.getBoolean(getString(R.string.shared_pref_premium_key), false);
                    return;
                }

                Log.d(getClass().getSimpleName(), "Query successful");

                Purchase premiumPurchase = inv.getPurchase(PREMIUM_SKU);
                isPremium = (premiumPurchase != null);
                IsPremiumSingleton.getInstance().setPremium(isPremium);
                Log.d(getClass().getSimpleName(), "This user is " + (isPremium ? "premium" : "basic") + " user");

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.shared_pref_premium_key), isPremium);
                editor.commit();
                Log.d(getClass().getSimpleName(), sharedPref.getBoolean(getString(R.string.shared_pref_premium_key), false) + "");

                if (!isPremium) {
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
                if (!result.isSuccess()) {
                    Log.d(getClass().getSimpleName(), "In-app billing setup failed: " + result);
                    return;
                }

                Log.d(getClass().getSimpleName(), "In-app billing is set up OK");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void activateAds() {
        if (isPremium) {
            mAdView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }


    private void setHeaderText() {
        String headerText = DateHelper.getCurrentTimeText(getApplicationContext());
        mGreetingText.setText(headerText);
    }


    private void retrieveSelectedSongs() {
        String songIds = sharedPref.getString(getString(R.string.shared_pref_songs_key), "-1");
        SongSingleton.getInstance().setSelectedSongs(getApplicationContext(), songIds);
    }


    private void prepareDatabase() {
        String firstLaunchPrefKey = getString(R.string.shared_pref_first_launch_key);
        isFirstLaunch = sharedPref.getBoolean(firstLaunchPrefKey, true);
        SharedPreferences.Editor editor = sharedPref.edit();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<WorkoutLog> results = realm.where(WorkoutLog.class).findAll();

        if (!isFirstLaunch && (results.size() != 0)) {

            WorkoutLog lastWorkoutLog = results.last();

            //Find out if today is another day from the last launch date
            if (DateHelper.isAnotherDayFromLastWorkout(lastWorkoutLog)) {

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

            RealmHelper.printUpdatedDatabase(realm);

        } else {
            editor.putBoolean(firstLaunchPrefKey, false);
            editor.apply();
            isFirstLaunch = false;
        }
        realm.close();
    }


    private void initializeData() {
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));
        if (theme == 3 || theme == 5) {
            mSettingsButton.setImageResource(R.drawable.ic_settings);
        }

        mSettingsButton.setOnClickListener(this);
        mHiitButton.setOnClickListener(this);
        mPresetLoadButton.setOnClickListener(this);
        mTrackProgressButton.setOnClickListener(this);

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
        if (sharedPref.getBoolean(getString(R.string.shared_pref_premium_key), false) && mAdView != null) {
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
        if (resultCode == RESULT_OK) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
