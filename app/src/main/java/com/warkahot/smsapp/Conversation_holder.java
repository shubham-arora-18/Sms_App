package com.warkahot.smsapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by warkahot on 25-Feb-17.
 */
public class Conversation_holder extends RecyclerView.ViewHolder {

    TextView name,first_message,date,time;
    CardView cardView;

    public Conversation_holder(View itemView) {
        super(itemView);

        name = (TextView)itemView.findViewById(R.id.sender_name);
        first_message = (TextView)itemView.findViewById(R.id.latest_msg);
        date = (TextView)itemView.findViewById(R.id.date);
        time = (TextView)itemView.findViewById(R.id.time);
        cardView = (CardView)itemView.findViewById(R.id.conversation_card_view);
    }
}
