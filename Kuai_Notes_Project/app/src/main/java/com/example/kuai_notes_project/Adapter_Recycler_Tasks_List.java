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
    private ArrayList<Boolean> selected_id;
    private ArrayList<Note> noteList;
    private ArrayList<Task_Element> task_elements;

    private static final int TYPE_TASK_MAIN = 0;
    private static final int TYPE_TASK_SUB = 0;


    private final Recycler_Tasks_List_Interface recycler_tasks_list_interface;
    private final Recycler_Tasks_Sub_List_Interface recycler_tasks_sub_list_interface;
    private  final Drawable drw_main_single, drw_main_father, drw_sub_middle, drw_sub_end;
    private  final Drawable drw_completed, drw_uncompleted;
    private boolean multi_selection_state = false;
    private boolean is_repeated = false;
    private int multi_first_count = 2;
    private int selected_in_single_mode = -1;

    public void Change_multi_selection_state (boolean multi_selection_state){
        this.multi_selection_state = multi_selection_state;
    }
    public void Change_is_repeated_value (boolean is_repeated){
        this.is_repeated = is_repeated;
    }

    public Adapter_Recycler_Tasks_List(Context context, ArrayList<Boolean> selected_id, ArrayList task_elements, Recycler_Tasks_List_Interface recyclerTaskListInterface, Recycler_Tasks_Sub_List_Interface recyclerTasksSubListInterface){
        this.context = context;
        this.selected_id = selected_id;
        this.task_elements = task_elements;
        this.recycler_tasks_list_interface =recyclerTaskListInterface ;

        this.recycler_tasks_sub_list_interface = recyclerTasksSubListInterface;

        /// Drawable item background:
        drw_main_single = ContextCompat.getDrawable(context, R.drawable.bg_main_task_single);
        drw_main_father = ContextCompat.getDrawable(context, R.drawable.bg_main_task_father_unfolded);
        drw_sub_middle = ContextCompat.getDrawable(context, R.drawable.bg_sub_task_middle);
        drw_sub_end = ContextCompat.getDrawable(context, R.drawable.bg_sub_task_end);

        /// Completed icon:
        drw_completed = ContextCompat.getDrawable(context, R.drawable.icon_completed_task_test_11);
        drw_uncompleted = ContextCompat.getDrawable(context, R.drawable.icon_complete_task_test_4);
        //drw_completed = DrawableCompat.wrap(drw_completed.mutate());
    }


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

        boolean isSelected = selected_id.get(position);
        Drawable background;
        Drawable completed_icon;
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

            //taskHolder.fl_reminder.setVisibility(isReminded ? View.VISIBLE : View.GONE); ///Ternary Operator
            //taskHolder.fl_reminder_activated.setVisibility(isReminded ? View.VISIBLE : View.GONE); ///Ternary Operator

            completed_icon = task.completed ? drw_completed.getConstantState().newDrawable().mutate() : drw_uncompleted.getConstantState().newDrawable().mutate();
            //taskHolder.fl_complete_mark.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), task.completed ? R.color.ex_green : R.color.gray_light_3 ))); ///Ternary Operator
            taskHolder.fl_complete_mark.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), task.completed ? R.color.task_completed_color : R.color.task_uncompleted_color ))); ///Ternary Operator
            //taskHolder.fl_complete_mark.setBackground( task.completed ? drw_completed : drw_uncompleted); ///Ternary Operator
            taskHolder.fl_complete_mark.setBackground( completed_icon); ///Ternary Operator

            if(isHas_Sub_Tasks){
                taskHolder.fl_unfold.setVisibility(View.VISIBLE);
                taskHolder.ghost_unfold.setVisibility(View.VISIBLE);
                taskHolder.fl_unfold.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), task.unfolded ? R.color.green_dark_cha : R.color.gray_light_2 ))); ///Ternary Operator
            }else{
                taskHolder.fl_unfold.setVisibility(View.GONE);
                taskHolder.ghost_unfold.setVisibility(View.GONE);
            }

            taskHolder.fl_pin_icon_activated.setVisibility( !isSelected && isPinned ? View.VISIBLE : View.GONE); ///Ternary Operator
            taskHolder.fl_reminder_activated.setVisibility( isReminded  && (!isSelected || multi_selection_state)? View.VISIBLE : View.GONE); ///Ternary Operator
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
        }else{

            MyViewHolder_Task_Sub subTaskHolder = (MyViewHolder_Task_Sub) holder;
            subTaskHolder.title_id.setVisibility(View.VISIBLE);
            subTaskHolder.title_id.setText(task_elements.get(position).getContent());

            Task_Sub task_sub = (Task_Sub) task_elements.get(position);

            completed_icon = task_sub.completed ? drw_completed.getConstantState().newDrawable().mutate() : drw_uncompleted.getConstantState().newDrawable().mutate();
            subTaskHolder.fl_task_sub_completed.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),task_sub.completed ? R.color.task_completed_color : R.color.task_uncompleted_color ))); ///Ternary Operator
            subTaskHolder.fl_task_sub_completed.setBackground( completed_icon); ///Ternary Operator

            subTaskHolder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),isSelected   ? R.color.item_background_selected : R.color.white_sand_light )));

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

    }
    @Override
    public int getItemViewType(int position){

        return task_elements.get(position).getViewType();
    }


    @Override
    public int getItemCount(){
        return task_elements.size();
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
        View ghost_unfold;
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
            ghost_unfold = itemView.findViewById(R.id.Layout_Unfold_Ghost);

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
            //itemView.findViewById(R.id.Fl_Completed_Mark).setOnClickListener(new View.OnClickListener(){
            //    @Override
            //    public void onClick(View v){
            //        if (recyclerTasksListInterface != null){
            //            int pos = getAbsoluteAdapterPosition();
            //            if (pos != RecyclerView.NO_POSITION){
            //                recyclerTasksListInterface.Complete_Main_Task(pos);
            //            }
            //        }
            //    }
            //});
            itemView.findViewById(R.id.Fl_Completed_Mark_Ghost).setOnClickListener(new View.OnClickListener(){
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
            //itemView.findViewById(R.id.Fl_Unfold).setOnClickListener(new View.OnClickListener(){
            //    @Override
            //    public void onClick(View v){
            //        if (recyclerTasksListInterface != null){
            //            int pos = getAbsoluteAdapterPosition();
            //            if (pos != RecyclerView.NO_POSITION){
            //                recyclerTasksListInterface.Unfold(pos,task_elements.get(pos).getId());
            //            }
            //        }
            //    }
            //});
            itemView.findViewById(R.id.Layout_Unfold_Ghost).setOnClickListener(new View.OnClickListener(){
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
                    if (recyclerTasksSubListInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksSubListInterface.onItemHold_Sub_Task(pos,v);
                            return true;
                        }
                    }
                    return false;
                }
            });
            //itemView.findViewById(R.id.Fl_Completed_Mark).setOnClickListener(new View.OnClickListener(){
            //    @Override
            //    public void onClick(View v){
            //        if (recyclerTasksSubListInterface != null){
            //            int pos = getAbsoluteAdapterPosition();
            //            if (pos != RecyclerView.NO_POSITION){
            //                recyclerTasksSubListInterface.Complete_Sub_Task(pos);
            //            }
            //        }
            //    }
            //});
            itemView.findViewById(R.id.Fl_Completed_Mark_Ghost).setOnClickListener(new View.OnClickListener(){
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