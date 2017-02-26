package com.warkahot.smsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by warkahot on 02-Feb-17.
 */
public class Chat_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int My_msg = 1;
    private static final int Other_msg = 2;
    ArrayList<Sms> sms_list;
    Context c;

    public Chat_Adapter(ArrayList<Sms> list, Context c) {
        this.sms_list = list;
        this.c = c;
    }

    @Override
    public int getItemViewType(int position) {
        if(sms_list.get(sms_list.size()-1-position).type == 2)//1 means inbox and 2 means sent message
            return My_msg;
        else
            return Other_msg;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType)
        {
            case My_msg:
                View v = inflater.inflate(R.layout.chat_my_msg_holder,parent,false);
                viewHolder = new Chat_My_Msg_Recycler_Holder(v);
                break;
            case Other_msg:
                View v1 = inflater.inflate(R.layout.chat_other_msg_holder,parent,false);
                viewHolder = new Chat_Other_Msg_Recycler_Holder(v1);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        String date_str,time_str,message;
        Sms sms = sms_list.get(sms_list.size()-1-position);
        Date date = new Date(sms.date);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        date_str = df2.format(date);
        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm a");
        time_str = formatDate.format(date);
        message = sms.message;
        switch (holder.getItemViewType())
        {
            case My_msg:
                Chat_My_Msg_Recycler_Holder my_msg_holder = (Chat_My_Msg_Recycler_Holder)holder;
                my_msg_holder.my_text.setText(message);
                my_msg_holder.date.setText(date_str);
                my_msg_holder.time.setText(time_str);

                break;
            case Other_msg:
                Chat_Other_Msg_Recycler_Holder other_msg_holder = (Chat_Other_Msg_Recycler_Holder)holder;
                other_msg_holder.other_text.setText(message);
                other_msg_holder.date.setText(date_str);
                other_msg_holder.time.setText(time_str);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sms_list.size();
    }


}
