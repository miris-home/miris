package com.miris.ui.cal;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miris.ui.activity.HLog;
import com.miris.ui.activity.MConfig;

import com.miris.R;

/**
 * View to display a day
 * @author Brownsoo
 *
 */
public class OneDayView extends RelativeLayout {
 
    private static final String TAG = MConfig.TAG;
    private static final String NAME = "OneDayView";
    private final String CLASS = NAME + "@" + Integer.toHexString(hashCode());
    
    /** number text field */
    private TextView dayTv;
    /** message text field*/
    private TextView msgTv;
    /** Weather icon */
    private ImageView weatherIv;
    /** Value object for a day info */
    private OneDayData one;

    /**
     * OneDayView constructor
     * @param context
     */
    public OneDayView(Context context) {
        super(context);
        init(context);
 
    }

    /**
     * OneDayView constructor for xml
     * @param context
     * @param attrs
     */
    public OneDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
 
    private void init(Context context)
    {

        View v = View.inflate(context, R.layout.oneday, this);
        
        dayTv = (TextView) v.findViewById(R.id.onday_dayTv);
        weatherIv = (ImageView) v.findViewById(R.id.onday_weatherIv);
        msgTv = (TextView) v.findViewById(R.id.onday_msgTv);
        one = new OneDayData();
        
    }
    
    /**
     * Set a day
     * @param year 4 digits of a year
     * @param month Calendar.JANUARY ~ Calendar.DECEMBER
     * @param day day of month
     */
    public void setDay(int year, int month, int day) {
        this.one.cal.set(year, month, day);
    }

    /**
     * Set a day
     * @param cal Calendar instance
     */
    public void setDay(Calendar cal) {
        this.one.setDay((Calendar) cal.clone());
    }

    /**
     * Set a day
     * @param one OneDayData instance
     */
    public void setDay(OneDayData one) {
        this.one = one;
    }
    
    /**
     * Get a day info
     * @return OneDayData instance
     */
    public OneDayData getDay() {
        return one;
    }

    /**
     * Set the message to display
     * @param msg message
     */
    public void setMsg(String msg){
        one.setMessage(msg);
    }

    /**
     * Get the message is displaying
     * @return message
     */
    public CharSequence getMsg(){
        return  one.getMessage();
    }

    /**
     * Returns the value of the given field after computing the field values by
     * calling {@code complete()} first.
     * 
     * @param field Calendar.YEAR or Calendar.MONTH or Calendar.DAY_OF_MONTH
     *
     * @throws IllegalArgumentException
     *                if the fields are not set, the time is not set, and the
     *                time cannot be computed from the current field values.
     * @throws ArrayIndexOutOfBoundsException
     *                if the field is not inside the range of possible fields.
     *                The range is starting at 0 up to {@code FIELD_COUNT}.
     */
    public int get(int field) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return one.get(field);
    }

    /**
     * Set weather
     * @param weather Weather instance
     */
    public void setWeather(MomusWeather.Weather weather) {
        this.one.setWeather(weather);
    }
    
    /**
     * Updates UI upon the value object.
     */
    public void refresh() {
        
        HLog.d(TAG, CLASS, "refresh");
        
        dayTv.setText(String.valueOf(one.get(Calendar.DAY_OF_MONTH)));
        msgTv.setText((one.getMessage()==null)?"":one.getMessage());
        switch(one.weather) {
        case CLOUDY :
        case SUN_CLOUND :
            weatherIv.setImageResource(R.drawable.cloudy);
            break;
        case RAINNY :
            weatherIv.setImageResource(R.drawable.rainy);
            break;
        case SNOW :
            weatherIv.setImageResource(R.drawable.snowy);
            break;
        case SUNSHINE :
            weatherIv.setImageResource(R.drawable.sunny);
            break;
        }
        
    }
    
}