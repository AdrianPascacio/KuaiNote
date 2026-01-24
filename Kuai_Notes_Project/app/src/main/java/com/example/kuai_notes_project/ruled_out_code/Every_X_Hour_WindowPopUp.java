package com.example.kuai_notes_project.ruled_out_code;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Space;

import com.example.kuai_notes_project.DB_Notes;
import com.example.kuai_notes_project.Date_and_Time_Names;
import com.example.kuai_notes_project.Note;
import com.example.kuai_notes_project.R;
import com.example.kuai_notes_project.Repeater_PopUpWindow;

import java.util.Calendar;

public class Every_X_Hour_WindowPopUp {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    private int position = 0;
    private final Context context;
    private int hours_interval = 0;

    public Every_X_Hour_WindowPopUp(Context context, int position){
        this.context = context;
        this.position = position;
    }

    public interface OnValueSelectedListener_Every_X_Hour{
        void OnValueSelected_Repeater_every_x_hour(int hour_interval);
    }
    public interface PopupDismissListener_Repeater_X_Hour{//esto puede ir tambien en una clase separada
        void onPopupClosed_Repeater_X_Hour(int salida); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }

    OnValueSelectedListener_Every_X_Hour listenerEveryXHour;
    PopupDismissListener_Repeater_X_Hour listener_dismiss_every_x_hour;

    public void setListener_every_x_hour(OnValueSelectedListener_Every_X_Hour listenerEveryXHour){
        this.listenerEveryXHour = listenerEveryXHour;
    }
    public void setListener_dismiss_every_x_hour(PopupDismissListener_Repeater_X_Hour listener_dismiss_every_x_hour){
        this.listener_dismiss_every_x_hour = listener_dismiss_every_x_hour;
    }

    public void show(View view_brought, int note_reminder_type, int note_reminder_interval) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.every_x_hour_repeat_windowpopup,null);
        NumberPicker numberpicker_hour, numberpicker_minute;
        FrameLayout repeater_hour_ok_button = container.findViewById(R.id.Repeater_Every_X_Hour_Ok_Button);
        FrameLayout repeater_hour_cancel_button = container.findViewById(R.id.Repeater_Every_X_Hour_Cancel_Button);

        int selected_hour = note_reminder_interval / (1000 * 60 * 60);
        int selected_minute = (note_reminder_interval - (selected_hour * 1000 * 60 * 60)) / (1000*60*15);//!! 15 debido a los 15 minutos


        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        if (position == -1) {
            //---note Visualizer activity
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER, 00, -300);

        } else {
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER, 00, -400);
        }

        numberpicker_hour = container.findViewById(R.id.Repeater_Every_X_Hour_Number_Hour);
        numberpicker_minute = container.findViewById(R.id.Repeater_Every_X_Hour_Number_Minute);

        Date_and_Time_Names.init_Days_Names();

        Date_and_Time_Names.init_RepeaterHours_Names();
        numberpicker_hour.setMinValue(0);
        numberpicker_hour.setMaxValue(24);
        ///numberpicker_hour.setValue(1);
        numberpicker_hour.setValue(note_reminder_type == 2 ? selected_hour : 1);///Ternary Operator
        numberpicker_hour.setDisplayedValues(Date_and_Time_Names.getNameRepeater_Hours());

        Date_and_Time_Names.init_RepeaterMinutes_Names();
        numberpicker_minute.setMinValue(0);
        numberpicker_minute.setMaxValue(3);
        ///numberpicker_minute.setValue(0);
        numberpicker_minute.setValue(note_reminder_type == 2 ? selected_minute : 0);
        numberpicker_minute.setDisplayedValues(Date_and_Time_Names.getNameRepeater_Minutes());



        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){ @Override
        public void onDismiss(){
                Every_X_Hour_WindowPopUp.this.listener_dismiss_every_x_hour.onPopupClosed_Repeater_X_Hour(2);
        }
        });
        repeater_hour_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listenerEveryXHour != null) {
                    hours_interval = numberpicker_hour.getValue() * 1000*60*60 + numberpicker_minute.getValue() *15*1000*60;
                    listenerEveryXHour.OnValueSelected_Repeater_every_x_hour(hours_interval); //!! Devolver el intervalo expresado en milisegundos
                }

                popupWindow.dismiss();
                if (listener_dismiss_every_x_hour != null) {
                    listener_dismiss_every_x_hour.onPopupClosed_Repeater_X_Hour(2); // Devolver el mismo valor`
                }
            }
        });
        repeater_hour_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener_dismiss_every_x_hour != null) {
                    listener_dismiss_every_x_hour.onPopupClosed_Repeater_X_Hour(2); // Devolver el mismo valor`
                }
            }
        });

    }
}