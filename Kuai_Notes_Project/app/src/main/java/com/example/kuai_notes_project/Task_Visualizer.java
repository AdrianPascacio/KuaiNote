package com.example.kuai_notes_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Canvas;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

//488 01apr2026, 1207 v9.0B, 2371 v9.OB 03jul2026, 1221 v9.0A 16jul2026
public class Task_Visualizer extends AppCompatActivity implements Recycler_Tasks_Sub_In_Visualizer_Interface, Reminder_PopUpWindow_Tasks.OnValueSelectedListener, Reminder_PopUpWindow_Tasks.PopupDismissListener{
    private int task_modification_result = -1; /// 0 Element Modification, 1 New Element, 2 Element Deleted
    private int order_type = 0;
    private int new_position_a =  0, new_position_b =  0;
    private long sub_taskID_a =   0, sub_taskID_b =   0;
    private DB_Tasks DB_T;
    private TextView tv_Date2, tv_Completion2;
    private EditText et_Task_main;
    private Task_Main task = new Task_Main();

    private RecyclerView recyclerView;
    Adapter_Recycler_Tasks_Sub_In_Visualizer adapter;
    private ArrayList<Task_Sub> task_subList;
    private ArrayList<Boolean> selected_list;
    ArrayList<Integer> selected_positions_list;

