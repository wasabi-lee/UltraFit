package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.util;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

public class SoundIdSwitcher {

    public static int beepThemeSwitcher(int beepTheme) {
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

    public static int tickThemeSwitcher(int tickTheme) {
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
