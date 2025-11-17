package com.example.kuai_notes_project.ruled_out_code;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.kuai_notes_project.Note;

import java.util.ArrayList;
import java.util.Objects;

//330 V6,
public class DB_Notes_BackUp extends SQLiteOpenHelper {
    public DB_Notes_BackUp(@Nullable Context context) {
        super(context, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB_N) {
        //DB_N.execSQL("create Table Notes(date TEXT, TIME TEXT, title TEXT, note TEXT, primary key (date))");
        //DB_N.execSQL("create Table Notes(date TEXT, title TEXT, note TEXT, pin INTEGER, reminder TEXT, primary key (date))");
        DB_N.execSQL("create Table Notes("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "date TEXT, "+
                "title TEXT, "+
                "note TEXT, "+
                "pin INTEGER, "+
                "reminder LONG, "+
                "reminder_type INTEGER, "+
                "reminder_interval INTEGER)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB_N, int oldVersion, int newVersion) {

        DB_N.execSQL("drop Table if exists Notes");

    }

    public Boolean Insert_Note(String current_date, String title,  String note, Integer pin, Long reminder, int reminder_type, int reminder_interval){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

        long result = DB_N.insert("Notes", null,contentValues);
        if (result == -1){
            Log.d("Inside DB_Notes","Insert_Note: NOT inserted");
            return false;
        }else{
            Log.d("Inside DB_Notes","Insert_Note: Note Inserted Satisfactorily");
            return true;
        }
    }
    public Boolean Modify_Note(long note_id, String current_date, String title, String note, Integer pin, Long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

        //Uso de try-finally como manejo estandar de recursos que necesitan ser cerrados como "Cursor"
        //try (Cursor cursor = DB_N.rawQuery("select date, time, title, note from Notes where date = ? and time = ?", new String[]{date, time})) {
        //El uso del metodo update retorna el numero de filas modificadas por lo que no hace falta inicializar un cursor para vericar si la modificacion se realizo

        //long result = DB_N.update("Notes", contentValues, "date=? and time=?", new String[]{date, time});
        int result = DB_N.update("Notes", contentValues, "_id = ? ", new String[]{String.valueOf(note_id)});

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

    public Boolean Modify_Note_BackUp(String previous_date, String current_date, String title, String note, Integer pin, Long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

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
    public Boolean Modify_Pin_Status(long note_id,  Integer pin){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

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
    public Boolean Modify_Reminder_Status(long note_id,  Long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

        if (result > 0) {
            Log.d("Inside DB_Notes", "Modify_Reminder_Status: Note modified");
            return true;
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Notes", "Modify_Reminder_Status: Note NOT Found");
            if (result == -1) Log.d("Inside DB_Notes", "Modify_Reminder_Status: Error");
            return false;
        }
    }

    public Cursor get_All_Notes(){
        SQLiteDatabase DB_N = this.getReadableDatabase();
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
                        note.setDate(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
                        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                        note.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                        note.setPin(cursor.getInt(cursor.getColumnIndexOrThrow("pin")));
                        note.setReminder(cursor.getLong(cursor.getColumnIndexOrThrow("reminder")));
                        note.setReminder_type(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_type")));
                        note.setReminder_interval(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_interval")));
                        noteList.add(note);
                    } while (cursor.moveToNext());
                }
            }
        }
        return noteList;
    }
    public boolean Note_Exist(long note_id){
        //Manera mas eficiente de consultar por una coincidencia
            //Se detiene al encontrar solo una coincidencia con LIMIT 1
        SQLiteDatabase DB_N = this.getReadableDatabase();
        boolean exist = false;
        try (Cursor cursor = DB_N.rawQuery("select COUNT(*) from Notes where _id = ? LIMIT 1", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    exist = true;
                }
            }
        }
        return exist;
    }
    public long Get_Note_Reminder(long note_id){
        //Manera mas eficiente de consultar por una coincidencia
        //Se detiene al encontrar solo una coincidencia con LIMIT 1
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select reminder from Notes where _id = ? LIMIT 1", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                     return cursor.getLong(cursor.getColumnIndexOrThrow("reminder"));
                }
            }
        }
        return 0L;
    }

    public Cursor get_Specific_Note(String date){
        SQLiteDatabase DB_N = this.getReadableDatabase();
        //Cursor cursor = DB_N.rawQuery("select date, time, title, note from Notes where date = ? and time = ?", new String[] {date, time});
        //Cursor cursor = DB_N.rawQuery("select date, title, note from Notes where date = ?", new String[] {date});
        Cursor cursor = DB_N.rawQuery("select * from Notes where date = ?", new String[] {date});
        return cursor;
    }
    public Note getASpecificNote(long note_id){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where _id = ? LIMIT 1", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
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
    public Note getASpecificNote_ByReminder(Long reminder){//!!----Debe corregirse para utilizar el id
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where reminder = ? LIMIT 2", new String[] {String.valueOf(reminder)}) ) {
            if (cursor.getCount() == 0) {
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
            }else if(cursor.getCount() == 2){
                Log.d("Read cursor_Notes", "Cursor_Notes : getASpecificNote_ByReminder : Note is duplicate");
            }else{
                if (cursor.moveToFirst()) {
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
    public String getASpecificNoteDate_ByReminder(Long reminder){//!!----Debe corregirse para utilizar el id
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where reminder = ? LIMIT 2", new String[] {String.valueOf(reminder)}) ) {
            if (cursor.getCount() == 0) {
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
            }else if(cursor.getCount() == 2){
                Log.d("Read cursor_Notes", "Cursor_Notes : getASpecificNote_ByReminder : Note is duplicate");
            }else{
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow("date"));
                }
            }
        }
        return null;
    }
    public int get_Specific_Note_Sorted_by_Pin_and_Date(String date){//!!--- que es esto parece que debe corregirse!!!
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

    public Boolean Delete_Specific_Note(long note_id){
        SQLiteDatabase DB_N = this.getWritableDatabase();

        int result = DB_N.delete("Notes",  "_id=? ", new String[]{String.valueOf(note_id)});

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
