package com.example.kuai_notes_project.ruled_out_code;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.Adapter_Recycler_Memo_Board;
import com.example.kuai_notes_project.Body_Note_Preview;
import com.example.kuai_notes_project.DB_Notes;
import com.example.kuai_notes_project.Date_of_Note;
import com.example.kuai_notes_project.MainActivity;
import com.example.kuai_notes_project.Note;
import com.example.kuai_notes_project.R;
import com.example.kuai_notes_project.Recycler_Memo_Board_Interface;
import com.example.kuai_notes_project.Reminder_Notification;
import com.example.kuai_notes_project.Reminder_PopUpWindow;
import com.example.kuai_notes_project.Selection_Item_Menu_MemoBoard_PopUpWindow;
import com.example.kuai_notes_project.Trash_Can;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

///324 V3, 305 V4, 358 V6, 306 V7, 450 V7.2
public class Memo_Board_BeforeSelectionMultiple_25nov2025 extends AppCompatActivity implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener{
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Boolean> unselected_list;
    ArrayList<Note> noteList;
    ArrayList<Integer> previous_selected_list;

    DB_Notes DB_N;

    Adapter_Recycler_Memo_Board adapter;

    long start_of_today = 0;
    Button btn_config;
    View main;
    View layout_dim;
    private int penultimate_Position = -1;
    private int antepenultimate_Position = -1;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;
    private Animation AnimationAddNoteButton;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel;
    private FloatingActionButton fa_btn;

    private static final String CHANNEL_ID = "My_App_Channel";

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        recyclerView = findViewById(R.id.Recycler_MemoBoard);
        //adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,unselected_list,this);
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

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        unselected_list = new ArrayList<>();
        previous_selected_list = new ArrayList<>();

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();
        fa_btn = findViewById(R.id.floatingActionButton);
        main = findViewById(R.id.main);
        layout_dim = findViewById(R.id.layout_dim_itemVisualizer);

