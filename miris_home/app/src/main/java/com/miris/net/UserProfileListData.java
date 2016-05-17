package com.miris.net;

/**
 * Created by fantastic on 2015-09-22.
 */
public class UserProfileListData {

    String userId;
    String user_name;
    String user_age;
    String user_img_url;
    int user_TotalLike;
    int user_TotalCommit;
    int user_registernumber;
    String user_rank;

    public UserProfileListData(String userId, String user_name, String user_age, String user_img_url,
                               int user_TotalLike, int user_TotalCommit,
                               int user_registernumber, String user_rank) {
        this.userId = userId;
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_img_url = user_img_url;
        this.user_TotalLike = user_TotalLike;
        this.user_TotalCommit = user_TotalCommit;
        this.user_registernumber = user_registernumber;
        this.user_rank = user_rank;
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

    public void setuser_TotalLike(int user_TotalLike) {
        this.user_TotalLike = user_TotalLike;
    }

    public void setuser_TotalCommit(int user_TotalCommit) {
        this.user_TotalCommit = user_TotalCommit;
    }

    public void setuser_registernumber(int user_registernumber) {
        this.user_registernumber = user_registernumber;
    }

    public void setuser_rank(String user_rank) {
        this.user_rank = user_rank;
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

}
