package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.Manifest;
import android.content.Context;
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
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.BuyPresetPremiumDialog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.ExternalStoragePermissionDialog;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments.PresetSaveDialogFragment;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.hiit.HiitTimerSet;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.music.SongSingleton;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.ThemeUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.TimerUtils;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
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
    private static final String BUY_PRESET_PREMIUM_DIALOG_TAG = "buy_preset_premium_dialog";
    private static final String EXTERNAL_STROAGE_PERMISSION_DIALOG_TAG = "external_storage_permission_dialog";

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

    private boolean isAdvancedSettingSet = false;
    private String customWorkoutNames, customWorkSecs, customRestSecs;
    private int customReps;

    private Realm timerRealm;
    private boolean isPremium;

    private HiitTimerSet currentPrset = null;

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
        Toolbar hiitTimerSetToolbar = findViewById(R.id.hiit_timer_toolbar);
        hiitTimerSetToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(hiitTimerSetToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.hiit_setting_toolbar));
    }


    private void initPreferences() {
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

            if (timerSet == null) {
                makeToast("Unexpected error occurred. Pleast try again!");
                return;
            }

            if (!checkInput(timerSet)) {
                throw new NumberFormatException("Number format error");
            }
            HiitSingleton.getInstance().addTimer(getFinalizedSet());
            Intent intent = new Intent(HiitSettingActivity.this, HiitTimerActivity.class);
            startActivityForResult(intent, 2);

        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            makeToast("Please type valid input");
        }
    }


    private HiitTimerSet getFinalizedSet() {
        if (trimAllEditText()) {
            int warmupTime = TimerUtils.stringTimeToSeconds(editWarmup.getText().toString());
            int defaultWorkTime = TimerUtils.stringTimeToSeconds(editWork.getText().toString());
            int defaultRestTime = TimerUtils.stringTimeToSeconds(editRest.getText().toString());
            int defaultReps = Integer.parseInt(editReps.getText().toString());
            int cooldownTime = TimerUtils.stringTimeToSeconds(editCooldown.getText().toString());

            return RoundHelper.getFinalizedHiitTimerSet(
                    warmupTime, defaultWorkTime, defaultRestTime, defaultReps, cooldownTime,
                    customWorkSecs, customRestSecs, customWorkoutNames, customReps,
                    isAdvancedSettingSet);
        }
        return null;
    }


    private String calculateTotalTime() {
        try {
            return TimerUtils.convertRawSecIntoString(getFinalizedSet().getTotal());
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            makeToast("Unexpected error occurred. Please try again later!");
            return "00:00";
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hitt_timer_setting_warmup_left_arrow:
                editWarmup.requestFocus();
                controlArrowAction(editWarmup, -1);
                break;
            case R.id.hitt_timer_setting_warmup_right_arrow:
                editWarmup.requestFocus();
                controlArrowAction(editWarmup, 1);
                break;
            case R.id.hitt_timer_setting_work_left_arrow:
                editWork.requestFocus();
                controlArrowAction(editWork, -1);
                break;
            case R.id.hitt_timer_setting_work_right_arrow:
                editWork.requestFocus();
                controlArrowAction(editWork, 1);
                break;
            case R.id.hitt_timer_setting_rest_left_arrow:
                editRest.requestFocus();
                controlArrowAction(editRest, -1);
                break;
            case R.id.hitt_timer_setting_rest_right_arrow:
                editRest.requestFocus();
                controlArrowAction(editRest, 1);
                break;
            case R.id.hitt_timer_setting_reps_left_arrow:
                editReps.requestFocus();
                controlArrowAction(editReps, -1);
                break;
            case R.id.hitt_timer_setting_reps_right_arrow:
                editReps.requestFocus();
                controlArrowAction(editReps, 1);
                break;
            case R.id.hitt_timer_setting_cooldown_left_arrow:
                editCooldown.requestFocus();
                controlArrowAction(editCooldown, -1);
                break;
            case R.id.hitt_timer_setting_cooldown_right_arrow:
                editCooldown.requestFocus();
                controlArrowAction(editCooldown, 1);
                break;
            case R.id.hiit_timer_setting_advanced_edit_image_view:
                startAdvancedSettingActivity();
                break;
            case R.id.hiit_timer_setting_music_card:
                startMusicSelectionActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            EditText currentEditText = (EditText) v;
            if (currentEditText.getId() == R.id.hitt_timer_setting_reps_edit)
                checkRepCount(currentEditText);
            else
                trimEditText(currentEditText);
            totalTextView.setText(calculateTotalTime());
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Please enter valid numbers");
            ((EditText) v).setText(getResources().getString(R.string.advanced_default_seconds));
        }
    }


    private void checkRepCount(EditText editText) {
        String currentReps = editText.getText().toString();
        if (Integer.parseInt(currentReps) < 1) {
            editText.setText("1");
        }
    }


    private void trimEditText(EditText editText) {
        String userInput = editText.getText().toString();
        editText.setText(TimerUtils.convertUserInputToValidString(userInput));
    }


    private void startMusicSelectionActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            launchPermissionRequiredDialog();
        } else {
            Intent intent = new Intent(HiitSettingActivity.this, CurrentMusicListActivity.class);
            startActivity(intent);
        }
    }


    private void startAdvancedSettingActivity() {
        Intent advanceIntent = new Intent(HiitSettingActivity.this, AdvancedSettingActivity.class);
        HiitTimerSet set = getFinalizedSet();

        if (set == null || !checkInput(set)) return;

        advanceIntent.putExtra(AdvancedSettingActivity.WORKOUT_NAME_DETAILS_KEY, set.getWorkoutNames());
        advanceIntent.putExtra(AdvancedSettingActivity.WORK_SECS_DETAILS_KEY, set.getWorkSeconds());
        advanceIntent.putExtra(AdvancedSettingActivity.REST_SECS_DETAILS_KEY, set.getRestSeconds());
        startActivityForResult(advanceIntent, AdvancedSettingActivity.REQUEST_CODE_FROM_SETTING_ACTIVITY);
    }


    private void launchPermissionRequiredDialog() {
        ExternalStoragePermissionDialog dialog = new ExternalStoragePermissionDialog();
        dialog.show(getSupportFragmentManager(), EXTERNAL_STROAGE_PERMISSION_DIALOG_TAG);
    }


    public void askStoragePermission() {
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


    public void startPresetSaveFlow() {
        currentPrset = getFinalizedSet();
        if (currentPrset == null || checkInput(currentPrset)) {
            if (isPremium || timerRealm.where(HiitTimerSet.class).findAll().size() < 3) {
                launchTimersetNameSettingDialog();
            } else {
                launchBuyPremiumDialog();
            }
        }
    }


    private void launchTimersetNameSettingDialog() {
        PresetSaveDialogFragment dialog = new PresetSaveDialogFragment();
        dialog.show(getSupportFragmentManager(), PRESET_SAVE_DIALOG_TAG);
    }


    public void savePreset(String timerName) {
        if (currentPrset == null) {
            makeToast("Unexpected error occurred. Please try again later!");
        }
        currentPrset.setTimerName(timerName);
        timerRealm.beginTransaction();
        timerRealm.copyToRealm(currentPrset);
        timerRealm.commitTransaction();
        Snackbar.make(parentLayout, "Saved this preset", Snackbar.LENGTH_LONG).show();
    }


    private boolean checkInput(HiitTimerSet set) {
        if (set.getReps() == 0) {
            makeToast("Please type at least 1 rep!");
            return false;
        } else if (set.getTotal() == 0) {
            makeToast("This set's total time is 0 second!");
            return false;
        }
        return true;
    }


    private void launchBuyPremiumDialog() {
        BuyPresetPremiumDialog dialog = new BuyPresetPremiumDialog();
        dialog.show(getSupportFragmentManager(), BUY_PRESET_PREMIUM_DIALOG_TAG);
    }


    public void startGoPremiumActivity() {
        Intent intent = new Intent(HiitSettingActivity.this, GoPremiumActivity.class);
        startActivity(intent);
    }


    private void controlArrowAction(EditText view, int offset) {
        if (view.getId() == R.id.hitt_timer_setting_reps_edit) {
            int currentReps = Integer.parseInt(view.getText().toString());
            int adjustedRep = currentReps + offset;
            view.setText(String.valueOf(adjustedRep <= 0 ? 1 : adjustedRep));
        } else {
            view.setText(TimerUtils.convertUserInputToValidStringWithOffset(view.getText().toString(), offset));
        }
        totalTextView.setText(calculateTotalTime());
    }


    private boolean trimAllEditText() {
        try {
            editWarmup.setText(TimerUtils.convertUserInputToValidString(editWarmup.getText().toString()));
            editWork.setText(TimerUtils.convertUserInputToValidString(editWork.getText().toString()));
            editRest.setText(TimerUtils.convertUserInputToValidString(editRest.getText().toString()));
            editCooldown.setText(TimerUtils.convertUserInputToValidString(editCooldown.getText().toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            makeToast("Please type valid input");
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1991) {
            isAdvancedSettingSet = true;
            customWorkoutNames = data.getStringExtra(WORKOUT_NAMES_RESULT_EXTRA_KEY);
            customWorkSecs = data.getStringExtra(WORK_TIME_RESULT_EXTRA_KEY);
            customRestSecs = data.getStringExtra(REST_TIME_RESULT_EXTRA_KEY);
            customReps = data.getIntExtra(REPS_RESULT_EXTRA_KEY, Integer.parseInt(editReps.getText().toString()));

            totalTextView.setText(calculateTotalTime());
            editReps.setText(String.valueOf(customReps));
        }
    }


    @Override
    protected void onResume() {
        if (SongSingleton.getInstance().getSelectedSongIds() != null) {
            String currentSongs = "Current songs : " + SongSingleton.getInstance().getSelectedSongIds().length;
            numOfMusicTextView.setText(currentSongs);
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
                startPresetSaveFlow();
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