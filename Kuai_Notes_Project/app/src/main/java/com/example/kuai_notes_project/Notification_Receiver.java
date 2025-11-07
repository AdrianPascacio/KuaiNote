package com.example.kuai_notes_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification_Receiver extends BroadcastReceiver {
    //El BroadcastReceiver es el metodo que se quedara abierto siempre incluso si la aplicacion se cierra

    DB_Notes DB_N;
    Body_Note_Preview BNP;
    @Override
    public void onReceive(Context context, Intent intent) {
        DB_N = new DB_Notes(context);
        BNP = new Body_Note_Preview();
        Note note = new Note();



        //String title = " Reminder Title";
        //String content = " reminder content";
        ///String title =  (intent.getStringExtra("NOTE_TITLE") != null ? intent.getStringExtra("NOTE_TITLE") : "Titulo no encontrado");
        ///String content =  (intent.getStringExtra("NOTE_CONTENT") != null ? intent.getStringExtra("NOTE_CONTENT") : "Content no encontrado");
        ///String bigText =  (intent.getStringExtra("NOTE_BIG_TEXT") != null ? intent.getStringExtra("NOTE_BIG_TEXT") : "Estiramiento y meditacion.");
        ///String note_date =  (intent.getStringExtra("NOTE_DATE") != null ? intent.getStringExtra("NOTE_DATE") : "0");
        long Reminder_alarmTime =  intent.getLongExtra("NOTE_REMINDER_ALARM_TIME",0L) ;

        note = DB_N.getASpecificNote_ByReminder(Reminder_alarmTime);
        String title =  note.title;
        String content =  BNP.Set_Body_Note_Preview(note.title,note.note,
                60,
                55,
                10,
                1);
        String bigText =  BNP.Set_Body_Note_Preview(note.title,note.note,
                60,
                55,
                10,
                4);;
        String note_date =  note.date;
        int _hashreminder = (int) (( Reminder_alarmTime >>> 32 ) ^ Reminder_alarmTime ); //hash creado con XOR operator (upper ^ lower)

        Reminder_Notification.sendNotification(context,title,content, bigText, note_date, _hashreminder,Reminder_alarmTime * (-1) );



        //!!--- si el reminder tiene un tipo repetitivo se debe restablecer otra alarma no borrarla
        /*----- la seccion de reminder sera utilizada en negativo para indicacr que ya fue completado,
         al mismo tiempo el numero sera utilizado para entrar en la nota
         incluso si es modificado
         sirviendo ahora de identificador para entrar desde la notificadion
         */

        if(DB_N.Modify_Reminder_Status(note_date,Reminder_alarmTime * (-1),0,0)){
            Toast.makeText(context, "Deleted After Alarm", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Error in database after alarm", Toast.LENGTH_SHORT).show();
        }

        //!!--- se puede agregar una actualizacion en el recycler view para animar el cambio despues de completado
    }
}
