package com.example.kuai_notes_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Reminder_PopUpWindow  {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    Calendar calendar = null;
    DB_Notes DB_N;
    boolean action_took = false;
    private int position = 0;
    private int original_reminder_type = 0;
    private boolean repeat_alarm = false;
    Animation Animation_setter_need_update;

    public interface PopupDismissListener{//esto puede ir tambien en una clase separada
        void onPopupClosed(int salida); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private PopupDismissListener listener_dismiss;

    public interface OnValueSelectedListener{
        void OnValueSelected(int position, long alarm_Time);
    }

    private final Context context;
    private OnValueSelectedListener listener;

    public Reminder_PopUpWindow(Context context, int position){
        this.context = context;
        this.position = position;
    }

    public void setListener(OnValueSelectedListener listener){
        this.listener = listener;
    }
    public void setListener_dismiss(PopupDismissListener listener){
        this.listener_dismiss = listener;
    }

    private void Disable_Editing_NumberPicker(NumberPicker numberPicker){
        int child_Count = numberPicker.getChildCount();
        for (int i = 0; i < child_Count; i++){
            View child = numberPicker.getChildAt(i);

            if (child instanceof EditText){
                child.setFocusable(false);
                child.setFocusableInTouchMode(false);
                child.setClickable(false);

                return;
            }
        }
    }
    public void show(View view_brought, Note note){
        DB_N = new DB_Notes(context);
        String note_title = note.title;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.reminder_setter,null);
        TextView label_in_reminder,name_in_reminder ;
        FrameLayout btn_set_reminder_alarm, btn_cancel_reminder_alarm;
        View divider_1, divider_2,divider_3,divider_4 ;
        NumberPicker numberpicker_day, numberpicker_month, numberpicker_year, numberpicker_hour, numberpicker_minute, numberpicker_meridian;
        name_in_reminder = container.findViewById(R.id.Note_title_in_Reminder_Setter);
        label_in_reminder = container.findViewById(R.id.Label_Reminder_Setter);
        btn_set_reminder_alarm = container.findViewById(R.id.Reminder_Ok_Button);
        btn_cancel_reminder_alarm = container.findViewById(R.id.Reminder_Cancel_Button);
        View layout_np_container = container.findViewById(R.id.Layout_numberpicker_container);
        Space space_date = container.findViewById(R.id.Space_date);
        Space space_time = container.findViewById(R.id.Space_time);
        LinearLayout layout_date = container.findViewById(R.id.layot_space_date);
        LinearLayout layout_time = container.findViewById(R.id.layot_space_time);
        divider_1 = container.findViewById(R.id.divider1);
        divider_2 = container.findViewById(R.id.divider2);
        divider_3 = container.findViewById(R.id.divider3);
        divider_4 = container.findViewById(R.id.divider4);

        Animation_setter_need_update = AnimationUtils.loadAnimation(context, R.anim.reminder_setter_btn_need_update);

        popupWindow = new PopupWindow(container, 800,900 , true);
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        if(position == -1){
            //---note Visualizer activity
            popupWindow.setAnimationStyle(R.style.ReminderAnimationInOut_NoteVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-300);
            name_in_reminder.setVisibility(View.GONE);
            layout_np_container.setPadding(0,90,0,20);
            layout_date.setPadding(0,-20,0,0);
            layout_time.setPadding(0,-20,0,0);

        }else{
            popupWindow.setAnimationStyle(R.style.ReminderAnimationInOut_ItemVisualizer);
            popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-400);
        }

        //!!--- utiliza update para actualizar el tama~o del pop up
        //popupWindow.update(800,1200);

        numberpicker_year = container.findViewById(R.id.Reminder_year_number_picker);
        numberpicker_month = container.findViewById(R.id.Reminder_month_number_picker);
        numberpicker_day = container.findViewById(R.id.Reminder_day_number_picker);
        numberpicker_hour = container.findViewById(R.id.Reminder_hour_number_picker);
        numberpicker_minute = container.findViewById(R.id.Reminder_minute_number_picker);
        numberpicker_meridian = container.findViewById(R.id.Reminder_meridian_number_picker);

        View itemView = container.findViewById(R.id.Reminder_relative_item_view);
        View layout_set_repeat_alarm_Ghost = container.findViewById(R.id.Layout_Repeat_Ghost);
        FrameLayout fl_set_repeat_alarm = container.findViewById(R.id.FL_Repeat_Icon);

        Calendar calendar_prev =  Calendar.getInstance();

        int year_current = calendar_prev.get(Calendar.YEAR);

        Date_and_Time_Names.init_Days_Names();
        numberpicker_day.setMinValue(1);
        numberpicker_day.setMaxValue(31);
        numberpicker_day.setValue(10);
        numberpicker_day.setDisplayedValues(Date_and_Time_Names.getNameDays());


        Date_and_Time_Names.init_Month_Names();
        numberpicker_month.setMinValue(0);
        numberpicker_month.setMaxValue(11);
        numberpicker_month.setValue(10);
        numberpicker_month.setDisplayedValues(Date_and_Time_Names.getNameMonths());

        numberpicker_year.setMinValue(year_current);
        numberpicker_year.setMaxValue(year_current + 1 );
        numberpicker_year.setValue(year_current);

        Date_and_Time_Names.init_Hours_Names();
        numberpicker_hour.setMinValue(0);
        numberpicker_hour.setMaxValue(12);
        numberpicker_hour.setValue(5);
        numberpicker_hour.setDisplayedValues(Date_and_Time_Names.getNameHours());

        Date_and_Time_Names.init_Minutes_Names();
        numberpicker_minute.setMinValue(0);
        numberpicker_minute.setMaxValue(59);
        numberpicker_minute.setValue(30);
        numberpicker_minute.setDisplayedValues(Date_and_Time_Names.getNameMinutes());

        numberpicker_meridian.setMinValue(0);
        numberpicker_meridian.setMaxValue(1);
        numberpicker_meridian.setValue(0);
        numberpicker_meridian.setDisplayedValues(new String[]  {"AM","PM"});
        Disable_Editing_NumberPicker(numberpicker_year);
        Disable_Editing_NumberPicker(numberpicker_month);
        Disable_Editing_NumberPicker(numberpicker_day);
        Disable_Editing_NumberPicker(numberpicker_hour);
        Disable_Editing_NumberPicker(numberpicker_minute);
        Disable_Editing_NumberPicker(numberpicker_meridian);

        original_reminder_type = note.reminder_type;
        if( note.reminder > 0){
            calendar_prev.setTimeInMillis( note.reminder);
            repeat_alarm = note.reminder_type > 0;
        }
        if(repeat_alarm == true){
            fl_set_repeat_alarm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.repeat_alarm_on)));
            fl_set_repeat_alarm.setBackgroundResource(R.drawable.repeat_normal_2);
        }

        numberpicker_day.setValue(calendar_prev.get(Calendar.DAY_OF_MONTH));
        numberpicker_month.setValue(calendar_prev.get(Calendar.MONTH));
        numberpicker_year.setValue(calendar_prev.get(Calendar.YEAR));
        int hour = calendar_prev.get(Calendar.HOUR_OF_DAY);
        if(hour > 12){
            numberpicker_meridian.setValue(1);
            hour -= 12;
        }
        numberpicker_hour.setValue(hour);
        numberpicker_minute.setValue(calendar_prev.get(Calendar.MINUTE));

        name_in_reminder.setText(   position == -1 ? "" :   note_title ); //Ternary Operator

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){ @Override
        public void onDismiss(){
            if(!action_took){
                note.reminder_type = original_reminder_type;
                Reminder_PopUpWindow.this.listener_dismiss.onPopupClosed(0);
            }
        }
        });


        layout_set_repeat_alarm_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //!!--- habilitar cuando se oportuno la capacidad de integrar otros tipos de repeticion
                if(note.reminder_type > 0){
                    note.reminder_type = 0;
                    fl_set_repeat_alarm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.repeat_alarm_off)));
                    fl_set_repeat_alarm.setBackgroundResource(R.drawable.repeat_never_2);
                }else{
                    note.reminder_type = 1;
                    note.reminder_interval = 1; //24horas en milisegundos
                    fl_set_repeat_alarm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.repeat_alarm_on)));
                    fl_set_repeat_alarm.setBackgroundResource(R.drawable.repeat_normal_2);
                }

                if(note.reminder_type != original_reminder_type){
                    btn_set_reminder_alarm.startAnimation(Animation_setter_need_update);
                }else{
                    Toast.makeText(context, "es igual", Toast.LENGTH_SHORT).show();
                    btn_set_reminder_alarm.clearAnimation();
                }
                //btn_set_reminder_alarm.setScaleX(1.1f);
                //btn_set_reminder_alarm.setScaleY(1.1f);

            }
        });
        btn_set_reminder_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //time_meridian = numberpicker_meridian.getValue();

                calendar = GregorianCalendar.getInstance();
                Log.d("Calendar","hour:"+calendar.getTime());
                calendar = Calendar.getInstance();
                Log.d("Calendar","hour:"+calendar.getTime());
                int previous_milli_to_random = calendar.get(Calendar.MILLISECOND);

                calendar.set(Calendar.YEAR, numberpicker_year.getValue());
                calendar.set(Calendar.MONTH, numberpicker_month.getValue());
                calendar.set(Calendar.DAY_OF_MONTH, numberpicker_day.getValue());
                int meridian = numberpicker_meridian.getValue();
                int hour = numberpicker_hour.getValue();
                if(meridian == 1){
                    Log.d("Setter Calendar","Meridian:"+meridian);
                    Log.d("Setter Calendar","hour before:"+hour);
                    hour += 12;
                    Log.d("Setter Calendar","hour after:"+hour);
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, numberpicker_minute.getValue());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, previous_milli_to_random);

                long alarm_Time = calendar.getTimeInMillis();
                Intent notificationIntent = new Intent(itemView.getContext(), Notification_Receiver.class);

                if(note.note_id == 0 ){
                    note.note_id=DB_N.Insert_Note_L(note.date,note.title,note.note,note.pin,note.reminder,note.reminder_type,note.reminder_interval);
                }
                notificationIntent.putExtra("NOTE_REMINDER_ALARM_TIME", alarm_Time);
                notificationIntent.putExtra("NOTE_ID", note.note_id);

                //!!---reminder_type, reminder_interval llevados a 0
                if( note.reminder > 0){
                    Reminder_Notification.Cancel_Reminder_Alarm(itemView,note.note_id);
                }


                int _hashreminder = (int) (( note.note_id >>> 32 ) ^ note.note_id ); //hash creado con XOR operator (upper ^ lower)

                //!!--- cuando tenga type and interval se debe corregir
                if(DB_N.Modify_Reminder_Status(note.note_id,alarm_Time,note.reminder_type,0)){


                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            itemView.getContext(),
                            _hashreminder,
                            notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE
                    );

                    //Ya que no es un activity se debe cambiar: AlarmManager alarmManager = (AlarmManager) getSystemService(context,ALARM_SERVICE);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    if( alarmManager != null ){
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                alarm_Time,
                                pendingIntent
                        );
                    }

                    action_took = true;

                    if (listener != null) {
                        listener.OnValueSelected(position, alarm_Time); // Devolver el valor
                    }
                    popupWindow.dismiss();
                    if (listener_dismiss != null) {
                        listener_dismiss.onPopupClosed(1); // Devolver el valor
                    }
                }
            }
        });
        btn_cancel_reminder_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reminder_Notification.Cancel_Reminder_Modifying_Database(itemView,note.reminder,note.note_id);

                action_took = true;

                if (listener != null) {
                    listener.OnValueSelected(position, 0); // Devolver el valor
                }
                popupWindow.dismiss();
                if (listener_dismiss != null) {
                    listener_dismiss.onPopupClosed(2); // Devolver el valor
                }
            }
        });
    }
}

