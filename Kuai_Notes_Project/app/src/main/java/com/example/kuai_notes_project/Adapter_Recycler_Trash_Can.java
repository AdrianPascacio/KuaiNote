package com.example.kuai_notes_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/// 168 V5, 190 V6, 186 V7
public class Adapter_Recycler_Trash_Can extends RecyclerView.Adapter<Adapter_Recycler_Trash_Can.MyViewHolder> {
    private Context context;
    private ArrayList date_id;
    private ArrayList<Boolean> selected_id;
    private ArrayList<Note> noteList;

    private final Recycler_Trash_Can_Interface recycler_trash_can_interface;
    private boolean multi_selection_state = false;
    private boolean is_repeated = false;
    private int pending_deactivation = -1;
    private int multi_first_count = 2;


/// Test:
    private int selected_in_single_mode = -1;

    public void Change_multi_selection_state (boolean multi_selection_state){
        this.multi_selection_state = multi_selection_state;
    }
    public void Change_is_repeated_value (boolean is_repeated){
        this.is_repeated = is_repeated;
    }

    public Adapter_Recycler_Trash_Can(Context context, ArrayList date_id, ArrayList<Boolean> selected_id, ArrayList noteList, Recycler_Trash_Can_Interface recyclerTrashCanInterface){
        this.context = context;
        this.date_id = date_id;
        this.selected_id = selected_id;
        this.noteList = noteList;
        this.recycler_trash_can_interface =recyclerTrashCanInterface ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_trash_can,parent,false);
        return new MyViewHolder(v, recycler_trash_can_interface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position){
        Note note = noteList.get(position);

        Animation Animation_Pin_Gray_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycler_appear_trashcan);
        Animation Animation_Pin_Gray_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycler_appear_trashcan_invert);
        Animation Animation_TrashCan_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fire_appear_trashcan);
        Animation Animation_TrashCan_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fire_appear_trashcan_invert);
        Animation Animation_Extend = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.extend_item);
        Animation Animation_Extend_Invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.extend_item_invert);

        //------Title Visibility depending on emptiness:
        if((!note.title.isEmpty())){
            holder.title_id.setVisibility(View.VISIBLE);
            holder.title_id.setText(note.title);
        }else {
            holder.title_id.setVisibility(View.GONE);
        }

        Log.d("Adapter","pending_deactivation: "+pending_deactivation + "   position: "+position);

        holder.date_id.setText(String.valueOf(date_id.get(position)));
        holder.note_preview_id.setText(note.note);

        //------Visibility depending if it is Selected:
        if(selected_id.get(position)==true){
            holder.note_preview_id.setMaxLines(5);
            holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_background_trashcan_selected)));

            holder.note_preview_id.setPadding(0,0,0,14);
            //holder.fl_item.startAnimation(Animation_Extend);
            holder.date_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_date_selected));
            holder.note_preview_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_note_preview_selected));
            holder.title_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_title_selected));

            if(multi_selection_state){
                if(multi_first_count > 0) {
                    Unselecting_View(holder, Animation_Pin_Gray_Appear_invert, Animation_TrashCan_Appear_invert);
                    multi_first_count--;
                }
                holder.layout_btn_options.setVisibility(View.GONE);

            }else{
                Selecting_View(holder, Animation_Pin_Gray_Appear, Animation_TrashCan_Appear);
            }

        }else{
            holder.note_preview_id.setMaxLines(3);
            Not_Selected_View(holder,Animation_Extend_Invert);

            if(is_repeated){
                Unselecting_View(holder, Animation_Pin_Gray_Appear_invert, Animation_TrashCan_Appear_invert);
                is_repeated = false;
                pending_deactivation = position;
                Log.d("Adapter","       Setting- pending_deactivation: "+pending_deactivation + "   position: "+position);
            }
        }

        if (!multi_selection_state){
            multi_first_count = 2;
        }

    }
    private static void Selecting_View(@NonNull MyViewHolder holder, Animation Animation_Pin_Gray_Appear, Animation Animation_TrashCan_Appear) {
        //Button Layout Visibility:
        holder.layout_btn_options.setVisibility(View.VISIBLE);
        holder.layout_btn_options_ghost.setVisibility(View.VISIBLE);

        holder.fl_pin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_icon_recycler_tint)));
        holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.fire_icon)));
        holder.fl_pin.startAnimation(Animation_Pin_Gray_Appear);
        holder.fl_delete.startAnimation(Animation_TrashCan_Appear);
    }

    private static void Not_Selected_View(@NonNull MyViewHolder holder, Animation Animation_Extend_Invert) {
        holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_background_trashcan_notselected)));

        //Button Layout Visibility:
        holder.layout_btn_options.setVisibility(View.GONE);
        holder.layout_btn_options_ghost.setVisibility(View.GONE);

        holder.note_preview_id.setPadding(0,0,0,0);
        holder.date_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_date_notselected));
        holder.note_preview_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_note_preview_notselected));
        holder.title_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_title_notselected));
    }

    private static void Unselecting_View(@NonNull MyViewHolder holder, Animation Animation_Pin_Gray_Appear_invert, Animation Animation_TrashCan_Appear_invert) {
        holder.layout_btn_options.setVisibility(View.GONE);

        //holder.fl_item.startAnimation(Animation_Extend_Invert);
        holder.fl_pin.clearAnimation();
        holder.fl_delete.clearAnimation();


        //Original:
        ///holder.layout_btn_options.setVisibility(View.VISIBLE);

        ///holder.fl_pin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));
        ///holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));
        ///holder.fl_pin.startAnimation(Animation_Pin_Gray_Appear_invert);
        ///holder.fl_delete.startAnimation(Animation_TrashCan_Appear_invert);
    }

    @Override
    public int getItemCount(){
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date_id, title_id, note_preview_id;
        View layout_btn_options, layout_btn_options_ghost, layout_global_item;
        FrameLayout fl_delete, fl_pin , fl_delete_ghost, fl_pin_ghost , framelayout_pin_icon;
        FrameLayout fl_item;
        View layout_space;


        public MyViewHolder(@NonNull View itemView, Recycler_Trash_Can_Interface recyclerTrashCanInterface){
            super(itemView);
            date_id = itemView.findViewById(R.id.Text_Note_Date);
            title_id = itemView.findViewById(R.id.Text_Note_Title);
            note_preview_id = itemView.findViewById(R.id.Text_Note_Preview);
            layout_btn_options = itemView.findViewById(R.id.Layout_Item_Options);
            layout_btn_options_ghost = itemView.findViewById(R.id.Layout_Item_Options_Ghost);
            layout_global_item = itemView.findViewById(R.id.Layout_Global_Item);
            fl_delete = itemView.findViewById(R.id.FL_Item_Delete);
            fl_pin = itemView.findViewById(R.id.Fl_Item_Pin);
            fl_delete_ghost = itemView.findViewById(R.id.FL_Item_Delete_Ghost);
            fl_pin_ghost = itemView.findViewById(R.id.Fl_Item_Pin_Ghost);
            framelayout_pin_icon = itemView.findViewById(R.id.FrameLayout_Pin_Icon);
            fl_item = itemView.findViewById((R.id.Layout_Item));
            layout_space = itemView.findViewById(R.id.Space_T_I);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerTrashCanInterface != null){
                        //!!int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTrashCanInterface.onItemClick(pos, v);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    if (recyclerTrashCanInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTrashCanInterface.onItemHold(pos, v);
                            return true;
                        }
                    }
                    return false;
                }
            });
            itemView.findViewById(R.id.FL_Item_Delete_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTrashCanInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTrashCanInterface.RemoveItem(pos);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.Fl_Item_Pin_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerTrashCanInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTrashCanInterface.RecycleItem(pos);
                        }
                    }
                }
            });
        }

    }
}