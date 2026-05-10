package com.example.kuai_notes_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//488 01apr2026, 1207 v9.0B
public class Task_Visualizer extends AppCompatActivity implements Recycler_Tasks_Sub_In_Visualizer_Interface, Reminder_PopUpWindow_Tasks.OnValueSelectedListener, Reminder_PopUpWindow_Tasks.PopupDismissListener,Note_Update_Listener {
    private int order_type = 0;
    private int new_position_a =  0;
    private int new_position_b =  0;
    private long sub_taskID_a =   0;
    private long sub_taskID_b =   0;
    private DB_Tasks DB_T;
    private TextView tv_Date, tv_Completion;
    private EditText et_Task_main;
    private Note note = new Note();
    private Task_Main task = new Task_Main();


    private RecyclerView recyclerView;
    Adapter_Recycler_Tasks_Sub_In_Visualizer adapter;
    private ArrayList<Task_Sub> task_subList;
    private ArrayList<Boolean> selected_list;

    private long received_task_id = 0;
    private boolean change_in_task = false, show_task_info = false, now_is_something_written = false;
    private boolean has_sub_tasks_in_database = false;
    private FrameLayout fl_Change_Pin_Status, fl_Change_Reminder_Status, fl_Back, fl_Delete, fl_Insert_Sub_Task, fl_Copy_To_Clipboard, fl_Set_Order;
    private FrameLayout fl_Change_Pin_Status_Ghost, fl_Change_Reminder_Status_Ghost, fl_Back_Ghost, fl_Delete_Ghost;
    private FrameLayout fl_Main_Task_Complete;
    private Date_of_Note DoN;
    private View layout_date_and_info, layout_body_task, layout_dim;
    private Animation AnimationPin, AnimationReminder, AnimationDate, AnimationDateInvert, AnimationInfo, AnimationInfoInvert, AnimationPinAppear, AnimationPinFade;
    private Animation AnimationNoteAppear, AnimationTitleAppear, AnimationNoteHintFading;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel;
    private int previous_note_size = -1;
    private char last_deleted_char = '0';
    Indent_Replicator indentReplicator;
    ///private Space space_below_note;


