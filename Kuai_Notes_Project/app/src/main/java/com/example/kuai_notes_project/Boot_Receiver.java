package com.example.kuai_notes_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

public class Boot_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            DB_Notes DB_N = new DB_Notes(context);

            List<Note> scheduled_notes_list = DB_N.get_All_Notes_With_Pending_Reminders();

            for (Note note : scheduled_notes_list){
                Intent notificationIntent = new Intent(context, Notification_Receiver.class);
                notificationIntent.putExtra("NOTE_REMINDER_ALARM_TIME", note.reminder);
                notificationIntent.putExtra("NOTE_ID", note.note_id);

                int hashreminder =  (int) (( note.note_id >>> 32 ) ^ note.note_id ); //hash creado con XOR operator (upper ^ lower)

                Reminder_Notification.Set_Reminder_Alarm(context,hashreminder,note.reminder,notificationIntent);
            }
            Toast.makeText(context, "Alarmas Reprogramadas correctamente", Toast.LENGTH_SHORT).show();
        }

    }
}