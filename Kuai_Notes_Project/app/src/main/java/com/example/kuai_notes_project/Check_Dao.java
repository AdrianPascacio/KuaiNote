package com.example.kuai_notes_project;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Check_Dao {
    @Insert
    long insertMain(DB_Check_Main check);

    @Insert
    void insertSub(DB_Check_Sub sub);

    @Transaction //Necesario para el room, utilizadas para realizar 2 consultas internas
    @Query("SELECT * FROM check_main WHERE deleted = 0")
    //se adaptara para que quede en "LiveData"
    //List<Check_With_Subs> getAllCheckWithSubs();
    LiveData<List<Check_With_Subs>> getAllCheckWithSubs();

    @Update
    void updateMain(DB_Check_Main check);
}
