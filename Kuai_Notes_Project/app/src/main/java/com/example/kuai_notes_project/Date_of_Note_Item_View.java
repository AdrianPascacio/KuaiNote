package com.example.kuai_notes_project;

import android.util.Log;
import android.widget.Toast;

/// 17 V5, 20 V7
public class Date_of_Note_Item_View {
    public String Set_Date_of_Note(String date, String _current_time){
        int dMy_first_index = date.indexOf(" ") + 1;
        int dMy_last_index = date.lastIndexOf(" ");
        String date_dMy = date.substring(dMy_first_index,dMy_last_index);
        String date_hm = date.substring(dMy_last_index + 1);

        if(_current_time.equals(date_dMy)){
            return ("Today\n"+_current_time+"\n"+date_hm);
        }else{
            return (date_dMy+"\n"+date_hm);
        }
    }
}