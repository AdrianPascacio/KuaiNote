package com.example.kuai_notes_project.ruled_out_code;

import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import com.example.kuai_notes_project.MainActivity;
import com.example.kuai_notes_project.Reminder_Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity_BackUp {
    boolean change_in_date = false;

    ///if (reversed_reminder < 0) {
    ///    //Toast.makeText(this, "reversed minor: " + reversed_reminder, Toast.LENGTH_SHORT).show();

    ///    //test
    ///    Note note = DB_N.getASpecificNote_ByReminder(reversed_reminder);
    ///    if (reversed_reminder == note.reminder) {
    ///        //Toast.makeText(this, "reversed minor are equal: ", Toast.LENGTH_SHORT).show();

    ///    } else {
    ///        //Toast.makeText(this, "reversed minor not equal", Toast.LENGTH_SHORT).show();

    ///    }

    ///    previous_date = DB_N.getASpecificNoteDate_ByReminder(reversed_reminder);
    ///    //Toast.makeText(this, "previous date: " + previous_date, Toast.LENGTH_SHORT).show();
    ///} else {
    ///    //Toast.makeText(this, "reversed => 0 looking previous send previous date: " , Toast.LENGTH_SHORT).show();
    ///    previous_date = getIntent().getStringExtra("send_date_of_note");
    ///}




    //private void Delete_Note() {

    //    boolean delete_Success = false;
    //    String _title = et_Title.getText().toString();
    //    String _note = et_Note.getText().toString();
    //    String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
    //    Boolean Insert_Note_In_TrashCan = false;

    //    if (note.note_id != 0) {      //Delete and save in the trashcan

    //        if (!now_is_something_writed) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
    //            if (note.title != null || note.note != null) {
    //                Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,note.title,note.note, 20);
    //                Toast.makeText(MainActivity.this, "1-Insertado datos previous", Toast.LENGTH_SHORT).show();//si se elimina todo y luego se sale
    //            } else {
    //                //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
    //                Toast.makeText(MainActivity.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
    //                Insert_Note_In_TrashCan = true;
    //            }
    //        } else if (!change_in_note) {   //if there_is_something save in database > Send to trashcan what was in the database before save
    //            Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,_title,_note, 20);
    //            Toast.makeText(MainActivity.this, "3- Sin cambios, save proyectado en edit.T ", Toast.LENGTH_SHORT).show();//si se borra intencionalmente
    //        } else {
    //            Insert_Note_In_TrashCan = getNoteInTrashCan(_current_time,_title,_note, 20);
    //            Toast.makeText(MainActivity.this, "4- Cambios realizados, salvando cambios ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
    //        }

    //        if (Insert_Note_In_TrashCan) {
    //            Boolean Delete_Note_Checker = DB_N.Delete_Specific_Note(note.note_id);
    //            if (Delete_Note_Checker) {
    //                Toast.makeText(MainActivity.this, "Deleted in DB", Toast.LENGTH_SHORT).show();
    //                delete_Success = true;
    //                Reminder_Notification.Cancel_Reminder_Alarm(layout_body_note, note.note_id);
    //            } else {
    //                Toast.makeText(MainActivity.this, "NOT Deleted", Toast.LENGTH_SHORT).show();
    //            }
    //        }

    //    } else {      //Save the note directly in the TrashCan

    //        if (now_is_something_writed) {
    //            Insert_Note_In_TrashCan = getNoteInTrashCan(_current_time,_title,_note, 20);
    //            Toast.makeText(MainActivity.this, "4!2- Cambios realizados, salvando cambios ", Toast.LENGTH_SHORT).show();//borrado a proposito sin salvar previamente
    //        } else {
    //            //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
    //            Insert_Note_In_TrashCan = true;
    //            Toast.makeText(MainActivity.this, "2!2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza pin y luego se borra
    //        }
    //        if (Insert_Note_In_TrashCan) {
    //            delete_Success = true;
    //        }
    //    }

    //    if (delete_Success) {
    //        et_Title.setText("");
    //        et_Note.setText("");
    //        //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
    //        Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
    //    }
    //}


    //private void Delete_Note_V7_1 () {// 12nov2025
    //    boolean delete_Success = false;
    //    String _title = et_Title.getText().toString();
    //    String _note = et_Note.getText().toString();
    //    String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
    //    Boolean Insert_Note_In_TrashCan = false;

    //    if (!now_is_something_writed) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
    //        if (note.title != null || note.note != null) {
    //            Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,note.title,note.note, 20);
    //            Toast.makeText(MainActivity.this, "1-Insertado datos previous", Toast.LENGTH_SHORT).show();//si se elimina todo y luego se sale
    //        } else {
    //            //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
    //            Toast.makeText(MainActivity.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
    //            Insert_Note_In_TrashCan = true;
    //        }
    //    } else if (!change_in_note) {   //if there_is_something save in database > Send to trashcan what was in the database before save
    //        Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,_title,_note, 20);
    //        Toast.makeText(MainActivity.this, "3- Sin cambios, save proyectado en edit.T ", Toast.LENGTH_SHORT).show();//si se borra intencionalmente
    //    } else {
    //        Insert_Note_In_TrashCan = getNoteInTrashCan(_current_time,_title,_note, 20);
    //        Toast.makeText(MainActivity.this, "4- Cambios realizados, salvando cambios ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
    //    }

    //    if (note.note_id != 0) {      //Delete and save in the trashcan
    //        Boolean Delete_Note_Checker = DB_N.Delete_DB_DEPRECATED(note.note_id,20);
    //        if (Delete_Note_Checker) {
    //            Toast.makeText(MainActivity.this, "Deleted in DB", Toast.LENGTH_SHORT).show();
    //            Reminder_Notification.Cancel_Reminder_Alarm(layout_body_note, note.note_id);
    //        } else {
    //            Toast.makeText(MainActivity.this, "NOT Deleted", Toast.LENGTH_SHORT).show();
    //        }
    //    }
    //    if (Insert_Note_In_TrashCan) {
    //        delete_Success = true;
    //    }else{
    //        Log.d("Delete","not inserted en trashcan");
    //    }

    //    if (delete_Success) {
    //        et_Title.setText("");
    //        et_Note.setText("");
    //        //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
    //        Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
    //    }else{
    //        Log.d("Delete","not success");
    //    }
    //}


    //private void Delete_Note_Deprecated_20nov2025() {
    //    boolean delete_Success = false;
    //    String _title = et_Title.getText().toString();
    //    String _note = et_Note.getText().toString();
    //    //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
    //    long _current_time = System.currentTimeMillis();
    //    Boolean Insert_Note_In_TrashCan = false;

    //    if (!now_is_something_written) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
    //        if (note.title != null || note.note != null) {
    //            Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,note.title,note.note, 20);
    //            Toast.makeText(MainActivity.this, "1-Insertado datos previous", Toast.LENGTH_SHORT).show();//si se elimina todo y luego se sale
    //        } else {
    //            //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
    //            Toast.makeText(MainActivity.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
    //            Insert_Note_In_TrashCan = true;
    //        }
    //    } else if (!change_in_note) {   //if there_is_something save in database > Send to trashcan what was in the database before save
    //        Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,_title,_note, 20);
    //        Toast.makeText(MainActivity.this, "3- Sin cambios, save proyectado en edit.T ", Toast.LENGTH_SHORT).show();//si se borra intencionalmente
    //    } else {
    //        Insert_Note_In_TrashCan = getNoteInTrashCan(_current_time,_title,_note, 20);
    //        Toast.makeText(MainActivity.this, "4- Cambios realizados, salvando cambios ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
    //    }

    //    if (note.note_id != 0) {      //Delete and save in the trashcan
    //        Reminder_Notification.Cancel_Reminder_Alarm(layout_body_note, note.note_id);
    //    }
    //    if (Insert_Note_In_TrashCan) {
    //        delete_Success = true;
    //    }else{
    //        Log.d("Delete","not inserted en trashcan");
    //    }

    //    if (delete_Success) {
    //        et_Title.setText("");
    //        et_Note.setText("");
    //        //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
    //        Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
    //    }else{
    //        Log.d("Delete","not success");
    //    }
    //}
    //private Boolean getNoteInTrashCan(long date, String title, String _note, int expire_days) {
    //    if (note.note_id == 0) {
    //        //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
    //        long _current_time = System.currentTimeMillis();
    //        return DB_N.Insert_Note_Directly_in_Trash(_current_time,title,_note,note.pin,0,0,0);
    //    }
    //    return DB_N.Send_Note_To_Trash(note.note_id, date, title, _note, note.pin, 0, 0, 0, expire_days);
    //}


    private void Date_Format_Change(){

        //!!----Simple change of format has been replaced
        if(change_in_date){
            //tv_Date.setTextSize(14);
        }else{
            //tv_Date.setTextSize(13);
        }
    }

    //private void Date_Format_Change_BackUp_V7_1_13nov2025() {
    //    show_note_info = !show_note_info;
    //    //!!--optimizar
    //    if (show_note_info) {
    //        if (note.note_id != 0) {
    //            tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
    //            tv_Date.startAnimation(AnimationDate);
    //        } else {
    //            tv_Date.setText("");
    //        }
    //        tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
    //        tv_Info.startAnimation(AnimationInfo);
    //    } else {
    //        if (note.note_id != 0) {
    //            tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
    //            tv_Date.startAnimation(AnimationDateInvert);
    //        } else {
    //            tv_Date.setText("");
    //        }
    //        tv_Info.startAnimation(AnimationInfoInvert);
    //    }
    //}



    ///if(note.getPin()== 0 ){
    ///    note.setPin(1);
    ///}else{
    ///    note.setPin(0);
    ///}
    //esto es igual (Utilizando el operador ternario)
    ///note.setPin(note.getPin() == 0 ? 1 : 0);
    //esto es igual (Utilizando el operador xor)
    ///note.setPin(note.getPin() ^ 1);

//    private String Verify_if_exist_something(){
//        boolean _change_to_empty = false;
//        boolean _change_to_somthing_writed = false;
//        String option = null;
//
//        String _title = et_Title.getText().toString();
//        String _note = et_Note.getText().toString();
//        if (_title.isEmpty() && _note.isEmpty()){
//            if (now_is_something_writed == true){
//                Toast.makeText(MainActivity.this, "Change to empty", Toast.LENGTH_SHORT).show();
//                //si ahora no existe nada entones:
//                if(previous_date ==null){
//                    fl_Change_Pin_Status.startAnimation(AnimationPinFade);
//                    fl_Delete.startAnimation(AnimationPinFade);
//                    et_Note.startAnimation(AnimationNoteHintFading);
//                }
//
//                _change_to_empty = true;
//                now_is_something_writed = false;
//            }
//
//        }else if(!_title.isEmpty() || !_note.isEmpty()){
//            if (now_is_something_writed == false){
//                Toast.makeText(MainActivity.this, "Change to somthing writed", Toast.LENGTH_SHORT).show();
//                //si ahora existe algo entonces:
//                fl_Change_Pin_Status.setAlpha(1f);
//                fl_Delete.setAlpha(1f);
//                fl_Change_Pin_Status.startAnimation(AnimationPinAppear);
//                fl_Delete.startAnimation(AnimationPinAppear);
//                et_Note.clearAnimation();
//
//                _change_to_somthing_writed = true;
//                now_is_something_writed = true;
//            }
//        }
//        if(!_change_to_somthing_writed && !_change_to_empty){
//
//            return "0";
//        }
//        if(_change_to_somthing_writed){
//            return "1";
//        }
//        if(_change_to_empty){
//            return "2";
//        }
//        return option;
//
//    }

    ////LocalDate today = null;
    ////today = LocalDate.now();
    ////DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy hh:mm:ss");
    ////String _current_time = today.format(formatter);


    //private void Indent_Replicator() { metodo  del 19-nov-2025
    //    //!!---como se puede obtener solo una parte del text (para optimizar. solo necesito los ultimos caracteres)
    //    Editable note_editable = et_Note.getText();
    //    if(previous_note_size > note_editable.length()){
    //        Log.d("Indent_Replicator","  menor que previo. SALIR");
    //        previous_note_size = note_editable.length();

    //        //!!--separar metodo para quitar indentado

    //        int cursor_position = et_Note.getSelectionStart();
    //        if (cursor_position <= 1){ //debe ser mayor que uno
    //            Log.d("Indent_Replicator","  Cursor no es mayor que uno. SALIR");
    //            return;
    //        }

    //        char c = note_editable.charAt(cursor_position - 1);
    //        if (c != ' ' && c != '\t' && c != '-' && c != '*') {
    //            Log.d("Indent_Replicator","  no hay indentado previo al cursor.");
    //            last_deleted_char = c;
    //            return;
    //        }

    //        int jump_before_cursor = note_editable.toString().lastIndexOf('\n',cursor_position - 1);

    //        if (jump_before_cursor == -1){
    //            Log.d("Indent_Replicator","  no se encuentra salto previo. SALIR");
    //            last_deleted_char = c;
    //            return;
    //        }


    //        int indent_length = 0;

    //        Log.d("Indent_Replicator","  caracter_current:"+c+ "    caracter_previo:"+last_deleted_char);
    //        if (last_deleted_char != ' ' && last_deleted_char != '\t' && last_deleted_char != '-' && last_deleted_char != '*') {
    //            Log.d("Indent_Replicator","  el caracter borrado en la vez anterior no era indentado. SALIR");
    //            last_deleted_char = c;
    //            return ;
    //        }
    //        last_deleted_char = c;

    //        for (int i = jump_before_cursor + 1; i < cursor_position ; i++){
    //            c = note_editable.charAt(i);
    //            //!!-- optimizar para evaluar vi~etas aparte de los espacios
    //            if (c == ' ' || c == '\t' || c == '-' || c == '*') {
    //                Log.d("Indent_Replicator","  ++c:("+c+")");
    //                indent_length++;
    //            }else{
    //                Log.d("Indent_Replicator","  --c:("+c+")");
    //                //--Existe texto importante antes del indentado que no debe borrarse.
    //                Log.d("Indent_Replicator","  Existe texto importante que no puede borrarse. SALIR");
    //                return;
    //            }
    //        }

    //        note_editable.delete(cursor_position - indent_length, cursor_position);
    //        Log.d("Indent_Replicator","  Existe indentado antes de cursor: ELIMINAR INDENTADO ");

    //        return;
    //    }
    //    last_deleted_char = '0';

    //    if(previous_note_size == note_editable.length()){
    //        Log.d("Indent_Replicator","  igual a previo. SALIR");
    //        previous_note_size = note_editable.length();
    //        return;
    //    }
    //    previous_note_size = note_editable.length();

    //    int jumpBeforeEnd = note_editable.toString().indexOf('\n',note_editable.length()-1);



    //    int cursor_position = et_Note.getSelectionStart();
    //    Log.d("Indent_Replicator","Jump Before End: "+jumpBeforeEnd+"   cursorPosition:"+ cursor_position+
    //            "        previo_size: "+ previous_note_size + "   size: "+note_editable.length());


    //    int jump_before_cursor = note_editable.toString().lastIndexOf('\n',cursor_position - 1);
    //    int penultimum__newLineIndex = note_editable.toString().lastIndexOf('\n',cursor_position - 2);
    //    Log.d("Indent_Replicator","  indx_salto_prev_cursor: "+jump_before_cursor+"   indx_penultimum_salto:"+ penultimum__newLineIndex);


    //    if (jump_before_cursor == -1){
    //        Log.d("Indent_Replicator","  no se encuentra salto previo.");
    //        return;
    //    }

    //    if (jump_before_cursor != (cursor_position - 1)){
    //        Log.d("Indent_Replicator","     indx_salto_prev_cursor: "+jump_before_cursor+
    //                "   cursor_position:"+ cursor_position+" cursor --:"+(cursor_position - 1));
    //        Log.d("Indent_Replicator","  no existe salto previo al cursor ");
    //        return;
    //    }

    //    if(penultimum__newLineIndex == jump_before_cursor - 1){
    //        Log.d("Indent_Replicator","  Existen dos saltos seguidos. SALIR");
    //        return;
    //    }


    //    //tal vez aqui ayude el tamano del texto antes de la modificacion para saber cuando se borro, sin embargo esto es muy costoso
    //    Log.d("Indent_Replicator","  Existe salto justo antes de cursor: GENERAR INDENTADO ");



    //    int indentation_start = penultimum__newLineIndex + 1;
    //    StringBuilder indentation = new StringBuilder();

    //    for (int i = indentation_start; i < cursor_position ; i++){
    //        char c = note_editable.charAt(i);
    //        //!!-- optimizar para evaluar vi~etas aparte de los espacios
    //        if(c == ' ' || c == '\t' || c == '-'|| c == '*'){
    //            indentation.append(c);
    //        }else{
    //            break;
    //        }
    //    }

    //    Log.d("Indent_Replicator","  Indentation:(" + indentation + ")");

    //    //!!-- verifica cual metodo es el mas eficiente para saber si esta vacio

    //    if(indentation.length() > 0){
    //        ///Editable note_editable = et_Note.getText(); // O usa la variable que ya tenías
    //        ///int cursor_position = et_Note.getSelectionStart(); // Vuelves a obtener la posición actual del cursor

    //        // 1. Insertar el indentado en la posición del cursor
    //        note_editable.insert(cursor_position, indentation.toString());

    //        // 2. Opcional: Mover el cursor al final del texto insertado
    //        // Esto es lo que probablemente quieres para que el usuario pueda empezar a escribir
    //        et_Note.setSelection(cursor_position + indentation.length());
    //    }
    //    last_deleted_char = ' ';

    //    previous_note_size = note_editable.length();
    //}

    //private void Save_Note_Deprecated_20nov2025() {
    //    boolean save_Success = false;

    //    //!!---- corregir formato de currenttime, ya no es necesario agregar tanta informacion poo
    //    //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
    //    //System.currentTimeMillis() es la forma mas directa y eficiente de obtener un timestampMillis directo del SO
    //    long _current_time = System.currentTimeMillis();

    //    String _title = et_Title.getText().toString();
    //    String _note = et_Note.getText().toString();

    //    if (note.note_id == 0) {
    //        //-------Insert new note
    //        if (DB_N.Insert_Note(_current_time, _title, _note, note.pin, note.reminder, note.reminder_type, note.reminder_interval)) {
    //            Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
    //            save_Success = true;
    //            note.setNote_id(DB_N.Get_Last_RowId());
    //        }
    //    } else {
    //        //-------Modify the Note
    //        if (DB_N.Modify_Note(note.note_id, _current_time, _title, _note, note.pin, note.reminder, note.reminder_type, note.reminder_interval)) {
    //            Toast.makeText(MainActivity.this, "Modified", Toast.LENGTH_SHORT).show();
    //            save_Success = true;
    //        }
    //    }

    //    //-------Update the view of the date of last modification:
    //    if (save_Success) {
    //        //!!---Verificar, no se esta actualizando los datos recien agragados al objeto nota.
    //        change_in_note = false;
    //        note.date = _current_time;
    //        tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
    //    }
    //}



    //private void Pin_Note_Deprecated_20nov2025() {
    //    boolean pin_modify_Success = false;

    //    note.setPin(note.getPin() ^ 1); //XOR Operator

    //    if (note.note_id != 0) {
    //        //-------Modify pin status of the Note
    //        Boolean Modify_Pin_Status = DB_N.Modify_Pin_Status(note.note_id, note.pin);
    //        if (Modify_Pin_Status) {
    //            Toast.makeText(MainActivity.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
    //            pin_modify_Success = true;
    //        } else {
    //            Log.d("Main Activity", "Not_Pin_Modified");
    //        }
    //    } else {
    //        //!!-- debe corregirse
    //        pin_modify_Success = true;
    //    }

    //    if (pin_modify_Success) {
    //        Change_Pin_Status_Style();
    //    }
    //}

}
