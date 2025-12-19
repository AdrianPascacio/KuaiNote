package com.example.kuai_notes_project;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

///354 V4, 461 V6, 411 V7, 442 V7.2,
public class Trash_Can extends AppCompatActivity implements Recycler_Trash_Can_Interface, Selection_Item_Menu_TrashCan_PopUpWindow.ST_PopupDismissListener{
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list, noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;
    ArrayList<Integer> selected_positions_list;

    DB_Notes DB_N;
    Body_Note_Preview BPN;
    Date_of_Note DoN;

    Adapter_Recycler_Trash_Can adapter;

    long start_of_today = 0;
    View fl_return, fl_back_ghost;
    TextView tv_empty_label;
    Animation Animation_empty_label;
    View trash_main_view;

    private int selection_count = 0;
    private boolean selection_mode = false;
    Selection_Item_Menu_TrashCan_PopUpWindow selection_item_menu_PopUp = new Selection_Item_Menu_TrashCan_PopUpWindow(this,-1);

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        recyclerView = findViewById(R.id.Recycler_Trash_Can);
        adapter = new Adapter_Recycler_Trash_Can(this, dateEdited_list,selected_list,noteList,this);
        recyclerView.setAdapter(adapter);

        Clear_Lists();
        Update_Recycler_View();

