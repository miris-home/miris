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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miris.R;
import com.miris.Utils;
import com.miris.net.ItnewsListData;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantastic on 2015-11-03.
 */
public class ITnewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_DEFAULT = 1;

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 1;
    private boolean animateItems = false;

    private OnNewsItemClickListener onNewsItemClickListener;
    private ArrayList<ItnewsListData> itnewsListDatas;

    public ITnewAdapter(Context context, ArrayList<ItnewsListData> items) {
        this.context = context;
        itnewsListDatas = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_itnews, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.ivNewsImg.setOnClickListener(this);
        cellFeedViewHolder.ivNewsTitle.setOnClickListener(this);
        cellFeedViewHolder.ivNewsName.setOnClickListener(this);
        cellFeedViewHolder.ivNewsLinearLayout.setOnClickListener(this);
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

        String str = itnewsListDatas.get(position).getdueDate();
        Date date = new Date(str);
        SimpleDateFormat fdm = new SimpleDateFormat("MM월 dd일 HH시 mm분 ");

        if (!itnewsListDatas.get(position).getImg().equals("")) {
            Picasso.with(context)
                    .load(itnewsListDatas.get(position).getImg())
                    .into(holder.ivNewsImg);
        } else {
            Picasso.with(context)
                .load(R.drawable.no_image)
                .into(holder.ivNewsImg);
        }

        holder.ivNewsTitle.setText(itnewsListDatas.get(position).getTitle());
        holder.ivNewsName.setText(itnewsListDatas.get(position).getauthor() + " " + fdm.format(date));

        holder.ivNewsImg.setTag(position);
        holder.ivNewsTitle.setTag(position);
        holder.ivNewsName.setTag(position);
        holder.ivNewsLinearLayout.setTag(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    public void setOnNewsItemClickListener(OnNewsItemClickListener onNewsItemClickListener) {
        this.onNewsItemClickListener = onNewsItemClickListener;
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems(boolean animated) {
        itemsCount = itnewsListDatas.size();
        animateItems = animated;
        notifyDataSetChanged();
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivNewsImg)
        ImageView ivNewsImg;
        @InjectView(R.id.ivNewsTitle)
        TextView ivNewsTitle;
        @InjectView(R.id.ivNewsName)
        TextView ivNewsName;
        @InjectView(R.id.ivNewsLinearLayout)
        LinearLayout ivNewsLinearLayout;


        View vProgressBg;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.ivNewsImg) {
            if (onNewsItemClickListener != null) {
                onNewsItemClickListener.onSendImg(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.ivNewsTitle) {
            if (onNewsItemClickListener != null) {
                onNewsItemClickListener.onSendTitle(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.ivNewsName) {
            onNewsItemClickListener.onSendName(view, (Integer) view.getTag());
        } else if (viewId == R.id.ivNewsLinearLayout) {
            onNewsItemClickListener.onSendLinearLayout(view, (Integer) view.getTag());
        }
    }

    public interface OnNewsItemClickListener {
        public void onSendImg(View v, int position);
        public void onSendTitle(View v, int position);
        public void onSendName(View v, int position);
        public void onSendLinearLayout(View v, int position);
    }

}