        AnimationAddNoteButton = AnimationUtils.loadAnimation(this,R.anim.add_note_button_zoom);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);

        btn_config = findViewById(R.id.button_Config);
        fa_btn.startAnimation(AnimationAddNoteButton);

        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
            }
        });
    }

    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
    }

    private void Update_Recycler_View(){
        try (Cursor cursor_Notes = DB_N.get_All_Notes()) {
            if(cursor_Notes.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx = cursor_Notes.getColumnIndex("_id");
                int date_indx = cursor_Notes.getColumnIndex("date");
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");
                int pin_indx = cursor_Notes.getColumnIndex("pin");
                int reminder_indx = cursor_Notes.getColumnIndex("reminder");
                int reminder_type_indx = cursor_Notes.getColumnIndex("reminder_type");
                int reminder_interval_indx = cursor_Notes.getColumnIndex("reminder_interval");

                while (cursor_Notes.moveToNext()){
                    //!!---debe actualizarse
                    Note note = new Note(cursor_Notes.getLong(id_indx),
                            cursor_Notes.getLong(date_indx),
                            cursor_Notes.getString(title_indx),
                            BNP.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                                    cursor_Notes.getString(note_indx),
                                    60,
                                    55,
                                    0,
                                    2,
                                    1,
                                    30),
                            cursor_Notes.getInt(pin_indx)==1,
                            cursor_Notes.getLong(reminder_indx),
                            cursor_Notes.getInt(reminder_type_indx),
                            cursor_Notes.getInt(reminder_interval_indx));
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.getDate(),start_of_today));
                    noteOriginal_list.add(cursor_Notes.getString(note_indx));
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
                    if(penultimate_Position != -1){
                        if(selected_list.get(penultimate_Position)== true){
                            Toast.makeText(Memo_Board_BeforeSelectionMultiple_25nov2025.this, "arras 1", Toast.LENGTH_SHORT).show();
                            selected_list.set(penultimate_Position,false);
                            unselected_list.set(penultimate_Position,true);
                            adapter.notifyItemChanged(penultimate_Position);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    unselected_list.set(penultimate_Position,false);
                                    adapter.notifyItemChanged(penultimate_Position,this);
                                    penultimate_Position = -1;
                                    previous_selected_list.clear();
                                }
                            }, 500); // Realiza accion luego de 500 milisegundos
                        }
                    }
                    if(antepenultimate_Position != -1){
                        if(unselected_list.get(antepenultimate_Position) == true){
                            Toast.makeText(Memo_Board_BeforeSelectionMultiple_25nov2025.this, "arras 2", Toast.LENGTH_SHORT).show();
                            unselected_list.set(antepenultimate_Position,false);
                            //unselected_list.set(prev_selectedPosition,false);
                            adapter.notifyItemChanged(antepenultimate_Position,this);
                        }
                        antepenultimate_Position = -1;       //!! Test
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
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty()){
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

    @Override
    public void onItemClick(int position, View v) {
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, MainActivity.class);
        goTo.putExtra("send_date_of_note",_note.getNote_id());
        goTo.putExtra("send_note_id",_note.getNote_id());
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Override
    public void onItemHold(int position, View v) {

        if(position == penultimate_Position){
            selected_list.set(position,!selected_list.get(position));// invert value
        }else{
            //--Si previo esta activado entonces desactivar
            if(penultimate_Position != -1){
                boolean previousIsSelected = selected_list.get(penultimate_Position);
                if(previousIsSelected){
                    selected_list.set(penultimate_Position,false);
                    adapter.notifyItemChanged(penultimate_Position);
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        Set_Unselected_List(position);

        antepenultimate_Position = penultimate_Position;
        penultimate_Position = position;

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

    /// Pin Items:
    @Override
    public void PinItem(int position) {
        Note _note = noteList.get(position);
        //int _pin = _note.getPin() ^ 1;      //XOR Operator
        boolean _pin = !_note.getPin();      //XOR Operator

        if(DB_N.Modify_Pin_Status(_note.getNote_id(),_pin)){
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(Memo_Board_BeforeSelectionMultiple_25nov2025.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }
    public void RecyclerView_Pin_Update(int position){
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

        /// Test:
        if(previous_selected_list.size() > 2){
            unselected_list.set(previous_selected_list.get(2),false);
        }
        /// Test ^

        previous_selected_list.clear();

        unselected_list.remove(position);
        noteList.remove(position);

        int current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(_note.getNote_id());
        Log.d("Pin","   current_pin:" + current_pinned_notes+ "    position:" + position);
        penultimate_Position = current_pinned_notes;

        dateEdited_list.add(current_pinned_notes,_date);
        noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        //--cambio de estado con referencia al anterior de (0 a 1)
        //_note.setPin(_note.getPin() ^ 1);       //XOR Operator
        _note.setPin(!_note.getPin());
        noteList.add(current_pinned_notes,_note);
        selected_list.add(current_pinned_notes,_selected);
        //unselected_list.add(current_pinned_notes,_unselected);  ///!! Test
        adapter.notifyItemMoved(position,current_pinned_notes);
        unselected_list.set(current_pinned_notes,false); ///!! Test
        adapter.notifyItemChanged(current_pinned_notes);

    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        layout_dim.startAnimation(AnimationLayoutDimAppear);

        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, position);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        Note _note = noteList.get(position);
        reminder_PopUp.show(main, _note);
    }
    @Override
    public void OnValueSelected(int position, long alarm_time) {
        Note _note = noteList.get(position);
        selected_list.set(position,false);
        unselected_list.set(position,true);

        if(previous_selected_list.size() > 1){
            unselected_list.set(previous_selected_list.get(1),false);
        }
        if(previous_selected_list.size() > 2){
            unselected_list.set(previous_selected_list.get(2),false);
        }
        previous_selected_list.clear();

        _note.setReminder(alarm_time);
        //!!---- actualizar type and interval
        _note.setReminder_type(0);
        _note.setReminder_interval(0);
        noteList.remove(position);
        noteList.add(position,_note);
        adapter.notifyItemChanged(position);
    }
    @Override
    public void onPopupClosed(int salida) {
        layout_dim.setVisibility(View.VISIBLE);
        if(salida == 1){//setter
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ex_green)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);

            Toast.makeText(this, "reminder dismiss"+"setter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(salida == 2){//cancel
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ex_orange)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            Toast.makeText(this, "reminder dismiss"+"cancel", Toast.LENGTH_SHORT).show();

            return;
        }
        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);

        Toast.makeText(this, "reminder dismiss"+"normal", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main,_note.getNote_id());

        if(DB_N.Send_Note_To_Trash(_note.getNote_id(),_note.getDate(),_note.getTitle(),noteOriginal_list.get(position),_note.getPin(),20)){
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

            penultimate_Position = -1;
        }
    }

    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }

}