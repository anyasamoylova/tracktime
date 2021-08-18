package com.sam.tracktime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sam.tracktime.R;
import com.sam.tracktime.adapter.ChildSprintAdapter;
import com.sam.tracktime.adapter.ParentSprintAdapter;
import com.sam.tracktime.model.Sprint;
import com.sam.tracktime.utils.Constant;
import com.sam.tracktime.viewmodel.SprintViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SprintFragment extends Fragment {
    private SprintViewModel sprintViewModel;
    private Integer itemId;
    private ImageView ivIcon;
    private TextView tvName;
    private RecyclerView recyclerView;
    private ParentSprintAdapter parentAdapter;
    private FloatingActionButton showStatisticButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sprint, container, false);
        sprintViewModel = new ViewModelProvider(
                getActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
        ).get(SprintViewModel.class);

        ivIcon = root.findViewById(R.id.ivIcon);
        ivIcon.setImageResource(getArguments().getInt(Constant.ITEM_RES_ID));
        tvName = root.findViewById(R.id.tvName);
        tvName.setText(getArguments().getString(Constant.ITEM_NAME));
        recyclerView = root.findViewById(R.id.rvSprintsWithDate);

        itemId = getArguments().getInt(Constant.ITEM_ID);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Sprints");
        createRecyclerView();

        showStatisticButton = root.findViewById(R.id.btnShowItemStatistic);
        showStatisticButton.setOnClickListener(v -> ((MainActivity) getActivity()).openItemStatisticFragment(
                itemId,
                getArguments().getString(Constant.ITEM_NAME))
        );

        return root;
    }

    private void createRecyclerView() {
        //on click listener for child adapter
        ChildSprintAdapter.OnSprintClickListener listener = sprint -> {
            Intent intent = new Intent(getActivity(), ChangeSprintActivity.class);
            intent.putExtra(ChangeSprintActivity.SPRINT_ID, sprint.getId());
            startActivity(intent);
        };

        //set ItemDecoration for adapter
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.devider));

        parentAdapter = new ParentSprintAdapter(listener, decoration);
        recyclerView.setAdapter(parentAdapter);

        sprintViewModel.getSprints(itemId).observe(getActivity(), sprints -> {
            if (sprints != null) {
                Map<Long, List<Sprint>> sprintsByDate = sprints.stream().collect(Collectors.groupingBy(Sprint::getStartDay));
                parentAdapter.setSprints(sprintsByDate);
            }
        });


    }

}
