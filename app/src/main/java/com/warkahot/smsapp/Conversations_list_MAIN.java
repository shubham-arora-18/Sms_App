package com.warkahot.smsapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Conversations_list_MAIN extends AppCompatActivity {

    RecyclerView recyc_view;
    Toolbar toolbar;
    ImageView new_sms_icon,search_icon;
    Background_service background_service;
    boolean is_binded_to_service;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    //Code to bind to service Start
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            background_service = ((Background_service.LocalService)service).get_service();
            is_binded_to_service = true;
            Log.d("Order", "Bound to Service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            is_binded_to_service = true;
            Log.d("Order","UnBound to Service");
        }
    };
    //Code to bind to service SEnd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversations_list);

        initialize_ui_elements();
        getPermissionToReadSms_list();
        onclickListeners();


        if(check_if_sms_permission_granted())
        {
            initialize_recycler_view();
        }

        start_background_service();
        bind_to_background_service();

    }

    public void initialize_ui_elements()
    {
        recyc_view = (RecyclerView)findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new_sms_icon = (ImageView)findViewById(R.id.new_sms_icon);
        search_icon = (ImageView)findViewById(R.id.search_icon);
    }

    public void onclickListeners()
    {
        new_sms_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Conversations_list_MAIN.this,New_message_page.class);
                startActivity(i);
            }
        });

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Conversations_list_MAIN.this,Search_page.class);
                startActivity(i);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadSms_list() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                initialize_recycler_view();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
                getPermissionToReadSms_list();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean check_if_sms_permission_granted()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return  true;
    }

    public void initialize_recycler_view()
    {
        LinearLayoutManager lLayout = new LinearLayoutManager(getApplicationContext());


        ArrayList<String> senders_address_list = get_senders_addresses_list();
       // Collections.sort(senders_address_list);
        //For sorting of message groups according to dates
        if(senders_address_list!=null && senders_address_list.size()!=0)
        {
            ArrayList<Conversation> conversations_list = get_conversations_list(senders_address_list);

            Collections.sort(conversations_list, new Comparator<Conversation>() {
                @Override
                public int compare(Conversation lhs, Conversation rhs) {
                    System.out.println("lhs date = " + lhs.date + " rhs date = " + rhs.date);
                    return rhs.compareTo(lhs);//Descending order arranging according to dates
                }

            });

            recyc_view.setAdapter(new Conversation_Adapter(conversations_list, getApplicationContext()));
            recyc_view.setHasFixedSize(true);
            recyc_view.setLayoutManager(lLayout);
        }
    }



    public ArrayList<String>  get_senders_addresses_list()
    {
        Uri uri = Uri.parse("content://sms/");
        Cursor c = getContentResolver().query(uri, new String[]{"Distinct address"}, null, null, "date desc");
        ArrayList <String> list;
        list= new ArrayList<String>();
        list.clear();
        int msgCount=c.getCount();
        if(c.moveToFirst()) {
            for(int ii=0; ii < msgCount; ii++) {
                String address = c.getString(c.getColumnIndexOrThrow("address")).toString();
                list.add(address);
                c.moveToNext();
            }
            System.out.println("List before = "+list);
        }

        list = combine_space_untrimmed_address_with_trimmed_addresses(list);
        System.out.println("List after = "+list);
        return  list;
    }



    public ArrayList<Conversation> get_conversations_list(ArrayList<String> senders_address)
    {
        ArrayList<Conversation> conversation_list = new ArrayList<>();
        for(int i =0 ;i<senders_address.size();i++)
        {
            String addrress  = senders_address.get(i);
            ArrayList<Sms> sms_list = get_only_first_sms_for_unique_address(addrress);
            System.out.println("sms list sie = " + sms_list.size());
            if(sms_list!=null && sms_list.size()!=0) {
                Conversation conversation_obj = new Conversation();
                if(addrress.contains("*"))
                    conversation_obj.setName(addrress);
                else
                    conversation_obj.setName(addrress);

                conversation_obj.setSms_list(sms_list);
                conversation_obj.setDate(sms_list.get(0).date);
                conversation_list.add(conversation_obj);
            }
        }
        return conversation_list;
    }

    public ArrayList<Sms> get_only_first_sms_for_unique_address(String address)
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
        System.out.println("addresses arr = "+addresses_arr);
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



                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                Toast.makeText(getApplicationContext(),"You have no new messages for address = "+address,Toast.LENGTH_LONG).show();
            } // end if
        }
        catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        Log.d("Oreder","Sms list size = "+sms_list.size());
        return sms_list;
    }

    public ArrayList<String> combine_space_untrimmed_address_with_trimmed_addresses(ArrayList<String> address_list)
    {

        ArrayList<String> trimmed_list = new ArrayList<>();
        Iterator iterator = address_list.iterator();
        while(iterator.hasNext())
        {
            String original_address = (String)iterator.next();
            String spaces_trimmed_address = original_address.replace(" ", "");

            if(!trimmed_list.contains(spaces_trimmed_address))
                trimmed_list.add(spaces_trimmed_address);
        }

        iterator = address_list.iterator();

        while(iterator.hasNext())
        {
            String original_address = (String)iterator.next();
            String spaces_trimmed_address = original_address.replace(" ", "");
            if(!original_address.equals(spaces_trimmed_address))
            {
                int index = trimmed_list.indexOf(spaces_trimmed_address);
                trimmed_list.set(index,spaces_trimmed_address+"*"+original_address);
            }
        }

        return trimmed_list;

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  initialize_recycler_view();
        Log.d("Order","App was resumed");
        if(getIntent().getStringExtra("refresh")!=null)
        {
            Log.d("Order","Got in 1");
            if(getIntent().getStringExtra("refresh").equals("yes"))
            {
                Log.d("Order","Got in 2");
                initialize_recycler_view();
            }
        }
    }

    public void start_background_service()
    {
        Intent i = new Intent(Conversations_list_MAIN.this,Background_service.class);
        startService(i);
    }

    public void bind_to_background_service()
    {
        Intent i = new Intent(Conversations_list_MAIN.this,Background_service.class);
        bindService(i, sc, Context.BIND_AUTO_CREATE);
    }






    /* public void get_sms_list()
    {
        List<Conversation> senders_list = new ArrayList<Conversation>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getApplicationContext().getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        Conversations_list_MAIN.this.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                if(senders_list.contains())

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setMessage(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setRead_state(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));

                /*
                public static final int MESSAGE_TYPE_ALL    = 0;
                public static final int MESSAGE_TYPE_INBOX  = 1;
                public static final int MESSAGE_TYPE_SENT   = 2;
                public static final int MESSAGE_TYPE_DRAFT  = 3;
                public static final int MESSAGE_TYPE_OUTBOX = 4;
                public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages
                public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send later
                 */
                /*if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolder_name("inbox");
                } else {
                    objSms.setFolder_name("sent");
                }

                lstSms.add(objSms);
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

        return lstSms;
    }*/

}
