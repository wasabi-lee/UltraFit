package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitAdvancedSettingAdapter;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.WorkoutDetails;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.TimerUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdvancedSettingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String WORKOUT_REPS_EXTRA_KEY = "WORKOUT_REPS";
    public static final String WORKOUT_WORK_TIME_EXTRA_KEY = "WORKOUT_SECS";
    public static final String WORKOUT_REST_TIME_EXTRA_KEY = "REST_SECS";

    public static final String WORKOUT_NAME_DETAILS_KEY = "WORKOUT_NAME_DETAILS_KEY";
    public static final String WORK_SECS_DETAILS_KEY = "WORK_SECS_DETAILS_KEY";
    public static final String REST_SECS_DETAILS_KEY = "REST_SECS_DETAILS_KEY";

    public static final int REQUEST_CODE_FROM_SETTING_ACTIVITY = 1;

    private RecyclerView recyclerView;
    private ArrayList<WorkoutDetails> mWorkoutDetails;

    private ImageView mRemoveIcon, mAddIcon;
    private TextView mRoundNumberTextView;

    private int mReps, mWork, mRest;
    private String[] mWorkoutNames, mWorkSecs, mRestSecs;
    private int theme;
    private HiitAdvancedSettingAdapter mAdapter;

    private LinearLayout linearLayout;

    private EditText dialogWorkoutNameEdit, dialogWorkEdit, dialogRestEdit;
    private ImageView dialogWorkRightArrow, dialogWorkLeftArrow, dialogRestRightArrow, dialogRestLeftArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_setting);

        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        theme = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));

        initializeData();
        HiitAdvancedSettingAdapter.OnItemClickListener listener = new HiitAdvancedSettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WorkoutDetails item, int position) {
                if (theme == 3 || theme == 5) {
                    launchSettingDialog(item, position, 16974377);
                } else {
                    launchSettingDialog(item, position, 16974397);
                }
            }
        };
        mAdapter = new HiitAdvancedSettingAdapter(this, mWorkoutDetails, listener);
        recyclerView.setAdapter(mAdapter);
    }

    private void initializeData() {
        Toolbar advancedSettingToolbar = (Toolbar) findViewById(R.id.advanced_setting_toolbar);
        advancedSettingToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        advancedSettingToolbar.setTitle(getResources().getString(R.string.advanced_toolbar));
        setSupportActionBar(advancedSettingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = (LinearLayout) findViewById(R.id.advanced_setting_parent_linear);

        if (getIntent().hasExtra(WORKOUT_REPS_EXTRA_KEY)) {
            mReps = getIntent().getIntExtra(WORKOUT_REPS_EXTRA_KEY, 1);
            mWork = getIntent().getIntExtra(WORKOUT_WORK_TIME_EXTRA_KEY, 20);
            mRest = getIntent().getIntExtra(WORKOUT_REST_TIME_EXTRA_KEY, 10);
            mWorkoutDetails = new ArrayList<>();
            for (int i = 0; i < mReps; i++) {
                //TODO Generate workout details objects and add it to the ArrayList
                WorkoutDetails detail = new WorkoutDetails("Sprint", mWork, mRest);
                mWorkoutDetails.add(detail);
                detail = null;
            }
        } else if (getIntent().hasExtra(WORKOUT_NAME_DETAILS_KEY)) {
            mWorkoutNames = getIntent().getStringExtra(WORKOUT_NAME_DETAILS_KEY).split("=");
            mWorkSecs = getIntent().getStringExtra(WORK_SECS_DETAILS_KEY).split("=");
            mRestSecs = getIntent().getStringExtra(REST_SECS_DETAILS_KEY).split("=");
            mReps = mWorkoutNames.length;
            mWorkoutDetails = new ArrayList<>();
            for (int i = 0; i < mWorkoutNames.length; i++) {
                WorkoutDetails detail = new WorkoutDetails(mWorkoutNames[i], Integer.parseInt(mWorkSecs[i]), Integer.parseInt(mRestSecs[i]));
                mWorkoutDetails.add(detail);
                detail = null;
            }
        }

        mRemoveIcon = (ImageView) findViewById(R.id.advanced_setting_rep_remove_image_view);
        mAddIcon = (ImageView) findViewById(R.id.advanced_setting_rep_add_image_view);
        mRoundNumberTextView = (TextView) findViewById(R.id.advanced_setting_rep_text_view);

        mRemoveIcon.setOnClickListener(this);
        mAddIcon.setOnClickListener(this);
        String repsText = mReps + " REPS";
        mRoundNumberTextView.setText(repsText);

        recyclerView = (RecyclerView) findViewById(R.id.advanced_setting_recycler_view);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llManager);


    }

    private void launchSettingDialog(WorkoutDetails item, final int position, int theme) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedSettingActivity.this, theme);
        View dialogView = AdvancedSettingActivity.this
                .getLayoutInflater()
                .inflate(R.layout.setting_dialog, linearLayout, false);
        builder.setView(dialogView);

        dialogWorkoutNameEdit = (EditText) dialogView.findViewById(R.id.advanced_setting_dialog_workout_name_edit_text);
        dialogWorkEdit = (EditText) dialogView.findViewById(R.id.advanced_setting_dialog_work_edit_text);
        dialogRestEdit = (EditText) dialogView.findViewById(R.id.advanced_setting_dialog_rest_edit_text);
        dialogWorkLeftArrow = (ImageView) dialogView.findViewById(R.id.advanced_setting_dialog_work_left_arrow);
        dialogWorkRightArrow = (ImageView) dialogView.findViewById(R.id.advanced_setting_dialog_work_right_arrow);
        dialogRestLeftArrow = (ImageView) dialogView.findViewById(R.id.advanced_setting_dialog_rest_left_arrow);
        dialogRestRightArrow = (ImageView) dialogView.findViewById(R.id.advanced_setting_dialog_rest_right_arrow);

        if (theme == 16974377){
            dialogWorkLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);
            dialogRestLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);
            dialogWorkRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
            dialogRestRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
        }

        dialogWorkoutNameEdit.setText(item.getWorkoutName());
        dialogWorkEdit.setText(TimerUtils.convertRawSecIntoString(item.getWorkSecs()));
        dialogRestEdit.setText(TimerUtils.convertRawSecIntoString(item.getRestSecs()));

        dialogWorkEdit.setOnFocusChangeListener(this);
        dialogRestEdit.setOnFocusChangeListener(this);

        dialogWorkLeftArrow.setOnClickListener(this);
        dialogWorkRightArrow.setOnClickListener(this);
        dialogRestLeftArrow.setOnClickListener(this);
        dialogRestRightArrow.setOnClickListener(this);

        builder.setTitle("Custom Round " + (position+1));
        builder.setPositiveButton("DONE", null);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String workoutName = dialogWorkoutNameEdit.getText().toString();
                        if (workoutName.equals("") || workoutName.contains("=")){
                            dialogWorkoutNameEdit.setError("Please add a valid workout name");
                            return;
                        }
                        int rawWork = TimerUtils.stringTimeToSeconds(dialogWorkEdit.getText().toString());
                        int rawRest = TimerUtils.stringTimeToSeconds(dialogRestEdit.getText().toString());
                        WorkoutDetails updatedDetail =
                                new WorkoutDetails(workoutName,
                                        rawWork,
                                        rawRest);
                        mWorkoutDetails.set(position, updatedDetail);
                        mAdapter.notifyDataSetChanged();
                        d.dismiss();
                    }
                });
            }
        });
        d.show();
    }


    private String trimTime(String userInput, boolean isLeft) {
        String trimmedTime = "";
        int rawMinSec = TimerUtils.stringTimeToSeconds(userInput);
        if (isLeft) {
            if (rawMinSec > 0)
                return TimerUtils.convertRawSecIntoString(rawMinSec - 1);
        } else {
            return TimerUtils.convertRawSecIntoString(rawMinSec + 1);
        }
        return "00:00";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advanced_setting_rep_remove_image_view:
                controlButtonAction(false);
                break;
            case R.id.advanced_setting_rep_add_image_view:
                controlButtonAction(true);
                break;
            case R.id.advanced_setting_dialog_work_left_arrow:
                dialogWorkEdit.clearFocus();
                controlArrowAction(true, true);
                break;
            case R.id.advanced_setting_dialog_work_right_arrow:
                dialogWorkEdit.clearFocus();
                controlArrowAction(true, false);
                break;
            case R.id.advanced_setting_dialog_rest_left_arrow:
                dialogRestEdit.clearFocus();
                controlArrowAction(false, true);
                break;
            case R.id.advanced_setting_dialog_rest_right_arrow:
                dialogRestEdit.clearFocus();
                controlArrowAction(false, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            EditText currentEditText = (EditText) v;
            String userInput = currentEditText.getText().toString();
            currentEditText.setText(TimerUtils.convertRawSecIntoString(TimerUtils.stringTimeToSeconds(userInput)));
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(AdvancedSettingActivity.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            ((EditText) v).setText(getResources().getString(R.string.advanced_default_seconds));
        }
    }

    private void controlArrowAction(boolean isWork, boolean isLeft) {
        String userInput = "", trimmedTime = "";
        int rawMinSec;
        if (isWork) {
            userInput = dialogWorkEdit.getText().toString();
            if (isLeft) {
                dialogWorkEdit.setText(trimTime(userInput, true));
            } else {
                dialogWorkEdit.setText(trimTime(userInput, false));
            }
        } else {
            userInput = dialogRestEdit.getText().toString();
            if (isLeft) {
                dialogRestEdit.setText(trimTime(userInput, true));
            } else {
                dialogRestEdit.setText(trimTime(userInput, false));
            }
        }
        userInput = null;
        trimmedTime = null;
    }


    private void controlButtonAction(boolean addOneMoreRound) {
        if (addOneMoreRound) {
            WorkoutDetails newRoundDetail = new WorkoutDetails("Sprint", mWork, mRest);
            mWorkoutDetails.add(newRoundDetail);
            mAdapter.notifyItemInserted(mWorkoutDetails.size() - 1);
            mReps++;
            newRoundDetail = null;
        } else {
            if (mReps > 1) {
                mWorkoutDetails.remove(mWorkoutDetails.size() - 1);
                mAdapter.notifyDataSetChanged();
                mReps--;
            }
        }
        String repsText = mReps + " REPS";
        mRoundNumberTextView.setText(repsText);
        repsText = null;
    }

    @Override
    public void onBackPressed() {
        String workoutNames = "";
        String workSecs = "";
        String restSecs = "";
        int totalTime = 0;

        for (int i = 0; i < mReps; i++) {
            workoutNames += mWorkoutDetails.get(i).getWorkoutName() + "=";
            workSecs += mWorkoutDetails.get(i).getWorkSecs() + "=";
            restSecs += mWorkoutDetails.get(i).getRestSecs() + "=";
            totalTime += (mWorkoutDetails.get(i).getWorkSecs() + mWorkoutDetails.get(i).getRestSecs());
        }
        workoutNames = workoutNames.substring(0, workoutNames.length()-1);
        workSecs = workSecs.substring(0, workSecs.length()-1);
        restSecs = restSecs.substring(0, restSecs.length()-1);

        Intent returningIntent = new Intent();

        returningIntent.putExtra(HiitSettingActivity.REPS_RESULT_EXTRA_KEY, mReps);
        returningIntent.putExtra(HiitSettingActivity.WORKOUT_NAMES_RESULT_EXTRA_KEY, workoutNames);
        returningIntent.putExtra(HiitSettingActivity.WORK_TIME_RESULT_EXTRA_KEY, workSecs);
        returningIntent.putExtra(HiitSettingActivity.REST_TIME_RESULT_EXTRA_KEY, restSecs);
        returningIntent.putExtra(HiitSettingActivity.TOTAL_WORKOUT_TIME_RESULT_EXTRA_KEY, totalTime);

        setResult(1991, returningIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
