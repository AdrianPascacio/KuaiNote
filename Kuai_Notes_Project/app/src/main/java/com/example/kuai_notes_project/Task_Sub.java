package com.example.kuai_notes_project;

public class Task_Sub implements Task_Element{
    long task_sub_id = 0;
    long parent_id = 0;
    String note;
    boolean completed = false;
    int task_sub_position = 0;


    public Task_Sub() {
    }
    public Task_Sub(long task_sub_id,long parent_id, String note, boolean completed, int task_sub_position) {
        this.task_sub_id = task_sub_id;
        this.parent_id = parent_id;
        this.note = note;
        this.completed = completed;
        this.task_sub_position = task_sub_position;
    }
    // Getters and Setters
    public long getTask_Sub_id() {
        return task_sub_id;
    }

    public void setTask_Sub_id(long task_sub_id) {
        this.task_sub_id = task_sub_id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean geCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getTask_sub_position() {
        return task_sub_position;
    }

    public void setTask_sub_position(int task_sub_position) {
        this.task_sub_position = task_sub_position;
    }

    @Override
    public int getViewType() {
        return TYPE_TASK_SUB;
    }

    @Override
    public long getId() {
        return getTask_Sub_id();
    }

    @Override
    public String getContent() {
        return getNote();
    }
}
