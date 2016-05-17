package com.miris.net;

import java.util.Date;

/**
 * Created by fantastic on 2015-11-03.
 */
public class CalendarListData {
    String userId;
    String user_name;
    Date user_calendar;
    String user_text;
    String user_public;
    String user_holiday;

    public CalendarListData(String userId, String user_name, Date user_calendar,
                                     String user_text, String user_public, String user_holiday) {

        this.userId = userId;
        this.user_name = user_name;
        this.user_calendar = user_calendar;
        this.user_text = user_text;
        this.user_public = user_public;
        this.user_holiday = user_holiday;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public void setuser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setuser_calendar(Date user_calendar) {
        this.user_calendar = user_calendar;
    }

    public void setuser_text(String user_text) {
        this.user_text = user_text;
    }

    public void setuser_public(String  user_public) {
        this.user_public = user_public;
    }

    public void setuser_holiday(String  user_holiday) {
        this.user_holiday = user_holiday;
    }

    public String getuserId() {
        return userId;
    }

    public String getuser_name() {
        return user_name;
    }

    public Date getuser_calendar() {
        return user_calendar;
    }

    public String getuser_text() {
        return user_text;
    }

    public String getuser_public() {
        return user_public;
    }

    public String getuser_holiday() {
        return user_holiday;
    }
}
