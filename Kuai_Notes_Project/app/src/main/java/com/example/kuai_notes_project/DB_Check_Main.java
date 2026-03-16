package com.example.kuai_notes_project;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "check_main")
public class DB_Check_Main {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long dateCreated;
    public long dateModified;
    public long dateCompeted;
    public String note;
    public int pin;
    public long reminder;
    public int reminder_type;
    public int reminder_interval;
    public int deleted;
    public int category;
    public int expire_days;
    public boolean checked;
    public boolean has_sub_checks;
}
