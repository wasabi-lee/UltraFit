package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses;

import android.app.Activity;
import android.content.Intent;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;

/**
 * Created by Wasabi on 5/4/2016.
 */
public class ThemeUtils {

    private static int mTheme;
    public final static int TEAL_BLUE = 1;
    public final static int HOT_PINK = 2;
    public final static int DARK_THEME = 3;
    public final static int LIGHT_GRAY = 4;
    public final static int LIME_GREEN = 5;

    public static void changeToTheme(int theme){
        mTheme = theme;
    }

    public static int getmTheme() {
        return mTheme;
    }

    public static void onActivityCreateSetTheme(Activity activity){
        switch (mTheme){
            case TEAL_BLUE:
                activity.setTheme(R.style.AppTheme);
                break;
            case HOT_PINK:
                activity.setTheme(R.style.HotPinkTheme);
                break;
            case DARK_THEME:
                activity.setTheme(R.style.DarkTheme);
                break;
            case LIGHT_GRAY:
                activity.setTheme(R.style.LightGrayTheme);
                break;
            case LIME_GREEN:
                activity.setTheme(R.style.LimeGreenTheme);
                break;
            default:
                break;
        }
    }

    public static int themeSwitcher(){
        switch (mTheme){
            case TEAL_BLUE:
                return R.style.AppTheme;
            case HOT_PINK:
                return R.style.HotPinkTheme;
            case DARK_THEME:
                return R.style.DarkTheme;
            case LIGHT_GRAY:
                return R.style.LightGrayTheme;
            case LIME_GREEN:
                return R.style.LimeGreenTheme;
            default:
                return R.style.AppTheme;
        }

    }

}
