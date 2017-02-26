package com.warkahot.smsapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by warkahot on 25-Feb-17.
 */
public class Conversation implements Comparable<Conversation>,Serializable{

    String number,name;
    ArrayList<Sms> sms_list;
    long date;

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSms_list(ArrayList<Sms> sms_list) {
        this.sms_list = sms_list;
    }

    public void setDate(long date) {
        this.date = date;
    }


    @Override
    public int compareTo(Conversation another) {
        return new Date(date).compareTo(new Date(another.date));
    }
}
