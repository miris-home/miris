package com.miris.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.miris.R;
import com.miris.Utils;
import com.miris.net.CalendarListData;
import com.miris.ui.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.miris.ui.activity.BaseActivity.calendarData;

/**
 * Created by fantastic on 2015-11-03.
 */
public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_DEFAULT = 1;

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 1;
    private boolean animateItems = false;

    private OnFeedItemClickListener onFeedItemClickListener;
    private ArrayList<CalendarListData> searcharraylist;

    public AddressAdapter(Context context, ArrayList<CalendarListData> items) {
        this.context = context;
        calendarData = items;
        this.searcharraylist = new ArrayList<CalendarListData>();
        this.searcharraylist.addAll(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.sendIvUserMessage.setOnClickListener(this);
        cellFeedViewHolder.sendIvUserCall.setOnClickListener(this);
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
        Picasso.with(context)
                .load(calendarData.get(position).getuserImgurl())
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(context.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                        context.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                .centerCrop()
                .transform(new CircleTransformation())
                .into(holder.ivUserProfile);

        holder.ivUserName.setText(calendarData.get(position).getuser_name()
                +" ("+calendarData.get(position).getuser_rank() +")");
        holder.ivUserNumber.setText(calendarData.get(position).getuser_phonenumber());
        holder.ivUserEmail.setText(calendarData.get(position).getuser_email());

        holder.ivUserProfile.setTag(position);
        holder.ivUserName.setTag(position);
        holder.ivUserNumber.setTag(position);
        holder.sendIvUserMessage.setTag(position);
        holder.sendIvUserCall.setTag(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems(boolean animated) {
        itemsCount = calendarData.size();
        animateItems = animated;
        notifyDataSetChanged();
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @InjectView(R.id.ivUserName)
        TextView ivUserName;
        @InjectView(R.id.ivUserNumber)
        TextView ivUserNumber;
        @InjectView(R.id.ivUserEmail)
        TextView ivUserEmail;
        @InjectView(R.id.sendIvUserMessage)
        ImageView sendIvUserMessage;
        @InjectView(R.id.sendIvUserCall)
        ImageView sendIvUserCall;


        View vProgressBg;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.sendIvUserMessage) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onSendMessage(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.sendIvUserCall) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onSendCall(view, (Integer) view.getTag());
            }
        }
    }

    public interface OnFeedItemClickListener {
        public void onSendMessage(View v, int position);
        public void onSendCall(View v, int position);
    }

    public void getFilter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        calendarData.clear();
        if (charText.length() == 0) {
            calendarData.addAll(searcharraylist);
        } else {
            for (CalendarListData calList : searcharraylist) {
                if (calList.getuser_name().toLowerCase(Locale.getDefault()).contains(charText)){
                    calendarData.add(calList);
                }
            }
        }
        if (calendarData.size() == 0) {
            calendarData.addAll(searcharraylist);
        }
        updateItems(false);
    }



}
