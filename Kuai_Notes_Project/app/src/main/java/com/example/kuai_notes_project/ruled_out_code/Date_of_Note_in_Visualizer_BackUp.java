package com.example.kuai_notes_project.ruled_out_code;

import android.util.Log;

/// 46 V5 / 39 V7
public class Date_of_Note_in_Visualizer_BackUp {
    String _Date = null;
    String _Information = null;

    public String Set_Date_of_Note(String previous_date, String complete_current_time){
        int size_of_date = previous_date.length() ;
        String date_dMy = previous_date.substring(13,size_of_date - 8);
        String date_hm = previous_date.substring(size_of_date - 7);
        Log.d("MainActivity", "Set_Date_Note : "+complete_current_time + " ::: " + date_dMy);

        //--If current date is equal to date of last modification then is "Today"
        if(complete_current_time.equals(date_dMy)){
            String short_current_time = complete_current_time.substring(0,6);
            _Date =("Today"+"    " + short_current_time + "   "+date_hm);

            //!!----If current date is equal Yesteday then "yesterday" (Verificar si el costo es muy elevado
        }else{
            //--Just complete the date and hour in the format "dd MMMM yyyy    hh:mma"
            _Date = (date_dMy+"   "+date_hm);
        }
        return _Date;
    }
    public String Set_Date_Note_Only_Information(String note){
        return _Information = ("Character" + ": " + note.length() + "   |   " + "Words" + ": " + Word_Counter(note)) ;
    }
    private int Word_Counter(String text){
        if (text == null || text.trim().isEmpty()){
            return 0;
        }
        String [] words = text.trim().split("\\s+");

        return words.length;
    }
}