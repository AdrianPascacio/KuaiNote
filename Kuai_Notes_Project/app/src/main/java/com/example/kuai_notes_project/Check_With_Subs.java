package com.example.kuai_notes_project;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class Check_With_Subs {
    @Embedded
    public DB_Check_Main checkMain;

    @Relation(
            parentColumn = "id",
            entityColumn = "parent_check"
    )
    public List<DB_Check_Sub> subChecks;
}
