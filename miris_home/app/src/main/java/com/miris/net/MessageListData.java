package com.miris.net;

/**
 * Created by miris on 2016-07-12.
 */
public class MessageListData {
    String sendId;
    String receiptId;
    String sendName;
    String content;
    String receiptYn;
    String sendDeleteYn;
    String receiptDeleteYn;
    String sendTime;
    String objectId;

    public MessageListData(String receiptId, String sendId, String sendName, String content, String receiptYn,
                           String receiptDeleteYn, String sendDeleteYn, String sendTime, String objectId) {

        this.sendId = sendId;
        this.sendName = sendName;
        this.receiptId = receiptId;
        this.content = content;
        this.receiptYn = receiptYn;
        this.receiptDeleteYn = receiptDeleteYn;
        this.sendDeleteYn = sendDeleteYn;
        this.sendTime = sendTime;
        this.objectId = objectId;
    }

    public void setsendId(String sendId) { this.sendId = sendId;}

    public void setreceiptId(String receiptId) { this.receiptId = receiptId; }

    public void setsendName(String sendName) { this.sendName = sendName; }

    public void setcontent(String content) { this.content = content; }

    public void setreceiptYn(String receiptYn) { this.receiptYn = receiptYn; }

    public void setsendDeleteYn(String sendDeleteYn) { this.sendDeleteYn = sendDeleteYn; }

    public void setreceiptDeleteYn(String receiptDeleteYn) { this.receiptDeleteYn = receiptDeleteYn; }

    public void setsendTime(String sendTime) { this.sendTime = sendTime; }

    public void setobjectId(String objectId) { this.objectId = objectId; }

    public String getSendId(){ return this.sendId;}

    public String getReceiptId(){ return this.receiptId;}

    public String getSendName(){ return this.sendName;}

    public String getContent(){ return this.content;}

    public String getReceiptYn(){ return this.receiptYn;}

    public String getSendDeleteYn(){ return this.sendDeleteYn;}

    public String getReceiptDeleteYn(){ return this.receiptDeleteYn;}

    public String getSendTime(){ return this.sendTime;}

    public String getObjectId(){ return this.objectId;}

}
