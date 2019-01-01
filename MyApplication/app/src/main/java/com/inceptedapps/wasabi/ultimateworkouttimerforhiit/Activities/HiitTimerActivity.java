package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.ExitTimerDialogFragment;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.TimerFragment;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.TimerSession;
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
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.RoundHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.SharedPrefHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.SoundIdSwitcher;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HiitTimerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, TimerFragment.TimerOnTickListener {

    //Declaring Views and the timer set object

    private static final String TAG = HiitTimerActivity.class.getSimpleName();
    private static final String NOTIFICATION_ID_KEY = "notif_id_key";
    private static final String EXIT_TIMER_DIALOG_TAG = "exit_timer_dialog_tag";

    @BindView(R.id.music_drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.hiit_timer_mins_textView)
    TextView mTimerMinsTextView;

    @BindView(R.id.hiit_timer_seconds_textView)
    TextView mTimerSecsTextView;

    @BindView(R.id.hiit_timer_total_textView)
    TextView mTotalTextView;

    @BindView(R.id.hiit_timer_workout_textView)
    TextView mWorkoutTextView;

    @BindView(R.id.hiit_timer_reps_textView)
    TextView mRepsTextView;

    @BindView(R.id.no_music_text)
    TextView mNoMusicText;

    @BindView(R.id.hiit_timer_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.hiit_timer_album_art)
    ImageView mAlbumArt;

    @BindView(R.id.drawer_skip_next_icon)
    ImageView mSkipNextIcon;

    @BindView(R.id.drawer_skip_prev_icon)
    ImageView mSkipPrevIcon;

    @BindView(R.id.drawer_play_pause_icon)
    ImageView mPlayPauseIcon;

    @BindView(R.id.drawer_shuffle_icon)
    ImageView mShuffleIcon;

    @BindView(R.id.no_music_background)
    ImageView mNoMusicBackground;

    @BindView(R.id.hiit_timer_song_name)
    TextView mSongName;

    @BindView(R.id.hiit_timer_artist)
    TextView mArtist;

    @BindView(R.id.music_drawer_button)
    ImageView mMusicDrawerButton;

    @BindView(R.id.adView)
    AdView mAdView;

    @BindView(R.id.hiit_timer_song_list)
    ListView mDrawerList;

    private boolean isMusicModeOn, isTimerActive = true, isTimerStopped;
    private ArrayList<Song> mSongList;
    private boolean isConfigChanged = false;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private BroadcastReceiver broadcastReceiver;
    private int songPosition;
    private long mBeepDuration = 1000;

    float workoutTvSize;
    HiitTimerSet hiitTimerSet;

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

    private MySoundPoolHelper mSoundPoolHelper;

    private Realm logRealm;
    private boolean isPremium;

    private TimerFragment timerFragment;
    private static final String FRAGMENT_TAG = "fragment_tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(SharedPrefHelper.getThemeId(this)));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_timer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);


        retrieveSharedPrefValues();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initializeAds();
        instantiateRealm();
        initiateSoundPool();
        initiateMusicData();
        initTimerData();
        initFragment();
        configAnim();

        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        } else {
            configNotification(warmup, TimerFragment.ACTION_WARMUP);
        }
    }


    private void restoreSavedState(Bundle savedState) {
        isConfigChanged = true;

        currentSeconds = savedState.getInt(TIME_BEFORE_CONFIG_CHANGE);
        totalSecsToGo = savedState.getInt(TOTAL_BEFORE_CONFIG_CHANGE);
        action = savedState.getInt(ACTION_BEFORE_CONFIG_CHANGE);
        duration = savedState.getInt(ROUND_DURATION_BEFORE_CONFIG_CHANGE);
        currentReps = savedState.getInt(CURRENT_REPS_BEFORE_CONFIG_CHANGE);
        isTimerActive = savedState.getBoolean(IS_TIMER_ACTIVE_BEFORE_CONFIG_CHANGE);
        isTimerStopped = savedState.getBoolean(IS_TIMER_PAUSED_BEFORE_CONFIG_CHANGE);

        Log.d(TAG, "onCreate: Remaining time: " + currentSeconds +
                ", isTimerActive: " + isTimerActive +
                ", isTimerStopped: " + isTimerStopped +
                ", Duration: " + duration +
                ", reps: " + reps +
                ", action: " + action +
                ", total: " + totalSecsToGo);

        mProgressBar.setMax(duration * 100);
        mProgressBar.setProgress(currentSeconds);
        if (!isTimerStopped) animateProgressBar(currentSeconds);
        handleTimerUI(currentSeconds, action, reps, totalSecsToGo);
        configNotification(currentSeconds, action);
    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (isMusicModeOn) {
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
        isPremium = SharedPrefHelper.isPremium(this);
        isOverlapAllowed = SharedPrefHelper.shouldMusicOverlapCue(this);
        isVibrationAllowed = SharedPrefHelper.isVibrationEnabled(this);
        tickCount = Integer.parseInt(SharedPrefHelper.getTickCounts(this));
        themeMode = Integer.parseInt(SharedPrefHelper.getThemeId(this));

        isMusicModeOn = SongSingleton.getInstance().getSelectedSongs().size() != 0;

        int beepTheme = 1;
        int tickTheme = 1;
        try {
            beepTheme = SharedPrefHelper.getBeepSoundId(this);
            tickTheme = SharedPrefHelper.getTickSoundId(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBeepResource = SoundIdSwitcher.beepThemeSwitcher(beepTheme);
        mTickResource = SoundIdSwitcher.tickThemeSwitcher(tickTheme);
        mBeepDuration = readDuration(mBeepResource);
    }


    private void initiateSoundPool() {
        mSoundPoolHelper = new MySoundPoolHelper(this);
        mSoundPoolHelper.initSoundPool();
    }


    private void initiateMusicData() {
        if (isMusicModeOn) {
            initMusicViews();
            prepSongData(isConfigChanged);
        } else {
            mNoMusicText.setVisibility(View.VISIBLE);
            mNoMusicBackground.setVisibility(View.VISIBLE);
        }
    }


    private void initMusicViews() {
        mShuffleIcon.setImageResource(SharedPrefHelper.isShuffleEnabled(this) ?
                R.drawable.ic_action_playback_schuffle : R.drawable.ic_action_playback_repeat);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(MusicService.UI_CHANGE_MESSAGE)) {
                    //Change song album art
                    songPosition = intent.getIntExtra(MusicService.UI_CHANGE_MESSAGE, -1);
                    prepareSongUI(songPosition);
                }
            }
        };

        prepareSongUI(songPosition);
    }


    private void prepSongData(boolean isConfigChanged) {

        if (!isConfigChanged)
            SongSingleton.getInstance().prepareAlbumArt();

        mSongList = new ArrayList<>();
        mSongList.addAll(SongSingleton.getInstance().getSelectedSongs());
        MusicDrawerListAdapter mAdapter = new MusicDrawerListAdapter(this, mSongList);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener((parent, view, position, id) -> {
            if (musicService != null) {
                prepareSongUI(position);
                musicService.setSong(position);
                musicService.playSong();
            }
        });
    }


    public void initTimerData() {

        workoutTvSize = mWorkoutTextView.getTextSize();

        HiitSingleton hiitSingleton = HiitSingleton.getInstance();
        int timerSetPosition = getIntent().getIntExtra("TIMER_SET_POSITION", -1);
        hiitTimerSet = hiitSingleton.getTimers().get(timerSetPosition != -1 ?
                timerSetPosition : hiitSingleton.getTimers().size() - 1);

        warmup = hiitTimerSet.getWarmup();
        reps = hiitTimerSet.getReps();
        cooldown = hiitTimerSet.getCooldown();
        total = hiitTimerSet.getTotal();
        totalSecsToGo = total;

        workoutNames = RoundHelper.getWorkoutNameArr(hiitTimerSet);
        workSeconds = RoundHelper.getTimeArr(hiitTimerSet, TimerSession.WORK);
        restSeconds = RoundHelper.getTimeArr(hiitTimerSet, TimerSession.REST);

        mTotalTextView.setText(TimerUtils.convertRawSecIntoString(total));
        String currReps = "0 / " + reps;
        mRepsTextView.setText(currReps);

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
            Log.d(TAG, "onNewRound: CurrentReps: " + currentReps);
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
        if (isMusicModeOn && position != -1) {
            mSongName.setText(mSongList.get(position).getmSongName());
            mArtist.setText(mSongList.get(position).getmArtist());
            Picasso.with(this)
                    .load(mSongList.get(position).getAlbumartUri())
                    .into(mAlbumArt);
        }
    }


    private void writeThisSessionIntoDB() {
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

//                printUpdatedDatabase(logRealm);
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
        Log.d(TAG, "launchNotification: Notification id: " + notificationId);
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


    @OnClick(R.id.hiit_timer_progressBar)
    public void resumePauseTimer() {
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
    }


    @OnClick(R.id.drawer_play_pause_icon)
    public void resumePauseSong() {
        mPlayPauseIcon.setImageResource(musicService.pauseSong() ?
                R.drawable.ic_play_arrow : R.drawable.ic_pause);
    }


    @OnClick(R.id.drawer_skip_next_icon)
    public void skipNextSong() {
        musicService.skipSong(true);
        mPlayPauseIcon.setImageResource(R.drawable.ic_pause);
    }


    @OnClick(R.id.drawer_skip_prev_icon)
    public void skipPrevSong() {
        musicService.skipSong(false);
        mPlayPauseIcon.setImageResource(R.drawable.ic_pause);
    }


    @OnClick(R.id.drawer_shuffle_icon)
    public void shufflePlayer() {
        mShuffleIcon.setImageResource(musicService.shuffleOn() ?
                R.drawable.ic_action_playback_schuffle : R.drawable.ic_action_playback_repeat);
        SharedPrefHelper.setShuffle(this, musicService.shuffleOn());
    }


    @OnClick(R.id.music_drawer_button)
    public void openHideDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
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
        Log.d(TAG, "handleTimerUI: " + actionSwitcher(action));
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
            ExitTimerDialogFragment dialog = new ExitTimerDialogFragment();
            dialog.show(getSupportFragmentManager(), EXIT_TIMER_DIALOG_TAG);
        } else {
            finish();
        }
    }


    public void finishActivity() {
        Intent returningIntent = new Intent();
        setResult(RESULT_OK, returningIntent);
        finish();
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
        super.onDestroy();
        if (isMusicModeOn) {
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
        Log.d(TAG, "onSaveInstanceState: Writing value " + currentSeconds + ", Total " + totalSecsToGo + " seconds left");
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if (musicService != null) {
            musicService.pauseSong();
        }
    }


    private long readDuration(int resource) {
        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + resource);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, mediaPath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(duration);
    }

}