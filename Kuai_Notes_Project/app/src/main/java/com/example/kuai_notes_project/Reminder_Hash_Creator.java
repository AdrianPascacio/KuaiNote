package com.example.kuai_notes_project;

import android.util.Log;

public class Reminder_Hash_Creator {

    //TASK_FLAG is a mask = 1 flowed by 31 zeros;
    private static final int TASK_FLAG = 1 << 31;

    public static int get_Note_Hash(long original_reminder){
        int reminder_code = (int) (( original_reminder >>> 32) ^ original_reminder); //hash creado con XOR operator (upper ^ lower)
        //Se asegura de que el bit 32 sea un 0
        return  reminder_code & ~TASK_FLAG;
    }
    public static int get_Task_Hash(long original_reminder){
        int reminder_code = (int) (( original_reminder >>> 32) ^ original_reminder); //hash creado con XOR operator (upper ^ lower)
        //Se asegura de que el bit 32 sea un 1
        return  reminder_code | TASK_FLAG;
    }

    public static boolean is_TaskHash(int hash_Id){
        //Verify if the hash belong to a Task reminder:
        boolean result  = (hash_Id & TASK_FLAG) != 0;
        Log.d("Reminder Hash Creator", " is a Task Hask: " + result );
        return result;
    }
}
