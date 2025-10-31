package com.example.kuai_notes_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

///324 V3, 305 V4, 358 V6, 306 V7
public class Memo_Board extends AppCompatActivity implements Recycler_Memo_Board_Interface, MyItemAnimator.ItemAnimatorListener{
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Boolean> unselected_list;
    ArrayList<Note> noteList;
    ArrayList<Integer> previous_selected_list;

    DB_Notes DB_N;
    DB_Trash_Can DB_TC;

    Adapter_Recycler_Memo_Board adapter;

    String _current_time = null;
    Button btn_config;
    Button btn_aux;
    View main;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private int current_hold_position = -1;

    private int prev_selectedPosition = -1;
    private int postprev_selectedPosition = -1;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View DoN_IV;
    private Animation AnimationAddNoteButton;
    private FloatingActionButton fa_btn;

    @Override
    protected void onResume(){
        super.onResume();
        _current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        recyclerView = findViewById(R.id.Recycler_MemoBoard);
        adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,unselected_list,this);
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
        unselected_list = new ArrayList<>();
        previous_selected_list = new ArrayList<>();

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View();
        fa_btn = findViewById(R.id.floatingActionButton);
        main = findViewById(R.id.main);

        ///SharedPreferences shared_preferences = getSharedPreferences("Day_change", Context.MODE_PRIVATE);
        ///SharedPreferences.Editor editor = shared_preferences.edit();
        ///editor.putString("today",_current_time);
        ///editor.apply();
        ///String saved_number = shared_preferences.getString("today","numero");
        ///Toast.makeText(this, saved_number, Toast.LENGTH_LONG).show();

        AnimationAddNoteButton = AnimationUtils.loadAnimation(this,R.anim.add_note_button_zoom);

