package com.example.kuai_notes_project;

public class Note {
    long note_id = 0;
    long date;
    String title;
    String note;
    int pin = 0;
    long reminder;
    int reminder_type = 0;
    int reminder_interval = 0;
    String category;
    int expire_days;

    public Note() {
    }
    public Note(long note_id, long date, String title, String note, int pin, long reminder, Integer reminder_type, Integer reminder_interval) {
        this.note_id = note_id;
        this.date = date;
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
    }
    // Getters and Setters
    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
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

}
