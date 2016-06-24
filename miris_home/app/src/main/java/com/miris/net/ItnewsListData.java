package com.miris.net;

/**
 * Created by fantastic on 2016-06-16.
 */
public class ItnewsListData {

    public String img;
    public String title;
    public String content;
    public String link;
    public String dueDate;
    public String author;

    public ItnewsListData(String img, String title, String content, String link, String dueDate, String author) {

        this.img = img;
        this.title = title;
        this.content = content;
        this.link = link;
        this.dueDate = dueDate;
        this.author = author;
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

    public String getdueDate() {
        return dueDate;
    }

    public void setdueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getauthor() {
        return author;
    }

    public void setauthor(String author) {
        this.author = author;
    }
}
