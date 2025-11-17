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

public class Reminder_Notification {
    private static final String CHANNEL_ID = "My_App_Channel";
    public static final int NOTIFICATION_ID = 1;
    static DB_Notes DB_N;
    public static void sendNotification(Context context, String title, String content, String bigText, long note_date, int hash_requestCode, long note_id){
        DB_N = new DB_Notes(context);
        Create_Notification_Channel(context);
        //!!--Agregar la nota especifica, por el momento solo lo envia al Main

        Intent intent = new Intent(context, MainActivity.class);
        //intent.putExtra("send_date_of_note",note_date); //Este es el putExtra que necesito
        //intent.putExtra("send_reversed_alarm",reversed_reminder); //Este es el putExtra que necesito
        intent.putExtra("send_note_id",note_id); //Este es el putExtra que necesito
        /// original segun gemini:
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        /// Modificacion de flag
        //intent.setFlags(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? Intent.FLAG_ACTIVITY_NEW_TASK : 0);


        ///PendingIntent pendingIntent = PendingIntent.getActivity(context,hash_requestCode,intent,PendingIntent.FLAG_IMMUTABLE);
        //--- cambiado flag para update el notificacion en vez de sustituirlo
        PendingIntent pendingIntent = PendingIntent.getActivity(context,hash_requestCode,intent,PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
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
        notificationManager.notify(NOTIFICATION_ID,builder.build());

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
    public static void Cancel_Reminder_just_reminder(View itemView, long previous_reminder  ) {
        if (previous_reminder > 0) {
            Cancel_Reminder_Alarm(itemView, previous_reminder);
        }
    }

    public static void Cancel_Reminder_Modifying_Database(View itemView, long previous_reminder, long note_id  ) {
        DB_Notes DB_N = new DB_Notes(itemView.getContext());
        if(previous_reminder > 0){

            if(DB_N.Modify_Reminder_Status(note_id,0L,0,0)){

                Cancel_Reminder_Alarm(itemView, previous_reminder);
            }
        }
    }

    public static void Cancel_Reminder_Alarm(View itemView, long note_id ) {
        //int _upperReminder_Half = (int) (_note.reminder >>> 32);
        //int _lowerReminder_Half = (int) (_note.reminder);
        //int _hashreminder = upperReminder_Half ^ lowerReminder_Half;
        //int _hashreminder = (int) (( previous_reminder >>> 32 ) ^ previous_reminder ); //hash creado con XOR operator (upper ^ lower)
        int note_id_As_reminderCode =  (int) (( note_id >>> 32 ) ^ note_id ); //hash creado con XOR operator (upper ^ lower)
        Intent notificationIntent = new Intent(itemView.getContext(), Notification_Receiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                itemView.getContext(),
                note_id_As_reminderCode,
                notificationIntent,
                //PendingIntent.FLAG_IMMUTABLE
                PendingIntent.FLAG_MUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) itemView.getContext().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(itemView.getContext(), "Reminder Canceled. Rem_not Handler", Toast.LENGTH_SHORT).show();
        }
    }
}
