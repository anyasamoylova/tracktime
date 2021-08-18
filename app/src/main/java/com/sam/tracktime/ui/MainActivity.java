package com.sam.tracktime.ui;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.sam.tracktime.R;
import com.sam.tracktime.model.Item;
import com.sam.tracktime.service.AlarmReceiver;
import com.sam.tracktime.utils.Constant;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final int HOME_FRAGMENT = 0;
    private final int HOME_FRAGMENT_IN_EDIT_MODE = 1;
    private final int STATISTIC_FRAGMENT = 2;
    private final int SPRINT_FRAGMENT = 3;
    private final int ITEM_STATISTIC_FRAGMENT = 4;

    private Toolbar toolbar;
    private Fragment fragment;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Drawable navigationIcon;

    private int currentState = HOME_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar and barDrawerToggle
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationIcon = toolbar.getNavigationIcon();
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        //show homeFragment by default
        fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, fragment);
        ft.commit();

        createAlarm();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                currentState = HOME_FRAGMENT;
                break;
            case R.id.nav_statistics:
                fragment = new StatisticsFragment();
                currentState = STATISTIC_FRAGMENT;
                break;
        }
        invalidateOptionsMenu();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        if (id == R.id.nav_statistics) {
            ft.addToBackStack(null);
        }
        ft.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else switch (currentState) {
            case SPRINT_FRAGMENT:
                currentState = HOME_FRAGMENT;
                invalidateOptionsMenu();
                super.onBackPressed();
                break;
            case ITEM_STATISTIC_FRAGMENT:
                currentState = SPRINT_FRAGMENT;
                invalidateOptionsMenu();
                super.onBackPressed();
                break;
            case HOME_FRAGMENT:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionEditItem) {
            currentState = currentState == HOME_FRAGMENT ? HOME_FRAGMENT_IN_EDIT_MODE : HOME_FRAGMENT;
            invalidateOptionsMenu();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            HomeFragment homeFragment = new HomeFragment();
            if (currentState == HOME_FRAGMENT_IN_EDIT_MODE) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(HomeFragment.IS_EDIT_MODE, true);
                homeFragment.setArguments(bundle);
            }
            ft.replace(R.id.content, homeFragment).addToBackStack(null);
            ft.commit();
        }
        if (item.getItemId() == R.id.actionGoBack) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editItem = menu.findItem(R.id.actionEditItem);
        MenuItem goBackItem = menu.findItem(R.id.actionGoBack);
        switch (currentState) {
            case HOME_FRAGMENT:
                editItem.setVisible(true);
                editItem.setIcon(R.drawable.ic_baseline_edit_24);
                toolbar.setNavigationIcon(navigationIcon);
                goBackItem.setVisible(false);
                break;
            case HOME_FRAGMENT_IN_EDIT_MODE:
                editItem.setVisible(true);
                editItem.setIcon(R.drawable.ic_baseline_done_24);
                toolbar.setNavigationIcon(null);
                goBackItem.setVisible(false);
                break;
            case STATISTIC_FRAGMENT:
                editItem.setVisible(false);
                toolbar.setNavigationIcon(navigationIcon);
                goBackItem.setVisible(false);
                break;
            case SPRINT_FRAGMENT:
            case ITEM_STATISTIC_FRAGMENT:
                editItem.setVisible(false);
                toolbar.setNavigationIcon(null);
                goBackItem.setVisible(true);
                break;
            default:
                return false;
        }
        return true;
    }


    //TODO maybe open sprint or ItemStatistic fragment should work throught interface
    public void openSprintsFragment(Item item) {
        currentState = SPRINT_FRAGMENT;
        invalidateOptionsMenu();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SprintFragment sprintFragment = new SprintFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ITEM_ID, item.getId());
        bundle.putString(Constant.ITEM_NAME, item.getName());
        bundle.putInt(Constant.ITEM_RES_ID, item.getIconResId());
        sprintFragment.setArguments(bundle);

        ft.replace(R.id.content, sprintFragment).addToBackStack(null);
        ft.commit();
    }

    public void openItemStatisticFragment(int itemId, String itemName) {
        currentState = ITEM_STATISTIC_FRAGMENT;
        invalidateOptionsMenu();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ItemStatisticFragment statisticsFragment = new ItemStatisticFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ITEM_ID, itemId);
        bundle.putString(Constant.ITEM_NAME, itemName);
        statisticsFragment.setArguments(bundle);

        ft.replace(R.id.content, statisticsFragment).addToBackStack(null);
        ft.commit();
    }

    private void createAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //create AlarmReceiver intent
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //set alarm time at 00:00
        DateTime tomorrow = DateTime.now().plusDays(1);
        DateTime dt = new DateTime(tomorrow.getYear(), tomorrow.getMonthOfYear(), tomorrow.getDayOfMonth(), 0, 0);
        long alarmTimeUTC = dt.getMillis();

        //set alarm
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeUTC, AlarmManager.INTERVAL_DAY, pendingIntent);
    }


}