package com.miris.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.miris.R;
import com.miris.net.CalendarListData;
import com.miris.net.CommitListData;
import com.miris.net.MemberListData;
import com.miris.net.NoticeListData;
import com.miris.net.SessionPreferences;
import com.miris.net.UserProImgData;
import com.miris.net.UserProfileListData;

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
    public static ArrayList<NoticeListData> noticeData;
    public static ArrayList<CommitListData> commitData;
    public static ArrayList<MemberListData> memberData;
    public static ArrayList<UserProfileListData> userProfileListData;
    public static ArrayList<UserProImgData> userProImgData;
    public static ArrayList<CalendarListData> calendarData;

    SessionPreferences session;
    public static int badge_count = 0;

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
        overridePendingTransition(0, 0);
    }

    @Optional
    @OnClick(R.id.ivLogo)
    public void onivLogoClick(final View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Optional
    @OnClick(R.id.ivCalendar)
    public void onivCalendarClick(final View v) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    protected boolean hideSoftInputWindow(View a_oView) {
        InputMethodManager imm = (InputMethodManager)a_oView.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(a_oView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected boolean showSoftInputWindow(View a_oView) {
        InputMethodManager imm = (InputMethodManager)a_oView.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return imm.showSoftInput(a_oView, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    protected void mirisBadge() {
        badge_count = 0;
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count_package_name", getComponentName().getPackageName());
        intent.putExtra("badge_count_class_name", "com.miris.ui.activity.SignInActivity");
        intent.putExtra("badge_count", badge_count);
        sendBroadcast(intent);

        NotificationManager NM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        NM.cancel(1);
    }
}
