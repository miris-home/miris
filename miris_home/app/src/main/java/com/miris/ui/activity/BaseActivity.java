package com.miris.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.miris.R;
import com.miris.net.MemberData;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Miris on 09.02.15.
 */
public class BaseActivity extends AppCompatActivity{

    @Optional
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Optional
    @InjectView(R.id.ivLogo)
    ImageView ivLogo;

    @Optional
    @InjectView(R.id.ivAddress)
    ImageView ivAddress;

    @Optional
    @InjectView(R.id.ivCalendar)
    ImageView ivCalendar;

    private MenuItem inboxMenuItem;
    public static ArrayList<MemberData> userData = new ArrayList<MemberData>();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        injectViews();
    }

    protected void injectViews() {
        ButterKnife.inject(this);
        setupToolbar();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        inboxMenuItem = menu.findItem(R.id.action_inbox);
//        inboxMenuItem.setActionView(R.layout.menu_item_view);
//        return true;
//    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MenuItem getInboxMenuItem() {
        return inboxMenuItem;
    }

    public ImageView getIvLogo() {
        return ivLogo;
    }
    public ImageView getivAddress() {
        return ivAddress;
    }
    public ImageView getivCalendar() {
        return ivCalendar;
    }

    @Optional
    @OnClick(R.id.ivAddress)
    public void onivAddressClick(final View v) {
        Intent intent = new Intent(this, AddressActivity.class);
        startActivity(intent);
    }

    @Optional
    @OnClick(R.id.ivCalendar)
    public void onivCalendarClick(final View v) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }
}
