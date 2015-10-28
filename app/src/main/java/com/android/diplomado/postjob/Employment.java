package com.android.diplomado.postjob;

import java.lang.reflect.Array;

/**
 * Created by Anbade on 08/10/2015.
 */
public class Employment {
    private int id;
    private String title;
    private String description;
    private String postedDate;

    public Employment(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public Array getContacts() {
        return contacts;
    }

    public void setContacts(Array contacts) {
        this.contacts = contacts;
    }

    private Array contacts;

}
