package com.miris.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.miris.R;
import com.miris.ui.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Miris on 09.02.15.
 */
public class BaseDrawerActivity extends BaseActivity {

    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.ivMenuUserProfilePhoto)
    ImageView ivMenuUserProfilePhoto;

    private int avatarSize;
    private String profilePhoto;
    boolean m_openDrawer = false;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        injectViews();

        setupHeader();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_openDrawer = true;
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    @OnClick(R.id.vGlobalMenuHeader)
    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                com.miris.ui.activity.UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }

    private void setupHeader() {
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        this.profilePhoto = getResources().getString(R.string.user_profile_photo);
        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }

    private NavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener(){

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.menu_settings:
                    Intent intent = new Intent(getApplication(), SettingActivity.class);
                    startActivity(intent);
                    break;
            }

            return false;
        }
    };
}
