package com.example.kuai_notes_project;

import android.view.View;

public interface Recycler_Trash_Can_Interface {
    void onItemClick(int position, View v);
    void onItemHold(int position, View v);
    void RemoveItem(int position);
    void RecycleItem(int position);
}
