package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.DeviceMusicListAdapter;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.Song;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;

public class MusicSettingActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    DeviceMusicListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_setting_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.music_setting_toolbar);
        toolbar.setTitle(getResources().getString(R.string.music_setting_toolbar));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeRecyclerView();

    }

    public void initializeRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.music_setting_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DeviceMusicListAdapter.OnMusicCheckedListener listener = new DeviceMusicListAdapter.OnMusicCheckedListener() {
            @Override
            public void onMusicSelected(Song item, int position) {
                }
            };

        mAdapter = new DeviceMusicListAdapter(this, listener);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        Intent returningIntent = new Intent();
        setResult(RESULT_OK, returningIntent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        SongSingleton.getInstance().updateSelectedSongs();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
