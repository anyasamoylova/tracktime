package com.sam.tracktime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.sam.tracktime.R;
import com.sam.tracktime.model.Sprint;
import com.sam.tracktime.model.Tag;
import com.sam.tracktime.ui.dialogFragments.TimerFragment;
import com.sam.tracktime.utils.Constant;
import com.sam.tracktime.viewmodel.SprintViewModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ChangeSprintActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String SPRINT_ID  = "sprintId";

    private TextView tvDuration;
    private TextView tvDate;
    private TextView tvStartTime;
    private TextView tvFinishTime;
    private Spinner spnTags;
    private Button btnSaveSprint;

    private Integer sprintId;
    private List<Tag> tags;
    private Integer itemId;
    private ArrayAdapter<Tag> adapter;

    private SprintViewModel sprintViewModel;
    private Sprint chosenSprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sprint);
        sprintViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(SprintViewModel.class);

        //create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Change sprint");
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);


        //find all ui elements
        tvDuration = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvFinishTime = findViewById(R.id.tvFinishTime);
        btnSaveSprint = findViewById(R.id.btnSaveSprint);
        tags = new ArrayList<>();
        spnTags = findViewById(R.id.spnTags);

        updateData();

        //show Calendar picker on click on date text view
        View.OnClickListener onDateClickListener = v -> showCalendarPickerFragment();
        tvDate.setOnClickListener(onDateClickListener);

        //show time picker on click on start or finish time
        View.OnClickListener onTimeClickListener = v -> showTimerFragment(v);
        tvStartTime.setOnClickListener(onTimeClickListener);
        tvFinishTime.setOnClickListener(onTimeClickListener);

        btnSaveSprint.setOnClickListener(v -> {
            sprintViewModel.update(chosenSprint);
            finish();
        });

        createSpinner();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            sprintViewModel.setTagId(null);
        } else {
            sprintViewModel.setTagId(tags.get(position).getId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    //get info about chosenSprint in db and update ui
    private void  updateData() {
        Intent prevIntent = getIntent();
        sprintId = prevIntent.getIntExtra(SPRINT_ID, -1);
        showData();
        sprintViewModel.getSprintById(sprintId).observe(this, sprint -> {
            chosenSprint = sprint;
            sprintViewModel.updateData(sprint);
        });
    }

    //update ui with user changes
    private void showData() {
        sprintViewModel.getSprintDate().observe(this, aLong -> {
            if (aLong != null)
                tvDate.setText(parseDate(aLong));
        });
        sprintViewModel.getSprintStartTime().observe(this, aLong -> {
            if (aLong != null)
                tvStartTime.setText(parseTime(aLong));
        });
        sprintViewModel.getSprintFinishTime().observe(this, aLong -> {
            if (aLong != null)
                tvFinishTime.setText(parseTime(aLong));
        });

        //update duration
        if (sprintViewModel.getSprintStartTime().getValue() != null &&
                sprintViewModel.getSprintFinishTime().getValue() != null) {
            tvDuration.setText(parseTime(sprintViewModel.getSprintStartTime().getValue()
                    - sprintViewModel.getSprintFinishTime().getValue()));
        }
        sprintViewModel.getItemId().observe(this, integer -> {
            itemId = integer;
            observeTags();
        });

    }

    //receive info about some changes in tags
    private void observeTags() {
        sprintViewModel.getAllTagsByItemId(itemId).observe(this, newTags -> {
            tags = newTags;
            tags.add(0, new Tag("none"));
            adapter.clear();
            adapter.addAll(newTags);
            spnTags.setSelection(0);
            //find index of selected tag
            for (int i = 1; i < newTags.size(); i++) {
                if (newTags.get(i).getId().equals(sprintViewModel.getTagId()))
                    spnTags.setSelection(i);
            }
        });
    }

    private String parseDate(Long date) {
        DateTime dt = new DateTime(date);
        return dt.toString("MM-dd-yy");
    }

    private String parseTime(Long time) {
        DateTime dt = new DateTime(time);
        return dt.toString("HH:mm:ss");
    }

    private void showCalendarPickerFragment() {
        CalendarPickerFragment calendarFragment = new CalendarPickerFragment();
        calendarFragment.show(getSupportFragmentManager(), null);
    }

    private void showTimerFragment(View v) {
        TimerFragment timerFragment = new TimerFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.IS_EDIT_MODE, true);
        bundle.putBoolean(Constant.IS_START_TIMER_MODE, v.getId() == R.id.tvStartTime);
        timerFragment.setArguments(bundle);
        timerFragment.show(getSupportFragmentManager(), null);
    }

    private void createSpinner(){
        spnTags.setOnItemSelectedListener(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTags.setAdapter(adapter);
    }
}