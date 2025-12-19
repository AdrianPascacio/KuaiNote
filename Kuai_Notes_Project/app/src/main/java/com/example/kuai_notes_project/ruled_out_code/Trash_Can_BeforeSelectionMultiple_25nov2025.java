package com.example.kuai_notes_project.ruled_out_code;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.Adapter_Recycler_Trash_Can;
import com.example.kuai_notes_project.Body_Note_Preview;
import com.example.kuai_notes_project.DB_Notes;
import com.example.kuai_notes_project.Date_of_Note;
import com.example.kuai_notes_project.Note;
import com.example.kuai_notes_project.R;
import com.example.kuai_notes_project.Recycler_Trash_Can_Interface;
import com.example.kuai_notes_project.Wasted_Note_Visualizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

///354 V4, 461 V6, 411 V7, 442 V7.2,
public class Trash_Can_BeforeSelectionMultiple_25nov2025 extends AppCompatActivity implements Recycler_Trash_Can_Interface {
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list, noteOriginal_list;
    ArrayList<Boolean> selected_list, unselected_list;
    ArrayList<Note> noteList;
    ArrayList<Integer> previous_selected_list;

    DB_Notes DB_N;
    Body_Note_Preview BPN;
    Date_of_Note DoN;

    Adapter_Recycler_Trash_Can adapter;

    long start_of_today = 0;
    View fl_return, fl_back_ghost;
    TextView tv_empty_label;
    Animation Animation_empty_label;

    private int penultimate_Position = -1, antepenultimate_Position = -1;
    private boolean repeated = false;

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        recyclerView = findViewById(R.id.Recycler_Trash_Can);
        //adapter = new Adapter_Recycler_Trash_Can(this, dateEdited_list,selected_list,noteList,unselected_list,this);
        recyclerView.setAdapter(adapter);

        penultimate_Position = -1;
        antepenultimate_Position = -1;
        Clear_Lists();
        Update_Recycler_View();

