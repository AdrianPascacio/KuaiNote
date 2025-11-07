package com.example.kuai_notes_project;

import static android.content.Context.ALARM_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Reminder_PopUpWindow{
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    Calendar calendar = null;
    DB_Notes DB_N;
    private int position = 0;
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

    public void show(View view_brought, Note note){
        DB_N = new DB_Notes(context);
        String note_title = note.title;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.reminder_setter,null);

        popupWindow = new PopupWindow(container, 800,850 , true);
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        popupWindow.showAtLocation(view_brought, Gravity.CENTER,00,-400);
        //!!---- verificar las animaciones
        popupWindow.setAnimationStyle(0);
        //Go_To_Reminder_Setter();
        TextView label_in_reminder,name_in_reminder ;
        Button btn_set_reminder_alarm, btn_cancel_reminder_alarm;
        NumberPicker numberpicker_day, numberpicker_month, numberpicker_year, numberpicker_hour, numberpicker_minute, numberpicker_meridian;

        numberpicker_year = container.findViewById(R.id.Reminder_year_number_picker);
        numberpicker_month = container.findViewById(R.id.Reminder_month_number_picker);
        numberpicker_day = container.findViewById(R.id.Reminder_day_number_picker);
        numberpicker_hour = container.findViewById(R.id.Reminder_hour_number_picker);
        numberpicker_minute = container.findViewById(R.id.Reminder_minute_number_picker);
        numberpicker_meridian = container.findViewById(R.id.Reminder_meridian_number_picker);

        View itemView = container.findViewById(R.id.Reminder_relative_item_view);
        label_in_reminder = container.findViewById(R.id.Label_Reminder_Setter);
        name_in_reminder = container.findViewById(R.id.Note_title_in_Reminder_Setter);
        btn_set_reminder_alarm = container.findViewById(R.id.Reminder_Ok_Button);
        btn_cancel_reminder_alarm = container.findViewById(R.id.Reminder_Cancel_Button);

        //!!-- esto es un inconveniente, si no le coloco null no se coloca el color que eleji para el desde el xml
        btn_cancel_reminder_alarm.setBackgroundTintList(null);

        //int year = 0;
        //int month = 0;
        //int day = 0;
        //int time_hour = 0;
        //int time_minute = 0;
        //int time_meridian = 0;


        //!!--mejorar el minimo y maximo de dia, mes a~o, hora, minuto y meridiano

        numberpicker_day.setMinValue(1);
        numberpicker_day.setMaxValue(31);
        numberpicker_day.setValue(15);
        numberpicker_month.setMinValue(1);
        numberpicker_month.setMaxValue(12);
        numberpicker_month.setValue(6);
        numberpicker_year.setMinValue(2025);
        numberpicker_year.setMaxValue(2026);
        numberpicker_year.setValue(2025);
        numberpicker_hour.setMinValue(0);
        numberpicker_hour.setMaxValue(12);
        numberpicker_hour.setValue(6);
        numberpicker_minute.setMinValue(0);
        numberpicker_minute.setMaxValue(59);
        numberpicker_minute.setValue(30);
        numberpicker_meridian.setMinValue(0);
        numberpicker_meridian.setMaxValue(1);
        numberpicker_meridian.setValue(0);


        name_in_reminder.setText(note_title);
         btn_set_reminder_alarm.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //time_meridian = numberpicker_meridian.getValue();

                 calendar = Calendar.getInstance();
                 int previous_milli_to_random = calendar.get(Calendar.MILLISECOND);

                 calendar.set(Calendar.YEAR, numberpicker_year.getValue());
                 calendar.set(Calendar.MONTH, numberpicker_month.getValue());
                 calendar.set(Calendar.DATE, numberpicker_day.getValue());
                 calendar.set(Calendar.HOUR, numberpicker_hour.getValue());
                 calendar.set(Calendar.MINUTE, numberpicker_minute.getValue());
                 calendar.set(Calendar.SECOND, 0);
                 calendar.set(Calendar.MILLISECOND, previous_milli_to_random);

                 long alarm_Time = calendar.getTimeInMillis();
                 int upperReminder_Half = (int) (alarm_Time >>> 32);
                 int lowerReminder_Half = (int) alarm_Time;
                 int new_hashreminder = upperReminder_Half ^ lowerReminder_Half;
                 Log.d("Cancel","upper: "+upperReminder_Half + "    lower: "+lowerReminder_Half + "    hash: "+new_hashreminder);
                 Intent notificationIntent = new Intent(itemView.getContext(), Notification_Receiver.class);

                 notificationIntent.putExtra("NOTE_REMINDER_ALARM_TIME", alarm_Time);

                 //!!---reminder_type, reminder_interval llevados a 0
                 long previous_reminder = DB_N.Get_Note_Reminder(note.date);
                 Reminder_Notification.Cancel_Reminder_just_reminder(itemView,previous_reminder);

                 int _hashreminder = (int) (( alarm_Time >>> 32 ) ^ alarm_Time ); //hash creado con XOR operator (upper ^ lower)

                 if(DB_N.Modify_Reminder_Status(note.date,alarm_Time,0,0)){


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

                     if (listener != null) {
                         listener.OnValueSelected(position, alarm_Time); // Devolver el valor
                     }
                     popupWindow.dismiss();
                 }


             }
         });
         btn_cancel_reminder_alarm.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Reminder_Notification.Cancel_Reminder_Modifying_Database(itemView,note.reminder,note.date);

                 if (listener != null) {
                     listener.OnValueSelected(position, 0); // Devolver el valor
                 }
                 popupWindow.dismiss();
             }
         });
    }
}

