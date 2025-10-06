package com.example.kuai_notes_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

///354 V4
public class Trash_Can extends AppCompatActivity implements Recycler_Trash_Can_Interface, MyItemAnimator.ItemAnimatorListener{
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;

    DB_Trash_Can DB_TC;
    DB_Notes DB_N;
    Body_Note_Preview BPN;

    Adapter_Recycler_Trash_Can adapter;

    String _current_time = null;
    Button btn_return;
    private int current_hold_position = -1;

    private int prev_selectedPosition = -1;

    @Override
    protected void onResume(){
        super.onResume();
        _current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());


        SharedPreferences shared_preferences = getSharedPreferences("Day_change", Context.MODE_PRIVATE);
        String saved_day = shared_preferences.getString("today","numero");
        //Toast.makeText(this, saved_day, Toast.LENGTH_LONG).show();

        //-----Comparar fechas
        if(!Objects.equals(_current_time, saved_day)){

            try (Cursor cursor = DB_TC.get_All_Notes()) {
                if(cursor.getCount()==0){
                    Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
                }else{
                    while (cursor.moveToNext()){
                        if (cursor.getInt(5) > 0) {
                            //---Decrementar expire days
                            DB_TC.Reduce_Note_Expire_Days(cursor.getString(0), cursor.getInt(5));
                            Toast.makeText(this, saved_day+"\n"+_current_time+" : "+cursor.getInt(5), Toast.LENGTH_LONG).show();
                        } else {
                            //---Delete if is less than "1"
                            DB_TC.Delete_Specific_Note(cursor.getString(0));
                        }
                    }
                }
            }


            //---Actualizar la fecha
            SharedPreferences.Editor editor = shared_preferences.edit();
            editor.putString("today",_current_time);
            editor.apply();
            //Toast.makeText(this, saved_day, Toast.LENGTH_LONG).show();

        }


        recyclerView = findViewById(R.id.Recycler_Trash_Can);
        adapter = new Adapter_Recycler_Trash_Can(this, dateEdited_list,selected_list,noteList,this);
        recyclerView.setAdapter(adapter);

