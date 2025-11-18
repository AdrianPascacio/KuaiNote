package com.example.kuai_notes_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

///354 V4, 461 V6, 411 V7
public class Trash_Can extends AppCompatActivity implements Recycler_Trash_Can_Interface, MyItemAnimator.ItemAnimatorListener{
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Boolean> unselected_list;
    ArrayList<Note> noteList;
    ArrayList<Integer> previous_selected_list;

    DB_Notes DB_N;
    Body_Note_Preview BPN;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;

    Adapter_Recycler_Trash_Can adapter;

    long start_of_today = 0;
    View fl_return, fl_back_ghost;
    TextView tv_empty_label;
    Animation Animation_empty_label;
    private int current_hold_position = -1;

    private int prev_selectedPosition = -1;

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        SharedPreferences shared_preferences = getSharedPreferences("Day_change", Context.MODE_PRIVATE);
        String saved_day = shared_preferences.getString("today","numero");

        //-----Comparar fechas
        //!!----- solo se compara si la fecha actual es igual a la fecha guardada para reducir en '1' los dias restantes, sin embargo,
            //!!----- es un error, ya se si no se entra en este activity no se descontara la diferencia de dias entre el guardado y el presente sino solo 1 dia
        ///if(!Objects.equals(_current_time, saved_day)){

        ///    try (Cursor cursor = DB_TC.get_All_Notes()) {
        ///        if(cursor.getCount()==0){
        ///            Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
        ///        }else{
        ///            while (cursor.moveToNext()){
        ///                int expire_day_index = cursor.getColumnIndexOrThrow("expire_days");
        ///                int date_index = cursor.getColumnIndexOrThrow("date");
        ///                String _note_saved_date = cursor.getString(date_index);
        ///                if (cursor.getInt(expire_day_index) > 0) {
        ///                    //---Decrementar expire days
        ///                    //DB_TC.Reduce_Note_Expire_Days(_note_saved_date, cursor.getInt(expire_day_index));
        ///                    //!!___Optimizar Reduce_note_expireDays, esta buscandola para luego restarle un dia e incluso asi no funciona bien
        ///                    //Toast.makeText(this, saved_day+"\n"+_current_time+" : "+cursor.getInt(expire_day_index), Toast.LENGTH_LONG).show();
        ///                } else {
        ///                    //---Delete if is less than "1"
        ///                    //DB_TC.Delete_Specific_Note(_note_saved_date);
        ///                }
        ///            }
        ///        }
        ///    }

        ///    //---Actualizar la fecha
        ///    SharedPreferences.Editor editor = shared_preferences.edit();
        ///    editor.putString("today",_current_time);
        ///    editor.apply();
        ///}

        recyclerView = findViewById(R.id.Recycler_Trash_Can);
        adapter = new Adapter_Recycler_Trash_Can(this, dateEdited_list,selected_list,noteList,unselected_list,this);
        recyclerView.setAdapter(adapter);

        Clear_Lists();
        Update_Recycler_View();

        //----- verify if is empty:
        if (noteList.isEmpty()){
            Show_Empty_Label();
        }
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

        DB_N = new DB_Notes(this);

        BPN = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();


        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        previous_selected_list = new ArrayList<>();
        unselected_list = new ArrayList<>();

        tv_empty_label = findViewById(R.id.TV_Label_Empty_TrashCan);
        Animation_empty_label = AnimationUtils.loadAnimation(this,R.anim.label_empty_animation);

        fl_return = findViewById(R.id.FrameLayout_Return);
        fl_back_ghost = findViewById(R.id.fl_Back_Ghost);
        fl_back_ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
            }
        });

        //--- Back button function:
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    Return_To_Memo_Board();
            }
        });

    }

    private void Update_Recycler_View(){
        try (Cursor cursor_Notes = DB_N.get_All_Notes_Of_Trash()) {
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
                    Note note = new Note(cursor_Notes.getLong(id_indx),
                            cursor_Notes.getLong(date_indx),
                            cursor_Notes.getString(title_indx),
                            BPN.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                                    cursor_Notes.getString(note_indx),
                                    115,
                                    100,
                                    0,
                                    5),
                            cursor_Notes.getInt(pin_indx),
                            cursor_Notes.getLong(reminder_indx),
                            cursor_Notes.getInt(reminder_type_indx),
                            cursor_Notes.getInt(reminder_interval_indx));
                    //!!---falta una lista para el expire day
                    Toast.makeText(this, "date of note: "+cursor_Notes.getString(date_indx), Toast.LENGTH_SHORT).show();
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
    }
    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
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
    }

    @Override
    public void onItemClick(int position) {
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, Wasted_Note_Visualizer.class);
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

        prev_selectedPosition = position;
    }

    private void Set_Unselected_List(int position) {
        //!!--cual es la forma mas eficiente de hacer una cola?
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
        if(DB_N.Delete_Hard_Specific_Note(_note.note_id)) {
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


            //Previous selection must be equal to -1
            prev_selectedPosition = -1;


            //----- verify if is empty:
            if (noteList.isEmpty()){
                Show_Empty_Label();
            }
        }
    }

    private void Show_Empty_Label() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_empty_label.setVisibility(View.VISIBLE);
                tv_empty_label.startAnimation(Animation_empty_label);
            }
        }, 250);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
            }
        }, 1450);
    }

    /// ----------------------------------------------------------Pin Items:
    @Override
    public void PinItem(int position) {
        //----Pin Status positive
        Note _note = noteList.get(position);
        int _pin = _note.getPin() ^ 1;

        //----Database update with new pin status value:
        if(DB_N.Modify_Pin_Status(_note.note_id,_pin)){
            //----RecyclerView pin status update:
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(Trash_Can.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void RecycleItem(int position) {
        Note _note = noteList.get(position);
        if(DB_N.Recycle_Note(_note.note_id,_note.date,_note.title,noteOriginal_list.get(position),_note.pin,0,0,0)){
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

            //Previous selection must be equal to -1
            prev_selectedPosition = -1;

            //----- verify if is empty:
            if (noteList.isEmpty()){
                Show_Empty_Label();
            }

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

        int current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date_In_Trash(_note.date);
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

    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
    }
}