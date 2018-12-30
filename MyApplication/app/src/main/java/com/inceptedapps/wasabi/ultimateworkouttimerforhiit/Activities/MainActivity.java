package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

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
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.IabHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.IabResult;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.Inventory;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.iab.util.Purchase;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.DateHelper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private boolean isPremium = false;

    private Calendar c;
    private SharedPreferences sharedPref;

    IabHelper mHelper;
    static final String PREMIUM_SKU = "com.inceptedapps.ultrafit.pro";
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.shared_pref_open_key), Context.MODE_PRIVATE);

        // In-app billing. Checks if the user purchased premium package
        setupIab();

        prepareUI();

        prepareDatabase();

        initializeListeners();

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
                editor.apply();

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


    private void prepareUI() {
        // Set correct icon depends on the theme color
        setIcons();
        // Set main header text
        setHeaderText();
    }


    private void setIcons() {
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.parseInt(defaultPref.getString(
                getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));
        if (theme == 3 || theme == 5) {
            mSettingsButton.setImageResource(R.drawable.ic_settings);
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
        boolean isFirstLaunch = sharedPref.getBoolean(firstLaunchPrefKey, true);
        if (!isFirstLaunch) {
            RealmHelper.insertEmptyRows();
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(firstLaunchPrefKey, false);
            editor.apply();
            isFirstLaunch = false;
        }
    }


    private void initializeListeners() {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Recreating activity for theme change
        if (resultCode == RESULT_OK) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
