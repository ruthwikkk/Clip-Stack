package com.ruthwikwarrier.cbmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */
@Entity(tableName = "cliphistory")
public class ClipObject {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "history")
    private String text;

    private Date date;
    private boolean star;

    @Ignore
    public ClipObject(String text, Date date) {
        this.text = text;
        this.date = date;
        this.star = false;
    }

    @Ignore
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }
}
