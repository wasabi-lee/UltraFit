package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.GoPremiumActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.PresetSaveDialogFragment;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.TimerUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.DateHelper;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util.RoundHelper;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class HiitSettingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String WORKOUT_NAMES_RESULT_EXTRA_KEY = "WORKOUT_NAMES_KEY";
    public static final String WORK_TIME_RESULT_EXTRA_KEY = "WORK_TIME_KEY";
    public static final String REST_TIME_RESULT_EXTRA_KEY = "REST_TIME_KEY";
    public static final String TOTAL_WORKOUT_TIME_RESULT_EXTRA_KEY = "TOTAL_WORKOUT_TIME_KEY";
    public static final String REPS_RESULT_EXTRA_KEY = "REPS_KEY";

    private static final String PRESET_SAVE_DIALOG_TAG = "preset_dialog";

    @BindView(R.id.hitt_timer_setting_warmup_edit)
    EditText editWarmup;
    @BindView(R.id.hitt_timer_setting_work_edit)
    EditText editWork;
    @BindView(R.id.hitt_timer_setting_rest_edit)
    EditText editRest;
    @BindView(R.id.hitt_timer_setting_reps_edit)
    EditText editReps;
    @BindView(R.id.hitt_timer_setting_cooldown_edit)
    EditText editCooldown;

    @BindView(R.id.hitt_timer_setting_warmup_left_arrow)
    ImageView warmupLeftArrow;
    @BindView(R.id.hitt_timer_setting_warmup_right_arrow)
    ImageView warmupRightArrow;
    @BindView(R.id.hitt_timer_setting_work_left_arrow)
    ImageView workLeftArrow;
    @BindView(R.id.hitt_timer_setting_work_right_arrow)
    ImageView workRightArrow;
    @BindView(R.id.hitt_timer_setting_rest_left_arrow)
    ImageView restLeftArrow;
    @BindView(R.id.hitt_timer_setting_rest_right_arrow)
    ImageView restRightArrow;
    @BindView(R.id.hitt_timer_setting_reps_left_arrow)
    ImageView repsLeftArrow;
    @BindView(R.id.hitt_timer_setting_reps_right_arrow)
    ImageView repsRightArrow;
    @BindView(R.id.hitt_timer_setting_cooldown_left_arrow)
    ImageView cooldownLeftArrow;
    @BindView(R.id.hitt_timer_setting_cooldown_right_arrow)
    ImageView cooldownRightArrow;
    @BindView(R.id.hiit_timer_setting_advanced_edit_image_view)
    ImageView advancedEditImageView;

    @BindView(R.id.hiit_timer_setting_start_button)
    TextView startButtonTextView;
    @BindView(R.id.hiit_timer_setting_total_text)
    TextView totalTextView;
    @BindView(R.id.hiit_timer_setting_songs)
    TextView numOfMusicTextView;

    @BindView(R.id.hiit_timer_setting_music_card)
    CardView musicCard;

    @BindView(R.id.hiit_timer_setting_parent_relativelayout)
    RelativeLayout parentLayout;

    private boolean isCanceled = false, isAdvancedSettingSet = false;
    private String customWorkoutNames, customWorkSecs, customRestSecs;
    private String finalWorkoutNames, finalWorkSecs, finalRestSecs;
    private int totalSecs;
    private int customReps;
    private int theme;

    private Realm timerRealm;
    private boolean isPremium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_setting);
        ButterKnife.bind(this);

        timerRealm = Realm.getDefaultInstance();

        initToolbar();

        initPreferences();

        initListeners();

    }


    private void initToolbar() {
        Toolbar hiitTimerSetToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerSetToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(hiitTimerSetToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.hiit_setting_toolbar));
    }


    private void initPreferences() {
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        theme = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));
        isPremium = this.getSharedPreferences(
                getString(R.string.shared_pref_open_key), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.shared_pref_premium_key), false);
    }


    private void initListeners() {
        musicCard.setOnClickListener(this);

        editWarmup.setOnFocusChangeListener(this);
        editWork.setOnFocusChangeListener(this);
        editRest.setOnFocusChangeListener(this);
        editReps.setOnFocusChangeListener(this);
        editCooldown.setOnFocusChangeListener(this);

        warmupLeftArrow.setOnClickListener(this);
        warmupRightArrow.setOnClickListener(this);
        workLeftArrow.setOnClickListener(this);
        workRightArrow.setOnClickListener(this);
        restLeftArrow.setOnClickListener(this);
        restRightArrow.setOnClickListener(this);
        repsLeftArrow.setOnClickListener(this);
        repsRightArrow.setOnClickListener(this);
        cooldownLeftArrow.setOnClickListener(this);
        cooldownRightArrow.setOnClickListener(this);
        advancedEditImageView.setOnClickListener(this);

        startButtonTextView.setOnClickListener(v -> startTimer());
    }


    private void startTimer() {
        try {
            HiitTimerSet timerSet = getFinalizedSet();

            if (timerSet.getReps() == 0) {
                makeToast("The workout should have at least 1 rep!");
            } else if (timerSet.getTotal() == 0) {
                makeToast("This set's total time is 0 second!");
            } else {
                HiitSingleton.getInstance().addTimer(getFinalizedSet());
                Intent intent = new Intent(HiitSettingActivity.this, HiitTimerActivity.class);
                startActivityForResult(intent, 2);
            }

        } catch (NullPointerException | NumberFormatException e) {
            makeToast("Please type valid input");
        }
    }


    private HiitTimerSet getFinalizedSet() {
        return getFinalizedSet(RoundHelper.DEFAULT_TIMER_NAME);
    }

    private HiitTimerSet getFinalizedSet(String timerName) {
        trimAllEditText();
        int warmupTime = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString());
        int defaultWorkTime = TimerUtils.stringTimeToSeconds(editWork.getText().toString());
        int defaultRestTime = TimerUtils.stringTimeToSeconds(editRest.getText().toString());
        int defaultReps = Integer.parseInt(editReps.getText().toString());
        int cooldownTime = TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());

        return RoundHelper.getFinalizedHiitTimerSet(
                warmupTime, defaultWorkTime, defaultRestTime, defaultReps, cooldownTime,
                customWorkSecs, customRestSecs, customWorkoutNames, customReps,
                isAdvancedSettingSet, timerName);
    }


    private String calculateTotalTime() {
        return TimerUtils.convertRawSecIntoString(getFinalizedSet().getTotal());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hitt_timer_setting_warmup_left_arrow:
                editWarmup.requestFocus();
                controlArrowAction(editWarmup, 0);
                break;
            case R.id.hitt_timer_setting_warmup_right_arrow:
                editWarmup.requestFocus();
                controlArrowAction(editWarmup, 1);
                break;
            case R.id.hitt_timer_setting_work_left_arrow:
                editWork.requestFocus();
                controlArrowAction(editWork, 0);
                break;
            case R.id.hitt_timer_setting_work_right_arrow:
                editWork.requestFocus();
                controlArrowAction(editWork, 1);
                break;
            case R.id.hitt_timer_setting_rest_left_arrow:
                editRest.requestFocus();
                controlArrowAction(editRest, 0);
                break;
            case R.id.hitt_timer_setting_rest_right_arrow:
                editRest.requestFocus();
                controlArrowAction(editRest, 1);
                break;
            case R.id.hitt_timer_setting_reps_left_arrow:
                editReps.requestFocus();
                controlArrowAction(editReps, 3);
                break;
            case R.id.hitt_timer_setting_reps_right_arrow:
                editReps.requestFocus();
                controlArrowAction(editReps, 4);
                break;
            case R.id.hitt_timer_setting_cooldown_left_arrow:
                editCooldown.requestFocus();
                controlArrowAction(editCooldown, 0);
                break;
            case R.id.hitt_timer_setting_cooldown_right_arrow:
                editCooldown.requestFocus();
                controlArrowAction(editCooldown, 1);
                break;
            case R.id.hiit_timer_setting_advanced_edit_image_view:
                Intent advancedSettingIntent = new Intent(HiitSettingActivity.this, AdvancedSettingActivity.class);
                int currentReps = Integer.parseInt(editReps.getText().toString());
                if (currentReps < 1) {
                    editReps.setText("1");
                }
                if (isAdvancedSettingSet) {
                    if (currentReps != customReps) {
                        String[] currentDetails = finalizeWorkoutDetails(currentReps,
                                TimerUtils.stringTimeToSeconds(editWork.getText().toString()),
                                TimerUtils.stringTimeToSeconds(editRest.getText().toString()),
                                customWorkoutNames, customWorkSecs, customRestSecs);
                        customWorkoutNames = currentDetails[0];
                        customWorkSecs = currentDetails[1];
                        customRestSecs = currentDetails[2];
                        currentDetails = null;
                    }
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORKOUT_NAME_DETAILS_KEY, customWorkoutNames);
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORK_SECS_DETAILS_KEY, customWorkSecs);
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.REST_SECS_DETAILS_KEY, customRestSecs);
                } else {
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORKOUT_REPS_EXTRA_KEY, Integer.parseInt(editReps.getText().toString()));
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORKOUT_WORK_TIME_EXTRA_KEY, TimerUtils.stringTimeToSeconds(editWork.getText().toString()));
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORKOUT_REST_TIME_EXTRA_KEY, TimerUtils.stringTimeToSeconds(editRest.getText().toString()));
                }
                startActivityForResult(advancedSettingIntent, AdvancedSettingActivity.REQUEST_CODE_FROM_SETTING_ACTIVITY);
                break;
            case R.id.hiit_timer_setting_music_card:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    launchPermissionRequiredDialog();
                } else {
                    Intent intent = new Intent(HiitSettingActivity.this, CurrentMusicListActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            EditText currentEditText = (EditText) v;
            if (currentEditText.getId() == R.id.hitt_timer_setting_reps_edit) {
                String currentReps = currentEditText.getText().toString();
                if (Integer.parseInt(currentReps) < 1) {
                    currentEditText.setText("1");
                }
            } else {
                String userInput = currentEditText.getText().toString();
                currentEditText.setText(TimerUtils.convertRawSecIntoString(TimerUtils.stringTimeToSeconds(userInput)));
            }
            totalTextView.setText(calculateTotalTime());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(HiitSettingActivity.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            ((EditText) v).setText(getResources().getString(R.string.advanced_default_seconds));
        }
    }

    private void launchPermissionRequiredDialog() {
        AlertDialog.Builder permissionDescription = new AlertDialog.Builder(this);
        permissionDescription.setTitle("User permission");
        permissionDescription.setMessage("UltraFit Interval Timer needs to access your media files to play music during the workout. " +
                "This permission will allow this app to do that. " +
                "If you click No, we won't access your music files but you won't be able to enjoy music playback. \n\n" +
                "(If you have denied previously and checked 'do not ask again', you should clear the app data and reinstall the app. " +
                "Please be aware that this will clear your workout data and stored presets.)");
        permissionDescription.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askStoragePermission();
                dialog.dismiss();
            }
        });
        permissionDescription.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        permissionDescription.create().show();
    }

    private void askStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1001);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(HiitSettingActivity.this, CurrentMusicListActivity.class);
                startActivity(intent);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String[] finalizeWorkoutDetails(int repsTime, int workTime, int restTime, String workoutNames, String workSecs, String restSecs) {
        if (repsTime > customReps) {
            for (int i = 0; i < repsTime - customReps; i++) {
                workoutNames += "=Sprint";
                workSecs += ("=" + workTime);
                restSecs += ("=" + restTime);
            }
        }
        if (repsTime < customReps) {
            for (int i = 0; i < customReps - repsTime; i++) {
                workoutNames = workoutNames.substring(0, workoutNames.lastIndexOf("="));
                workSecs = workSecs.substring(0, workSecs.lastIndexOf("="));
                restSecs = restSecs.substring(0, restSecs.lastIndexOf("="));
            }
        }
        String[] splitedWorkSecs = workSecs.split("=");
        String[] splitedRestSecs = restSecs.split("=");
        int totalWorkRestSecs = 0;
        for (int i = 0; i < splitedWorkSecs.length; i++) {
            totalWorkRestSecs += (Integer.parseInt(splitedWorkSecs[i]) + Integer.parseInt(splitedRestSecs[i]));
        }
        return new String[]{workoutNames, workSecs, restSecs, String.valueOf(totalWorkRestSecs)};
    }

    public void saveThisPreset() {
        try {
            trimAllEditText();
        } catch (NumberFormatException e) {
            makeToast("Please type valid input");
        }

        if (Integer.parseInt(editReps.getText().toString()) == 0) {
            makeToast("Please type at least 1 rep!");
        } else if (TimerUtils.stringTimeToSeconds(calculateTotalTime()) == 0) {
            makeToast("This set's total time is 0 second!");
        } else {
            if (isPremium || timerRealm.where(HiitTimerSet.class).findAll().size() < 3) {
                if (theme == 3 || theme == 5) {
                    launchTimersetNameSettingDialog(16974377);
                } else {
                    launchTimersetNameSettingDialog(16974397);
                }
            } else {
                if (theme == 3 || theme == 5) {
                    launchBuyPremiumDialog(16974377);
                } else {
                    launchBuyPremiumDialog(16974397);
                }
            }
        }
    }

    private void launchBuyPremiumDialog(int theme) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HiitSettingActivity.this, theme);
        builder.setTitle(getResources().getString(R.string.hiit_setting_buy_premium_dialog_title));
        builder.setMessage(getResources().getString(R.string.hiit_setting_buy_premium_dialog_message));
        builder.setPositiveButton(getResources().getString(R.string.hiit_setting_buy_premium_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HiitSettingActivity.this, GoPremiumActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.hiit_setting_buy_premium_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void launchTimersetNameSettingDialog(int theme) {
        PresetSaveDialogFragment dialog = new PresetSaveDialogFragment();
        dialog.show(getSupportFragmentManager(), PRESET_SAVE_DIALOG_TAG);
    }


    public void savePreset(String timerName) {
        timerRealm.beginTransaction();
        timerRealm.copyToRealm(getFinalizedSet(timerName));
        timerRealm.commitTransaction();
        Snackbar.make(parentLayout, "Saved this preset", Snackbar.LENGTH_LONG).show();
    }



    private void controlArrowAction(View view, int flag) {
        EditText currentEditText = (EditText) view;
        String currentTime = currentEditText.getText().toString();
        String[] currentMinSec = currentTime.split(":");
        if (currentMinSec.length == 2) {
            if (flag == 1) { // if the user clicked right arrow, add 1 more second
                if (currentMinSec[1].equals("59")) {
                    currentMinSec[1] = "00";
                    currentMinSec[0] = String.valueOf(Integer.parseInt(currentMinSec[0]) + 1);
                } else {
                    currentMinSec[1] = String.valueOf(Integer.parseInt(currentMinSec[1]) + 1);
                }
                String trimmedMinSec = trimTimeString(currentMinSec);
                ((EditText) view).setText(trimmedMinSec);
            } else if (flag == 0) { // If the user clicked left arrow, subtract 1 second from the original time
                if (!currentTime.equals("00:00")) {
                    if (currentMinSec[1].equals("00")) {
                        currentMinSec[1] = "59";
                        currentMinSec[0] = String.valueOf(Integer.parseInt(currentMinSec[0]) - 1);
                    } else {
                        currentMinSec[1] = String.valueOf(Integer.parseInt(currentMinSec[1]) - 1);
                    }
                    String trimmedMinSec = trimTimeString(currentMinSec);
                    ((EditText) view).setText(trimmedMinSec);
                }
            }
            currentMinSec = null;
        } else {
            if (flag == 3) { // If the user clicked the left arrow of the reps EditText, subtract 1 second from the original reps
                if (Integer.parseInt(currentTime) > 1) {
                    String currentReps = String.valueOf(Integer.parseInt(currentTime) - 1);
                    currentEditText.setText(currentReps);
                } else {
                    currentEditText.setText(getResources().getString(R.string.hiit_setting_default_reps));
                }
            } else if (flag == 4) { // If the user clicked the right arrow of the reps EditText, add 1 more rep
                String currentReps = String.valueOf(Integer.parseInt(currentTime) + 1);
                currentEditText.setText(currentReps);
            } else {
                currentEditText.setText(getResources().getString(R.string.hiit_setting_default_second));
            }
            trimAllEditText();
        }
        totalTextView.setText(calculateTotalTime());
        currentEditText = null;
    }

    private void trimAllEditText() {
        editWarmup.setText(trimTimeString(editWarmup.getText().toString().split(":")));
        editWork.setText(trimTimeString(editWork.getText().toString().split(":")));
        editRest.setText(trimTimeString(editRest.getText().toString().split(":")));
        editCooldown.setText(trimTimeString(editCooldown.getText().toString().split(":")));
    }

    private String trimTimeString(String[] minAndSec) {
        int min = 0;
        int sec = 0;
        if (minAndSec.length == 2) {
            if (minAndSec[0].isEmpty()) {
                minAndSec[0] = "00";
            }
            min = Integer.parseInt(minAndSec[0]);
            sec = Integer.parseInt(minAndSec[1]);
        } else if (minAndSec.length == 1) {
            if (!(minAndSec[0].isEmpty())) {
                sec = Integer.parseInt(minAndSec[0]);
            }
        }
        minAndSec = null;
        String minString = "00";
        String secString = "00";


        if (sec >= 60) {
            int oldMin = min;
            min = (int) (TimeUnit.SECONDS.toMinutes(sec));
            sec = (int) (sec - TimeUnit.MINUTES.toSeconds(min));
            min = min + oldMin;
        }

        if (min < 10) {
            minString = "0" + min;
        } else {
            minString = String.valueOf(min);
        }
        if (sec < 10) {
            secString = "0" + sec;
        } else {
            secString = String.valueOf(sec);
        }

        return minString + ":" + secString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1991) {
            isAdvancedSettingSet = true;
            customWorkoutNames = data.getStringExtra(WORKOUT_NAMES_RESULT_EXTRA_KEY);
            customWorkSecs = data.getStringExtra(WORK_TIME_RESULT_EXTRA_KEY);
            customRestSecs = data.getStringExtra(REST_TIME_RESULT_EXTRA_KEY);
            totalSecs = data.getIntExtra(TOTAL_WORKOUT_TIME_RESULT_EXTRA_KEY, 0);
            customReps = data.getIntExtra(REPS_RESULT_EXTRA_KEY, Integer.parseInt(editReps.getText().toString()));

            int rawTotal = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString()) + totalSecs + TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());
            totalTextView.setText(TimerUtils.convertRawSecIntoString(rawTotal));
            editReps.setText(String.valueOf(customReps));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (SongSingleton.getInstance().getSelectedSongIds() != null) {
            String currentSongs = "Current songs : " + SongSingleton.getInstance().getSelectedSongIds().length;
            numOfMusicTextView.setText(currentSongs);
            currentSongs = null;
        } else {
            numOfMusicTextView.setText(getResources().getString(R.string.hiit_setting_num_of_music));
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timer_setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.hiit_timer_setting_save_preset:
                saveThisPreset();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerRealm.close();
    }


    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}

