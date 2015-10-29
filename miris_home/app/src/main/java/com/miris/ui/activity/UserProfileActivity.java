package com.miris.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.miris.R;
import com.miris.net.UserProImgData;
import com.miris.net.UserProfileListData;
import com.miris.ui.adapter.UserProfileAdapter;
import com.miris.ui.utils.CircleTransformation;
import com.miris.ui.view.RevealBackgroundView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by Miris on 09.02.15.
 */
public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    public static final String USER_ID = "user_id";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @InjectView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @InjectView(R.id.vUserDetails)
    View vUserDetails;
    @InjectView(R.id.vUserStats)
    View vUserStats;
    @InjectView(R.id.vUserProfileRoot)
    View vUserProfileRoot;

    @InjectView(R.id.vUserName)
    TextView vUserName;
    @InjectView(R.id.vUserNickname)
    TextView vUserNickname;
    @InjectView(R.id.vUserLike)
    TextView vUserLike;
    @InjectView(R.id.vUserRegister)
    TextView vUserRegister;
    @InjectView(R.id.vUserCommit)
    TextView vUserCommit;

    private int avatarSize;
    private String profilePhoto;
    private String userId;
    private UserProfileAdapter userPhotosAdapter;
    List<ParseObject> userAcountList;
    List<ParseObject> userImgList;
    ProgressDialog myLoadingDialog;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity, String userId) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(USER_ID, userId);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userId = getIntent().getStringExtra(USER_ID);
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        new loadDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);

    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this);
            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileHeader();
        } else {
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());
           vUserStats.setAlpha(0);

           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
           vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    @Override
    public void onDestroy() {
        if (userProfileListData != null){
            userProfileListData.clear();
        }
        if (userProImgData != null){
            userProImgData.clear();
        }
        super.onDestroy();
    }
    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showDialog();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            userProfileListData = new ArrayList<UserProfileListData>();
            ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery("miris_member");
            memberQuery.whereEqualTo("user_id", userId);
            memberQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject membermodule, ParseException e) {
                    if (e == null) {
                        String userImgurl = null;
                        userImgurl = ((ParseFile) membermodule.get("user_img")).getUrl();
                        userProfileListData.add(new UserProfileListData(
                                membermodule.get("user_id").toString(),
                                membermodule.get("user_name").toString(),
                                membermodule.get("user_age").toString(),
                                userImgurl,
                                membermodule.getInt("user_totallike"),
                                membermodule.getInt("user_totalcommit"),
                                membermodule.getInt("user_registernumber"),
                                membermodule.get("user_rank").toString()));

                        new loadImgDataTask().execute();
                    }
                }
            });
            return null ;

        }
        @Override
        protected void onPostExecute(Void result) {
        }
    }

    class loadImgDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            userProImgData = new ArrayList<UserProImgData>();
            ParseQuery<ParseObject> memberImgQuery = ParseQuery.getQuery("miris_notice");
            memberImgQuery.whereEqualTo("user_id", userId);
            try {
                for (ParseObject img : memberImgQuery.find()) {
                    if (img.get("user_public").toString().equals("N")) {
                        if (!img.get("user_id").toString().equals(memberData.get(0).getuserId())) {
                            continue;
                        }
                    }
                    ParseFile userImgfile = null;
                    String userImgurlData = null;
                    userImgfile = (ParseFile) img.get("user_img");
                    if (userImgfile != null) {
                        userImgurlData = userImgfile.getUrl();
                        userProImgData.add(new UserProImgData(userImgurlData));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null ;
        }
        @Override
        protected void onPostExecute(Void result) {
            try {
                Picasso.with(getApplicationContext())
                        .load(userProfileListData.get(0).getuser_img_url())
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(avatarSize, avatarSize)
                        .centerCrop()
                        .transform(new CircleTransformation())
                        .into(ivUserProfilePhoto);

                vUserName.setText(userProfileListData.get(0).getuser_name());
                vUserNickname.setText(userProfileListData.get(0).getuser_rank());

                vUserRegister.setText(String.valueOf(userProfileListData.get(0).getuser_registernumber()));
                vUserLike.setText(String.valueOf(userProfileListData.get(0).getuser_TotalLike()));
                vUserCommit.setText(String.valueOf(userProfileListData.get(0).getuser_TotalCommit()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (userPhotosAdapter != null) {
                userPhotosAdapter.updateItems(true);
            }
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
        }
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(UserProfileActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }
}
