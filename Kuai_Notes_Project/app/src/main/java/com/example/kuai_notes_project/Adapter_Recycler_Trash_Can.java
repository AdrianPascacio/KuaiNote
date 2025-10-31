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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/// 168 V5, 190 V6, 186 V7
public class Adapter_Recycler_Trash_Can extends RecyclerView.Adapter<Adapter_Recycler_Trash_Can.MyViewHolder> {
    private Context context;
    private ArrayList date_id;
    private ArrayList<Boolean> selected_id;
    private ArrayList<Note> noteList;
    private ArrayList<Boolean> unselected_id;

    private final Recycler_Trash_Can_Interface recycler_trash_can_interface;

    public Adapter_Recycler_Trash_Can(Context context, ArrayList date_id, ArrayList<Boolean> selected_id, ArrayList noteList,ArrayList unselected_id, Recycler_Trash_Can_Interface recyclerTrashCanInterface){
        this.context = context;
        this.date_id = date_id;
        this.selected_id = selected_id;
        this.noteList = noteList;
        this.unselected_id = unselected_id;
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

        //------Title Visibility depending on emptiness:
        if((!note.title.isEmpty())){
            holder.title_id.setVisibility(View.VISIBLE);
            holder.title_id.setText(note.title);
        }else{
            holder.title_id.setVisibility(View.GONE);
        }

        //------Visibility depending if it is Selected:
        if(selected_id.get(position)==true){
            holder.note_id.setPadding(0,0,0,14);
            holder.date_id.setTextColor(Color.parseColor("#717171"));
            holder.note_id.setTextColor(Color.parseColor("#616161"));
            holder.title_id.setTextColor(Color.parseColor("#454545"));


            //Button Layout Visibility:
            holder.fl_pin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5CAD6D")));
            holder.fl_pin.startAnimation(Animation_Pin_Gray_Appear);
            holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A12015")));
            holder.fl_delete.setAnimation(Animation_TrashCan_Appear);
            holder.layout_btn_options.setVisibility(View.VISIBLE);
            holder.layout_btn_options_ghost.setVisibility(View.VISIBLE);

            holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#84FDF1E8")));

        }else{
            holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008F8F8F")));

            //Button Layout Visibility:
            holder.layout_btn_options.setVisibility(View.GONE);
            holder.layout_btn_options_ghost.setVisibility(View.GONE);

            holder.note_id.setPadding(0,0,0,0);
            holder.date_id.setTextColor(Color.parseColor("#787777"));
            holder.note_id.setTextColor(Color.parseColor("#686868"));
            holder.title_id.setTextColor(Color.parseColor("#616161"));

            if(unselected_id.get(position)==true){
                holder.layout_btn_options.setVisibility(View.VISIBLE);
                Log.d("Adapter TrashCan",  " :unselected - position: " + position);
                //holder.title_id.setTextColor(Color.parseColor("#57e8a3"));

                holder.fl_pin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));
                holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));
                holder.fl_pin.startAnimation(Animation_Pin_Gray_Appear_invert);
                holder.fl_delete.setAnimation(Animation_TrashCan_Appear_invert);
            }
        }

        holder.date_id.setText(String.valueOf(date_id.get(position)));
        holder.note_id.setText(note.note);
    }

    @Override
    public int getItemCount(){
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date_id, title_id, note_id;
        View layout_btn_options, layout_btn_options_ghost, layout_global_item;
        FrameLayout fl_delete, fl_pin , fl_delete_ghost, fl_pin_ghost , framelayout_pin_icon;
        FrameLayout fl_item;


        public MyViewHolder(@NonNull View itemView, Recycler_Trash_Can_Interface recyclerTrashCanInterface){
            super(itemView);
            date_id = itemView.findViewById(R.id.Text_Note_Date);
            title_id = itemView.findViewById(R.id.Text_Note_Title);
            note_id = itemView.findViewById(R.id.Text_Note_Preview);
            layout_btn_options = itemView.findViewById(R.id.Layout_Item_Options);
            layout_btn_options_ghost = itemView.findViewById(R.id.Layout_Item_Options_Ghost);
            layout_global_item = itemView.findViewById(R.id.Layout_Global_Item);
            fl_delete = itemView.findViewById(R.id.FL_Item_Delete);
            fl_pin = itemView.findViewById(R.id.Fl_Item_Pin);
            fl_delete_ghost = itemView.findViewById(R.id.FL_Item_Delete_Ghost);
            fl_pin_ghost = itemView.findViewById(R.id.Fl_Item_Pin_Ghost);
            framelayout_pin_icon = itemView.findViewById(R.id.FrameLayout_Pin_Icon);
            fl_item = itemView.findViewById((R.id.Layout_Item));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerTrashCanInterface != null){
                        //!!int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTrashCanInterface.onItemClick(pos);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    if (recyclerTrashCanInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerTrashCanInterface.onItemHold(pos);
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