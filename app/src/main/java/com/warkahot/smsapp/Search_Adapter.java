package com.warkahot.smsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by warkahot on 26-Feb-17.
 */
public class Search_Adapter extends RecyclerView.Adapter<Conversation_holder> {

    ArrayList<Sms> sms_list;
    Context c;
    String searched_string ;

    public Search_Adapter(ArrayList<Sms> sms_list, Context c,String searched_string) {
        this.sms_list = sms_list;
        this.c = c;
        this.searched_string = searched_string;
    }

    @Override
    public Conversation_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.search_holder,null);
        Conversation_holder conv_holder = new Conversation_holder(v);
        return conv_holder;
    }

    @Override
    public void onBindViewHolder(Conversation_holder holder, int position) {

        String date_str,time_str,latest_msg;

        Sms latest_sms = sms_list.get(position);
        Date date = new Date(latest_sms.date);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        date_str = df2.format(date);
        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm a");
        time_str = formatDate.format(date);
        latest_msg = latest_sms.message;

        if(latest_sms.type==1)
            holder.name.setText("From : "+latest_sms.address);
        else if (latest_sms.type==2)
            holder.name.setText("To : "+latest_sms.address);
        highlight_searched_text_in_text_view(holder.first_message,searched_string,latest_msg);
        holder.date.setText(date_str);
        holder.time.setText(time_str);


    }

    @Override
    public int getItemCount() {
        return sms_list.size();
    }



    public void highlight_searched_text_in_text_view(TextView tv, String searched_string,String original_string)
    {
       /* Spannable spanText = Spannable.Factory.getInstance().newSpannable(original_string);
        int start_index = original_string.indexOf(searched_string);
        int end_index = original_string.lastIndexOf(searched_string);
        spanText.setSpan(new BackgroundColorSpan(0xFFFFFF00), start_index, end_index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

        original_string = original_string.replaceAll("(?i)"+searched_string,"<font color='red'>"+searched_string+"</font>");
        tv.setText(Html.fromHtml(original_string));
    }
}