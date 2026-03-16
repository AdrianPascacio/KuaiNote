package com.example.kuai_notes_project;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "check_sub",
foreignKeys = @ForeignKey(entity = DB_Check_Main.class,
parentColumns =  "id",
childColumns = "parent_check",
onDelete = ForeignKey.CASCADE))
public class DB_Check_Sub {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "parent_check")
    public int parentCheckId;

    public String note;
    public boolean checked;
    public int position;
}