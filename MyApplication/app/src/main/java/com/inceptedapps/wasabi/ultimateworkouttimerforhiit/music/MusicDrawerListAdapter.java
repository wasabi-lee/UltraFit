package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;

/**
 * Created by Wasabi on 4/29/2016.
 */
public class MusicDrawerListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Song> mSongData;

    public MusicDrawerListAdapter(Context context, ArrayList<Song> mSongData) {
        super(context, -1);
        this.context = context;
        this.mSongData = mSongData;
    }

    static class ViewHolder {
        TextView songNameTextView, artistTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_music_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.songNameTextView = (TextView) convertView.findViewById(R.id.drawer_list_song_name);
            viewHolder.artistTextView = (TextView) convertView.findViewById(R.id.drawer_list_artist);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Song currentSong = mSongData.get(position);
        if(currentSong != null) {
            viewHolder.songNameTextView.setText(currentSong.getmSongName());
            viewHolder.artistTextView.setText(currentSong.getmArtist());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mSongData.size();
    }
}
