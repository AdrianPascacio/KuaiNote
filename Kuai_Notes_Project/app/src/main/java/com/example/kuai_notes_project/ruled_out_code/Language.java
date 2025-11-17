package com.example.kuai_notes_project.ruled_out_code;

import java.util.ArrayList;

public class Language {
    private static ArrayList<Language> LanguageArrayList;


    private int id;
    private String name;

    public Language(int id, String name){
        this.id = id;
        this.name = name;
    }
    public static void init_NumberP_Content(){
        Language jan = new Language(0,"Jan");
        LanguageArrayList.add(jan);
        Language feb = new Language(1,"Feb");
        Language mar = new Language(2,"Mar");
        Language apr = new Language(3,"Apr");
        LanguageArrayList.add(feb);
        LanguageArrayList.add(mar);
        LanguageArrayList.add(apr);
    }

    public static ArrayList<Language> getLanguageArrayListList(){
        return LanguageArrayList;
    }

    public static String [] lenguageNames(){
        String [] names = new String [LanguageArrayList.size()];
        for ( int i = 0; i < LanguageArrayList.size() ; i++){
            names[i] = LanguageArrayList.get(i).name;
        }
        return names;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
