package com.sam.tracktime.ui.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sam.tracktime.R;

public class EditTextFragment extends DialogFragment {
    //TODO use constant?
    public static final String TAG_NAME = "tagName";
    public static final String REQUEST_KEY = "addedTag";
    EditText etTag;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(
                R.layout.fragment_edit_text,
                (ViewGroup) getParentFragment().getView(),
                false
        );
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        etTag = v.findViewById(R.id.etTag);
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Bundle bundle = new Bundle();
                    bundle.putString(TAG_NAME, etTag.getText().toString());
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, bundle);
                case DialogInterface.BUTTON_NEGATIVE:
                    dismiss();
            }
        };

        return builder
                .setView(v)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", listener)
                .create();

    }
}
