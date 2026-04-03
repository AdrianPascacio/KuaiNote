package com.example.kuai_notes_project;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import kotlinx.coroutines.scheduling.Task;

public class Tasks_List extends AppCompatActivity implements Recycler_Tasks_List_Interface, Recycler_Tasks_Sub_List_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener {
    RecyclerView recyclerView;
    Adapter_Recycler_Tasks_List adapter;
    private Check_ViewModel checkViewModel;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;
    ArrayList<Task_Main> taskList;
    ArrayList<Task_Sub> task_subList;
    ArrayList<Task_Element> task_elements;
    ArrayList<Integer> selected_positions_list;

    DB_Tasks DB_T;
    FloatingActionButton floating_button;


    long start_of_today = 0;
    ///Button btn_config, btn_check_lists;
    View main;
    View layout_dim;
    View fl_return, fl_back_ghost;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;
    FloatingActionButton floatingActionButton;
    private Animation AnimationAddNoteButton;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel, Animation_FloatingButton_Appear, Animation_FloatingButton_Disappear;
    private FloatingActionButton fa_btn;

    private static final String CHANNEL_ID = "My_App_Channel";

    Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(this,-1);

    private int selection_count = 0;
    private boolean pin_initial_state_MS= false;
    private boolean selection_mode = false;
    private boolean pin_multi_change = false;
    ///private AdapterView.OnItemClickListener listener;
    ///public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
    ///    this.listener = listener;
    ///}

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        //recyclerView = findViewById(R.id.Recycler_Check_Lists);
        //adapter = new Adapter_Recycler_Check_Lists(this,this);
        //recyclerView.setAdapter(adapter);

        //checkViewModel = new ViewModelProvider(this).get(Check_ViewModel.class);

        //Clear_Lists();


        ///recyclerView = findViewById(R.id.Recycler_MemoBoard);
        ///adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        ///recyclerView.setAdapter(adapter);

        ///Clear_Lists();
        ///Update_Recycler_View();












        Log.d("CheckList","   OnResume  ");

        //Update_Recycler_View();

        recyclerView = findViewById(R.id.Recycler_Tasks_List);
        adapter = new Adapter_Recycler_Tasks_List(this, dateEdited_list,selected_list,taskList,task_subList,task_elements,this,this);
        //adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        recyclerView.setAdapter(adapter);

        checkViewModel = new ViewModelProvider(this).get(Check_ViewModel.class);

