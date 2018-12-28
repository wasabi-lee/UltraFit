package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom.SoundListAdapter;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

public class TickSettingActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SoundListAdapter.OnSoundSelectedListener {

    int lastClick;
    boolean isTickPreference;

    public static final String IS_TICK_PREFERENCE_KEY = "is_tick_preference";
    public static final String SELECTED_VALUE = "selected_value";
    public static final String ORIGINAL_VALUE = "original_value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tick_setting);

        isTickPreference = getIntent().getBooleanExtra(IS_TICK_PREFERENCE_KEY, false);
        int originalValue = getIntent().getIntExtra(ORIGINAL_VALUE, 0);

        String[] nameArray = null;
        if (isTickPreference) {
            nameArray = new String[]{"Basic Tick", "Camera Click", "Hint", "Pistol",
                    "Shotgun Pump", "Temple Block", "Xylophone Tick"};
        } else {
            nameArray = new String[]{"Basic Beep", "Whistle", "Machine Gun",
                    "Rifle Shot", "Chime Bell", "Click", "Glass Click",
                    "Home Run", "Xylophone Beep"};
        }

        ListView listView = (ListView) findViewById(R.id.tick_list_view);
        SoundListAdapter adapter = new SoundListAdapter(this, nameArray, originalValue-1, this);
        listView.setAdapter(adapter);


    }

    @Override
    public void onSoundSelected(int position) {
        try {
            lastClick = position;
            int resource = 0;
            if (isTickPreference) {
                resource = tickThemeSwitcher(position + 1);
            } else {
                resource = beepThemeSwitcher(position + 1);
            }
            playSound(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void playSound(int resource) {
        MediaPlayer mp = MediaPlayer.create(this, resource);
        mp.setOnCompletionListener(this);
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        mp = null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_VALUE, lastClick + 1);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public int beepThemeSwitcher(int beepTheme) {
        switch (beepTheme) {
            case 1:
                return R.raw.beep_basic_beep;
            case 2:
                return R.raw.beep_whistle;
            case 3:
                return R.raw.beep_machine_gun;
            case 4:
                return R.raw.beep_rifle_shot;
            case 5:
                return R.raw.beep_chime_bell;
            case 6:
                return R.raw.beep_click;
            case 7:
                return R.raw.beep_glass_click;
            case 8:
                return R.raw.beep_home_run;
            case 9:
                return R.raw.beep_xylophone_beep;
            default:
                return R.raw.beep_basic_beep;
        }
    }

    public int tickThemeSwitcher(int tickTheme) {
        switch (tickTheme) {
            case 1:
                return R.raw.tick_basic_tick;
            case 2:
                return R.raw.tick_camera_click;
            case 3:
                return R.raw.tick_hint;
            case 4:
                return R.raw.tick_pistol;
            case 5:
                return R.raw.tick_shotgun_pump;
            case 6:
                return R.raw.tick_temple_block_hit;
            case 7:
                return R.raw.tick_xylophone_tick;
            default:
                return R.raw.beep_basic_beep;
        }
    }
}
