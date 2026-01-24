package com.example.kuai_notes_project;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.kuai_notes_project.ruled_out_code.Every_X_Hour_WindowPopUp;

public class Every_Day_Of_The_Week {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    private int position = 0;
    private final Context context;
    private int  mon_val = 0, tus_val = 0, wed_val = 0,thu_val = 0,fri_val = 0,sat_val = 0 ,sun_val = 0;
    private int days_of_the_week_picked;

    public Every_Day_Of_The_Week(Context context, int position){
        this.context = context;
        this.position = position;
    }

    public interface OnValueSelectedListener_Every_Day_Of_Week{
        void OnValueSelected_Repeater_every_day_of_week(int days_of_week_picked);
    }
    public interface PopupDismissListener_Repeater_Day_Of_Week{
        void OnPopupClosed_Repeater_Day_Of_Week(int salida);
    }

    OnValueSelectedListener_Every_Day_Of_Week listenerEveryDayOfWeek;
    PopupDismissListener_Repeater_Day_Of_Week listener_dismiss_every_day_of_week;

    public void setListener_every_day_of_week(OnValueSelectedListener_Every_Day_Of_Week listener_every_day_of_week){
        this.listenerEveryDayOfWeek = listener_every_day_of_week;
    }
    public void setListener_dismiss_every_day_of_week(PopupDismissListener_Repeater_Day_Of_Week listener_dismiss_every_day_of_week){
        this.listener_dismiss_every_day_of_week = listener_dismiss_every_day_of_week;
    }

    public void show (View view_brought, int note_reminder_type, int note_reminder_interval){
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)  layoutInflater.inflate(R.layout.days_of_week,null);
        TextView mo,tu,we,th,fr,sa,su;
        FrameLayout repeater_day_of_week_ok_button = container.findViewById(R.id.Repeater_Day_of_Week_Ok_Button);
        FrameLayout repeater_day_of_week_cancel_button = container.findViewById(R.id.Repeater_Day_of_Week_Cancel_Button);

        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        if (position == -1){
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-300);
        }else{
            popupWindow.setAnimationStyle(R.style.SpecialRepeaterAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-400);
        }

            mo = container.findViewById(R.id.Repeater_Day_of_Week_MO);
            tu = container.findViewById(R.id.Repeater_Day_of_Week_TU);
            we = container.findViewById(R.id.Repeater_Day_of_Week_WE);
            th = container.findViewById(R.id.Repeater_Day_of_Week_TH);
            fr = container.findViewById(R.id.Repeater_Day_of_Week_FR);
            sa = container.findViewById(R.id.Repeater_Day_of_Week_SA);
            su = container.findViewById(R.id.Repeater_Day_of_Week_SU);

        if(note_reminder_type == 3){
            mo.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 1 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            tu.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 2 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            we.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 4 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            th.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 8 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            fr.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 16 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            sa.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 32 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            su.setTextColor(ContextCompat.getColor(context, (note_reminder_interval & 64 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and AND Operator
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){ @Override
        public void onDismiss(){
            Every_Day_Of_The_Week.this.listener_dismiss_every_day_of_week.OnPopupClosed_Repeater_Day_Of_Week(3);
        }
        });
        mo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 1; ///XOR operator to toggle result!!
                mo.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 1 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator

            }
        });
        tu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 2; ///XOR operator to toggle result!!
                tu.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 2 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            }
        });
        we.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 4; ///XOR operator to toggle result!!
                we.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 4 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            }
        });
        th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 8; ///XOR operator to toggle result!!
                th.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 8 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            }
        });
        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 16; ///XOR operator to toggle result!!
                fr.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 16 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            }
        });
        sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 32; ///XOR operator to toggle result!!
                sa.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 32 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and XOR Operator
            }
        });
        su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days_of_the_week_picked = days_of_the_week_picked ^ 64; ///XOR operator to toggle result!!
                su.setTextColor(ContextCompat.getColor(context, (days_of_the_week_picked & 64 )> 0 /*:- ///AND Operator!! -*/? R.color.ex_orange : R.color.Neutral_gray_icon_note)); ///Ternary Operator and AND Operator
            }
        });
        repeater_day_of_week_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listenerEveryDayOfWeek != null){
                    //days_of_the_week_picked = mon_val + tus_val + wed_val + thu_val + fri_val + sat_val + sun_val;
                    listenerEveryDayOfWeek.OnValueSelected_Repeater_every_day_of_week(days_of_the_week_picked);

                }
                popupWindow.dismiss();
                if(listener_dismiss_every_day_of_week != null){
                    listener_dismiss_every_day_of_week.OnPopupClosed_Repeater_Day_Of_Week(3);
                }
            }
        });
        repeater_day_of_week_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if(listener_dismiss_every_day_of_week != null){
                    listener_dismiss_every_day_of_week.OnPopupClosed_Repeater_Day_Of_Week(3);
                }
            }
        });



    }
}
