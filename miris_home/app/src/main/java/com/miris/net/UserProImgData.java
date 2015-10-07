package com.miris.net;

/**
 * Created by fantastic on 2015-09-22.
 */
public class UserProImgData {

    String userId;
    String user_name;
    String user_age;
    String user_img_url;

    public UserProImgData(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public void setuser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public String getuser_img_url() {
        return user_img_url;
    }
}
