package com.example.kuai_notes_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

///324 V3, 305 V4
public class Memo_Board extends AppCompatActivity implements Recycler_Memo_Board_Interface, MyItemAnimator.ItemAnimatorListener{
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;

    DB_Notes DB_N;
    DB_Trash_Can DB_TC;

    Adapter_Recycler_Memo_Board adapter;

    String _current_time = null;
    Button btn_config;
    private int current_hold_position = -1;

    private int prev_selectedPosition = -1;
    Body_Note_Preview BNP;

    @Override
    protected void onResume(){
        super.onResume();
        _current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        recyclerView = findViewById(R.id.Recycler_MemoBoard);
        adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        recyclerView.setAdapter(adapter);

        Clear_Lists();
        Update_Recycler_View();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memo_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DB_N = new DB_Notes(this);
        DB_TC = new DB_Trash_Can(this);

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();

        BNP = new Body_Note_Preview();

        ///SharedPreferences shared_preferences = getSharedPreferences("Day_change", Context.MODE_PRIVATE);
        ///SharedPreferences.Editor editor = shared_preferences.edit();
        ///editor.putString("today",_current_time);
        ///editor.apply();
        ///String saved_number = shared_preferences.getString("today","numero");
        ///Toast.makeText(this, saved_number, Toast.LENGTH_LONG).show();


        btn_config = findViewById(R.id.button_Config);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
            }
        });
    }
    private void Update_Recycler_View(){
        try (Cursor cursor_Notes = DB_N.get_All_Notes()) {
            if(cursor_Notes.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                while (cursor_Notes.moveToNext()){
                    Note note = new Note(cursor_Notes.getString(0),cursor_Notes.getString(1),BNP.Set_Body_Note_Preview(cursor_Notes.getString(1),cursor_Notes.getString(2),60,55,0,2),cursor_Notes.getInt(3),"");
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
    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
    }

    @Override
    public void onItemClick(int position) {
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, MainActivity.class);
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
                Log.d("Memo Board", "Hold position == prev : ");
                //--Invertir estado de seleccion
                boolean previousIsSelected = selected_list.get(position);
                selected_list.set(position,!previousIsSelected);
                adapter.notifyItemChanged(position);

            }else{
                Log.d("Memo Board", "Hold position diff prev : ");
                //--Si previo esta activado entonces desactivar
                if(prev_selectedPosition != -1){
                    Log.d("Memo Board", "Hold position diff Inicio :     prev= " + prev_selectedPosition);
                    boolean previousIsSelected = selected_list.get(prev_selectedPosition);
                    if(previousIsSelected){
                        Log.d("Memo Board", "Hold position prev = true");
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
        if(DB_TC.Insert_Note(_note.date,_note.title,noteOriginal_list.get(position),_note.pin,20)){
            if(DB_N.Delete_Specific_Note(_note.date)) {
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

    public Boolean Modify_Pin_Status_In_DataBase(Note _note,int position,int _pin){
        //Note _note = noteList.get(position);
        Boolean Modify_Pin_Status = DB_N.Modify_Pin_Status(_note.date,_pin);
        if (Modify_Pin_Status) {
            //Toast.makeText(Memo_Board.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(Memo_Board.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
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

        int current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(_note.date);
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
}