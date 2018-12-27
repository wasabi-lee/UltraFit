package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

/**
 * Created by Wasabi on 8/12/2016.
 */
public class SoundListAdapter extends ArrayAdapter {

    public interface OnSoundSelectedListener {
        void onSoundSelected(int position);
    }

    private String[] names;
    private Context context;
    private int selectedPosition;
    private OnSoundSelectedListener mCallback;

    public SoundListAdapter(Context context, String[] names, int selectedPosition, OnSoundSelectedListener mCallback) {
        super(context, -1);
        this.context = context;
        this.names = names;
        this.selectedPosition = selectedPosition;
        this.mCallback = mCallback;
    }

    static class ViewHolder {
        TextView nameTextView;
        CheckBox checkBox;
        LinearLayout container;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sound_selection_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.sound_item_text);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.sound_item_check_box);
            viewHolder.container = (LinearLayout) convertView.findViewById(R.id.sound_item_parent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameTextView.setText(names[position]);
        viewHolder.checkBox.setChecked(position == selectedPosition);
        viewHolder.container.setTag(position);
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = (Integer) view.getTag();
                notifyDataSetChanged();
                mCallback.onSoundSelected(position);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return names.length;
    }
}
