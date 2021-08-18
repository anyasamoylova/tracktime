package com.sam.tracktime.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sam.tracktime.R;
import com.sam.tracktime.utils.Constant;

import org.jetbrains.annotations.NotNull;

public class RemoveItemDialogFragment extends DialogFragment {
    public static final String REQUEST_KEY = "removedItem";
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to remove the category from home screen only, " +
                "or remove it from statistic too?")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setNegativeButton("Remove only from home screen", (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.REMOVE_MODE, Constant.REMOVE_ONLY_ICON);
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, bundle);
                })
                .setPositiveButton("Remove all ", (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.REMOVE_MODE, Constant.REMOVE_ALL);
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, bundle);
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        return alertDialogBuilder.create();
    }
}
