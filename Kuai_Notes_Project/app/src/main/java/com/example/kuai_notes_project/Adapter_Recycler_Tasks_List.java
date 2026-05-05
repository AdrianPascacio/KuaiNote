package com.example.kuai_notes_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Adapter_Recycler_Tasks_List   extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList date_id;
    private ArrayList<Boolean> selected_id;
    private ArrayList<Note> noteList;
    private ArrayList<Task_Main> taskList;
    private ArrayList<Task_Sub> task_subList;
    private ArrayList<Task_Element> task_elements;

    private static final int TYPE_TASK_MAIN = 0;
    private static final int TYPE_TASK_SUB = 0;


    private final Recycler_Tasks_List_Interface recycler_tasks_list_interface;
    private final Recycler_Tasks_Sub_List_Interface recycler_tasks_sub_list_interface;
    private  final Drawable drw_main_single, drw_main_father, drw_sub_middle, drw_sub_end;
    private boolean multi_selection_state = false;
    private boolean is_repeated = false;
    private int multi_first_count = 2;
    private int selected_in_single_mode = -1;

    private List<Check_With_Subs> checks = new ArrayList<>();
    private List<DB_Check_Main> mainChecks = new ArrayList<>();
    public void Change_multi_selection_state (boolean multi_selection_state){
        this.multi_selection_state = multi_selection_state;
    }
    public void Change_is_repeated_value (boolean is_repeated){
        this.is_repeated = is_repeated;
    }

    public Adapter_Recycler_Tasks_List(Context context, ArrayList date_id, ArrayList<Boolean> selected_id, ArrayList taskList, ArrayList task_subList, ArrayList task_elements, Recycler_Tasks_List_Interface recyclerTaskListInterface, Recycler_Tasks_Sub_List_Interface recyclerTasksSubListInterface){
        this.context = context;
        this.date_id = date_id;
        this.selected_id = selected_id;
        ///this.noteList = noteList;
        this.taskList = taskList;
        this.task_subList = task_subList;
        this.task_elements = task_elements;
        this.recycler_tasks_list_interface =recyclerTaskListInterface ;

        this.recycler_tasks_sub_list_interface = recyclerTasksSubListInterface;

        drw_main_single = ContextCompat.getDrawable(context, R.drawable.bg_main_task_single);
        drw_main_father = ContextCompat.getDrawable(context, R.drawable.bg_main_task_father_unfolded);
        drw_sub_middle = ContextCompat.getDrawable(context, R.drawable.bg_sub_task_middle);
        //!!--Have to update the final drawable type:
        drw_sub_end = ContextCompat.getDrawable(context, R.drawable.bg_sub_task_end);
    }

    ///public Adapter_Recycler_Check_Lists(Context context, ArrayList date_id, ArrayList<Boolean> selected_id, ArrayList noteList,  Recycler_Check_Lists_Interface recyclerCheckListsInterface){
    ///    this.context = context;
    ///    this.date_id = date_id;
    ///    this.selected_id = selected_id;
    ///    this.noteList = noteList;
    ///    this.recycler_check_lists_interface =recyclerCheckListsInterface ;

    /// }



    ///@NonNull
    ///@Override
    ///public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ///        View v = LayoutInflater.from(context).inflate(R.layout.recycler_tasks_list,parent,false);
    ///        return new MyViewHolder(v, recycler_tasks_list_interface);
    ///}



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d("TasksList","   view type:  "+viewType);
        if(viewType == TYPE_TASK_MAIN){
            ///View v = LayoutInflater.from(context).inflate(R.layout.recycler_tasks_list,parent,false);
            View v = inflater.inflate(R.layout.recycler_tasks_list,parent,false);
            return new MyViewHolder_Task_Main(v, recycler_tasks_list_interface);
        }else{
            ///View v = LayoutInflater.from(context).inflate(R.layout.recycler_tasks_sub_list,parent,false);
            View v = inflater.inflate(R.layout.recycler_tasks_sub_list,parent,false);
            return new MyViewHolder_Task_Sub(v,recycler_tasks_sub_list_interface);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,int position){

        //DB_Check_Main current_check_main =   mainChecks.get(position);
        //Check_With_Subs current_check = checks.get(position);



        //Task_Main task = taskList.get(position);
        //Log.d("TasksSubList","   TaskSub size:  "+ task_subList.size());
        //Task_Sub task_sub = task_subList.get(0);
        ///boolean isPinned = note.pin;
        ///boolean isReminded = note.reminder > 0;
        ///Animation Animation_Pin_Orange_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_appear_memoboard);
        ///Animation Animation_Pin_Orange_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_appear_memoboard_invert);
        ///Animation Animation_Pin_Gray_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_gray_appear_memoboard);
        ///Animation Animation_Pin_Gray_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_gray_appear_memoboard_invert);
        ///Animation Animation_Reminder_Active_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.reminder_active_icon_appear_memoboard);
        ///Animation Animation_Reminder_Active_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.reminder_active_icon_appear_memoboard_invert);
        ///Animation Animation_TrashCan_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.trashcan_appear_memoboard);
        ///Animation Animation_TrashCan_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.trashcan_appear_memoboard_invert);
        ///Animation Animation_Extend = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.extend_item);
        ///Animation Animation_Extend_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.extend_item_invert);
        ///Body_Note_Preview BNP = new Body_Note_Preview();

        //------Title Visibility depending on emptiness:
        //Log.d("TasksList","   Note id:  "+task.getTask_id());
        boolean isSelected = selected_id.get(position);
        Drawable background;
        if( getItemViewType(position) == TYPE_TASK_MAIN){




            Task_Main task = (Task_Main) task_elements.get(position);
            boolean isPinned = task.pin;
            boolean isReminded = task.reminder > 0;
            boolean isHas_Sub_Tasks = task.has_sub_tasks;
            boolean isUnfolded = task.unfolded;
            Log.d("Adapter Recycler Task List" , "unfold: " +isUnfolded + "    Content: " +task.note);

            MyViewHolder_Task_Main taskHolder = (MyViewHolder_Task_Main) holder;




            taskHolder.title_id.setVisibility(View.VISIBLE);
            taskHolder.title_id.setText(task_elements.get(position).getContent() );

            ///taskHolder.fl_pin_icon_activated.setVisibility(isPinned ? View.VISIBLE : View.GONE); ///Ternary Operator
            taskHolder.fl_reminder.setVisibility(isReminded ? View.VISIBLE : View.GONE); ///Ternary Operator

            taskHolder.fl_complete_mark.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), task.completed ? R.color.ex_green : R.color.gray_light_3 ))); ///Ternary Operator

            if(isHas_Sub_Tasks){
                taskHolder.fl_unfold.setVisibility(View.VISIBLE);
                taskHolder.fl_unfold.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), task.unfolded ? R.color.ex_green : R.color.gray_light_3 ))); ///Ternary Operator
            }else{
                taskHolder.fl_unfold.setVisibility(View.GONE);
            }

            taskHolder.fl_pin_icon_activated.setVisibility( !isSelected && isPinned ? View.VISIBLE : View.GONE); ///Ternary Operator
            taskHolder.fl_delete_ghost.setVisibility(isSelected && !multi_selection_state ? View.VISIBLE : View.GONE);
            taskHolder.fl_delete.setVisibility(isSelected  && !multi_selection_state ? View.VISIBLE : View.GONE);
            taskHolder.fl_pin.setVisibility(isSelected  && !multi_selection_state ? View.VISIBLE : View.GONE);
            taskHolder.fl_pin_ghost.setVisibility(isSelected  && !multi_selection_state ? View.VISIBLE : View.GONE);

            //taskHolder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),isSelected   ? R.color.item_background_selected : R.color.white_sand_light )));
            //taskHolder.fl_item.setBackgroundResource(R.drawable.bg_main_task_single);

            ///Changing background color:
            //Drawable background = ContextCompat.getDrawable(context,R.drawable.bg_main_task_single);
            //background = DrawableCompat.wrap(background).mutate();
            ////DrawableCompat.setTint(background,ContextCompat.getColor(holder.itemView.getContext(),isSelected   ? R.color.item_background_selected : R.color.white_sand_light ));
            //DrawableCompat.setTint(background, Color.parseColor("#FF5722"));
            //taskHolder.fl_item.setBackground(background);
            taskHolder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),isSelected   ? R.color.item_background_selected : R.color.white_sand_light )));
            //taskHolder.fl_item.setBackgroundResource(R.drawable.bg_main_task_single);
            if (isUnfolded) {
                //taskHolder.fl_item.setBackgroundResource(R.drawable.bg_main_task_father_unfolded);
                background = drw_main_father.getConstantState().newDrawable().mutate();
            }else{
                background = drw_main_single.getConstantState().newDrawable().mutate();

            }
            //int color = (ContextCompat.getColor(holder.itemView.getContext(),isSelected   ? R.color.item_background_selected : R.color.white_sand_light ));
            //DrawableCompat.setTint(background,color);
            taskHolder.fl_item.setBackground(background);

            if(isSelected){
                taskHolder.fl_item.setScaleX(1.02f);
                taskHolder.fl_item.setScaleY(1.02f);
            }else{
                taskHolder.fl_item.setScaleX(1.0f);
                taskHolder.fl_item.setScaleY(1.0f);

            }
            //taskHolder.layout_global_item.setAlpha(isSelected   ? 0.5f : 1.0f );

            ///taskHolder.note_preview_id.setVisibility(View.VISIBLE);
            ///taskHolder.note_preview_id.setText("Task slave example: " +   String.valueOf((int) task.getTask_id() + "    task sub:" + task_sub.getNote()) );

            ///taskHolder.layout_btn_options.setVisibility(View.GONE);
            ///taskHolder.layout_btn_options_ghost.setVisibility(View.GONE);
            ///taskHolder.layout_options_reminder_ghost.setVisibility(View.GONE);
            ///taskHolder.fl_reminder.setVisibility(View.GONE);

            ///taskHolder.fl_pin_icon_activated.setVisibility( View.GONE);
            ///taskHolder.fl_reminder_activated.setVisibility( View.GONE);
        }else{

            MyViewHolder_Task_Sub subTaskHolder = (MyViewHolder_Task_Sub) holder;
            subTaskHolder.title_id.setVisibility(View.VISIBLE);
            subTaskHolder.title_id.setText(task_elements.get(position).getContent());

            Task_Sub task_sub = (Task_Sub) task_elements.get(position);

            subTaskHolder.fl_task_sub_completed.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),task_sub.completed ? R.color.ex_green : R.color.gray_light_3 ))); ///Ternary Operator

            subTaskHolder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),isSelected   ? R.color.item_background_selected : R.color.white_sand_light )));
            //background = drw_main_father.getConstantState().newDrawable().mutate();
            ///subTaskHolder.fl_item.setBackgroundResource(R.drawable.bg_sub_task_middle);
            if(position + 1 <= task_elements.size()-1){
                if( getItemViewType(position + 1) == TYPE_TASK_MAIN){
                    background = Objects.requireNonNull(drw_sub_end.getConstantState()).newDrawable().mutate();
                }else{
                    background = Objects.requireNonNull(drw_sub_middle.getConstantState()).newDrawable().mutate();
                }
            }else{
                background = Objects.requireNonNull(drw_sub_end.getConstantState()).newDrawable().mutate();
            }
            subTaskHolder.fl_item.setBackground(background);
        }














        ///Log.d("TasksList","   rv  "+current_check_main.note);

        ///if((!current_check.checkMain.note.isEmpty())){
        ///    holder.title_id.setVisibility(View.VISIBLE);
        ///    holder.title_id.setText(current_check.checkMain.note);

        ///    holder.date_id.setPadding(0,0,0,0);
        ///}else{
        ///    holder.title_id.setVisibility(View.GONE);

        ///    holder.date_id.setPadding(0,10,0,0);
        ///}

        /// Original:
        ///if((!note.title.isEmpty())){
        ///    holder.title_id.setVisibility(View.VISIBLE);
        ///    holder.title_id.setText(note.title);

        ///    holder.date_id.setPadding(0,0,0,0);
        ///}else{
        ///    holder.title_id.setVisibility(View.GONE);

        ///    holder.date_id.setPadding(0,10,0,0);
        ///}

        ///holder.date_id.setText(String.valueOf(date_id.get(position)));



        ///holder.note_preview_id.setText(note.note);
        /////------Visibility depending if it is Selected:
        ///if(selected_id.get(position)==true){
        ///    //holder.note_preview_id.setText(note.note);
        ///    holder.note_preview_id.setMaxLines(3);

        ///    holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_background_selected)));
        ///    //holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AEFDF2D8")));
        ///    //holder.fl_item.setScaleX(1.02f);
        ///    //holder.fl_item.setScaleY(1.02f);

        ///    //holder.fl_item.startAnimation(Animation_Extend);
        ///    //holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_bloody_1)));
        ///    holder.title_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_title_selected));
        ///    holder.date_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_date_selected));
        ///    holder.note_preview_id.setPadding(0,0,0,44);
        ///    holder.note_preview_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_note_preview_selected));
        ///    //Log.d("Adapter","    --Selected: " +position);

        ///    if(multi_selection_state){
        ///        if(multi_first_count > 0) {
        ///            //Log.d("Adapter", "    --selected_in_single_mode: " + selected_in_single_mode);
        ///            if(selected_in_single_mode == position) {
        ///                Unselecting_View_For_First_Tow_Multiple_Selections(holder, Animation_TrashCan_Appear_invert, isPinned, Animation_Pin_Orange_Appear_invert, Animation_Pin_Gray_Appear_invert, isReminded, Animation_Reminder_Active_Appear_invert);
        ///                //Log.d("Adapter", "    --Unselecting_View_For_First_Tow_Multiple_Selections: " + position);
        ///            }else{
        ///                Selecting_View_With_No_Animations(holder, isPinned, isReminded);
        ///                //Log.d("Adapter", "    --Selecting_View_Without_animation_Multiple_Selections: " + position);
        ///            }
        ///            multi_first_count--;
        ///        }else{
        ///            Selecting_View_With_No_Animations(holder, isPinned, isReminded);
        ///            //Log.d("Adapter","    --Add_Item_Without_animations_In_Multiple_Selections_Mode: " +position);
        ///        }
        ///    }else{
        ///        //Log.d("Adapter","   Selecting_View_Single_Mode: " +position + "    selected_in_single_mode: "+selected_in_single_mode+"\n" +
        ///        //      "       Is_Reminded:"+isReminded);
        ///        Selecting_View_Single_Mode(holder, Animation_TrashCan_Appear, isPinned, Animation_Pin_Orange_Appear, Animation_Pin_Gray_Appear, isReminded, Animation_Reminder_Active_Appear);
        ///        selected_in_single_mode = position;
        ///    }
        ///}else{
        ///    holder.note_preview_id.setMaxLines(2);
        ///    ///holder.note_preview_id.setText(BNP.Set_Body_Note_Preview(note.note,
        ///    ///        note.note,
    ///    ///    ///        60,
    ///    ///    ///        55,
    ///    ///    ///        0,
    ///    ///    ///        2,
    ///    ///    ///        1,
    ///    ///    ///        30));


    ///    ///    //holder.fl_item.clearAnimation();
    ///    ///    holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_background_unselected)));
    ///    ///    //holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008F8F8F")));

    ///    ///    holder.title_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_title_notselected));
    ///    ///    holder.date_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_date_notselected));
    ///    ///    holder.note_preview_id.setPadding(0,0,0,0);
    ///    ///    holder.note_preview_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_note_preview_notselected));

    ///    ///    //holder.fl_item.setScaleX(1f);
    ///    ///    //holder.fl_item.setScaleY(1f);
    ///    ///    //Log.d("Adapter","   Not_Selected_View: " +position);

    ///    ///    if(is_repeated){
    ///    ///        Unselecting_View_Repeated(holder, Animation_TrashCan_Appear_invert, isPinned, Animation_Pin_Orange_Appear_invert, Animation_Pin_Gray_Appear_invert, isReminded, Animation_Reminder_Active_Appear_invert);
    ///    ///        //Log.d("Adapter","   Unselecting_View Repeated: " +position);
    ///    ///        is_repeated = false;
    ///    ///        selected_in_single_mode = -1;
    ///    ///    }else{
    ///    ///        if(multi_selection_state){
    ///    ///            Unselect_Item_Without_Animations(holder, isPinned, isReminded);
    ///    ///            //Log.d("Adapter","   --Rest_Item_Without_animations_In_Multiple_Selections_Mode: " +position);
    ///    ///        }else{
    ///    ///            Unselect_Item_Without_Animations(holder, isPinned, isReminded);
    ///    ///            //Log.d("Adapter","   Unselecting View  (no multi_mode)-------: " +position);
    ///    ///        }
    ///    ///    }
    ///    ///}

    ///    ///if (!multi_selection_state){
    ///    ///    if(multi_first_count == 0) {
    ///    ///        selected_in_single_mode = -1;
    ///    ///    }
    ///    ///    multi_first_count = 2;
    ///    ///}

    ///}

    ///private static void Unselect_Item_Without_Animations(@NonNull MyViewHolder_Task_Main holder, boolean isPinned, boolean isReminded) {
    ///    //Button Layout Visibility:
    ///    holder.layout_btn_options.setVisibility(View.GONE);
    ///    holder.layout_btn_options_ghost.setVisibility(View.GONE);
    ///    holder.layout_options_reminder_ghost.setVisibility(View.GONE);
    ///    holder.fl_reminder.setVisibility(View.GONE);

    ///    holder.fl_pin_icon_activated.setVisibility(isPinned ? View.VISIBLE : View.GONE); ///Ternary Operator
    ///    holder.fl_reminder_activated.setVisibility(isReminded ? View.VISIBLE : View.GONE); ///Ternary Operator
    ///    //log.d("Adapter","   Reminded icon activated: " +isReminded);
    ///}

    ///private static void Unselecting_View_For_First_Tow_Multiple_Selections(@NonNull MyViewHolder_Task_Main holder, Animation Animation_TrashCan_Appear_invert, boolean isPinned, Animation Animation_Pin_Orange_Appear_invert, Animation Animation_Pin_Gray_Appear_invert, boolean isReminded, Animation Animation_Reminder_Active_Appear_invert) {
    ///    holder.layout_btn_options.setVisibility(View.GONE);
    ///    holder.layout_options_reminder_ghost.setVisibility(View.GONE);
    ///    holder.fl_delete.clearAnimation();
    ///    holder.fl_delete.clearAnimation();

    ///    Buttons_View_And_Animations_Unselecting(holder, isPinned, Animation_Pin_Orange_Appear_invert, Animation_Pin_Gray_Appear_invert, isReminded, Animation_Reminder_Active_Appear_invert);


    ///    ///holder.layout_btn_options.setVisibility(View.VISIBLE);
    ///    ///holder.fl_delete.startAnimation(Animation_TrashCan_Appear_invert);
    ///    ///holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));

    ///    ///Buttons_View_And_Animations(holder, isPinned, Animation_Pin_Orange_Appear_invert, Animation_Pin_Gray_Appear_invert, isReminded, Animation_Reminder_Active_Appear_invert);
    ///}

    ///private static void Selecting_View_With_No_Animations(@NonNull MyViewHolder_Task_Main holder, boolean isPinned, boolean isReminded) {
    ///    //Button Layout Visibility:
    ///    holder.layout_btn_options.setVisibility(View.GONE);
    ///    holder.layout_btn_options_ghost.setVisibility(View.GONE);
    ///    holder.layout_options_reminder_ghost.setVisibility(View.GONE);
    ///    holder.fl_reminder.setVisibility(View.GONE);

    ///    holder.fl_pin_icon_activated.setVisibility(isPinned ? View.VISIBLE : View.GONE); ///Ternary Operator
    ///    holder.fl_reminder_activated.setVisibility(isReminded ? View.VISIBLE : View.GONE); ///Ternary Operator
    ///}

    ///private static void Unselecting_View_Repeated(@NonNull MyViewHolder_Task_Main holder, Animation Animation_TrashCan_Appear_invert, boolean isPinned, Animation Animation_Pin_Orange_Appear_invert, Animation Animation_Pin_Gray_Appear_invert, boolean isReminded, Animation Animation_Reminder_Active_Appear_invert) {
    ///    holder.layout_btn_options.setVisibility(View.GONE);
    ///    holder.layout_btn_options_ghost.setVisibility(View.GONE);
    ///    ///Este es el culpable!!!!: holder.layout_reminder.setVisibility(View.GONE);
    ///    holder.layout_options_reminder_ghost.setVisibility(View.GONE);
    ///    holder.fl_delete.clearAnimation();

    ///    Buttons_View_And_Animations_Unselecting(holder, isPinned, Animation_Pin_Orange_Appear_invert, Animation_Pin_Gray_Appear_invert, isReminded, Animation_Reminder_Active_Appear_invert);


    ///    ///holder.layout_btn_options.setVisibility(View.VISIBLE);
    ///    ///holder.layout_btn_options_ghost.setVisibility(View.GONE);
    ///    ///holder.layout_options_reminder_ghost.setVisibility(View.GONE);
    ///    ///holder.fl_delete.startAnimation(Animation_TrashCan_Appear_invert);//!!Importante falta quitar animacion en la contrapante
    ///    ///holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));

    ///    ///Buttons_View_And_Animations(holder, isPinned, Animation_Pin_Orange_Appear_invert, Animation_Pin_Gray_Appear_invert, isReminded, Animation_Reminder_Active_Appear_invert);
    ///}

    ///private static void Selecting_View_Single_Mode(@NonNull MyViewHolder_Task_Main holder, Animation Animation_TrashCan_Appear, boolean isPinned, Animation Animation_Pin_Orange_Appear, Animation Animation_Pin_Gray_Appear, boolean isReminded, Animation Animation_Reminder_Active_Appear) {
    ///    //Button Layout Visibility:
    ///    holder.layout_btn_options.setVisibility(View.VISIBLE);
    ///    holder.layout_btn_options_ghost.setVisibility(View.VISIBLE);
    ///    holder.layout_options_reminder_ghost.setVisibility(View.VISIBLE);
    ///    holder.fl_delete.setAnimation(Animation_TrashCan_Appear);

    ///    //holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A12015")));

    ///    //Original standar way:
    ///    //Context context = holder.itemView.getContext();
    ///    //int color = ContextCompat.getColor(context, R.color.red_bloody_1);
    ///    //ColorStateList colorStateList = ColorStateList.valueOf(color);
    ///    //holder.fl_delete.setBackgroundTintList(colorStateList);

    ///    holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_bloody_1)));




    ///    Buttons_View_And_Animations(holder, isPinned, Animation_Pin_Orange_Appear, Animation_Pin_Gray_Appear, isReminded, Animation_Reminder_Active_Appear);
    ///}
    ///private static void Buttons_View_And_Animations_Unselecting(@NonNull MyViewHolder_Task_Main holder, boolean isPinned, Animation Animation_Is_Pinned, Animation Animation_Is_NOT_Pinned, boolean isReminded, Animation Animation_Is_Reminded) {
    ///    if(isPinned){
    ///        holder.fl_pin_icon_activated.setVisibility(View.VISIBLE);
    ///        holder.fl_pin_icon_activated.startAnimation(Animation_Is_Pinned);
    ///    }else{
    ///        holder.fl_pin_icon_activated.setVisibility(View.GONE);
    ///        holder.fl_pin_icon_activated.clearAnimation();
    ///        holder.fl_pin.setVisibility(View.VISIBLE);
    ///        holder.fl_pin.startAnimation(Animation_Is_NOT_Pinned);
    ///    }

    ///    if(isReminded){
    ///        holder.fl_reminder_activated.setVisibility(View.VISIBLE);
    ///        holder.fl_reminder_activated.startAnimation(Animation_Is_Reminded);

    ///    }else{
    ///        holder.fl_reminder_activated.setVisibility(View.GONE);
    ///        holder.fl_reminder_activated.clearAnimation();

    ///        holder.fl_reminder.setVisibility(View.VISIBLE);
    ///        holder.fl_reminder.startAnimation(Animation_Is_NOT_Pinned);
    ///    }
    ///}
    ///private static void Buttons_View_And_Animations(@NonNull MyViewHolder_Task_Main holder, boolean isPinned, Animation Animation_Is_Pinned, Animation Animation_Is_NOT_Pinned, boolean isReminded, Animation Animation_Is_Reminded) {
    ///    //if(isPinned){
    ///    //    holder.fl_pin_icon_activated.setVisibility(View.VISIBLE);
    ///    //    holder.fl_pin_icon_activated.startAnimation(Animation_Is_Pinned);
    ///    //    holder.fl_pin.clearAnimation();
    ///    //    holder.fl_pin.setVisibility(View.GONE);
    ///    //}else{
    ///    //    holder.fl_pin_icon_activated.setVisibility(View.GONE);
    ///    //    holder.fl_pin_icon_activated.clearAnimation();
    ///    //    holder.fl_pin.clearAnimation();
    ///    //    holder.fl_pin.setVisibility(View.GONE);
    ///    //}

    ///    //if(isReminded){
    ///    //    holder.fl_reminder_activated.setVisibility(View.VISIBLE);
    ///    //    holder.fl_reminder_activated.startAnimation(Animation_Is_Reminded);

    ///    //    holder.fl_reminder.setVisibility(View.GONE);
    ///    //    holder.fl_reminder.clearAnimation();
    ///    //}else{
    ///    //    holder.fl_reminder_activated.setVisibility(View.GONE);
    ///    //    holder.fl_reminder_activated.clearAnimation();

    ///    //    holder.fl_reminder.setVisibility(View.GONE);
    ///    //    holder.fl_reminder.clearAnimation();
    ///    //}

    ///    if(isPinned){
    ///        holder.fl_pin_icon_activated.setVisibility(View.VISIBLE);
    ///        holder.fl_pin_icon_activated.startAnimation(Animation_Is_Pinned);
    ///        holder.fl_pin.clearAnimation();
    ///        holder.fl_pin.setVisibility(View.GONE);
    ///    }else{
    ///        holder.fl_pin_icon_activated.setVisibility(View.GONE);
    ///        holder.fl_pin_icon_activated.clearAnimation();
    ///        holder.fl_pin.setVisibility(View.VISIBLE);
    ///        holder.fl_pin.startAnimation(Animation_Is_NOT_Pinned);
    ///    }

    ///    if(isReminded){
    ///        holder.fl_reminder_activated.setVisibility(View.VISIBLE);
    ///        holder.fl_reminder_activated.startAnimation(Animation_Is_Reminded);

    ///        holder.fl_reminder.setVisibility(View.GONE);
    ///        holder.fl_reminder.clearAnimation();
    ///    }else{
    ///        holder.fl_reminder_activated.setVisibility(View.GONE);
    ///        holder.fl_reminder_activated.clearAnimation();

    ///        //Log.d("Adapter","   Activando fl_reminder");
    ///        holder.fl_reminder.setVisibility(View.VISIBLE);
    ///        holder.fl_reminder.startAnimation(Animation_Is_NOT_Pinned);
    ///    }




        //Original:

        //if(isPinned){
        //    holder.fl_pin_icon_activated.setVisibility(View.VISIBLE);
        //    holder.fl_pin_icon_activated.startAnimation(Animation_Is_Pinned);
        //    holder.fl_pin.clearAnimation();
        //    holder.fl_pin.setVisibility(View.GONE);
        //}else{
        //    holder.fl_pin_icon_activated.setVisibility(View.GONE);
        //    holder.fl_pin_icon_activated.clearAnimation();
        //    holder.fl_pin.setVisibility(View.VISIBLE);
        //    holder.fl_pin.startAnimation(Animation_Is_NOT_Pinned);
        //}

        //if(isReminded){
        //    holder.fl_reminder_activated.setVisibility(View.VISIBLE);
        //    holder.fl_reminder_activated.startAnimation(Animation_Is_Reminded);

        //    holder.fl_reminder.setVisibility(View.GONE);
        //    holder.fl_reminder.clearAnimation();
        //}else{
        //    holder.fl_reminder_activated.setVisibility(View.GONE);
        //    holder.fl_reminder_activated.clearAnimation();

        //    holder.fl_reminder.setVisibility(View.VISIBLE);
        //    holder.fl_reminder.startAnimation(Animation_Is_NOT_Pinned);
        //}
    }
    @Override
    public int getItemViewType(int position){
        ///if(task_elements.get(position) instanceof Task_Main){
        ///    return TYPE_TASK_MAIN;
        ///}else{
        ///    return  TYPE_TASK_SUB;
        ///}

        return task_elements.get(position).getViewType();
    }


    @Override
    public int getItemCount(){
        ///return mainChecks.size();
        return task_elements.size();
    }
    public void setChecks(List<DB_Check_Main> checks){
        this.mainChecks = checks;
        Log.d("CheckList","   RVV  ");
    }

    public void Set_Selection_Mode_On() {

        this.multi_selection_state = true;
    }
    public void Set_Selection_Mode_Off() {

        this.multi_selection_state = false;
    }

    public class MyViewHolder_Task_Main extends RecyclerView.ViewHolder {
        TextView date_id, title_id, note_preview_id;
        View layout_btn_options, layout_btn_options_ghost, layout_global_item, layout_reminder, layout_options_reminder_ghost;
        FrameLayout fl_delete, fl_reminder, fl_pin ,fl_delete_ghost, fl_reminder_ghost, fl_pin_ghost ,  fl_pin_icon_activated, fl_reminder_activated;
        FrameLayout fl_complete_mark, fl_unfold;
        FrameLayout fl_item;


        public MyViewHolder_Task_Main(@NonNull View itemView, Recycler_Tasks_List_Interface recyclerTasksListInterface){
            super(itemView);
            date_id = itemView.findViewById(R.id.Text_Note_Date);
            title_id = itemView.findViewById(R.id.Text_Note_Title);
            note_preview_id = itemView.findViewById(R.id.Text_Note_Preview);
            layout_btn_options = itemView.findViewById(R.id.Layout_Item_Options);
            layout_btn_options_ghost = itemView.findViewById(R.id.Layout_Item_Options_Ghost);
            layout_reminder = itemView.findViewById(R.id.Layout_Reminder);
            layout_options_reminder_ghost = itemView.findViewById(R.id.Layout_Option_Reminder_Ghost);
            layout_global_item = itemView.findViewById(R.id.Layout_Global_Item);
            fl_delete = itemView.findViewById(R.id.FL_Item_Delete);
            fl_reminder = itemView.findViewById(R.id.Fl_Reminder);
            fl_pin = itemView.findViewById(R.id.Fl_Item_Pin);
            fl_delete_ghost = itemView.findViewById(R.id.FL_Item_Delete_Ghost);
            fl_reminder_ghost = itemView.findViewById(R.id.Fl_Reminder_Ghost);
            fl_pin_ghost = itemView.findViewById(R.id.Fl_Item_Pin_Ghost);
            fl_pin_icon_activated = itemView.findViewById(R.id.FrameLayout_Pin_Icon);
            fl_reminder_activated = itemView.findViewById(R.id.Fl_Reminder_Activated);
            fl_item = itemView.findViewById((R.id.Layout_Item));
            fl_complete_mark = itemView.findViewById((R.id.Fl_Completed_Mark));
            fl_unfold = itemView.findViewById((R.id.Fl_Unfold));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerTasksListInterface != null){
                        //int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.onItemClick(pos,v);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    if (recyclerTasksListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.onItemHold(pos,v);
                            return true;
                        }
                    }
                    return false;
                }
            });
            itemView.findViewById(R.id.FL_Item_Delete_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.RemoveItem(pos);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.Fl_Item_Pin_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.PinItem(pos);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.Fl_Completed_Mark).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.Complete_Main_Task(pos);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.Fl_Unfold).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.Unfold(pos,task_elements.get(pos).getId());
                        }
                    }
                }
            });
            itemView.findViewById(R.id.Fl_Reminder_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksListInterface.SetReminder(pos);
                        }
                    }
                }
            });
        }

    }
    public class MyViewHolder_Task_Sub extends RecyclerView.ViewHolder {
        TextView date_id, title_id, note_preview_id;
        View layout_btn_options, layout_btn_options_ghost, layout_global_item, layout_reminder, layout_options_reminder_ghost;
        FrameLayout fl_delete, fl_reminder, fl_pin ,fl_delete_ghost, fl_reminder_ghost, fl_pin_ghost ,  fl_pin_icon_activated, fl_reminder_activated;
        FrameLayout fl_task_sub_completed;
        FrameLayout fl_item;


        public MyViewHolder_Task_Sub(@NonNull View itemView, Recycler_Tasks_Sub_List_Interface recyclerTasksSubListInterface){
            super(itemView);
            title_id = itemView.findViewById(R.id.Text_Task_Sub_Title);
            fl_task_sub_completed = itemView.findViewById(R.id.Fl_Completed_Mark);
            fl_item = itemView.findViewById((R.id.Layout_Item));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerTasksSubListInterface != null){
                        //int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksSubListInterface.onItemClick(pos,v);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    ///if (recyclerTasksSubListInterface != null){
                    ///    int pos = getAbsoluteAdapterPosition();
                    ///    if (pos != RecyclerView.NO_POSITION){
                    ///        recyclerTasksSubListInterface.onItemHold(pos,v);
                    ///        return true;
                    ///    }
                    ///}
                    return false;
                }
            });
            itemView.findViewById(R.id.Fl_Completed_Mark).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksSubListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksSubListInterface.Complete_Sub_Task(pos);
                        }
                    }
                }
            });
        }

    }
}