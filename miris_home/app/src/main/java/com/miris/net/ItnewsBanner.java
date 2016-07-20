package com.miris.net;

/**
 * Created by fantastic on 2016-07-19.
 */
public class ItnewsBanner {

    public String img;
    public String title;
    public String content;
    public String link;

    public ItnewsBanner(String img, String title, String content, String link) {

        this.img = img;
        this.title = title;
        this.content = content;
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getlink() {
        return link;
    }

    public void setlink(String link) {
        this.link = link;
    }
}
