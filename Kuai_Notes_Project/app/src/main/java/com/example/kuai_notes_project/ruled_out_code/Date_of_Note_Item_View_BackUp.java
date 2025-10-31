package com.example.kuai_notes_project.ruled_out_code;

import android.util.Log;

/// 17 V5
public class Date_of_Note_Item_View_BackUp {
    public String Set_Date_of_Note_BackUp_V7(String date, String _current_time){
        int size_of_date = date.length() ;
        String date_dMy = date.substring(13,size_of_date - 8);
        String date_hm = date.substring(size_of_date - 6);

        Log.d("Date of Note Item View","date_D");
        //if last modification date is equal to current date then "Today"
        if(_current_time.equals(date_dMy)){
            return ("Today\n"+_current_time+"\n"+date_hm);
        }else{
            return (date_dMy+"\n"+date_hm);
        }
    }
}