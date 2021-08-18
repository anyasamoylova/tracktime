package com.sam.tracktime.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.sam.tracktime.R;
import com.sam.tracktime.viewmodel.SprintViewModel;

import org.joda.time.DateTime;

public class CalendarPickerFragment extends DialogFragment {
    private DatePicker datePicker;
    private Button saveButton;
    private Button cancelButton;

    private SprintViewModel sprintViewModel;
    private Long currentDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_calendar_picker);
        sprintViewModel = new ViewModelProvider(getActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(SprintViewModel.class);
        datePicker = dialog.findViewById(R.id.datePicker);
        saveButton = dialog.findViewById(R.id.btnSaveDate);
        cancelButton = dialog.findViewById(R.id.btnCancel);

        sprintViewModel.getSprintDate().observe(getActivity(), aLong -> {
            currentDate = aLong;
            updateData();
        });

        saveButton.setOnClickListener(v -> {
            DateTime dt = new DateTime(datePicker.getYear(),
                    datePicker.getMonth()+1,
                    datePicker.getDayOfMonth(),
                    0,
                    0);
            sprintViewModel.saveDate(dt);
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return dialog;

    }

    private void updateData() {
        if (currentDate != null) {
            DateTime dt = new DateTime(currentDate);
            datePicker.init(dt.getYear(),
                    dt.getMonthOfYear()-1,
                    dt.getDayOfMonth(),
                    (view, year, monthOfYear, dayOfMonth) -> { });
        }
    }
}
