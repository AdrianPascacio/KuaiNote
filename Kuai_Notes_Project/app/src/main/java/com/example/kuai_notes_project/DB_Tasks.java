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

import kotlinx.coroutines.scheduling.Task;

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
                "unfolded INTEGER, "+
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
                ///"note, " +
                ///"content='Tasks')"

                        "title, " +
                "note)"
        );


        //El LONG en sqlite solo se reconoce como un Integer
        //Los integer en SQL tienen un almacenamietno variable dependiendo del dato guardado 1,2,3 o bytes dependiendo del tama~o
        //Indexado complejo para evitar el sort
            //Indice antiguo:   //DB_N.execSQL("CREATE INDEX idx_delete ON Notes (deleted, date DESC)"); //Creacion de indice para optimizar la consulta mas comun (deleted, sorted por date
        DB_T.execSQL("CREATE INDEX idx_principal_default ON Tasks (deleted, pin DESC, date DESC)");
        DB_T.execSQL("CREATE INDEX idx_trashcan_default ON Tasks (deleted, date DESC)");
        //!!--hace falta un indice para que los subtask esten relacionados con los id de los task_main (parent_id)

        //!!--se debe verificar la eficacia de los indices para los sub_tasks


        //Triggers para update de indice fts4:
        DB_T.execSQL("Create TRIGGER After_Tasks_Insert AFTER INSERT ON Tasks BEGIN " +
                /// Original intentaba meter el titulo del main y las notas del sub tasks en una sola nota sin embargo es muy dificil separarlas para presentarlas por lo que los separare
                ///"INSERT INTO Tasks_fts(docid, title, note) VALUES(new._id,new.title, new.note); "+
                "INSERT INTO Tasks_fts(docid, title) VALUES(new._id,new.title); "+
                "END;");
        DB_T.execSQL("Create TRIGGER After_Sub_Tasks_Insert AFTER INSERT ON Tasks_Sub BEGIN " +
                ///"UPDATE Tasks_fts SET note = COALESCE(note, 'NoPreviousNote') || ' [\n -] ' || COALESCE(new.note, 'NoPreviousNote') "+
                "UPDATE Tasks_fts SET note =  COALESCE('\n-' || new.note, 'NoPreviousNote') "+
                "WHERE docid = new.parent_id; "+
                "END;");
        DB_T.execSQL("CREATE TRIGGER After_Sub_Tasks_Update AFTER UPDATE ON Tasks_Sub BEGIN " +
                ///"UPDATE Tasks_fts SET note = (SELECT t.note || ' ' || COALESCE(GROUP_CONCAT('\n-'||s.note, ' '), 'NoPreviousNote') " +
                "UPDATE Tasks_fts SET note = (SELECT  COALESCE(GROUP_CONCAT('\n-'||s.note, ' '), 'NoPreviousNote') " +
                "FROM Tasks t LEFT JOIN Tasks_Sub s ON t._id = s.parent_id " +
                "WHERE t._id = old.parent_id GROUP BY t._id) " +
                "WHERE docid = old.parent_id; " +
                "END;");
        DB_T.execSQL("CREATE TRIGGER trg_subtasks_delete AFTER DELETE ON Tasks_Sub BEGIN " +
                "UPDATE Tasks_fts SET note = ( " +
                ///"SELECT t.note || ' ' || COALESCE(GROUP_CONCAT('\n-'||s.note, ' '), 'NoPreviousNote') " +
                "SELECT  COALESCE(GROUP_CONCAT('\n-'||s.note, ' '), 'NoPreviousNote') " +
                "FROM Tasks t " +
                "LEFT JOIN Tasks_Sub s ON t._id = s.parent_id AND s.deleted = 0 " +
                "WHERE t._id = old.parent_id " +
                "GROUP BY t._id" +
                ") " +
                "WHERE docid = old.parent_id; " +
                "END;");
        DB_T.execSQL("CREATE TRIGGER trg_subtasks_soft_delete AFTER UPDATE OF deleted ON Tasks_Sub BEGIN " +
                "UPDATE Tasks_fts SET note = ( " +
                ///"SELECT t.note || ' ' || COALESCE(GROUP_CONCAT('\n-'||s.note, ' '), 'NoPreviousNote') " +
                "SELECT  COALESCE(GROUP_CONCAT('\n-'||s.note, ' '), 'NoPreviousNote') " +
                "FROM Tasks t " +
                "LEFT JOIN Tasks_Sub s ON t._id = s.parent_id AND s.deleted = 0 " +
                "WHERE t._id = new.parent_id " +
                "GROUP BY t._id" +
                ") " +
                "WHERE docid = new.parent_id; " +
                "END;");




        ///DB_T.execSQL("Create TRIGGER Tasks_after_insert AFTER INSERT ON Tasks BEGIN " +
        ///        "INSERT INTO Tasks_fts(docid, title, note, deleted) VALUES(new._id, new.title, new.note, new.deleted); "+
        ///        "END;");

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
                reminder, reminder_type, reminder_interval,0,0,false,false,false,false);

        long result = DB_T.insert("Tasks", null,contentValues);

        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        Log.d("Inside DB_Tasks","Insert_Task: " + (result == -1 ? "NOT inserted"   :   "Task Inserted Satisfactorily"));  ///Ternary Operator
        return  result;
    }
    public long Insert_Task_L_for_test_random_generator(long current_date, String title,  String note, boolean pin, long reminder, int reminder_type, int reminder_interval,boolean has_sub_tasks, boolean complete){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Main_Task(current_date,current_date,current_date,current_date, title, note, pin,
                reminder, reminder_type, reminder_interval,0,0,complete,has_sub_tasks,false,false);

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

    public long Insert_Task_Directly_in_Trash(long current_date, String title,  String note, boolean pin, int expire_days, boolean completed, boolean has_sub_tasks){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Main_Task(current_date, current_date, current_date, current_date, title, note, pin,
                0, 0, 0,0,expire_days,completed,has_sub_tasks,false,true);

        long result = DB_T.insert("Tasks", null,contentValues);

        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        Log.d("Inside DB_Tasks","Insert_Directly in Trash: " + (result == -1 ? "NOT inserted"   :   "Note Inserted Satisfactorily"));    ///Ternary Operator
        return result;
    }
    public long Insert_Sub_Task_Directly_in_Trash( long parent_id, String note, boolean completed, int task_sub_position){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Sub_Task(parent_id,  note,completed,task_sub_position,true);

        long result = DB_T.insert("Tasks_Sub", null,contentValues);

        //.insert devuelve el id de la fila insertada y "-1" si se produce algun error
        Log.d("Inside DB_Tasks","Insert_Directly in Trash: " + (result == -1 ? "NOT inserted"   :   "Note Inserted Satisfactorily"));    ///Ternary Operator
        return result;
    }
    public long Get_Last_RowId(){
        //!!--lo ideal seria usar el nativo .insert para recuperar este variable long
        SQLiteDatabase DB_T = this.getWritableDatabase();
        SQLiteStatement statement = DB_T.compileStatement("SELECT LAST_INSERT_ROWID() FROM Tasks;");
        long lastId = statement.simpleQueryForLong();
        statement.close();
        return lastId;
    }
    public Boolean Modify_Task(long note_id, long current_date, long date_modified, long date_completed, String title, String note, boolean pin, long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_N = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("date",current_date);
        contentValues.put("date_modified",date_modified);
        //contentValues.put("date_completed",date_completed);
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

        int result = DB_N.update("Tasks", contentValues, "_id = ? ", new String[]{String.valueOf(note_id)});
        Result_Log_treatment(result, "Modify_Task");
        return result > 0;
    }
    public Boolean Modify_Has_Sub_Tasks_Status(long task_id,  boolean has_sub_tasks){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("has_sub_tasks",has_sub_tasks);

        int result = DB_T.update("Tasks", contentValues, "_id=? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Modify_Has_Sub_Tasks_Status");
        return result > 0;
    }
    public Boolean Modify_Main_Task_Completed_Status(long task_id,  boolean completed, long new_completed_time){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("completed",completed);
        contentValues.put("date_completed",new_completed_time);

        int result = DB_T.update("Tasks", contentValues, "_id=? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Modify_Completed_Status");
        return result > 0;
    }
    public Boolean Modify_Main_Task_Modified_Date(long task_id,  long new_modified_time){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date_modified",new_modified_time);

        int result = DB_T.update("Tasks", contentValues, "_id=? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Modify_Completed_Status");
        return result > 0;
    }
    public Boolean Modify_Sub_Task_Description(long task_sub_id,  String description){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("note",description);

        int result = DB_T.update("Tasks_Sub", contentValues, "_id=? ", new String[]{String.valueOf(task_sub_id)});
        Result_Log_treatment(result, "Modify_Description");
        return result > 0;
    }
    public Boolean Modify_Sub_Task_Completed_Status(long task_sub_id,  boolean completed){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("completed",completed);

        int result = DB_T.update("Tasks_Sub", contentValues, "_id=? ", new String[]{String.valueOf(task_sub_id)});
        Result_Log_treatment(result, "Modify_Completed_Status");
        return result > 0;
    }
    public void Modify_All_Sub_Task_Completed_Status(long parent_id, boolean completed) {
        SQLiteDatabase DB_T = this.getReadableDatabase();
        Cursor cursor = DB_T.rawQuery("select _id, completed from Tasks_Sub where parent_id = ? AND deleted = 0 order by task_sub_position DESC", new String[] {String.valueOf(parent_id)});
        if(cursor.getCount()==0){
            Log.d("Read cursor_Tasks", "Cursor_Tasks :  No Entry exist");
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put("completed",completed);
            while (cursor.moveToNext()){
                if((cursor.getInt(1)==1) != completed){
                    long task_sub_id = cursor.getLong(0);
                    boolean task_sub_completed =  cursor.getInt(1)==1;
                    int result = DB_T.update("Tasks_Sub", contentValues, "_id=? ", new String[]{String.valueOf(task_sub_id)});
                    Result_Log_treatment(result, "Modify_All_Completed_Status");
                }
            }
        }
    }
    public Integer Verify_If_All_Sub_Task_Completed(long parent_id){

        SQLiteDatabase DB_T = this.getReadableDatabase();
        int result = 0;
        Cursor  cursor = DB_T.rawQuery("select MIN(completed) from Tasks_Sub where parent_id = ? AND deleted = 0 order by task_sub_position DESC", new String[]{String.valueOf(parent_id)});
        if(cursor.getCount()==0){
            Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
        }else{
            if(cursor.moveToFirst()){
                result= cursor.getInt(0);
            }
        }
        return result;
    }
    public int Verify_Top_Sub_Task_Position(long parent_id) {
        SQLiteDatabase DB_T = this.getReadableDatabase();
        int result = 0;
        Cursor  cursor = DB_T.rawQuery("select MAX(task_sub_position) from Tasks_Sub where parent_id = ? AND deleted = 0 ", new String[]{String.valueOf(parent_id)});
        if(cursor.getCount()==0){
            Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
        }else{
            if(cursor.moveToFirst()){
                result= cursor.getInt(0);
            }
        }
        return result;
    }
    public Boolean Verify_If_Sub_Task_Is_Empty(long sub_task_id){

        SQLiteDatabase DB_T = this.getReadableDatabase();
        boolean result = false;
        Cursor  cursor = DB_T.rawQuery("select note from Tasks_Sub where _id = ? AND deleted = 0 Limit 1", new String[]{String.valueOf(sub_task_id)});
        if(cursor.getCount()==0){
            Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
        }else{
            if(cursor.moveToFirst()){
                result= cursor.getString(0).isEmpty();
            }
        }
        return result;
    }
    public Boolean Delete_Hard_Specific_Task_Sub(long task_sub_id){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        int result = DB_T.delete("Tasks_Sub",  "_id=? ", new String[]{String.valueOf(task_sub_id)});
        Result_Log_treatment(result, "Delete_Hard_Specific_Sub_Task");
        return result > 0;
    }
    public Boolean Modify_Pin_Status(long task_id,  boolean pin){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin",pin);

        int result = DB_T.update("Tasks", contentValues, "_id=? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Modify_Pin_Status");
        return result > 0;
    }
    public boolean Modify_Unfold_Status(long task_id, boolean unfolded) {
        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("unfolded",unfolded);

        int result = DB_T.update("Tasks", contentValues, "_id=? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Modify_Unfolded_Status");
        return result > 0;
    }
    public Boolean Modify_Reminder_Status(long task_id,  long reminder, int reminder_type, int reminder_interval){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("reminder",reminder);
        contentValues.put("reminder_type",reminder_type);
        contentValues.put("reminder_interval",reminder_interval);

        int result = DB_T.update("Tasks", contentValues, "_id=? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Modify_Reminder_Status");
        return result > 0;
    }
    public void Modify_Sub_Task_New_Position(long sub_task_id, int new_position) {
        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_sub_position",new_position);
        int result = DB_T.update("Tasks_Sub", contentValues , "_id=? ", new String[]{String.valueOf(sub_task_id)});
        Result_Log_treatment(result, "Modify_Sub_Task_New_Position");
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
        Cursor cursor = DB_T.rawQuery("select * from Tasks where deleted = 0 order by  pin DESC, completed ASC, date DESC", null);
        return cursor;
    }
    public Task_Main getASpecificTask(long task_id){
        Task_Main task = new Task_Main();
        SQLiteDatabase DB_N = this.getReadableDatabase();
        try (Cursor cursor = DB_N.rawQuery("select * from Tasks where _id = ? AND deleted = 0 LIMIT 1", new String[] {String.valueOf(task_id)}) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes :  No Entry Does not exist");
            }else{
                if (cursor.moveToFirst()) {
                    Task_Main_Setter(task, cursor);
                }
            }
        }
        return task;
    }
    public Cursor get_All_Tasks_Sub(){
        SQLiteDatabase DB_T = this.getReadableDatabase();
        Cursor cursor = DB_T.rawQuery("select * from Tasks_Sub where deleted = 0 order by task_sub_position DESC", null);
        return cursor;
    }
    public Cursor get_All_Tasks_Sub_For_Specific_Task_Main(long parent_id){
        SQLiteDatabase DB_T = this.getReadableDatabase();
        Cursor cursor = DB_T.rawQuery("select * from Tasks_Sub where parent_id = ? AND deleted = 0 order by completed ASC ,task_sub_position ASC", new String[] {String.valueOf(parent_id)});
        return cursor;

    }
    public Cursor get_All_Tasks_fts(String searched_text){
        SQLiteDatabase DB_T = this.getReadableDatabase();

        String queryInput = searched_text + "*";

        Cursor cursor = DB_T.rawQuery("select t._id, t.title, t.note, " +
                        "snippet(Tasks_fts, '[', ']', '...', 1, 4) AS preview, " +
                        "snippet(Tasks_fts, '[', ']', '...', 0, 4) AS preview_title " +
                //"snippet(Tasks_fts, '[', ']', '...', -1, 10) AS preview " +





                "FROM Tasks t " +
                "JOIN Tasks_fts f ON t._id = f.docid " +
                "WHERE f.Tasks_fts MATCH ? AND t.deleted = 0"

                ///"offsets(Tasks_fts) AS preview " +
                ///        "FROM Tasks t " +
                ///        "JOIN Tasks_fts f ON t._id = f.docid " +
                ///        "WHERE f.Tasks_fts MATCH ? AND t.deleted = 0"



                , new String[]{queryInput});
        return cursor;

    }
    public List<Task_Main> get_All_Notes_With_Pending_Reminders(){
        List<Task_Main> scheduled_Tasks = new ArrayList<>() {};
        SQLiteDatabase DB_T = this.getReadableDatabase();
        try (Cursor cursor = DB_T.rawQuery("select * from Tasks where deleted = 0 AND reminder > 0 ", null) ){
            if(cursor.getCount()==0){
                Log.d("Read cursor_Tasks", "Cursor_Tasks :  No Entry Does not exist");
            }else{
                while (cursor.moveToNext()){
                    Task_Main task = new Task_Main();
                    Task_Main_Setter(task, cursor);
                    scheduled_Tasks.add(task);
                }
            }
        }
        return scheduled_Tasks;
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
    public int get_Specific_Task_Sorted_by_Pin_and_Date(long task_id){
        int New_Position = -1;
        SQLiteDatabase DB_T = this.getReadableDatabase();
        String query = "SELECT COUNT(*)" +
                " FROM Tasks AS T1"+
                " WHERE " +
                " T1.deleted = 0" +
                " AND" +
                /// Version inicial, solo cuenta pin y date.
                ///" (" +
                ///" (T1.pin > (SELECT pin FROM Tasks WHERE _id = ?))" +
                ///" OR" +
                ///" (T1.pin = (SELECT pin FROM Tasks WHERE _id = ?) AND T1.date > (SELECT date FROM Tasks WHERE _id = ?))" +
                ///" )";

                /// Version contiene el parametro para completed:
                " (" +
                " (T1.pin > (SELECT pin FROM Tasks WHERE _id = ?))" +
                " OR" +
                " (T1.pin = (SELECT pin FROM Tasks WHERE _id = ?) AND T1.completed < (SELECT completed FROM Tasks WHERE _id = ?))" +
                " OR" +
                " (T1.pin = (SELECT pin FROM Tasks WHERE _id = ?) AND T1.completed = (SELECT completed FROM Tasks WHERE _id = ?) AND T1.date > (SELECT date FROM Tasks WHERE _id = ?))" +
                " )";



        String [] selectionArgs = { String.valueOf(task_id),String.valueOf(task_id),String.valueOf(task_id),String.valueOf(task_id), String.valueOf(task_id), String.valueOf(task_id)};

        try (Cursor cursor = DB_T.rawQuery(query, selectionArgs)){
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
    public Boolean Send_Task_To_Trash(long task_id, long current_date, String title, String note, boolean pin, int expire_days, boolean completed, boolean has_sub_tasks){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Main_Task(current_date,current_date,current_date,current_date,title,note,pin,
                0,0,0,0,expire_days,completed,has_sub_tasks,false,true);

        int result = DB_T.update("Tasks", contentValues, "_id = ? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Send_Task_To_Trash");
        return result > 0;
    }
    public Boolean Send_Sub_Task_To_Trash(long task_id, long parent_id, String note, boolean completed, int task_sub_position){

        SQLiteDatabase DB_T = this.getWritableDatabase();
        //!!--Corregir valores de: fechas, completed, has_sub_task
        ContentValues contentValues = ContentValues_Complete_Setter_Sub_Task(parent_id,note,completed,task_sub_position, true);

        int result = DB_T.update("Tasks_Sub", contentValues, "_id = ? ", new String[]{String.valueOf(task_id)});
        Result_Log_treatment(result, "Send_Task_To_Trash");
        return result > 0;
    }
    public void Send_Previous_Sub_Task_To_Trash_With_Out_Modification(long parent_id) {
        SQLiteDatabase DB_T = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted",true);

        int result = DB_T.update("Tasks_Sub", contentValues, "parent_id = ? ", new String[]{String.valueOf(parent_id)});
        Result_Log_treatment(result, "Send_Task_To_Trash_With_Out_Modification");
    }
    @NonNull
    private static ContentValues ContentValues_Complete_Setter_Main_Task(long current_date,long date_created,long date_modified,long date_completed, String title, String note, boolean pin, long reminder, int reminder_type, int reminder_interval, int category_id, int expire_days,boolean completed, boolean has_sub_tasks, boolean unfolded, boolean deleted) {
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
        contentValues.put("unfolded",unfolded);
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
    public Boolean Delete_Hard_All_Tasks(){
        SQLiteDatabase DB_T = this.getWritableDatabase();
        //Borrado de la Tabla Tasks y Tasks_Sub
        int result = DB_T.delete("Tasks",  null, null);
        int result1_2 = DB_T.delete("Tasks_Sub",  null, null);
        //Borrado del buscador Notes_fts
        int result_2 = DB_T.delete("Tasks_fts",  null, null);

        Result_Log_treatment(result, "Delete_Hard_All_Tasks");
        Result_Log_treatment(result_2, "Delete_Hard_All_Tasks");

        //Reinicio de los Id autoincrementales:
        int result_3 = DB_T.delete("sqlite_sequence",  "name=?", new String[]{String.valueOf("Tasks")});
        int result_4 = DB_T.delete("sqlite_sequence",  "name=?", new String[]{String.valueOf("Tasks_Sub")});

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
    private static void Task_Main_Setter(Task_Main task, Cursor cursor) {
        task.setTask_id(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
        task.setDate(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
        task.setDate_Created(cursor.getLong(cursor.getColumnIndexOrThrow("date_created")));
        task.setDate_Modified(cursor.getLong(cursor.getColumnIndexOrThrow("date_modified")));
        task.setDate_Completed(cursor.getLong(cursor.getColumnIndexOrThrow("date_completed")));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        task.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
        task.setPin(cursor.getInt(cursor.getColumnIndexOrThrow("pin")) == 1);
        task.setReminder(cursor.getLong(cursor.getColumnIndexOrThrow("reminder")));
        task.setReminder_type(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_type")));
        task.setReminder_interval(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_interval")));
        task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow("completed")) == 1);
        task.setHas_Sub_Tasks(cursor.getInt(cursor.getColumnIndexOrThrow("has_sub_tasks")) == 1);
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