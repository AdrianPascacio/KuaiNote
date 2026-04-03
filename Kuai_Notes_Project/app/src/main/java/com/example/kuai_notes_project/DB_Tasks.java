package com.example.kuai_notes_project;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DB_Tasks extends SQLiteOpenHelper {
    public DB_Tasks(@Nullable Context context) {
        super(context, "tasks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB_T) {
        DB_T.execSQL("create Table Tasks("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "date LONG, "+
                "date_created LONG, "+
                "date_modified LONG, "+
                "date_completed LONG, "+
                "title TEXT, "+
                "note TEXT, "+
                "pin INTEGER, "+
                "reminder LONG, "+
                "reminder_type INTEGER, "+
                "reminder_interval INTEGER,"+
                "category_id INTEGER,"+
                "expire_days INTEGER,"+
                "completed INTEGER, "+
                "has_sub_tasks INTEGER, "+
                "deleted INTEGER)"
        );
        DB_T.execSQL("create Table Tasks_Sub("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "parent_id LONG, "+
                "note TEXT, "+
                "completed INTEGER, "+
                "task_sub_position INTEGER, "+
                "deleted INTEGER)"
        );

        DB_T.execSQL("create Virtual Table Tasks_fts Using fts4(" +
                "title, "+
                "note, " +
                "deleted, " +
                "content='Tasks')"
        );


        //El LONG en sqlite solo se reconoce como un Integer
        //Los integer en SQL tienen un almacenamietno variable dependiendo del dato guardado 1,2,3 o bytes dependiendo del tama~o
        //Indexado complejo para evitar el sort
            //Indice antiguo:   //DB_N.execSQL("CREATE INDEX idx_delete ON Notes (deleted, date DESC)"); //Creacion de indice para optimizar la consulta mas comun (deleted, sorted por date
        DB_T.execSQL("CREATE INDEX idx_principal_default ON Tasks (deleted, pin DESC, date DESC)");
        DB_T.execSQL("CREATE INDEX idx_trashcan_default ON Tasks (deleted, date DESC)");

        //!!--se debe verificar la eficacia de los indices para los sub_tasks

        //Triggers para update de indice fts4:
        DB_T.execSQL("Create TRIGGER Tasks_after_insert AFTER INSERT ON Tasks BEGIN " +
                "INSERT INTO Tasks_fts(docid, title, note, deleted) VALUES(new._id, new.title, new.note, new.deleted); "+
                "END;");

        //!!--verificar si es necesario una busqueda por fts4 para los sub_tasks

        //DB_N.execSQL("Create TRIGGER Notes_after_delete AFTER DELETE ON Notes BEGIN " +
        //        "DELETE FROM Notes_fts WHERE docid = old._id; "+
        //        "END;");

        //DB_N.execSQL("Create TRIGGER Notes_after_update AFTER UPDATE ON Notes BEGIN " +
        //        "UPDATE  Notes_fts SET title = new.title, note = new.note WHERE docid = old._id; "+
        //        "END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB_T, int oldVersion, int newVersion) {
        DB_T.execSQL("drop Table if exists Tasks");
        DB_T.execSQL("drop Table if exists Tasks_Sub");
        //!!--en el DB de notes hace falta el drop if exist de la tabla Note_fts
        DB_T.execSQL("drop Table if exists Tasks_fts");
    }

    public long Insert_Task_L(long current_date, String title,  String note, boolean pin, long reminder, int reminder_type, int reminder_interval){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Main_Task(current_date,current_date,current_date,current_date, title, note, pin,
                reminder, reminder_type, reminder_interval,0,0,false,false,0);

        long result = DB_T.insert("Tasks", null,contentValues);

        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        Log.d("Inside DB_Tasks","Insert_Task: " + (result == -1 ? "NOT inserted"   :   "Task Inserted Satisfactorily"));  ///Ternary Operator
        return  result;
    }
    public long Insert_Task_Sub_L( long parent_id, String note, boolean completed, int task_sub_position){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = ContentValues_Complete_Setter_Sub_Task(parent_id, note, completed, task_sub_position, false);

        long result = DB_T.insert("Tasks_Sub", null,contentValues);

        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        Log.d("Inside DB_Tasks_Sub","Insert_Task_Sub: " + "number of insertion:"  + result );  ///Ternary Operator
        Log.d("Inside DB_Tasks_Sub","Insert_Task_Sub: " + (result == -1 ? "NOT inserted"   :   "Task Inserted Satisfactorily"));  ///Ternary Operator
        return  result;
    }

    public Boolean Insert_Task_Directly_in_Trash(long current_date, String title,  String note, boolean pin, int expire_days){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Main_Task(current_date, current_date, current_date, current_date, title, note, pin,
                0, 0, 0,0,expire_days,false,false,1);

        long result = DB_N.insert("Tasks", null,contentValues);

        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        Log.d("Inside DB_Notes","Insert_Directly in Trash: " + (result == -1 ? "NOT inserted"   :   "Note Inserted Satisfactorily"));    ///Ternary Operator
        return result != -1;
    }
    public long Get_Last_RowId(){
        //!!--lo ideal seria usar el nativo .insert para recuperar este variable long
        SQLiteDatabase DB_N = this.getWritableDatabase();
        SQLiteStatement statement = DB_N.compileStatement("SELECT LAST_INSERT_ROWID();");
        long lastId = statement.simpleQueryForLong();
        statement.close();
        return lastId;
    }
    public Boolean Modify_Task(long note_id, long current_date, String title, String note, boolean pin, long reminder, int reminder_type, int reminder_interval){

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
        //contentValues.put("deleted",0);

        int result = DB_N.update("Notes", contentValues, "_id = ? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Modify_Note");
        return result > 0;
    }
    public Boolean Modify_Pin_Status(long note_id,  boolean pin){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Modify_Pin_Status");
        return result > 0;
    }
    public Boolean Modify_Reminder_Status(long note_id,  long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Modify_Reminder_Status");
        return result > 0;
    }
    public Boolean Recycle_Note(long note_id){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted",0);

        int result = DB_N.update("Notes", contentValues, "_id=? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Recycle_Note");
        return result > 0;
    }

    //--updated to Tasks: get_All_Notes:
    public Cursor get_All_Tasks(){
        SQLiteDatabase DB_T = this.getReadableDatabase();
        Cursor cursor = DB_T.rawQuery("select * from Tasks where deleted = 0 order by pin DESC, date DESC", null);
        return cursor;
    }
    public Cursor get_All_Tasks_Sub(){
        SQLiteDatabase DB_T = this.getReadableDatabase();
        Cursor cursor = DB_T.rawQuery("select * from Tasks_Sub where deleted = 0 order by task_sub_position DESC", null);
        return cursor;
    }
    public Cursor get_All_Notes_fts_2(String searched_text){
        SQLiteDatabase DB_N = this.getReadableDatabase();

        //!! update:
        ///searched_text = "Com";
        String queryInput = searched_text + "*";

        ///Cursor cursor = DB_N.rawQuery("select snippet( Notes_fts, '[', ']', '...', -1, 10) as extract, id  from Notes_fts where Notes_fts MATCH 'Com*'", null);
        //Cursor cursor = DB_N.rawQuery("select n._id, n.title, n.note From Notes n Join Notes_fts f ON n._id = f.docid where Notes_fts MATCH ?", new String[]{queryInput});

        //Cursor cursor = DB_N.rawQuery("select n._id, n.title, n.note , n.deleted From Notes n Join Notes_fts f ON n._id = f.docid where  f.Notes_fts MATCH ? AND n.deleted = 0", new String[]{queryInput});

        //---esto solo funciona con las notas no eliminadas
        ///Cursor cursor = DB_N.rawQuery("select n._id, n.title, snippet(Notes_fts, '[', ']', '...', -1, 10) AS note  From Notes n Join Notes_fts f ON n._id = f.docid where  f.Notes_fts MATCH ? AND n.deleted = 0", new String[]{queryInput});
        ///Cursor cursor = DB_N.rawQuery("select n._id, n.title, snippet(Notes_fts, '[', ']', '...') As somthingelse From Notes n Join Notes_fts f ON n._id = f.docid where  f.Notes_fts MATCH ? AND n.deleted = 0", new String[]{queryInput});
        ///Cursor cursor = DB_N.rawQuery("select n._id, n.title, n.note, snippet(Notes_fts, '[', ']', '...') As search_snippet From Notes n Join Notes_fts f ON n._id = f.rowid where  f.Notes_fts MATCH ? AND n.deleted = 0", new String[]{queryInput});
        Cursor cursor = DB_N.rawQuery("select n._id, n.title, n.note, snippet(Notes_fts, '[', ']', '...', 1, 13) As search_snippet, snippet(Notes_fts, '[', ']', '...', 0, 13) As search_snippetTitle From Notes n Join Notes_fts f ON n._id = f.docid where  f.Notes_fts MATCH ? AND n.deleted = 0", new String[]{queryInput});
        return cursor;

    }
    public List<Note> get_All_Notes_With_Pending_Reminders(){
        List<Note> scheduled_Notes = new ArrayList<>() {};
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where deleted = 0 AND reminder > 0 ", null) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }else{
                while (cursor.moveToNext()){
                    Note note = new Note();
                    Note_Setter(note, cursor);
                    scheduled_Notes.add(note);
                }
            }
        }
        return scheduled_Notes;
    }
    public Cursor get_All_Notes_Of_Trash(){
        SQLiteDatabase DB_N = this.getReadableDatabase();
        Cursor cursor = DB_N.rawQuery("select * from Notes where deleted = 1 order by  date DESC", null);
        return cursor;
    }
    public boolean Note_Exist(long note_id){
        SQLiteDatabase DB_N = this.getReadableDatabase();
        boolean exist = false;
        String query = "SELECT COUNT(*) FROM Notes WHERE _id = ? AND deleted = 0";
        String[] selectionArgs = {String.valueOf(note_id)};

        try (Cursor cursor = DB_N.rawQuery(query,selectionArgs)){
            if (cursor.moveToFirst()) {
                exist = true;
            } else {
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }
        }
        return exist;
    }

    public Note getASpecificNote(long note_id){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where _id = ? AND deleted = 0 LIMIT 1", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    Note_Setter(note, cursor);
                }
            }
        }
        return note;
    }


    public Note getASpecificNote_In_Trash(long note_id){
        Note note = new Note();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where _id = ? AND deleted = 1  LIMIT 1", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    Note_Setter(note, cursor);
                }
            }
        }
        return note;
    }
    public int get_Specific_Note_Sorted_by_Pin_and_Date(long note_id){
        int New_Position = -1;
        SQLiteDatabase DB_N = this.getReadableDatabase();
        String query = "SELECT COUNT(*)" +
                " FROM Notes AS T1"+
                " WHERE " +
                " T1.deleted = 0" +
                " AND" +
                " (" +
                " (T1.pin > (SELECT pin FROM Notes WHERE _id = ?))" +
                " OR" +
                " (T1.pin = (SELECT pin FROM Notes WHERE _id = ?) AND T1.date > (SELECT date FROM Notes WHERE _id = ?))" +
                " )";

        String [] selectionArgs = { String.valueOf(note_id), String.valueOf(note_id), String.valueOf(note_id)};

        try (Cursor cursor = DB_N.rawQuery(query, selectionArgs)){
            if (cursor.moveToFirst()){
                New_Position = cursor.getInt(0);
            }
        }

        return New_Position;
    }
    public int get_expire_Day(long note_id) {
        int expire_day = 0;
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Notes where _id = ?", new String[] {String.valueOf(note_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    expire_day = cursor.getInt(cursor.getColumnIndexOrThrow("expire_days"));
                }
            }
        }
        return expire_day;
    }
    public Boolean Send_Note_To_Trash(long note_id, long current_date, String title, String note, boolean pin, int expire_days){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Main_Task(current_date,current_date,current_date,current_date,title,note,pin,
                0,0,0,0,expire_days,false,false,1);

        int result = DB_N.update("Notes", contentValues, "_id = ? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Send_Note_To_Trash");
        return result > 0;
    }
    @NonNull
    private static ContentValues ContentValues_Complete_Setter_Main_Task(long current_date,long date_created,long date_modified,long date_completed, String title, String note, boolean pin, long reminder, int reminder_type, int reminder_interval, int category_id, int expire_days,boolean completed, boolean has_sub_tasks, int deleted) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", current_date);
        contentValues.put("date_created",   date_created);
        contentValues.put("date_modified",  date_modified);
        contentValues.put("date_completed", date_completed);
        contentValues.put("title", title);
        contentValues.put("note", note);
        contentValues.put("pin", pin);
        contentValues.put("reminder", reminder);
        contentValues.put("reminder_type", reminder_type);
        contentValues.put("reminder_interval", reminder_interval);
        //!!--categoria_id no implementada todavia
        contentValues.put("category_id",category_id);
        //!!--expire_days no implementada todavia
        contentValues.put("expire_days",expire_days);
        contentValues.put("completed",completed);
        contentValues.put("has_sub_tasks",has_sub_tasks);
        contentValues.put("deleted",deleted);
        return contentValues;
    }
    @NonNull
    private static ContentValues ContentValues_Complete_Setter_Sub_Task(long parent_id,  String note, boolean completed, int task_sub_position, boolean deleted) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("parent_id", parent_id);
        contentValues.put("note", note);
        contentValues.put("completed", completed);
        contentValues.put("task_sub_position", task_sub_position);
        contentValues.put("deleted",deleted);
        return contentValues;
    }
    public Boolean Delete_Hard_Specific_Note(long note_id){
        SQLiteDatabase DB_N = this.getWritableDatabase();
        int result = DB_N.delete("Notes",  "_id=? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Delete_Hard_Specific_Note");
        return result > 0;
    }
    private static void Note_Setter(Note note, Cursor cursor) {
        note.setNote_id(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
        note.setDate(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        note.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
        note.setPin(cursor.getInt(cursor.getColumnIndexOrThrow("pin")) == 1);
        note.setReminder(cursor.getLong(cursor.getColumnIndexOrThrow("reminder")));
        note.setReminder_type(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_type")));
        note.setReminder_interval(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_interval")));
    }

    @NonNull
    private static void Result_Log_treatment(int result, String from) {
        if (result > 0) {
            Log.d("Inside DB_Notes", "From: " + from);
        } else {
            //result == 0 no se encontro | -1 hubo un error
            if (result == 0) Log.d("Inside DB_Tasks", from + ": NOT Found");
            if (result == -1) Log.d("Inside DB_Tasks", from + ": Error");
        }
    }
}