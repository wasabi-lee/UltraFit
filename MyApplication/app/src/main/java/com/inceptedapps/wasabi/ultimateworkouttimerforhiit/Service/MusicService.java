package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities.MainActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.Song;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.IsPremiumSingleton;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Wasabi on 4/29/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static final String FROM_MUSIC_SERVICE = "com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Service.FROM_MUSIC_SERVICE";
    public static final String UI_CHANGE_MESSAGE = "com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Service.UI_CHANGE_MESSAGE";
    public static final String MUSIC_FINISHED_MESSAGE = "com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Service.MUSIC_FINISHED_MESSAGE";

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosition, previousSongPosition;
    private final IBinder musicBind = new MusicBinder();
    private LocalBroadcastManager broadcaster;
    private Random random = new Random();
    private CountDownTimer timer;

    private boolean isShuffleOn = false, isPaused = false, isPremium = IsPremiumSingleton.getInstance().returnPremium();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getSimpleName(), "Service tracker : onBind");
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(getClass().getSimpleName(), "Service tracker : onUnbind");
        return super.onUnbind(intent);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(getClass().getSimpleName(), "Service tracker : onCreate");
        songPosition = 0;
        previousSongPosition = 0;
        player = new MediaPlayer();
        broadcaster = LocalBroadcastManager.getInstance(this);

        initMusicPlayer();
        initTimer(isPremium);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getSimpleName(), "Service tracker : onStartCommand");
        setList();
        setShuffleOn();
        setSong(songPosition);
        playSong();
        if (timer != null){
            timer.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void setShuffleOn() {
        isShuffleOn = getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE).getBoolean(MainActivity.sharedPrefShuffleKey, false);
        if (isShuffleOn) {
            if (songs.size() != 0) {
                songPosition = random.nextInt(songs.size());
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void initTimer(boolean isPremium) {
        if (!isPremium) {
            timer = new CountDownTimer(1000 * 60 * 10 , 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d(getClass().getSimpleName(), "Until music finish: " + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    sendMusicFinishBroadcast();
                    isPaused = true;
                    player.reset();
                    player.release();
                    player = null;
                    stopSelf();
                }
            };
        }
    }


    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList() {
        songs = SongSingleton.getInstance().getSelectedSongs();
        Log.d(getClass().getName(), "Service tracker : Songs size : " + songs.size() + "");
    }

    public void setSong(int songPosition) {
        this.songPosition = songPosition;
    }

    public boolean pauseSong() {
        if (player != null) {
            if (player.isPlaying()) {
                isPaused = true;
                player.pause();
                Log.d(getClass().getSimpleName(), "Paused the music");
                return isPaused;
            } else {
                isPaused = false;
                player.start();
                Log.d(getClass().getSimpleName(), "Resumed the music");
                return isPaused;
            }
        }
        return true;
    }

    public void pauseSongForBeep(long beepDuration) {
        if (player.isPlaying()) {
            pauseSong();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pauseSong();
                }
            }, beepDuration);
        }
    }

    public void skipSong(boolean isNext) {
        previousSongPosition = songPosition;
        if (isShuffleOn) {
            getNextRandomSongPosition();
        } else {
            if (isNext) {
                if (songPosition < songs.size() - 1) {
                    songPosition++;
                } else {
                    songPosition = 0;
                }
            } else {
                if (songPosition == 0) {
                    songPosition = songs.size() - 1;
                } else {
                    songPosition--;
                }
            }
        }
        setSong(songPosition);
        playSong();
        sendResult(songPosition);
    }

    public void playSong() {
        player.reset();
        Song playSong = songs.get(songPosition);
        long currentSongId = playSong.getmSongId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongId);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.d("MUSIC SERVICE", "Error setting data source ", e);
        }

        player.prepareAsync();
    }

    public boolean shuffleOn() {
        if (isShuffleOn) {
            isShuffleOn = false;
        } else {
            isShuffleOn = true;
        }
        return isShuffleOn;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
        Log.d(getClass().getSimpleName(), "Service tracker : onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        sendResult(songPosition);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        previousSongPosition = songPosition;
        if (isShuffleOn) {
            getNextRandomSongPosition();
        } else {
            if (songPosition < songs.size() - 1) {
                songPosition++;
            } else {
                songPosition = 0;
            }
        }
        setSong(songPosition);
        playSong();
        sendResult(songPosition);
    }

    private void getNextRandomSongPosition() {
        songPosition = random.nextInt(songs.size());
        while (songPosition == previousSongPosition) {
            songPosition = random.nextInt(songs.size());
        }
    }

    public void sendMusicFinishBroadcast(){
        Intent intent = new Intent(FROM_MUSIC_SERVICE);
        intent.putExtra(MUSIC_FINISHED_MESSAGE, true);
        broadcaster.sendBroadcast(intent);
    }

    public void sendResult(int songPosition) {
        Intent intent = new Intent(FROM_MUSIC_SERVICE);
        if (songPosition != -1) {
            intent.putExtra(UI_CHANGE_MESSAGE, songPosition);
        }
        broadcaster.sendBroadcast(intent);
    }

}