    private long received_task_id = 0;
    private boolean change_in_task = false,  now_is_something_written = false;
    private boolean is_a_new_task = false;
    private boolean has_sub_tasks_in_database = false;
    private boolean is_Completion_Ration_Showed = false;
    private boolean selection_mode = false, aux_selection_state = false;
    private int selection_count = 0;
    private int true_from_position = -1, true_to_position = -1;
    private FrameLayout fl_Change_Pin_Status, fl_Change_Reminder_Status, fl_Back, fl_Delete, fl_Insert_Sub_Task, fl_Insert_Sub_Task_Initial, fl_Copy_To_Clipboard, fl_Set_Order;
    private FrameLayout fl_Change_Pin_Status_Ghost, fl_Change_Reminder_Status_Ghost, fl_Back_Ghost, fl_Delete_Ghost;
    private FrameLayout fl_Main_Task_Complete;
    private Date_of_Note DoN;
    private View layout_date_and_info, layout_body_task, layout_dim;
    private Animation AnimationPin, AnimationReminder, AnimationDate, AnimationDateInvert, AnimationInfo, AnimationInfoInvert, AnimationPinAppear, AnimationPinFade;
    private Animation AnimationSetOrder_Change_Sort, AnimationCopy_Confirmed, AnimationSubTask_Inserted;
    private Animation  AnimationTitleAppear, Animation_FloatingButton_Appear, Animation_FloatingButton_Disappear;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel;

    Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(this,-1);

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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {//0 para eliminar los swipe horizontales utilizados para borrar.
            int lesser_position = 0, greater_position = 0;
            int lesser_position_assigned = 0, greater_position_assigned = 0;
            int from_aux_onMove = 0;
            int original_from_position = 0, original_to_position = 0;
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from_Position = viewHolder.getAdapterPosition();
                int to_Position = target.getAdapterPosition();
                if(true_from_position == -1) true_from_position = from_Position;
                if(true_to_position == -1) true_to_position = to_Position;
                from_aux_onMove = from_Position;

                original_from_position = from_Position;
                original_to_position = to_Position;
                lesser_position = to_Position < true_from_position ? to_Position : true_from_position;
                greater_position = to_Position > true_from_position ? to_Position : true_from_position;
                Task_Sub _task_sub = task_subList.get(from_Position);

                task_subList.remove(from_Position);
                task_subList.add(to_Position,_task_sub);
                new_position_a = task_subList.get(from_Position).getTask_sub_position();
                new_position_b = task_subList.get(to_Position).getTask_sub_position();

                lesser_position_assigned = new_position_a < new_position_b ? new_position_a : new_position_b;
                greater_position_assigned = new_position_a > new_position_b ? new_position_a : new_position_b;
                sub_taskID_a = task_subList.get(from_Position).getTask_Sub_id();
                sub_taskID_b = task_subList.get(to_Position).getTask_Sub_id();
                adapter.notifyItemMoved(from_Position,to_Position);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                super.onSelectedChanged(viewHolder, direction);
                if(direction == ItemTouchHelper.RIGHT){
                    viewHolder.itemView.setAlpha(0.5f);
                    if(!Objects.equals(task_subList.get(position).note, "")) task_modification_result = 0;
                    Remove_Item(position);
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    float width = (float) viewHolder.itemView.getWidth();
                    float alpha = 1.0f - Math.abs (dX) / width;

                    viewHolder.itemView.setAlpha(alpha);

                    viewHolder.itemView.setTranslationX(dX );

                }else{
                    super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
                }
            }
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
                ///--Modificacion miestra se agarra el item
                super.onSelectedChanged(viewHolder, actionState);
                if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                    viewHolder.itemView.setAlpha(0.5f);
                }
            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
                ///--Modificacion al soltar el item
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f);

                if(original_from_position == original_to_position) return;
                if(true_from_position == original_to_position) return;

                long start_nano_time = System.nanoTime();

                Save_Sub_Tasks_New_Positions_6(lesser_position,greater_position,original_from_position,original_to_position,lesser_position_assigned,greater_position_assigned,sub_taskID_a, sub_taskID_b,new_position_a,new_position_b);

                long end_nano_time = System.nanoTime();
                long bench_time = end_nano_time - start_nano_time;
                Log.d("TasksList","   Time Consumed in pin:  "+ bench_time/1000);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Clear_Lists();
        Update_Recycler_View();
    }
    private void Save_Sub_Tasks_New_Positions_6(int lesser_position,int greater_position, int original_from_position, int original_to_position,int lesser_position_assigned,int greater_position_assigned,long sub_taskID_a, long sub_taskID_b, int position_a, int position_b) {
        true_from_position = -1;

        if(order_type == 2 || !Is_Valid_To_Sort_Completed_Sub_Tasks()){ /// IF ALL ELEMENTS are completed or uncompleted:
            for(int i = lesser_position; i <= task_subList.size()-1; i++){
                sub_taskID_a = task_subList.get(i).getTask_Sub_id();
                DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, i + 1);
                task_subList.get(i).setTask_sub_position(i+1);
                if(task_subList.get(i).task_sub_position == greater_position + 1) break;
            }
        }else if(order_type < 2){ /// Include Order_Type 1 and Order_Type 2:
            int max_changes = greater_position_assigned - lesser_position_assigned;
            int lower_start = lesser_position < lesser_position_assigned -1 ? lesser_position:lesser_position_assigned -1;
            int higher_end = greater_position < greater_position_assigned -1 ? greater_position:greater_position_assigned -1;
            int search_start = order_type == 1 ? 0:lower_start ;
            int search_end = order_type == 0 ? task_subList.size() - 1: higher_end ;
            int increaser = 1;

            if(order_type == 1){
                search_start = search_start ^ search_end;
                search_end = search_start ^ search_end;
                search_start = search_start ^ search_end;
                increaser = -1;
            }

            if(original_from_position < original_to_position) {/// Movimiento de Arriba hacia abajo
                if(position_a > position_b){/// Movimiento de Arriba hacia abajo
                    for (int i = search_start;(order_type == 0) ? (i <= search_end) : (i >= search_end); i+= increaser) {
                        Task_Sub task_sub = task_subList.get(i);

                        sub_taskID_a = task_subList.get(i).getTask_Sub_id();
                        if (task_sub.getTask_sub_position() <= position_a && task_sub.getTask_sub_position() > position_b) {
                            task_sub.setTask_sub_position(task_sub.task_sub_position - 1);
                            DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, task_sub.task_sub_position);
                            max_changes --;
                            if(max_changes == 0) break;
                        }
                    }
                    task_subList.get(original_to_position).setTask_sub_position(position_a);
                    DB_T.Modify_Sub_Task_New_Position(sub_taskID_b, position_a);
                }else {/// Movimiento de Abajo hacia arriba
                    for (int i = search_start;(order_type == 0) ? (i <= search_end) : (i >= search_end); i+= increaser) {

                        Task_Sub task_sub = task_subList.get(i);
                        sub_taskID_a = task_subList.get(i).getTask_Sub_id();
                        if(task_sub.getTask_sub_position() > position_a && task_sub.getTask_sub_position() < position_b ){
                            task_sub.setTask_sub_position(task_sub.task_sub_position + 1) ;
                            DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, task_sub.task_sub_position );
                            max_changes --;
                            if(max_changes == 0) break;
                        }
                    }
                    task_subList.get(original_to_position).setTask_sub_position(position_a + 1);
                    DB_T.Modify_Sub_Task_New_Position(sub_taskID_b, position_a + 1);
                }
            }else{/// Movimiento de Abajo hacia arriba ↑
                if(position_a < position_b){
                        for(int i = search_start;(order_type == 0) ? (i <= search_end) : (i >= search_end); i+= increaser){
                            Task_Sub task_sub = task_subList.get(i);
                            sub_taskID_a = task_subList.get(i).getTask_Sub_id();
                            if(task_sub.getTask_sub_position() >= position_a && task_sub.getTask_sub_position() < position_b ){
                                task_sub.setTask_sub_position(task_sub.task_sub_position + 1) ;
                                DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, task_sub.task_sub_position );
                                max_changes --;
                                if(max_changes == 0) break;
                            }
                        }
                    task_subList.get(original_to_position).setTask_sub_position(position_a);
                    DB_T.Modify_Sub_Task_New_Position(sub_taskID_b, position_a);
                }else{/// ↑*↓***Posicion Real --  Arriba hacia abajo ↓
                    for (int i = search_start;(order_type == 0) ? (i <= search_end)  : (i >= search_end); i+= increaser) {
                        Task_Sub task_sub = task_subList.get(i);
                        sub_taskID_a = task_subList.get(i).getTask_Sub_id();
                        if (task_sub.getTask_sub_position() < position_a && task_sub.getTask_sub_position() > position_b) {
                            task_sub.setTask_sub_position(task_sub.task_sub_position - 1);
                            DB_T.Modify_Sub_Task_New_Position(sub_taskID_a, task_sub.task_sub_position);
                            max_changes --;
                            if(max_changes == 0) break;
                        }
                    }
                    task_subList.get(original_to_position).setTask_sub_position(position_a - 1);
                    DB_T.Modify_Sub_Task_New_Position(sub_taskID_b, position_a - 1);
                }
            }
        }
    }

    private void Update_Recycler_View(){
        try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task.task_id)) {
            if(cursor_Tasks_Sub.getCount()==0){
                Log.d("Read cursor_Tasks", "Cursor_Tasks :  No Entry Exist");
            }else{
                int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                while (cursor_Tasks_Sub.moveToNext()){
                    Log.d("Read cursor_Tasks", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                            cursor_Tasks_Sub.getLong(parent_indx_sub),
                            cursor_Tasks_Sub.getString(note_indx_sub),
                            cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                            cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                    task_subList.add(task_sub);
                    selected_list.add(false);
                }
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void Clear_Lists(){
        if(task_subList.isEmpty())  return;
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
        selected_positions_list = new ArrayList<>();
        DB_T = new DB_Tasks(this);

        tv_Completion2 = findViewById(R.id.Task_Completion2);
        tv_Date2 = findViewById(R.id.Task_Date2);

        et_Task_main = findViewById(R.id.Task_Main_Text);

        fl_Change_Pin_Status = findViewById(R.id.FrameLayout_Change_Pin_Status);
        fl_Change_Reminder_Status = findViewById(R.id.FrameLayout_Change_Reminder_Status);
        fl_Back = findViewById(R.id.fl_Back);
        fl_Delete = findViewById(R.id.fl_Delete);

        fl_Change_Pin_Status_Ghost = findViewById(R.id.FrameLayout_Change_Pin_Status_Ghost);
        fl_Change_Reminder_Status_Ghost = findViewById(R.id.FrameLayout_Change_Reminder_Status_Ghost);
        fl_Back_Ghost = findViewById(R.id.fl_Back_Ghost);
        fl_Delete_Ghost = findViewById(R.id.fl_Delete_Ghost);
        fl_Insert_Sub_Task_Initial = findViewById(R.id.FrameLayout_Insert_Sub_Task_Initial);
        fl_Insert_Sub_Task = findViewById(R.id.FrameLayout_Insert_Sub_Task);
        fl_Copy_To_Clipboard = findViewById(R.id.FrameLayout_Copy_To_Clipboard);
        fl_Set_Order = findViewById(R.id.FrameLayout_Order);
        fl_Main_Task_Complete = findViewById(R.id.FrameLayout_Change_Complete_Task_Main);

        DoN = new Date_of_Note();

        layout_date_and_info = findViewById(R.id.Layout_date_and_info);
        layout_body_task = findViewById(R.id.Layout_Body_Task);

        AnimationCopy_Confirmed = AnimationUtils.loadAnimation(this, R.anim.copy_confirmed);
        AnimationSetOrder_Change_Sort = AnimationUtils.loadAnimation(this, R.anim.set_order_change_sort);
        AnimationSubTask_Inserted = AnimationUtils.loadAnimation(this, R.anim.sub_task_inserted);
        AnimationPin = AnimationUtils.loadAnimation(this, R.anim.pin_visualizer_change_status);
        AnimationReminder = AnimationUtils.loadAnimation(this, R.anim.reminder_visualizer_change_status);
        AnimationDate = AnimationUtils.loadAnimation(this, R.anim.date_visualizer);
        AnimationDateInvert = AnimationUtils.loadAnimation(this, R.anim.date_visualizer_invert);
        AnimationInfo = AnimationUtils.loadAnimation(this, R.anim.info_visualizer);
        AnimationInfoInvert = AnimationUtils.loadAnimation(this, R.anim.info_visualizer_invert);
        AnimationPinAppear = AnimationUtils.loadAnimation(this, R.anim.appear_visualizer);
        AnimationPinFade = AnimationUtils.loadAnimation(this, R.anim.fade_visualizer);
        AnimationTitleAppear = AnimationUtils.loadAnimation(this, R.anim.title_appear_mainvisualizer);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        Animation_FloatingButton_Appear = AnimationUtils.loadAnimation(this, R.anim.floating_button_appear);
        Animation_FloatingButton_Disappear = AnimationUtils.loadAnimation(this, R.anim.floating_buttton_disappear);

        received_task_id = getIntent().getLongExtra("send_task_id", 0);

        layout_dim = findViewById(R.id.layout_dim_noteVisualizer);

        if (received_task_id != 0) {
            Initialize_Received_Note(received_task_id);
            Set_Written_Note_Style();
            fl_Insert_Sub_Task_Initial.setVisibility(View.GONE);
            fl_Insert_Sub_Task.setVisibility(View.VISIBLE);
            fl_Set_Order.setVisibility(Is_Valid_To_Sort_Completed_Sub_Tasks()? View.VISIBLE : View.GONE);
        } else {
            is_a_new_task = true;
            Set_Blank_Note_Style();
            fl_Insert_Sub_Task_Initial.setVisibility(View.VISIBLE);
            fl_Insert_Sub_Task.setVisibility(View.GONE);
            tv_Completion2.setVisibility(View.GONE);
            tv_Date2.setVisibility(View.GONE);
            fl_Set_Order.setVisibility(View.GONE);

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
                task_modification_result = 0;
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
                task_modification_result = 0;

                if(task.completed) {
                    Change_Complete_Main_Task_Status();
                }
                Update_Completion_Ratio();
                fl_Set_Order.setVisibility(Is_Valid_To_Sort_Completed_Sub_Tasks()? View.VISIBLE : View.GONE);
                fl_Insert_Sub_Task.startAnimation(AnimationSubTask_Inserted);
            }
        });
        fl_Insert_Sub_Task_Initial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fl_Insert_Sub_Task_Initial.setVisibility(View.GONE);
                fl_Insert_Sub_Task.setVisibility(View.VISIBLE);
                Insert_Sub_Task();
                change_in_task = true;
                task_modification_result = 0;

                if(task.completed) {
                    Change_Complete_Main_Task_Status();
                }
                Update_Completion_Ratio();
                fl_Insert_Sub_Task_Initial.animate().alpha(0).setDuration(500).withEndAction(new Runnable(){
                    @Override
                    public void run(){
                        fl_Insert_Sub_Task_Initial.setVisibility(View.GONE);
                        fl_Insert_Sub_Task_Initial.setFocusable(false);
                        fl_Insert_Sub_Task_Initial.setClickable(false);
                    }
                });
            }
        });
        fl_Copy_To_Clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Copy_Tasks_To_Clipboard();
                fl_Copy_To_Clipboard.startAnimation(AnimationCopy_Confirmed);
            }
        });
        fl_Set_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set_Tasks_Order();
                if(order_type == 0){
                    fl_Set_Order.setBackground(ContextCompat.getDrawable(fl_Set_Order.getContext(),R.drawable.sort_icon_uncomplete_first_2));
                    fl_Set_Order.startAnimation(AnimationSetOrder_Change_Sort);

                }else if(order_type == 1){
                    fl_Set_Order.setBackground(ContextCompat.getDrawable(fl_Set_Order.getContext(),R.drawable.sort_icon_test_12));
                    fl_Set_Order.startAnimation(AnimationSetOrder_Change_Sort);

                }else if(order_type == 2){
                    fl_Set_Order.setBackground(ContextCompat.getDrawable(fl_Set_Order.getContext(),R.drawable.sort_icon_original_sort_2));
                    fl_Set_Order.startAnimation(AnimationSetOrder_Change_Sort);

                }
                Debug_sub_task_list_position();
            }
        });
        fl_Main_Task_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(received_task_id == 0 && (!et_Task_main.getText().toString().isEmpty() || !All_Current_Sub_Task_Are_Empty_2())){
                    Save_Task();
                    received_task_id = task.task_id;
                }
                Change_Complete_Main_Task_Status();
                if(task.has_sub_tasks) {
                    Change_Sub_task_Completed_Status();
                }
                Update_Completion_Ratio();
                Update_Date();
                fl_Set_Order.setVisibility(Is_Valid_To_Sort_Completed_Sub_Tasks()? View.VISIBLE : View.GONE);
                Sort_Sub_Task_According_Original_Order();
                Debug_sub_task_list_position();
                task_modification_result= 0;
            }
        });
        fl_Change_Pin_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Task_is_not_empty()|| !All_Current_Sub_Task_Are_Empty_2()) {
                    Pin_Task();
                    fl_Change_Pin_Status.startAnimation(AnimationPin);
                    task_modification_result= 0;
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

                    tv_Date2.setAlpha(0.9f);
                    tv_Completion2.setAlpha(0.9f);
                    et_Task_main.setAlpha(0.8f);
                    layout_dim.setVisibility(View.VISIBLE);
                    layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
                    layout_dim.startAnimation(AnimationLayoutDimAppear);

                    Set_Reminder_Note();
                    fl_Change_Reminder_Status.startAnimation(AnimationReminder);
                    task_modification_result= 0;
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

    private void Select_Item(int position) {}
    private void Restart_Selection() {}

    private void Set_Tasks_Order() {
        //--Order states: || 0- DEFAULT = UNCOMPLETED FIRST || 1- COMPLETED_FIRST || 2- CUSTOM
        if (!Is_Valid_To_Sort_Completed_Sub_Tasks()) return;
        int sub_Task_size = task_subList.size();
        if (order_type == 0) { /// --   0→Default (Uncomplete first) to → Complete first
            for (int i = sub_Task_size - 1; i >= 0; i--) {
                if (task_subList.get(i).getCompleted() == false) {
                    Log.d("Task Visualizer", " Default Uncomplete first: Diferente: " + "   task:" + task_subList.get(i).getContent());
                    Task_Sub _task_sub = task_subList.get(i);
                    task_subList.remove(i);
                    task_subList.add(sub_Task_size - 1, _task_sub);
                    adapter.notifyItemMoved(i, sub_Task_size - 1);
                }
            }
            order_type = 1;   //-- Complete first
        } else if (order_type == 1) {//-- Complete first to →  Custom
            Log.d("Task Visualizer", "New Order:  Custom: ");
            Move_Sub_Tasks_in_Original_Custom_Order(sub_Task_size);
            order_type = 2;   //-- Custom
        } else if (order_type == 2) {//--   Custom to → Default (Uncomplete first)
            for (int i = sub_Task_size - 1; i >= 0 ; i--) {
                if (task_subList.get(i).getCompleted() == true) {
                    Log.d("Task Visualizer", " Default Uncomplete first: Diferente: " + "   task:" + task_subList.get(i).getContent());
                    Task_Sub _task_sub = task_subList.get(i);
                    task_subList.remove(i);
                    task_subList.add(sub_Task_size - 1, _task_sub);
                    adapter.notifyItemMoved(i, sub_Task_size - 1);
                }
            }
            order_type = 0;   //-- Default
        }
    }
    private void Sort_Sub_Task_According_Original_Order() {
        if(order_type == 2) return;
        int sub_Task_size = task_subList.size();
        if (sub_Task_size < 2) return;
        Move_Sub_Tasks_in_Original_Custom_Order(sub_Task_size);
    }
    private void Move_Sub_Tasks_in_Original_Custom_Order(int sub_Task_size) {
        for (int i = 0; i <= sub_Task_size - 1; i++) {
            if (task_subList.get(i).getTask_sub_position() != (i + 1)) {
                Log.d("Task Visualizer", " Custom: Diferente: " + "   task:" + task_subList.get(i).getContent() + "  tiene pos:" + task_subList.get(i).getContent());
                for (int j = i + 1; j <= sub_Task_size - 1; j++) {
                    if (task_subList.get(j).getTask_sub_position() == (i + 1)) {
                        Log.d("Task Visualizer", "   Custom: " + task_subList.get(j).getContent());
                        Task_Sub _task_sub = task_subList.get(j);
                        task_subList.remove(j);
                        task_subList.add(i, _task_sub);
                        adapter.notifyItemMoved(j, i);
                        break;
                    }
                }
            }
        }
    }
    private void Set_Sub_Tasks_Order_When_Complete(int position) {
        Task_Sub _task_sub = task_subList.get(position);
        int sub_Task_size = task_subList.size();
        if (sub_Task_size < 2)  return;
        if (order_type == 2)    return;
        if ((order_type == 1) == _task_sub.completed) { //--   Complete first
            for (int i = 0; i <= sub_Task_size - 1; i++) { /// Move to the superior group that is the opposite (completed / uncompleted)
                if (task_subList.get(i).getCompleted() != _task_sub.completed || task_subList.get(i).getTask_sub_position() >= _task_sub.getTask_sub_position()) {/// if complete value is different let it pass, else , verify if the current task have a GreaterOrEqual positon
                    Update_Sub_Tasks_Order_When_Complete(position, i, _task_sub);
                    break;
                }
            }
        } else {//--   Default (Uncomplete first) ///--Esta solucion asume el order_type == 0
            for (int i = sub_Task_size -1; i >= 0; i--) {/// Move to the inferior group that is the opposite (completed / uncompleted)
                if (task_subList.get(i).getCompleted() != _task_sub.completed || task_subList.get(i).getTask_sub_position() <= _task_sub.getTask_sub_position()) {/// if complete value is different let it pass, else , verify if the current task have a LesserOrEqual positon
                    Update_Sub_Tasks_Order_When_Complete(position, i, _task_sub);
                    break;
                }
            }
        }
    }
    private void Update_Sub_Tasks_Order_When_Complete(int from_position, int to_position, Task_Sub _task_sub){
        task_subList.remove(from_position);
        task_subList.add(to_position, _task_sub);
        adapter.notifyItemMoved(from_position, to_position);
    }

    private void Copy_Tasks_To_Clipboard() {
        StringBuilder clip_text = new StringBuilder("");
        if(!now_is_something_written) return;
        boolean Tasks_Have_Title = !et_Task_main.getText().toString().isEmpty();
        if(Tasks_Have_Title){
            clip_text.append(et_Task_main.getText().toString() );
        }
        if(!task_subList.isEmpty()){
            if(Tasks_Have_Title){
                for(int i = 0 ; i <= task_subList.size() - 1; i++){
                    Append_Sub_Task_To_ClipBoard(task_subList.get(i), clip_text, "\n   ✓", "\n   ·");
                }
            }else{
                Append_Sub_Task_To_ClipBoard(task_subList.get(0), clip_text, "   ✓", "   ·");
                for(int i = 1 ; i <= task_subList.size() - 1; i++){
                    Append_Sub_Task_To_ClipBoard(task_subList.get(i), clip_text, "\n  ✓", "\n  ·");
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
    private static void Append_Sub_Task_To_ClipBoard(Task_Sub _task_sub, StringBuilder clip_text, String x, String x1) {
        if (!_task_sub.note.toString().trim().isEmpty()) {
            clip_text.append( ( _task_sub.completed ?  x  :  x1 )  + _task_sub.note);
        }
    }

    private void Insert_Sub_Task() {
        if(received_task_id == 0){
            Save_Task();
            received_task_id = task.task_id;
        }
        int _new_sub_task_position = DB_T.Verify_Top_Sub_Task_Position(received_task_id) + 1;
        Log.d("Task_Visualizer", "--------------new sub task position: " +_new_sub_task_position);
        long task_sub_new_id = DB_T.Insert_Task_Sub_L(received_task_id,"",false,_new_sub_task_position);
        if(task.has_sub_tasks == false){
            if(DB_T.Modify_Has_Sub_Tasks_Status(task.task_id,true)){
                task.setHas_Sub_Tasks(true);
            }
        }
        if (task_sub_new_id < 0) return;
        Task_Sub task_sub = new Task_Sub(task_sub_new_id,
                received_task_id,
                "",
                false,
                _new_sub_task_position);
        selected_list.add(false);
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

    private void Initialize_Received_Note(long received_task_id) {
        task = DB_T.getASpecificTask(received_task_id);
        Update_Visualization_Of_Main_Task_Complete_Check();

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
                        Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                        Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                                cursor_Tasks_Sub.getLong(parent_indx_sub),
                                cursor_Tasks_Sub.getString(note_indx_sub),
                                cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                                cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                        task_subList.add(task_sub);
                    }
                    has_sub_tasks_in_database = true;
                }
            }
        }

        now_is_something_written = true;
        et_Task_main.setText(task.note);
        Update_Completion_Ratio();
        Update_Date();

        Change_Pin_Status_Style();
        Change_Reminder_Status_Style();
    }
    private void Update_Visualization_Of_Main_Task_Complete_Check() {
        if (task.completed) {
            fl_Main_Task_Complete.setBackground(ContextCompat.getDrawable(this,R.drawable.icon_completed_task_test_11));
            fl_Main_Task_Complete.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.task_completed_color)));
        } else {
            fl_Main_Task_Complete.setBackground(ContextCompat.getDrawable(this,R.drawable.icon_complete_task_test_4));
            fl_Main_Task_Complete.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.task_uncompleted_color)));
        }
    }
    private void Update_Date() {
        tv_Date2.setVisibility(received_task_id > 0 ? View.VISIBLE : View.GONE);///Ternary Operator
        if(task.completed){///--Date Completed
            tv_Date2.setText("Completed:2 " +  DoN.Set_Date_of_Note_In_Visualizer(task.date_completed));
        }else{
            if(task.date_created == task.date_modified){///--Date created
                tv_Date2.setText("Created:2 " + DoN.Set_Date_of_Note_In_Visualizer(task.date_created));
            }else{///--Date modified
                tv_Date2.setText("Modified:2 "+DoN.Set_Date_of_Note_In_Visualizer(task.date_modified));
            }
        }
    }

    private void Update_Completion_Ratio() {
        if(task.has_sub_tasks ^ is_Completion_Ration_Showed){ /// XOR Operator (XOR == Return 1 if both bits are different)
            if(!is_Completion_Ration_Showed){
                tv_Completion2.setVisibility(View.VISIBLE);
                tv_Completion2.setAlpha(0);
                tv_Completion2.animate().alpha(1).setDuration(300);
                tv_Date2.animate().translationY(55).setDuration(500);
                fl_Copy_To_Clipboard.animate().translationY(0).setDuration(500);
                is_Completion_Ration_Showed = true;
            }else{
                tv_Completion2.setVisibility(View.GONE);
                tv_Date2.animate().translationY(0).setDuration(500);
                fl_Copy_To_Clipboard.animate().translationY(-55).setDuration(500);
                is_Completion_Ration_Showed = false;
            }
        }
        if(is_Completion_Ration_Showed){
            int sub_Task_size = task_subList.size();
            int completion_size = getCompletionSize();
            tv_Completion2.setText(completion_size + "/" + sub_Task_size);
        }
    }
    private boolean Is_Valid_To_Sort_Completed_Sub_Tasks(){
        int sub_Task_size = task_subList.size();
        int completion_size = getCompletionSize();
        return completion_size > 0 && completion_size != sub_Task_size && sub_Task_size >= 2;
    }
    private int getCompletionSize() {
        int completion_size = 0;
        for(int i = 0; i <= task_subList.size() -1 ; i++){
            if(task_subList.get(i).getCompleted()){
                completion_size ++;
            }
        }
        return completion_size;
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
        fl_Insert_Sub_Task_Initial.setScaleX(0.9f);
        fl_Insert_Sub_Task_Initial.setScaleY(0.9f);
        fl_Delete.setScaleY(0.9f);
        fl_Change_Reminder_Status.setAlpha(0.4f);
        fl_Change_Pin_Status.setAlpha(0.4f);
        fl_Delete.setAlpha(0.4f);
        fl_Insert_Sub_Task_Initial.setAlpha(0.4f);

        fl_Main_Task_Complete.setBackground(ContextCompat.getDrawable(this,R.drawable.icon_complete_task_test_4));
    }

    private void Verify_if_exist_something() {
        if (Task_is_not_empty() != now_is_something_written) {//    si el estado de la nota ha cambiado:
            now_is_something_written = Task_is_not_empty();
            Update_Task_Status(now_is_something_written);
        }
    }

    private boolean Task_is_not_empty() {
        return !et_Task_main.getText().toString().isEmpty();
    }

    private void Update_Task_Status(boolean current_status) {
        if(current_status) {
            fl_Change_Pin_Status.setAlpha(1f);
            fl_Change_Reminder_Status.setAlpha(1f);
            fl_Delete.setAlpha(1f);
            if(fl_Insert_Sub_Task.getVisibility()== View.VISIBLE){
                fl_Insert_Sub_Task.setAlpha(1f);
                fl_Insert_Sub_Task.startAnimation(AnimationPinAppear);
            }else{
                fl_Insert_Sub_Task_Initial.setAlpha(1f);
                fl_Insert_Sub_Task_Initial.startAnimation(AnimationPinAppear);
            }

            fl_Change_Pin_Status.startAnimation(AnimationPinAppear);
            fl_Change_Reminder_Status.startAnimation(AnimationPinAppear);
            fl_Delete.startAnimation(AnimationPinAppear);
        }else{
            if(fl_Insert_Sub_Task.getVisibility()== View.VISIBLE){
                fl_Insert_Sub_Task.startAnimation(AnimationPinFade);
            }else{
                fl_Insert_Sub_Task_Initial.startAnimation(AnimationPinFade);
            }
            fl_Change_Pin_Status.startAnimation(AnimationPinFade);
            fl_Change_Reminder_Status.startAnimation(AnimationPinFade);

            if(task.task_id == 0){
                fl_Delete.startAnimation(AnimationPinFade);
            }
        }
    }


    /// Pin Task
    private void Pin_Task() {
        task.setPin(!task.getPin());

        if(task.task_id == 0){
            Change_Pin_Status_Style();
            return;
        }

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
    }
    @Override
    public void onPopupClosed(int salida, int position) { //  0 nada/normal, 1 setter, 2 cancelado
        tv_Date2.setAlpha(1f);
        tv_Completion2.setAlpha(1f);
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

    @Override
    public void onItemClick(int position, View v) {}
    @Override
    public void onItemHold(int position, View v) {}
    @Override
    public void onLongPress(int position) {}
    @Override
    public void onDoubleTap(int position) {}

    @Override
    public void Change_Sub_Task_Description(int position, String description) {
        Log.d("Task Visualizer", "Change description pos: " + position + "   description: "+description);
        task_subList.get(position).setNote(description);
        change_in_task = true;
        task_modification_result = 0;
    }


    @Override
    public void Mark_Sub_Task_As_Completed(int position) {
        Task_Sub task_sub = task_subList.get(position);
        task_sub.setCompleted(!task_sub.completed);

        if (!DB_T.Modify_Sub_Task_Completed_Status(task_sub.task_sub_id, task_sub.completed))   return;
        adapter.notifyItemChanged(position);
        long _current_time = System.currentTimeMillis();
        if (!DB_T.Modify_Main_Task_Modified_Date(task.task_id, _current_time))  return;
        task.date_modified = _current_time;

        Main_Task_Completed(received_task_id);
        Update_Completion_Ratio();
        Update_Date();
        fl_Set_Order.setVisibility(Is_Valid_To_Sort_Completed_Sub_Tasks()? View.VISIBLE : View.GONE);

        Set_Sub_Tasks_Order_When_Complete(position);
        Debug_sub_task_list_position();
        change_in_task = true;

        task_modification_result= 0;
    }
    private void Debug_sub_task_list_position() {
        for(int i = 0; i <= task_subList.size()-1; i++){
            Task_Sub task_sub = task_subList.get(i);
            Log.d("Task Visualizer", "        ••: " + task_sub.note + "    -pos: "+task_sub.getTask_sub_position());
        }
    }

    private void Main_Task_Completed(long parent_id) {
        int result = DB_T.Verify_If_All_Sub_Task_Completed(parent_id);
        Log.d("Task Visualizer", "Verify if all sub task are completed: " + result);
        if((result > 0) != task.completed)Change_Complete_Main_Task_Status();
    }

    private void Change_Complete_Main_Task_Status() {
        task.setCompleted(!task.completed);
        long _current_time = System.currentTimeMillis();
        if(DB_T.Modify_Main_Task_Completed_Status(task.task_id, task.completed, _current_time)) {
            Update_Visualization_Of_Main_Task_Complete_Check();
            task.date_completed = _current_time;
            Update_Date();
        }
    }

    private void Change_Sub_task_Completed_Status() {
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
            task_modification_result= 0;
        }else{
            Log.d("Task Visualizer", "Save NOT Success: ");
        }
    }

    private Boolean  Save_Sub_Tasks() {
        task_modification_result= 0;
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
        Hard_Delete_All_Empty_Sub_Tasks();

        if (Save_Task_in_TrashCan()) {
            Return_To_Task_List(); //is a method with the finish() method inside, but is there to add animations later

            if (task.task_id != 0) {      //Delete Reminder if exist
                Reminder_Notification.Cancel_Reminder_Alarm(layout_body_task, task.task_id,1,task.reminder);
            }
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
        Log.d("Task Visualizer", "Hard_Delete_All_Empty_Sub_Tasks: ");
        for(int i = task_subList.size() - 1; i >= 0; i --){
            if(task_subList.get(i).note.isEmpty()){
                Task_Sub task_sub = task_subList.get(i);
                if(DB_T.Verify_If_Sub_Task_Is_Empty(task_sub.task_sub_id)){
                    DB_T.Delete_Hard_Specific_Task_Sub(task_sub.task_sub_id);
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

    private Boolean Save_Task_in_TrashCan() {
        task_modification_result = 2;
        if (et_Task_main.getText().toString().isEmpty()) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
            if (task.note != null || has_sub_tasks_in_database ) {
                Log.d("Delete","    Delete: 1-");
                return  getTaskInTrashCan(task.date,task.title,task.note,20,"1-Insertado datos previous");
            }
            Log.d("Delete","    Delete: 2-");
            if(DB_T.Task_Exist(task.task_id)){
                Log.d("Delete","    Delete: 2.2- Main task hard delete by non Useful");
                DB_T.Delete_Hard_Specific_Main_Task(task.task_id);
            }
            task_modification_result = -1;
            Toast.makeText(Task_Visualizer.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
            return true;
        }

        String _description = et_Task_main.getText().toString();
        long _current_time = System.currentTimeMillis();
        if (!change_in_task) {   //if there_is_something save in database > Send to trashcan what was in the database before save
            Log.d("Delete","    Delete: 3-");
            return getTaskInTrashCan(task.date, _description, _description, 20,"3- Sin cambios, save proyectado en edit.T ");
        } else {
            Log.d("Delete","    Delete: 4-");
            return getTaskInTrashCan(_current_time, _description, _description, 20,"4- Cambios realizados, moving to trash ");
        }
    }
    private Boolean getTaskInTrashCan(long date, String title, String _note, int expire_days, String Delete_Case) {
        if ( task.task_id == 0 ) {
            Log.d("Delete","    Delete: 5-");
            Toast.makeText(Task_Visualizer.this, "5- Cambios realizados, directo a TrashCan ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
            change_in_task = false;
            task.task_id =  DB_T.Insert_Task_Directly_in_Trash(date,title,_note,task.pin,20,task.completed,task.has_sub_tasks);

            if(!task_subList.isEmpty()) {
                for(int i = task_subList.size() - 1; i >= 0; i --){
                    Task_Sub task_sub = task_subList.get(i);
                    DB_T.Insert_Sub_Task_Directly_in_Trash(task_sub.parent_id,task_sub.note,task_sub.completed,task_sub.task_sub_position);
                }
            }
            return true;
        }
        Toast.makeText(Task_Visualizer.this, Delete_Case, Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
        DB_T.Send_Task_To_Trash(task.task_id, date, title, _note, task.pin,  expire_days,task.completed,task.has_sub_tasks);
        Log.d("Task Visualizer", "Main Task sent to trash: " + task.getTask_id());
        if(!task_subList.isEmpty()){
            for(int i = task_subList.size() - 1; i >= 0; i --){
                Task_Sub task_sub = task_subList.get(i);
                DB_T.Send_Sub_Task_To_Trash(task_sub.task_sub_id,task_sub.parent_id,task_sub.note, task_sub.completed, task_sub.task_sub_position);
                Log.d("Task Visualizer", "Send_Sub_Task_To_Trash: " + task_sub.getTask_Sub_id());
            }
        }else if(has_sub_tasks_in_database){
            Log.d("Task Visualizer", "Send_Previous_Sub_Task_To_Trash_With_Out_Modification");
            DB_T.Send_Previous_Sub_Task_To_Trash_With_Out_Modification(task.getTask_id());
        }
        return true;
    }

    @Override
    public void Remove_Item(int position) {
        Task_Sub task_sub = task_subList.get(position);
        task_sub.setCompleted(!task_sub.completed);
        if(DB_T.Delete_Hard_Specific_Task_Sub(task_sub.task_sub_id)){
            task_subList.remove(position);
            selected_list.remove(position);
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
        fl_Set_Order.setVisibility(Is_Valid_To_Sort_Completed_Sub_Tasks()? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void Out_Of_Activity() {
        Log.d("Task Visualizer", "Out_Of_Activity: change_in_task: " + change_in_task);
        if(change_in_task)    Hard_Delete_All_Empty_Sub_Tasks();
        if (!now_is_something_written) {
            Delete_Task();
        } else {
            if (tv_Date2.getText().toString().isEmpty()) {
                tv_Date2.setVisibility(View.GONE);
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

        if (is_a_new_task == true && (Task_is_not_empty() || !All_Current_Sub_Task_Are_Empty_2())  && change_in_task) {
            Log.d("Task_Visualizer", "Return to memo board:     New Task. Saving before");
            Save_Task();
            task_modification_result = 1;
        }

        Intent resultadoIntent = new Intent();
        resultadoIntent.putExtra("extra_modificacion", task_modification_result);
        resultadoIntent.putExtra("extra_id", task.task_id);
        Log.d("Task_Visualizer", "Return to memo board, note id: " + task.task_id);
        setResult(Task_Visualizer.RESULT_OK,resultadoIntent);

        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in, R.anim.return_activity_slide_right_out);
    }
}