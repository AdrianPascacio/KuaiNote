package com.example.kuai_notes_project;

import android.view.View;

public interface Recycler_Tasks_List_Interface {
    void onItemClick(int position, View v);
    void onItemHold(int position, View v);
    void RemoveItem(int position);
    void Complete_Main_Task(int position);
    void Unfold(int position, long element_id);
    void SetReminder(int position);
    void PinItem(int position);
}
