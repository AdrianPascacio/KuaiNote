package com.example.kuai_notes_project;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DB_Notes extends SQLiteOpenHelper {
    public DB_Notes(@Nullable Context context) {
        super(context, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB_N) {

        //DB_N.execSQL("create Table Notes(date TEXT, TIME TEXT, title TEXT, note TEXT, primary key (date))");
        DB_N.execSQL("create Table Notes(date TEXT, title TEXT, note TEXT, pin INTEGER, reminder TEXT, primary key (date))");

    }
    //!!--- cambio de pin para recibir booleano

    @Override
    public void onUpgrade(SQLiteDatabase DB_N, int oldVersion, int newVersion) {

        DB_N.execSQL("drop Table if exists Notes");

    }

    public Boolean Insert_Note(String current_date, String title,  String note, Integer pin){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder","");

        long result = DB_N.insert("Notes", null,contentValues);
        if (result == -1){
            Log.d("Inside DB_Notes","Insert_Note: NOT inserted");
            return false;
        }else{
            Log.d("Inside DB_Notes","Insert_Note: Note Inserted Satisfactorily");
            return true;
        }
    }

    public Boolean Modify_Note(String previous_date, String current_date, String title, String note, Integer pin){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        //!!----Verify if the reminder is needed
        contentValues.put("reminder","");

        //Uso de try-finally como manejo estandar de recursos que necesitan ser cerrados como "Cursor"
        //try (Cursor cursor = DB_N.rawQuery("select date, time, title, note from Notes where date = ? and time = ?", new String[]{date, time})) {
        //El uso del metodo update retorna el numero de filas modificadas por lo que no hace falta inicializar un cursor para vericar si la modificacion se realizo

        //long result = DB_N.update("Notes", contentValues, "date=? and time=?", new String[]{date, time});
        int result = DB_N.update("Notes", contentValues, "date=? ", new String[]{previous_date});

        if (result > 0) {
            Log.d("Inside DB_Notes", "Modify_Note: Note modified");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Notes", "Modify_Note: Note NOT Found");
            if (result == -1) Log.d("Inside DB_Notes", "Modify_Note: Error");
            return false;
        }
    }
    public Boolean Modify_Pin_Status(String previous_date,  Integer pin){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_N.update("Notes", contentValues, "date=? ", new String[]{previous_date});

        if (result > 0) {
            Log.d("Inside DB_Notes", "Modify_Pin_Status: Note modified");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Notes", "Modify_Pin_Status: Note NOT Found");
            if (result == -1) Log.d("Inside DB_Notes", "Modify_Pin_Status: Error");
            return false;
        }
    }

    public Cursor get_All_Notes(){
        SQLiteDatabase DB_N = this.getReadableDatabase();
        //!!Se debe agragar en select el "*" para indicar que traiga todo incluyendo pin y reminder cuando corresponda
        //Cursor cursor = DB_N.rawQuery("select date, title, note from Notes order by date DESC", null);
        Cursor cursor = DB_N.rawQuery("select * from Notes order by pin DESC, date DESC", null);
        return cursor;
    }
    public ArrayList<Note> getAllNotes(){
        ArrayList<Note> noteList = new ArrayList<>();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes order by pin DESC, date DESC", null)) {
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                if (cursor.moveToFirst()) {
                    do {
                        Note note = new Note();
                        note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                        note.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                        note.setPin(cursor.getInt(cursor.getColumnIndexOrThrow("pin")));
                        note.setReminder(cursor.getString(cursor.getColumnIndexOrThrow("reminder")));
                        noteList.add(note);
                    } while (cursor.moveToNext());
                }
            }
        }
        return noteList;
    }

    public Cursor get_Specific_Note(String date){
        SQLiteDatabase DB_N = this.getReadableDatabase();
        //Cursor cursor = DB_N.rawQuery("select date, time, title, note from Notes where date = ? and time = ?", new String[] {date, time});
        //Cursor cursor = DB_N.rawQuery("select date, title, note from Notes where date = ?", new String[] {date});
        Cursor cursor = DB_N.rawQuery("select * from Notes where date = ?", new String[] {date});
        return cursor;
    }
    public Note getASpecificNote(String date){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where date = ?", new String[] {date}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
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
    public int get_Specific_Note_Sorted_by_Pin_and_Date(String date){
        int New_Position = 0;
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select date from Notes order by pin DESC, date DESC", null)) {
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

    //Get Array of Notes:

    public Boolean Delete_Specific_Note(String date){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        //!!Se debe agragar en select el "*" para indicar que traiga todo incluyendo pin y reminder cuando corresponda


        int result = DB_N.delete("Notes",  "date=? ", new String[]{date});

        if (result > 0) {
            Log.d("Inside DB_Notes", "Delete_Note: Note Deleted");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Notes", "Delete_Note: NOT Found");
            if (result == -1) Log.d("Inside DB_Notes", "Delete_Note: Error");
            return false;
        }
    }


}
