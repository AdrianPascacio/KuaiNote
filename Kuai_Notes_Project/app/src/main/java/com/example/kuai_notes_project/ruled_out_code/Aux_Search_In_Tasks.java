package com.example.kuai_notes_project.ruled_out_code;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.Adapter_Recycler_Search;
import com.example.kuai_notes_project.DB_Tasks;
import com.example.kuai_notes_project.R;
import com.example.kuai_notes_project.Recycler_Search_Interface;
import com.example.kuai_notes_project.Task_Visualizer;

import java.util.ArrayList;
import java.util.Objects;

public class Aux_Search_In_Tasks extends AppCompatActivity implements Recycler_Search_Interface {
    //----Encargado de busqueda lo mas eficiente y rapido posible
    //----SQLite FTS4
    //----Debounce (300ms) aprox
    //----Busqueda en segundo plano
    //----DiffUtil


    int Journal_Element_Type = 1;
    DB_Tasks DB_T;

    RecyclerView recyclerView;
    Adapter_Recycler_Search adapter;
    ArrayList<Long> id_List;
    ArrayList<Long> cursor_id_List;
    ArrayList<String> Title_List;
    ArrayList<String> NoteContent_List;
    ArrayList<String> Snipped_Note_List;
    ArrayList<String> Snipped_Title_List;
    ArrayList<Boolean> Selected_List;

    TextView et_searched_Text;

