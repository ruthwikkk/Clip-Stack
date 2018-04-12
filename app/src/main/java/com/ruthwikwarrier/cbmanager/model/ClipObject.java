package com.ruthwikwarrier.cbmanager.model;

import java.util.Date;

/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */

public class ClipObject {

    protected int id;
    protected String text;
    protected Date date;
    protected boolean star;

    public ClipObject(String text, Date date) {
        this.text = text;
        this.date = date;
        this.star = false;
    }
    public ClipObject(String text, Date date, boolean star) {
        this.text = text;
        this.date = date;
        this.star = star;
    }

    public ClipObject(int id, String text, Date date, boolean star) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.star = star;
    }

    public String getText() {
        return text;
    }
    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public  boolean isStarred() {
        return star;
    }
    public ClipObject setStarred(boolean isStarred) {
        this.star = isStarred;
        return this;
    }
}
