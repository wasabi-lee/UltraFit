package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wasabi on 8/13/2016.
 */
public class RateDialogHelper {

    private static RateDialogHelper instance;
    private Context context;
    private static final int COMPLETED_WORKOUTS_UNTIL_PROMPT = 5;

    private RateDialogHelper() {
    }

    public static RateDialogHelper getInstance() {
        if (instance == null) {
            instance = new RateDialogHelper();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void checkIfShouldLaunchDialog() {
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE);
        if (prefs.getBoolean(MainActivity.sharedPrefDontShowNumKey, false)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        int launch_count = prefs.getInt(MainActivity.sharedPrefCompletedWorkoutsNumKey, 0) + 1;
        editor.putInt(MainActivity.sharedPrefCompletedWorkoutsNumKey, launch_count);
        editor.apply();

        if (launch_count % COMPLETED_WORKOUTS_UNTIL_PROMPT == 0) {
            launchAskingDialog();
        }


    }

    public void launchAskingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("UltraFit. Timer");
        builder.setMessage("You're on the right track! Do you like this app?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchRateAppDialog();
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("REMIND ME LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                context = null;
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchFeedbackDialog();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void launchRateAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rate UltraFit. Timer");
        builder.setMessage("Your support keeps us going! Please take a second to rate it.");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                writeDontShowAgain();
                dialogInterface.dismiss();
                context = null;
            }
        });
        builder.setNeutralButton("REMIND ME LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                context = null;
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                writeDontShowAgain();
                dialogInterface.dismiss();
                context = null;
            }
        });
        builder.create().show();
    }

    public void launchFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_dialog_layout, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.feedback_edittext);
        builder.setView(view);
        builder.setTitle("Tell us what you think");
        builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Feedback newFeedback = getFeedbackObject(editText.getText().toString());
                writeToFirebase(newFeedback);
                writeDontShowAgain();
                Toast.makeText(context, "Thank you for your support!", Toast.LENGTH_SHORT).show();
                context = null;
            }
        });
        builder.setNeutralButton("REMIND ME LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                context = null;
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                writeDontShowAgain();
                context = null;
            }
        });
        builder.create().show();
    }

    private Feedback getFeedbackObject(String userInput) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd, hh:mm:ss");
        String content = "[" + getDeviceModelNumber() + "/" + getUltraFitVersion() + "/" +getDeviceOSVersion() +"] \n" + userInput;
        String date = simpleDateFormat.format(new Date());
        return new Feedback(date, content);
    }

    private void writeToFirebase(Feedback feedback) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (feedback == null) return;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("feedback").child(simpleDateFormat.format(new Date()));
            ref.setValue(feedback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDontShowAgain() {
        SharedPreferences pref = context.getSharedPreferences(MainActivity.sharedPrefOpenKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(MainActivity.sharedPrefDontShowNumKey, true);
        editor.apply();
    }

    private static String getDeviceModelNumber() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String getUltraFitVersion() {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo != null ? pInfo.versionName : "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getDeviceOSVersion() {
        String versionCode = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        if (versionCode != null) {
            return versionCode + " / " + sdkVersion;
        } else {
            return "" + sdkVersion;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
