package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Wasabi on 4/28/2016.
 */
public class CurrentMusicAdapter extends RecyclerView.Adapter<CurrentMusicAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Song> mSongs;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView songNameTextView, artistNextView;
        ImageView deleteIcon;

        public ViewHolder (View itemView){
            super(itemView);
            songNameTextView = (TextView) itemView.findViewById(R.id.current_music_list_song_name);
            artistNextView = (TextView) itemView.findViewById(R.id.current_music_list_artist);
            deleteIcon = (ImageView) itemView.findViewById(R.id.current_music_delete_icon);
        }
    }

    public CurrentMusicAdapter(Context context, ArrayList<Song> mSongs) {
        this.context = context;
        this.mSongs = mSongs;
    }

    @Override
    public CurrentMusicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.current_music_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Song currentSong = mSongs.get(position);

        holder.songNameTextView.setText(currentSong.getmSongName());
        holder.artistNextView.setText(currentSong.getmArtist());
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
        int theme = Integer.parseInt(defaultPref.getString("COLOR_THEME_KEY", "1"));
        if (theme == 3 || theme == 5){
            holder.deleteIcon.setImageResource(R.drawable.ic_close_white);
        }

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSongs.remove(holder.getAdapterPosition());
                int singletonPosition = SongSingleton.getInstance().getSelectedSongs().get(holder.getAdapterPosition()).getSingletonPosition();
                SongSingleton.getInstance().getSelectedSongs().remove(holder.getAdapterPosition());
                SongSingleton.getInstance().getSongList().get(singletonPosition).setAdded(false);
                Log.d("SONG_", SongSingleton.getInstance().getSongList().get(singletonPosition).getmSongName() + ": "
                        + SongSingleton.getInstance().getSongList().get(singletonPosition).isAdded());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }
}
