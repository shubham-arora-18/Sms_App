package com.warkahot.smsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by warkahot on 25-Feb-17.
 */
public class Chat_page extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView address ;
    Toolbar toolbar;
    String address_str;
    String trimmed_address ;
    EditText write_msg_et;
    ImageView send_button;
    ArrayList<Sms> sms_list;
    IntentFilter filter;
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.chat_screen);

        initialize_Ui_variables();
        get_sms_list();
        initialize_recyclerView();
        onClickListeners();
        setup_for_local_broadcast_reciever_for_messages();
        
    }

    public void initialize_Ui_variables()
    {
        recyclerView = (RecyclerView)findViewById(R.id.chat_recyc_view);
        address = (TextView)findViewById(R.id.address);
        write_msg_et = (EditText)findViewById(R.id.write_msg_et);
        send_button = (ImageView)findViewById(R.id.send_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onClickListeners()
    {
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!write_msg_et.getText().toString().equals(""))
                    send_message(trimmed_address, write_msg_et.getText().toString());

            }
        });
    }
    
    public void get_sms_list()
    {
        Bundle b = getIntent().getExtras();
        address_str = ((Conversation)b.getSerializable("conversation")).name;

        if(address_str.contains("*"))
        {
            trimmed_address = address_str.substring(0,address_str.indexOf('*'));
        }
        else
            trimmed_address = address_str;

         address.setText(trimmed_address);

        sms_list = get_all_sms_for_unique_address(address_str);
        Collections.sort(sms_list);
    }
    
    public void initialize_recyclerView()
    {
        LinearLayoutManager lLayout = new LinearLayoutManager(getApplicationContext());

        lLayout.setStackFromEnd(true);
        recyclerView.setAdapter(new Chat_Adapter(sms_list, getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
    }

    public ArrayList<Sms> get_all_sms_for_unique_address(String address)
    {
        String addresses_arr[] = new String[2];
        if(address.contains("*"))
        {
            addresses_arr[0] = address.substring(0,address.indexOf("*"));
            addresses_arr[1] = address.substring(address.indexOf("*")+1,address.length());
        }
        else
        {
            addresses_arr[0] = address;
            addresses_arr[1] = address;
        }
        System.out.println("addresses arr = " + addresses_arr);
        // StringBuilder smsBuilder = new StringBuilder();
        ArrayList<Sms> sms_list = new ArrayList<>();
        final String SMS_URI_INBOX = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
            //Cursor cur = getContentResolver().query(uri, projection, "address='"+address+"'", null, "date desc");
            Cursor cur = getContentResolver().query(uri, projection, "address IN(?,?)",addresses_arr,"date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                do {

                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);

                    Sms sms_obj = new Sms();

                    sms_obj.setAddress(strAddress);
                    sms_obj.setPerson(intPerson);
                    sms_obj.setMessage(strbody);
                    sms_obj.setDate(longDate);
                    sms_obj.setType(int_Type);

                    sms_list.add(sms_obj);

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                Toast.makeText(getApplicationContext(), "You have no new messages for address = " + address, Toast.LENGTH_LONG).show();
            } // end if
        }
        catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        Log.d("Oreder", "Sms list size = " + sms_list.size());
        return sms_list;
    }

    public void send_message(String address,String msg)
    {
        SmsManager smsManager = SmsManager.getDefault();
        if(address.matches("^[0-9\\+]*$"))
        {
            smsManager.sendTextMessage(address, null, msg, null, null);
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            add_a_temporary_message(msg);
            initialize_recyclerView();
        }
        else
            Toast.makeText(this, "You cannot send Message to this number!", Toast.LENGTH_SHORT).show();

    }

    public void add_a_temporary_message(String msg)
    {
        Sms temp_sms = new Sms();
        temp_sms.setMessage(msg);
        temp_sms.setType(2);
        Calendar c = Calendar.getInstance();
        long time = c.getTimeInMillis();
        temp_sms.setDate(time);
        sms_list.add(0,temp_sms);
        write_msg_et.setText("");

    }

    public void setup_for_local_broadcast_reciever_for_messages()
    {
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Brd Rcv", "Broadcast Reviever was fired...");

            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                get_sms_list();
                initialize_recyclerView();
            }
        }
    };
}
