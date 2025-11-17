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

///330 V6, 331 V7despues de refactorizar DB con _id, Version 7.1 12nov2025
public class DB_Notes_BackUp2 extends SQLiteOpenHelper {
    public DB_Notes_BackUp2(@Nullable Context context) {
        super(context, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB_N) {
        DB_N.execSQL("create Table Notes("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "date TEXT, "+
                "title TEXT, "+
                "note TEXT, "+
                "pin INTEGER, "+
                "reminder LONG, "+
                "reminder_type INTEGER, "+
                "reminder_interval INTEGER,"+
                "category_id INTEGER,"+
                "expire_days INTEGER,"+
                "deleted INTEGER)"
        );
        //DB_N.execSQL("CREATE INDEX idx_delete ON Notes (is_deleted)"); //Creacion de indice para optimizar la consulta
        //!!--no esta implementado
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB_N, int oldVersion, int newVersion) {
        DB_N.execSQL("drop Table if exists Notes");
    }

    public Boolean Insert_Note(String current_date, String title,  String note, Integer pin, long reminder, int reminder_type, int reminder_interval){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);
        //!!--categoria_id no implementada todavia
        contentValues.put("category_id",0);
        //!!--expire_days no implementada todavia
        contentValues.put("expire_days",0);
        //!!--deleted no implementada todavia
        contentValues.put("deleted",0);

        long result = DB_N.insert("Notes", null,contentValues);
        Log.d("Inside DB_Notes","result = "+result);
        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        if (result == -1){
            Log.d("Inside DB_Notes","Insert_Note: NOT inserted");
            return false;
        }else{
            Log.d("Inside DB_Notes","Insert_Note: Note Inserted Satisfactorily");
            return true;
        }
    }
    public long Insert_Note_L(String current_date, String title,  String note, Integer pin, long reminder, int reminder_type, int reminder_interval){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);
        //!!--categoria_id no implementada todavia
        contentValues.put("category_id",0);
        //!!--expire_days no implementada todavia
        contentValues.put("expire_days",10);
        //!!--deleted no implementada todavia
        contentValues.put("deleted",0);

        long result = DB_N.insert("Notes", null,contentValues);
        Log.d("Inside DB_Notes","result = "+result);
        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        if (result == -1){
            Log.d("Inside DB_Notes","Insert_Note: NOT inserted");
            return -1;
        }else{
            Log.d("Inside DB_Notes","Insert_Note: Note Inserted Satisfactorily");
            return result;
        }
    }
    public long Get_Last_RowId(){
        //!!--lo ideal seria usar el nativo .insert para recuperar este variable long
        SQLiteDatabase DB_N = this.getWritableDatabase();
        SQLiteStatement statement = DB_N.compileStatement("SELECT LAST_INSERT_ROWID();");
        long lastId = statement.simpleQueryForLong();
        statement.close();
        return lastId;
    }
    public Boolean Modify_Note(long note_id, String current_date, String title, String note, Integer pin, long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",current_date);
        contentValues.put("title",title);
        contentValues.put("note",note);
        contentValues.put("pin",pin);
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);
        //!!--categoria_id no implementada todavia
        contentValues.put("category_id",0);
        //!!--expire_days no implementada todavia
        //contentValues.put("expire_days",10);
        //!!--deleted no implementada todavia
        //contentValues.put("deleted",0);

        int result = DB_N.update("Notes", contentValues, "_id = ? ", new String[]{String.valueOf(note_id)});

        String log_from = "Modify_Note";
        Result_Log_treatment(result, log_from);
        return result > 0;
    }
    public Boolean Modify_Pin_Status(long note_id,  Integer pin){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

        String log_from = "Modify_Pin_Status";
        Result_Log_treatment(result, log_from);
        return result > 0;
    }
    public Boolean Modify_Reminder_Status(long note_id,  long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});

        String log_from = "Modify_Reminder_Status";
        Result_Log_treatment(result, log_from);
        return result > 0;
    }

    public Cursor get_All_Notes(){
        SQLiteDatabase DB_N = this.getReadableDatabase();
        //Cursor cursor = DB_N.rawQuery("select date, title, note from Notes order by date DESC", null);
        Cursor cursor = DB_N.rawQuery("select * from Notes order by pin DESC, date DESC", null);
        return cursor;
    }
    public boolean Note_Exist(long note_id){
        //Manera mas eficiente de consultar por una coincidencia
            //Se detiene al encontrar solo una coincidencia con LIMIT 1
        SQLiteDatabase DB_N = this.getReadableDatabase();
        boolean exist = false;
        ///try (Cursor cursor = DB_N.rawQuery("select COUNT(*) from Notes where _id = ? LIMIT 1", new String[] {String.valueOf(note_id)}) ){
        ///    if (cursor.moveToFirst()) {
        ///        exist = true;
        ///    } else {
        ///        Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
        ///    }
        ///}

        //!!--implementar un .query en lugar de un rawquery para evitar sqlinyectoins
        try (Cursor cursor = DB_N.query("Notes",new String[] {"_id"},"_id = ?",new String[] {String.valueOf(note_id)},null,null,null,"1" )){
            if (cursor.moveToFirst()) {
                exist = true;
            } else {
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
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
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                     return cursor.getLong(cursor.getColumnIndexOrThrow("reminder"));
                }
            }
        }
        return 0L;
    }

    public Note getASpecificNote(long note_id){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where _id = ? LIMIT 1", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
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
    public Note getASpecificNote_ByReminder(long reminder){//!!----Debe corregirse para utilizar el id
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where reminder = ? LIMIT 2", new String[] {String.valueOf(reminder)}) ) {
            if (cursor.getCount() == 0) {
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }else if(cursor.getCount() == 2){
                Log.d("Read cursor_Notes", "Cursor_Notes : getASpecificNote_ByReminder : Note is duplicate");
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
    public String getASpecificNoteDate_ByReminder(long reminder){//!!----Debe corregirse para utilizar el id
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where reminder = ? LIMIT 2", new String[] {String.valueOf(reminder)}) ) {
            if (cursor.getCount() == 0) {
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
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
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Exist");
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

    public Boolean Delete_Specific_Note(long note_id){
        SQLiteDatabase DB_N = this.getWritableDatabase();

        int result = DB_N.delete("Notes",  "_id=? ", new String[]{String.valueOf(note_id)});

        String log_from = "Delete_Note";
        Result_Log_treatment(result, log_from);
        return result > 0;
    }

    @NonNull
    private static void Result_Log_treatment(int result, String from) {
        if (result > 0) {
            Log.d("Inside DB_Notes", from + ": Note Deleted");
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Notes", from + ": NOT Found");
            if (result == -1) Log.d("Inside DB_Notes", from + ": Error");
        }
    }
}
