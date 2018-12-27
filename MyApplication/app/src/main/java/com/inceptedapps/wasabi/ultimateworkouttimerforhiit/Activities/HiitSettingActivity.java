package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.GoPremiumActivity;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.HIIT.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.Song;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.MusicSystem.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.IsPremiumSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses.TimerUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class HiitSettingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String WORKOUT_NAMES_RESULT_EXTRA_KEY = "WORKOUT_NAMES_KEY";
    public static final String WORK_TIME_RESULT_EXTRA_KEY = "WORK_TIME_KEY";
    public static final String REST_TIME_RESULT_EXTRA_KEY = "REST_TIME_KEY";
    public static final String TOTAL_WORKOUT_TIME_RESULT_EXTRA_KEY = "TOTAL_WORKOUT_TIME_KEY";
    public static final String REPS_RESULT_EXTRA_KEY = "REPS_KEY";

    private EditText editWarmup, editWork, editRest, editReps, editCooldown;
    private TextView startButtonTextView, totalTextView, numOfMusicTextView;
    private ImageView warmupLeftArrow, warmupRightArrow,
            workLeftArrow, workRightArrow,
            restLeftArrow, restRightArrow,
            repsLeftArrow, repsRightArrow,
            cooldownLeftArrow, cooldownRightArrow, advancedEditImageView;

    private CardView musicCard;
    private RelativeLayout parentLayout;
    private boolean isCanceled = false, isAdvancedSettingSet = false;
    private String workoutNames, workSecs, restSecs;
    private String finalWorkoutNames, finalWorkSecs, finalRestSecs;
    private int totalSecs;
    private int retrievedReps;
    private int theme;

    private Realm timerRealm;
    private boolean isPremium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.changeToTheme(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1")));
        setTheme(ThemeUtils.themeSwitcher());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiit_setting);

        // ======================= Reference and toolbar setting ===========================

        timerRealm = Realm.getDefaultInstance();

        Toolbar hiitTimerSetToolbar = (Toolbar) findViewById(R.id.hiit_timer_toolbar);
        hiitTimerSetToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(hiitTimerSetToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.hiit_setting_toolbar));

        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        theme = Integer.parseInt(defaultPref.getString(getResources().getString(R.string.SHARED_PREF_COLOR_THEME_KEY), "1"));
        isPremium = this.getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE).getBoolean(MainActivity.sharedPrefPremiumKey, false);
        Log.d(getClass().getSimpleName(), isPremium + "");

        editWarmup = (EditText) findViewById(R.id.hitt_timer_setting_warmup_edit);
        editWork = (EditText) findViewById(R.id.hitt_timer_setting_work_edit);
        editRest = (EditText) findViewById(R.id.hitt_timer_setting_rest_edit);
        editReps = (EditText) findViewById(R.id.hitt_timer_setting_reps_edit);
        editCooldown = (EditText) findViewById(R.id.hitt_timer_setting_cooldown_edit);

        startButtonTextView = (TextView) findViewById(R.id.hiit_timer_setting_start_button);
        totalTextView = (TextView) findViewById(R.id.hiit_timer_setting_total_text);

        musicCard = (CardView) findViewById(R.id.hiit_timer_setting_music_card);
        if (musicCard != null) musicCard.setOnClickListener(this);

        numOfMusicTextView = (TextView) findViewById(R.id.hiit_timer_setting_songs);

        warmupLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_warmup_left_arrow);
        warmupRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_warmup_right_arrow);
        workLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_work_left_arrow);
        workRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_work_right_arrow);
        restLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_rest_left_arrow);
        restRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_rest_right_arrow);
        repsLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_reps_left_arrow);
        repsRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_reps_right_arrow);
        cooldownLeftArrow = (ImageView) findViewById(R.id.hitt_timer_setting_cooldown_left_arrow);
        cooldownRightArrow = (ImageView) findViewById(R.id.hitt_timer_setting_cooldown_right_arrow);
        advancedEditImageView = (ImageView) findViewById(R.id.hiit_timer_setting_advanced_edit_image_view);

        if (theme == 3 || theme == 5) {
            if (advancedEditImageView != null) advancedEditImageView.setImageResource(R.drawable.ic_create_white);

            warmupLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);
            workLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);
            restLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);
            repsLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);
            cooldownLeftArrow.setImageResource(R.drawable.ic_keyboard_arrow_left_white);

            warmupRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
            workRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
            restRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
            repsRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
            cooldownRightArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white);
        }

        parentLayout = (RelativeLayout) findViewById(R.id.hiit_timer_setting_parent_relativelayout);


        // ========================== Time trimming =================================

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


        // ====================== Passing data to service ==================================

        startButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trimAllEditText();
                    int warmupTime = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString());
                    int workTime = TimerUtils.stringTimeToSeconds(editWork.getText().toString());
                    int restTime = TimerUtils.stringTimeToSeconds(editRest.getText().toString());
                    int repsTime = Integer.parseInt(editReps.getText().toString());
                    int cooldownTime = TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());
                    int totalTime = TimerUtils.stringTimeToSeconds(calculateTotalTime());

                    if (repsTime == 0) {
                        Toast.makeText(HiitSettingActivity.this, "Please type at least 1 rep!", Toast.LENGTH_SHORT).show();
                    } else if (warmupTime == 0 && workTime == 0 && restTime == 0 && cooldownTime == 0) {
                        Toast.makeText(HiitSettingActivity.this, "This set's total time is 0 second!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isAdvancedSettingSet) {
                            finalWorkoutNames = "";
                            finalWorkSecs = "";
                            finalRestSecs = "";
                            for (int i = 0; i < repsTime; i++) {
                                finalWorkoutNames += "Sprint=";
                                finalWorkSecs += workTime + "=";
                                finalRestSecs += restTime + "=";
                            }
                            finalWorkoutNames = finalWorkoutNames.substring(0, finalWorkoutNames.length() - 1);
                            finalWorkSecs = finalWorkSecs.substring(0, finalWorkSecs.length() - 1);
                            finalRestSecs = finalRestSecs.substring(0, finalRestSecs.length() - 1);
                        } else {
                            finalWorkoutNames = workoutNames;
                            finalWorkSecs = workSecs;
                            finalRestSecs = restSecs;

                            if (repsTime != retrievedReps) {
                                String[] finalDetails = finalizeWorkoutDetails(repsTime, workTime, restTime, finalWorkoutNames, finalWorkSecs, finalRestSecs);
                                finalWorkoutNames = finalDetails[0];
                                finalWorkSecs = finalDetails[1];
                                finalRestSecs = finalDetails[2];
                                totalSecs = Integer.parseInt(finalDetails[3]);
                                finalDetails = null;
                            }

                            totalTime = warmupTime + totalSecs + cooldownTime;
                        }

                        HiitSingleton.getInstance().addTimer(new HiitTimerSet(warmupTime, workTime, restTime, repsTime, cooldownTime, totalTime, "HIIT Timer", finalWorkoutNames, finalWorkSecs, finalRestSecs));
                        Intent intent = new Intent(HiitSettingActivity.this, HiitTimerActivity.class);
                        startActivityForResult(intent, 2);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(HiitSettingActivity.this, "Please type valid numbers!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String calculateTotalTime() {
        int currentReps = Integer.parseInt(editReps.getText().toString());
        int currentWork = TimerUtils.stringTimeToSeconds(editWork.getText().toString());
        int currentRest = TimerUtils.stringTimeToSeconds(editRest.getText().toString());
        int currentWarmup = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString());
        int currentCooldown = TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());
        if (isAdvancedSettingSet) {
            if (currentReps != retrievedReps) {
                String[] currentDetails = finalizeWorkoutDetails(currentReps, currentWork, currentRest, workoutNames, workSecs, restSecs);
                return TimerUtils.convertRawSecIntoString(Integer.parseInt(currentDetails[3]) + currentWarmup + currentCooldown);
            } else {
                return TimerUtils.convertRawSecIntoString(totalSecs + currentWarmup + currentCooldown);
            }
        } else {
            int sec = currentWarmup + ((currentWork + currentReps) * currentReps) + currentReps;
            return TimerUtils.convertRawSecIntoString(sec);
        }
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
                    if (currentReps != retrievedReps) {
                        String[] currentDetails = finalizeWorkoutDetails(currentReps,
                                TimerUtils.stringTimeToSeconds(editWork.getText().toString()),
                                TimerUtils.stringTimeToSeconds(editRest.getText().toString()),
                                workoutNames, workSecs, restSecs);
                        workoutNames = currentDetails[0];
                        workSecs = currentDetails[1];
                        restSecs = currentDetails[2];
                        currentDetails = null;
                    }
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORKOUT_NAME_DETAILS_KEY, workoutNames);
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.WORK_SECS_DETAILS_KEY, workSecs);
                    advancedSettingIntent.putExtra(AdvancedSettingActivity.REST_SECS_DETAILS_KEY, restSecs);
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
        if (repsTime > retrievedReps) {
            for (int i = 0; i < repsTime - retrievedReps; i++) {
                workoutNames += "=Sprint";
                workSecs += ("=" + workTime);
                restSecs += ("=" + restTime);
            }
        }
        if (repsTime < retrievedReps) {
            for (int i = 0; i < retrievedReps - repsTime; i++) {
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
            Toast.makeText(HiitSettingActivity.this, "Please type valid numbers", Toast.LENGTH_SHORT).show();
        }
        if (Integer.parseInt(editReps.getText().toString()) == 0) {
            Toast.makeText(HiitSettingActivity.this, "Please type at least 1 rep!", Toast.LENGTH_SHORT).show();
        } else if (TimerUtils.stringTimeToSeconds(calculateTotalTime()) == 0) {
            Toast.makeText(HiitSettingActivity.this, "This set's total time is 0 second!", Toast.LENGTH_SHORT).show();
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

        final int warmupTime = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString());
        final int workTime = TimerUtils.stringTimeToSeconds(editWork.getText().toString());
        final int restTime = TimerUtils.stringTimeToSeconds(editRest.getText().toString());
        final int repsTime = Integer.parseInt(editReps.getText().toString());
        final int cooldownTime = TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());
        final int totalTime = TimerUtils.stringTimeToSeconds(calculateTotalTime());

        AlertDialog.Builder builder = new AlertDialog.Builder(HiitSettingActivity.this, theme);
        View dialogView = HiitSettingActivity.this
                .getLayoutInflater()
                .inflate(R.layout.name_dialog, parentLayout, false);
        builder.setView(dialogView);

        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.edittext);

        builder.setTitle(getResources().getString(R.string.hiit_setting_preset_save_dialog));
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String timerName = nameEdit.getText().toString();
                if (nameEdit.getText().toString().isEmpty()) {
                    timerName = "HIIT Timer";
                }
                HiitTimerSet newTimerSet = null;
                if (!isAdvancedSettingSet) {
                    workoutNames = "";
                    workSecs = "";
                    restSecs = "";
                    for (int i = 0; i < repsTime; i++) {
                        workoutNames += "Sprint=";
                        workSecs += workTime + "=";
                        restSecs += restTime + "=";
                    }
                    workoutNames = workoutNames.substring(0, workoutNames.length() - 1);
                    workSecs = workSecs.substring(0, workSecs.length() - 1);
                    restSecs = restSecs.substring(0, restSecs.length() - 1);
                    newTimerSet =
                            new HiitTimerSet(warmupTime, workTime, restTime, repsTime, cooldownTime, totalTime,
                                    timerName, workoutNames, workSecs, restSecs);
                } else {
                    if (repsTime != retrievedReps) {
                        String[] finalDetails = finalizeWorkoutDetails(repsTime, workTime, restTime,
                                workoutNames, workSecs, restSecs);
                        newTimerSet =
                                new HiitTimerSet(warmupTime, workTime, restTime, repsTime, cooldownTime,
                                        warmupTime + (Integer.parseInt(finalDetails[3])) + cooldownTime,
                                        timerName, finalDetails[0], finalDetails[1], finalDetails[2]);
                    } else {
                        newTimerSet =
                                new HiitTimerSet(warmupTime, workTime, restTime, repsTime, cooldownTime,
                                        totalTime, timerName, workoutNames, workSecs, restSecs);
                    }
                }


                timerRealm.beginTransaction();
                timerRealm.copyToRealm(newTimerSet);
                timerRealm.commitTransaction();
                Snackbar.make(parentLayout, "Saved this preset", Snackbar.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCanceled = true;
                dialog.dismiss();
            }
        });
        builder.create().show();
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
            workoutNames = data.getStringExtra(WORKOUT_NAMES_RESULT_EXTRA_KEY);
            workSecs = data.getStringExtra(WORK_TIME_RESULT_EXTRA_KEY);
            restSecs = data.getStringExtra(REST_TIME_RESULT_EXTRA_KEY);
            totalSecs = data.getIntExtra(TOTAL_WORKOUT_TIME_RESULT_EXTRA_KEY, 0);
            retrievedReps = data.getIntExtra(REPS_RESULT_EXTRA_KEY, Integer.parseInt(editReps.getText().toString()));

            int rawTotal = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString()) + totalSecs + TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());
            totalTextView.setText(TimerUtils.convertRawSecIntoString(rawTotal));
            editReps.setText(String.valueOf(retrievedReps));

            Log.d("RECEIVED_STRING", "Workout Names: " + workoutNames);
            Log.d("RECEIVED_STRING", "Work seconds: " + workSecs);
            Log.d("RECEIVED_STRING", "Rest Names: " + restSecs);
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
}

