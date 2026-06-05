package com.example.kuai_notes_project;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import static com.google.android.material.color.MaterialColors.getColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/// 168 V5, 190 V6, 186 V7
public class Adapter_Recycler_Search extends RecyclerView.Adapter<Adapter_Recycler_Search.MyViewHolder> {
    private int JOURNAL_ELEMENT_TYPE;
    private int NOTE_ELEMENT_TYPE = 0;
    private int TASK_ELEMENT_TYPE = 1;
    private int Journal_Element_Type;

    private Context context;
    private ArrayList<Boolean> selected_id;
    private ArrayList<String> title;
    private ArrayList<String> note;
    private ArrayList<String> snipped_note;
    //private ArrayList<Note> noteList;

    private final Recycler_Search_Interface recycler_search_interface;



    public Adapter_Recycler_Search(Context context, int JOURNAL_ELEMENT_TYPE, ArrayList<Boolean> selected_id, ArrayList <String> Title_List, ArrayList <String> NoteContent_List, ArrayList <String> Snipped_Note_List, Recycler_Search_Interface recyclerSearchInterface){
        this.context = context;
        this.JOURNAL_ELEMENT_TYPE = JOURNAL_ELEMENT_TYPE;
        this.title = Title_List;
        //this.note = NoteContent_List;
        if(JOURNAL_ELEMENT_TYPE == TASK_ELEMENT_TYPE){
            this.note = Snipped_Note_List;
        }else{
            this.note = NoteContent_List;
        }
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
        ///if(Journal_Element_Type == 0){
            if(title.get(position) != null && !title.get(position).isEmpty()){
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
            if(note.get(position) != null && !note.get(position).isEmpty()){

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
        }
    }
}