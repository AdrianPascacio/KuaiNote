package com.example.kuai_notes_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class Notification_Receiver extends BroadcastReceiver {
    //El BroadcastReceiver es el metodo que se quedara abierto siempre incluso si la aplicacion se cierra

    DB_Notes DB_N;
    Body_Note_Preview BNP;
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
        }else{
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
                if(DB_N.Modify_Reminder_Status(note.note_id,note.reminder+86400000,1,86400000)){
                    Toast.makeText(context, "Alarma repetida", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }
}