package com.miris.net;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by fantastic on 2015-09-22.
 */
public class MemberData {

    Uri imgUrl;
    String editText;
    Bitmap imgBitmap;

    public MemberData(Uri imgUrl, String editText, String str) {
        this.imgUrl= imgUrl;
        this.editText=editText;
    }

    public MemberData(Bitmap imgBitmap, String editText) {
        this.imgBitmap = imgBitmap;
        this.editText = editText;
    }
    public void setimgUrl(Uri imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setimgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }
    public void seteditText(String editText) {
        this.editText = editText;
    }

    public Uri getimgUrl() {
        return imgUrl;
    }

    public Bitmap getimgBitmap() {
        return imgBitmap;
    }
    public String geteditText() {
        return editText;
    }
}
