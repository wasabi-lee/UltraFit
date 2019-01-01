package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.HiitTimerActivity;

public class ExitTimerDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.exit_timer_dialog_title))
                .setMessage(getString(R.string.exit_timer_dialog_message))
                .setPositiveButton(R.string.exit_timer_dialog_positive_btn_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            ((HiitTimerActivity) getActivity()).finishActivity();
                        })
                .setNegativeButton(R.string.exit_timer_dialog_negative_btn_text,
                        (dialog, which) -> dialog.dismiss())
                .create();
    }
}
