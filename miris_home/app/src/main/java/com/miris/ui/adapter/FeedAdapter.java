package com.miris.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.miris.R;
import com.miris.Utils;
import com.miris.net.NoticeListData;
import com.miris.ui.activity.MainActivity;
import com.miris.ui.view.SendingProgressView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.miris.ui.activity.BaseActivity.memberData;
import static com.miris.ui.activity.BaseActivity.noticeData;

/**
 * Created by Miris on 09.02.15.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 1;
    private boolean animateItems = false;

//    private final Map<Integer, Integer> likesCount = new HashMap<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();

    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;
    private int loadingViewSize = Utils.dpToPx(200);
    int currentLikesCount;

    public FeedAdapter(Context context) {
        this.context = context;
    }
    public FeedAdapter(Context context, ArrayList<NoticeListData> items) {
        this.context = context;
        noticeData = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        if (viewType == VIEW_TYPE_DEFAULT) {
            cellFeedViewHolder.btnComments.setOnClickListener(this);
            cellFeedViewHolder.btnMore.setOnClickListener(this);
            cellFeedViewHolder.ivFeedCenter.setOnClickListener(this);
            cellFeedViewHolder.btnLike.setOnClickListener(this);
            cellFeedViewHolder.ivUserProfile.setOnClickListener(this);
            cellFeedViewHolder.ivUserDelete.setOnClickListener(this);
        } else if (viewType == VIEW_TYPE_LOADER) {
            View bgView = new View(context);
            bgView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            ));
            bgView.setBackgroundColor(0x77ffffff);
            cellFeedViewHolder.vImageRoot.addView(bgView);
            cellFeedViewHolder.vProgressBg = bgView;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(loadingViewSize, loadingViewSize);
            params.gravity = Gravity.CENTER;
            SendingProgressView sendingProgressView = new SendingProgressView(context);
            sendingProgressView.setLayoutParams(params);
            cellFeedViewHolder.vImageRoot.addView(sendingProgressView);
            cellFeedViewHolder.vSendingProgress = sendingProgressView;
        }

        return cellFeedViewHolder;
    }

    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindDefaultFeedItem(position, holder);
        } else {
           bindLoadingFeedItem(position, holder);
        }
    }

    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        if (noticeData.size() == 0) {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.ivFeedBottom.setText(R.string.defult_user_message);
        } else {
            if (noticeData.get(position).getuser_public().equals("N")) {
                holder.ivUserSecret.setVisibility(View.VISIBLE);
            } else {
                holder.ivUserSecret.setVisibility(View.INVISIBLE);
            }
            if (noticeData.get(position).getuserid().equals(memberData.get(0).getuserId())) {
                holder.ivUserDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivUserDelete.setVisibility(View.INVISIBLE);
            }
            holder.ivUserName.setText(noticeData.get(position).getusername());
            if (noticeData.get(position).getimgBitmap() == null) {
                holder.vImageRoot.setVisibility(View.GONE);
            } else {
                holder.vImageRoot.setVisibility(View.VISIBLE);
                holder.ivFeedCenter.setImageBitmap(noticeData.get(position).getimgBitmap());
            }

            holder.ivFeedBottom.setText(noticeData.get(position).geteditText());
            holder.ivUserDate.setText(noticeData.get(position).getdate());
            holder.ivUserProfile.setImageBitmap(noticeData.get(position).getuserimgBitmap());
        }
        currentLikesCount = noticeData.get(holder.getPosition()).getDoLike();
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );
        holder.tsLikesCounter.setCurrentText(likesCountText);

        updateHeartButton(holder, false);

        holder.ivUserProfile.setTag(position);
        holder.btnComments.setTag(position);
        holder.btnMore.setTag(position);
        holder.ivFeedCenter.setTag(holder);
        holder.btnLike.setTag(holder);
        holder.ivUserDelete.setTag(position);

        if (likeAnimations.containsKey(holder)) {
            likeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);
    }

    private void bindLoadingFeedItem(int position, final CellFeedViewHolder holder) {
        if (noticeData.get(position).getimgBitmap() == null) {
            holder.vImageRoot.setVisibility(View.GONE);
        } else {
            holder.vImageRoot.setVisibility(View.VISIBLE);
            holder.ivFeedCenter.setImageBitmap(noticeData.get(position).getimgBitmap());
        }
        holder.ivFeedBottom.setText(noticeData.get(position).geteditText());
        holder.ivUserName.setText(noticeData.get(position).getusername());
        holder.ivUserDate.setText(noticeData.get(position).getdate());
        holder.ivUserProfile.setImageBitmap(noticeData.get(position).getuserimgBitmap());

        holder.vSendingProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.vSendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                holder.vSendingProgress.simulateProgress();
                return true;
            }
        });
        holder.vSendingProgress.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                holder.vSendingProgress.animate().scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
                holder.vProgressBg.animate().alpha(0.f).setDuration(200).setStartDelay(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.vSendingProgress.setScaleX(1);
                                holder.vSendingProgress.setScaleY(1);
                                holder.vProgressBg.setAlpha(1);
                                showLoadingView = false;
                                notifyItemChanged(0);
                            }
                        })
                        .start();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    private void updateLikesCounter(final CellFeedViewHolder holder, boolean animated) {
        currentLikesCount = noticeData.get(holder.getPosition()).getDoLike()+1;
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }
        noticeData.get(holder.getPosition()).setDoLike(currentLikesCount);
        new updateLikeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, holder);
    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.btnComments) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onCommentsClick(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.btnMore) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onMoreClick(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.btnLike) {
            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
            if (!likedPositions.contains(holder.getPosition())) {
                likedPositions.add(holder.getPosition());
                updateLikesCounter(holder, true);
                updateHeartButton(holder, true);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }
            }
        } else if (viewId == R.id.ivFeedCenter) {
            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
            if (!likedPositions.contains(holder.getPosition())) {
                likedPositions.add(holder.getPosition());
                updateLikesCounter(holder, true);
                animatePhotoLike(holder);
                updateHeartButton(holder, false);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }
            }
        } else if (viewId == R.id.ivUserProfile) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onProfileClick(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.ivUserDelete) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onUserDeleteClick(view, (Integer) view.getTag());
            }
        }
    }

    private void animatePhotoLike(final CellFeedViewHolder holder) {
        if (!likeAnimations.containsKey(holder)) {
            holder.vBgLike.setVisibility(View.VISIBLE);
            holder.ivLike.setVisibility(View.VISIBLE);

            holder.vBgLike.setScaleY(0.1f);
            holder.vBgLike.setScaleX(0.1f);
            holder.vBgLike.setAlpha(1f);
            holder.ivLike.setScaleY(0.1f);
            holder.ivLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            likeAnimations.put(holder, animatorSet);

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetLikeAnimationState(holder);
                }
            });
            animatorSet.start();
        }
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        likeAnimations.remove(holder);
        holder.vBgLike.setVisibility(View.GONE);
        holder.ivLike.setVisibility(View.GONE);
    }

    public void updateItems(boolean animated) {
        itemsCount = noticeData.size();
        animateItems = animated;
        notifyDataSetChanged();
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.ivFeedBottom)
        TextView ivFeedBottom;
        @InjectView(R.id.btnComments)
        ImageButton btnComments;
        @InjectView(R.id.btnLike)
        ImageButton btnLike;
        @InjectView(R.id.btnMore)
        ImageButton btnMore;
        @InjectView(R.id.vBgLike)
        View vBgLike;
        @InjectView(R.id.ivLike)
        ImageView ivLike;
        @InjectView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @InjectView(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @InjectView(R.id.ivUserName)
        TextView ivUserName;
        @InjectView(R.id.ivUserDate)
        TextView ivUserDate;
        @InjectView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @InjectView(R.id.ivUserSecret)
        ImageView ivUserSecret;
        @InjectView(R.id.ivUserDelete)
        ImageButton ivUserDelete;


        SendingProgressView vSendingProgress;
        View vProgressBg;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

    }

    public interface OnFeedItemClickListener {
        public void onCommentsClick(View v, int position);

        public void onMoreClick(View v, int position);

        public void onProfileClick(View v, int position);

        public void onUserDeleteClick(View v, int position);

    }

    class updateLikeTask extends AsyncTask<CellFeedViewHolder, Void, Void> {

        @Override
        protected Void doInBackground(CellFeedViewHolder... holder) {
            String objectID = String.valueOf(noticeData.get(holder[0].getPosition()).getobjId());
            String userID = memberData.get(0).getuserId();

            ParseQuery testObject = ParseQuery.getQuery("miris_notice");
            testObject.whereEqualTo("objectId", objectID);
            testObject.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> updateLikeList, ParseException e) {
                    if (e == null) {
                        for (ParseObject nameObj : updateLikeList) {
                            nameObj.put("user_like", currentLikesCount);
                            nameObj.saveInBackground();
                        }
                    }
                }
            });
            ParseQuery mamberObject = ParseQuery.getQuery("miris_member");
            mamberObject.whereEqualTo("user_id", userID);
            mamberObject.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> updateLikeList, ParseException e) {
                    if (e == null) {
                        for (ParseObject nameObj : updateLikeList) {
                            int userTotal;
                            userTotal = nameObj.getInt("user_totallike");
                            nameObj.put("user_totallike", userTotal + 1);
                            nameObj.saveInBackground();
                        }
                    }
                }
            });
            return null;
        }
    }
}
