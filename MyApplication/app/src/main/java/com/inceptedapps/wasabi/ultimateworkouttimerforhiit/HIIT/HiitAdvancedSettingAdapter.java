package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Wasabi on 4/24/2016.
 */
public class HiitAdvancedSettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(WorkoutDetails item, int position);
    }

    private ArrayList<WorkoutDetails> mDetails;
    private Context mContext;
    private final OnItemClickListener mListener;

    private static final int FOOTER_VIEW = 1;

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView roundNumberTextView, workoutNameTextView, workSecsTextView, restSecsTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            roundNumberTextView = (TextView) itemView.findViewById(R.id.advanced_setting_round_name_text_view);
            workoutNameTextView = (TextView) itemView.findViewById(R.id.advanced_setting_workout_name_text_view);
            workSecsTextView = (TextView) itemView.findViewById(R.id.advanced_setting_workout_time_text_view);
            restSecsTextView = (TextView) itemView.findViewById(R.id.advanced_setting_rest_time_text_view);
        }

        public void bind(final WorkoutDetails detail, final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(detail, position);
                }
            });
        }
    }

    public HiitAdvancedSettingAdapter(Context mContext, ArrayList<WorkoutDetails> mDetails, OnItemClickListener mListener) {
        this.mContext = mContext;
        this.mDetails = mDetails;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent, false);
            return new FooterViewHolder(v);
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.advanced_setting_item, parent, false);
        return new NormalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof NormalViewHolder) {
                NormalViewHolder vh = (NormalViewHolder) holder;
                WorkoutDetails currentDetail = mDetails.get(position);

                String roundName = "Round " + (position + 1);
                vh.roundNumberTextView.setText(roundName);
                vh.workoutNameTextView.setText(currentDetail.getWorkoutName());
                vh.workSecsTextView.setText(timeConverterToString(currentDetail.getWorkSecs()));
                vh.restSecsTextView.setText(timeConverterToString(currentDetail.getRestSecs()));
                vh.bind(mDetails.get(position), position, mListener);
                roundName = null;
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder vh = (FooterViewHolder) holder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (mDetails == null) {
            return 0;
        }
        if (mDetails.size() == 0) {
            return 1;
        }
        return mDetails.size() + 1;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDetails.size()) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    private String timeConverterToString(int rawTotalSec) {

        int rawTotalMin = 0;

        if (rawTotalSec >= 60) {
            rawTotalMin = rawTotalMin + (int) TimeUnit.SECONDS.toMinutes(rawTotalSec);
            rawTotalSec = rawTotalSec - (int) TimeUnit.SECONDS.toMinutes(rawTotalSec) * 60;
        }

        String totalMin = String.valueOf(rawTotalMin);
        String totalSec = String.valueOf(rawTotalSec);

        if (rawTotalSec < 10) {
            totalSec = "0" + rawTotalSec;
        }
        return totalMin + ":" + totalSec;
    }

}
