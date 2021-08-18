package com.sam.tracktime.ui.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sam.tracktime.R;
import com.sam.tracktime.model.Tag;
import com.sam.tracktime.utils.Constant;
import com.sam.tracktime.viewmodel.HomeViewModel;
import com.sam.tracktime.viewmodel.SprintViewModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private TimePicker timePicker;
    private Button saveButton;
    private Button cancelButton;
    private Spinner spinner;

    private Integer itemId;
    private List<Tag> tags;
    private Tag chosenTag;
    private Long currentTime;
    private Boolean isEditSprintMode;
    private boolean isStartTimerMode;
    private ArrayAdapter<Tag> adapter;

    private HomeViewModel homeViewModel;
    private SprintViewModel sprintViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_timer);

        timePicker = dialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        saveButton = dialog.findViewById(R.id.btnCreateTimer);
        cancelButton = dialog.findViewById(R.id.btnCancel);
        tags = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, tags);
        spinner = dialog.findViewById(R.id.spnTags);

        Bundle bundle = getArguments();
        itemId = bundle.getInt(Constant.ITEM_ID, -1);
        isEditSprintMode = bundle.getBoolean(Constant.IS_EDIT_MODE, false);

        //if is not edit mode - open time picker with 00:00:00 else get data from sprintViewModel and show it
        initSpinner();
        if (!isEditSprintMode) {
            homeViewModel = new ViewModelProvider(
                    getActivity(),
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
            ).get(HomeViewModel.class);

            setTimeToPicker(0,0);

            //observe tags changes
            homeViewModel.getAllTagsByItemId(itemId).observe(getActivity(), newTags -> {
                tags = newTags;
                createSpinnerItems();
                adapter.addAll(newTags);
            });
        } else {
            sprintViewModel = new ViewModelProvider(
                    getActivity(),
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
            ).get(SprintViewModel.class);

            isStartTimerMode = bundle.getBoolean(Constant.IS_START_TIMER_MODE);

            setObserves();
        }
        setListenerOnSaveBtn();
        cancelButton.setOnClickListener(v -> dismiss());

        return dialog;
    }

    private void setObserves() {
        Observer<Long> observer = aLong -> {
            currentTime = aLong;
            updateData();
        };

        if (isStartTimerMode)
            sprintViewModel.getSprintStartTime().observe(getActivity(), observer);
        else
            sprintViewModel.getSprintFinishTime().observe(getActivity(), observer);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        if (position == tags.size() - 1) {
            EditTextFragment etFragment = new EditTextFragment();
            getChildFragmentManager().setFragmentResultListener(EditTextFragment.REQUEST_KEY, getActivity(), (requestKey, result) -> {
                if (requestKey.equals(EditTextFragment.REQUEST_KEY)){
                    //create new Tag and chose it
                    String tagName = result.getString(EditTextFragment.TAG_NAME);
                    Tag tag = new Tag(itemId, tagName);
                    homeViewModel.insert(tag);
                    chosenTag = tag;
                } else {
                    chosenTag = null;
                }
            });
            etFragment.show(getChildFragmentManager(), null);
        } else if (position == 0) {
            chosenTag = null;
        } else {
            chosenTag = tags.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void updateData() {
        if (currentTime != null) {
            DateTime dt = new DateTime(currentTime);
            setTimeToPicker(dt.getHourOfDay(), dt.getMinuteOfHour());
        }
    }

    private void initSpinner() {
        if (!isEditSprintMode) {
            spinner.setOnItemSelectedListener(this);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } else {
            spinner.setVisibility(View.GONE);
        }
    }

    private void setTimeToPicker(int hour, int min) {
        timePicker.setHour(hour);
        timePicker.setMinute(min);
    }

    private void setListenerOnSaveBtn() {
        if (!isEditSprintMode) {
            saveButton.setOnClickListener(view -> {
                long timeMs = (timePicker.getHour() * 60 + timePicker.getMinute()) * 60000;
                homeViewModel.setTimer(timeMs, itemId, chosenTag == null ? null : chosenTag.getId());
                dismiss();
            });
        } else {
            saveButton.setText(R.string.save);
            saveButton.setOnClickListener(v -> {
                //TODO use smth normal for time, not just long
                sprintViewModel.saveTime(isStartTimerMode, (timePicker.getHour()*60L + timePicker.getMinute())*60000L);
                dismiss();
            });
        }
    }

    private void createSpinnerItems() {
        if (chosenTag != null && tags.get(tags.size() - 1).getName().equals(chosenTag.getName()))
            chosenTag = tags.get(tags.size() - 1);
        tags.add(0, new Tag("Choose tag..."));
        tags.add(tags.size(), new Tag("Create new tag"));
        adapter.clear();
    }
}
