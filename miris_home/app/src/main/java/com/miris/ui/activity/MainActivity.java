package com.miris.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
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
    public static final String ACTION_NO_IMG_ITEM = "action_no_img_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    private final long FINSH_INTERVAL_TIME    = 2000;
    private long backPressedTime        = 0;
    private boolean dialogUpdate = false;
    private boolean updateNoticeData = false;
    private boolean updateAdapter = false;
    private boolean newIntentUpdate = false;
    private boolean newIntentUpdateLoding = false;
    private boolean isLastItemVisibleOpen = true;

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
    List<ParseObject> totalcount;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    ProgressDialog myLoadingDialog;
    int maxSize = 0;
    int setSkip = 0;
    int obsize = 0;
    LinearLayoutManager linearLayoutManager;

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
        linearLayoutManager = new LinearLayoutManager(this) {
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
        dialogUpdate = true;
        updateAdapter = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new loadDataTask().execute();
            }
        }, 1000);
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
        if (myLoadingDialog != null){
            if(myLoadingDialog.isShowing()) {
                myLoadingDialog.dismiss();
                myLoadingDialog = null;
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        newIntentUpdate = true;
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            dialogUpdate = true;
            newIntentUpdateLoding = true;
        }
        new loadDataTask().execute();
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.smoothScrollToPosition(0);
                if (newIntentUpdateLoding) {
                    feedAdapter.showLoadingView();
                    newIntentUpdateLoding = false;
                }
            }
        }, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mirisBadge();
        if (m_openDrawer){
            drawerLayout.closeDrawers();
            m_openDrawer = false;
        }
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        FeedContextMenuManager.getInstance().hidePauseMenu();
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

    @Override
    public void onUserDeleteClick(View v, final int position) {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_text))
                .setMessage(getString(R.string.delete_message))
                .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.cancel();
                        }
                    }
                })
                .setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showDialog();
                        if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                            new deleteImgTask().execute(position);
                        }
                    }
                })
                .show();
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
        Snackbar.make(clContent, getString(R.string.like_you), Snackbar.LENGTH_SHORT).show();
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

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (!dialogUpdate) {
                if (myLoadingDialog != null) {
                    if (!myLoadingDialog.isShowing()) {
                        showDialog();
                    }
                }
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            if (!updateNoticeData) {
                if (noticeData != null) {
                    noticeData.clear();
                    noticeData = null;
                }
                noticeData = new ArrayList<NoticeListData>();
                maxSize = 0;
                setSkip = 0;
            }
            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_notice");
            offerQuery.setLimit(10);
            offerQuery.setSkip(setSkip);
            offerQuery.orderByDescending("createdAt");

            try {
                ob = offerQuery.find();
                obsize = ob.size();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            for (ParseObject country : ob) {
                if (isCancelled()) {
                    return null;
                }
                if (country.get("user_public").toString().equals("N")) {
                    if (!country.get("user_id").toString().equals(memberData.get(0).getuserId())) {
                        setSkip = setSkip + 1;
                        continue;
                    }
                }
                noticeData.add(new NoticeListData(
                        country.getObjectId(),
                        country.get("user_id").toString(),
                        country.get("user_name").toString(),
                        country.get("user_text").toString(),
                        country.getInt("user_like"),
                        country.get("creatdate").toString(),
                        country.get("user_public").toString()));
                new loadImgTask().execute(country);
                setSkip++;
            }
            return null ;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (!updateAdapter) {
                feedAdapter = new FeedAdapter(MainActivity.this, noticeData);
                rvFeed.setAdapter(feedAdapter);
                feedAdapter.setOnFeedItemClickListener(MainActivity.this);

                rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    private int visibleItemCount = 0;
                    private int totalItemCount = 0;
                    private int firstVisibleItemIndex = 0;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);

                        visibleItemCount = rvFeed.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        firstVisibleItemIndex = linearLayoutManager.findFirstVisibleItemPosition();

                        View view = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int diff = (view.getBottom() - (recyclerView.getHeight() + recyclerView.getScrollY()));

                        if (recyclerView.getHeight() > view.getBottom()) {
                            if (isLastItemVisibleOpen && view.getBottom() == recyclerView.getHeight() + diff
                                    && (visibleItemCount + firstVisibleItemIndex) >= totalItemCount) {
                                if (obsize != 0) {
                                    isLastItemVisibleOpen = false;
                                    updateNoticeData = true;
                                    updateAdapter = true;
                                    new loadDataTask().execute();
                                }
                            }
                        }
                    }
                });
                mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
                mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
                mWaveSwipeRefreshLayout.setOnRefreshListener(MainActivity.this);
            }
            isLastItemVisibleOpen = true;
            dialogUpdate = false;
            updateNoticeData = false;
            updateAdapter = false;

            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
            if (newIntentUpdate) {
                showFeedLoadingItemDelayed();
                newIntentUpdate = false;
            }
            feedAdapter.updateItems(true);
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }
    }

    class loadImgTask extends AsyncTask<ParseObject, Void, Integer> {

        @Override
        protected Integer doInBackground(ParseObject... country) {
            int totalCount = 0;
            int updateMaxSize = 0;
            if (isCancelled()) {
                return -1;
            }

            Bitmap bMap = null;
            Bitmap userBmap = null;
            String bMapPath = null;
            ParseFile userImgfile = null;

            ParseQuery<ParseObject> countQuery = ParseQuery.getQuery("miris_commit");
            countQuery.whereEqualTo("user_defult_id", country[0].getObjectId());
            countQuery.orderByDescending("createdAt");
            try {
                totalcount = countQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (totalcount.size() > 0) {
                totalCount = totalcount.size();
            }
            ParseFile userFile = (ParseFile) country[0].get("user_img");

            if (userFile != null) {
                try {
                    byte[] data = userFile.getData();
                    bMapPath = userFile.getUrl();
                    bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
            ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery("miris_member");
            memberQuery.whereEqualTo("user_id", country[0].get("user_id").toString());

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
            try {
                noticeData.get(maxSize).setimgPath(bMapPath);
                noticeData.get(maxSize).setuserimgBitmap(userBmap);
                noticeData.get(maxSize).setimgBitmap(bMap);
                noticeData.get(maxSize).settotalcount(totalCount);
            } catch (IndexOutOfBoundsException e2) {
                e2.printStackTrace();
            }
            updateMaxSize = maxSize;
            maxSize++;
            return updateMaxSize;
        }
        @Override
        protected void onPostExecute(Integer result) {
            feedAdapter.notifyItemChanged(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    class deleteImgTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            ParseQuery<ParseObject> commitListQuery = new ParseQuery<ParseObject>("miris_commit");
            commitListQuery.whereEqualTo("user_defult_id", noticeData.get(params[0]).getobjId());
            commitListQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> module, ParseException e) {
                    if (e == null) {
                        for (ParseObject delete : module) {
                            ParseQuery<ParseObject> userListQuery = new ParseQuery<ParseObject>("miris_member");
                            userListQuery.whereEqualTo("user_id", delete.get("user_id").toString());
                            userListQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject module, ParseException e) {
                                    if (e == null) {
                                        int uCommit = module.getInt("user_totalcommit");

                                        module.put("user_totalcommit", uCommit -1);
                                        module.saveInBackground();
                                        if (memberData.get(0).getuserId().equals(module.get("user_id"))) {
                                            memberData.get(0).setuser_TotalCommit(uCommit -1);
                                        }
                                    }
                                }
                            });
                            delete.deleteInBackground();
                        }
                    }
                }
            });

            ParseQuery<ParseObject> mainListQuery = new ParseQuery<ParseObject>("miris_notice");
            mainListQuery.whereEqualTo("objectId", noticeData.get(params[0]).getobjId());
            mainListQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject noticemodule, ParseException e) {
                    if (e == null) {
                        final int noticeLike = noticemodule.getInt("user_totallike");
                        ParseQuery<ParseObject> userListQuery = new ParseQuery<ParseObject>("miris_member");
                        userListQuery.whereEqualTo("user_id", noticemodule.get("user_id").toString());
                        userListQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject membermodule, ParseException e) {
                                if (e == null) {
                                    int totalLike = membermodule.getInt("user_totallike");
                                    int uRegister = membermodule.getInt("user_registernumber");
                                    membermodule.put("user_totallike", totalLike - noticeLike);
                                    membermodule.put("user_registernumber", uRegister -1);
                                    membermodule.saveInBackground();

                                    memberData.get(0).setuser_TotalLike(totalLike - noticeLike);
                                    memberData.get(0).setuser_registernumber(uRegister -1);
                                }
                            }
                        });
                        noticemodule.deleteInBackground();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateAdapter = true;
            dialogUpdate = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new loadDataTask().execute();
                    Snackbar.make(clContent, getString(R.string.delete_toast), Snackbar.LENGTH_SHORT).show();
                }
            }, 2000);
        }
    }
}