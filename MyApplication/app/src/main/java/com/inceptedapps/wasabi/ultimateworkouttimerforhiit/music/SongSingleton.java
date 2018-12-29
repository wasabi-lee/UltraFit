package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Wasabi on 4/28/2016.
 */
public class SongSingleton {

    public static SongSingleton instance;
    public static ArrayList<Song> songList;
    public static ArrayList<Song> selectedSongs;
    public static String[] selectedSongIds;

    private SongSingleton() {
        songList = new ArrayList<>();
        selectedSongs = new ArrayList<>();
    }

    public static SongSingleton getInstance() {
        if (instance == null) {
            instance = new SongSingleton();
        }
        return instance;
    }

    public void setSelectedSongs(Context context, String songIds) {
        if (songIds.equals("-1")) {
            // No songs are selected
            selectedSongs.clear();
            selectedSongIds = null;
        } else {
            // Parse selected songs
            selectedSongIds = songIds.split("~");
            prepSavedSongs(context);

        }
    }

    public String[] getSelectedSongIds() {
        return selectedSongIds;
    }

    public void setSelectedSongIds(String[] selectedSongIds) {
        if (selectedSongIds == null){
            SongSingleton.selectedSongIds = null;
        } else {
            SongSingleton.selectedSongIds = new String[selectedSongIds.length];
            SongSingleton.selectedSongIds = selectedSongIds;
        }
    }

    public void prepSavedSongs(Context context){
        selectedSongs.clear();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = "";
        for (int i = 0; i < selectedSongIds.length; i++) {
            if (i == selectedSongIds.length-1) {
                selection += MediaStore.Audio.Media._ID + " = ?";
            } else {
                selection += MediaStore.Audio.Media._ID + " = ? OR ";
            }
        }
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, selectedSongIds, null);
        if (musicCursor != null && musicCursor.moveToFirst()){
            int songNameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do{
                long thisSongId = musicCursor.getLong(songIdColumn);
                String thisSongName = musicCursor.getString(songNameColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisAlbumId = musicCursor.getShort(albumIdColumn);
                selectedSongs.add(new Song(thisSongId, thisAlbumId, thisSongName, thisArtist, true));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        context = null;
    }

    public void addSong(Song newSong) {
        newSong.setSingletonPosition(songList.size());
        songList.add(newSong);
    }

    public int getSongSize() {
        return songList.size();
    }

    public void updateSelectedSongs(){
        selectedSongs.clear();
        Log.d(getClass().getSimpleName(), "Song list size = "+songList.size());
        ArrayList<String> newSongIds = new ArrayList<>();
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).isAdded()) {
                selectedSongs.add(songList.get(i));
                newSongIds.add(String.valueOf(songList.get(i).getmSongId()));
            }
        }
        setSelectedSongIds(newSongIds.toArray(new String[newSongIds.size()]));
    }

    public void prepareAlbumArt() {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUriWithId = null;
        for (int i = 0; i < selectedSongs.size(); i++) {
            albumArtUriWithId = ContentUris.withAppendedId(albumArtUri, selectedSongs.get(i).getmAlbumId());
            selectedSongs.get(i).setAlbumartUri(albumArtUriWithId);
        }
    }

    public ArrayList<Song> getSelectedSongs() {
        return selectedSongs;
    }

    public void clearAndAddSongList(ArrayList<Song> newSongList){
        songList.clear();
        songList.addAll(newSongList);
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        SongSingleton.songList = songList;
    }
}
