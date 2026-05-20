package com.example.kuai_notes_project;

import android.view.View;

public interface Recycler_Tasks_Sub_List_Interface {
    void onItemClick(int position, View v);
    void onItemHold_Sub_Task(int position, View v);
    void Complete_Sub_Task(int position);
}
