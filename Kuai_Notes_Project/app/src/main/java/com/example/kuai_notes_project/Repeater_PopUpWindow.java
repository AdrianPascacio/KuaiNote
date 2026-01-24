package com.example.kuai_notes_project;

import android.content.Context;
import android.text.Highlights;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.kuai_notes_project.ruled_out_code.Every_X_Hour_WindowPopUp;

import java.util.Calendar;

public class Repeater_PopUpWindow implements Every_X_Hour_WindowPopUp.OnValueSelectedListener_Every_X_Hour, Every_X_Hour_WindowPopUp.PopupDismissListener_Repeater_X_Hour,Every_Day_Of_The_Week.OnValueSelectedListener_Every_Day_Of_Week, Every_Day_Of_The_Week.PopupDismissListener_Repeater_Day_Of_Week, Every_Day_Of_Month.OnValueSelectedListener_Every_Day_Of_Month, Every_Day_Of_Month.PopupDismiissListener_Repeater_Every_Day_Of_Month {

    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    private View view_brought;

    private int position = 0;
    int type_of_note = 0;
    DB_Notes DB_N;
    boolean action_took = false;
    private int original_reminder_type = 0;
    private int original_reminder_interval = 0;
    private int note_reminder_type = 0;
    private int note_reminder_interval = 0;
    private boolean repeat_alarm = false;
    Animation Animation_setter_need_update,AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal;
    TextView label_none, label_every_day, label_every_x_hour, label_every_day_of_week, label_every_day_of_month;
    View layout_dim;

    @Override
    public void OnValueSelected_Repeater_every_x_hour(int hour_interval) {
        if (listener_repeater_every_x_hour != null) {
            listener_repeater_every_x_hour.OnValueSelected_Repeater_type_Every_X_Hour(2, hour_interval); // Devolver el nuevo tipo de repeater
        }

        popupWindow.dismiss();
        if (listener_dismiss_repeater != null) {
            listener_dismiss_repeater.onPopupClosed_Repeater(2); // Devolver el mismo valor`
        }

    }

    @Override
    public void OnValueSelected_Repeater_every_day_of_week(int days_of_week_picked) {
        //Toast.makeText(context, "days picked: " + days_of_week_picked, Toast.LENGTH_SHORT).show();
        listener_repeater_every_day_of_week.OnValueSelected_Repeater_type_Every_Day_Of_Week(3, days_of_week_picked); // Devolver el nuevo tipo de repeater

        popupWindow.dismiss();
        if (listener_dismiss_repeater != null) {
            listener_dismiss_repeater.onPopupClosed_Repeater(3); // Devolver el mismo valor`
        }

    }

    @Override
    public void OnValueSelected_Repeater_every_day_of_month(int day_of_month) {
        //Toast.makeText(context, "days picked: " + days_of_week_picked, Toast.LENGTH_SHORT).show();
        listener_repeater_every_day_of_month.OnValueSelected_Repeater_type_Every_Day_Of_Month(4, day_of_month); // Devolver el nuevo tipo de repeater

        popupWindow.dismiss();
        if (listener_dismiss_repeater != null) {
            listener_dismiss_repeater.onPopupClosed_Repeater(4); // Devolver el mismo valor`
        }

    }