        if (noteList.isEmpty()){
            Show_Empty_Label();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();

        Restart_Selection();
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
        getWindow().setStatusBarColor(getResources().getColor(R.color.Trashcan_status_bar));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.Trashcan_navigation_bar));

        DB_N = new DB_Notes(this);

        BPN = new Body_Note_Preview();
        DoN = new Date_of_Note();

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        tv_empty_label = findViewById(R.id.TV_Label_Empty_TrashCan);
        Animation_empty_label = AnimationUtils.loadAnimation(this,R.anim.label_empty_animation);
        trash_main_view = findViewById(R.id.main);

        fl_return = findViewById(R.id.FrameLayout_Return);
        fl_back_ghost = findViewById(R.id.fl_Back_Ghost);
        fl_back_ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(selection_item_menu_PopUp.popupWindow != null){
                    for( int i = 0; i < selected_list.size() ; i++){
                        if(selected_list.get(i)== true){
                            selected_list.set(i,false);
                            adapter.notifyItemChanged(i);
                        }
                    }
                    Restart_Selection();
                }else{
                    Return_To_Memo_Board();
                }
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
                            ///BPN.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                            ///        cursor_Notes.getString(note_indx),
                            ///        115,
                            ///        100,
                            ///        0,
                            ///        5,
                            ///        1,
                            ///        30),
                            cursor_Notes.getString(note_indx),
                            cursor_Notes.getInt(pin_indx)== 1,
                            cursor_Notes.getLong(reminder_indx),
                            cursor_Notes.getInt(reminder_type_indx),
                            cursor_Notes.getInt(reminder_interval_indx));
                    //!!---falta una lista para el expire day
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.date,start_of_today));
                    noteOriginal_list.add(cursor_Notes.getString(note_indx));
                    selected_list.add(false);
                    noteList.add(note);
                }
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void Clear_Lists(){
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty()){
            return;
        }
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
    }

    @Override
    public void onItemClick(int position, View v) {
        if(selection_mode) {
            Select_Item(position, v);
            return;
        }

        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, Wasted_Note_Visualizer.class);
        goTo.putExtra("send_date_of_note",_note.date);
        goTo.putExtra("send_note_id",_note.note_id);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Override
    public void onItemHold(int position, View v) {

        Select_Item(position, v);

    }

    private void Select_Item(int position, View v) {
        ///selected_list.set(position,!selected_list.get(position));// invert value

        ///selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!


        ///selection_mode = selection_count > 0;

        ///selected_positions_list.add(0,position);


        ///if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
        ///    //--Buscar estado del pin de las dos primeras notas seleccionadas:
        ///    ///Note _note = noteList.get(selected_positions_list.get(0));
        ///    ///Note _note2 = noteList.get(selected_positions_list.get(1));



        ///    selection_item_menu_PopUp.setListener_dismiss(this);
        ///    selection_item_menu_PopUp.show(v);

        ///    adapter.Change_multi_selection_state(selection_mode);
        ///    adapter.notifyItemChanged(position,this);
        ///    adapter.notifyItemChanged(selected_positions_list.get(1),this);//!!se estan desvaneciendo sin las animaciones

        ///}
        ///if(selection_item_menu_PopUp.popupWindow != null && !selection_mode){
        ///    Restart_Selection();
        ///}
        ///if(selection_item_menu_PopUp.popupWindow != null && selection_mode){
        ///    //selection_item_menu_PopUp.popupWindow.update(v,60,-150,140,360);
        ///}
        ///adapter.notifyItemChanged(position);//!! se esta duplicando con la instruccion de arriba

        /////---Set unselecting_view to repeated unselect
        ///if(selected_positions_list.size()==2) {
        ///    if(Objects.equals(position, selected_positions_list.get(1))){
        ///        adapter.Change_is_repeated_value(true);
        ///        selected_positions_list.clear();
        ///    }
        ///}

        ///if(selected_positions_list.size()==3) selected_positions_list.remove(2);



        selected_list.set(position,!selected_list.get(position));// invert value

        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        selection_mode = selection_count > 0;

        selected_positions_list.add(0,position);

        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            selection_item_menu_PopUp.setListener_dismiss(this);
            //selection_item_menu_PopUp.show(trash_main_view);
            selection_item_menu_PopUp.show(v);

            adapter.Change_multi_selection_state(selection_mode);
            adapter.notifyItemChanged(position);
            adapter.notifyItemChanged(selected_positions_list.get(1));//!!se estan desvaneciendo sin las animaciones
        }
        if(selection_item_menu_PopUp.popupWindow != null && !selection_mode){
            //selection_item_menu_PopUp.popupWindow.dismiss();
            //selection_item_menu_PopUp.popupWindow = null;
            //adapter.Change_multi_selection_state(selection_mode);

            //selected_positions_list.clear();
            Toast.makeText(this, "cerrando popup", Toast.LENGTH_SHORT).show();
            Restart_Selection();
        }
        if(selection_item_menu_PopUp.popupWindow != null && selection_mode){
            selection_item_menu_PopUp.popupWindow.update(v,60,-150,140,360);
        }
        adapter.notifyItemChanged(position);//!! se esta duplicando con la instruccion de arriba

        //---Set unselecting_view to repeated unselect
        if(selected_positions_list.size()==2) {
            if(Objects.equals(position, selected_positions_list.get(1))){
                adapter.Change_is_repeated_value(true);
                selected_positions_list.clear();
            }
        }

        if(selected_positions_list.size()==3) selected_positions_list.remove(2);
    }
    @Override
    public void onTrashCanSelection_PopupClosed(int option) {
        if(option == 1){
            Toast.makeText(this, "recycle", Toast.LENGTH_SHORT).show();
            int count = 0;
            for(int i = 0;i-count < selected_list.size(); i++){
                if(selected_list.get(i-count)){
                    RecycleItem(i-count);
                    count ++;
                }
            }
            selected_positions_list.clear();
            return;
        }

        if(option == 2){
            Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
            int count = 0;
            for(int i = 0;i-count < selected_list.size(); i++){
                if(selected_list.get(i-count)){
                    RemoveItem(i-count);
                    count ++;
                }
            }
            selected_positions_list.clear();
        }
    }

    @Override
    public void RecycleItem(int position) {
        Note _note = noteList.get(position);
        if(DB_N.Recycle_Note(_note.note_id)){
            Remove_Item_From_ArraysLists(position);
        }
    }

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);
        if(DB_N.Delete_Hard_Specific_Note(_note.note_id)) {
            Remove_Item_From_ArraysLists(position);
        }
    }

    private void Remove_Item_From_ArraysLists(int position) {
        dateEdited_list.remove(position);
        noteOriginal_list.remove(position);
        noteList.remove(position);
        selected_list.remove(position);
        adapter.notifyItemRemoved(position);

        //----- verify if is empty:
        if (noteList.isEmpty()){
            Show_Empty_Label();
        }

        Restart_Selection();
    }

    private void Restart_Selection() {
        selection_count =0;
        selection_mode = false;
        selected_positions_list.clear();
        if(selection_item_menu_PopUp.popupWindow != null){
            selection_item_menu_PopUp.popupWindow.dismiss();
            selection_item_menu_PopUp.popupWindow = null;
        }
        adapter.Change_multi_selection_state(false);
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

    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
    }

}