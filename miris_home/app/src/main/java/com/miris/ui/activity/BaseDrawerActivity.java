package com.miris.ui.activity;

import android.app.Activity;
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
import android.widget.TextView;

import com.miris.R;
import com.miris.ui.adapter.BlurBehind;
import com.miris.ui.adapter.OnBlurCompleteListener;
import com.miris.ui.utils.CircleTransformation;
import com.miris.ui.view.FeedContextMenuManager;
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
    @InjectView(R.id.ivMenuUserProfileName)
    TextView ivMenuUserProfileName;

    private int avatarSize;
    private String profilePhoto;
    boolean m_openDrawer = false;
    Activity mActivity;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        mActivity = this;
        if (memberData == null) {
            Intent i = new Intent(this, SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
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
                    FeedContextMenuManager.getInstance().hidePauseMenu();
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
                com.miris.ui.activity.UserProfileActivity.startUserProfileFromLocation(
                        startingLocation, BaseDrawerActivity.this, memberData.get(0).getuserId());
                overridePendingTransition(0, 0);
            }
        }, 200);
    }

    private void setupHeader() {
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        this.profilePhoto = memberData.get(0).getuserImgurl();
        ivMenuUserProfileName.setText(memberData.get(0).getuser_name());
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
            Intent intent;
            drawerLayout.closeDrawers();

            switch (menuItem.getItemId()) {
                case R.id.menu_settings:
                    intent = new Intent(getApplication(), SettingActivity.class);
                    startActivity(intent);
                    break;

                case R.id.menu_about:
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BlurBehind.getInstance().execute(mActivity, new OnBlurCompleteListener() {
                                @Override
                                public void onBlurComplete() {
                                    Intent intent = new Intent(getApplication(), AboutActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            });
                        }
                    }, 300);
                    break;

                case R.id.menu_feed:
                    intent = new Intent(getApplication(), ITnewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.menu_news:
                    intent = new Intent(getApplication(), MessageActivity.class);
                    startActivity(intent);
                    break;
            }
            return false;
        }
    };
}
