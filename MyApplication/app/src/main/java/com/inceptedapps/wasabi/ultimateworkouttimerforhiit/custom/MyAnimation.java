package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom;

import android.view.animation.AlphaAnimation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

/**
 * Created by Wasabi on 3/28/2016.
 */
public class MyAnimation extends AlphaAnimation {
    private ProgressBar progressBar;
    private float from;
    private float  to;
    private long mElapsedAtPause = 0;
    private boolean mPaused = false;

    public MyAnimation(ProgressBar progressBar, float from, float to) {
        super(from, to);
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if(mPaused && mElapsedAtPause==0){
            mElapsedAtPause = currentTime - getStartTime();
        }
        if(mPaused){
            setStartTime(currentTime-mElapsedAtPause);
        }
        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause(){
        mElapsedAtPause = 0;
        mPaused = true;
    }
    public void resume(){
        mPaused = false;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }

}
