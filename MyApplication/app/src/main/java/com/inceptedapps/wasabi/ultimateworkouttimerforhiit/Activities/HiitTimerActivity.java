package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.TimerFragment;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.MusicDrawerListAdapter;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.Song;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.MyAnimation;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.MySoundPoolHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.RateDialogHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.TimerUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.TimerLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.progress.WorkoutLog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.service.KillNotificationsService;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.service.MusicService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HiitTimerActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, TimerFragment.TimerOnTickListener {

    //Declaring Views and the timer set object

    private static final String TAG = HiitTimerActivity.class.getSimpleName();
    private static final String NOTIFICATION_ID_KEY = "notif_id_key";
    private DrawerLayout mDrawerLayout;
    private boolean isMusicModeOn, isTimerActive = true, isTimerStopped;
    private ListView mDrawerList;
    private MusicDrawerListAdapter mAdapter;
    private ArrayList<Song> mSongList;
    private boolean isConfigChanged = false;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false, isMusicFinished = false;
    private BroadcastReceiver broadcastReceiver;
    private int songPosition;
    private long mBeepDuration = 1000;

    TextView mTimerMinsTextView, mTimerSecsTextView, mTotalTextView, mWorkoutTextView, mRepsTextView, mNoMusicText;
    float workoutTvSize;
    ProgressBar mProgressBar;
    HiitTimerSet hiitTimerSet;
    ImageView mAlbumArt, mSkipNextIcon, mSkipPrevIcon, mPlayPauseIcon, mShuffleIcon, mNoMusicBackground;
    TextView mSongName, mArtist;

    //Keys
    public static final String WORKOUT_NAMES_KEY = "workout_names_key";
    public static final String WORKOUT_SECONDS_KEY = "workout_seconds_key";
    public static final String RECOVERY_SECONDS_KEY = "recovery_seconds_key";
    public static final String WARMUP_SECONDS_KEY = "warmup_seconds_key";
    public static final String COOLDOWN_SECONDS_KEY = "cooldown_seconds_key";

    public static final String TOTAL_SECONDS_KEY = "total_seconds_key";
    public static final String TIME_BEFORE_CONFIG_CHANGE = "seconds_before_config_change_key";
    public static final String TOTAL_BEFORE_CONFIG_CHANGE = "total_before_config_change_key";
    public static final String ACTION_BEFORE_CONFIG_CHANGE = "action_before_config_change_key";
    public static final String ROUND_DURATION_BEFORE_CONFIG_CHANGE = "round_duration_before_config_change_key";
    public static final String CURRENT_REPS_BEFORE_CONFIG_CHANGE = "current_reps_before_config_change_key";
    public static final String IS_TIMER_PAUSED_BEFORE_CONFIG_CHANGE = "is_timer_paused_before_config_change_key";
    public static final String IS_TIMER_ACTIVE_BEFORE_CONFIG_CHANGE = "is_timer_active_reps_before_config_change_key";

    //Preferences
    private int tickCount, themeMode, mBeepResource, mTickResource;
    private boolean isOverlapAllowed, isVibrationAllowed;

    //Second variables.
    int warmup, reps, cooldown, total;
    String[] workoutNames;
    int[] workSeconds, restSeconds;
    private int totalSecsToGo = -1;
    private int currentSeconds;
    private int action;
    private int duration;
    private int currentReps;
    private String currentActionName;

    //Notification
    private NotificationManager notifManager;
    private NotificationCompat.Builder notifBuilder;
    private int notificationId = 262;

    //Timer components
    MyAnimation anim;
    Vibrator v;

    private SharedPreferences sharedPref;
    private MySoundPoolHelper mSoundPoolHelper;

    private Realm logRealm;
    private boolean isPremium;

    private TimerFragment timerFragment;
    private static final String FRAGMENT_TAG = "fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_timer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Log.d(TAG, "onCreate: ");
        sharedPref = getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE);
        isPremium = this.getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE).getBoolean(MainActivity.sharedPrefPremiumKey, false);
        Log.d(getClass().getSimpleName(), "Is Premium: " + isPremium);


        initializeAds();
        retrieveSharedPrefValues();
        instantiateRealm();
        initiateSoundPool();
        initiateMusicData();
        initiateTimerData();
        initFragment();
        configAnim();

        if (savedInstanceState != null) {

            isConfigChanged = true;

            currentSeconds = savedInstanceState.getInt(TIME_BEFORE_CONFIG_CHANGE);
            totalSecsToGo = savedInstanceState.getInt(TOTAL_BEFORE_CONFIG_CHANGE);
            action = savedInstanceState.getInt(ACTION_BEFORE_CONFIG_CHANGE);
            duration = savedInstanceState.getInt(ROUND_DURATION_BEFORE_CONFIG_CHANGE);
            currentReps = savedInstanceState.getInt(CURRENT_REPS_BEFORE_CONFIG_CHANGE);
            isTimerActive = savedInstanceState.getBoolean(IS_TIMER_ACTIVE_BEFORE_CONFIG_CHANGE);
            isTimerStopped = savedInstanceState.getBoolean(IS_TIMER_PAUSED_BEFORE_CONFIG_CHANGE);

            Log.d(TAG, "onCreate: Remaining time: " + currentSeconds +
            ", isTimerActive: "+isTimerActive +
            ", isTimerStopped: "+isTimerStopped +
            ", Duration: "+duration+
            ", reps: " + reps +
            ", action: " + action +
            ", total: " + totalSecsToGo);

            mProgressBar.setMax(duration * 100);
            mProgressBar.setProgress(currentSeconds);
            if (!isTimerStopped) animateProgressBar(currentSeconds);
            handleTimerUI(currentSeconds, action, reps, totalSecsToGo);
            configNotification(currentSeconds, action);
        } else {
            configNotification(warmup, TimerFragment.ACTION_WARMUP);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (mSongList.size() != 0) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                musicBound = true;
                Log.d(getClass().getSimpleName(), "Service tracker : Service is bound");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void instantiateRealm() {
        logRealm = Realm.getDefaultInstance();
    }

    private void initializeAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (mAdView != null) {
            if (!isPremium) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                Log.d(getClass().getSimpleName(), "Initializing Ads");
            } else {
                mAdView.setVisibility(View.GONE);
                Log.d(getClass().getSimpleName(), "No ads!");
            }
        }
    }

    private void retrieveSharedPrefValues() {
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(HiitTimerActivity.this);

        isOverlapAllowed = defaultPref.getBoolean(getResources().getString(R.string.SHARED_PREF_CUE_OVERLAP_KEY), false);
        isVibrationAllowed = defaultPref.getBoolean(getResources().getString(R.string.SHARED_PREF_VIBRATION_KEY), true);
        tickCount = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_TRANSITION_TICKS_KEY), "3"));
        isMusicModeOn = SongSingleton.getInstance().getSelectedSongs().size() != 0;
        themeMode = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));

        int beepTheme = 1;
        int tickTheme = 1;
        try {
            beepTheme = defaultPref.getInt(getResources().getString(R.string.SHARED_PREF_BEEP_SOUND_SELECTION_KEY), 1);
            tickTheme = defaultPref.getInt(getResources().getString(R.string.SHARED_PREF_TICK_SOUND_SELECTION_KEY), 1);
        } catch (Exception e ){
            e.printStackTrace();
        }
        mBeepResource = beepThemeSwitcher(beepTheme);
        mTickResource = tickThemeSwitcher(tickTheme);

        Log.d(getClass().getSimpleName(), "Sound theme: " + beepTheme);
        Log.d(getClass().getSimpleName(), "isOverlapAllowed: " + isOverlapAllowed);
        Log.d(getClass().getSimpleName(), "isVibrationAllowed: " + isVibrationAllowed);
        Log.d(getClass().getSimpleName(), "tickCount: " + tickCount);
        Log.d(getClass().getSimpleName(), "isMusicModeOn: " + isMusicModeOn);
    }

    private void initiateSoundPool() {
        mSoundPoolHelper = new MySoundPoolHelper(this);
        mSoundPoolHelper.initSoundPool();
    }

    private void initiateMusicData() {
        mNoMusicText = (TextView) findViewById(R.id.no_music_text);
        mNoMusicBackground = (ImageView) findViewById(R.id.no_music_background);
        if (isMusicModeOn) {
            initMusicViews();
            prepSongData(isConfigChanged);
        } else {
            mNoMusicText.setVisibility(View.VISIBLE);
            mNoMusicBackground.setVisibility(View.VISIBLE);
        }
    }

    private void initMusicViews() {
        if (mNoMusicText != null) mNoMusicText.setVisibility(View.GONE);
        if (mNoMusicBackground != null) mNoMusicBackground.setVisibility(View.GONE);

        ImageView musicDrawerButton = (ImageView) findViewById(R.id.music_drawer_button);
        if (musicDrawerButton != null) musicDrawerButton.setOnClickListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.music_drawer);
        mDrawerList = (ListView) findViewById(R.id.hiit_timer_song_list);
        if (themeMode == 3 || themeMode == 5) {
            if (mDrawerList != null) mDrawerList.setBackgroundColor(Color.BLACK);
        }

        mSongName = (TextView) findViewById(R.id.hiit_timer_song_name);
        mArtist = (TextView) findViewById(R.id.hiit_timer_artist);
        mAlbumArt = (ImageView) findViewById(R.id.hiit_timer_album_art);
        mPlayPauseIcon = (ImageView) findViewById(R.id.drawer_play_pause_icon);
        mSkipNextIcon = (ImageView) findViewById(R.id.drawer_skip_next_icon);
        mSkipPrevIcon = (ImageView) findViewById(R.id.drawer_skip_prev_icon);
        mShuffleIcon = (ImageView) findViewById(R.id.drawer_shuffle_icon);
        if (sharedPref.getBoolean(MainActivity.sharedPrefShuffleKey, false)) {
            mShuffleIcon.setImageResource(R.drawable.ic_action_playback_schuffle);
        } else {
            mShuffleIcon.setImageResource(R.drawable.ic_action_playback_repeat);
        }

        mPlayPauseIcon.setOnClickListener(this);
        mSkipNextIcon.setOnClickListener(this);
        mSkipPrevIcon.setOnClickListener(this);
        mShuffleIcon.setOnClickListener(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(MusicService.UI_CHANGE_MESSAGE)) {
                    //Change song album art
                    songPosition = intent.getIntExtra(MusicService.UI_CHANGE_MESSAGE, -1);
                    prepareSongUI(songPosition);
                    Log.d(getClass().getSimpleName(), "Service tracker: On Receive: Song Position: " + songPosition);

                } else if (intent.hasExtra(MusicService.MUSIC_FINISHED_MESSAGE)) {
                    //10 MINUTE FREE PLAYBACK IS DONE
                    Log.d(getClass().getSimpleName(), "Free music playback is finished");
                    isMusicFinished = true;

                    if (musicBound) {
                        unbindService(musicConnection);
                        stopService(playIntent);
                        if (mNoMusicBackground != null)mNoMusicBackground.setVisibility(View.VISIBLE);
                        if (mNoMusicText != null)mNoMusicText.setVisibility(View.VISIBLE);
                        if (mNoMusicText != null) mNoMusicText.setText(getResources().getString(R.string.hiit_timer_music_finished));
                        mDrawerList.setVisibility(View.GONE);
                        mPlayPauseIcon.setVisibility(View.GONE);
                        mSkipNextIcon.setVisibility(View.GONE);
                        mSkipPrevIcon.setVisibility(View.GONE);
                        mShuffleIcon.setVisibility(View.GONE);
                    }
                    LocalBroadcastManager.getInstance(HiitTimerActivity.this).unregisterReceiver(broadcastReceiver);
                }
            }
        };

        ViewTreeObserver vto = mAlbumArt.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                prepareSongUI(songPosition);
                ViewTreeObserver obs = mAlbumArt.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void prepSongData(boolean isConfigChanged) {
        if (!isConfigChanged) SongSingleton.getInstance().prepareAlbumArt();
        mSongList = new ArrayList<>();
        mSongList.addAll(SongSingleton.getInstance().getSelectedSongs());
        mAdapter = new MusicDrawerListAdapter(this, mSongList);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (musicService != null) {
                    prepareSongUI(position);
                    musicService.setSong(position);
                    musicService.playSong();
                }
            }
        });

    }

    public void initiateTimerData() {
        mTimerMinsTextView = (TextView) findViewById(R.id.hiit_timer_mins_textView);
        mTimerSecsTextView = (TextView) findViewById(R.id.hiit_timer_seconds_textView);
        mTotalTextView = (TextView) findViewById(R.id.hiit_timer_total_textView);
        mWorkoutTextView = (TextView) findViewById(R.id.hiit_timer_workout_textView);

        mProgressBar = (ProgressBar) findViewById(R.id.hiit_timer_progressBar);
        if (mProgressBar != null) {
            mProgressBar.setOnClickListener(this);
        }

        if (themeMode == 3 || themeMode == 5) {
            mWorkoutTextView.setTextColor(Color.WHITE);
        }
        workoutTvSize = mWorkoutTextView.getTextSize();

        HiitSingleton hiitSingleton = HiitSingleton.getInstance();
        if (!getIntent().hasExtra("TIMER_SET_POSITION")) {
            hiitTimerSet = hiitSingleton.getTimers().get((hiitSingleton.getTimers().size() - 1));
        } else {
            hiitTimerSet = hiitSingleton.getTimers().get(getIntent().getIntExtra("TIMER_SET_POSITION", -1));
        }
        warmup = hiitTimerSet.getWarmup();
        reps = hiitTimerSet.getReps();
        cooldown = hiitTimerSet.getCooldown();
        total = hiitTimerSet.getTotal();
        totalSecsToGo = total;

        workoutNames = hiitTimerSet.getWorkoutNames().split("=");
        String[] stringWorkSeconds = hiitTimerSet.getWorkSeconds().split("=");
        String[] stringRestSeconds = hiitTimerSet.getRestSeconds().split("=");
        workSeconds = new int[reps];
        restSeconds = new int[reps];
        for (int i = 0; i < reps; i++) {
            workSeconds[i] = Integer.parseInt(stringWorkSeconds[i]);
            restSeconds[i] = Integer.parseInt(stringRestSeconds[i]);
        }


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mTotalTextView.setText(TimerUtils.convertRawSecIntoString(total));
        mRepsTextView = (TextView) findViewById(R.id.hiit_timer_reps_textView);
        if (mRepsTextView != null) {
            String currReps = "0 / " + reps;
            mRepsTextView.setText(currReps);
        }
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        timerFragment = (TimerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (timerFragment == null) {
            timerFragment = new TimerFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArray(WORKOUT_NAMES_KEY, workoutNames);
            bundle.putIntArray(WORKOUT_SECONDS_KEY, workSeconds);
            bundle.putIntArray(RECOVERY_SECONDS_KEY, restSeconds);
            bundle.putInt(WARMUP_SECONDS_KEY, warmup);
            bundle.putInt(COOLDOWN_SECONDS_KEY, cooldown);
            bundle.putInt(TOTAL_SECONDS_KEY, total);
            timerFragment.setArguments(bundle);
            fm.beginTransaction().add(timerFragment, FRAGMENT_TAG).commit();
        }
    }

    private void configAnim() {
        anim = new MyAnimation(mProgressBar, total * 100, 0);
        anim.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void onTick(int remainingSeconds) {
        totalSecsToGo--;
        try {
            currentSeconds = remainingSeconds;
            handleTimerUI(remainingSeconds, totalSecsToGo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (remainingSeconds <= tickCount && remainingSeconds > 0) {
                fireTick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            launchNotification(remainingSeconds, currentActionName, notifBuilder, notifManager, notificationId);
        } catch (Exception e) {

        }
    }

    @Override
    public void onNewRound(int duration, int action, int reps) {
        try {
            if (action != TimerFragment.ACTION_WARMUP) {
                fireBeep();
                totalSecsToGo--;
            }
            currentSeconds = duration;
            this.duration = duration;
            this.currentReps = reps;
            this.action = action;
            currentActionName = actionSwitcher(action);
            mProgressBar.setMax(duration * 100);
            animateProgressBar(duration);
            handleTimerUI(duration, action, reps, totalSecsToGo);
            launchNotification(duration, currentActionName, notifBuilder, notifManager, notificationId);
            Log.d(TAG, "onNewRound: CurrentReps: "+currentReps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimerFinished() {
        try {
            fireBeep();
            isTimerActive = false;
            handleFinishedTimerUI();
            writeThisSessionIntoDB();
            closeNotification();
            RateDialogHelper.getInstance().setContext(this);
            RateDialogHelper.getInstance().checkIfShouldLaunchDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareSongUI(int position) {
        if (isMusicModeOn) {
            Log.d(TAG, "prepareSongUI: "+ mSongList.get(position).getAlbumartUri());
            mSongName.setText(mSongList.get(position).getmSongName());
            mArtist.setText(mSongList.get(position).getmArtist());
            Picasso.with(this)
                    .load(mSongList.get(position).getAlbumartUri())
                    .into(mAlbumArt);
        }
    }

    private void writeThisSessionIntoDB() {
        Log.d("MILLIS_Complete", "complete");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                TimerLog completedTimerSet = new TimerLog(warmup, hiitTimerSet.getWorkSeconds(), hiitTimerSet.getRestSeconds(), reps, cooldown, total, hiitTimerSet.getWorkoutNames());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                Date todaysDate = Calendar.getInstance().getTime();

                RealmResults<WorkoutLog> results = logRealm.where(WorkoutLog.class).findAll();

                String todaysStringDate = "DEFAULT_1";
                String lastStringWorkoutDate = "DEFAULT_2";
                WorkoutLog lastWorkoutLog = null;

                if (results.size() != 0) {
                    lastWorkoutLog = results.last();
                    todaysStringDate = simpleDateFormat.format(todaysDate);
                    lastStringWorkoutDate = simpleDateFormat.format(lastWorkoutLog.getmDate());
                }
                simpleDateFormat = null;

                logRealm.beginTransaction();
                if (!todaysStringDate.equals(lastStringWorkoutDate)) {
                    WorkoutLog newWorkoutLog = logRealm.createObject(WorkoutLog.class);
                    RealmList<TimerLog> newLogList = new RealmList<TimerLog>();
                    newLogList.add(completedTimerSet);
                    newWorkoutLog.setmDate(todaysDate);
                    newWorkoutLog.setTimerLogs(newLogList);
                    Log.d("SAVED_REALM", "ADDED A NEW WORKOUT LOG");
                } else {
                    if (lastWorkoutLog != null) {
                        lastWorkoutLog.getTimerLogs().add(completedTimerSet);
                        Log.d("SAVED_REALM", "ADDED A NEW TIMER LOG");
                    }
                }
                logRealm.commitTransaction();

                printUpdatedDatabase(logRealm);
                if (!getIntent().hasExtra("TIMER_SET_POSITION")) {
                    HiitSingleton.getInstance().getTimers().remove(HiitSingleton.getInstance().getTimerListSize() - 1);
                }
            }
        });
    }


    private void fireTick() {
        mSoundPoolHelper.onFireRequested(mTickResource, MySoundPoolHelper.FLAG_TICK_SOUND);
    }

    private void fireBeep() {
        if (!isFinishing()) {
            if (isVibrationAllowed) {
                v.vibrate(1000);
            }
            if (!isOverlapAllowed) {
                if (isMusicModeOn && musicService != null) {
                    musicService.pauseSongForBeep(mBeepDuration);
                }
            }
            mSoundPoolHelper.onFireRequested(mBeepResource, MySoundPoolHelper.FLAG_BEEP_SOUND);
        }
    }

    private void configNotification(int seconds, int action) {
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        launchNotification(seconds, actionSwitcher(action), createNotification(), notifManager, notificationId);
        Log.d(TAG, "launchNotification: Notification id: "+notificationId);
    }

    private NotificationCompat.Builder createNotification() {
        notifBuilder = new NotificationCompat.Builder(HiitTimerActivity.this);
        notifBuilder.setContentTitle("UltraFit. timer is running")
                .setContentText("Start!")
                .setSmallIcon(R.drawable.status_bar_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(HiitTimerActivity.this,
                        (int) System.currentTimeMillis(),
                        new Intent(HiitTimerActivity.this, HiitTimerActivity.class), 0));
        Intent killNotifIntent = new Intent(HiitTimerActivity.this, KillNotificationsService.class);
        startService(killNotifIntent);
        return notifBuilder;
    }


    private void launchNotification(int seconds, String session, NotificationCompat.Builder builder, NotificationManager manager, int id) {
        String remainingTime = TimerUtils.convertRawSecIntoString(seconds);
        builder.setContentText(session + "! " + remainingTime);
        manager.notify(id, builder.build());
    }

    private void closeNotification() {
        notifManager.cancel(notificationId);
    }

    private void printUpdatedDatabase(Realm realm) {
        RealmResults<WorkoutLog> workoutList = realm.where(WorkoutLog.class).findAll();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM dd, yyyy, EEE");
        WorkoutLog currentWorkoutLog = null;
        TimerLog currentTimerLog = null;
        for (int i = 0; i < workoutList.size(); i++) {
            currentWorkoutLog = workoutList.get(i);
            Log.d("CURRENT_DATABASE", "DATE: " + simpleDateformat.format(workoutList.get(i).getmDate()));
            for (int j = 0; j < workoutList.get(i).getTimerLogs().size(); j++) {
                currentTimerLog = currentWorkoutLog.getTimerLogs().get(j);
                Log.d("CURRENT_DATABASE", ", WARMUP: "
                        + currentTimerLog.getWarmup() + ", WORK: "
                        + currentTimerLog.getTotalWorkOrRestSeconds(currentTimerLog.getWorkSecs()) + ", RESTS: "
                        + currentTimerLog.getTotalWorkOrRestSeconds(currentTimerLog.getRestSecs()) + ", REPS: "
                        + currentTimerLog.getReps() + ", COOL DOWN: "
                        + currentTimerLog.getCooldown() + ", TOTAL: "
                        + currentTimerLog.getTotal() + ", "
                        + currentTimerLog.getWorkoutNames() + ", ");

            }
        }

        RealmResults<HiitTimerSet> timerSetList = realm.where(HiitTimerSet.class).findAll();
        for (int i = 0; i < timerSetList.size(); i++) {
            Log.d("SIMPLE_PRESETS", ", WARMUP: "
                    + timerSetList.get(i).getWarmup() + ", WORK: "
                    + timerSetList.get(i).getWork() + ", RESTS: "
                    + timerSetList.get(i).getRest() + ", REPS: "
                    + timerSetList.get(i).getReps() + ", COOL DOWN: "
                    + timerSetList.get(i).getCooldown() + ", TOTAL: "
                    + timerSetList.get(i).getTotal() + ", ");
        }
        realm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hiit_timer_progressBar:
                if (isTimerActive) {
                    if (!isTimerStopped) {
                        timerFragment.pauseTimer();
                        anim.pause();
                        isTimerStopped = true;
                    } else {
                        timerFragment.resumeTimer();
                        anim.resume();
                        isTimerStopped = false;
                    }
                }
                break;
            case R.id.drawer_play_pause_icon:
                if (musicService.pauseSong()) {
                    mPlayPauseIcon.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    mPlayPauseIcon.setImageResource(R.drawable.ic_pause);
                }
                break;
            case R.id.drawer_skip_next_icon:
                musicService.skipSong(true);
                mPlayPauseIcon.setImageResource(R.drawable.ic_pause);
                break;
            case R.id.drawer_skip_prev_icon:
                musicService.skipSong(false);
                mPlayPauseIcon.setImageResource(R.drawable.ic_pause);
                break;
            case R.id.drawer_shuffle_icon:
                if (musicService.shuffleOn()) {
                    mShuffleIcon.setImageResource(R.drawable.ic_action_playback_schuffle);
                    Toast.makeText(HiitTimerActivity.this, "Shuffle on", Toast.LENGTH_SHORT).show();
                    saveShuffleIntoSharedPref(true);
                } else {
                    mShuffleIcon.setImageResource(R.drawable.ic_action_playback_repeat);
                    Toast.makeText(HiitTimerActivity.this, "Shuffle off", Toast.LENGTH_SHORT).show();
                    saveShuffleIntoSharedPref(false);
                }
                break;
            case R.id.music_drawer_button:
                mDrawerLayout.openDrawer(Gravity.LEFT);
            default:
                break;
        }
    }

    private void saveShuffleIntoSharedPref(boolean isShuffleOn) {
        sharedPref = getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(MainActivity.sharedPrefShuffleKey);
        editor.putBoolean(MainActivity.sharedPrefShuffleKey, isShuffleOn);
        Log.d(getClass().getSimpleName(), sharedPref.getBoolean(MainActivity.sharedPrefShuffleKey, false) + "");
        editor.apply();
    }

    public void animateProgressBar(int time) {
        long fromTo = (long) time;
        anim.setFrom(fromTo * 100);
        anim.setDuration(time * 1000);
        anim.setTo(0);
        mProgressBar.startAnimation(anim);
    }

    private String actionSwitcher(int action) {
        switch (action) {
            case TimerFragment.ACTION_WARMUP:
                return "WARM UP";
            case TimerFragment.ACTION_WORK:
                return workoutNames[currentReps - 1];
            case TimerFragment.ACTION_REST:
                return "REST";
            case TimerFragment.ACTION_COOLDOWN:
                return "COOL DOWN";
        }
        return "WORK";
    }

    private void handleTimerUI(int duration, int action, int currentReps, int remainingTotal) {
        mWorkoutTextView.setText(actionSwitcher(action));
        Log.d(TAG, "handleTimerUI: "+actionSwitcher(action));
        if (action == TimerFragment.ACTION_WORK) mRepsTextView.setText(currentReps + " / " + reps);
        handleTimerUI(duration, remainingTotal);
    }

    private void handleTimerUI(int currentSeconds, int remainingTotal) {
        String[] minSec = TimerUtils.convertRawSecIntoStringArr(currentSeconds);
        mTimerMinsTextView.setText(minSec[0]);
        mTimerSecsTextView.setText(minSec[1]);
        mTotalTextView.setText(TimerUtils.convertRawSecIntoString(remainingTotal));
    }

    private void handleFinishedTimerUI() {
        mWorkoutTextView.setText("COMPLETE");
        mTimerMinsTextView.setText("00");
        mTimerSecsTextView.setText("00");
        mTotalTextView.setText("00:00");
    }

    @Override
    public void onBackPressed() {
        if (isTimerActive) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want to exit?");
            builder.setMessage("The timer will be canceled");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent returningIntent = new Intent();
                    setResult(RESULT_OK, returningIntent);
                    finish();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        if (isMusicModeOn) {
            if (playIntent == null) {
                playIntent = new Intent(this, MusicService.class);
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                if (!isConfigChanged) startService(playIntent);
            }
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(MusicService.FROM_MUSIC_SERVICE));
            prepareSongUI(songPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(getClass().getSimpleName(), "Service tracker : On Stop");
        if (isFinishing()) {
            Log.d(getClass().getSimpleName(), "Service tracker : Finishing");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 1. Music mode is on (there are more than one song in the list) -> unbind service. do not stop it.
        // 2. Music mode is off -> service doesn't exist. do nothing.
        // 3. Music finished (free playback is finished) -> service is already unbound, and stopped. reciever is also unregister. so do nothing.
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "Service tracker : On Destroy");
        if (isMusicModeOn && !isMusicFinished) {
            if (musicBound) {
                unbindService(musicConnection);
            }
            if (isFinishing()) {
                stopService(playIntent);
                stopService(new Intent(HiitTimerActivity.this, KillNotificationsService.class));
            }
            musicService = null;
        }
        notifManager.cancel(notificationId);
        mSoundPoolHelper = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TIME_BEFORE_CONFIG_CHANGE, currentSeconds);
        outState.putInt(TOTAL_BEFORE_CONFIG_CHANGE, totalSecsToGo);
        outState.putInt(CURRENT_REPS_BEFORE_CONFIG_CHANGE, currentReps);
        outState.putInt(ACTION_BEFORE_CONFIG_CHANGE, action);
        outState.putInt(ROUND_DURATION_BEFORE_CONFIG_CHANGE, duration);
        outState.putBoolean(IS_TIMER_PAUSED_BEFORE_CONFIG_CHANGE, isTimerStopped);
        outState.putBoolean(IS_TIMER_ACTIVE_BEFORE_CONFIG_CHANGE, isTimerActive);
        Log.d(TAG, "onSaveInstanceState: Writing value "+currentSeconds + ", Total " + totalSecsToGo + " seconds left");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (musicService != null) {
            musicService.pauseSong();
        }
    }

    public int beepThemeSwitcher(int beepTheme) {
        switch (beepTheme) {
            case 1:
                readDuration(R.raw.beep_basic_beep);
                return R.raw.beep_basic_beep;
            case 2:
                readDuration(R.raw.beep_whistle);
                return R.raw.beep_whistle;
            case 3:
                readDuration(R.raw.beep_machine_gun);
                return R.raw.beep_machine_gun;
            case 4:
                readDuration(R.raw.beep_rifle_shot);
                return R.raw.beep_rifle_shot;
            case 5:
                readDuration(R.raw.beep_chime_bell);
                return R.raw.beep_chime_bell;
            case 6:
                readDuration(R.raw.beep_click);
                return R.raw.beep_click;
            case 7:
                readDuration(R.raw.beep_glass_click);
                return R.raw.beep_glass_click;
            case 8:
                readDuration(R.raw.beep_home_run);
                return R.raw.beep_home_run;
            case 9:
                readDuration(R.raw.beep_xylophone_beep);
                return R.raw.beep_xylophone_beep;
            default:
                readDuration(R.raw.beep_basic_beep);
                return R.raw.beep_basic_beep;
        }
    }

    public int tickThemeSwitcher(int tickTheme) {
        switch (tickTheme) {
            case 1:
                return R.raw.tick_basic_tick;
            case 2:
                return R.raw.tick_camera_click;
            case 3:
                return R.raw.tick_hint;
            case 4:
                return R.raw.tick_pistol;
            case 5:
                return R.raw.tick_shotgun_pump;
            case 6:
                return R.raw.tick_temple_block_hit;
            case 7:
                return R.raw.tick_xylophone_tick;
            default:
                return R.raw.beep_basic_beep;
        }
    }

    private void readDuration(int resource) {
        Uri mediaPath =  Uri.parse("android.resource://" + getPackageName() + "/" + resource);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, mediaPath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mBeepDuration = Long.parseLong(duration);
    }

}