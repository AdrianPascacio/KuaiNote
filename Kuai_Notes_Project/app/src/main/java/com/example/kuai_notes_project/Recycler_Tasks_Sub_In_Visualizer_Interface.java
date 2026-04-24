package com.example.kuai_notes_project;

import android.view.View;

public interface Recycler_Tasks_Sub_In_Visualizer_Interface {
    void onItemClick(int position, View v);
    void onItemHold(int position, View v);
    void Mark_Sub_Task_As_Completed(int position);
    void Change_Sub_Task_Description(int position, String description);
    void Remove_Item(int position);
}
