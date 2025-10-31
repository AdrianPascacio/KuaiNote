package com.example.kuai_notes_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.util.LogPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

///180 V3, 258 V6
public class Adapter_Recycler_Memo_Board extends RecyclerView.Adapter<Adapter_Recycler_Memo_Board.MyViewHolder> {
    private Context context;
    private ArrayList date_id;
    private ArrayList<Boolean> selected_id;
    private ArrayList<Note> noteList;
    private ArrayList<Boolean> unselected_id;

    private final Recycler_Memo_Board_Interface recycler_memo_board_interface;

    public Adapter_Recycler_Memo_Board(Context context, ArrayList date_id, ArrayList<Boolean> selected_id, ArrayList noteList, ArrayList unselected_id, Recycler_Memo_Board_Interface recyclerMemoBoardInterface){
        this.context = context;
        this.date_id = date_id;
        this.selected_id = selected_id;
        this.noteList = noteList;
        this.unselected_id = unselected_id;
        this.recycler_memo_board_interface =recyclerMemoBoardInterface ;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_memo_board,parent,false);
        return new MyViewHolder(v, recycler_memo_board_interface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position){
        Note note = noteList.get(position);
        boolean isPinned = note.pin ==1;
        Animation Animation_Pin_Orange_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_appear_memoboard);
        Animation Animation_Pin_Orange_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_appear_memoboard_invert);
        Animation Animation_Pin_Gray_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_gray_appear_memoboard);
        Animation Animation_Pin_Gray_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.pin_gray_appear_memoboard_invert);
        Animation Animation_TrashCan_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.trashcan_appear_memoboard);
        Animation Animation_TrashCan_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.trashcan_appear_memoboard_invert);

        //------Title Visibility depending on emptiness:
        if((!note.title.isEmpty())){
            holder.title_id.setVisibility(View.VISIBLE);
            holder.title_id.setText(note.title);

            holder.date_id.setPadding(0,0,0,0);
        }else{
            holder.title_id.setVisibility(View.GONE);
            holder.date_id.setPadding(0,10,0,0);
            holder.note_id.setPadding(0,0,0,0);

        }

        //------Visibility depending if it is Selected:
        if(selected_id.get(position)==true){
            //note_id padding button14dp if is long clicked
            holder.note_id.setPadding(0,0,0,14);
            holder.date_id.setTextColor(Color.parseColor("#717171"));
            holder.note_id.setTextColor(Color.parseColor("#616161"));
            holder.title_id.setTextColor(Color.parseColor("#454545"));

            //Button Layout Visibility:
            holder.layout_btn_options.setVisibility(View.VISIBLE);
            holder.layout_btn_options_ghost.setVisibility(View.VISIBLE);
            holder.fl_delete.setAnimation(Animation_TrashCan_Appear);
            holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A12015")));

            //Pin Visibility
            if(isPinned){
                holder.fl_pin.setVisibility(View.GONE);
                //Pin Icon Visibility
                    holder.framelayout_pin_icon.setVisibility(View.VISIBLE);
                    holder.framelayout_pin_icon.setAnimation(Animation_Pin_Orange_Appear);
            }else{
                holder.fl_pin.setVisibility(View.VISIBLE);
                //holder.fl_pin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
                holder.fl_pin.setAnimation(Animation_Pin_Gray_Appear);

                //Pin Icon Visibility
                holder.framelayout_pin_icon.setVisibility(View.GONE);
            }


            holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AEFDF2D8")));

        }else{
            holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008F8F8F")));

            //Button Layout Visibility:
            holder.layout_btn_options.setVisibility(View.GONE);
            holder.layout_btn_options_ghost.setVisibility(View.GONE);

            //Pin Icon Visibility
            if(isPinned){
                holder.framelayout_pin_icon.setVisibility(View.VISIBLE);
                if(unselected_id.get(position)==true){
                    holder.framelayout_pin_icon.setAnimation(Animation_Pin_Orange_Appear_invert);

                    holder.layout_btn_options.setVisibility(View.VISIBLE);
                    Log.d("Adapter Memo",  " :pinned: " + position);
                    holder.fl_delete.setAnimation(Animation_TrashCan_Appear_invert);
                    holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));
                }
            }else{
                holder.framelayout_pin_icon.setVisibility(View.GONE);
                if(unselected_id.get(position)==true){
                    holder.layout_btn_options.setVisibility(View.VISIBLE);
                    Log.d("Adapter Memo",  " :unpinned: " + position);
                    holder.fl_delete.setAnimation(Animation_TrashCan_Appear_invert);
                    holder.fl_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#686868")));
                }
            }

            holder.fl_pin.setVisibility(View.VISIBLE);
            holder.fl_pin.setAnimation(Animation_Pin_Gray_Appear_invert);

            holder.note_id.setPadding(0,0,0,0);
            holder.date_id.setTextColor(Color.parseColor("#787777"));
            holder.note_id.setTextColor(Color.parseColor("#686868"));
            holder.title_id.setTextColor(Color.parseColor("#616161"));
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
        FrameLayout fl_delete, fl_pin ,fl_delete_ghost, fl_pin_ghost ,  framelayout_pin_icon;
        FrameLayout fl_item;


        public MyViewHolder(@NonNull View itemView, Recycler_Memo_Board_Interface recyclerMemoBoardInterface){
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
                    if (recyclerMemoBoardInterface != null){
                        //!!int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerMemoBoardInterface.onItemClick(pos);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    if (recyclerMemoBoardInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerMemoBoardInterface.onItemHold(pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
            itemView.findViewById(R.id.FL_Item_Delete_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerMemoBoardInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerMemoBoardInterface.RemoveItem(pos);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.Fl_Item_Pin_Ghost).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (recyclerMemoBoardInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerMemoBoardInterface.PinItem(pos);
                        }
                    }
                }
            });
        }

    }

}