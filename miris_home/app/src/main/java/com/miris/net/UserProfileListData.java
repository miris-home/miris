package com.miris.net;

/**
 * Created by fantastic on 2015-09-22.
 */
public class UserProfileListData {

    String userId;
    String user_name;
    String user_age;
    String user_img_url;

    public UserProfileListData(String userId, String user_name, String user_age, String user_img_url) {
        this.userId = userId;
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_img_url = user_img_url;
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

    public void setuser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
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

    public String getuser_img_url() {
        return user_img_url;
    }

}
