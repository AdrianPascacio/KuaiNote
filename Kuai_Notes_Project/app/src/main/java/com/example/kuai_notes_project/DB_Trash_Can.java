package com.example.kuai_notes_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class DB_Trash_Can extends SQLiteOpenHelper {
    public DB_Trash_Can(@Nullable Context context) {
        super(context, "deleted_notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB_TC) {
        DB_TC.execSQL("create Table Deleted_Notes(date TEXT, title TEXT, note TEXT, pin INTEGER, reminder TEXT, expire_days INTEGER, primary key (date))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB_TC, int oldVersion, int newVersion) {

        DB_TC.execSQL("drop Table if exists Deleted_Notes");

    }

    public Boolean Insert_Note(String date, String title,  String note, Integer pin, Integer expire_days){
        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder","");
        contentValues.put("expire_days",expire_days);

        long result = DB_TC.insert("Deleted_Notes", null,contentValues);
        if (result == -1){
            Log.d("Inside DB_Trash_Can","Insert_Note: NOT inserted");
            return false;
        }else{
            Log.d("Inside DB_Trash_Can","Insert_Note: Note Inserted Satisfactorily");
            return true;
        }
    }

    public Boolean Modify_Note(String previous_date, String current_date, String title, String note, Integer pin, Integer expire_days){

        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("expire_days",expire_days);
        contentValues.put("reminder","");


        //long result = DB_N.update("Notes", contentValues, "date=? and time=?", new String[]{date, time});
        int result = DB_TC.update("Deleted_Notes", contentValues, "date=? ", new String[]{previous_date});

        if (result > 0) {
            Log.d("Inside DB_Trash_Can", "Modify_Note: Note modified");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Trash_Can", "Modify_Note: Note NOT Found");
            if (result == -1) Log.d("Inside DB_Trash_Can", "Modify_Note: Error");
            return false;
        }
    }
    public Boolean Reduce_Note_Expire_Days(String previous_date, Integer expire_days){

        expire_days --;

        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("expire_days",expire_days);

        //long result = DB_N.update("Notes", contentValues, "date=? and time=?", new String[]{date, time});
        int result = DB_TC.update("Deleted_Notes", contentValues, "date=? ", new String[]{previous_date});

        if (result > 0) {
            Log.d("Inside DB_Trash_Can", "Modify_Note: Note modified");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Trash_Can", "Modify_Note: Note NOT Found");
            if (result == -1) Log.d("Inside DB_Trash_Can", "Modify_Note: Error");
            return false;
        }
    }
    public Cursor get_All_Notes(){
        SQLiteDatabase DB_TC = this.getReadableDatabase();
        Cursor cursor = DB_TC.rawQuery("select * from Deleted_Notes order by date DESC", null);
        return cursor;
    }
    public int get_Specific_Note_Sorted_by_Pin_and_Date(String date){
        int New_Position = 0;
        SQLiteDatabase DB_TC = this.getReadableDatabase();
        try (Cursor cursor = DB_TC.rawQuery("select date from Deleted_Notes order by pin DESC, date DESC", null)) {
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
                return New_Position;
            }else{
                while(cursor.moveToNext()){
                    if(Objects.equals(cursor.getString(0), date)){
                        Log.d("Read cursor_Notes", "Cursor Pin_Date : Position: " + cursor.getPosition());
                        return cursor.getPosition();
                    }
                }
            }
        }

        return New_Position;
    }
    public Note getASpecificNote(String date){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Deleted_Notes where date = ?", new String[] {date}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Deleted_Notes", "Cursor_Deleted_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    note.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                    note.setPin(cursor.getInt(cursor.getColumnIndexOrThrow("pin")));
                    note.setReminder(cursor.getString(cursor.getColumnIndexOrThrow("reminder")));
                }
            }
        }
        return note;
    }
    public int get_expire_Day(String dateOfNote) {
        int expire_day = 0;
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Deleted_Notes where date = ?", new String[] {dateOfNote}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Deleted_Notes", "Cursor_Deleted_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    expire_day = cursor.getInt(cursor.getColumnIndexOrThrow("expire_days"));
                }
            }
        }
        return expire_day;
    }
    public Boolean Modify_Pin_Status(String previous_date,  Integer pin){

        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_TC.update("Deleted_Notes", contentValues, "date=? ", new String[]{previous_date});

        if (result > 0) {
            Log.d("Inside DB_Trash_Can", "Modify_Pin_Status: Note modified");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Trash_Can", "Modify_Pin_Status: Note NOT Found");
            if (result == -1) Log.d("Inside DB_Trash_Can", "Modify_Pin_Status: Error");
            return false;
        }
    }

    //Get Array of Notes:

    public Boolean Delete_Specific_Note(String date){
        SQLiteDatabase DB_TC = this.getWritableDatabase();

        int result = DB_TC.delete("Deleted_Notes",  "date=? ", new String[]{date});

        if (result > 0) {
            Log.d("Inside DB_Trash_Can", "Delete_Note: Note Deleted");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Trash_Can", "Delete_Note: NOT Found");
            if (result == -1) Log.d("Inside DB_Trash_Can", "Delete_Note: Error");
            return false;
        }
    }


}