    int item_count = 0 ;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_aux_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] nameMonths;

        DB_T = new DB_Tasks(this);

        Selected_List = new ArrayList<>();
        Title_List = new ArrayList<>();
        NoteContent_List = new ArrayList<>();
        Snipped_Note_List = new ArrayList<>();
        Snipped_Title_List = new ArrayList<>();
        id_List = new ArrayList<>();

        cursor_id_List = new ArrayList<>();

        recyclerView = findViewById(R.id.Recycler_Search);
        //adapter = new Adapter_Recycler_Search(this,Journal_Element_Type,Selected_List,Title_List, NoteContent_List,Snipped_Note_List, this);
        adapter = new Adapter_Recycler_Search(this,Journal_Element_Type,Selected_List,Snipped_Title_List, NoteContent_List,Snipped_Note_List, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        et_searched_Text = findViewById(R.id.Searched_Text);


        //Clear_Lists();
        //Update_Recycler_View_originalValues();
        ///Update_Recycler_View_ftsValues();

        //!!--this is not a debounce:
        new Handler().postDelayed(new Runnable() {//Se enfoca en cuerpo de la nota y se abre el teclado solo si el texto es nuevo
            @Override
            public void run() {
                et_searched_Text.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //Abrir teclado luego de realizar el enfoque:
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(et_searched_Text, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 300); // Realiza accion luego de 300 milisegundos


        et_searched_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //!!-- si se desactiva el diffutil se necesita limpiar aqui:
                ///Clear_Lists();

                String searched_Text = et_searched_Text.getText().toString();
                //Update_Recycler_View_ftsValues_With_Snipped_Brute(searched_Text);
                //Update_Recycler_Simple_and_Snipped(searched_Text);
                //Update_Recycler_View_ftsValues(searched_Text);
                //Update_Recycler_View_ftsValues_Snipped(searched_Text);
                //Update_Recycler_View_ftsValues_Snipped2(searched_Text);

                Update_Recycler_View_ftsValues_Snipped3(searched_Text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Return_To_Memo_Board();
            }
        });
    }
    /// 133 lines:
    private void Update_Recycler_View_ftsValues_Snipped3(String searched_Text) {
        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
        //---agregar coincidencias progresivamente al eliminar algunas letras
        //---Limpiar las listas al no tener ninguna coincidencia
        //!!--Optimizar
        try (Cursor cursor_Tasks = DB_T.get_All_Tasks_fts(searched_Text)) {
            if(cursor_Tasks.getCount()==0 || et_searched_Text.getTextSize()==0){
                Log.d("2Search", "Zero : adapter itemcount:" + adapter.getItemCount());
                item_count = adapter.getItemCount();
                if(item_count > 0){
                    //adapter.notifyItemRemoved(0 );
                    Clear_Lists();
                    ///adapter.notifyItemRangeRemoved(0,item_count - 1);
                    ///adapter.notifyItemChanged(0);

                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    item_count = 0;
                }
            }else{
                int title_indx = cursor_Tasks.getColumnIndex("title");
                int note_indx = cursor_Tasks.getColumnIndex("note");

                while (cursor_Tasks.moveToNext()){

                    boolean is_duplicated = false;
                    is_duplicated = Refresh_Data_On_Existing_Notes(cursor_Tasks, title_indx, note_indx);

                    if(!is_duplicated){

                        id_List.add(cursor_Tasks.getLong(0));
                        //Title_List.add(cursor_Notes.getString(title_indx));
                        //if(cursor_Tasks.getString(3).lastIndexOf('[',1)==0){

                        //}
                        Title_List.add(cursor_Tasks.getString(title_indx));
                        NoteContent_List.add(cursor_Tasks.getString(note_indx));
                        if(cursor_Tasks.getString(3) != null){
                            Snipped_Note_List.add(cursor_Tasks.getString(3).trim());
                        }else{
                            Snipped_Note_List.add(cursor_Tasks.getString(3));
                        }
                        if(cursor_Tasks.getString(4) != null){
                            Snipped_Title_List.add(cursor_Tasks.getString(4).trim());
                        }else{
                            Snipped_Title_List.add(cursor_Tasks.getString(4));
                        }
                        ///Snipped_Title_List.add(cursor_Tasks.getString(4).trim());
                        Selected_List.add(false);
                        adapter.notifyItemInserted(0);
                        if(adapter.getItemCount() > 1){
                            adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
                        }
                        Log.d("2Search", "            Adding: Title: "+Title_List.get(Title_List.size()-1)+ "    note: "+ NoteContent_List.get(NoteContent_List.size()-1)+ "    snippet: "+ Snipped_Note_List.get(Snipped_Note_List.size()-1)+ "    title_snipped: "+ Snipped_Title_List.get(Snipped_Title_List.size()-1));

                        item_count ++;
                    }

                    //--Agregar id a list nueva del cursor:
                    cursor_id_List.add(cursor_Tasks.getLong(0));

                }
                //--Eliminar id_list que no esten en la nueva lista de cursor id list:
                for(int i = id_List.size() - 1 ;item_count > cursor_id_List.size() && i >= 0 ; i --){
                    Log.d("2Search", "        Loop Remove: item count: "+ item_count+ "    cursor count: "+ cursor_Tasks.getCount() + "    id_list: " + id_List.size());
                    boolean appear_in_cursor = false;
                    for(int j = cursor_id_List.size() - 1; j>=0 ; j--){
                        if(Objects.equals(id_List.get(i), cursor_id_List.get(j))){
                            appear_in_cursor = true;
                            break;
                        }
                    }
                    if(!appear_in_cursor){
                        Log.d("2Search", "            Removing: Title: "+Title_List.get(i)+ "    note: "+ Snipped_Note_List.get(i));
                        id_List.remove(i);
                        Title_List.remove(i);
                        NoteContent_List.remove(i);
                        Snipped_Note_List.remove(i);
                        Snipped_Title_List.remove(i);
                        Selected_List.remove(i);
                        adapter.notifyItemRemoved(i);
                        adapter.notifyItemChanged(i);

                        item_count --;
                    }
                }
                //--clear cursor_id_list:
                cursor_id_List.clear();
            }
        }
    }

    private boolean Refresh_Data_On_Existing_Notes(Cursor cursor_Notes, int title_indx, int note_indx) {
        boolean is_duplicated  = false;
        for(int i = id_List.size()-1; i>=0;i-- ){

            if(id_List.get(i) == cursor_Notes.getInt(0)){
                Log.d("2Search", "        Duplicated Refreshing: id_list:" + id_List.get(i) + "  cursor_id:" + cursor_Notes.getInt(0)+"\n            Title: "+Title_List.get(i)+ "    note: "+ Snipped_Note_List.get(i));
                is_duplicated = true;

                ///Title_List.set(i, cursor_Notes.getString(title_indx));
                Title_List.set(i, cursor_Notes.getString(title_indx));
                NoteContent_List.set(i, cursor_Notes.getString(note_indx));
                if(cursor_Notes.getString(3) != null){
                    Snipped_Note_List.set(i,cursor_Notes.getString(3).trim());
                }else{
                    Snipped_Note_List.set(i,cursor_Notes.getString(3));
                }
                if(cursor_Notes.getString(4) != null){
                    Snipped_Title_List.set(i,cursor_Notes.getString(4).trim());
                }else{
                    Snipped_Title_List.set(i,cursor_Notes.getString(4));
                }
                //Snipped_Note_List.set(i, cursor_Notes.getString(3));
                //Snipped_Title_List.set(i, cursor_Notes.getString(4));
                Selected_List.set(i, false);
                adapter.notifyItemChanged(i);

                break;
            }
        }
        return is_duplicated;
    }

    private void Clear_Lists() {
        //!!verify condition:
        if(id_List.isEmpty() && Selected_List.isEmpty()){
            return;
        }
        id_List.clear();
        Title_List.clear();
        NoteContent_List.clear();
        Selected_List.clear();
        Snipped_Note_List.clear();
        Snipped_Title_List.clear();
    }


    @Override
    public void onItemClick(int position, View v) {
        long _task_id = id_List.get(position);
        Intent goTo = new Intent(this, Task_Visualizer.class);
        goTo.putExtra("send_date_of_note",_task_id);
        goTo.putExtra("send_task_id",_task_id);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
        finish();
    }

    @Override
    public void onItemHold(int position, View v) {

    }

    @Override
    public void RemoveItem(int position) {

    }

    @Override
    public void RecycleItem(int position) {

    }
    public void Return_To_Memo_Board(){
        finish();
        //!!Actualizar animaciones para salir y entrar:
        overridePendingTransition(R.anim.return_activity_slide_right_in_to_search,R.anim.return_activity_slide_right_out_to_search);
    }
}
