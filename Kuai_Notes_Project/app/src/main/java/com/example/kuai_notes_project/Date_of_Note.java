package com.example.kuai_notes_project;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/// 46 V5 / 40 V7 /
public class Date_of_Note {
    String Date = null;
    String Information = null;
    String date_dMy = null , date_hm = null;
    long start_of_today = 0;

    public String Set_Date_of_Note_In_Visualizer(long previous_date){
        getStartOfToday();
        Set_Date_format(previous_date);

        if(previous_date >= start_of_today){
            Date =("Today"+"    " + date_dMy + "   "+ date_hm);
        }else{
            Date = (date_dMy +"   "+ date_hm);
        }
        return Date;
    }
    public String Set_Date_of_Note_Item_View(long date, long current_today){
        start_of_today = current_today;
        Set_Date_format(date);

        if(date >= start_of_today){
            Date = ("Today"+"\n"+date_dMy+"\n"+date_hm);
        }else{
            Date = (date_dMy+"\n"+date_hm);
        }
        return Date;
    }

    private void Set_Date_format(long previous_date) {
        SimpleDateFormat date_Fmt = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat time_Fmt = new SimpleDateFormat("hh:mma");
        date_dMy = date_Fmt.format(previous_date);
        date_hm = time_Fmt.format(previous_date);
    }

    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
    }

    public String Set_Date_Note_Only_Information(String note){
        return Information = ("Character" + ": " + note.length() + "   |   " + "Words" + ": " + Word_Counter(note)) ;
    }
    private int Word_Counter(String text){
        if (text == null || text.trim().isEmpty()){
            return 0;
        }
        String [] words = text.trim().split("\\s+");

        return words.length;
    }
}