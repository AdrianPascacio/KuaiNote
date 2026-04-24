package com.example.kuai_notes_project;

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

import java.util.ArrayList;
import java.util.Objects;

public class Aux_Search extends AppCompatActivity implements Recycler_Search_Interface {
    //----Encargado de busqueda lo mas eficiente y rapido posible
    //----SQLite FTS4
    //----Debounce (300ms) aprox
    //----Busqueda en segundo plano
    //----DiffUtil


    DB_Notes DB_N;

    RecyclerView recyclerView;
    Adapter_Recycler_Search adapter;
    ArrayList<Long> id_List;
    ArrayList<Long> cursor_id_List;
    ArrayList<String> Title_List;
    ArrayList<String> NoteContent_List;
    ArrayList<String> Snipped_Note_List;
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

        DB_N = new DB_Notes(this);

        Selected_List = new ArrayList<>();
        Title_List = new ArrayList<>();
        NoteContent_List = new ArrayList<>();
        Snipped_Note_List = new ArrayList<>();
        id_List = new ArrayList<>();

        cursor_id_List = new ArrayList<>();

        recyclerView = findViewById(R.id.Recycler_Search);
        adapter = new Adapter_Recycler_Search(this,0,Selected_List,Title_List, NoteContent_List,Snipped_Note_List, this);
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
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
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
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");

