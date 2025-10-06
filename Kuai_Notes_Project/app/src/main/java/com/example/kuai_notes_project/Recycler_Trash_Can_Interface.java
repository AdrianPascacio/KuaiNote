package com.example.kuai_notes_project;

public interface Recycler_Trash_Can_Interface {
    void onItemClick(int position);
    void onItemHold(int position);
    void RemoveItem(int position);
    void PinItem(int position);
    void RecycleItem(int position);
}
