package com.example.kuai_notes_project;

public class Task_Main implements Task_Element {
    long task_id = 0;
    long date;
    long date_created;
    long date_modified;
    long date_completed;
    //!!--indagar si realmente se necesitan 2 campos String para titulo y nota siendo que solo es una tarea a completar. Si de pronto la tarea es el titulo
    String title;
    String note;
    boolean pin = false;
    long reminder;
    int reminder_type = 0;
    int reminder_interval = 0;
    String category;
    int expire_days;


    boolean completed = false;
    boolean has_sub_tasks = false;
    boolean unfolded = false;


    public Task_Main() {
    }
    public Task_Main(long task_id, long date, long date_created, long date_modified, long date_completed, String title, String note, boolean pin, long reminder, Integer reminder_type, Integer reminder_interval, boolean completed, boolean has_sub_tasks, boolean unfolded) {
        this.task_id = task_id;
        this.date = date;
        this.date_created = date_created;
        this.date_modified = date_modified;
        this.date_completed = date_completed;
        this.title = title;
        this.note = note;
        this.pin = pin;
        this.reminder = reminder;
        this.reminder_type = reminder_type;
        this.reminder_interval = reminder_interval;
        //!!-categoria no implementada
        this.category = "";
        //!!-expire_days no implementada
        this.expire_days = 20;
        this.completed = completed;
        this.has_sub_tasks = has_sub_tasks;
        this.unfolded = unfolded;
    }
    // Getters and Setters
    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public long getDate() {
        return date;
    }

    public long getDate_Created() {
        return date_created;
    }

    public long getDate_Modified() {
        return date_modified;
    }

    public long getDate_completed() {
        return date_completed;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDate_Created(long date_created) {
        this.date_created = date_created;
    }

    public void setDate_Modified(long date_modified) {
        this.date_modified = date_modified;
    }

    public void setDate_Completed(long date_completed) {
        this.date_completed = date_completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean getPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public long getReminder() {
        return reminder;
    }

    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    public int getReminder_Type() {
        return reminder_type;
    }

    public void setReminder_type(int reminder_type) {
        this.reminder_type = reminder_type;
    }

    public int getReminder_Interval() {
        return reminder_interval;
    }

    public void setReminder_interval(int reminder_interval) {
        this.reminder_interval = reminder_interval;
    }
    public int getExpire_days() {
        return expire_days;
    }

    public void setExpire_days(int expire_days) {
        this.expire_days = expire_days;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getHas_Sub_Tasks() {
        return has_sub_tasks;
    }

    public void setHas_Sub_Tasks(boolean has_sub_tasks) {
        this.has_sub_tasks = has_sub_tasks;
    }
    public boolean getUnfolded() {
        return unfolded;
    }

    public void setUnfolded(boolean unfolded) {
        this.unfolded = unfolded;
    }

    @Override
    public int getViewType() {
        return TYPE_TASK_MAIN;
        //Retorna el valor declarado dentro de la interface Task_Element
    }

    @Override
    public long getId() {
        return getTask_id();
    }

    @Override
    public String getContent() {
        return getTitle();
    }
    @Override
    public boolean getCompletion() {
        return getCompleted();
    }
}
