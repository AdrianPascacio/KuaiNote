package com.example.kuai_notes_project.ruled_out_code;

import android.widget.Toast;

import com.example.kuai_notes_project.MainActivity;

public class MainActivity_BackUp {
    boolean change_in_date = false;

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
