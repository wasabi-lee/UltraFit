package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.HiitSettingActivity;

public class ExternalStoragePermissionDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.user_permission_external_storage_dialog_title))
                .setMessage(getString(R.string.user_permission_external_storage_dialog_message))
                .setPositiveButton(R.string.user_permission_external_storage_dialog_positive_btn_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            ((HiitSettingActivity) getActivity()).askStoragePermission();
                        })
                .setNegativeButton(R.string.user_permission_external_storage_dialog_negative_btn_text,
                        (dialog, which) -> dialog.dismiss())
                .create();
    }
}
