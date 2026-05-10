package com.example.kuai_notes_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Recycler_Tasks_Sub_In_Visualizer extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList date_id;
    private ArrayList<Boolean> selected_id;
    private ArrayList<Task_Sub> task_subList;

    private static final int TYPE_TASK_MAIN = 0;
    private static final int TYPE_TASK_SUB = 0;


    private final Recycler_Tasks_Sub_In_Visualizer_Interface recycler_tasks_sub_in_visualizer_interface;
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

    public Adapter_Recycler_Tasks_Sub_In_Visualizer(Context context, ArrayList<Boolean> selected_id, ArrayList task_subList, Recycler_Tasks_Sub_In_Visualizer_Interface recyclerTasksSubInVisualizerInterface){
        this.context = context;
        this.selected_id = selected_id;
        this.task_subList = task_subList;

        this.recycler_tasks_sub_in_visualizer_interface =recyclerTasksSubInVisualizerInterface ;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_tasks_sub_in_visualizer,parent,false);
        return new MyViewHolder_Task_Sub(v, recycler_tasks_sub_in_visualizer_interface);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,int position){

            MyViewHolder_Task_Sub taskHolder = (MyViewHolder_Task_Sub) holder;
            taskHolder.task_sub_description_id.setVisibility(View.VISIBLE);


            taskHolder.task_sub_description_id.removeTextChangedListener(taskHolder.activeTextWatcher_Sub_Task_Description);
            taskHolder.task_sub_description_id.setText(task_subList.get(position).getContent() );
            taskHolder.task_sub_description_id.addTextChangedListener(taskHolder.activeTextWatcher_Sub_Task_Description);


            if(task_subList.get(position).completed){
                taskHolder.fl_task_sub_completed.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.ex_green)));
            }else{
                taskHolder.fl_task_sub_completed.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_light_3)));
            }

    }

    @Override
    public int getItemViewType(int position){
        return task_subList.get(position).getViewType();
    }


    @Override
    public int getItemCount(){
        return task_subList.size();
    }

    public class MyViewHolder_Task_Sub extends RecyclerView.ViewHolder {
        EditText task_sub_description_id;
        public TextWatcher activeTextWatcher_Sub_Task_Description;
        TextView date_id, note_preview_id;
        View layout_btn_options, layout_btn_options_ghost, layout_global_item, layout_reminder, layout_options_reminder_ghost;
        FrameLayout fl_delete, fl_reminder, fl_pin ,fl_delete_ghost, fl_reminder_ghost, fl_pin_ghost ,  fl_pin_icon_activated, fl_reminder_activated;
        FrameLayout fl_task_sub_completed, fl_remove_sub_task;
        FrameLayout fl_task_sub_completed_ghost;
        FrameLayout fl_item;


        public MyViewHolder_Task_Sub(@NonNull View itemView, Recycler_Tasks_Sub_In_Visualizer_Interface recyclerTasksSubInVisualizerInterface){
            super(itemView);
            task_sub_description_id = itemView.findViewById(R.id.Task_Sub_Description);
            fl_task_sub_completed = itemView.findViewById(R.id.Fl_Completed_Mark);
            fl_task_sub_completed_ghost = itemView.findViewById(R.id.Fl_Completed_Mark_Ghost);
            fl_remove_sub_task = itemView.findViewById(R.id.Fl_Remove_Sub_Task);

            activeTextWatcher_Sub_Task_Description = new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && recyclerTasksSubInVisualizerInterface != null) {
                        recyclerTasksSubInVisualizerInterface.Change_Sub_Task_Description(pos, s.toString());
                    }

                }
            };


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerTasksSubInVisualizerInterface != null){
                        //int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksSubInVisualizerInterface.onItemClick(pos,v);
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
            itemView.findViewById(R.id.Fl_Completed_Mark_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksSubInVisualizerInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksSubInVisualizerInterface.Mark_Sub_Task_As_Completed(pos);
                        }
                    }
                }
            });

            ///task_sub_description_id.addTextChangedListener(new TextWatcher() {
            ///    @Override
            ///    public void afterTextChanged(Editable s) {
            ///    }

            ///    @Override
            ///    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            ///    }

            ///    @Override
            ///    public void onTextChanged(CharSequence s, int start, int before, int count) {
            ///        int pos = getAbsoluteAdapterPosition();
            ///        if (pos != RecyclerView.NO_POSITION && recyclerTasksSubInVisualizerInterface != null) {
            ///            // Enviamos el nuevo texto y la posición a la interfaz
            ///            recyclerTasksSubInVisualizerInterface.Change_Sub_Task_Description(pos, s.toString());
            ///        }
            ///    }
            ///});
            itemView.findViewById(R.id.Fl_Remove_Sub_Task).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTasksSubInVisualizerInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTasksSubInVisualizerInterface.Remove_Item(pos);
                        }
                    }
                }
            });
        }

    }


}