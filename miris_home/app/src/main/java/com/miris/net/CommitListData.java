package com.miris.net;

import java.util.Date;

/** 
 * Created by fantastic on 2015-09-22.
 */
public class CommitListData {

    String objId;
    String userEditText;
    String user_img_url;
    String user_name;
    Date user_createDate;

    public CommitListData(String objId, String userEditText,
                          String user_img_url, String user_name, Date user_createDate) {
        this.objId = objId;
        this.userEditText = userEditText;
        this.user_img_url = user_img_url;
        this.user_name = user_name;
        this.user_createDate = user_createDate;
    }

    public void setobjId(String objId) {
        this.objId = objId;
    }

    public void setuserEditText(String userEditText) {
        this.userEditText = userEditText;
    }

    public void setuser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public void setuser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setuser_createDate(Date user_createDate) {
        this.user_createDate = user_createDate;
    }

    public String getobjId() {
        return objId;
    }

    public String getuserEditText() {
        return userEditText;
    }

    public String getuser_img_url() {
        return user_img_url;
    }

    public String getuser_name() {
        return user_name;
    }

    public Date getuser_createDate() {
        return user_createDate;
    }
}
