package com.miris.net;

import android.net.Uri;

/**
 * Created by fantastic on 2015-09-22.
 */
public class MemberData {

    Uri imgUrl;
    String editText;

    public MemberData(Uri imgUrl, String editText) {
        this.imgUrl= imgUrl;
        this.editText=editText;
    }

    public void setimgUrl(Uri imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void seteditText(String editText) {
        this.editText = editText;
    }

    public Uri getimgUrl() {
        return imgUrl;
    }

    public String geteditText() {
        return editText;
    }
}