        Clear_Lists();
        Update_Recycler_View();

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
        setContentView(R.layout.activity_tasks_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_navigation_bar));

        DB_T = new DB_Tasks(this);

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        taskList = new ArrayList<>();
        task_subList = new ArrayList<>();
        task_elements = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();
        fa_btn = findViewById(R.id.floatingActionButton);
        main = findViewById(R.id.main);
        layout_dim = findViewById(R.id.layout_dim_itemVisualizer);

        fl_return = findViewById(R.id.FrameLayout_Return);
        fl_back_ghost = findViewById(R.id.fl_Back_Ghost);

        floating_button = findViewById(R.id.floatingActionButton);

        AnimationAddNoteButton = AnimationUtils.loadAnimation(this,R.anim.add_note_button_zoom);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        Animation_FloatingButton_Appear = AnimationUtils.loadAnimation(this, R.anim.floating_button_appear);
        Animation_FloatingButton_Disappear = AnimationUtils.loadAnimation(this, R.anim.floating_buttton_disappear);

        ///btn_config = findViewById(R.id.button_Config);
        ///btn_check_lists = findViewById(R.id.button_Check_Lists);
        fa_btn.startAnimation(AnimationAddNoteButton);



        fl_back_ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
            }
        });
        floating_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                New_check_list();
                Update_Recycler_View();
            }
        });

        ///btn_config.setOnClickListener(new View.OnClickListener() {
        ///    @Override
        ///    public void onClick(View view) {
        ///        Go_To_Trash_Can();
        ///    }
        ///});
        ///btn_check_lists.setOnClickListener(new View.OnClickListener() {
        ///    @Override
        ///    public void onClick(View view) {
        ///        Go_To_Check_Lists();
        ///    }
        ///});
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
                    finish();
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
        try (Cursor cursor_Tasks= DB_T.get_All_Tasks()) {
            if(cursor_Tasks.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx = cursor_Tasks.getColumnIndex("_id");
                int date_indx = cursor_Tasks.getColumnIndex("date");
                int title_indx = cursor_Tasks.getColumnIndex("title");
                int note_indx = cursor_Tasks.getColumnIndex("note");
                int pin_indx = cursor_Tasks.getColumnIndex("pin");
                int reminder_indx = cursor_Tasks.getColumnIndex("reminder");
                int reminder_type_indx = cursor_Tasks.getColumnIndex("reminder_type");
                int reminder_interval_indx = cursor_Tasks.getColumnIndex("reminder_interval");

                //while (cursor_Tasks.moveToNext()){
                //    //!!---debe actualizarse
                //    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks.getLong(id_indx));
                //    Note note = new Note(cursor_Tasks.getLong(id_indx),
                //            cursor_Tasks.getLong(date_indx),
                //            cursor_Tasks.getString(title_indx),
                //            ///BNP.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                //            ///        cursor_Notes.getString(note_indx),
                //            ///        60,
                //            ///        55,
                //            ///        0,
                //            ///        3,
                //            ///        1,
                //            ///        30),
                //            cursor_Tasks.getString(note_indx),
                //            cursor_Tasks.getInt(pin_indx)==1,
                //            cursor_Tasks.getLong(reminder_indx),
                //            cursor_Tasks.getInt(reminder_type_indx),
                //            cursor_Tasks.getInt(reminder_interval_indx));
                //    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.date,start_of_today));
                //    noteOriginal_list.add(cursor_Tasks.getString(note_indx));
                //    selected_list.add(false);
                //    noteList.add(note);
                //}
                while (cursor_Tasks.moveToNext()){
                    //!!---debe actualizarse
                    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks.getLong(id_indx));
                    Task_Main task = new Task_Main(cursor_Tasks.getLong(id_indx),
                            cursor_Tasks.getLong(date_indx),
                            cursor_Tasks.getLong(2),
                            cursor_Tasks.getLong(3),
                            cursor_Tasks.getLong(4),
                            cursor_Tasks.getString(title_indx),
                            cursor_Tasks.getString(note_indx),
                            cursor_Tasks.getInt(pin_indx)==1,
                            cursor_Tasks.getLong(reminder_indx),
                            cursor_Tasks.getInt(reminder_type_indx),
                            cursor_Tasks.getInt(reminder_interval_indx),
                            cursor_Tasks.getInt(13)==1,
                            cursor_Tasks.getInt(14)==1);
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(task.date,start_of_today));
                    noteOriginal_list.add(cursor_Tasks.getString(note_indx));
                    selected_list.add(false);
                    taskList.add(task);
                    task_elements.add(task);
                }
            }
        }
        try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub()) {
            if(cursor_Tasks_Sub.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx = cursor_Tasks_Sub.getColumnIndex("_id");
                int parent_indx = cursor_Tasks_Sub.getColumnIndex("parent_id");
                int note_indx = cursor_Tasks_Sub.getColumnIndex("note");
                int completed_indx = cursor_Tasks_Sub.getColumnIndex("completed");
                int task_sub_position_indx = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                while (cursor_Tasks_Sub.moveToNext()){
                    //!!---debe actualizarse
                    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx));
                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx),
                            cursor_Tasks_Sub.getLong(parent_indx),
                            cursor_Tasks_Sub.getString(note_indx),
                            cursor_Tasks_Sub.getInt(completed_indx)==1,
                            cursor_Tasks_Sub.getInt(task_sub_position_indx));
                    task_subList.add(task_sub);
                    task_elements.add(task_sub);
                }
                Log.d("TasksSubList","   TaskSub size:  "+ task_subList.size());
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ///checkViewModel.getAllChecks().observe(this,checkWithSubs -> {

        ///    List<DB_Check_Main> onlyMain = new ArrayList<>();
        ///    for (Check_With_Subs item : checkWithSubs ){
        ///        Log.d("CheckList","   hh  "+item.checkMain.note);
        ///        onlyMain.add(item.checkMain);
        ///    }
        ///    adapter.setChecks(onlyMain);

        ///});
    }
    private void Clear_Lists(){
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty()){
            return;
        }
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
        taskList.clear();
        task_subList.clear();
        task_elements.clear();
    }

    public void New_check_list(){
        if(!selection_mode) {
            ///Intent goTo = new Intent(this, Main_Check_Visualizer.class);
            ///startActivity(goTo);
            ///overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

            //!!-- pasar al main check visualizer
            long _current_time = System.currentTimeMillis();
            long task_new_id = DB_T.Insert_Task_L(_current_time, "Task Title", "Task 1", false, 0, 0, 0);
            long task_sub_new_id = DB_T.Insert_Task_Sub_L(task_new_id,"sub_note",false,0);
            //checkViewModel.saveCheck("2", null);
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        if(selection_mode) {
            Select_Item(position, v);
            return;
        }

        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, MainActivity.class);
        goTo.putExtra("send_date_of_note",_note.date);
        goTo.putExtra("send_note_id",_note.note_id);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Override
    public void onItemHold(int position,View v) {

        Select_Item(position, v);

    }
    private void Select_Item(int position, View v) {
        selected_list.set(position,!selected_list.get(position));// invert value

        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Disappear);

        selection_mode = selection_count > 0;

        selected_positions_list.add(0,position);


        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            //--Buscar estado del pin de las dos primeras notas seleccionadas:
            Note _note = noteList.get(selected_positions_list.get(0));
            Note _note2 = noteList.get(selected_positions_list.get(1));

            //pin_initial_state_MS = false;
            pin_initial_state_MS = _note.getPin() & _note2.getPin() || _note2.getPin(); /// AND Operator !!--Verificar si la opcion de elegir lo primero que escoja el usuario es lo mejor


            selection_item_menu_PopUp.setListener_dismiss(this);
            selection_item_menu_PopUp.show(v, pin_initial_state_MS);

            adapter.Change_multi_selection_state(selection_mode);
            adapter.notifyItemChanged(position,this);
            adapter.notifyItemChanged(selected_positions_list.get(1),this);//!!se estan desvaneciendo sin las animaciones

            //fa_btn.startAnimation(AnimationLayoutDimDisappear_Normal);
        }
        if(selection_item_menu_PopUp.popupWindow != null && !selection_mode){
            //selection_item_menu_PopUp.popupWindow.dismiss();
            //selection_item_menu_PopUp.popupWindow = null;
            //adapter.Change_multi_selection_state(selection_mode);

            //selected_positions_list.clear();
            //fa_btn.startAnimation(AnimationLayoutDimAppear);
            Restart_Selection();
        }
        if(selection_item_menu_PopUp.popupWindow != null && selection_mode){
            //selection_item_menu_PopUp.popupWindow.update(v,60,-150,140,360);
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
        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {
        Note _note = noteList.get(position);
        Task_Main _task = taskList.get(position);
        //int _pin = _note.getPin() ^ 1;      //XOR Operator


        //!!--en modo multiple seleccion, cambiar el pin dependiendo del color del pin
        //!!--no invertir todo
        if(pin_multi_change && pin_initial_state_MS ^ _note.getPin()){///XOR Operator
            selected_list.set(position,!selected_list.get(position));// invert value
            adapter.notifyItemChanged(position);
            return;
        }

        Toast.makeText(this, "Repeated pinned", Toast.LENGTH_SHORT).show();
        adapter.Change_is_repeated_value(true);

        boolean _pin = pin_multi_change ? !pin_initial_state_MS : !_note.getPin();///Ternary Operator


        if(DB_T.Modify_Pin_Status(_note.note_id,_pin)){
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(Tasks_List.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }
    public void RecyclerView_Pin_Update(int position){

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

        int current_pinned_notes = DB_T.get_Specific_Note_Sorted_by_Pin_and_Date(_note.note_id);
        //Log.d("Pin","   current_pin:" + current_pinned_notes+ "    position:" + position);

        dateEdited_list.add(current_pinned_notes,_date);
        noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        //--cambio de estado con referencia al anterior de (0 a 1)
        //_note.setPin(_note.getPin() ^ 1);       //XOR Operator
        _note.setPin(!_note.getPin());
        noteList.add(current_pinned_notes,_note);
        selected_list.add(current_pinned_notes,_selected);
        adapter.notifyItemMoved(position,current_pinned_notes);
        adapter.notifyItemChanged(current_pinned_notes);

        Restart_Selection();

    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        layout_dim.startAnimation(AnimationLayoutDimAppear);

        adapter.Change_is_repeated_value(true);
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


        _note.setReminder(alarm_time);
        //!!---- actualizar type and interval
        _note.setReminder_type(0);
        _note.setReminder_interval(0);
        noteList.remove(position);
        noteList.add(position,_note);
        adapter.notifyItemChanged(position);
    }
    @Override
    public void onPopupClosed(int salida, int postion) {
        layout_dim.setVisibility(View.VISIBLE);
        Restart_Selection();
        if(salida == 1){//setter
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);

            Toast.makeText(this, "reminder dismiss"+"setter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(salida == 2){//cancel
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
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
        Task_Main _task = taskList.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main,_note.note_id);

        if(DB_T.Send_Note_To_Trash(_note.note_id,_note.date,_note.title,noteOriginal_list.get(position),_note.pin,20)){
            //----Remove Note from Recycler View
            dateEdited_list.remove(position);
            noteOriginal_list.remove(position);
            noteList.remove(position);
            taskList.remove(position);
            selected_list.remove(position);
            adapter.notifyItemRemoved(position);

            Restart_Selection();
        }
    }
    private void Restart_Selection() {
        selection_count =0;
        selection_mode = false;
        selected_positions_list.clear();
        if(selection_item_menu_PopUp.popupWindow != null){
            selection_item_menu_PopUp.popupWindow.dismiss();
            selection_item_menu_PopUp.popupWindow = null;
        }
        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
        adapter.Change_multi_selection_state(false);
    }

    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }
    public void Go_To_Check_Lists(){
        Intent goTo = new Intent(this, Tasks_List.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }

    @Override
    public void onMemoBoardSelection_PopupClosed(int option) {
        if(option == 1){
            Toast.makeText(this, "pin", Toast.LENGTH_SHORT).show();

            pin_multi_change = true;

            if(pin_initial_state_MS){
                for(int i = selected_list.size()-1;i >= 0; i--) {
                    if (selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }else{
                for(int i = 0;i < selected_list.size(); i++) {
                    if (selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }

            pin_multi_change = false;
            Restart_Selection();
            return;
        }

        if(option == 2){
            Toast.makeText(this, "reminder", Toast.LENGTH_SHORT).show();
            //    int count = 0;
            //    for(int i = 0;i-count < selected_list.size(); i++){
            //        if(selected_list.get(i-count)){
            //            RemoveItem(i-count);
            //            count ++;
            //        }
            //    }
            //    selected_positions_list.clear();
        }
        if(option == 3){
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
    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
    }
}