        btn_config = findViewById(R.id.button_Config);
        btn_aux = findViewById(R.id.button_aux);
        fa_btn.startAnimation(AnimationAddNoteButton);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
            }
        });
        btn_aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start_Reminder_PopUpWindow();
            }
        });
    }

    private void Start_Reminder_PopUpWindow(String note_title) {
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.reminder_setter,null);

        popupWindow = new PopupWindow(container, 400, 400, true);
        popupWindow.showAtLocation(main, Gravity.CENTER,00,-400);
        //Go_To_Reminder_Setter();
        TextView label_in_reminder,name_in_reminder ;
        label_in_reminder = container.findViewById(R.id.Label_Reminder_Setter);
        name_in_reminder = container.findViewById(R.id.Note_title_in_Reminder_Setter);
        name_in_reminder.setText(note_title);
        label_in_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popupWindow.dismiss();
            }
        });
        container.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public  boolean onTouch(View v, MotionEvent event){
                //popupWindow.dismiss();
                return true;
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
                    dateEdited_list.add(DoN_IV.Set_Date_of_Note(note.date,_current_time));
                    noteOriginal_list.add(cursor_Notes.getString(2));
                    selected_list.add(false);
                    noteList.add(note);
                    unselected_list.add(false);
                }
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(RecyclerView.SCROLL_STATE_DRAGGING == newState){
                    if(prev_selectedPosition != -1){
                        if(selected_list.get(prev_selectedPosition)== true){
                            Toast.makeText(Memo_Board.this, "arrastrando", Toast.LENGTH_SHORT).show();
                            selected_list.set(prev_selectedPosition,false);
                            unselected_list.set(prev_selectedPosition,true);
                            adapter.notifyItemChanged(prev_selectedPosition);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    unselected_list.set(prev_selectedPosition,false);
                                    adapter.notifyItemChanged(prev_selectedPosition,this);
                                    prev_selectedPosition = -1;
                                    previous_selected_list.clear();
                                }
                            }, 500); // Realiza accion luego de 300 milisegundos
                        }
                    }
                    if(postprev_selectedPosition != -1){
                        if(unselected_list.get(postprev_selectedPosition)== true){
                            Toast.makeText(Memo_Board.this, "arrastrando", Toast.LENGTH_SHORT).show();
                            unselected_list.set(postprev_selectedPosition,false);
                            //unselected_list.set(prev_selectedPosition,false);
                            adapter.notifyItemChanged(postprev_selectedPosition,this);
                        }
                    }

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
    private void Clear_Lists(){
        if(noteOriginal_list.isEmpty()){
            return;
        }
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
        unselected_list.clear();
        previous_selected_list.clear();
    }
    public void Go_To_Add_New_Note(View view){
        Intent goTo = new Intent(this, MainActivity.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }
    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }
    private void Go_To_Reminder_Setter() {
        //Intent goTo = new Intent(this, Reminder_Setter.class);
        //startActivity(goTo);
        //overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }

    @Override
    public void onItemClick(int position) {
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, MainActivity.class);
        goTo.putExtra("send_date_of_note",_note.date);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Override
    public void onItemHold(int position) {

        //--Si actual es igual previo
        if(position == prev_selectedPosition){
            //--Invertir estado de seleccion
            boolean previousIsSelected = selected_list.get(position);
            selected_list.set(position,!previousIsSelected);
        }else{
            //--Si previo esta activado entonces desactivar
            if(prev_selectedPosition != -1){
                boolean previousIsSelected = selected_list.get(prev_selectedPosition);
                if(previousIsSelected){
                    selected_list.set(prev_selectedPosition,false);
                    adapter.notifyItemChanged(prev_selectedPosition);
                    current_hold_position = position;
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        Set_Unselected_List(position);

        postprev_selectedPosition = prev_selectedPosition;
        prev_selectedPosition = position;

        Note _note = new Note();
        _note = noteList.get(position);
        Start_Reminder_PopUpWindow(_note.title);
    }
    public void onItemHold_BackUp_2V(int position) {

        //--Si actual es igual previo
        if(position == prev_selectedPosition){
            //--Invertir estado de seleccion
            boolean previousIsSelected = selected_list.get(position);
            selected_list.set(position,!previousIsSelected);
        }else{
            //--Si previo esta activado entonces desactivar
            if(prev_selectedPosition != -1){
                boolean previousIsSelected = selected_list.get(prev_selectedPosition);
                if(previousIsSelected){
                    selected_list.set(prev_selectedPosition,false);
                    adapter.notifyItemChanged(prev_selectedPosition);
                    current_hold_position = position;
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        Set_Unselected_List(position);

        prev_selectedPosition = position;
    }

    private void Set_Unselected_List(int position) {
        previous_selected_list.add(0, position);

        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(previous_selected_list.get(1),true);
            adapter.notifyItemChanged(previous_selected_list.get(1));

            boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));
            if(current_eq_previous){
                previous_selected_list.clear();
            }
            return;
        }

        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(previous_selected_list.get(2),false);
            unselected_list.set(previous_selected_list.get(1),true);
            adapter.notifyItemChanged(previous_selected_list.get(2),this);

            boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));

            previous_selected_list.remove(2);

            if(current_eq_previous){
                previous_selected_list.clear();
                return;
            }
        }
    }
    private void Set_Unselected_List_BackUp_V2(int position) {
        previous_selected_list.add(0, position);

        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(previous_selected_list.get(1),true);
            adapter.notifyItemChanged(previous_selected_list.get(1));

            boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));
            if(current_eq_previous){
                previous_selected_list.clear();
            }
            return;
        }

        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(previous_selected_list.get(2),false);
            unselected_list.set(previous_selected_list.get(1),true);

            boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));

            previous_selected_list.remove(2);

            if(current_eq_previous){
                previous_selected_list.clear();
                return;
            }
        }
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

                if(previous_selected_list.size() > 1){
                    unselected_list.set(previous_selected_list.get(1),false);
                }
                previous_selected_list.clear();

                unselected_list.remove(position);


                prev_selectedPosition = -1;
            }
        }
    }

    /// ----------------------------------------------------------Pin Items:
    @Override
    public void PinItem(int position) {
        //----Pin Status positive
        Note _note = noteList.get(position);
        int _pin = _note.getPin() ^ 1;      //XOR Operator

        //----Database update with new pin status value:
        if(DB_N.Modify_Pin_Status(_note.date,_pin)){
            //----RecyclerView pin status update:
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(Memo_Board.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }

    public void RecyclerView_Pin_Update(int position){
        //----RecyclerView update:

        Note _note = noteList.get(position);
        String _date= dateEdited_list.get(position);
        String _noteOriginal= noteOriginal_list.get(position);
        boolean _selected=false;
        boolean _unselected=true;
        selected_list.set(position,false);
        unselected_list.set(position,true);
        adapter.notifyItemChanged(position);

        dateEdited_list.remove(position);
        noteOriginal_list.remove(position);
        selected_list.remove(position);

        if(previous_selected_list.size() > 1){
            unselected_list.set(previous_selected_list.get(1),false);
        }
        previous_selected_list.clear();

        unselected_list.remove(position);
        noteList.remove(position);

        int current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(_note.date);
        prev_selectedPosition = current_pinned_notes;

        dateEdited_list.add(current_pinned_notes,_date);
        noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        //--cambio de estado con referencia al anterior de (0 a 1)
        _note.setPin(_note.getPin() ^ 1);       //XOR Operator
        noteList.add(current_pinned_notes,_note);
        selected_list.add(current_pinned_notes,_selected);
        unselected_list.add(current_pinned_notes,_unselected);
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