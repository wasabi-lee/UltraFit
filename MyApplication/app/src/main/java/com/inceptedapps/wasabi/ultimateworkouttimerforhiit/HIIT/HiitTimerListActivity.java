package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT;

import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities.HiitSettingActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities.MainActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.IsPremiumSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.ProgressTracker.WorkoutLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.io.FileNotFoundException;
import java.util.Calendar;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;

public class HiitTimerListActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_timer_list);

        Toolbar hiitTimerListToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerListToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        hiitTimerListToolbar.setTitle("HIIT Timer List");
        setSupportActionBar(hiitTimerListToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();
        RealmResults<HiitTimerSet> timerSets = realm.where(HiitTimerSet.class).findAll();
        Log.d("SIZE_CHECK", "PRESETS: " + timerSets.size());
        TextView noPresetTV = (TextView) findViewById(R.id.hiit_timer_no_preset_text_view);
        if (timerSets.size() == 0) {
            noPresetTV.setVisibility(View.VISIBLE);
        } else {
            noPresetTV.setVisibility(View.GONE);
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.hiit_timer_list_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        HiitSingleton.getInstance().getTimers().clear();
        HiitSingleton.getInstance().getTimers().addAll(timerSets);

        HiitPresetAdapter mAdapter = new HiitPresetAdapter(realm, this, timerSets);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
