package com.warkahot.smsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by warkahot on 25-Feb-17.
 */
public class Conversation_Adapter extends RecyclerView.Adapter<Conversation_holder> {

    ArrayList<Conversation> conversations_list ;
    Context c;

    public Conversation_Adapter(ArrayList<Conversation> conversations_list, Context c) {
        this.conversations_list = conversations_list;
        this.c = c;
    }

    @Override
    public Conversation_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.conversation_view_holder,null);
        Conversation_holder conv_holder = new Conversation_holder(v);
        return conv_holder;
    }

    @Override
    public void onBindViewHolder(Conversation_holder holder, int position) {

        String date_str,time_str,latest_msg;
        Conversation conversation = conversations_list.get(position);
        Sms latest_sms = conversation.sms_list.get(0);
        Date date = new Date(latest_sms.date);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        date_str = df2.format(date);
        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm a");
        time_str = formatDate.format(date);
        latest_msg = latest_sms.message;
        latest_msg = trim_and_return_message(latest_msg);


        if(conversation.name.contains("*"))
            holder.name.setText(conversation.name.substring(0, conversation.name.indexOf("*")));
        else
            holder.name.setText(conversation.name);
        holder.first_message.setText(latest_msg);
        holder.date.setText(date_str);
        holder.time.setText(time_str);

        holder_onclick_listener(holder,position);

    }

    @Override
    public int getItemCount() {
        return conversations_list.size();
    }


    public String  trim_and_return_message(String str)
    {
        if(str.length()>100)
        {
            str = str.substring(0,97);
            str += "...";
        }
        return  str;

    }

    public void holder_onclick_listener(Conversation_holder holder , final int position)
    {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Conversation conversation = conversations_list.get(position);
                b.putSerializable("conversation",conversation);
                Intent i = new Intent(c.getApplicationContext(),Chat_page.class);
                i.putExtras(b);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);
            }
        });
    }
}
