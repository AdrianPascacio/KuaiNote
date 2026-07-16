package com.example.kuai_notes_project;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
/// 185 V9.0B 13jul2026
public class Reminder_Notification {
    private static final String CHANNEL_ID = "My_App_Channel";
    private static final String CHANNEL_ID_TASK = "My_App_Channel_Task";
    public static final int NOTIFICATION_ID = 1;
    public static final int TYPE_JOURNAL_ELEMENT_NOTE = 0;
    public static final int TYPE_JOURNAL_ELEMENT_TASK = 1;
    static DB_Notes DB_N;
    static DB_Tasks DB_T;
    public static void sendNotification(Context context, int Element_Type, String title, String content, String bigText, long note_date, int hash_requestCode, long note_id){
        DB_N = new DB_Notes(context);
        DB_T = new DB_Tasks(context);
        if(Element_Type == TYPE_JOURNAL_ELEMENT_NOTE){
            Create_Notification_Channel(context);
        }else{
            Create_Task_Notification_Channel(context);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("send_note_id",note_id); //Este es el putExtra que necesito

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        /// Modificacion de flag
        //intent.setFlags(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? Intent.FLAG_ACTIVITY_NEW_TASK : 0);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                hash_requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Element_Type == TYPE_JOURNAL_ELEMENT_NOTE ? CHANNEL_ID: CHANNEL_ID_TASK) /// TERNARY OPERATOR
                .setSmallIcon(R.drawable.fire_icon_5)
                .setBadgeIconType(R.drawable.recycler_logo_icon_4)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(bigText)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(hash_requestCode,builder.build());
    }
    private static void Create_Notification_Channel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "General Notification";
            String description = "Application notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private static void Create_Task_Notification_Channel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "General Task Notification";
            String description = "Application Task notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TASK,name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static void Set_Reminder_Alarm(Context context, int hashreminder,long alarm_Time,Intent notificationIntent  ) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                hashreminder,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Ya que no es un activity se debe cambiar:
        //AlarmManager alarmManager = (AlarmManager) getSystemService(context,ALARM_SERVICE);
        //AlarmManager alarmManager = (AlarmManager) itemView.getContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if( alarmManager != null ){
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarm_Time,
                    pendingIntent
            );
        }
    }

    public static void Cancel_Reminder_Modifying_Database(View itemView, long previous_reminder, long element_id  ) {

        DB_Notes DB_N = new DB_Notes(itemView.getContext());
        if(previous_reminder > 0){

            boolean mayor = element_id > 0;
            Toast.makeText(itemView.getContext(), "Reminder Previo mayor 0"+mayor, Toast.LENGTH_SHORT).show();
            if(DB_N.Modify_Reminder_Status(element_id,0L,0,0)){

                Cancel_Reminder_Alarm(itemView, element_id,0,previous_reminder);
            }
        }
    }
    public static void Cancel_Task_Reminder_Modifying_Database(View itemView, long previous_reminder, long element_id  ) {

        DB_Tasks DB_T = new DB_Tasks(itemView.getContext());
        if(previous_reminder > 0){

            boolean mayor = element_id > 0;
            Toast.makeText(itemView.getContext(), "Reminder Previo mayor 0"+mayor, Toast.LENGTH_SHORT).show();
            if(DB_T.Modify_Reminder_Status(element_id,0L,0,0)){

                Cancel_Reminder_Alarm(itemView, element_id,1,previous_reminder);
            }
        }
    }

    public static void Cancel_Reminder_Alarm(View itemView, long element_id , int Type_OF_Element, long reminder) {
        //!!se esta ejecutando incluso si no tiene un reminder registrado.
        //int _upperReminder_Half = (int) (_note.reminder >>> 32);
        //int _lowerReminder_Half = (int) (_note.reminder);
        //int _hashreminder = upperReminder_Half ^ lowerReminder_Half;
        //int _hashreminder = (int) (( previous_reminder >>> 32 ) ^ previous_reminder ); //hash creado con XOR operator (upper ^ lower)
        if(reminder == 0){
            return;   //Cancel Method because is not needed
        }
        int _hashreminder =  0;
        if(Type_OF_Element == 0){
            _hashreminder =  Reminder_Hash_Creator.get_Note_Hash(element_id);
        }else{
            _hashreminder =  Reminder_Hash_Creator.get_Task_Hash(element_id);
        }
        Intent notificationIntent = new Intent(itemView.getContext(), Notification_Receiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                itemView.getContext(),
                _hashreminder,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) itemView.getContext().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(itemView.getContext(), "Reminder Canceled. Rem_not Handler", Toast.LENGTH_SHORT).show();
        }
    }
}