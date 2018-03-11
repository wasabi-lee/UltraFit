package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;

/**
 * Created by Wasabi on 4/28/2016.
 */
public class DeviceMusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnMusicCheckedListener {
        void onMusicSelected(Song item, int position);
    }

    private ArrayList<Song> songsInDevice;
    private Context mContext;
    private OnMusicCheckedListener mListener;

    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView, artistTextView;
        CheckBox checkBox;

        public SongViewHolder(View itemView) {
            super(itemView);
            songNameTextView = (TextView) itemView.findViewById(R.id.music_setting_song_name);
            artistTextView = (TextView) itemView.findViewById(R.id.music_setting_artist);
            checkBox = (CheckBox) itemView.findViewById(R.id.music_setting_checkbox);
        }

        public void bind(final Song song, final int position, final OnMusicCheckedListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("BEFORE_CHANGE", SongSingleton.getInstance().getSongList().get(position).isAdded()+" SONG NAME: "+ SongSingleton.getInstance().getSongList().get(position).getmSongName()+" POSITION: "+position);
                    SongSingleton.getInstance().getSongList().get(position).setAdded(!SongSingleton.getInstance().getSongList().get(position).isAdded());
                    checkBox.setChecked(SongSingleton.getInstance().getSongList().get(position).isAdded());
                    Log.d("CHANGED_OBJ", SongSingleton.getInstance().getSongList().get(position).isAdded()+" SONG NAME: "+ SongSingleton.getInstance().getSongList().get(position).getmSongName()+" POSITION: "+position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public DeviceMusicListAdapter(Context mContext, OnMusicCheckedListener mListener) {
        this.mContext = mContext;
        this.songsInDevice = SongSingleton.getInstance().getSongList();
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.music_list_item, parent, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SongViewHolder vh = (SongViewHolder) holder;
        final int selectedPosition = position;
        Song currentSong = songsInDevice.get(position);
        vh.songNameTextView.setText(currentSong.getmSongName());
        vh.artistTextView.setText(currentSong.getmArtist());
        vh.checkBox.setOnCheckedChangeListener(null);
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BEFORE_CHANGE", SongSingleton.getInstance().getSongList().get(selectedPosition).isAdded()+" SONG NAME: "+ SongSingleton.getInstance().getSongList().get(selectedPosition).getmSongName()+" POSITION: "+selectedPosition);
                SongSingleton.getInstance().getSongList().get(selectedPosition).setAdded(!SongSingleton.getInstance().getSongList().get(selectedPosition).isAdded());
                Log.d("CHANGED_OBJ", SongSingleton.getInstance().getSongList().get(selectedPosition).isAdded()+" SONG NAME: "+ SongSingleton.getInstance().getSongList().get(selectedPosition).getmSongName()+" POSITION: "+selectedPosition);
            }
        });
        if(currentSong.isAdded()) {
            vh.checkBox.setChecked(true);
            Log.d("SONG_", "MusicSettingActivity: onBindViewHolder: Position "+position+", This item is added");
        } else {
            vh.checkBox.setChecked(false);
        }

        vh.bind(currentSong, position, mListener);
    }

    @Override
    public int getItemCount() {
        return songsInDevice.size();
    }
}