    @Override
    public void onPopupClosed_Repeater_X_Hour(int salida) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);
    }

    @Override
    public void OnPopupClosed_Repeater_Day_Of_Week(int salida) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);
    }


    @Override
    public void onPopupClosed_Repeater_Every_Day_Of_Month(int salida) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);
    }


    public interface OnValueSelectedListener_Repeater_None{
        void OnValueSelected_Repeater_type_none(int reminder_repeater_type);
    }
    public interface OnValueSelectedListener_Repeater{
        void OnValueSelected_Repeater_type_daily(int reminder_repeater_type, int reminder_repeater_interval);
    }
    public interface OnValueSelectedListener_Repeater_Every_X_Hour{
        void OnValueSelected_Repeater_type_Every_X_Hour(int reminder_repeater_type, int reminder_repeater_interval);
    }
    public interface OnValueSelectedListener_Repeater_Every_Day_Of_Week{
        void OnValueSelected_Repeater_type_Every_Day_Of_Week(int reminder_repeater_type, int reminder_repeater_interval);
    }
    public interface OnValueSelectedListener_Repeater_Every_Day_Of_Month{
        void OnValueSelected_Repeater_type_Every_Day_Of_Month(int reminder_repeater_type, int reminder_repeater_interval);
    }
    public interface PopupDismissListener_Repeater{//esto puede ir tambien en una clase separada
        void onPopupClosed_Repeater(int salida); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }

    private final Context context;
    //private Reminder_PopUpWindow.OnValueSelectedListener listener;
    //private Reminder_PopUpWindow.PopupDismissListener listener_dismiss;
    private OnValueSelectedListener_Repeater_None listener_repeater_none;
    private OnValueSelectedListener_Repeater listener_repeater_daily;
    private OnValueSelectedListener_Repeater_Every_X_Hour listener_repeater_every_x_hour;
    private OnValueSelectedListener_Repeater_Every_Day_Of_Week listener_repeater_every_day_of_week;
    private OnValueSelectedListener_Repeater_Every_Day_Of_Month listener_repeater_every_day_of_month;
    private PopupDismissListener_Repeater listener_dismiss_repeater;

    public Repeater_PopUpWindow(Context context, int position){
        this.context = context;
        this.position = position;
    }

    public void setListener(OnValueSelectedListener_Repeater listener_repeater){
        this.listener_repeater_daily = listener_repeater;
    }
    public void setListener_repeater_every_x_hour(OnValueSelectedListener_Repeater_Every_X_Hour listener_repeater_every_x_hour){
        this.listener_repeater_every_x_hour = listener_repeater_every_x_hour;
    }
    public void setListener_repeater_every_day_of_week(OnValueSelectedListener_Repeater_Every_Day_Of_Week listener_repeater_every_day_of_week){
        this.listener_repeater_every_day_of_week = listener_repeater_every_day_of_week;
    }
    public void setListener_repeater_every_day_of_month(OnValueSelectedListener_Repeater_Every_Day_Of_Month listener_repeater_every_day_of_month){
        this.listener_repeater_every_day_of_month = listener_repeater_every_day_of_month;
    }
    public void setListener_none(OnValueSelectedListener_Repeater_None listener_repeater_none){
        this.listener_repeater_none = listener_repeater_none;
    }
    public void setListener_dismiss(PopupDismissListener_Repeater listener_dismiss_repeater){
        this.listener_dismiss_repeater = listener_dismiss_repeater;
    }
    public void show(View view_brought, Note note, int n_reminder_type, int n_reminder_interval, int day_picked){
        //!! el parametro Note note que es traido de el reminderpopupwindow, no se esta actualizando como espero, se intenta actualizar con un listener, pero no surte efecto, es por esta razon que se incluyeron los parametros de tipo int (n_reminder_type y n_reminder_interval)
        //!! esta funcionando como note y no como el this.note traido desde Reminder_PopUpWindow
        this.view_brought = view_brought;

        DB_N = new DB_Notes(context);
        String note_title = note.title;
        note_reminder_interval = n_reminder_interval;
        note_reminder_type = n_reminder_type;
        original_reminder_type = note.reminder_type;
        original_reminder_interval = note.reminder_interval;

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.repeat_setter,null);
        FrameLayout btn_cancel_reminder_alarm;
        btn_cancel_reminder_alarm = container.findViewById(R.id.Repeater_Setter_Cancel_Button);
        layout_dim = container.findViewById(R.id.Layout_Dim_Repeater_Setter);

        label_none = container.findViewById(R.id.Repeater_Label_None);
        label_every_day = container.findViewById(R.id.Repeater_Label_Every_Day);
        label_every_x_hour = container.findViewById(R.id.Repeater_Label_Every_X_Hour);
        label_every_day_of_week = container.findViewById(R.id.Repeater_Label_Every_Day_Of_Week);
        label_every_day_of_month = container.findViewById(R.id.Repeater_Label_Every_Day_Of_Month);

        if(note.reminder_interval == n_reminder_interval && note.reminder_type == n_reminder_type){
            Highlight_Type_Of_Reminder();
        }else{
            Highlight_Type_Of_Reminder_Not_Updated();
        }

        Animation_setter_need_update = AnimationUtils.loadAnimation(context, R.anim.reminder_setter_btn_need_update);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(context, R.anim.layout_dim_appear_repeater_setter);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(context, R.anim.layout_dim_disappear_repeater_setter);

        int container_width = container.getWidth();
        //Toast.makeText(context, "container_w: "+container_width, Toast.LENGTH_SHORT).show();


        //popupWindow = new PopupWindow(container, 800,900 , true);
        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT , true);
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        if(position == -1){
            //---note Visualizer activity
            popupWindow.setAnimationStyle(R.style.RepeaterReminderAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-300);

        }else{
            popupWindow.setAnimationStyle(R.style.RepeaterReminderAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-400);
        }

        //!!--- utiliza update para actualizar el tama~o del pop up
        //popupWindow.update(800,1200);


        View itemView = container.findViewById(R.id.Reminder_relative_item_view);
        View layout_set_repeat_alarm_Ghost = container.findViewById(R.id.Layout_Repeat_Ghost);
        FrameLayout fl_set_repeat_alarm = container.findViewById(R.id.FL_Repeat_Icon);

        Calendar calendar_prev =  Calendar.getInstance();

        int year_current = calendar_prev.get(Calendar.YEAR);

        if( note.reminder > 0){
            calendar_prev.setTimeInMillis( note.reminder);
            repeat_alarm = note.reminder_type > 0;
        }
        if(repeat_alarm == true){
            //fl_set_repeat_alarm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.repeat_alarm_on)));
            //fl_set_repeat_alarm.setBackgroundResource(R.drawable.repeat_normal_2);
        }

        int hour = calendar_prev.get(Calendar.HOUR_OF_DAY);


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){ @Override
        public void onDismiss(){
            if(!action_took){
                note.reminder_type = original_reminder_type;
                note.reminder_interval = original_reminder_interval;
                Repeater_PopUpWindow.this.listener_dismiss_repeater.onPopupClosed_Repeater(original_reminder_type);
            }
        }
        });


        //layout_set_repeat_alarm_Ghost.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        //!!--- habilitar cuando se oportuno la capacidad de integrar otros tipos de repeticion
        //        if(note.reminder_type > 0){
        //            note.reminder_type = 0;
        //            fl_set_repeat_alarm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.repeat_alarm_off)));
        //            fl_set_repeat_alarm.setBackgroundResource(R.drawable.repeat_never_2);
        //        }else{
        //            note.reminder_type = 1;
        //            note.reminder_interval = 1; //24horas en milisegundos
        //            fl_set_repeat_alarm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.repeat_alarm_on)));
        //            fl_set_repeat_alarm.setBackgroundResource(R.drawable.repeat_normal_2);
        //        }

        //        if(note.reminder_type != original_reminder_type){
        //            btn_set_reminder_alarm.startAnimation(Animation_setter_need_update);
        //        }else{
        //            Toast.makeText(context, "es igual", Toast.LENGTH_SHORT).show();
        //            btn_set_reminder_alarm.clearAnimation();
        //        }
        //        //btn_set_reminder_alarm.setScaleX(1.1f);
        //        //btn_set_reminder_alarm.setScaleY(1.1f);

        //    }
        //});
        label_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener_repeater_none != null) {
                    listener_repeater_none.OnValueSelected_Repeater_type_none(0); // Devolver el nuevo tipo de repeater
                }

                popupWindow.dismiss();
                if (listener_dismiss_repeater != null) {
                    listener_dismiss_repeater.onPopupClosed_Repeater(note.reminder_type); // Devolver el mismo valor`
                }
            }
        });
        btn_cancel_reminder_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener_dismiss_repeater != null) {
                    listener_dismiss_repeater.onPopupClosed_Repeater(note.reminder_type); // Devolver el mismo valor`
                }
            }
        });
        label_every_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener_repeater_daily != null) {
                    listener_repeater_daily.OnValueSelected_Repeater_type_daily(1, 86400000); // Devolver el nuevo tipo de repeater
                }

                popupWindow.dismiss();
                if (listener_dismiss_repeater != null) {
                    listener_dismiss_repeater.onPopupClosed_Repeater(note.reminder_type); // Devolver el mismo valor`
                }
            }
        });
        label_every_x_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///if (listener_repeater_every_x_hour != null) {
                ///    listener_repeater_every_x_hour.OnValueSelected_Repeater_type_Every_X_Hour(2); // Devolver el nuevo tipo de repeater
                ///}

                ///popupWindow.dismiss();
                ///if (listener_dismiss_repeater != null) {
                ///    listener_dismiss_repeater.onPopupClosed_Repeater(note.reminder_type); // Devolver el mismo valor`
                ///}
                layout_dim.setVisibility(View.VISIBLE);
                layout_dim.startAnimation(AnimationLayoutDimAppear);
                Set_Every_X_Hour_Repeater(note_reminder_type,note_reminder_interval);
            }
        });
        label_every_day_of_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_dim.setVisibility(View.VISIBLE);
                layout_dim.startAnimation(AnimationLayoutDimAppear);
                Set_Every_Day_Of_The_Week(note_reminder_type,note_reminder_interval);
            }
        });
        label_every_day_of_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_dim.setVisibility(View.VISIBLE);
                layout_dim.startAnimation(AnimationLayoutDimAppear);
                Set_Every_Day_Of_The_Month(note_reminder_type,note_reminder_interval, day_picked);
            }
        });
    }

    private void Highlight_Type_Of_Reminder() {
        label_none.setTextColor(ContextCompat.getColor(context, note_reminder_type == 0 ? R.color.ex_orange : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_day.setTextColor(ContextCompat.getColor(context, note_reminder_type == 1 ? R.color.ex_orange : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_x_hour.setTextColor(ContextCompat.getColor(context, note_reminder_type == 2 ? R.color.ex_orange : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_day_of_week.setTextColor(ContextCompat.getColor(context, note_reminder_type == 3 ? R.color.ex_orange : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_day_of_month.setTextColor(ContextCompat.getColor(context, note_reminder_type == 4 ? R.color.ex_orange : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
    }

    private void Highlight_Type_Of_Reminder_Not_Updated() {
        label_none.setTextColor(ContextCompat.getColor(context, note_reminder_type == 0 ? R.color.matcha_1_trans : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_day.setTextColor(ContextCompat.getColor(context, note_reminder_type == 1 ? R.color.matcha_1_trans : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_x_hour.setTextColor(ContextCompat.getColor(context, note_reminder_type == 2 ? R.color.matcha_1_trans : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_day_of_week.setTextColor(ContextCompat.getColor(context, note_reminder_type == 3 ? R.color.matcha_1_trans : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
        label_every_day_of_month.setTextColor(ContextCompat.getColor(context, note_reminder_type == 4 ? R.color.matcha_1_trans : R.color.item_visualizer_preview_text_color)); ///Ternary Operator and XOR Operator
    }

    private void Set_Every_X_Hour_Repeater(int note_reminder_type, int note_remider_interval) {
        Every_X_Hour_WindowPopUp repeater_PopUp = new Every_X_Hour_WindowPopUp(context, position);
        repeater_PopUp.setListener_every_x_hour(this);
        repeater_PopUp.setListener_dismiss_every_x_hour(this);

        repeater_PopUp.show(view_brought, note_reminder_type, note_remider_interval);
    }

    private void Set_Every_Day_Of_The_Week(int note_reminder_type, int note_remider_interval) {
        Every_Day_Of_The_Week repeater_PopUp = new Every_Day_Of_The_Week(context, position);
        repeater_PopUp.setListener_every_day_of_week(this);
        repeater_PopUp.setListener_dismiss_every_day_of_week(this);

        repeater_PopUp.show(view_brought, note_reminder_type, note_remider_interval);
    }
    private void Set_Every_Day_Of_The_Month(int note_reminder_type, int note_remider_interval, int day_picked) {
        Every_Day_Of_Month repeater_PopUp = new Every_Day_Of_Month(context, position);
        repeater_PopUp.setListener_every_day_of_month(this);
        repeater_PopUp.setListener_dismiss_every_day_of_month(this);

        repeater_PopUp.show(view_brought, note_reminder_type, note_remider_interval, day_picked);
    }
}
