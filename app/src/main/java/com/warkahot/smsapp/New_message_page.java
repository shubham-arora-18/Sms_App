package com.warkahot.smsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by warkahot on 26-Feb-17.
 */
public class New_message_page extends AppCompatActivity {

    EditText to_et,msg_et;
    Button send_but;
    Toolbar toolbar;

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.new_message_page);

        initialize_Ui_variables();
        onclickListeners();

    }

    public void initialize_Ui_variables()
    {
        to_et = (EditText)findViewById(R.id.to_et);
        msg_et = (EditText)findViewById(R.id.enter_msg_et);
        send_but = (Button)findViewById(R.id.send_but);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onclickListeners()
    {
        send_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_if_to_field_contains_address())
                {
                    if(check_if_msg_entered())
                    {
                        String address = to_et.getText().toString();
                        String msg = msg_et.getText().toString();
                        send_message(address,msg);
                    }
                }

            }
        });
    }

    public boolean check_if_to_field_contains_address()
    {
        if(!to_et.getText().toString().equals(""))
            return true;
        return false;
    }

    public boolean check_if_msg_entered()
    {
        if(!msg_et.getText().toString().equals(""))
            return true;
        return false;
    }

    public void send_message(String address,String msg)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address, null, msg, null, null);
        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
        msg_et.setText("");
    }


}
