package com.miris.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.miris.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerLayout extends RelativeLayout {

    private ViewPager pager;
    private LinearLayout indicatorContainer;

    private Drawable unSelectedDrawable;
    private Drawable selectedDrawable;

    private int WHAT_AUTO_PLAY = 1000;

    private boolean isAutoPlay = true;

    private int itemCount;

    private int selectedIndicatorColor = 0xffff0000;
    private int unSelectedIndicatorColor = 0x88888888;
    private int bannerImgColor = 0x88000000;

    private Shape indicatorShape = Shape.oval;
    private int selectedIndicatorHeight = 6;
    private int selectedIndicatorWidth = 6;
    private int unSelectedIndicatorHeight = 6;
    private int unSelectedIndicatorWidth = 6;

    private Position indicatorPosition = Position.centerBottom;
    private int autoPlayDuration = 4000;
    private int scrollDuration = 900;

    private int indicatorSpace = 3;
    private int indicatorMargin = 10;

    private int defaultImage;

    private int banner_Text_Color = 0xffffffff;
    private int banner_Text_Size = 30;

    private enum Shape {
        rect, oval
    }

    private enum Position {
        centerBottom,
        rightBottom,
        leftBottom,
        centerTop,
        rightTop,
        leftTop
    }

    private OnBannerItemClickListener onBannerItemClickListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                if (pager != null) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
                }
            }
            return false;
        }
    });

    public BannerLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BannerLayoutStyle, defStyle, 0);
        selectedIndicatorColor = array.getColor(R.styleable.BannerLayoutStyle_selectedIndicatorColor, selectedIndicatorColor);
        unSelectedIndicatorColor = array.getColor(R.styleable.BannerLayoutStyle_unSelectedIndicatorColor, unSelectedIndicatorColor);

        int shape = array.getInt(R.styleable.BannerLayoutStyle_indicatorShape, Shape.oval.ordinal());
        for (Shape shape1 : Shape.values()) {
            if (shape1.ordinal() == shape) {
                indicatorShape = shape1;
                break;
            }
        }
        selectedIndicatorHeight = (int) array.getDimension(R.styleable.BannerLayoutStyle_selectedIndicatorHeight, selectedIndicatorHeight);
        selectedIndicatorWidth = (int) array.getDimension(R.styleable.BannerLayoutStyle_selectedIndicatorWidth, selectedIndicatorWidth);
        unSelectedIndicatorHeight = (int) array.getDimension(R.styleable.BannerLayoutStyle_unSelectedIndicatorHeight, unSelectedIndicatorHeight);
        unSelectedIndicatorWidth = (int) array.getDimension(R.styleable.BannerLayoutStyle_unSelectedIndicatorWidth, unSelectedIndicatorWidth);

        int position = array.getInt(R.styleable.BannerLayoutStyle_indicatorPosition, Position.centerBottom.ordinal());
        for (Position position1 : Position.values()) {
            if (position == position1.ordinal()) {
                indicatorPosition = position1;
            }
        }
        indicatorSpace = (int) array.getDimension(R.styleable.BannerLayoutStyle_indicatorSpace, indicatorSpace);
        indicatorMargin = (int) array.getDimension(R.styleable.BannerLayoutStyle_indicatorMargin, indicatorMargin);
        autoPlayDuration = array.getInt(R.styleable.BannerLayoutStyle_autoPlayDuration, autoPlayDuration);
        scrollDuration = array.getInt(R.styleable.BannerLayoutStyle_scrollDuration, scrollDuration);
        isAutoPlay = array.getBoolean(R.styleable.BannerLayoutStyle_isAutoPlay, isAutoPlay);
        defaultImage = array.getResourceId(R.styleable.BannerLayoutStyle_defaultImage,defaultImage);
        array.recycle();

        LayerDrawable unSelectedLayerDrawable;
        LayerDrawable selectedLayerDrawable;
        GradientDrawable unSelectedGradientDrawable;
        unSelectedGradientDrawable = new GradientDrawable();

        GradientDrawable selectedGradientDrawable;
        selectedGradientDrawable = new GradientDrawable();
        switch (indicatorShape) {
            case rect:
                unSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                selectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
            case oval:
                unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
                selectedGradientDrawable.setShape(GradientDrawable.OVAL);
                break;
        }
        unSelectedGradientDrawable.setColor(unSelectedIndicatorColor);
        unSelectedGradientDrawable.setSize(unSelectedIndicatorWidth, unSelectedIndicatorHeight);
        unSelectedLayerDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        unSelectedDrawable = unSelectedLayerDrawable;

        selectedGradientDrawable.setColor(selectedIndicatorColor);
        selectedGradientDrawable.setSize(selectedIndicatorWidth, selectedIndicatorHeight);
        selectedLayerDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        selectedDrawable = selectedLayerDrawable;

    }

    public void setViewUrls(List<String> urls, List<String> text) {
        List<View> views = new ArrayList<>();
        itemCount = urls.size();

        if (itemCount < 1) {
            throw new IllegalStateException("item count not equal zero");
        }  else {
            for (int i = 0; i < urls.size(); i++) {
                views.add(getImageView(urls.get(i), i, text.get(i)));
            }
        }
        setViews(views);
    }

    @NonNull
    private FrameLayout getImageView(String url, final int position, String text) {
        ImageView imageView = new ImageView(getContext());
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerItemClickListener != null) {
                    onBannerItemClickListener.onItemClick(position);
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setFitsSystemWindows(true);
        if (defaultImage != 0){
            Glide.with(getContext()).load(url).placeholder(defaultImage).centerCrop().into(imageView);
        }else {
            Glide.with(getContext()).load(url).centerCrop().into(imageView);
        }

        FrameLayout relativeLayout = new FrameLayout(getContext());
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setColorFilter(bannerImgColor);
        relativeLayout.addView(imageView);

        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(50, 0, 0, 50);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(banner_Text_Color);
        textView.setTextSize(banner_Text_Size);
        textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
        relativeLayout.addView(textView);
        return relativeLayout;
    }


    private void setViews(final List<View> views) {

        pager = new ViewPager(getContext());

        addView(pager);
        setSliderTransformDuration(scrollDuration);

        indicatorContainer = new LinearLayout(getContext());
        indicatorContainer.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        switch (indicatorPosition) {
            case centerBottom:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case centerTop:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case leftBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case leftTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case rightBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case rightTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }

        params.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);

        addView(indicatorContainer, params);


        for (int i = 0; i < itemCount; i++) {
            ImageView indicator = new ImageView(getContext());
            indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            indicator.setPadding(indicatorSpace, indicatorSpace, indicatorSpace, indicatorSpace);
            indicator.setImageDrawable(unSelectedDrawable);
            indicatorContainer.addView(indicator);
        }
        LoopPagerAdapter pagerAdapter = new LoopPagerAdapter(views);
        pager.setAdapter(pagerAdapter);

        int targetItemPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % itemCount;
        pager.setCurrentItem(targetItemPosition);
        switchIndicator(targetItemPosition % itemCount);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switchIndicator(position % itemCount);
            }
        });
        startAutoPlay();

    }

    public void setSliderTransformDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), null, duration);
            mScroller.set(pager, scroller);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public void startAutoPlay() {
        stopAutoPlay();
        if (isAutoPlay) {
            handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }


    public void stopAutoPlay() {
        if (isAutoPlay) {
            handler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void switchIndicator(int currentPosition) {
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            ((ImageView) indicatorContainer.getChildAt(i)).setImageDrawable(i == currentPosition ? selectedDrawable : unSelectedDrawable);
        }
    }


    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    public interface OnBannerItemClickListener {
        void onItemClick(int position);
    }

    public class LoopPagerAdapter extends PagerAdapter {
        private List<View> views;

        public LoopPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {

            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (views.size() > 0) {

                View view = views.get(position % views.size());
                if (container.equals(view.getParent())) {
                    container.removeView(view);
                }
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            this(context, interpolator);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {

            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {

            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}


