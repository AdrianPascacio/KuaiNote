package com.example.kuai_notes_project;

import android.view.View;

public interface Recycler_Memo_Board_Interface {
    void onItemClick(int position, View v);
    void onItemHold(int position, View v);
    void RemoveItem(int position);
    void SetReminder(int position);
    void PinItem(int position);
}
