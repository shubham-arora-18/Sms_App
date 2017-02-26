package com.warkahot.smsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by warkahot on 27-Feb-17.
 */
public class Activity_for_intent_problem extends AppCompatActivity {

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Log.d("Order", "App was resumed");
        if(getIntent().getStringExtra("refresh")!=null)
        {
            Log.d("Order","Got in 1");
            if(getIntent().getStringExtra("refresh").equals("yes"))
            {
                Log.d("Order","Got in 2");
                Intent i = new Intent(this,Conversations_list_MAIN.class);
                startActivity(i);
            }
        }
    }
}
