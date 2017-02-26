package com.warkahot.smsapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by warkahot on 26-Feb-17.
 */
public class Search_page extends AppCompatActivity {

    RecyclerView search_recycler_view;
    EditText search_et;
    Button search_but;
    ArrayList<Sms> smses_with_search_string;
    ProgressDialog pd;
    Toolbar toolbar;

    public  void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.search_page);

        initialize_Ui_variables();
        onClickListeners();

    }

    public void initialize_Ui_variables()
    {
        search_but = (Button)findViewById(R.id.search_button);
        search_et = (EditText)findViewById(R.id.search_et);
        search_recycler_view = (RecyclerView)findViewById(R.id.search_recy_view);
        pd = new ProgressDialog(Search_page.this);
        pd.setMessage("Please Wait...");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public void onClickListeners()
    {
        search_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_et.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please enter a string to search ...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.show();
                    smses_with_search_string = search_for_list_of_smses_with_search_string(search_et.getText().toString());
                    initialize_recycler_view(search_et.getText().toString());
                    pd.dismiss();
                }
            }
        });
    }
    
    public void initialize_recycler_view(String search_string)
    {
        LinearLayoutManager lLayout = new LinearLayoutManager(getApplicationContext());
        search_recycler_view.setAdapter(new Search_Adapter(smses_with_search_string, getApplicationContext(),search_string));
        search_recycler_view.setHasFixedSize(true);
        search_recycler_view.setLayoutManager(lLayout);
    }
    
    public ArrayList<Sms> search_for_list_of_smses_with_search_string(String search_str)
    {
        ArrayList<Sms> sms_list = new ArrayList<>();
        final String SMS_URI_INBOX = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
            Cursor cur = getContentResolver().query(uri, projection, null,null,"date desc");
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


                    if(Pattern.compile(Pattern.quote(search_str), Pattern.CASE_INSENSITIVE).matcher(strbody).find())
                    {
                        Sms sms_obj = new Sms();

                        sms_obj.setAddress(strAddress);
                        sms_obj.setPerson(intPerson);
                        sms_obj.setMessage(strbody);
                        sms_obj.setDate(longDate);
                        sms_obj.setType(int_Type);

                        sms_list.add(sms_obj);
                    }
                   

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    
                }
            } else {
                Toast.makeText(getApplicationContext(), "You have no related messages...", Toast.LENGTH_LONG).show();
            } // end if
        }
        catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        Log.d("Oreder", "Sms list size = " + sms_list.size());
        return sms_list;
    }
}
