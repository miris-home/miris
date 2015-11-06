package com.miris.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.miris.R;
import com.miris.Utils;
import com.miris.net.CalendarListData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.miris.ui.activity.BaseActivity.calendarData;

/**
 * Created by fantastic on 2015-11-03.
 */
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 1;
    private boolean animateItems = false;

    private OnFeedItemClickListener onFeedItemClickListener;
    private ArrayList<CalendarListData> doCalendarlist;

    SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

    DateFormat df = new SimpleDateFormat("yyyy년 MMM dd일");

    public CalendarAdapter(Context context, ArrayList<CalendarListData> items) {
        this.context = context;
        calendarData = items;
        this.doCalendarlist = new ArrayList<CalendarListData>();
        this.doCalendarlist.addAll(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.ivUserName.setOnClickListener(this);
        cellFeedViewHolder.ivUserDate.setOnClickListener(this);
        cellFeedViewHolder.ivUserMessage.setOnClickListener(this);
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
        String strDate = df.format(calendarData.get(position).getuser_calendar());

        holder.ivUserName.setText(calendarData.get(position).getuser_name());
        holder.ivUserDate.setText(strDate);
        holder.ivUserMessage.setText(calendarData.get(position).getuser_text());

        holder.ivUserName.setTag(position);
        holder.ivUserDate.setTag(position);
        holder.ivUserMessage.setTag(position);
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
        @InjectView(R.id.ivUserName)
        TextView ivUserName;
        @InjectView(R.id.ivUserMessage)
        TextView ivUserMessage;
        @InjectView(R.id.ivUserDate)
        TextView ivUserDate;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.ivUserName) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onClickName(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.ivUserDate) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onClickDate(view, (Integer) view.getTag());
            }
        }else if (viewId == R.id.ivUserMessage) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onClickMessage(view, (Integer) view.getTag());
            }
        }
    }

    public interface OnFeedItemClickListener {
        public void onClickName(View v, int position);
        public void onClickDate(View v, int position);
        public void onClickMessage(View v, int position);
    }

    public void getFilter(Date dateClicked) {
        Date today = new Date();
        String strCurYear = CurYearFormat.format(dateClicked);
        String strCurMonth = CurMonthFormat.format(dateClicked);
        String strCurDay = CurDayFormat.format(dateClicked);

        calendarData.clear();
        if (CurYearFormat.format(today).equals(strCurYear)
                &&CurMonthFormat.format(today).equals(strCurMonth)
                &&CurDayFormat.format(today).equals(strCurDay)) {
            calendarData.addAll(doCalendarlist);
        } else {
            for (CalendarListData calList : doCalendarlist) {
                if (CurYearFormat.format(calList.getuser_calendar()).equals(strCurYear)
                        &&CurMonthFormat.format(calList.getuser_calendar()).equals(strCurMonth)
                        &&CurDayFormat.format(calList.getuser_calendar()).equals(strCurDay)) {
                    calendarData.add(calList);
                }
            }
        }
        updateItems(false);
    }
}
