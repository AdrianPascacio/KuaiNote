package com.example.kuai_notes_project.ruled_out_code;


import static java.util.Locale.getDefault;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/// 17 V5, 20 V7
public class Date_of_Note_Item_View_DEPRECATED {
    public String Set_Date_of_Note(long date, String _current_time){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long start_of_today = today.getTimeInMillis();
        //int dMy_last_index = previous_date.lastIndexOf(" ");

        SimpleDateFormat date_Fmt = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat time_Fmt = new SimpleDateFormat("hh:mma");
        String date_dMy = date_Fmt.format(date);
        String date_hm = time_Fmt.format(date);

        if(date >= start_of_today){
            return ("Today\n"+date_dMy+"\n"+date_hm);
        }else{
            return (date_dMy+"\n"+date_hm);
        }
    }
    public String Set_Date_of_Note_BackUP_V7_1_13nov2025(String date, String _current_time){
        int dMy_last_index = date.lastIndexOf(" ");
        String date_dMy = date.substring(0,dMy_last_index);
        String date_hm = date.substring(dMy_last_index + 1);

        //Set_Date_of_Note_Calendar(0,0);

        if(_current_time.equals(date_dMy)){
            return ("Today\n"+_current_time+"\n"+date_hm);
        }else{
            return (date_dMy+"\n"+date_hm);
        }
    }
    public String Set_Date_of_Note_Calendar(long date, long current_time){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_Fmt = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat time_Fmt = new SimpleDateFormat("hh:mma");
        String date_dMy = date_Fmt.format(calendar.getTime());
        String date_hm = time_Fmt.format(calendar.getTime());

        Log.d("Formater 0000", "Formato de fecha :" + date_dMy);

        if(date == current_time){
            return ("Today\n"+date_dMy+"\n"+date_hm);
        }else{
            return (date_dMy+"\n"+date_hm);
        }
    }
}