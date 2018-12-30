package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.R;
import com.inceptedapps.wasabi.ultimateworkouttimerforhiit.activities.HiitSettingActivity;

public class PresetSaveDialogFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder=  new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater()
                .inflate(R.layout.name_dialog, null);
        builder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.edittext);

        return builder.setCancelable(false)
                .setTitle(R.string.hiit_setting_preset_save_dialog)
                .setPositiveButton(R.string.hiit_setting_preset_save_positive_btn_text,
                        (dialog, which) -> {
                            String timerName = editText.getText().toString();
                            dialog.dismiss();
                            ((HiitSettingActivity) getActivity()).savePreset(timerName);
                        })
                .setNegativeButton(R.string.hiit_setting_preset_save_negative_btn_text,
                        (dialog, which) -> dialog.dismiss())
                .create();
    }
}
