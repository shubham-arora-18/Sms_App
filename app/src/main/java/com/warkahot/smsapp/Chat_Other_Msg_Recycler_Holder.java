package com.warkahot.smsapp;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by warkahot on 02-Feb-17.
 */
public class Chat_Other_Msg_Recycler_Holder extends RecyclerView.ViewHolder {

    TextView other_text,date,time;

    public Chat_Other_Msg_Recycler_Holder(View itemView) {
        super(itemView);
        other_text = (TextView)itemView.findViewById(R.id.text);
        date = (TextView)itemView.findViewById(R.id.date);
        time = (TextView)itemView.findViewById(R.id.time);
    }
}