        Clear_Lists();
        Update_Recycler_View();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash_can);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.light_brown_natural_trans));

        DB_TC = new DB_Trash_Can(this);
        DB_N = new DB_Notes(this);

        BPN = new Body_Note_Preview();


        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        btn_return = findViewById(R.id.button_Return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
            }
        });
    }

    private void Update_Recycler_View(){
        try (Cursor cursor_Notes = DB_TC.get_All_Notes()) {
            if(cursor_Notes.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                while (cursor_Notes.moveToNext()){
                    //!!---- the note is saving just the note preview
                        //!!--- when is going to delete the note and pass the note to the trash can it will be incomplete
                    Note note = new Note(cursor_Notes.getString(0),cursor_Notes.getString(1),BPN.Set_Body_Note_Preview(cursor_Notes.getString(1),cursor_Notes.getString(2),115,100,0,5),cursor_Notes.getInt(3),"");
                    dateEdited_list.add(Set_Date_of_Note(note.date));
                    noteOriginal_list.add(cursor_Notes.getString(2));
                    selected_list.add(false);
                    noteList.add(note);
                }
            }
        }
        //////With object "Note"
        ////noteList = DB_N.getAllNotes();

    //    adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private String Set_Date_of_Note(String date){
        int size_of_date = date.length() ;
        String date_dMy = date.substring(13,size_of_date - 8);
        String date_hm = date.substring(size_of_date - 6);

        //if last modification date is equal to current date then "Today"
        if(_current_time.equals(date_dMy)){
            return ("Today\n"+_current_time+"\n"+date_hm);
        }else{
            return (date_dMy+"\n"+date_hm);
        }
    }

    private void Clear_Lists(){
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
    }

    public void Go_To_Add_New_Note(View view){
        Intent goTo = new Intent(this, MainActivity.class);
        startActivity(goTo);
    }

    @Override
    public void onItemClick(int position) {
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, Wasted_Note_Visualizer.class);
        goTo.putExtra("send_date_of_note",_note.date);
        startActivity(goTo);
    }

    //@Override
    public void Update_Recycler_Adapter(){
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public void onItemHold(int position) {

        //--Si actual es igual previo
            if(position == prev_selectedPosition){
                Log.d("Trash Can", "Hold position == prev : ");
                //--Invertir estado de seleccion
                boolean previousIsSelected = selected_list.get(position);
                selected_list.set(position,!previousIsSelected);
                adapter.notifyItemChanged(position);

            }else{
                Log.d("Trash Can", "Hold position diff prev : ");
                //--Si previo esta activado entonces desactivar
                if(prev_selectedPosition != -1){
                    Log.d("Trash Can", "Hold position diff Inicio :     prev= " + prev_selectedPosition);
                    boolean previousIsSelected = selected_list.get(prev_selectedPosition);
                    if(previousIsSelected){
                        Log.d("Trash Can", "Hold position prev = true");
                        selected_list.set(prev_selectedPosition,false);
                        adapter.notifyItemChanged(prev_selectedPosition);

                        current_hold_position = position;
                        //adapter.notifyItemRangeChanged(prev_selectedPosition,position);
                        //adapter.notifyDataSetChanged();
                        //Update_Recycler_Adapter();
                    }
                }

                //!!---- This have to be replaced to a callback or listener
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //--Si actual es diferente a previo
                        //--Activar la seleccion de actual
                        selected_list.set(position,true);
                        adapter.notifyItemChanged(position);
                    }
                }, 350); // Realiza accion luego de 350 milisegundos depende de la ejecucion del celular, mejorar


                //Update_Recycler_Adapter();
            }

        //--Previo es igual a position actual:
        prev_selectedPosition = position;
    }

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);
        //----Remove Note from Data Base:
        if(DB_TC.Delete_Specific_Note(_note.date)) {
            //----Remove Note from Recycler View
            dateEdited_list.remove(position);
            noteOriginal_list.remove(position);
            noteList.remove(position);
            selected_list.remove(position);
            adapter.notifyItemRemoved(position);

            //Previous selection must be equal to -1
            prev_selectedPosition = -1;
        }
    }

    /// ----------------------------------------------------------Pin Items:
    @Override
    public void PinItem(int position) {
        //----Pin Status positive
        Note _note = noteList.get(position);
        boolean _pin_state = _note.pin == 1;
        int _pin=0;
        if(_note.pin == 0) {
            _pin =1;
        }

        //----Database update with new pin status value:
        boolean pin_modify_Success = Modify_Pin_Status_In_DataBase(_note, position, _pin);

        if(pin_modify_Success){
            //----RecyclerView pin status update:
            RecyclerView_Pin_Update(position);
        }
    }
    @Override
    public void RecycleItem(int position) {
        Note _note = noteList.get(position);
        //----Remove Note from Data Base:
        if(DB_N.Insert_Note(_note.date,_note.title,noteOriginal_list.get(position),_note.pin)){
            if(DB_TC.Delete_Specific_Note(_note.date)) {
                //----Remove Note from Recycler View
                dateEdited_list.remove(position);
                noteOriginal_list.remove(position);
                noteList.remove(position);
                selected_list.remove(position);
                adapter.notifyItemRemoved(position);

                //Previous selection must be equal to -1
                prev_selectedPosition = -1;
            }

        }
    }

    public Boolean Modify_Pin_Status_In_DataBase(Note _note,int position,int _pin){
        //Note _note = noteList.get(position);
        Boolean Modify_Pin_Status = DB_TC.Modify_Pin_Status(_note.date,_pin);
        if (Modify_Pin_Status) {
            return true;
        } else {
            Toast.makeText(Trash_Can.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void RecyclerView_Pin_Update(int position){
        //----RecyclerView update:

        Note _note = noteList.get(position);
        String _date= dateEdited_list.get(position);
        String _noteOriginal= noteOriginal_list.get(position);
        boolean _selected=false;
        selected_list.set(position,false);
        adapter.notifyItemChanged(position);

        dateEdited_list.remove(position);
        noteOriginal_list.remove(position);
        selected_list.remove(position);
        noteList.remove(position);

        int current_pinned_notes = DB_TC.get_Specific_Note_Sorted_by_Pin_and_Date(_note.date);
        prev_selectedPosition = current_pinned_notes;

        dateEdited_list.add(current_pinned_notes,_date);
        noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        //--cambio de estado con referencia al anterior de (0 a 1)
        if (_note.pin==0){
            _note.pin=1;
        }else {
            _note.pin = 0;
        }
        noteList.add(current_pinned_notes,_note);
        selected_list.add(current_pinned_notes,_selected);
        adapter.notifyItemMoved(position,current_pinned_notes);
        adapter.notifyItemChanged(current_pinned_notes);

    }

    @Override
    //!!!--- currently is not working
    public void onAnimationFinished() {
        if(current_hold_position != -1){
            selected_list.set(current_hold_position,true);
            adapter.notifyItemChanged(current_hold_position);

            current_hold_position = -1;
        }

    }

    public void Return_To_Memo_Board(){
        finish();
    }
}