package com.example.kuai_notes_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
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

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

///324 V3, 305 V4, 358 V6, 306 V7
public class Memo_Board extends AppCompatActivity implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener, MyItemAnimator.ItemAnimatorListener{
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
    Button btn_aux;
    View main;
    private int current_hold_position = -1;
    private int prev_selectedPosition = -1;
    private int postprev_selectedPosition = -1;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;
    private Animation AnimationAddNoteButton;
    private FloatingActionButton fa_btn;

    private static final String CHANNEL_ID = "My_App_Channel";

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

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

        Create_Notification_Channel();

        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
            }
        });
        //btn_aux.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Start_Reminder_PopUpWindow();
        //    }
        //});
    }
    @Override
    public void SetReminder(int position) {
        //This is functional but pero estoy tratando de pasarlo a una clase externa para poder reutilizarlo
        ///Start_Reminder_PopUpWindow(position);

        //--- popup in anotherclass:
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, position);
        reminder_PopUp.setListener(this);

        Note _note = noteList.get(position);
        reminder_PopUp.show(main, _note);

    }
    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
    }

    private void Create_Notification_Channel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "General Notification";
            String description = "Application notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
                                    2),
                            cursor_Notes.getInt(pin_indx),
                            cursor_Notes.getLong(reminder_indx),
                            cursor_Notes.getInt(reminder_type_indx),
                            cursor_Notes.getInt(reminder_interval_indx));
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.date,start_of_today));
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
    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }

    @Override
    public void onItemClick(int position) {
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, MainActivity.class);
        goTo.putExtra("send_date_of_note",_note.date);
        goTo.putExtra("send_note_id",_note.note_id);
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

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);
        //----Remove Note from Data Base:
        Reminder_Notification.Cancel_Reminder_Alarm(main,_note.note_id);
        if(DB_N.Send_Note_To_Trash(_note.note_id,_note.date,_note.title,noteOriginal_list.get(position),_note.pin,_note.reminder,0,0,20)){
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


    /// ----------------------------------------------------------Pin Items:
    @Override
    public void PinItem(int position) {
        //----Pin Status positive
        Note _note = noteList.get(position);
        int _pin = _note.getPin() ^ 1;      //XOR Operator

        //----Database update with new pin status value:
        if(DB_N.Modify_Pin_Status(_note.note_id,_pin)){
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

    /// ----------------------------------------------------------Reminder Note:

    @Override
    //!!!--- currently is not working
    public void onAnimationFinished() {
        if(current_hold_position != -1){
            selected_list.set(current_hold_position,true);
            adapter.notifyItemChanged(current_hold_position);

            current_hold_position = -1;
        }
    }

    @Override
    public void OnValueSelected(int position, long alarm_time) {
        Note _note = noteList.get(position);
        selected_list.set(position,false);
        unselected_list.set(position,true);

        if(previous_selected_list.size() > 1){
            unselected_list.set(previous_selected_list.get(1),false);
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
}