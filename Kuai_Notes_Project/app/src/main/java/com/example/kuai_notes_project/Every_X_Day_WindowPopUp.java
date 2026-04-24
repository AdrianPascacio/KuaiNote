package com.example.kuai_notes_project;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;

public class Every_X_Day_WindowPopUp {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    private int position = 0;
    private final Context context;
    private int days_interval = 0;

    public Every_X_Day_WindowPopUp(Context context, int position){
        this.context = context;
        this.position = position;
    }

    public interface OnValueSelectedListener_Every_X_Day{
        void OnValueSelected_Repeater_every_x_day(int day_interval);
    }
    public interface PopupDismissListener_Repeater_X_Day{//esto puede ir tambien en una clase separada
        void onPopupClosed_Repeater_X_Day(int salida); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }

    OnValueSelectedListener_Every_X_Day listenerEveryXDay;
    PopupDismissListener_Repeater_X_Day listener_dismiss_every_x_day;

    public void setListener_every_x_day(OnValueSelectedListener_Every_X_Day listenerEveryXDay){
        this.listenerEveryXDay = listenerEveryXDay;
    }
    public void setListener_dismiss_every_x_day(PopupDismissListener_Repeater_X_Day listener_dismiss_every_x_day){
        this.listener_dismiss_every_x_day = listener_dismiss_every_x_day;
    }

    public void show(View view_brought, int note_reminder_type, int note_reminder_interval) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.every_x_day_repeat_windowpopup,null);
        NumberPicker numberpicker_day;
        FrameLayout repeater_day_ok_button = container.findViewById(R.id.Repeater_Every_X_Day_Ok_Button);
        FrameLayout repeater_day_cancel_button = container.findViewById(R.id.Repeater_Every_X_Day_Cancel_Button);

        int selected_day = note_reminder_interval / (1000 * 60 * 60);
        int selected_minute = (note_reminder_interval - (selected_day * 1000 * 60 * 60)) / (1000*60*15);//!! 15 debido a los 15 minutos


        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        if (position == -1) {
            //---note Visualizer activity
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER, 00, -300);

        } else {
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER, 00, -400);
        }

        numberpicker_day = container.findViewById(R.id.Repeater_Every_X_Day_Number);

        Date_and_Time_Names.init_Days_Names();

        Date_and_Time_Names.init_RepeaterHours_Names();
        numberpicker_day.setMinValue(1);
        numberpicker_day.setMaxValue(100); //!!-----Debe cambiarse
        ///numberpicker_day.setValue(1);
        numberpicker_day.setValue(1);
        //numberpicker_day.setDisplayedValues(Date_and_Time_Names.getNameRepeater_Hours()); //!!--Debe cambiarse a los dias disponibles para repetirse




        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){ @Override
        public void onDismiss(){
                Every_X_Day_WindowPopUp.this.listener_dismiss_every_x_day.onPopupClosed_Repeater_X_Day(5);
        }
        });
        repeater_day_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listenerEveryXDay != null) {
                    days_interval = numberpicker_day.getValue() * 86400000;
                    //days_interval = numberpicker_day.getValue();
                    listenerEveryXDay.OnValueSelected_Repeater_every_x_day(days_interval); //!! Devolver el intervalo expresado en milisegundos
                }

                popupWindow.dismiss();
                if (listener_dismiss_every_x_day != null) {
                    listener_dismiss_every_x_day.onPopupClosed_Repeater_X_Day(5); // Devolver el mismo valor`
                }
            }
        });
        repeater_day_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener_dismiss_every_x_day != null) {
                    listener_dismiss_every_x_day.onPopupClosed_Repeater_X_Day(5); // Devolver el mismo valor`
                }
            }
        });

    }
}