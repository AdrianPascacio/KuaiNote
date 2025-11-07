package com.example.kuai_notes_project;

public interface Recycler_Memo_Board_Interface {
    void onItemClick(int position);
    void onItemHold(int position);
    void RemoveItem(int position);
    void SetReminder(int position);
    void PinItem(int position);
}
