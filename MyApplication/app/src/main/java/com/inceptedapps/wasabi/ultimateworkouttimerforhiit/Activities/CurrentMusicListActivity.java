package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.CurrentMusicAdapter;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.Song;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;

public class CurrentMusicListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 43356;

    RecyclerView mCurrentMusicList;
    CurrentMusicAdapter mCurrentMusicAdapter;
    ArrayList<Song> mSongs;

    ProgressDialog progressDialog;
    MusicQueryAsync async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_music_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.current_music_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.current_music_list_toolbar));

        progressDialog = new ProgressDialog(this);
        async = new MusicQueryAsync();
        async.execute();
        progressDialog.setMessage("Loading songs from device");
        progressDialog.show();

        mSongs = new ArrayList<>();
        mCurrentMusicList = (RecyclerView)findViewById(R.id.current_music_recycler_view);
        mCurrentMusicList.setLayoutManager(new LinearLayoutManager(this));

        mCurrentMusicAdapter = new CurrentMusicAdapter(CurrentMusicListActivity.this, mSongs);
        mCurrentMusicList.setAdapter(mCurrentMusicAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.current_music_add_button);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CurrentMusicListActivity.this, MusicSettingActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mSongs.clear();
            mSongs.addAll(SongSingleton.getInstance().getSelectedSongs());
            for (int i = 0; i < mSongs.size(); i++) {
                Log.d("SONGS_", "CurrMusicActivity, onResume: " + mSongs.get(i).getmSongId() + "");
            }
            mCurrentMusicAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        SongSingleton.getInstance().updateSelectedSongs();
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_pref_open_key), Context.MODE_PRIVATE);

        if (SongSingleton.getInstance().getSelectedSongs().size() != 0) {
            String savedSongIds = "";
            for (int i = 0; i < SongSingleton.getInstance().getSelectedSongs().size(); i++) {
                if (i == SongSingleton.getInstance().getSelectedSongs().size() - 1) {
                    savedSongIds += SongSingleton.getInstance().getSelectedSongs().get(i).getmSongId();
                } else {
                    savedSongIds += SongSingleton.getInstance().getSelectedSongs().get(i).getmSongId() + "~";
                }
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.shared_pref_songs_key), savedSongIds);
            editor.commit();
//            Log.d("SONG_", "Shared Pref = " + sharedPref.getString(MainActivity.sharedPrefSongsKey, "DEFAULT"));
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.shared_pref_songs_key), "-1");
            editor.commit();
//            Log.d("SONG_", "Shared Pref = " + sharedPref.getString(MainActivity.sharedPrefSongsKey, "DEFAULT"));
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void retrieveSongList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            SongSingleton.getInstance().getSongList().clear();
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                int songNameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                do {
                    long thisSongId = musicCursor.getLong(songIdColumn);
                    String thisSongName = musicCursor.getString(songNameColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    long thisAlbumId = musicCursor.getShort(albumIdColumn);
                    boolean isAdded = false;
                    if (SongSingleton.getInstance().getSelectedSongIds() != null) {
                        for (int i = 0; i < SongSingleton.getInstance().getSelectedSongIds().length; i++) {
                            if (thisSongId == Long.parseLong(SongSingleton.getInstance().getSelectedSongIds()[i])) {
                                isAdded = true;
                            }
                        }
                    }
                    SongSingleton.getInstance().addSong(new Song(thisSongId, thisAlbumId, thisSongName, thisArtist, isAdded));
                }
                while (musicCursor.moveToNext());
            }
            if (musicCursor != null) {
                musicCursor.close();
            }
        }
    }

    public class MusicQueryAsync extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            retrieveSongList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            SongSingleton.getInstance().updateSelectedSongs();
            mSongs.clear();
            mSongs.addAll(SongSingleton.getInstance().getSelectedSongs());
            mCurrentMusicAdapter.notifyDataSetChanged();
            Log.d("SONG_", "onPostExecute: ");
        }
    }
}
