package com.miris.ui.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.ArrayList;
import com.miris.net.MessageListData;

import static com.miris.ui.activity.BaseActivity.messageData;

import com.miris.R;
/**
 * Created by miris on 2016-06-28.
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    /*추가해야할 사항
    * 1. 삭제버튼에 key 값을 넣어야 함.
    * */

    private Context context;
    private OnMsgItemClickListener onMsgItemClickListener;
    private ArrayList<MessageListData> searcharraylist;

    private int itemsCount = 1;
    private boolean animateItems = false;
    private String flag = "0";

    public MessageAdapter(Context context, ArrayList<MessageListData> items, String flag) {
        this.context = context;
        messageData = items;
        this.searcharraylist = new ArrayList<MessageListData>();
        this.searcharraylist.addAll(items);
        this.flag = flag;
    }

    public static class MsgViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.MsgTitle) TextView titleView; // 쪽지 타이틀 뷰
        @InjectView(R.id.MsgDate) TextView dateView;   // 쪽지 보낸 일자 뷰
        @InjectView(R.id.MsgId) TextView idView;       // 쪽지 보낸 아이디 뷰
        @InjectView(R.id.ivMsgImg) ImageView keyView;  // 쪽지 삭제 버튼 이미지(키 값)

        public MsgViewHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.ivMsgImg) {
            if (onMsgItemClickListener != null) {
                onMsgItemClickListener.onDeleteMsg(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.MsgTitle){
            onMsgItemClickListener.onDetailMsg(view, (Integer) view.getTag());
        } else if (viewId == R.id.MsgId){
            onMsgItemClickListener.onDetailMsg(view, (Integer) view.getTag());
        }
    }

    public interface OnMsgItemClickListener {
        public void onDeleteMsg(View v, int position);   // 메시지 삭제
        public void onDetailMsg(View v, int position);   // 메시지 상세 보기

    }


    /* onCreateViewHolder()
       : 데이터를 보여주는데 사용하는 뷰를 갖도록 초기화된 ViewHolder 객체를 생성하고 반환한다.
         이때 그뷰는 XML 레이아웃 파일을 인플레이트(Inflate)하여 생성함. */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_message, viewGroup, false);
        MsgViewHolder viewHolder = new MsgViewHolder(v);
        viewHolder.keyView.setOnClickListener(this);
        viewHolder.titleView.setOnClickListener(this);
        viewHolder.idView.setOnClickListener(this);
        return viewHolder;
    }

    /* onBindViewHolder()
     : 이 메서드는 두개의 파라미터를 받는다. onCreateViewHolder() 메서드에서 생성된 ViewHolder 객체와
       보여줄 리스트 항목을 나타내는 정수값이다. 이 메서드에서는 지정된 항목의 텍스트와 그래픽 데이터를 레이아웃 뷰에 넣은후
       그 객체를 RecylerView에 반환한다. 그럼으로써 RecylerView가 사용자에게 보여질 수 있다. */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MsgViewHolder holder = (MsgViewHolder) viewHolder;

        Log.i("titles", messageData.get(position).getContent());
        Log.i("date", messageData.get(position).getSendTime());
        Log.i("objectId", messageData.get(position).getObjectId());

        holder.titleView.setText(messageData.get(position).getContent());
        holder.titleView.setTag(position);
        holder.dateView.setText(messageData.get(position).getSendTime());
        if("2".equals(flag)){ // 보낸쪽지함에는 받는사람이 나오도록 함.
            holder.idView.setText(messageData.get(position).getReceiptId());
        } else {
            holder.idView.setText(messageData.get(position).getSendId());
        }
        holder.idView.setTag(position);
        //holder.keyView.setTag(messageData.get(position).getObjectId());
        holder.keyView.setTag(position);
    }

    public void setOnMsgItemClickListener(OnMsgItemClickListener onMsgItemClickListener) {
        this.onMsgItemClickListener = onMsgItemClickListener;
    }

    /* getItemCount(): 리스트에 보여줄 항목의 개수를 반환하는 메서드 */
    @Override
    public int getItemCount() { return messageData.size(); }

    public void updateItems(boolean animated) {
        itemsCount = messageData.size();
        animateItems = animated;
        /*데이터가 변경되어 Adapter에 연결된 리스트 뷰를 갱신*/
        notifyDataSetChanged();
    }

}