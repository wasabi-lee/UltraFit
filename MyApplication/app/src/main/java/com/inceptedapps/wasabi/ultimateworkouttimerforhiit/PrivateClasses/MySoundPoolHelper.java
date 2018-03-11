package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Wasabi on 8/6/2016.
 */
public class MySoundPoolHelper implements SoundPool.OnLoadCompleteListener {
    
    private static final String TAG = MySoundPoolHelper.class.getSimpleName();

    private SoundPool mSoundPool;
    private int[] mLoadedSoundIds = new int[3];
    private boolean[] mIsSoundLoaded = new boolean[3];
    private Context mContext;
    //Position 0 = Beep
    //Position 1 = Tick
    //Position 2 = Halfway

    public static final int FLAG_BEEP_SOUND = 0;
    public static final int FLAG_TICK_SOUND = 1;
    public static final int FLAG_HALFWAY_SOUND = 2;

    public MySoundPoolHelper(Context context) {
        this.mContext = context;
    }

    public void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        mSoundPool.setOnLoadCompleteListener(this);
        for (int i = 0; i < mIsSoundLoaded.length; i++) {
            mIsSoundLoaded[i] = false;
        }
    }

    public void onFireRequested(int resource, int flag) {
        if (mLoadedSoundIds == null) mLoadedSoundIds = new int[3];
        if (mIsSoundLoaded[flag]) {
            Log.d(TAG, "onFireRequested: Playing loaded sound");
            playSoundWithSoundPool(mLoadedSoundIds[flag]);
        } else {
            mLoadedSoundIds[flag] = mSoundPool.load(mContext, resource, 1);
            Log.d(TAG, "onFireRequested: Playing unloaded sound. loading now...");
            mIsSoundLoaded[flag] = true;
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        playSoundWithSoundPool(sampleId);
    }

    private void playSoundWithSoundPool(int soundId) {
        mSoundPool.play(soundId, 0.9f, 0.9f, 1, 0, 1f);
        Log.d(TAG, "playSoundWithSoundPool: Fired sound "+soundId);
    }
}