    @Override
    protected void onPause() {
        super.onPause();
        if ((Task_is_not_empty() || !All_Current_Sub_Task_Are_Empty_2()) && change_in_task) {
            Log.d("Task_Visualizer", "onPause, saving");
            Save_Task();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((Task_is_not_empty() || !All_Current_Sub_Task_Are_Empty_2())  && change_in_task) {
            Log.d("Task_Visualizer", "onResume, saving");
            Save_Task();
        }

        recyclerView = findViewById(R.id.Recycler_Task_Sub_in_Visualizer);
        adapter = new Adapter_Recycler_Tasks_Sub_In_Visualizer(this, selected_list,task_subList,this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {//0 para eliminar los swipe horizontales utilizados para borrar.
            int lesser_position = 0;
            int lesser_position_assigned = 0;
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from_Positoin = viewHolder.getAdapterPosition();
                int to_Positoin = target.getAdapterPosition();

                //Collections.swap(task_subList,from_Positoin,to_Positoin);
                lesser_position = to_Positoin < from_Positoin ? to_Positoin : from_Positoin;
                Task_Sub _task_sub = task_subList.get(from_Positoin);
                task_subList.remove(from_Positoin);
                task_subList.add(to_Positoin,_task_sub);
                new_position_a = task_subList.get(from_Positoin).getTask_sub_position();
                new_position_b = task_subList.get(to_Positoin).getTask_sub_position();
                lesser_position_assigned = new_position_a < new_position_b ? new_position_a : new_position_b;
                sub_taskID_a = task_subList.get(from_Positoin).getTask_Sub_id();
                sub_taskID_b = task_subList.get(to_Positoin).getTask_Sub_id();
                adapter.notifyItemMoved(from_Positoin,to_Positoin);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
                //!!--modificacion miestra se agarra el item
                super.onSelectedChanged(viewHolder, actionState);
                if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                    viewHolder.itemView.setAlpha(0.5f);
                }
            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
                //!!--Modificacion al soltar el item
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f);

                //Save_Sub_Tasks_New_Positions();
                Save_Sub_Tasks_New_Positions(lesser_position,lesser_position_assigned,sub_taskID_a, sub_taskID_b,new_position_a,new_position_b);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Clear_Lists();
        Update_Recycler_View();
    }
    private void Save_Sub_Tasks_New_Positions(int lesser_position,int lesser_position_assigned,long sub_taskID_a, long sub_taskID_b, int position_a, int position_b) {
        DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, position_b);

        for(int i = lesser_position; i <= task_subList.size()-1; i++){

            sub_taskID_a = task_subList.get(i).getTask_Sub_id();
            DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, i + 1);
            Log.d("Moving Position", "task: "+task_subList.get(i).note+ "    new position: "+(i+1));
        }
    }

    private void Update_Recycler_View(){

        try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task.task_id)) {
            if(cursor_Tasks_Sub.getCount()==0){
                Log.d("Read cursor_Tasks", "Cursor_Tasks : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                while (cursor_Tasks_Sub.moveToNext()){
                    //!!---debe actualizarse
                    Log.d("Read cursor_Tasks", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                            cursor_Tasks_Sub.getLong(parent_indx_sub),
                            cursor_Tasks_Sub.getString(note_indx_sub),
                            cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                            cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                    task_subList.add(task_sub);
                    selected_list.add(false);
                }
                Log.d("TasksSubList","   TaskSub size:  "+ task_subList.size());
            }
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void Clear_Lists(){
        if(task_subList.isEmpty()){
            return;
        }
        selected_list.clear();
        task_subList.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_visualizer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_navigation_bar));

        task_subList = new ArrayList<>();
        selected_list = new ArrayList<>();
        DB_T = new DB_Tasks(this);

        tv_Completion = findViewById(R.id.Task_Completion);
        tv_Date = findViewById(R.id.Task_Date);

        et_Task_main = findViewById(R.id.Task_Main_Text);

        fl_Change_Pin_Status = findViewById(R.id.FrameLayout_Change_Pin_Status);
        fl_Change_Reminder_Status = findViewById(R.id.FrameLayout_Change_Reminder_Status);
        fl_Back = findViewById(R.id.fl_Back);
        fl_Delete = findViewById(R.id.fl_Delete);

        fl_Change_Pin_Status_Ghost = findViewById(R.id.FrameLayout_Change_Pin_Status_Ghost);
        fl_Change_Reminder_Status_Ghost = findViewById(R.id.FrameLayout_Change_Reminder_Status_Ghost);
        fl_Back_Ghost = findViewById(R.id.fl_Back_Ghost);
        fl_Delete_Ghost = findViewById(R.id.fl_Delete_Ghost);
        fl_Insert_Sub_Task = findViewById(R.id.FrameLayout_Insert_Sub_Task);
        fl_Copy_To_Clipboard = findViewById(R.id.FrameLayout_Copy_To_Clipboard);
        fl_Set_Order = findViewById(R.id.FrameLayout_Order);
        fl_Main_Task_Complete = findViewById(R.id.FrameLayout_Change_Complete_Task_Main);

        DoN = new Date_of_Note();

        layout_date_and_info = findViewById(R.id.Layout_date_and_info);
        layout_body_task = findViewById(R.id.Layout_Body_Task);

        AnimationPin = AnimationUtils.loadAnimation(this, R.anim.pin_visualizer_change_status);
        AnimationReminder = AnimationUtils.loadAnimation(this, R.anim.reminder_visualizer_change_status);
        AnimationDate = AnimationUtils.loadAnimation(this, R.anim.date_visualizer);
        AnimationDateInvert = AnimationUtils.loadAnimation(this, R.anim.date_visualizer_invert);
        AnimationInfo = AnimationUtils.loadAnimation(this, R.anim.info_visualizer);
        AnimationInfoInvert = AnimationUtils.loadAnimation(this, R.anim.info_visualizer_invert);
        AnimationPinAppear = AnimationUtils.loadAnimation(this, R.anim.appear_visualizer);
        AnimationPinFade = AnimationUtils.loadAnimation(this, R.anim.fade_visualizer);
        AnimationNoteAppear = AnimationUtils.loadAnimation(this, R.anim.note_appear_mainvisualizer);
        AnimationTitleAppear = AnimationUtils.loadAnimation(this, R.anim.title_appear_mainvisualizer);
        AnimationNoteHintFading = AnimationUtils.loadAnimation(this, R.anim.hint_note_fading_visualizer);

        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);

        received_task_id = getIntent().getLongExtra("send_task_id", 0);

        layout_dim = findViewById(R.id.layout_dim_noteVisualizer);
        indentReplicator = new Indent_Replicator(this);



        if (received_task_id != 0) {
            Initialize_Received_Note(received_task_id);
            Set_Written_Note_Style();
        } else {
            Set_Blank_Note_Style();
            tv_Completion.setVisibility(View.GONE);
            tv_Date.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {//Se enfoca en titulo del tasky abre el teclado solo si el task es nuevo
                @Override
                public void run() {
                    et_Task_main.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //Abrir teclado luego de realizar el enfoque:
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(et_Task_main, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }, 300); // Realiza accion luego de 300 milisegundos
        }

        et_Task_main.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_task = true;
                Verify_if_exist_something();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        fl_Insert_Sub_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Insert_Sub_Task();
                change_in_task = true;

                if(task.completed) {
                    Change_Complete_Main_Task_Status();
                }
                Update_Completion_Ratio();
            }
        });
        fl_Copy_To_Clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Copy_Tasks_To_Clipboard();
            }
        });
        fl_Set_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set_Tasks_Order();
            }
        });
        fl_Main_Task_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Change_Complete_Main_Task_Status();
                if(task.has_sub_tasks) {
                    Change_Sub_task_Completed_Status();
                }
                Update_Completion_Ratio();
                Update_Date();
            }
        });
        fl_Change_Pin_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Task_is_not_empty()|| !All_Current_Sub_Task_Are_Empty_2()) {
                    Pin_Task();
                    fl_Change_Pin_Status.startAnimation(AnimationPin);
                }
            }
        });
        fl_Change_Reminder_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Task_is_not_empty()|| !All_Current_Sub_Task_Are_Empty_2()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(et_Task_main.getWindowToken(),0);
                    }

                    tv_Date.setAlpha(0.9f);
                    tv_Completion.setAlpha(0.9f);
                    et_Task_main.setAlpha(0.8f);
                    layout_dim.setVisibility(View.VISIBLE);
                    layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
                    layout_dim.startAnimation(AnimationLayoutDimAppear);

                    Set_Reminder_Note();
                    fl_Change_Reminder_Status.startAnimation(AnimationReminder);
                }
            }
        });
        ///layout_date_and_info.setOnClickListener(new View.OnClickListener() {
        ///    @Override
        ///    public void onClick(View view) {
        ///        Date_Format_Change();
        ///    }
        ///});
        ///layout_body_note.setOnClickListener(new View.OnClickListener() {
        ///    @Override
        ///    public void onClick(View view) {
        ///        et_Note.requestFocus();
        ///        et_Note.setSelection(et_Note.getText().length());
        ///        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ///        if (inputMethodManager != null) {
        ///            inputMethodManager.showSoftInput(et_Note, InputMethodManager.SHOW_IMPLICIT);
        ///        }
        ///    }
        ///});

        fl_Delete_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Task();
            }
        });
        fl_Back_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Out_Of_Activity();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Out_Of_Activity();
            }
        });
    }

    private void Set_Tasks_Order() {
        //--Order states:
            //1- default = uncompleted first
            //2- completed_first
            //3- custom
        //--when it begin in default (uncompleted first)
        int completion_size = 0;
        int sub_Task_size = task_subList.size();
        for(int i = 0; i <= task_subList.size() -1 ; i++){
            if(task_subList.get(i).geCompleted()){
                completion_size ++;
            }
        }
        //--Cambiar solo si el ratio no es absoluto (todas incompletas o tadas completas)
        //--Y si la cantidad de sub task es mayor que 1
        if(completion_size > 0 && completion_size != sub_Task_size && sub_Task_size >=2){
            if(order_type == 0){ /// --   0→Default (Uncomplete first) to → Complete first
                int changes = 0;
                for(int i = 0; i <= sub_Task_size - 1 - changes; i++){
                    if(task_subList.get(i).geCompleted() == false){
                        Log.d("Task Visualizer" , " Default Uncomplete first: Diferente: " +"   task:" +task_subList.get(i).getContent() );

                        Task_Sub _task_sub = task_subList.get(i);
                        task_subList.remove(i);
                        task_subList.add(sub_Task_size-1,_task_sub);

                        adapter.notifyItemMoved(i,sub_Task_size-1);
                        changes ++;
                        i--;
                    }
                }
                    order_type = 1;   //-- Complete first

            }else if(order_type == 1){//-- Complete first to →  Custom
                Log.d("Task Visualizer" , "New Order:  Custom: ");
                for(int i = 0; i <= sub_Task_size - 1; i++){
                    if(task_subList.get(i).getTask_sub_position() != (i + 1)){
                        Log.d("Task Visualizer" , " Custom: Diferente: " +"   task:" +task_subList.get(i).getContent() + "  tiene pos:" + task_subList.get(i).getContent());
                        for(int j = i+1; j <= sub_Task_size -1 ; j++){
                            if(task_subList.get(j).getTask_sub_position() == (i + 1)){
                                Log.d("Task Visualizer" , "   Custom: " +task_subList.get(j).getContent() );
                                Task_Sub _task_sub = task_subList.get(j);
                                task_subList.remove(j);
                                task_subList.add(i,_task_sub);

                                adapter.notifyItemMoved(j,i);
                                break;

                            }
                        }
                    }
                }

                order_type = 2;   //-- Custom
            }else if(order_type == 2){//--   Custom to → Default (Uncomplete first)
                int changes = 0;
                for(int i = 0; i <= sub_Task_size - 1 - changes; i++){
                    if(task_subList.get(i).geCompleted() == true){
                        Log.d("Task Visualizer" , " Default Uncomplete first: Diferente: " +"   task:" +task_subList.get(i).getContent() );

                        Task_Sub _task_sub = task_subList.get(i);
                        task_subList.remove(i);
                        task_subList.add(sub_Task_size-1,_task_sub);

                        adapter.notifyItemMoved(i,sub_Task_size-1);
                        changes ++;
                        i--;
                    }
                }

                order_type = 0;   //-- Default
            }

        }

    }


    private void Copy_Tasks_To_Clipboard() {
        StringBuilder clip_text = new StringBuilder("");
        if(!now_is_something_written){
            return;
        }
        boolean Tasks_Have_Title = !et_Task_main.getText().toString().isEmpty();
        if(Tasks_Have_Title){
            clip_text.append(et_Task_main.getText().toString() );
        }
        if(!task_subList.isEmpty()){
            if(Tasks_Have_Title){
                for(int i = 0 ; i <= task_subList.size() - 1; i++){
                    Task_Sub _task_sub = task_subList.get(i);
                    ///Falta eliminar la posibilidad de copiar tasks en blanco
                    if(!_task_sub.note.toString().trim().isEmpty()) {
                        clip_text.append("\n  ·" + _task_sub.note);
                    }
                }
            }else{
                ///Falta eliminar la posibilidad de copiar tasks en blanco
                Task_Sub _task_sub = task_subList.get(0);
                clip_text.append("·" + _task_sub.note);
                for(int i = 1 ; i <= task_subList.size() - 1; i++){
                    _task_sub = task_subList.get(i);

                    if(!_task_sub.note.toString().trim().isEmpty()) {
                        clip_text.append("\n·" + _task_sub.note);
                    }
                }
            }
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Pending Tasks Clipped", clip_text.toString());

        if(clipboard != null){
            clipboard.setPrimaryClip(clip);

            Toast.makeText(Task_Visualizer.this, "Task has been copy.", Toast.LENGTH_SHORT).show();
        }

    }

    private void Insert_Sub_Task() {

        //!!--Esto est'a funcionando como un auxiliar, se debe primero captar la modificacion y si es valida entonces se debe guardar en la base de datos, no al revez.
        if(received_task_id == 0){
            Save_Task();
            received_task_id = task.task_id;
        }
        int _new_sub_task_position = DB_T.Verify_Top_Sub_Task_Position(received_task_id) + 1;
        long task_sub_new_id = DB_T.Insert_Task_Sub_L(received_task_id,"",false,_new_sub_task_position);
        if(task_sub_new_id >= 0){
            Task_Sub task_sub = new Task_Sub(task_sub_new_id,
                    received_task_id,
                    "",
                    false,
                    task_subList.size());

            task_subList.add(task_sub);
            adapter.notifyItemInserted(task_subList.size());
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    int last_Position = adapter.getItemCount() -1 ;

                    recyclerView.smoothScrollToPosition(last_Position);
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(last_Position);

                    if(viewHolder != null){
                        EditText etNote = ((Adapter_Recycler_Tasks_Sub_In_Visualizer.MyViewHolder_Task_Sub) viewHolder).task_sub_description_id;
                        etNote.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm != null){
                            imm.showSoftInput(etNote,InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
            });
        }
        if(task.has_sub_tasks == false){
            if(DB_T.Modify_Has_Sub_Tasks_Status(task.task_id,true)){
                task.setHas_Sub_Tasks(true);
            }
        }
    }

    private void Initialize_Received_Note(long received_task_id) {
        task = DB_T.getASpecificTask(received_task_id);

        //!!--Duplicated:
        if (task.completed) {
            //!!--Verificar los colores correctos:
            fl_Main_Task_Complete.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ex_green)));
        } else {
            //!!--Verificar los colores correctos:
            fl_Main_Task_Complete.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_light_3)));
        }

        if(task.has_sub_tasks){
            try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task.task_id)) {
                if(cursor_Tasks_Sub.getCount()==0){
                    Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
                }else{
                    int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                    int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                    int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                    int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                    int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                    while (cursor_Tasks_Sub.moveToNext()){
                        //!!---debe actualizarse
                        Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                        Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                                cursor_Tasks_Sub.getLong(parent_indx_sub),
                                cursor_Tasks_Sub.getString(note_indx_sub),
                                cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                                cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                        task_subList.add(task_sub);
                    }
                    has_sub_tasks_in_database = true;
                    Log.d("TasksSubList","   TaskSub size:  "+ task_subList.size());
                }

            }
        }


        now_is_something_written = true;
        et_Task_main.setText(task.note);
        Update_Completion_Ratio();
        Update_Date();

        Change_Pin_Status_Style();
        Change_Reminder_Status_Style();
        previous_note_size = task.note.length();
    }

    private void Update_Date() {
        if(received_task_id > 0){
            tv_Date.setVisibility(View.VISIBLE);
        }else{
            tv_Date.setVisibility(View.GONE);
        }
        if(task.completed){
            tv_Date.setText("Completed: " +  DoN.Set_Date_of_Note_In_Visualizer(task.date_completed));
        }else{
            if(task.date_created == task.date_modified){
                //--Date created
                tv_Date.setText("Created: " + DoN.Set_Date_of_Note_In_Visualizer(task.date_created));

            }else{
                //--Date modified
                tv_Date.setText("Modified: "+DoN.Set_Date_of_Note_In_Visualizer(task.date_modified));
            }
        }
    }

    private void Update_Completion_Ratio() {
        if(task.has_sub_tasks){
            //!!--La visibilidad se esta activando cada vez que se refresca innecesariamente
            tv_Completion.setVisibility(View.VISIBLE);
            int completion_size = 0;
            int sub_Task_size = task_subList.size();
            for(int i = 0; i <= task_subList.size() -1 ; i++){
                if(task_subList.get(i).geCompleted()){
                    completion_size ++;
                }
            }
            tv_Completion.setText(completion_size + "/" + sub_Task_size);
        }else{
            tv_Completion.setVisibility(View.GONE);
        }
    }

    private void Set_Written_Note_Style() {
        et_Task_main.startAnimation(AnimationTitleAppear);
    }

    private void Set_Blank_Note_Style() {
        fl_Change_Reminder_Status.setScaleX(0.9f);
        fl_Change_Reminder_Status.setScaleY(0.9f);
        fl_Change_Pin_Status.setScaleX(0.9f);
        fl_Change_Pin_Status.setScaleY(0.9f);
        fl_Delete.setScaleX(0.9f);
        fl_Delete.setScaleY(0.9f);
        fl_Delete.setScaleY(0.9f);
        fl_Change_Reminder_Status.setAlpha(0.4f);
        fl_Change_Pin_Status.setAlpha(0.4f);
        fl_Delete.setAlpha(0.4f);

    }

    private void Verify_if_exist_something() {
        if (Task_is_not_empty() != now_is_something_written) {//    si el estado de la nota ha cambiado:
            now_is_something_written = Task_is_not_empty();
            //!!--Update_Task_Status is wrong because now_is_something is contemplating only the main task
            Update_Task_Status(now_is_something_written);
        }
    }

    private boolean Task_is_not_empty() {
        String _Main_Task_Description = et_Task_main.getText().toString();

        //!!--Solo funciona para el main task, falta comprobar la validez de los cambios realizados en los sub tasks
        ///ORIGINAL: return !_title.isEmpty() || !_note.isEmpty();
        return !_Main_Task_Description.isEmpty();
    }

    private void Update_Task_Status(boolean current_status) {
        if(current_status) {
            fl_Change_Pin_Status.setAlpha(1f);
            fl_Change_Reminder_Status.setAlpha(1f);
            fl_Delete.setAlpha(1f);

            fl_Change_Pin_Status.startAnimation(AnimationPinAppear);
            fl_Change_Reminder_Status.startAnimation(AnimationPinAppear);
            fl_Delete.startAnimation(AnimationPinAppear);
        }else{
            fl_Change_Pin_Status.startAnimation(AnimationPinFade);
            fl_Change_Reminder_Status.startAnimation(AnimationPinFade);

            if(note.note_id == 0){
                fl_Delete.startAnimation(AnimationPinFade);
            }
        }
    }

    @Override
    public void Update_Note_Content(int indent_type, char last_deleted_char, int previous_note_size, int cursor_selection) {
        this.previous_note_size = previous_note_size;
        this.last_deleted_char = last_deleted_char;
    }

    /// Pin Task
    private void Pin_Task() {
        task.setPin(!task.getPin());

        if(task.task_id == 0){
            Change_Pin_Status_Style();
            return;
        }

        //!!--That most be updated. just work with thhe note secition
        if (DB_T.Modify_Pin_Status(task.task_id, task.pin)) {
            Toast.makeText(Task_Visualizer.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
            Change_Pin_Status_Style();
        } else {
            Log.d("Task Visualizer", "Not_Pin_Modified");
        }
    }
    private void Change_Pin_Status_Style() {
        fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                task.getPin() ? R.color.ex_orange :R.color.Neutral_gray_icon_note)));  ///Ternary Operator
    }

    /// Reminder Note
    private void Set_Reminder_Note() {
        //!!--have to be updated to work with an alternative option inside the original "Reminder_PopUpWindow"
        Reminder_PopUpWindow_Tasks reminder_PopUp = new Reminder_PopUpWindow_Tasks(this, -1);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        task.note = et_Task_main.getText().toString();
        task.title = et_Task_main.getText().toString();
        long _current_time = System.currentTimeMillis();
        task.date = _current_time;
        reminder_PopUp.show(layout_body_task, task);
    }
    private void Change_Reminder_Status_Style() {
        fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                task.getReminder() > 0 ? R.color.item_visualizer_icon_reminder_tint :R.color.Neutral_gray_icon_note)));  ///Ternary Operator
    }
    @Override
    public void OnValueSelected(int position, long alarm_Time) {
        task.setReminder(alarm_Time);
        Change_Reminder_Status_Style();
        if(task.task_id==0){//!!---Verificar si realmente es necesario, deberia ya tener un ID si fue guardado
            task.setTask_id(DB_T.Get_Last_RowId());
        }
    }
    @Override
    public void onPopupClosed(int salida, int position) { //  0 nada/normal, 1 setter, 2 cancelado
        tv_Date.setAlpha(1f);
        tv_Completion.setAlpha(1f);
        ///et_Note.setAlpha(1f);
        layout_dim.setVisibility(View.VISIBLE);

        if(salida == 1){//setter
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);
            return;
        }

        if(salida == 2){//cancel
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            return;
        }

        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);
    }

    private void Save_Task() {
        boolean save_Success;

        long _current_time = System.currentTimeMillis();

        String _main_task_description = et_Task_main.getText().toString();

        if (task.task_id == 0) {
            task.task_id = DB_T.Insert_Task_L(_current_time, _main_task_description , _main_task_description, task.pin, task.reminder, task.reminder_type, task.reminder_interval);
            save_Success = task.task_id > 0 && Save_Sub_Tasks();;
        } else {
            save_Success = DB_T.Modify_Task(task.task_id, _current_time, _current_time, _current_time,_main_task_description, _main_task_description, task.pin, task.reminder, task.reminder_type, task.reminder_interval)
                    && Save_Sub_Tasks();
        }

        if (save_Success) {
            Log.d("Task Visualizer", "Save Success: ");
            change_in_task = false;
            task.date = _current_time;
            //!!--Update info:
            ///tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        }else{
            Log.d("Task Visualizer", "Save NOT Success: ");
        }

    }

    private Boolean  Save_Sub_Tasks() {

        Log.d("Task Visualizer", "Save_Sub_Task: ");
        boolean save_sub_tasks_success = true;
        for(int i = task_subList.size() - 1; i >= 0; i --){
            if(!task_subList.get(i).note.isEmpty()){
                Task_Sub task_sub = task_subList.get(i);
                if (!DB_T.Modify_Sub_Task_Description(task_sub.task_sub_id, task_sub.note)) {
                    save_sub_tasks_success = false;
                    break;
                }
            }
        }
        return save_sub_tasks_success;
    }

    private void Delete_Task() {
        Verify_if_All_Current_Sub_Task_Are_Empty();
        Hard_Delete_All_Empty_Sub_Tasks();


        if (Save_Task_in_TrashCan()) {

            //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
            Return_To_Task_List(); //is a method with the finish() method inside, but is there to add animations later

            if (task.task_id != 0) {      //Delete Reminder if exist
                Reminder_Notification.Cancel_Reminder_Alarm(layout_body_task, task.task_id,1,task.reminder);
            }

        }
    }

    private void Verify_if_All_Current_Sub_Task_Are_Empty() {
        Log.d("Task Visualizer", "Verify_if_All_Sub_Task_Description_Are_Empty: ");

        for(int i = task_subList.size() - 1; i >= 0; i --){
            if(!task_subList.get(i).note.isEmpty()){
                return;
            }
        }
        if(DB_T.Modify_Has_Sub_Tasks_Status(task.task_id,false)){
            task.setHas_Sub_Tasks(false);
        }
    }
    private boolean All_Current_Sub_Task_Are_Empty_2() {
        Log.d("Task Visualizer", "All_Sub_Task_Description_Are_Empty_: ");

        for(int i = task_subList.size() - 1; i >= 0; i --){
            if(!task_subList.get(i).note.isEmpty()){
                return false;
            }
        }
        return true;
    }

    private void Hard_Delete_All_Empty_Sub_Tasks() {
        Log.d("Task Visualizer", "Delete all empty sub tasks: ");
        for(int i = task_subList.size() - 1; i >= 0; i --){
            if(task_subList.get(i).note.isEmpty()){
                Task_Sub task_sub = task_subList.get(i);
                if(DB_T.Verify_If_Sub_Task_Is_Empty(task_sub.task_sub_id)){
                    if (DB_T.Delete_Hard_Specific_Task_Sub(task_sub.task_sub_id)) {
                        task_subList.remove(i);
                        Log.d("Task Visualizer", "Position hard deleted: " + i);
                    }
                }
            }
        }
        if(task_subList.isEmpty()){
            if(DB_T.Modify_Has_Sub_Tasks_Status(task.task_id,false)){
                task.setHas_Sub_Tasks(false);
            }
        }
        now_is_something_written = Task_is_not_empty() || task.has_sub_tasks;
    }
    private void Delete_All_Blank_Sub_Tasks_The_Main_Will_not_be_Deleted() {
        Log.d("Task Visualizer", "Delete all empty sub tasks MAIN will not be deleted: ");
        if(Task_is_not_empty() || !All_Current_Sub_Task_Are_Empty_2()){
            for(int i = task_subList.size() - 1; i >= 0; i --){
                Log.d("Task Visualizer", "subtask note: " + task_subList.get(i).getContent());
                Log.d("Task Visualizer", "subtask note is empty: " + task_subList.get(i).note.isEmpty());
                if(task_subList.get(i).getContent().isEmpty()){
                    Task_Sub task_sub = task_subList.get(i);
                    if(DB_T.Delete_Hard_Specific_Task_Sub(task_sub.task_sub_id)){
                            Log.d("Task Visualizer", "Content hard deleted: " + task_sub.getContent());
                            task_subList.remove(i);
                            Log.d("Task Visualizer", "Position hard deleted: " + i);
                    }
                }
            }

        }else{
            now_is_something_written = false;
        }
        if(Task_is_not_empty() || !All_Current_Sub_Task_Are_Empty_2()){
            now_is_something_written = true;
            Log.d("Task Visualizer", "Set now is somethiing written to: " + now_is_something_written);
        }
        Log.d("Task Visualizer", "        task subList size:" + task_subList.size());
        if(task_subList.isEmpty()){
            if(DB_T.Modify_Has_Sub_Tasks_Status(task.task_id,false)){
                task.setHas_Sub_Tasks(false);
            }
        }
        Log.d("Task Visualizer", "    Delete all empty sub tasks MAIN will not be deleted: \n    now is something written: " + now_is_something_written);
        Log.d("Task Visualizer", "        Task is not emtpy " + Task_is_not_empty());
        Log.d("Task Visualizer", "        task hasSub tasks " + task.has_sub_tasks);
        //now_is_something_written = Task_is_not_empty() || task.has_sub_tasks;
        Log.d("Task Visualizer", "    Delete all empty sub tasks MAIN will not be deleted: \n    now is something written: " + now_is_something_written);
    }

    private Boolean Save_Task_in_TrashCan() {
        //!!--Save in trashcan what was saved in data base before all tasks were cleared:
        if (!now_is_something_written) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
            if (task.note != null || has_sub_tasks_in_database ) {//!!--aqui se debe corregir para que se verifiquen los sub task originales
                Log.d("Delete","1-");
                return  getTaskInTrashCan(task.date,task.title,task.note,20,"1-Insertado datos previous");
            } else {
                Log.d("Delete","2-");
                Toast.makeText(Task_Visualizer.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
                return true;
            }
        }

        String _description = et_Task_main.getText().toString();
        long _current_time = System.currentTimeMillis();
        if (!change_in_task) {   //if there_is_something save in database > Send to trashcan what was in the database before save
            Log.d("Delete","3-");
            return getTaskInTrashCan(task.date, _description, _description, 20,"3- Sin cambios, save proyectado en edit.T ");
        } else {
            Log.d("Delete","4-");
            return getTaskInTrashCan(_current_time, _description, _description, 20,"4- Cambios realizados, moving to trash ");
        }
    }
    private Boolean getTaskInTrashCan(long date, String title, String _note, int expire_days, String Delete_Case) {
        if ( task.task_id == 0 ) {
            Log.d("Delete","5-");
            Toast.makeText(Task_Visualizer.this, "5- Cambios realizados, directo a TrashCan ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
            change_in_task = false;
            task.task_id =  DB_T.Insert_Task_Directly_in_Trash(date,title,_note,task.pin,20,task.completed,task.has_sub_tasks); //!!--Check cual es la mejor opcion para este valor de expire days
            if(!task_subList.isEmpty()){
                Insert_Sub_Task_Directly_in_Trash();
            }
            return true;
        }
        Toast.makeText(Task_Visualizer.this, Delete_Case, Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
        DB_T.Send_Task_To_Trash(task.task_id, date, title, _note, task.pin,  expire_days,task.completed,task.has_sub_tasks);
        Log.d("Task Visualizer", "Main Task sent to trash: " + task.getTask_id());
        if(!task_subList.isEmpty()){
            Send_Sub_Task_To_Trash();
            return true;
        }else if(has_sub_tasks_in_database){
            Send_Sub_Task_To_Trash_With_Out_Modification();
            return true;
        }
        return true;
    }

    private void Insert_Sub_Task_Directly_in_Trash() {
        Log.d("Task Visualizer", "Insert sub tasks directly");
        for(int i = task_subList.size() - 1; i >= 0; i --){
            Task_Sub task_sub = task_subList.get(i);
            long _sub_task_id = DB_T.Insert_Sub_Task_Directly_in_Trash(task_sub.parent_id,task_sub.note,task_sub.completed,task_sub.task_sub_position);
            Log.d("Task Visualizer", "Sub Task sent to trash: " + _sub_task_id);
        }
    }
    private void Send_Sub_Task_To_Trash() {
        Log.d("Task Visualizer", "Insert sub tasks directly");
        for(int i = task_subList.size() - 1; i >= 0; i --){
            Task_Sub task_sub = task_subList.get(i);
            DB_T.Send_Sub_Task_To_Trash(task_sub.task_sub_id,task_sub.parent_id,task_sub.note, task_sub.completed, task_sub.task_sub_position);
            Log.d("Task Visualizer", "Sub Task sent to trash: " + task_sub.getTask_Sub_id());
        }
    }
    private void Send_Sub_Task_To_Trash_With_Out_Modification() {
        Log.d("Task Visualizer", "Modify sub tasks to mark as soft deleted");
        DB_T.Send_Previous_Sub_Task_To_Trash_With_Out_Modification(task.getTask_id());
    }

    private void Out_Of_Activity() {
        //!!--es necesario configurar para que se actualize o elimine dependiendo del caso, por el momento solo sale de la pantalla
        Log.d("Task Visualizer", "Out_Of_Activity: change_in_task: " + change_in_task);
        if(change_in_task){
            Log.d("Task Visualizer", "Out_Of_Activity: enter in delete blank sub tasks" + now_is_something_written);
            Delete_All_Blank_Sub_Tasks_The_Main_Will_not_be_Deleted();
        }
        Log.d("Task Visualizer", "Out_Of_Activity: now is something written" + now_is_something_written);
        if (!now_is_something_written) {
            Delete_Task();
        } else {
            if (tv_Date.getText().toString().isEmpty()) {
                tv_Date.setVisibility(View.GONE);
            }
            Return_To_Task_List();
        }

    }
    public void Return_To_Task_List() {
        View view = this.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in, R.anim.return_activity_slide_right_out);
    }

    @Override
    public void onItemClick(int position, View v) {

    }

    @Override
    public void onItemHold(int position, View v) {

    }

    @Override
    public void Mark_Sub_Task_As_Completed(int position) {
        //!!--Esto est'a funcionando como un auxiliar, se debe primero captar la modificacion y si es valida entonces se debe guardar en la base de datos, no al revez.
        Task_Sub task_sub = task_subList.get(position);
        task_sub.setCompleted(!task_sub.completed);
        if(DB_T.Modify_Sub_Task_Completed_Status(task_sub.task_sub_id, task_sub.completed)){
            task_subList.set(position,task_sub);
            adapter.notifyItemChanged(position);
            long _current_time = System.currentTimeMillis();
            if(DB_T.Modify_Main_Task_Modified_Date(task.task_id, _current_time)) {
                //!!--Verificar que tan viable seria incluir Step_Completed_Date en vez de modificar el date_modified

                task.date_modified = _current_time;
            }
        }
        Main_Task_Completed(received_task_id);
        Update_Completion_Ratio();
        Update_Date();

    }

    @Override
    public void Change_Sub_Task_Description(int position, String description) {
        Log.d("Task Visualizer", "Change description pos: " + position + "   description: "+description);

        Task_Sub task_sub = task_subList.get(position);
        task_sub.setNote(description);
        task_subList.set(position,task_sub);
        change_in_task = true;
    }

    private void Main_Task_Completed(long parent_id) {
        int result = DB_T.Verify_If_All_Sub_Task_Completed(parent_id);
        Log.d("Task Visualizer", "Verify if all sub task are completed: " + result);

        if(result > 0){
            if(!task.completed) Change_Complete_Main_Task_Status();
        }else{
            if(task.completed) Change_Complete_Main_Task_Status();
        }

    }
    private void Change_Complete_Main_Task_Status() {
        task.setCompleted(!task.completed);
        long _current_time = System.currentTimeMillis();
        if(DB_T.Modify_Main_Task_Completed_Status(task.task_id, task.completed, _current_time)) {
            //!!--Verificar los colores correctos
            fl_Main_Task_Complete.setBackgroundTintList(ColorStateList.valueOf(getColor(task.completed ? R.color.ex_green : R.color.gray_light_3)));///Ternary Operator
            task.date_completed = _current_time;
            Update_Date();
        }
    }

    private void Change_Sub_task_Completed_Status() {

        //!!--Seria bueno agregar una animacion a forma de notificacion para que el usuario sepa que los subtask estan siendo cambiados de estado si no coinciden con el nuevo estado de main

        for(int i = task_subList.size() - 1; i >= 0; i --){
            if(task_subList.get(i).completed!= task.completed){
                Task_Sub task_sub = task_subList.get(i);
                task_sub.setCompleted(!task_sub.completed);
                if(DB_T.Modify_Sub_Task_Completed_Status(task_sub.task_sub_id, task_sub.completed)){
                    task_subList.set(i,task_sub);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }


    @Override
    public void Remove_Item(int position) {
        //!!--Esto est'a funcionando como un auxiliar, se debe primero captar la modificacion y si es valida entonces se debe guardar en la base de datos, no al revez.
        Task_Sub task_sub = task_subList.get(position);
        task_sub.setCompleted(!task_sub.completed);
        if(DB_T.Delete_Hard_Specific_Task_Sub(task_sub.task_sub_id)){
            task_subList.remove(position);
            adapter.notifyItemRemoved(position);
        }
        if(task_subList.isEmpty()){
            if(DB_T.Modify_Has_Sub_Tasks_Status(task.task_id,false)){
                task.setHas_Sub_Tasks(false);
            }
        }else{
            Main_Task_Completed(received_task_id);
        }
        Update_Completion_Ratio();
    }
}