package com.miris.net;

import android.graphics.Bitmap;

/**
 * Created by fantastic on 2015-09-22.
 */
public class MemberListData {

    String userId;
    String user_name;
    String user_age;
    Bitmap user_img;
    String userImgurl;

    public MemberListData(String userId, String user_name, String user_age,
                          Bitmap user_img, String userImgurl) {
        this.userId = userId;
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_img = user_img;
        this.userImgurl = userImgurl;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public void setuser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setuser_age(String user_age) {
        this.user_age = user_age;
    }

    public void setuser_img(Bitmap user_img) {
        this.user_img = user_img;
    }

    public void setuserImgurl(String userImgurl) {
        this.userImgurl = userImgurl;
    }

    public String getuserId() {
        return userId;
    }

    public String getuser_name() {
        return user_name;
    }

    public String getuser_age() {
        return user_age;
    }

    public Bitmap getuser_img() {
        return user_img;
    }

    public String getuserImgurl() {
        return userImgurl;
    }

}
