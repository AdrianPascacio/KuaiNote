package com.example.kuai_notes_project;

import java.util.ArrayList;

public class Date_and_Time_Names {
    private static ArrayList<Date_and_Time_Names> LanguageArrayList;


    private static String[] nameMonths;
    private static String[] nameDays;
    private static String[] nameHours;
    private static String[] nameMinutes;



    //public Month_Names( String name){
    //    this.name = name;
    //}
    public static void init_Month_Names(){
        nameMonths = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    }
    public static void init_Days_Names(){
        nameDays = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                "30", "31"};
    }
    public static void init_Hours_Names(){
        nameHours = new String[]{"00","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }
    public static void init_Minutes_Names(){
        nameMinutes = new String[]{"00","01", "02", "03", "04", "05", "06", "07", "08", "09",
                "10","11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20","21", "22", "23", "24", "25", "26", "27", "28", "29",
                "30","31", "32", "33", "34", "35", "36", "37", "38", "39",
                "40","41", "42", "43", "44", "45", "46", "47", "48", "49",
                "50","51", "52", "53", "54", "55", "56", "57", "58", "59"};
    }
    public static int Months_Count(){
        return nameMonths.length;
    }
    public static String[] getNameMonths() {
        return nameMonths;
    }
    public static String[] getNameDays() {
        return nameDays;
    }
    public static String[] getNameHours() {
        return nameHours;
    }
    public static String[] getNameMinutes() {
        return nameMinutes;
    }

    public static void setNameMonths(String[] nameMonths) {
        Date_and_Time_Names.nameMonths = nameMonths;
    }


}