                while (cursor_Notes.moveToNext()){

                    boolean is_duplicated = false;
                    is_duplicated = Refresh_Data_On_Existing_Notes(cursor_Notes, title_indx, note_indx);

                    if(!is_duplicated){

                        id_List.add(cursor_Notes.getLong(0));
                        //Title_List.add(cursor_Notes.getString(title_indx));
                        Title_List.add(cursor_Notes.getString(4));
                        NoteContent_List.add(cursor_Notes.getString(note_indx));
                        Snipped_Note_List.add(cursor_Notes.getString(3));
                        Selected_List.add(false);
                        adapter.notifyItemInserted(0);
                        if(adapter.getItemCount() > 1){
                            adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
                        }
                        Log.d("2Search", "            Adding: Title: "+Title_List.get(Title_List.size()-1)+ "    note original: "+ NoteContent_List.get(NoteContent_List.size()-1)+ "    snipped: "+ Snipped_Note_List.get(Snipped_Note_List.size()-1));

                        item_count ++;
                    }

                    //--Agregar id a list nueva del cursor:
                    cursor_id_List.add(cursor_Notes.getLong(0));

                }
                //--Eliminar id_list que no esten en la nueva lista de cursor id list:
                for(int i = id_List.size() - 1 ;item_count > cursor_id_List.size() && i >= 0 ; i --){
                    Log.d("2Search", "        Loop Remove: item count: "+ item_count+ "    cursor count: "+ cursor_Notes.getCount() + "    id_list: " + id_List.size());
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
    private void Update_Recycler_View_ftsValues_Snipped2(String searched_Text) {
        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
        //---agregar coincidencias progresivamente al eliminar algunas letras
        //---Limpiar las listas al no tener ninguna coincidencia
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
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
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");

                while (cursor_Notes.moveToNext()){

                    boolean is_duplicated = false;
                    if(item_count < cursor_Notes.getCount()){
                        Log.d("2Search", "Cursor Mayor : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());

                        is_duplicated = Refresh_Data_On_Existing_Notes(cursor_Notes, title_indx, note_indx);

                        if(!is_duplicated){
                            Log.d("2Search", "      Adding Mayor : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());

                            id_List.add(cursor_Notes.getLong(0));
                            Title_List.add(cursor_Notes.getString(title_indx));
                            NoteContent_List.add(cursor_Notes.getString(note_indx));
                            Snipped_Note_List.add(cursor_Notes.getString(3));
                            Selected_List.add(false);
                            adapter.notifyItemInserted(0);
                            if(adapter.getItemCount() > 1){
                                adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
                            }
                            Log.d("2Search", "Coincidence : adapter itemcount:" + adapter.getItemCount() + "  titleList:" + Title_List.size());

                            item_count ++;
                        }

                    }else if(item_count == cursor_Notes.getCount()){

                        Log.d("2Search", "Equal : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());

                        Refresh_Data_On_Existing_Notes(cursor_Notes, title_indx, note_indx);

                    }else if(item_count > cursor_Notes.getCount()){

                        //--Eliminar si no coincide con la misma posicion en el cursor:
                        for(int i = id_List.size() ; i > 0; i --){
                            if(id_List.get(cursor_Notes.getPosition()) != cursor_Notes.getInt(0)){
                                Log.d("2Search", "      Cursor Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                                        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                                id_List.remove(cursor_Notes.getPosition());
                                Title_List.remove(cursor_Notes.getPosition());
                                NoteContent_List.remove(cursor_Notes.getPosition());
                                Snipped_Note_List.remove(cursor_Notes.getPosition());
                                Selected_List.remove(cursor_Notes.getPosition());
                                adapter.notifyItemRemoved(cursor_Notes.getPosition());
                                adapter.notifyItemChanged(cursor_Notes.getPosition());

                                item_count --;
                            }
                            if(item_count == cursor_Notes.getCount()) break;
                        }

                        if(item_count == cursor_Notes.getCount()) break;

                        //--Eliminar solo sobrantes sin comprobaciones:
                        for(int i = id_List.size() - cursor_Notes.getCount() ; i > 0; i --){

                            id_List.remove(cursor_Notes.getCount() + i -1);
                            Title_List.remove(cursor_Notes.getCount() + i -1);
                            NoteContent_List.remove(cursor_Notes.getCount() + i -1);
                            Snipped_Note_List.remove(cursor_Notes.getPosition() + i -1);
                            Selected_List.remove(cursor_Notes.getCount() + i -1);
                            adapter.notifyItemRemoved(cursor_Notes.getCount() + i -1 );
                            adapter.notifyItemChanged(cursor_Notes.getCount() + i -1);
                        }

                        item_count = cursor_Notes.getCount();

                        Refresh_Data_On_Existing_Notes(cursor_Notes, title_indx, note_indx);
                    }
                }
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
                Title_List.set(i, cursor_Notes.getString(4));
                NoteContent_List.set(i, cursor_Notes.getString(note_indx));
                Snipped_Note_List.set(i, cursor_Notes.getString(3));
                Selected_List.set(i, false);
                adapter.notifyItemChanged(i);

                break;
            }
        }
        return is_duplicated;
    }
    private boolean Refresh_Data_On_Existing_Notes_By_Cursor(Cursor cursor_Notes, int title_indx, int note_indx) {
        boolean is_duplicated  = false;
        for(int i = cursor_Notes.getCount() -1; i>=0;i-- ){

            if(id_List.get(i) == cursor_Notes.getInt(0)){
                Log.d("2Search", "        Duplicated Refreshing: id_list:" + id_List.get(i) + "  cursor_id:" + cursor_Notes.getInt(0)+"\n            Title: "+Title_List.get(i));
                is_duplicated = true;

                Title_List.set(i, cursor_Notes.getString(title_indx));
                NoteContent_List.set(i, cursor_Notes.getString(note_indx));
                Snipped_Note_List.set(i, cursor_Notes.getString(3));
                Selected_List.set(i, false);
                adapter.notifyItemChanged(i);

                break;
            }
        }
        return is_duplicated;
    }

    private void Update_Recycler_View_ftsValues_Snipped(String searched_Text) {
        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
        //---agregar coincidencias progresivamente al eliminar algunas letras
        //---Limpiar las listas al no tener ninguna coincidencia
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
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
                if(item_count < cursor_Notes.getCount()){
                    Log.d("2Search", "Mayor : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());
                    //int id_indx = cursor_Notes.getColumnIndex("id");
                    int title_indx = cursor_Notes.getColumnIndex("title");
                    //int note_indx = cursor_Notes.getColumnIndex("note");
                    int note_indx = cursor_Notes.getColumnIndex("note");
                    ///int deleted_indx = cursor_Notes.getColumnIndex("deleted");


                    while (cursor_Notes.moveToNext()){

                        boolean is_duplicated = false;
                        for(int i = id_List.size()-1; i>=0;i-- ){

                            Log.d("2Search", "Duplicated : id_list:" + id_List.get(i) + "  cursor_id:" + cursor_Notes.getInt(0));
                            if(id_List.get(i) == cursor_Notes.getInt(0)){
                                is_duplicated = true;

                                Title_List.set(i, cursor_Notes.getString(title_indx));
                                NoteContent_List.set(i, cursor_Notes.getString(note_indx));
                                Snipped_Note_List.set(i, cursor_Notes.getString(3));
                                Selected_List.set(i, false);
                                adapter.notifyItemChanged(i);


                                break;
                            }
                        }
                        if(!is_duplicated){
                            id_List.add(cursor_Notes.getLong(0));
                            Title_List.add(cursor_Notes.getString(title_indx));
                            NoteContent_List.add(cursor_Notes.getString(note_indx));
                            Snipped_Note_List.add(cursor_Notes.getString(3));
                            Selected_List.add(false);
                            adapter.notifyItemInserted(0);
                            if(adapter.getItemCount() > 1){
                                adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
                            }
                            Log.d("2Search", "Coincidence : adapter itemcount:" + adapter.getItemCount() + "  titleList:" + Title_List.size());

                            if(item_count == cursor_Notes.getCount()) break;
                        }
                    }
                    item_count = cursor_Notes.getCount();

                }
                if(item_count == cursor_Notes.getCount()){
                    Log.d("2Search", "Equal : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());

                    int title_indx = cursor_Notes.getColumnIndex("title");
                    int note_indx = cursor_Notes.getColumnIndex("note");

                    while (cursor_Notes.moveToNext()){

                        boolean is_duplicated = false;
                        for(int i = id_List.size()-1; i>=0;i-- ){

                            Log.d("2Search", "Duplicated : id_list:" + id_List.get(i) + "  cursor_id:" + cursor_Notes.getInt(0));
                            if(id_List.get(i) == cursor_Notes.getInt(0)){
                                is_duplicated = true;

                                Title_List.set(i, cursor_Notes.getString(title_indx));
                                NoteContent_List.set(i, cursor_Notes.getString(note_indx));
                                Snipped_Note_List.set(i, cursor_Notes.getString(3));
                                Selected_List.set(i, false);
                                adapter.notifyItemChanged(i);


                                break;
                            }
                        }
                    }

                }
                if(item_count > cursor_Notes.getCount()){
                    while (cursor_Notes.moveToNext()){

                        long current_id = id_List.get(cursor_Notes.getPosition());


                        for(int i = id_List.size() ; i > 0; i --){
                            if(id_List.get(cursor_Notes.getPosition()) != cursor_Notes.getInt(0)){
                                Log.d("2Search", "      Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                                        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                                id_List.remove(cursor_Notes.getPosition());
                                Title_List.remove(cursor_Notes.getPosition());
                                NoteContent_List.remove(cursor_Notes.getPosition());
                                Snipped_Note_List.remove(cursor_Notes.getPosition());
                                Selected_List.remove(cursor_Notes.getPosition());
                                adapter.notifyItemRemoved(cursor_Notes.getPosition());
                                adapter.notifyItemChanged(cursor_Notes.getPosition());
                            }
                            if(item_count == cursor_Notes.getCount()) break;
                        }
                        if(item_count == cursor_Notes.getCount()) break;
                    }
                    for(int i = id_List.size() - cursor_Notes.getCount() ; i > 0; i --){
                        //Log.d("2Search", "      Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                        //        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                        id_List.remove(cursor_Notes.getCount() + i -1);
                        Title_List.remove(cursor_Notes.getCount() + i -1);
                        NoteContent_List.remove(cursor_Notes.getCount() + i -1);
                        Selected_List.remove(cursor_Notes.getPosition() + i -1);
                        Selected_List.remove(cursor_Notes.getCount() + i -1);
                        adapter.notifyItemRemoved(cursor_Notes.getCount() + i -1 );
                        adapter.notifyItemChanged(cursor_Notes.getCount() + i -1);
                    }

                    item_count = cursor_Notes.getCount();
                }
            }
        }

    }

    private void Update_Recycler_View_ftsValues(String searched_Text) {
        //try (Cursor cursor_Notes = DB_N.get_All_Notes_fts(searched_Text)) {
        //    if(cursor_Notes.getCount()==0){
        //        //Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
        //    }else{
        //        ///int id_indx = cursor_Notes.getColumnIndex("id");
        //        int title_indx = cursor_Notes.getColumnIndex("title");
        //        int note_indx = cursor_Notes.getColumnIndex("note");
        //        ///int deleted_indx = cursor_Notes.getColumnIndex("deleted");


        //        while (cursor_Notes.moveToNext()){
        //            //ArrayList <Integer> deleted = new ArrayList<>();
        //            ///deleted.add(cursor_Notes.getInt(deleted_indx));
        //            ///deleted.add(cursor_Notes.getInt(10));

        //            if(cursor_Notes.getInt(3) == 0){
        //                Title_List.add(cursor_Notes.getString(title_indx));
        //                NoteContent_List.add(cursor_Notes.getString(note_indx));
        //                ///  id_List.add(cursor_Notes.getLong(id_indx));
        //                Selected_List.add(false);

        //            }
        //        }
        //    }
        //}
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
            //---agregar coincidencias progresivamente al eliminar algunas letras
            //---Limpiar las listas al no tener ninguna coincidencia
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            ///try (Cursor cursor_Notes_fts = DB_N.get_All_Notes_fts_3(searched_Text)) {
            ///    if(cursor_Notes_fts.getCount()==0 || et_searched_Text.getTextSize()==0){
            ///    }else{
            ///        while (cursor_Notes_fts.moveToNext()){
            ///            Log.d("2Search", "3 : :" + cursor_Notes_fts.getString(0));
            ///        }
            ///    }
            ///}
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
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
                if(item_count < cursor_Notes.getCount()){
                    Log.d("2Search", "Mayor : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());
                    //int id_indx = cursor_Notes.getColumnIndex("id");
                    int title_indx = cursor_Notes.getColumnIndex("title");
                    //int note_indx = cursor_Notes.getColumnIndex("note");
                    int note_indx = cursor_Notes.getColumnIndex("note");
                    ///int deleted_indx = cursor_Notes.getColumnIndex("deleted");


                    while (cursor_Notes.moveToNext()){

                        boolean is_duplicated = false;
                        for(int i = id_List.size()-1; i>=0;i-- ){

                            if(id_List.get(i) == cursor_Notes.getInt(0)){
                                Log.d("2Search", "Duplicated : id_list:" + id_List.get(i) + "  cursor_id:" + cursor_Notes.getInt(0));
                                is_duplicated = true;
                            }
                        }
                        if(!is_duplicated){
                            id_List.add(cursor_Notes.getLong(0));
                            Title_List.add(cursor_Notes.getString(title_indx));
                            NoteContent_List.add(cursor_Notes.getString(note_indx));
                            Snipped_Note_List.add(cursor_Notes.getString(3));
                            Selected_List.add(false);
                            adapter.notifyItemInserted(0);
                            if(adapter.getItemCount() > 1){
                                adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
                            }
                            Log.d("2Search", "Coincidence : adapter itemcount:" + adapter.getItemCount() + "  titleList:" + Title_List.size());

                            if(item_count == cursor_Notes.getCount()) break;
                        }
                    }
                    item_count = cursor_Notes.getCount();

                }
                if(item_count > cursor_Notes.getCount()){
                    while (cursor_Notes.moveToNext()){

                        long current_id = id_List.get(cursor_Notes.getPosition());


                        for(int i = id_List.size() ; i > 0; i --){
                            if(id_List.get(cursor_Notes.getPosition()) != cursor_Notes.getInt(0)){
                                Log.d("2Search", "      Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                                        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                                id_List.remove(cursor_Notes.getPosition());
                                Title_List.remove(cursor_Notes.getPosition());
                                NoteContent_List.remove(cursor_Notes.getPosition());
                                Snipped_Note_List.remove(cursor_Notes.getPosition());
                                Selected_List.remove(cursor_Notes.getPosition());
                                adapter.notifyItemRemoved(cursor_Notes.getPosition());
                                adapter.notifyItemChanged(cursor_Notes.getPosition());
                            }
                            if(item_count == cursor_Notes.getCount()) break;
                        }
                        if(item_count == cursor_Notes.getCount()) break;
                    }
                    for(int i = id_List.size() - cursor_Notes.getCount() ; i > 0; i --){
                            //Log.d("2Search", "      Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                            //        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                            id_List.remove(cursor_Notes.getCount() + i -1);
                            Title_List.remove(cursor_Notes.getCount() + i -1);
                            NoteContent_List.remove(cursor_Notes.getCount() + i -1);
                            Selected_List.remove(cursor_Notes.getPosition() + i -1);
                            Selected_List.remove(cursor_Notes.getCount() + i -1);
                            adapter.notifyItemRemoved(cursor_Notes.getCount() + i -1 );
                            adapter.notifyItemChanged(cursor_Notes.getCount() + i -1);
                    }

                    item_count = cursor_Notes.getCount();
                }
            }
        }

    }
    private void Update_Recycler_Simple_and_Snipped(String searched_Text) {
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0){
                Clear_Lists();
                adapter.notifyItemRangeRemoved(0,item_count - 1);
                adapter.notifyItemChanged(0);
            }else{
                ///int id_indx = cursor_Notes.getColumnIndex("id");
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");
                ///int deleted_indx = cursor_Notes.getColumnIndex("deleted");


                while (cursor_Notes.moveToNext()){
                    //ArrayList <Integer> deleted = new ArrayList<>();
                    ///deleted.add(cursor_Notes.getInt(deleted_indx));
                    ///deleted.add(cursor_Notes.getInt(10));

                    if(cursor_Notes.getInt(3) == 0){
                        Title_List.add(cursor_Notes.getString(title_indx));
                        NoteContent_List.add(cursor_Notes.getString(note_indx));
                        Snipped_Note_List.add(cursor_Notes.getString(3));
                        ///  id_List.add(cursor_Notes.getLong(id_indx));
                        Selected_List.add(false);

                    }
                }
            }
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void Update_Recycler_View_ftsValues_With_Snipped_Brute(String searched_Text) {

        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
        //---agregar coincidencias progresivamente al eliminar algunas letras
        //---Limpiar las listas al no tener ninguna coincidencia
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
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
                if(item_count < cursor_Notes.getCount()){
                    Log.d("2Search", "Mayor : adapter itemcount:" + item_count + "   cursor: "+ cursor_Notes.getCount());
                    //int id_indx = cursor_Notes.getColumnIndex("id");
                    int title_indx = cursor_Notes.getColumnIndex("title");
                    //int note_indx = cursor_Notes.getColumnIndex("note");
                    int note_indx = cursor_Notes.getColumnIndex("note");
                    ///int deleted_indx = cursor_Notes.getColumnIndex("deleted");


                    while (cursor_Notes.moveToNext()){

                        boolean is_duplicated = false;
                        for(int i = id_List.size()-1; i>=0;i-- ){

                            if(id_List.get(i) == cursor_Notes.getInt(0)){
                                Log.d("2Search", "Duplicated : id_list:" + id_List.get(i) + "  cursor_id:" + cursor_Notes.getInt(0));
                                is_duplicated = true;
                            }
                        }
                        if(!is_duplicated){
                            id_List.add(cursor_Notes.getLong(0));
                            Title_List.add(cursor_Notes.getString(title_indx));
                            NoteContent_List.add(cursor_Notes.getString(note_indx));
                            Snipped_Note_List.add(cursor_Notes.getString(3));
                            Selected_List.add(false);
                            adapter.notifyItemInserted(0);
                            if(adapter.getItemCount() > 1){
                                adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
                            }
                            Log.d("2Search", "Coincidence : adapter itemcount:" + adapter.getItemCount() + "  titleList:" + Title_List.size());

                            if(item_count == cursor_Notes.getCount()) break;
                        }
                    }
                    item_count = cursor_Notes.getCount();

                }
                if(item_count > cursor_Notes.getCount()){
                    while (cursor_Notes.moveToNext()){

                        long current_id = id_List.get(cursor_Notes.getPosition());


                        for(int i = id_List.size() ; i > 0; i --){
                            if(id_List.get(cursor_Notes.getPosition()) != cursor_Notes.getInt(0)){
                                Log.d("2Search", "      Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                                        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                                id_List.remove(cursor_Notes.getPosition());
                                Title_List.remove(cursor_Notes.getPosition());
                                NoteContent_List.remove(cursor_Notes.getPosition());
                                Snipped_Note_List.remove(cursor_Notes.getPosition());
                                Selected_List.remove(cursor_Notes.getPosition());
                                adapter.notifyItemRemoved(cursor_Notes.getPosition());
                                adapter.notifyItemChanged(cursor_Notes.getPosition());
                            }
                            if(item_count == cursor_Notes.getCount()) break;
                        }
                        if(item_count == cursor_Notes.getCount()) break;
                    }
                    for(int i = id_List.size() - cursor_Notes.getCount() ; i > 0; i --){
                        //Log.d("2Search", "      Menor : id_list_size:" + id_List.size() + "  Cursor_size:" + cursor_Notes.getCount() +
                        //        "\n     id_list: " + id_List.get(cursor_Notes.getPosition()) + "   cursor id: " + cursor_Notes.getInt(0));
                        id_List.remove(cursor_Notes.getCount() + i -1);
                        Title_List.remove(cursor_Notes.getCount() + i -1);
                        NoteContent_List.remove(cursor_Notes.getCount() + i -1);
                        Selected_List.remove(cursor_Notes.getPosition() + i -1);
                        Selected_List.remove(cursor_Notes.getCount() + i -1);
                        adapter.notifyItemRemoved(cursor_Notes.getCount() + i -1 );
                        adapter.notifyItemChanged(cursor_Notes.getCount() + i -1);
                    }

                    item_count = cursor_Notes.getCount();
                }
            }
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void Update_Recycler_View_originalValues() {
        try (Cursor cursor_Notes = DB_N.get_All_Notes()) {
            if(cursor_Notes.getCount()==0){
                //Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx = cursor_Notes.getColumnIndex("_id");
                int date_indx = cursor_Notes.getColumnIndex("date");
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");
                int pin_indx = cursor_Notes.getColumnIndex("pin");
                int reminder_indx = cursor_Notes.getColumnIndex("reminder");
                int reminder_type_indx = cursor_Notes.getColumnIndex("reminder_type");
                int reminder_interval_indx = cursor_Notes.getColumnIndex("reminder_interval");

                //while (cursor_Notes.moveToNext()){
                //    //!!---debe actualizarse
                //    Note note = new Note(cursor_Notes.getLong(id_indx),
                //            cursor_Notes.getLong(date_indx),
                //            cursor_Notes.getString(title_indx),
                //            ///BNP.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                //            ///        cursor_Notes.getString(note_indx),
                //            ///        60,
                //            ///        55,
                //            ///        0,
                //            ///        3,
                //            ///        1,
                //            ///        30),
                //            cursor_Notes.getString(note_indx),
                //            cursor_Notes.getInt(pin_indx)==1,
                //            cursor_Notes.getLong(reminder_indx),
                //            cursor_Notes.getInt(reminder_type_indx),
                //            cursor_Notes.getInt(reminder_interval_indx));
                //    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.date,start_of_today));
                //    noteOriginal_list.add(cursor_Notes.getString(note_indx));
                //    selected_list.add(false);
                //    noteList.add(note);
                //}

                while (cursor_Notes.moveToNext()){
                    //!!---debe actualizarse
                    //Note note = new Note(cursor_Notes.getLong(id_indx),
                    //        cursor_Notes.getLong(date_indx),
                    //        cursor_Notes.getString(title_indx),
                    //        ///BNP.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                    //        ///        cursor_Notes.getString(note_indx),
                    //        ///        60,
                    //        ///        55,
                    //        ///        0,
                    //        ///        3,
                    //        ///        1,
                    //        ///        30),
                    //        cursor_Notes.getString(note_indx),
                    //        cursor_Notes.getInt(pin_indx)==1,
                    //        cursor_Notes.getLong(reminder_indx),
                    //        cursor_Notes.getInt(reminder_type_indx),
                    //        cursor_Notes.getInt(reminder_interval_indx));
                    Title_List.add(cursor_Notes.getString(title_indx));
                    NoteContent_List.add(cursor_Notes.getString(note_indx));
                    id_List.add(cursor_Notes.getLong(0));
                    Selected_List.add(false);
                    //noteList.add(note);
                }
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    }


    @Override
    public void onItemClick(int position, View v) {
        long _note_id = id_List.get(position);
        Intent goTo = new Intent(this, MainActivity.class);
        goTo.putExtra("send_date_of_note",_note_id);
        goTo.putExtra("send_note_id",_note_id);
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
