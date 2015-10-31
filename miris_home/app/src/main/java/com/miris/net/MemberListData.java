package com.miris.net;

import android.graphics.Bitmap;

/**
 * Created by fantastic on 2015-09-22.
 */
public class MemberListData {

    String userId;
    String user_password;
    String user_name;
    String user_age;
    Bitmap user_img;
    String userImgurl;
    int user_TotalLike;
    int user_TotalCommit;
    int user_registernumber;
    String user_rank;
    String user_email;

    public MemberListData(String userId, String user_password, String user_name, String user_age,
                          Bitmap user_img, String userImgurl, int user_TotalLike,
                          int user_TotalCommit, int user_registernumber,
                          String user_rank, String user_email) {

        this.userId = userId;
        this.user_password = user_password;
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_img = user_img;
        this.userImgurl = userImgurl;
        this.user_TotalLike = user_TotalLike;
        this.user_TotalCommit = user_TotalCommit;
        this.user_registernumber = user_registernumber;
        this.user_rank = user_rank;
        this.user_email = user_email;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public void setuser_password(String user_password) {
        this.user_password = user_password;
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

    public void setuser_TotalLike(int  user_TotalLike) {
        this.user_TotalLike = user_TotalLike;
    }

    public void setuser_TotalCommit(int  user_TotalCommit) {
        this.user_TotalCommit = user_TotalCommit;
    }

    public void setuser_registernumber(int  user_registernumber) {
        this.user_registernumber = user_registernumber;
    }

    public void setuser_rank(String  user_rank) {
        this.user_rank = user_rank;
    }

    public void setuser_email(String  user_email) {
        this.user_rank = user_email;
    }

    public String getuserId() {
        return userId;
    }

    public String getuser_password() {
        return user_password;
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

    public int getuser_TotalLike() {
        return user_TotalLike;
    }

    public int getuser_TotalCommit() {
        return user_TotalCommit;
    }

    public int getuser_registernumber() {
        return user_registernumber;
    }

    public String getuser_rank() {
        return user_rank;
    }

    public String getuser_email() {
        return user_email;
    }

}