        if (noteList.isEmpty()){
            Show_Empty_Label();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash_can);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.light_brown_natural_trans));

        DB_N = new DB_Notes(this);

        BPN = new Body_Note_Preview();
        DoN = new Date_of_Note();


        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        unselected_list = new ArrayList<>();
        previous_selected_list = new ArrayList<>();

        tv_empty_label = findViewById(R.id.TV_Label_Empty_TrashCan);
        Animation_empty_label = AnimationUtils.loadAnimation(this,R.anim.label_empty_animation);

        fl_return = findViewById(R.id.FrameLayout_Return);
        fl_back_ghost = findViewById(R.id.fl_Back_Ghost);
        fl_back_ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    Return_To_Memo_Board();
            }
        });

    }

    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
    }

    private void Update_Recycler_View(){
        try (Cursor cursor_Notes = DB_N.get_All_Notes_Of_Trash()) {
            if(cursor_Notes.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx = cursor_Notes.getColumnIndex("_id");
                int date_indx = cursor_Notes.getColumnIndex("date");
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");
                int pin_indx = cursor_Notes.getColumnIndex("pin");
                int reminder_indx = cursor_Notes.getColumnIndex("reminder");
                int reminder_type_indx = cursor_Notes.getColumnIndex("reminder_type");
                int reminder_interval_indx = cursor_Notes.getColumnIndex("reminder_interval");
                while (cursor_Notes.moveToNext()){
                    Note note = new Note(cursor_Notes.getLong(id_indx),
                            cursor_Notes.getLong(date_indx),
                            cursor_Notes.getString(title_indx),
                            BPN.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                                    cursor_Notes.getString(note_indx),
                                    115,
                                    100,
                                    0,
                                    5,
                                    1,
                                    30),
                            cursor_Notes.getInt(pin_indx)== 1,
                            cursor_Notes.getLong(reminder_indx),
                            cursor_Notes.getInt(reminder_type_indx),
                            cursor_Notes.getInt(reminder_interval_indx));
                    //!!---falta una lista para el expire day
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.getDate(),start_of_today));
                    noteOriginal_list.add(cursor_Notes.getString(note_indx));
                    selected_list.add(false);
                    noteList.add(note);
                    unselected_list.add(false);
                }
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(RecyclerView.SCROLL_STATE_DRAGGING == newState){
                    if(penultimate_Position != -1){
                        if(selected_list.get(penultimate_Position)== true){
                            Toast.makeText(Trash_Can_BeforeSelectionMultiple_25nov2025.this, "arras 1", Toast.LENGTH_SHORT).show();
                            selected_list.set(penultimate_Position,false);
                            unselected_list.set(penultimate_Position,true);
                            adapter.notifyItemChanged(penultimate_Position);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(antepenultimate_Position == penultimate_Position){
                                        unselected_list.set(penultimate_Position,false);
                                        adapter.notifyItemChanged(penultimate_Position,this);

                                    }else{
                                        unselected_list.set(penultimate_Position,false);
                                        adapter.notifyItemChanged(penultimate_Position,this);
                                        if(antepenultimate_Position != -1){
                                            unselected_list.set(antepenultimate_Position,false);
                                            adapter.notifyItemChanged(antepenultimate_Position,this);
                                        }
                                    }
                                    //unselected_list.set(penultimate_Position,false);
                                    //adapter.notifyItemChanged(penultimate_Position,this);
                                    //if(antepenultimate_Position != -1){
                                    //    unselected_list.set(antepenultimate_Position,false);
                                    //    adapter.notifyItemChanged(antepenultimate_Position,this);
                                    //}
                                    penultimate_Position = -1;
                                    antepenultimate_Position = -1;
                                    previous_selected_list.clear();
                                }
                            }, 1000); // Realiza accion luego de 500 milisegundos
                        }
                    }
                    if(repeated){
                        for ( int i = 0; i < unselected_list.size(); i++ ){
                            if(unselected_list.get(i) == true){
                                Log.d("Unselected","en Unselected list: falseado ###### "+i+" -> " +unselected_list.get(i));   unselected_list.set(i,false);
                                adapter.notifyItemChanged(i,this);
                            }
                        }

                    }
                    //if(antepenultimate_Position != -1){
                    //    if(unselected_list.get(antepenultimate_Position) == true){
                    //        Toast.makeText(Trash_Can.this, "arras 2", Toast.LENGTH_SHORT).show();
                    //        unselected_list.set(antepenultimate_Position,false);
                    //        //unselected_list.set(prev_selectedPosition,false);
                    //        adapter.notifyItemChanged(antepenultimate_Position,this);
                    //    }
                    //    antepenultimate_Position = -1;       //!! Test
                    //}

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //Original
        //recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

        //    @Override
        //    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        //        super.onScrollStateChanged(recyclerView, newState);
        //        if(RecyclerView.SCROLL_STATE_DRAGGING == newState){
        //            if(penultimate_Position != -1){
        //                if(selected_list.get(penultimate_Position)== true){
        //                    Toast.makeText(Trash_Can.this, "arrastrando", Toast.LENGTH_SHORT).show();
        //                    selected_list.set(penultimate_Position,false);
        //                    unselected_list.set(penultimate_Position,true);
        //                    adapter.notifyItemChanged(penultimate_Position);

        //                    new Handler().postDelayed(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            unselected_list.set(penultimate_Position,false);
        //                            adapter.notifyItemChanged(penultimate_Position,this);
        //                            penultimate_Position = -1;
        //                            previous_selected_list.clear();
        //                        }
        //                    }, 500); // Realiza accion luego de 300 milisegundos
        //                }
        //            }
        //            if(antepenultimate_Position != -1){
        //                if(unselected_list.get(antepenultimate_Position)== true){
        //                    Toast.makeText(Trash_Can.this, "arrastrando", Toast.LENGTH_SHORT).show();
        //                    unselected_list.set(antepenultimate_Position,false);
        //                    //unselected_list.set(prev_selectedPosition,false);
        //                    adapter.notifyItemChanged(antepenultimate_Position,this);
        //                }
        //            }

        //        }
        //    }

        //    @Override
        //    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        //        super.onScrolled(recyclerView, dx, dy);
        //    }
        //});
    }
    private void Clear_Lists(){
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty()){
            return;
        }
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
        unselected_list.clear();
        previous_selected_list.clear();
    }

    @Override
    public void onItemClick(int position, View v) {
        if(penultimate_Position != -1){       //--size 2 : current and just unselected:
            unselected_list.set(penultimate_Position,false);
            penultimate_Position = -1;
        }
        if(antepenultimate_Position != -1){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(antepenultimate_Position,false);
            antepenultimate_Position = -1;
        }
        Note _note = noteList.get(position);
        Intent goTo = new Intent(this, Wasted_Note_Visualizer.class);
        goTo.putExtra("send_date_of_note",_note.getDate());
        goTo.putExtra("send_note_id",_note.getNote_id());
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Override
    public void onItemHold(int position,View v) {

        onItemHold_4(position);

    }
    private void onItemHold_4(int position) {
        repeated = position == penultimate_Position;
        if(repeated){
            selected_list.set(position,!selected_list.get(position));// invert value
            if(selected_list.get(position) == false){
                unselected_list.set(position,false);
            }
        }else{
            //--Si previo esta activado entonces desactivar
            if(penultimate_Position != -1){
                boolean previousIsSelected = selected_list.get(penultimate_Position);
                if(previousIsSelected){
                    selected_list.set(penultimate_Position,false);
                    adapter.notifyItemChanged(penultimate_Position);
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        previous_selected_list.add(0, position);
        Log_Unselected();

        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(penultimate_Position);

            if(repeated){
                antepenultimate_Position = -1;
                previous_selected_list.clear();
            }
        }

        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(antepenultimate_Position,false);
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(antepenultimate_Position,this);


            antepenultimate_Position = -1;
            previous_selected_list.remove(2);

            if(repeated){
                previous_selected_list.clear();
            }
        }

        if(repeated){
            adapter.notifyItemChanged(antepenultimate_Position);
            antepenultimate_Position = -1;
        }else{
            antepenultimate_Position = penultimate_Position;
        }

        penultimate_Position = position;

        for ( int i = 0; i < unselected_list.size(); i++ ){
            if(unselected_list.get(i) == true) Log.d("Unselected","en Unselected list: "+i+" -> " +unselected_list.get(i));
            if(selected_list.get(i) == true) Log.d("Unselected","  en Selected list: "+i+" -> " +selected_list.get(i));
        }
    }
    private void Log_Unselected() {
        Log.d("Unselected","////////////////////////");
        if(previous_selected_list.size()==2){
            if(penultimate_Position == previous_selected_list.get(1)){
                Log.d("Unselected" , "###penu: equal: " +penultimate_Position );
            }else{
                Log.d("Unselected" , "---diffpenu: " +penultimate_Position + "   prev_list_1: "+previous_selected_list.get(1));
            }
        }else if(penultimate_Position != -1){
            Log.d("Unselected" , "-------------penultimo: " +penultimate_Position );
        }
        if(previous_selected_list.size()==3){
            if(penultimate_Position == previous_selected_list.get(1)){
                Log.d("Unselected" , "###penu: equal: " +penultimate_Position );
            }else{
                Log.d("Unselected" , "---diffpenu: " +penultimate_Position + "   prev_list_1: "+previous_selected_list.get(1));
            }
            if(antepenultimate_Position == previous_selected_list.get(2)){
                Log.d("Unselected" , "$$$ante: equal: " +antepenultimate_Position );
            }else{
                Log.d("Unselected" , "---diffante: " +antepenultimate_Position + "   prev_list_1: "+previous_selected_list.get(2));
            }
        }else if(antepenultimate_Position != -1){
            Log.d("Unselected" , "-------------antepenultimo: " +antepenultimate_Position );
        }
        for ( int i : previous_selected_list){
            Log.d("Unselected","en lista: " +i);
        }
        if (!(previous_selected_list.size() >= 2 & penultimate_Position != -1)) {
            Log.d("Unselected","----------------------------previus_2 diff");
        }
        if (!(previous_selected_list.size() == 3 & antepenultimate_Position != -1)) {
            Log.d("Unselected","-------------------------------------previus_3 diff");
        }
    }


    private void onItemHold_3(int position) {
        if(position == penultimate_Position){
            repeated = true;
            selected_list.set(position,!selected_list.get(position));// invert value
            if(selected_list.get(position) == false) unselected_list.set(position,false);
        }else{
            repeated = false;
            //--Si previo esta activado entonces desactivar
            if(penultimate_Position != -1){
                boolean previousIsSelected = selected_list.get(penultimate_Position);
                if(previousIsSelected){
                    selected_list.set(penultimate_Position,false);
                    adapter.notifyItemChanged(penultimate_Position);
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        previous_selected_list.add(0, position);
        Log_Unselected();

        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(penultimate_Position);

            if(position == penultimate_Position){
                penultimate_Position = -1;
                antepenultimate_Position = -1;
                previous_selected_list.clear();
            }
        }

        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(antepenultimate_Position,false);
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(antepenultimate_Position,this);


            antepenultimate_Position = -1;
            previous_selected_list.remove(2);

            if(position == penultimate_Position){
                penultimate_Position = -1;
                previous_selected_list.clear();
            }
        }

        if(repeated){
            adapter.notifyItemChanged(antepenultimate_Position);
            penultimate_Position = -1;
            antepenultimate_Position = -1;
        }else{
            antepenultimate_Position = penultimate_Position;
        }

        penultimate_Position = position;

        for ( int i = 0; i < unselected_list.size(); i++ ){
            if(unselected_list.get(i) == true) Log.d("Unselected","en Unselected list: "+i+" -> " +unselected_list.get(i));
            if(selected_list.get(i) == true) Log.d("Unselected","  en Selected list: "+i+" -> " +selected_list.get(i));
        }
    }


    private void onItemHold_2(int position) {

        if(position == penultimate_Position){
            repeated = true;
            selected_list.set(position,!selected_list.get(position));// invert value
            if(selected_list.get(position) == false) unselected_list.set(position,false);
        }else{
            repeated = false;
            //--Si previo esta activado entonces desactivar
            if(penultimate_Position != -1){
                boolean previousIsSelected = selected_list.get(penultimate_Position);
                if(previousIsSelected){
                    selected_list.set(penultimate_Position,false);
                    adapter.notifyItemChanged(penultimate_Position);
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        Set_Unselected_List_3(position);

        if(position == penultimate_Position){
            adapter.notifyItemChanged(antepenultimate_Position);
            penultimate_Position = -1;
            antepenultimate_Position = -1;
        }else{
            antepenultimate_Position = penultimate_Position;
        }
        penultimate_Position = position;

        for ( int i = 0; i < unselected_list.size(); i++ ){
            if(unselected_list.get(i) == true) Log.d("Unselected","en Unselected list: "+i+" -> " +unselected_list.get(i));
            if(selected_list.get(i) == true) Log.d("Unselected","  en Selected list: "+i+" -> " +selected_list.get(i));
        }

    }

    private void Set_Unselected_List_3(int position) {//    remplazar list por variables antepenultimo y penultimo
        previous_selected_list.add(0, position);
        Log_Unselected();

        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(penultimate_Position);

            if(position == penultimate_Position){
                penultimate_Position = -1;
                antepenultimate_Position = -1;
                previous_selected_list.clear();
            }
        }
        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(antepenultimate_Position,false);
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(antepenultimate_Position,this);


            antepenultimate_Position = -1;
            previous_selected_list.remove(2);

            if(position == penultimate_Position){
                penultimate_Position = -1;
                previous_selected_list.clear();
            }
            return;
        }
    }
    private void onItemHold_Original(int position) {

        if(position == penultimate_Position){
            selected_list.set(position,!selected_list.get(position));// invert value
        }else{
            //--Si previo esta activado entonces desactivar
            if(penultimate_Position != -1){
                boolean previousIsSelected = selected_list.get(penultimate_Position);
                if(previousIsSelected){
                    selected_list.set(penultimate_Position,false);
                    adapter.notifyItemChanged(penultimate_Position);
                }
            }
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        Set_Unselected_List_3(position);

        antepenultimate_Position = penultimate_Position;
        penultimate_Position = position;

    }
    private void Set_Unselected_List_2(int position) {//    remplazar list por variables antepenultimo y penultimo
        previous_selected_list.add(0, position);
        Log_Unselected();

        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(antepenultimate_Position,false);
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(antepenultimate_Position,this);


            antepenultimate_Position = -1;
            previous_selected_list.remove(2);

            if(position == penultimate_Position){
                penultimate_Position = -1;
                previous_selected_list.clear();
            }
            return;
        }
        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(penultimate_Position);

            if(position == penultimate_Position){
                penultimate_Position = -1;
                antepenultimate_Position = -1;
                previous_selected_list.clear();
            }
        }
    }


    private void Set_Unselected_List(int position) {
        previous_selected_list.add(0, position);
        Log_Unselected();
        for ( int i : previous_selected_list){
            Log.d("Unselected","en lista: " +i);
        }

        if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
            unselected_list.set(previous_selected_list.get(1),true);
            adapter.notifyItemChanged(previous_selected_list.get(1));

            boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));
            if(current_eq_previous){
                previous_selected_list.clear();
            }
            return;
        }

        if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
            unselected_list.set(previous_selected_list.get(2),false);
            unselected_list.set(previous_selected_list.get(1),true);
            adapter.notifyItemChanged(previous_selected_list.get(2),this);

            boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));

            previous_selected_list.remove(2);

            if(current_eq_previous){
                previous_selected_list.clear();
                return;
            }
        }
    }

    private void itemHold_2(int position) {
        if(position == penultimate_Position){
            selected_list.set(position,!selected_list.get(position));
        }else{
            selected_list.set(position,true);
        }

        adapter.notifyItemChanged(position);

        if(penultimate_Position != -1){
            selected_list.set(penultimate_Position,false);
            unselected_list.set(penultimate_Position,true);
            adapter.notifyItemChanged(penultimate_Position);
        }
        if(antepenultimate_Position != -1){
            unselected_list.set(antepenultimate_Position,false);
        }
        if(position == penultimate_Position){
            if(antepenultimate_Position != -1){
                adapter.notifyItemChanged(antepenultimate_Position);
            }
            penultimate_Position = -1;
            antepenultimate_Position = -1;
        }else{
            antepenultimate_Position = penultimate_Position;
            penultimate_Position = position;
        }
    }


    /// Recycle Items:
    @Override
    public void RecycleItem(int position) {
        Note _note = noteList.get(position);
        if(DB_N.Recycle_Note(_note.getNote_id())){
            Remove_Item_From_ArraysLists(position);
        }
    }

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);
        if(DB_N.Delete_Hard_Specific_Note(_note.getNote_id())) {
            Remove_Item_From_ArraysLists(position);
        }
    }

    private void Remove_Item_From_ArraysLists(int position) {
        dateEdited_list.remove(position);
        noteOriginal_list.remove(position);
        noteList.remove(position);
        selected_list.remove(position);
        adapter.notifyItemRemoved(position);

        if(penultimate_Position != -1){
            unselected_list.set(penultimate_Position,false);
        }

        unselected_list.remove(position);

        //Previous selection must be equal to -1
        penultimate_Position = -1;
        antepenultimate_Position = -1;
        previous_selected_list.clear();

        //----- verify if is empty:
        if (noteList.isEmpty()){
            Show_Empty_Label();
        }
    }

    private void Show_Empty_Label() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_empty_label.setVisibility(View.VISIBLE);
                tv_empty_label.startAnimation(Animation_empty_label);
            }
        }, 250);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
            }
        }, 1450);
    }

    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
    }
}