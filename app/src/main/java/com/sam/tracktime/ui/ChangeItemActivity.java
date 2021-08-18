package com.sam.tracktime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.tracktime.R;
import com.sam.tracktime.adapter.IconsAdapter;
import com.sam.tracktime.obj.Icons;
import com.sam.tracktime.utils.Constant;

public class ChangeItemActivity extends AppCompatActivity {
    private static final String TAG = "ChangeItemActivity";

    private int itemId;
    private String itemName;
    private int mainIconRes;
    private long itemDuration;
    private Intent prevIntent;

    private Button btnSaveItem;
    private Button btnDeleteItem;
    private EditText etAddItem;
    private ImageView ivMainIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_item);

        //create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        //find ui elements
        btnSaveItem = findViewById(R.id.btnSaveItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        etAddItem = findViewById(R.id.etAddItem);
        ivMainIcon = findViewById(R.id.ivMainIcon);

        createRecyclerView();

        //if user wanted to create new Item
        prevIntent = getIntent();
        if (prevIntent.getStringExtra(Constant.OPERATION).compareTo(Constant.OPERATION_ADD) == 0) {
            actionBar.setTitle(R.string.create_new_category);
            createNewItem();
        } else {
            //else he wants to change item, so we should get data about this item
            actionBar.setTitle(R.string.edit_category);
            getDataFromIntent();
            setOnSaveBtnListener();
            setOnDeleteBtnListener();
        }
    }

    private void createRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.rvIcons);
        final IconsAdapter adapter = new IconsAdapter(this, Icons.getIcons());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            mainIconRes = adapter.getIconResId(position);
            ivMainIcon.setImageResource(mainIconRes);
        });
    }

    private void createNewItem() {
        btnDeleteItem.setVisibility(View.GONE);
        mainIconRes = R.drawable.car;
        ivMainIcon.setImageResource(mainIconRes);

        btnSaveItem.setOnClickListener(v -> {
            //send new data to MainActivity if everything OK
            Intent newIntent = new Intent();
            newIntent.putExtra(Constant.ITEM_NAME, etAddItem.getText().toString());
            newIntent.putExtra(Constant.ITEM_RES_ID, mainIconRes);
            setResult(RESULT_OK, newIntent);
            finish();
        });
    }

    private void getDataFromIntent(){
        btnDeleteItem.setVisibility(View.VISIBLE);
        itemId = prevIntent.getIntExtra(Constant.ITEM_ID, -1);
        itemName = prevIntent.getStringExtra(Constant.ITEM_NAME);
        mainIconRes = prevIntent.getIntExtra(Constant.ITEM_RES_ID, -1);
        itemDuration = prevIntent.getLongExtra(Constant.ITEM_DURATION, 0L);
        etAddItem.setText(itemName);
        ivMainIcon.setImageResource(mainIconRes);
    }

    private void setOnSaveBtnListener() {
        btnSaveItem.setOnClickListener(v -> {
            if (itemId == -1 || mainIconRes == -1) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Didn't receive itemId or iconRes from MainActivity");
            } else {
                //send new data to MainActivity if everything OK
                Intent newIntent = new Intent();
                newIntent.putExtra(Constant.ITEM_ID, itemId);
                newIntent.putExtra(Constant.ITEM_NAME, etAddItem.getText().toString());
                newIntent.putExtra(Constant.ITEM_RES_ID, mainIconRes);
                newIntent.putExtra(Constant.ITEM_DURATION, itemDuration);
                setResult(RESULT_OK, newIntent);
            }
            finish();
        });
    }

    private void setOnDeleteBtnListener() {
        btnDeleteItem.setOnClickListener(v -> {
            if (itemId == -1 || mainIconRes == -1) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Didn't receive itemId or iconRes from MainActivity");
            } else {
                //open remove dialog to sure that user wants to delete
                RemoveItemDialogFragment removeFragment = new RemoveItemDialogFragment();
                getSupportFragmentManager().setFragmentResultListener(RemoveItemDialogFragment.REQUEST_KEY, this, (requestKey, result) -> {
                    if (requestKey.equals(RemoveItemDialogFragment.REQUEST_KEY)) {
                        Integer removeMode = result.getInt(Constant.REMOVE_MODE, -1);
                        if (removeMode != -1) {
                            //send info about item user wants to delete to MainActivity
                            Intent newIntent = new Intent();
                            newIntent.putExtra(Constant.ITEM_ID, itemId);
                            newIntent.putExtra(Constant.REMOVE_MODE, removeMode);
                            setResult(RESULT_OK, newIntent);
                            finish();
                        }
                    }
                });
                removeFragment.show(getSupportFragmentManager(), null);
            }
        });
    }
}
