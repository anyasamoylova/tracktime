package com.sam.tracktime.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sam.tracktime.R;
import com.sam.tracktime.adapter.ItemsAdapter;
import com.sam.tracktime.model.Item;
import com.sam.tracktime.model.MyTimer;
import com.sam.tracktime.service.NotificationService;
import com.sam.tracktime.ui.dialogFragments.TimerFragment;
import com.sam.tracktime.utils.Constant;
import com.sam.tracktime.utils.WakeLockManager;
import com.sam.tracktime.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    //TODO should i put it in constant?
    private static final int REQUEST_CODE_ADD_ITEM = 1;
    private static final int REQUEST_CODE_EDIT_ITEM = 2;

    public static final String IS_EDIT_MODE = "isEditMode";

    private HomeViewModel homeViewModel;
    private TextView tvTimer;
    private ItemsAdapter adapter;

    private boolean isEditMode = false;
    private List<Item> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new ViewModelProvider(
                getActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
        ).get(HomeViewModel.class);
        //homeViewModel.initDB();
        createTimer(root);
        createRecyclerView(root);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("TrackTime");
        startService();

        FloatingActionButton addButton = root.findViewById(R.id.btnAddItem);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeItemActivity.class);
            intent.putExtra(Constant.OPERATION, Constant.OPERATION_ADD);
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM);
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //new item was created
        if (requestCode == REQUEST_CODE_ADD_ITEM && resultCode == RESULT_OK) {
            homeViewModel.insert(new Item (
                    data.getStringExtra(Constant.ITEM_NAME),
                    data.getIntExtra(Constant.ITEM_RES_ID, -1)));
        }

        //item must be changed or deleted
        if (requestCode == REQUEST_CODE_EDIT_ITEM && resultCode == RESULT_OK) {
            int itemId = data.getIntExtra(Constant.ITEM_ID, -1);
            //then item was changed
            if (data.hasExtra(Constant.ITEM_NAME)) {
                int itemResId = data.getIntExtra(Constant.ITEM_RES_ID, -1);
                String itemName = data.getStringExtra(Constant.ITEM_NAME);
                long itemDuration = data.getLongExtra(Constant.ITEM_DURATION, 0L);
                homeViewModel.changeItemNameAndResId(itemId, itemName, itemResId, itemDuration);
            } else {
                Integer removeMode = data.getIntExtra(Constant.REMOVE_MODE, -1);
                if (removeMode.equals(Constant.REMOVE_ALL))
                    homeViewModel.deleteById(itemId);
                else if (removeMode.equals(Constant.REMOVE_ONLY_ICON))
                    homeViewModel.removeIconOfItem(itemId);
            }
        }


    }

    private void createTimer(View root) {
        final ImageView toggleTimer = root.findViewById(R.id.ivToggleItem);
        tvTimer = root.findViewById(R.id.tvTimer);

        //on touch start dragging
        tvTimer.setOnTouchListener((v, event) -> {
            View.DragShadowBuilder shadow = new MyDragShadowBuilder(v);
            v.startDragAndDrop(null, shadow, null, 0);
            return true;
        });

        //update timer's ui
        homeViewModel.getTimeMsLeft().observe(getActivity(), timeMs -> {
            tvTimer.setText(homeViewModel.getTimeAsStr());
        });

        //change icon depends on timer's state
        homeViewModel.getTimerState().observe(getActivity(), runningState -> {
            if (runningState.equals(MyTimer.STATE_RUNNING)) {
                toggleTimer.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
            } else if (runningState.equals(MyTimer.STATE_STOPPED)) {
                toggleTimer.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
            } else {
                toggleTimer.setImageResource(R.drawable.ic_baseline_stop_circle_24);
            }
        });

        toggleTimer.setOnClickListener(v -> homeViewModel.toggleTimer());
    }

    private void createRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.rvItems);
        adapter = new ItemsAdapter(getContext());
        recyclerView.setAdapter(adapter);
        Bundle bundle = getArguments();

        if (bundle != null) {
            isEditMode = bundle.getBoolean(IS_EDIT_MODE, false);
        }
        setOnItemClickListener();

        if (!isEditMode) {
            adapter.setEditMode(false);
            setListenersForStandardMode();
        }

        homeViewModel.getAllItems().observe(getActivity(), items -> {
            if (items != null) {
                this.items = items;
                adapter.setItems(items);
            }
        });

        //if is edit mode, gives we ability to change order of items
        if (isEditMode) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    private void setOnItemClickListener() {
        //if isEditMode we save all new data, else just toggle timer
        if (isEditMode) {
            adapter.setEditMode(true);
            adapter.setOnItemClickListener(position -> {
                Item item = adapter.getItemAt(position);
                Intent intent = new Intent (getActivity(), ChangeItemActivity.class);

                intent.putExtra(Constant.OPERATION, Constant.OPERATION_EDIT_REMOVE);
                intent.putExtra(Constant.ITEM_ID, item.getId());
                intent.putExtra(Constant.ITEM_NAME, item.getName());
                intent.putExtra(Constant.ITEM_RES_ID, item.getIconResId());
                intent.putExtra(Constant.ITEM_DURATION, item.getTodayDuration());
                startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM);
            });
        } else {
            adapter.setOnItemClickListener(position -> {
                Item item = adapter.getItemAt(position);
                homeViewModel.toggleItem(item);
            });
        }
    }

    private void setListenersForStandardMode() {
        //on long click open item's sprints
        adapter.setLongClickListener(position -> ((MainActivity) getActivity()).openSprintsFragment(adapter.getItemAt(position)));
        //on drop set timer on the item
        adapter.setOnDropAtItemListener(position -> {
            Bundle bundle1 = new Bundle();
            bundle1.putInt(Constant.ITEM_ID, adapter.getItemAt(position).getId());
            bundle1.putBoolean(Constant.IS_EDIT_MODE, false);
            TimerFragment timerFragment = new TimerFragment();
            timerFragment.setArguments(bundle1);
            timerFragment.show(getChildFragmentManager(), null);
        });
    }

    @SuppressLint({"BatteryLife", "WakelockTimeout"})
    public void startService() {
        //use wakeLock to not go to sleeping mode
        PowerManager.WakeLock wakeLock = WakeLockManager.getWakeLock(requireContext());
        wakeLock.acquire();

        Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP |
                    ItemTouchHelper.DOWN |
                    ItemTouchHelper.START |
                    ItemTouchHelper.END,
            0) {
        @Override
        public boolean onMove (
                @NonNull @NotNull RecyclerView recyclerView,
                @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                @NonNull @NotNull RecyclerView.ViewHolder target)
        {
            int fromPosition = viewHolder.getAbsoluteAdapterPosition();
            int toPosition = target.getAbsoluteAdapterPosition();
            Collections.swap(items ,fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            //do nothing
        }
    };


}
