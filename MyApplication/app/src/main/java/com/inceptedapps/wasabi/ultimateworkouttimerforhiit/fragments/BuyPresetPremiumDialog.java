package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.HiitSettingActivity;

public class BuyPresetPremiumDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.hiit_setting_buy_premium_dialog_title))
                .setMessage(getResources().getString(R.string.hiit_setting_buy_premium_dialog_message))
                .setCancelable(false)
                .setPositiveButton(R.string.hiit_setting_buy_premium_dialog_positive_button,
                        (dialog, which) -> {
                            dialog.dismiss();
                            ((HiitSettingActivity) getActivity()).startGoPremiumActivity();
                        })
                .setNegativeButton(R.string.hiit_setting_buy_premium_dialog_negative_button,
                        (dialog, which) -> dialog.dismiss())
                .create();
    }

}
