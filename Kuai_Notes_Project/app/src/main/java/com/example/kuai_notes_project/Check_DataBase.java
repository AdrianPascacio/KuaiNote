package com.example.kuai_notes_project;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DB_Check_Main.class, DB_Check_Sub.class}, version = 1)
public abstract class Check_DataBase extends RoomDatabase {
    public abstract  Check_Dao checkDao();
    private static Check_DataBase instance;

    public static synchronized  Check_DataBase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Check_DataBase.class, "checks_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
