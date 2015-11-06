package com.miris.net;

/**
 * Created by fantastic on 2015-11-03.
 */
public class AddressListData {
    String userId;
    String user_name;
    String user_age;
    String userImgurl;
    String user_rank;
    String user_email;
    String user_phonenumber;

    public AddressListData(String userId, String user_name, String user_age, String userImgurl,
                            String user_rank, String user_email, String user_phonenumber) {

        this.userId = userId;
        this.user_name = user_name;
        this.user_age = user_age;
        this.userImgurl = userImgurl;
        this.user_rank = user_rank;
        this.user_email = user_email;
        this.user_phonenumber = user_phonenumber;
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

    public void setuserImgurl(String userImgurl) {
        this.userImgurl = userImgurl;
    }

    public void setuser_rank(String  user_rank) {
        this.user_rank = user_rank;
    }

    public void setuser_email(String  user_email) {
        this.user_rank = user_email;
    }

    public void setuser_phonenumber(String  user_phonenumber) {
        this.user_phonenumber = user_phonenumber;
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

    public String getuserImgurl() {
        return userImgurl;
    }

    public String getuser_rank() {
        return user_rank;
    }

    public String getuser_email() {
        return user_email;
    }

    public String getuser_phonenumber() {
        return user_phonenumber;
    }
}
