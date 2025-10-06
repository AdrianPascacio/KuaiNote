package com.example.kuai_notes_project;

public class Note {
    String date;
    String title;
    String note;
    int pin = 0;
    String reminder;

    public Note() {
    }
    public Note(String date, String title, String note, int pin, String reminder) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.pin = pin;
        this.reminder = reminder;
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

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}
