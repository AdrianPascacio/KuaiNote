package com.example.kuai_notes_project.ruled_out_code;

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


    private void Date_Format_Change(){

        //!!----Simple change of format has been replaced
        if(change_in_date){
            //tv_Date.setTextSize(14);
        }else{
            //tv_Date.setTextSize(13);
        }
    }

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
}
