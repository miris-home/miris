package com.miris.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.miris.R;
import com.miris.Utils;
import com.miris.net.CommitListData;
import com.miris.ui.adapter.CommentsAdapter;
import com.miris.ui.view.SendCommentButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by Miris on 09.02.15.
 */
public class CommentsActivity extends BaseDrawerActivity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;
    @InjectView(R.id.llAddComment)
    LinearLayout llAddComment;
    @InjectView(R.id.etComment)
    EditText etComment;
    @InjectView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    private String objectID;
    List<ParseObject> ob;
    List<ParseObject> img_List;
    private boolean startIntroAnimation = false;
    ProgressDialog myLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupComments();
        setupSendCommentButton();

        objectID = getIntent().getStringExtra("objID");
        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        showDialog();
        new loadCommitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        if (m_openDrawer){
            drawerLayout.closeDrawers();
            m_openDrawer = false;
            return;
        } else {
            contentRoot.animate()
                    .translationY(Utils.getScreenHeight(this))
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            CommentsActivity.super.onBackPressed();
                            overridePendingTransition(0, 0);
                        }
                    })
                    .start();
        }

    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            hideSoftInputWindow(v);
            showDialog();
            ParseObject testObject = new ParseObject("miris_commit");
            testObject.put("user_defult_id", objectID);
            testObject.put("user_id", memberData.get(0).getuserId());
            testObject.put("user_name", memberData.get(0).getuser_name());
            testObject.put("user_commit_text", etComment.getText().toString());
            testObject.saveInBackground();
            testObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        startIntroAnimation = true;
                        new loadCommitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            });
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(CommentsActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    class loadCommitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_commit");
            offerQuery.whereEqualTo("user_defult_id", objectID);
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
            commitData = new ArrayList<CommitListData>();
            for (ParseObject country : ob) {
                ParseFile userImgfile = null;
                String userImgurl = null;
                ParseQuery<ParseObject> userImg = ParseQuery.getQuery("miris_member");
                userImg.whereEqualTo("user_id", country.get("user_id").toString());

                try {
                    img_List = userImg.find();
                } catch (ParseException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                int size = img_List.size();
                for (int i = 0; i<size; i++){
                    userImgfile = (ParseFile) img_List.get(i).get("user_img");
                }
                if (userImgfile != null) {
                    userImgurl = userImgfile.getUrl();
                }
                commitData.add(new CommitListData(objectID,
                        country.get("user_commit_text").toString(),
                        userImgurl,
                        country.get("user_name").toString()));

            }
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }

            if (!startIntroAnimation) {
                startIntroAnimation();
            } else {
                commentsAdapter.updateItems();
                commentsAdapter.setAnimationsLocked(false);
                commentsAdapter.setDelayEnterAnimation(false);
                if (commitData.size() > 1) {
                    rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());
                }
                etComment.setText(null);
                btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
            }
        }
    }
}
