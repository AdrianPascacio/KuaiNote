package com.example.kuai_notes_project;

import android.view.View;

public interface Task_Element {
    int TYPE_TASK_MAIN = 0;
    int TYPE_TASK_SUB = 1;
    int getViewType();
    long getId();
    String getContent();
    boolean getCompletion();
}
