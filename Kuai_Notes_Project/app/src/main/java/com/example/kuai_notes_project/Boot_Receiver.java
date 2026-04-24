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

                ///int hashreminder =  (int) (( note.note_id >>> 32 ) ^ note.note_id ); //hash creado con XOR operator (upper ^ lower)
                int hashreminder =  Reminder_Hash_Creator.get_Note_Hash(note.note_id);

                Reminder_Notification.Set_Reminder_Alarm(context,hashreminder,note.reminder,notificationIntent);
            }
            Toast.makeText(context, "Alarmas Reprogramadas correctamente", Toast.LENGTH_SHORT).show();
            DB_Tasks DB_T = new DB_Tasks(context);

            List<Task_Main> scheduled_tasks_list = DB_T.get_All_Notes_With_Pending_Reminders();

            for (Task_Main task : scheduled_tasks_list){
                Intent notificationIntent = new Intent(context, Notification_Receiver.class);
                notificationIntent.putExtra("NOTE_REMINDER_ALARM_TIME", task.reminder);
                notificationIntent.putExtra("NOTE_ID", task.task_id);

                //int hashreminder =  (int) (( task.task_id >>> 32 ) ^ task.task_id ); //hash creado con XOR operator (upper ^ lower)
                int hashreminder =  Reminder_Hash_Creator.get_Task_Hash(task.task_id);

                Reminder_Notification.Set_Reminder_Alarm(context,hashreminder,task.reminder,notificationIntent);
            }
            Toast.makeText(context, "Alarmas Tasks Reprogramadas correctamente", Toast.LENGTH_SHORT).show();
        }

    }
}