package com.miris.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.miris.ui.comp.CalendarDayEvent;
import com.miris.ui.comp.CompactCalendarController;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompactCalendarView extends View {

    private CompactCalendarController compactCalendarController;
    private GestureDetectorCompat gestureDetector;
    private CompactCalendarViewListener listener;
    private boolean shouldScroll = true;

    public interface CompactCalendarViewListener {
        public void onDayClick(Date dateClicked);
        public void onMonthScroll(Date firstDayOfNewMonth);
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Date onDateClicked = compactCalendarController.onSingleTapConfirmed(e);
            invalidate();
            if(listener != null && onDateClicked != null){
                listener.onDayClick(onDateClicked);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return compactCalendarController.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            compactCalendarController.onFling(e1, e2, velocityX, velocityY);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(shouldScroll) {
                compactCalendarController.onScroll(e1, e2, distanceX, distanceY);
                invalidate();
            }
            return true;
        }
    };

    public CompactCalendarView(Context context) {
        this(context, null);
    }

    public CompactCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompactCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        compactCalendarController = new CompactCalendarController(new Paint(), new OverScroller(getContext()),
                new Rect(), attrs, getContext(),  Color.argb(255, 233, 84, 81), Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219));
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
    }

    public void setLocale(Locale locale){
        compactCalendarController.setLocale(locale);
        invalidate();
    }

    public void setUseThreeLetterAbbreviation(boolean useThreeLetterAbbreviation){
        compactCalendarController.setUseWeekDayAbbreviation(useThreeLetterAbbreviation);
        invalidate();
    }

    public void drawSmallIndicatorForEvents(boolean shouldDrawDaysHeader){
        compactCalendarController.showSmallIndicator(shouldDrawDaysHeader);
    }

    public void setDayColumnNames(String[] dayColumnNames){
       compactCalendarController.setDayColumnNames(dayColumnNames);
    }

    public int getHeightPerDay(){
        return compactCalendarController.getHeightPerDay();
    }

    public void setListener(CompactCalendarViewListener listener){
        this.listener = listener;
    }

    public Date getFirstDayOfCurrentMonth(){
        return compactCalendarController.getFirstDayOfCurrentMonth();
    }

    public void setCurrentDate(Date dateTimeMonth){
        compactCalendarController.setCurrentDate(dateTimeMonth);
        invalidate();
    }

    public int getWeekNumberForCurrentMonth(){
        return compactCalendarController.getWeekNumberForCurrentMonth();
    }

    public void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader){
        compactCalendarController.setShouldDrawDaysHeader(shouldDrawDaysHeader);
    }

    @Deprecated
    public void addEvent(CalendarDayEvent event){
        addEvent(event, false);
    }

    public void addEvent(CalendarDayEvent event, boolean shouldInvalidate){
        compactCalendarController.addEvent(event);
        if(shouldInvalidate){
            invalidate();
        }
    }

    public void addEvents(List<CalendarDayEvent> events){
       compactCalendarController.addEvents(events);
       invalidate();
    }

    @Deprecated
    public void removeEvent(CalendarDayEvent event){
        removeEvent(event, false);
    }

    public void removeEvent(CalendarDayEvent event, boolean shouldInvalidate){
        compactCalendarController.removeEvent(event);
        if(shouldInvalidate){
            invalidate();
        }
    }

    public void removeEvents(List<CalendarDayEvent> events){
        compactCalendarController.removeEvents(events);
        invalidate();
    }

    public void showNextMonth(){
        compactCalendarController.showNextMonth();
        invalidate();
        if(listener != null){
             listener.onMonthScroll(compactCalendarController.getFirstDayOfCurrentMonth());
        }
    }

    public void showPreviousMonth(){
        compactCalendarController.showPreviousMonth();
        invalidate();
        if(listener != null){
             listener.onMonthScroll(compactCalendarController.getFirstDayOfCurrentMonth());
        }
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        int width = MeasureSpec.getSize(parentWidth);
        int height = MeasureSpec.getSize(parentHeight);
        if(width > 0 && height > 0) {
            compactCalendarController.onMeasure(width, height, getPaddingRight(), getPaddingLeft());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        compactCalendarController.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(compactCalendarController.computeScroll()){
            invalidate();
        }
    }

    public void shouldScrollMonth(boolean shouldDisableScroll){
        this.shouldScroll = shouldDisableScroll;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(compactCalendarController.onTouch(event) && shouldScroll){
            invalidate();
            if(listener != null){
                listener.onMonthScroll(compactCalendarController.getFirstDayOfCurrentMonth());
            }
            return true;
        }
        return gestureDetector.onTouchEvent(event);
    }

}
