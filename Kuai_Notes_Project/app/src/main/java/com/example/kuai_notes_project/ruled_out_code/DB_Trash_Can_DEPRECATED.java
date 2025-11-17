package com.example.kuai_notes_project.ruled_out_code;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kuai_notes_project.Note;

import java.util.Objects;

public class DB_Trash_Can_DEPRECATED extends SQLiteOpenHelper {
    public DB_Trash_Can_DEPRECATED(@Nullable Context context) {
        super(context, "deleted_notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB_TC) {
        DB_TC.execSQL("create Table Deleted_Notes("+
                "_id INTEGER PRIMARY KEY, "+
                "date TEXT, "+
                "title TEXT, "+
                "note TEXT, "+
                "pin INTEGER, "+
                "reminder LONG, "+
                "reminder_type INTEGER, "+
                "reminder_interval INTEGER, "+
                "expire_days INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB_TC, int oldVersion, int newVersion) {
        DB_TC.execSQL("drop Table if exists Deleted_Notes");
    }

    public Boolean Insert_Note(long note_id, String date, String title,  String note, Integer pin, long reminder , int reminder_type , int reminder_interval , Integer expire_days){
        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id",note_id);
        contentValues.put("date",date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);
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

    public Boolean Modify_Note(long note_id, String previous_date, String current_date, String title, String note, Integer pin, long reminder , int reminder_type , int reminder_interval , Integer expire_days){

        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("expire_days",expire_days);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);


        //long result = DB_N.update("Notes", contentValues, "date=? and time=?", new String[]{date, time});
        int result = DB_TC.update("Deleted_Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

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
    public Boolean Reduce_Note_Expire_Days(long note_id, String previous_date, Integer expire_days){

        expire_days --;

        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("expire_days",expire_days);

        //long result = DB_N.update("Notes", contentValues, "date=? and time=?", new String[]{date, time});
        int result = DB_TC.update("Deleted_Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

        Result_Log_treatment(result, "Reduce_Note_Expire_Days");
        return result > 0;
    }
    public long Get_Last_RowId(){
        //!!--lo ideal seria usar el nativo .insert para recuperar este variable long
        SQLiteDatabase DB_TC = this.getWritableDatabase();
        SQLiteStatement statement = DB_TC.compileStatement("SELECT LAST_INSERT_ROWID();");
        long lastId = statement.simpleQueryForLong();
        statement.close();
        return lastId;
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
                    int date_index = cursor.getColumnIndex("date");
                    if(Objects.equals(cursor.getString(date_index), date)){
                        Log.d("Read cursor_Notes", "Cursor Pin_Date : Position: " + cursor.getPosition());
                        return cursor.getPosition();
                    }
                }
            }
        }

        return New_Position;
    }
    public Note getASpecificNote(long note_id){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Deleted_Notes where _id = ?", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Deleted_Notes", "Cursor_Deleted_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    note.setNote_id(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                    note.setDate(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
                    note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    note.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                    note.setPin(cursor.getInt(cursor.getColumnIndexOrThrow("pin")));
                    note.setReminder(cursor.getLong(cursor.getColumnIndexOrThrow("reminder")));
                    note.setReminder_type(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_type")));
                    note.setReminder_interval(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_interval")));
                }
            }
        }
        return note;
    }
    public int get_expire_Day(long note_id) {
        int expire_day = 0;
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Deleted_Notes where _id = ?", new String[] {String.valueOf(note_id)}) ){
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
    public Boolean Modify_Pin_Status(long note_id,  Integer pin){

        SQLiteDatabase DB_TC = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_TC.update("Deleted_Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

        Result_Log_treatment(result, "Modify_Pin_Status");
        return result > 0;
    }

    //Get Array of Notes:

    public Boolean Delete_Specific_Note(long note_id){
        SQLiteDatabase DB_TC = this.getWritableDatabase();

        int result = DB_TC.delete("Deleted_Notes",  "_id=? ", new String[]{String.valueOf(note_id)});

        Result_Log_treatment(result, "Delete_Note");
        return result > 0;
    }
    @NonNull
    private static void Result_Log_treatment(int result, String from) {
        if (result > 0) {
            Log.d("Inside DB_Trash_Can", from + ": Note Deleted");
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d( "Inside DB_Trash_Can", from + ": NOT Found");
            if (result == -1) Log.d("Inside DB_Trash_Can", from + ": Error");
        }
    }


}
