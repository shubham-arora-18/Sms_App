package com.warkahot.smsapp;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

/**
 * Created by warkahot on 25-Feb-17.
 */
public class Sms implements Serializable,Comparable<Sms> {

    String address;
    String message;
    Long date;
    String id;
    String read_state;
    String folder_name;
    int person;
    int type;

    public void setPerson(int person) {
        this.person = person;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRead_state(String read_state) {
        this.read_state = read_state;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public int compareTo(Sms another) {
        return new Date(another.date).compareTo(new Date(date));
    }
}
