package com.example.kuai_notes_project;

public class Note {
    String date;
    String title;
    String note;
    int pin = 0;
    Long reminder;
    int reminder_type = 0;
    int reminder_interval = 0;

    public Note() {
    }
    public Note(String date, String title, String note, int pin, Long reminder, Integer reminder_type, Integer reminder_interval) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.pin = pin;
        this.reminder = reminder;
        this.reminder_type = reminder_type;
        this.reminder_interval = reminder_interval;
    }
    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public Long getReminder() {
        return reminder;
    }

    public void setReminder(Long reminder) {
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
}
