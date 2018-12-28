package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music;

import android.net.Uri;

/**
 * Created by Wasabi on 4/28/2016.
 */
public class Song {
    private long mSongId;
    private String mSongName;
    private String mArtist;
    private long mAlbumId;
    private int singletonPosition;
    private boolean isAdded;
    private Uri albumartUri;

    public Song(long mSongId, long mAlbumId, String mSongName, String mArtist, boolean isAdded) {
        this.mSongId = mSongId;
        this.mAlbumId = mAlbumId;
        this.mSongName = mSongName;
        this.mArtist = mArtist;
        this.isAdded = isAdded;
    }

    public int getSingletonPosition() {
        return singletonPosition;
    }

    public void setSingletonPosition(int singletonPosition) {
        this.singletonPosition = singletonPosition;
    }

    public Uri getAlbumartUri() {
        return albumartUri;
    }

    public void setAlbumartUri(Uri albumartUri) {
        this.albumartUri = albumartUri;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public long getmAlbumId() {
        return mAlbumId;
    }

    public String getmArtist() {
        return mArtist;
    }

    public long getmSongId() {
        return mSongId;
    }

    public String getmSongName() {
        return mSongName;
    }

}
