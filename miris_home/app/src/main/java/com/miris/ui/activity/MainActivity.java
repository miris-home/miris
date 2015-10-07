package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.miris.R;
import com.miris.Utils;
import com.miris.net.NoticeListData;
import com.miris.ui.adapter.FeedAdapter;
import com.miris.ui.view.FeedContextMenu;
import com.miris.ui.view.FeedContextMenuManager;
import com.miris.ui.view.WaveSwipeRefreshLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener, WaveSwipeRefreshLayout.OnRefreshListener {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    private final long FINSH_INTERVAL_TIME    = 2000;
    private long backPressedTime        = 0;
    public boolean updateData = false;

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @InjectView(R.id.content)
    CoordinatorLayout clContent;

    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;
    List<ParseObject> ob;
    List<ParseObject> img_List;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    ProgressDialog myLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFeed();
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        new loadDataTask().execute();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_notice");
                try {
                    if (noticeData.size() < offerQuery.find().size()) {
                        updateData = true;
                        new loadDataTask().execute();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (!updateData) {
                showDialog();
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_notice");
            offerQuery.orderByDescending("createdAt");

            try {
                ob = offerQuery.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null ;

        }
        @Override
        protected void onPostExecute(Void result) {
            noticeData = new ArrayList<NoticeListData>();
            for (ParseObject country : ob) {
                Bitmap bMap = null;
                Bitmap userBmap = null;
                ParseFile userImgfile = null;
                ParseFile userFile = (ParseFile) country.get("user_img");

                if (userFile != null) {
                    try {
                        byte[] data = userFile.getData();
                        bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }
                }
                ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery("miris_member");
                memberQuery.whereEqualTo("user_id", country.get("user_id").toString());

                try {
                    img_List = memberQuery.find();
                } catch (ParseException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                int size = img_List.size();
                for (int i = 0; i<size; i++){
                    userImgfile = (ParseFile) img_List.get(i).get("user_img");
                }
                if (userImgfile != null) {
                    try {
                        byte[] data2 = userImgfile.getData();
                        userBmap = BitmapFactory.decodeByteArray(data2, 0, data2.length);
                        userBmap = userBmap.createScaledBitmap(userBmap, 120, 120, true);
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }
                }
                noticeData.add(new NoticeListData(
                        country.getObjectId(),
                        country.get("user_id").toString(),
                        country.get("user_name").toString(),
                        userBmap,
                        bMap,
                        country.get("user_text").toString(),
                        country.getInt("user_like"),
                        country.get("creatdate").toString()));
            }
            if (!updateData) {
                feedAdapter = new FeedAdapter(MainActivity.this, noticeData);
                rvFeed.setAdapter(feedAdapter);
                feedAdapter.setOnFeedItemClickListener(MainActivity.this);

                rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
                    }
                });

                mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
                mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
                mWaveSwipeRefreshLayout.setOnRefreshListener(MainActivity.this);
            }
            feedAdapter.updateItems(true);
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
        }
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(MainActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
            myLoadingDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            updateData = true;
            showFeedLoadingItemDelayed();
            new loadDataTask().execute();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 700);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
    }


    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getivAddress().setTranslationY(-actionbarSize);
        getivCalendar().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getivAddress().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500);
        getivCalendar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(600);
        startContentAnimation();
    }

    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra("objID", noticeData.get(position).getobjId());
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v, int position) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        com.miris.ui.activity.UserProfileActivity.startUserProfileFromLocation
                (startingLocation, this, noticeData.get(position).getuserid());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        com.miris.ui.activity.TakePhotoActivity.startCameraFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        long tempTime        = System.currentTimeMillis();
        long intervalTime    = tempTime - backPressedTime;
        if (m_openDrawer){
            drawerLayout.closeDrawers();
            m_openDrawer = false;
            return;
        } else {
            if ( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime ) {
                super.onBackPressed();
            }
            else {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), getString(R.string.back_toast), Toast.LENGTH_SHORT).show();
            }
        }
    }
}