package com.miris.net;

import android.graphics.Bitmap;

/**
 * Created by fantastic on 2015-09-22.
 */
public class NoticeListData {

    String objId;
    String userid;
    String username;
    String date;
    String editText;
    int DoLike;
    Bitmap imgBitmap;
    Bitmap userimgBitmap;
    String imgPath;
    String user_public;

    public NoticeListData(String objId, String userid, String username, Bitmap userimgBitmap,
                          Bitmap imgBitmap, String editText, int DoLike, String date,
                          String user_public) {
        this.objId = objId;
        this.userid = userid;
        this.username = username;
        this.userimgBitmap = userimgBitmap;
        this.imgBitmap = imgBitmap;
        this.editText = editText;
        this.DoLike = DoLike;
        this.date = date;
        this.user_public = user_public;
    }

    public NoticeListData(String objId, String userid, String username, Bitmap userimgBitmap,
                          String editText, int DoLike, String date, String user_public) {
        this.objId = objId;
        this.userid = userid;
        this.username = username;
        this.userimgBitmap = userimgBitmap;
        this.editText = editText;
        this.DoLike = DoLike;
        this.date = date;
        this.user_public = user_public;
    }

    public NoticeListData(Bitmap userimgBitmap, Bitmap imgBitmap, String imgPath) {
        this.userimgBitmap = userimgBitmap;
        this.imgBitmap = imgBitmap;
        this.imgPath = imgPath;
    }

    public NoticeListData(String objId, String userid, String username, String editText,
                          int DoLike, String date, String user_public) {
        this.objId = objId;
        this.userid = userid;
        this.username = username;
        this.editText = editText;
        this.DoLike = DoLike;
        this.date = date;
        this.user_public = user_public;
    }

    public void setobjId(String objId) {
        this.objId = objId;
    }

    public void setuserid(String userid) {
        this.userid = userid;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public void setimgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public void setuserimgBitmap(Bitmap imgBitmap) {
        this.userimgBitmap = imgBitmap;
    }

    public void seteditText(String editText) {
        this.editText = editText;
    }

    public void setDoLike(int DoLike) {
        this.DoLike = DoLike;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public void setimgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setuser_public(String user_public) {
        this.user_public = user_public;
    }

    public String getobjId() {
        return objId;
    }

    public String getuserid() {
        return userid;
    }

    public String getusername() {
        return username;
    }

    public Bitmap getimgBitmap() {
        return imgBitmap;
    }

    public Bitmap getuserimgBitmap() {
        return userimgBitmap;
    }

    public String geteditText() {
        return editText;
    }

    public int getDoLike() {
        return DoLike;
    }

    public String getdate() {
        return date;
    }

    public String getimgPath() {
        return imgPath;
    }

    public String getuser_public() {
        return user_public;
    }
}
