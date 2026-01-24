package com.example.kuai_notes_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;


public class Notification_Receiver extends BroadcastReceiver {
    //El BroadcastReceiver es el metodo que se quedara abierto siempre incluso si la aplicacion se cierra

    DB_Notes DB_N;
    Body_Note_Preview BNP;
    private LocalDate today = LocalDate.now();
    private int days_until_picked_day_of_week = 0;
    Calendar calendar = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        DB_N = new DB_Notes(context);
        BNP = new Body_Note_Preview();

        //long Reminder_alarmTime =  intent.getLongExtra("NOTE_REMINDER_ALARM_TIME",0L) ;
        long note_id =  intent.getLongExtra("NOTE_ID",0L) ;

        Note note = DB_N.getASpecificNote(note_id);
        String title =  note.title;
        String content =  BNP.Set_Body_Note_Preview(note.title,note.note,
                60,
                55,
                10,
                1,
                1,
                30);
        String bigText =  BNP.Set_Body_Note_Preview(note.title,note.note,
                60,
                55,
                10,
                4,
                1,
                30);;
        long note_date =  note.date;
        int _hashreminder = (int) (( note_id >>> 32 ) ^ note_id ); //hash creado con XOR operator (upper ^ lower)

        Reminder_Notification.sendNotification(context,title,content, bigText, note_date, _hashreminder,note_id );


        if(note.reminder_type == 0){
            if(DB_N.Modify_Reminder_Status(note.note_id,0,0,0)){
                Toast.makeText(context, "Deleted After Alarm", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
            }
        }else if(note.reminder_type == 1){
            //!! se deben agregar los otros tipos de reminder
            Intent notificationIntent = new Intent(context, Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    _hashreminder,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if( alarmManager != null ){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        //!!---corregir, en vez de guardar el intervalo, deberia guardarse el tipo de intervalo y luego calcular aqui cuantos milisegundos
                        note.reminder+86400000,
                        pendingIntent
                );
                if(DB_N.Modify_Reminder_Status(note.note_id,note.reminder+note.reminder_interval,note.reminder_type,note.reminder_interval)){
                    Toast.makeText(context, "Alarma repetida", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
                }
            }

        }else if( note.reminder_type == 2){
            //!! esto esta repetido, deberia funcionar solo con una condicional, no agregar todas las opciones aqui, a menos que sea necesario
            Intent notificationIntent = new Intent(context, Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    _hashreminder,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if( alarmManager != null ){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        //!!---corregir, en vez de guardar el intervalo, deberia guardarse el tipo de intervalo y luego calcular aqui cuantos milisegundos
                        note.reminder+note.reminder_interval,
                        pendingIntent
                );
                if(DB_N.Modify_Reminder_Status(note.note_id,note.reminder+note.reminder_interval,note.reminder_type,note.reminder_interval)){
                    Toast.makeText(context, "Alarma repetida", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
                }
            }

        }else if( note.reminder_type == 3){
            Toast.makeText(context, "tipo 3 No reprogrmada aun", Toast.LENGTH_SHORT).show();



            LocalDate corrected_date = today.plusDays(1);
            DayOfWeek dow_ori = today.getDayOfWeek();
            DayOfWeek dow = corrected_date.getDayOfWeek();
            int dow_number_ori = dow_ori.getValue();
            int dow_number = dow.getValue();

            if((note.reminder_interval & ((int) Math.pow(2 ,dow_number -1 ))) == 0){ /// si el dia de hoy no fue seleccionado se debe agregar la cantidad de dias restantes al reminder
                days_until_picked_day_of_week = 0;
                for (int i = 1 ; i <= 7 ; i ++){
                    if( (note.reminder_interval & ((int) Math.pow(2 ,dow_number -1 ))) == 0){
                        if(dow_number == 7){
                            dow_number = 1;
                        }else{
                            dow_number ++;
                        }
                        days_until_picked_day_of_week ++;
                    }else{
                        break;
                    }
                }
                Toast.makeText(context, "days ++" + days_until_picked_day_of_week, Toast.LENGTH_SHORT).show();
            }



            Intent notificationIntent = new Intent(context, Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    _hashreminder,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if( alarmManager != null ){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        //!!---corregir, en vez de guardar el intervalo, deberia guardarse el tipo de intervalo y luego calcular aqui cuantos milisegundos
                        note.reminder+(86400000 *(1 + days_until_picked_day_of_week) ),
                        pendingIntent
                );
                if(DB_N.Modify_Reminder_Status(note.note_id,note.reminder+(86400000 *(1 + days_until_picked_day_of_week) ),note.reminder_type,note.reminder_interval)){
                    Toast.makeText(context, "Alarma repetida", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
                }
            }


        }else if (note.reminder_type == 4){

            //-- Comprobacion de que el dia escogido existe segun el mes o correccion:
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(note.reminder);
            calendar.set(Calendar.DAY_OF_MONTH, 1);//--Se colocara el dia uno de mes para que al sumar el mes siguiente no se desborde por accidente (30 de enero + 1 mes =  2 de marzo == ERROR)
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);//--Se agrega un mes adicional

            int max_day_of_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Toast.makeText(context, "max: " + max_day_of_month + "interv: " + note.reminder_interval, Toast.LENGTH_SHORT).show();
            if(max_day_of_month < note.reminder_interval){
                calendar.set(Calendar.DAY_OF_MONTH, max_day_of_month);
            }else{
                calendar.set(Calendar.DAY_OF_MONTH, note.reminder_interval);
                Toast.makeText(context, "callen day: " + calendar.get(Calendar.DAY_OF_MONTH  ), Toast.LENGTH_SHORT).show();
            }
            note.reminder = calendar.getTimeInMillis();



            Intent notificationIntent = new Intent(context, Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    _hashreminder,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if( alarmManager != null ){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        //!!---corregir, en vez de guardar el intervalo, deberia guardarse el tipo de intervalo y luego calcular aqui cuantos milisegundos
                        note.reminder,
                        pendingIntent
                );
                if(DB_N.Modify_Reminder_Status(note.note_id,note.reminder+note.reminder_interval,note.reminder_type,note.reminder_interval)){
                    Toast.makeText(context, "Alarma repetida", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
                }
            }



        }

    }
}