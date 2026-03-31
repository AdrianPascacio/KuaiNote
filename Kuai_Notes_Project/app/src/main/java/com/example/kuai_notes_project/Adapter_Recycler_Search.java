package com.example.kuai_notes_project;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import static com.google.android.material.color.MaterialColors.getColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
public class Adapter_Recycler_Search extends RecyclerView.Adapter<Adapter_Recycler_Search.MyViewHolder> {
    private Context context;
    private ArrayList<Boolean> selected_id;
    private ArrayList<String> title;
    private ArrayList<String> note;
    private ArrayList<String> snipped_note;
    //private ArrayList<Note> noteList;

    private final Recycler_Search_Interface recycler_search_interface;
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

    public Adapter_Recycler_Search(Context context,  ArrayList<Boolean> selected_id, ArrayList <String> Title_List, ArrayList <String> NoteContent_List, ArrayList <String> Snipped_Note_List, Recycler_Search_Interface recyclerSearchInterface){
        this.context = context;
        this.title = Title_List;
        this.note = NoteContent_List;
        this.note = NoteContent_List;
        this.snipped_note = Snipped_Note_List;
        //this.noteList = noteList;
        this.recycler_search_interface =recyclerSearchInterface ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_search,parent,false);
        return new MyViewHolder(v, recycler_search_interface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position){
        //Note note = noteList.get(position);

        //Animation Animation_Pin_Gray_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycler_appear_trashcan);
        //Animation Animation_Pin_Gray_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycler_appear_trashcan_invert);
        //Animation Animation_TrashCan_Appear = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fire_appear_trashcan);
        //Animation Animation_TrashCan_Appear_invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fire_appear_trashcan_invert);
        //Animation Animation_Extend = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.extend_item);
        //Animation Animation_Extend_Invert = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.extend_item_invert);

        //------Title Visibility depending on emptiness:
        ////if((!note.title.isEmpty())){
        ////    holder.title_id.setVisibility(View.VISIBLE);
        ////    holder.title_id.setText(note.title);
        ////}else {
        ////    holder.title_id.setVisibility(View.GONE);
        ////}
        SpannableString spannableString ;
        if(!title.get(position).isEmpty()){
            holder.title_id.setVisibility(View.VISIBLE);
            //holder.note_preview_id.setText(note.get(position)+" \n " +snipped_note.get(position));
            String raw_snipped_title = title.get(position);
            spannableString = new SpannableString(raw_snipped_title);


            int start = raw_snipped_title.indexOf("[");
            while ( start != -1 ) {
                int end = raw_snipped_title.indexOf("]",start);
                if (end != -1){
                    //ForegroundColorSpan highlightSpan = new ForegroundColorSpan(Color.parseColor("#a015a0"));
                    //---Choosing color from R.color:
                    //!!--getResources().getColor esta deprecado. es necesario remplazarlo en el libro
                    ForegroundColorSpan highlightSpan = new ForegroundColorSpan(ContextCompat.getColor(holder.itemView.getContext(), R.color.ex_orange));

                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    ///spannableString.setSpan(highlightSpan,start,end + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(highlightSpan,start + 1, end , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(boldSpan,start + 1, end , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(0f), start, start + 1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(0f), end, end + 1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    start = raw_snipped_title.indexOf("[", end + 1);
                }else{
                    break;
                }
            }

            ///holder.note_preview_id.setText(snipped_note.get(position));
            holder.title_id.setText(spannableString);
            //Log.d("Adapter","visible note: " + note.get(position) + "\n    note size: " +note.size());
            Log.d("Adapter","visible snipped_note: " + snipped_note.get(position));
            //holder.title_id.setText(title.get(position));
            Log.d("Adapter","visible title: " +title.get(position) + "\n    title size: " +title.size());
        }else {
            holder.title_id.setVisibility(View.GONE);
            Log.d("Adapter","gone title: ");
        }
        if(!note.get(position).isEmpty()){

            holder.note_preview_id.setVisibility(View.VISIBLE);
            //holder.note_preview_id.setText(note.get(position)+" \n " +snipped_note.get(position));
            String raw_snipped_note = snipped_note.get(position);
            spannableString = new SpannableString(raw_snipped_note);


            int start = raw_snipped_note.indexOf("[");
            while ( start != -1 ) {
                int end = raw_snipped_note.indexOf("]",start);
                if (end != -1){
                    //ForegroundColorSpan highlightSpan = new ForegroundColorSpan(Color.parseColor("#a015a0"));
                    //---Choosing color from R.color:
                    //!!--getResources().getColor esta deprecado. es necesario remplazarlo en el libro
                    ForegroundColorSpan highlightSpan = new ForegroundColorSpan(ContextCompat.getColor(holder.itemView.getContext(), R.color.ex_orange));

                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    ///spannableString.setSpan(highlightSpan,start,end + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(highlightSpan,start + 1, end , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(boldSpan,start + 1, end , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(0f), start, start + 1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(0f), end, end + 1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    start = raw_snipped_note.indexOf("[", end + 1);
                }else{
                    break;
                }
            }

            ///holder.note_preview_id.setText(snipped_note.get(position));
            holder.note_preview_id.setText(spannableString);
            //Log.d("Adapter","visible note: " + note.get(position) + "\n    note size: " +note.size());
            Log.d("Adapter","visible snipped_note: " + snipped_note.get(position));

        }else {

            holder.note_preview_id.setVisibility(View.GONE);
            Log.d("Adapter","gone note: ");

        }

        //Log.d("Adapter","pending_deactivation: "+pending_deactivation + "   position: "+position);

        //holder.date_id.setText(String.valueOf(date_id.get(position)));
        //holder.note_preview_id.setText(note.note);

        ////------Visibility depending if it is Selected:
        //if(selected_id.get(position)==true){
        //    holder.note_preview_id.setMaxLines(5);
        //    holder.fl_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_background_trashcan_selected)));

        //    holder.note_preview_id.setPadding(0,0,0,14);
        //    //holder.fl_item.startAnimation(Animation_Extend);
        //    holder.date_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_date_selected));
        //    holder.note_preview_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_note_preview_selected));
        //    holder.title_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_visualizer_title_selected));

        //    if(multi_selection_state){
        //        if(multi_first_count > 0) {
        //            Unselecting_View(holder, Animation_Pin_Gray_Appear_invert, Animation_TrashCan_Appear_invert);
        //            multi_first_count--;
        //        }
        //        holder.layout_btn_options.setVisibility(View.GONE);

        //    }else{
        //        Selecting_View(holder, Animation_Pin_Gray_Appear, Animation_TrashCan_Appear);
        //    }

        //}else{
        //    holder.note_preview_id.setMaxLines(3);
        //    Not_Selected_View(holder,Animation_Extend_Invert);

        //    if(is_repeated){
        //        Unselecting_View(holder, Animation_Pin_Gray_Appear_invert, Animation_TrashCan_Appear_invert);
        //        is_repeated = false;
        //        pending_deactivation = position;
        //        Log.d("Adapter","       Setting- pending_deactivation: "+pending_deactivation + "   position: "+position);
        //    }
        //}

        //if (!multi_selection_state){
        //    multi_first_count = 2;
        //}

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
        return title.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date_id, title_id, note_preview_id;
        View layout_btn_options, layout_btn_options_ghost, layout_global_item;
        FrameLayout fl_delete, fl_pin , fl_delete_ghost, fl_pin_ghost , framelayout_pin_icon;
        FrameLayout fl_item;
        View layout_space;


        public MyViewHolder(@NonNull View itemView, Recycler_Search_Interface recyclerSearchInterface){
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
                    if (recyclerSearchInterface != null){
                        //!!int pos = getAdapterPosition();
                        int pos = getAbsoluteAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerSearchInterface.onItemClick(pos, v);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    if (recyclerSearchInterface != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerSearchInterface.onItemHold(pos, v);
                            return true;
                        }
                    }
                    return false;
                }
            });
            ///itemView.findViewById(R.id.FL_Item_Delete_Ghost).setOnClickListener(new View.OnClickListener(){
            ///    @Override
            ///    public void onClick(View v){
            ///        if (recyclerSearchInterface != null){
            ///            int pos = getAbsoluteAdapterPosition();
            ///            if (pos != RecyclerView.NO_POSITION){
            ///                recyclerSearchInterface.RemoveItem(pos);
            ///            }
            ///        }
            ///    }
            ///});
            ///itemView.findViewById(R.id.Fl_Item_Pin_Ghost).setOnClickListener(new View.OnClickListener(){
            ///    @Override
            ///    public void onClick(View v){
            ///        if (recyclerSearchInterface != null){
            ///            int pos = getAbsoluteAdapterPosition();
            ///            if (pos != RecyclerView.NO_POSITION){
            ///                recyclerSearchInterface.RecycleItem(pos);
            ///            }
            ///        }
            ///    }
            ///});
        }

    }
}