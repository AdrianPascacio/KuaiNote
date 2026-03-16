package com.example.kuai_notes_project;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class Check_ViewModel extends AndroidViewModel {
    private Check_Dao checkDao;
    private LiveData<List<Check_With_Subs>> allChecks;

    public Check_ViewModel(@NonNull Application application){
        super(application);
        Check_DataBase DB_CH = Check_DataBase.getInstance(application);
        checkDao = DB_CH.checkDao();
        allChecks = checkDao.getAllCheckWithSubs();
    }

    public LiveData<List<Check_With_Subs>> getAllChecks(){
        return allChecks;
    }

    public void insertComplexCheck(DB_Check_Main main, List<DB_Check_Sub> subs){
        //Ejecutar el hilo seccundario para no bloqear la UI
        new Thread(() -> {
            long id = checkDao.insertMain(main);
            for (DB_Check_Sub sub : subs){
                sub.parentCheckId = (int) id;
                checkDao.insertSub(sub);
            }
        }).start();
    }
    public void saveCheck(String note, List<String> subNotesString){
        new Thread(() -> {
            DB_Check_Main main = new DB_Check_Main();
            main.note = note;
            main.dateCreated = System.currentTimeMillis();
            main.has_sub_checks = (subNotesString != null && !subNotesString.isEmpty());

            long mainID = checkDao.insertMain(main);

            if(main.has_sub_checks){
                for (int i = 0; i < subNotesString.size(); i++){
                    DB_Check_Sub sub = new DB_Check_Sub();
                    sub.parentCheckId = (int) mainID;
                    sub.note = subNotesString.get(i);
                    sub.position = i;
                    sub.checked = false;
                    checkDao.insertSub(sub);
                }
            }
        }).start();
    }
}
