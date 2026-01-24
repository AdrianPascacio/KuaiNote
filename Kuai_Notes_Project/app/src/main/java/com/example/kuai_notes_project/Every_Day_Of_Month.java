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

public class Every_Day_Of_Month {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    private int position = 0;
    private final Context context;

    private int day_of_month;

    public Every_Day_Of_Month(Context context, int position){
        this.context = context;
        this.position = position;
    }

    public interface OnValueSelectedListener_Every_Day_Of_Month{
        void OnValueSelected_Repeater_every_day_of_month(int day_of_month);
    }
    public interface PopupDismiissListener_Repeater_Every_Day_Of_Month{
        void onPopupClosed_Repeater_Every_Day_Of_Month(int salida);
    }

    OnValueSelectedListener_Every_Day_Of_Month listenerEveryDayOfMonth;
    PopupDismiissListener_Repeater_Every_Day_Of_Month listener_dismiss_every_day_of_month;

    public void setListener_every_day_of_month(OnValueSelectedListener_Every_Day_Of_Month listenerEveryDayOfMonth){
        this.listenerEveryDayOfMonth = listenerEveryDayOfMonth;
    }
    public void setListener_dismiss_every_day_of_month(PopupDismiissListener_Repeater_Every_Day_Of_Month listener_dismiss_every_day_of_month){
        this.listener_dismiss_every_day_of_month = listener_dismiss_every_day_of_month;
    }
    public void show(View view_brought, int note_reminder_type, int note_reminder_interval, int day_picked){

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.day_of_month_windowpopup,null);
        NumberPicker numberPicker_day_of_month;
        FrameLayout repeater_day_of_month_ok_button = container.findViewById(R.id.Repeater_Every_Day_Of_Month_Ok_Button);
        FrameLayout repeater_day_of_month_cancel_button = container.findViewById(R.id.Repeater_Every_Day_Of_Month_Cancel_Button);

        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        if(position == -1){
            //---note Visualiizer Activity
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00, -300);
        }else{
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00, -400);
        }
        numberPicker_day_of_month = container.findViewById(R.id.NumberPicker_Every_Day_Of_Month);
        Date_and_Time_Names.init_Days_Names();
        numberPicker_day_of_month.setMinValue(1);
        numberPicker_day_of_month.setMaxValue(31);
        //!!---- analizar si cuando no tiene una fecha programada se debe colocar el dia de hoy como un valor por defecto en vez de "1"
        numberPicker_day_of_month.setValue(note_reminder_type == 4 ? note_reminder_interval : day_picked);///Ternary Operator
        numberPicker_day_of_month.setDisplayedValues(Date_and_Time_Names.getNameDays());

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Every_Day_Of_Month.this.listener_dismiss_every_day_of_month.onPopupClosed_Repeater_Every_Day_Of_Month(4);
            }
        });

        repeater_day_of_month_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listenerEveryDayOfMonth != null) {
                    day_of_month = numberPicker_day_of_month.getValue();
                    listenerEveryDayOfMonth.OnValueSelected_Repeater_every_day_of_month(day_of_month); //!!--- al llegar el dato al reminder principal debe mover el dia escogido si este no coincide
                }

                popupWindow.dismiss();
                if (listener_dismiss_every_day_of_month != null) {
                    listener_dismiss_every_day_of_month.onPopupClosed_Repeater_Every_Day_Of_Month(4); // Devolver el mismo valor`
                }
            }
        });
        repeater_day_of_month_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if(listener_dismiss_every_day_of_month != null){
                    listener_dismiss_every_day_of_month.onPopupClosed_Repeater_Every_Day_Of_Month(4);
                }
            }
        });






    }
}
