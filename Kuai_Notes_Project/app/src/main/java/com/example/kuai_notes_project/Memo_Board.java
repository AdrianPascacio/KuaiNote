package com.example.kuai_notes_project;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

///324 V3, 305 V4, 358 V6, 306 V7, 450 V7.2, 570 v9.0B
public class Memo_Board extends AppCompatActivity implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener {
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;

    ArrayList<String> searched_note_list;
    ArrayList<String> searched_snipped_note_list;
    ArrayList<String> searched_title_list;
    ArrayList<Boolean> searched_selected_list;
    ArrayList<Long> id_List;
    ArrayList<Long> cursor_id_List;
    EditText et_searched_Text;
    int item_count = 0 ;

    ArrayList<Integer> selected_positions_list;

    DB_Notes DB_N;
    Random_Content_Generator_For_Test Random_G;
    Stable_Content_Generator_For_Test Stable_G;

    Adapter_Recycler_Memo_Board adapter;

    long start_of_today = 0;
    Button btn_config, btn_check_lists, btn_search, btn_generate_random_content, btn_generate_stable_content, btn_delete_all_notes_database;

    View main;
    View layout_dim;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;
    FloatingActionButton floatingActionButton;
    private Animation AnimationAddNoteButton;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel, Animation_FloatingButton_Appear, Animation_FloatingButton_Disappear;
    private FloatingActionButton fa_btn;



    private LinearLayout floating_TrashCan_Access;
    private TextView tabArrow;
    private View menu_show_recycler_trashcan;
    private View btn_go_trash_can;
    private View menuTab;

    private boolean isExpanded = false;
    private float initialX;
    //!!-- have to fix this:
    // private final float HIDDEN_OFFSET = dpToPx(170); // Ajusta según el XML
    private float HIDDEN_OFFSET = 40; // Ajusta según el XML
    private float HIDDEN_OFFSET_ARROW = 6; // Ajusta según el XML




    private static final String CHANNEL_ID = "My_App_Channel";

    Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(this,-1);

    private int selection_count = 0;
    private boolean pin_initial_state_MS= false;
    private boolean selection_mode = false;
    private boolean pin_multi_change = false;
    ///private AdapterView.OnItemClickListener listener;
    ///public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
    ///    this.listener = listener;
    ///}

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        recyclerView = findViewById(R.id.Recycler_MemoBoard);
        adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        recyclerView.setAdapter(adapter);

        et_searched_Text.setText("");
        adapter.Change_Searching_Mode_Status(false);
        item_count = 0;

        Clear_Lists();
        Update_Recycler_View();

        fa_btn.setVisibility(View.VISIBLE);
        fa_btn.setFocusable(true);
        fa_btn.setClickable(true);
        fa_btn.animate().alpha(1f).scaleY(1f).scaleX(1f).setDuration(700);

        if(isExpanded){
            floating_TrashCan_Access.setTranslationX(HIDDEN_OFFSET);
            tabArrow.setTranslationX(0);
            tabArrow.setText("<");
            isExpanded = false;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Restart_Selection();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memo_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_navigation_bar));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);



        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0){
                    tab.setText("Notes");
                }else{
                    tab.setText("Tasks");
                }
            }
        }).attach();

        DB_N = new DB_Notes(this);
        Random_G = new Random_Content_Generator_For_Test();
        Stable_G = new Stable_Content_Generator_For_Test();

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        searched_title_list = new ArrayList<>();
        searched_note_list = new ArrayList<>();
        searched_snipped_note_list = new ArrayList<>();
        searched_selected_list = new ArrayList<>();
        id_List = new ArrayList<>();
        cursor_id_List = new ArrayList<>();

        et_searched_Text = findViewById(R.id.Searched_Text);

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();
        fa_btn = findViewById(R.id.floatingActionButton);
        main = findViewById(R.id.main);
        layout_dim = findViewById(R.id.layout_dim_itemVisualizer);

        AnimationAddNoteButton = AnimationUtils.loadAnimation(this,R.anim.add_note_button_zoom);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        Animation_FloatingButton_Appear = AnimationUtils.loadAnimation(this, R.anim.floating_button_appear);
        Animation_FloatingButton_Disappear = AnimationUtils.loadAnimation(this, R.anim.floating_buttton_disappear);

        btn_config = findViewById(R.id.button_Config);
        btn_check_lists = findViewById(R.id.button_Check_Lists);
        btn_search = findViewById(R.id.button_Search);
        btn_generate_random_content = findViewById(R.id.button_Generate_Random_Content);
        btn_generate_stable_content = findViewById(R.id.button_Generate_Stable_Content);
        btn_delete_all_notes_database = findViewById(R.id.button_Delete_All_Notes_DataBase);
        fa_btn.startAnimation(AnimationAddNoteButton);


        floating_TrashCan_Access = findViewById(R.id.floatingMenu);
        //menuTab = findViewById(R.id.menuTab);
        menuTab = findViewById(R.id.menuTab);
        tabArrow = findViewById(R.id.tabArrow);
        btn_go_trash_can = findViewById(R.id.btnNavigate);
        HIDDEN_OFFSET = dpToPx(55);
        HIDDEN_OFFSET_ARROW = dpToPx(6);
        floating_TrashCan_Access.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    toggle_TrashCan_Menu(false);
                }

            }
        });
        menuTab.setOnTouchListener(new View.OnTouchListener() {

            private static final int MAX_CLICK_DURATION = 150;
            private long startClickTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getRawX();
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float currentX = event.getRawX();
                        float deltaX = currentX - initialX;

                        if (deltaX < -2 && !isExpanded) {//Arrastre a la izquierda
                            toggle_TrashCan_Menu(true);
                        }
                        else if (deltaX > 2 && isExpanded) {//Arrastre a la derecha
                            toggle_TrashCan_Menu(false);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                        if (clickDuration < MAX_CLICK_DURATION) {
                            toggle_TrashCan_Menu(!isExpanded);
                        }
                        return true;
                }
                return false;
            }
        });


        fa_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Add_New_Note();
            }
        });
        et_searched_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String searched_Text = et_searched_Text.getText().toString();

                Update_Recycler_View_ftsValues_Snipped4(searched_Text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
            }
        });
        btn_check_lists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Check_Lists();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Search();
            }
        });
        btn_go_trash_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
                //toggleMenu(!isExpanded);
            }
        });
        btn_generate_random_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Generate_Random_Content_For_Test();
            }
        });
        btn_generate_stable_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Generate_Stable_Content_For_Test();
            }
        });
        btn_delete_all_notes_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_All_Notes_From_DataBase();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(selection_item_menu_PopUp.popupWindow != null){
                    for( int i = 0; i < selected_list.size() ; i++){
                        if(selected_list.get(i)== true){
                            selected_list.set(i,false);
                            adapter.notifyItemChanged(i);
                        }
                    }
                    Restart_Selection();
                }else{
                    finish();
                }
            }
        });
    }
    private void Update_Recycler_View_ftsValues_Snipped4(String searched_Text) {
        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
        //---agregar coincidencias progresivamente al eliminar algunas letras
        //---Limpiar las listas al no tener ninguna coincidencia
        //!!--Optimizar

        ////if(!adapter.Get_Searching_Mode_Status()) item_count = 0;
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
                adapter.Change_Searching_Mode_Status(false);
                Log.d("2Search", "Zero : adapter itemcount:" + adapter.getItemCount());
                ///if(id_List.size() > 0){
                ///    //Clear_Lists();
                ///    Clear_Searched_Lists();

                ///    recyclerView.setAdapter(adapter);
                ///    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                ///    item_count = 0;
                ///}


                Clear_Lists();
                //Clear_Searched_Lists();

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));




                Update_Recycler_View();
                //adapter.Change_Searching_Mode_Status(false);
            }else{
                adapter.Change_Searching_Mode_Status(true);

                //if(!adapter.Get_Searching_Mode_Status()) {
                //    adapter.Change_Searching_Mode_Status(true);
                //}
                int title_indx = cursor_Notes.getColumnIndex("title");
                int note_indx = cursor_Notes.getColumnIndex("note");


                if(cursor_Notes.getCount() < noteList.size()){
                    int i = 0;
                    ///!! la ultima condicion de while solo esta alli porque se esta eliminando debido a que el orden de la lista de ntf2 no es la misma que el actual orden de la lista
                    while(i <= cursor_Notes.getCount() -1 && i <= noteList.size() -1){
                        Note _note = noteList.get(i);
                        cursor_Notes.moveToPosition(i);
                        if(_note.note_id != cursor_Notes.getLong(0)){
                            Log.d("2Search", "            Removing: Title: "+_note.title+ "    i: "+ i);
                            noteList.remove(i);
                            selected_list.remove(i);
                            dateEdited_list.remove(i);
                            noteOriginal_list.remove(i);
                            adapter.notifyItemRemoved(i);
                        }else{
                            noteList.get(i).setTitle(cursor_Notes.getString(4));
                            noteList.get(i).setNote(cursor_Notes.getString(3));
                            adapter.notifyItemChanged(i);
                            i++;
                        }
                    }
                    if(cursor_Notes.getCount() < noteList.size()){
                        for(int j = noteList.size() -1  ; j >=cursor_Notes.getCount()  ; j --){
                            Log.d("2Search", "            Removing: Title: "+noteList.get(j).title+ "    j: "+ j);
                            noteList.remove(j);
                            selected_list.remove(j);
                            dateEdited_list.remove(j);
                            noteOriginal_list.remove(j);
                            adapter.notifyItemRemoved(j);
                        }
                    }
                }else if (cursor_Notes.getCount() > noteList.size()){
                    int i = 0;
                    while(i <= noteList.size() -1){
                        cursor_Notes.moveToPosition(i);
                        Note _note = noteList.get(i);
                        Log.d("2Search", "            first Adding: Title: "+_note.title+ "    i: "+ i);
                        if(_note.note_id != cursor_Notes.getLong(0)){


                            Note note_adding = DB_N.getASpecificNote(cursor_Notes.getLong(0));
                            Log.d("2Search", "            Adding: Title: "+note_adding.title+ "    i: "+ i);
                            dateEdited_list.add(i,DoN.Set_Date_of_Note_Item_View(note_adding.date,start_of_today));
                            noteOriginal_list.add(i,note_adding.note);
                            note_adding.setTitle(cursor_Notes.getString(4));
                            note_adding.setNote(cursor_Notes.getString(3));
                            selected_list.add(i,false);
                            noteList.add(i,note_adding);
                            adapter.notifyItemInserted(i);
                        }else{
                            noteList.get(i).setTitle(cursor_Notes.getString(4));
                            noteList.get(i).setNote(cursor_Notes.getString(3));
                            adapter.notifyItemChanged(i);
                        }
                        i++;
                    }
                    if(cursor_Notes.getCount() > noteList.size()){
                        for(int j = noteList.size()   ; j <=cursor_Notes.getCount() -1  ; j ++){
                            cursor_Notes.moveToPosition(j);
                            Note note_adding = DB_N.getASpecificNote(cursor_Notes.getLong(0));
                            Log.d("2Search", "            Adding: Title: "+note_adding.title+ "    j: "+ j);
                            dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note_adding.date,start_of_today));
                            noteOriginal_list.add(note_adding.note);
                            note_adding.setTitle(cursor_Notes.getString(4));
                            note_adding.setNote(cursor_Notes.getString(3));
                            selected_list.add(false);
                            noteList.add(note_adding);
                            adapter.notifyItemInserted(j);
                        }
                    }
                }else{
                    while(cursor_Notes.moveToNext()){
                        int i = cursor_Notes.getPosition();
                        Note _note = noteList.get(i);
                        Log.d("2Search", "            Just updating: Title: "+_note.title+ "    i: "+ i);
                        noteList.get(i).setTitle(cursor_Notes.getString(4));
                        noteList.get(i).setNote(cursor_Notes.getString(3));
                        adapter.notifyItemChanged(i);
                        //i++;
                    }

                }
            }
        }
    }

    /// TrashCan_Access:
    private void toggle_TrashCan_Menu(boolean expand) {
        if (isExpanded == expand) return; // Evita repetir la animación si ya está en ese estado
        isExpanded = expand;

        float targetX = expand ? 0f : HIDDEN_OFFSET;
        float targetX_arrow = expand ? HIDDEN_OFFSET_ARROW : 0f  ;
        if(expand){
            //if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Disappear);
            fa_btn.animate().alpha(0f).scaleY(0.7f).scaleX(0.7f).setDuration(325).withEndAction(new Runnable() {
                @Override
                public void run() {
                    fa_btn.setFocusable(false);
                    fa_btn.setClickable(false);
                    fa_btn.setVisibility(View.GONE);
                }
            });
        }else{
            fa_btn.setVisibility(View.VISIBLE);
            fa_btn.setFocusable(true);
            fa_btn.setClickable(true);
            //if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
            fa_btn.animate().alpha(1f).scaleY(1).scaleX(1).setDuration(600);
        }

        // Animación suave del atributo translationX
        ObjectAnimator animator = ObjectAnimator.ofFloat(floating_TrashCan_Access, "translationX", targetX);
        //ObjectAnimator animator_arrow = ObjectAnimator.ofFloat(menuTab, "translationX", targetX_arrow);
        ObjectAnimator animator_arrow = ObjectAnimator.ofFloat(tabArrow, "translationX", targetX_arrow);
        animator.setDuration(300); // Duración en milisegundos
        animator_arrow.setDuration(350); // Duración en milisegundos
        animator.start();
        animator_arrow.start();

        // Cambiar la flecha indicadora
        tabArrow.setText(expand ? ">" : "<");
    }
    private float dpToPx(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Solo nos interesa evaluar cuando el usuario recién apoya el dedo
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // Si el menú está expandido, verificamos dónde se hizo el toque
            if (isExpanded && floating_TrashCan_Access != null) {

                // Obtenemos los límites reales del menú flotante en la pantalla
                Rect outRect = new Rect();
                floating_TrashCan_Access.getGlobalVisibleRect(outRect);

                // Si el toque NO ocurrió dentro de los límites del menú
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    toggle_TrashCan_Menu(false); // Cerramos el menú

                    // Opcional: Si quieres que ese toque "fuera" además de cerrar el menú
                    // no haga nada más (ej. que no presione un botón que estaba atrás por accidente),
                    // puedes retornar 'true' aquí para consumir el evento.
                    // return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /// Content Generation for Test:
    private void Generate_Random_Content_For_Test() {
        Random_G.Random_Note_Generator(this,20);
    }
    private void Generate_Stable_Content_For_Test() {
        Stable_G.Stable_Note_Generator(this,20,0,20,20,20);
    }
    private void Delete_All_Notes_From_DataBase() {
        DB_N.Delete_Hard_All_Notes();
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

                while (cursor_Notes.moveToNext()){
                    //!!---debe actualizarse
                    Note note = new Note(cursor_Notes.getLong(id_indx),
                            cursor_Notes.getLong(date_indx),
                            cursor_Notes.getString(title_indx),
                            ///BNP.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                            ///        cursor_Notes.getString(note_indx),
                            ///        60,
                            ///        55,
                            ///        0,
                            ///        3,
                            ///        1,
                            ///        30),
                            cursor_Notes.getString(note_indx),
                            cursor_Notes.getInt(pin_indx)==1,
                            cursor_Notes.getLong(reminder_indx),
                            cursor_Notes.getInt(reminder_type_indx),
                            cursor_Notes.getInt(reminder_interval_indx));
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.date,start_of_today));
                    noteOriginal_list.add(cursor_Notes.getString(note_indx));
                    selected_list.add(false);
                    noteList.add(note);
                }
            }
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void Clear_Lists(){
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty()){
            return;
        }
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
    }

    public void Go_To_Add_New_Note(){
        if(!selection_mode) {
            Intent goTo = new Intent(this, MainActivity.class);
            startActivity(goTo);
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        if(!adapter.Get_Searching_Mode_Status()){
            if(selection_mode) {
                Select_Item(position, v);
                return;
            }

            Note _note = noteList.get(position);
            Intent goTo = new Intent(this, MainActivity.class);
            goTo.putExtra("send_date_of_note",_note.date);
            goTo.putExtra("send_note_id",_note.note_id);
            startActivity(goTo);
            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
        }else{
            if(selection_mode) {
                //!! Must correct this section
                Select_Item(position, v);
                return;
            }

            Intent goTo = new Intent(this, MainActivity.class);
            goTo.putExtra("send_note_id",noteList.get(position).getNote_id());
            startActivity(goTo);
            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);

        }
    }

    @Override
    public void onItemHold(int position,View v) {
        Select_Item(position, v);
    }
    private void Select_Item(int position, View v) {
        selected_list.set(position,!selected_list.get(position));// invert value

        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Disappear);

        selection_mode = selection_count > 0;

        selected_positions_list.add(0,position);


        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            //--Buscar estado del pin de las dos primeras notas seleccionadas:
            Note _note = noteList.get(selected_positions_list.get(0));
            Note _note2 = noteList.get(selected_positions_list.get(1));

            //pin_initial_state_MS = false;
            pin_initial_state_MS = _note.getPin() & _note2.getPin() || _note2.getPin(); /// AND Operator !!--Verificar si la opcion de elegir lo primero que escoja el usuario es lo mejor


            selection_item_menu_PopUp.setListener_dismiss(this);
            selection_item_menu_PopUp.show(v, pin_initial_state_MS);

            adapter.Change_multi_selection_state(selection_mode);
            adapter.notifyItemChanged(position,this);
            adapter.notifyItemChanged(selected_positions_list.get(1),this);//!!se estan desvaneciendo sin las animaciones

            //fa_btn.startAnimation(AnimationLayoutDimDisappear_Normal);
        }
        if(selection_item_menu_PopUp.popupWindow != null && !selection_mode){
            //selection_item_menu_PopUp.popupWindow.dismiss();
            //selection_item_menu_PopUp.popupWindow = null;
            //adapter.Change_multi_selection_state(selection_mode);

            //selected_positions_list.clear();
            //fa_btn.startAnimation(AnimationLayoutDimAppear);
            Restart_Selection();
        }
        if(selection_item_menu_PopUp.popupWindow != null && selection_mode){
            //selection_item_menu_PopUp.popupWindow.update(v,60,-150,140,360);
        }
        adapter.notifyItemChanged(position);//!! se esta duplicando con la instruccion de arriba

        //---Set unselecting_view to repeated unselect
        if(selected_positions_list.size()==2) {
            if(Objects.equals(position, selected_positions_list.get(1))){
                adapter.Change_is_repeated_value(true);
                selected_positions_list.clear();
            }
        }

        if(selected_positions_list.size()==3) selected_positions_list.remove(2);
        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {
        Note _note = noteList.get(position);
        //int _pin = _note.getPin() ^ 1;      //XOR Operator


        //!!--en modo multiple seleccion, cambiar el pin dependiendo del color del pin
            //!!--no invertir todo
        if(pin_multi_change && pin_initial_state_MS ^ _note.getPin()){///XOR Operator
            selected_list.set(position,!selected_list.get(position));// invert value
            adapter.notifyItemChanged(position);
            return;
        }

        Toast.makeText(this, "Repeated pinned", Toast.LENGTH_SHORT).show();
        adapter.Change_is_repeated_value(true);

        boolean _pin = pin_multi_change ? !pin_initial_state_MS : !_note.getPin();///Ternary Operator


        if(DB_N.Modify_Pin_Status(_note.note_id,_pin)){
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(Memo_Board.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }
    public void RecyclerView_Pin_Update(int position){

        Note _note = noteList.get(position);
        String _date= dateEdited_list.get(position);
        String _noteOriginal= noteOriginal_list.get(position);
        boolean _selected=false;
        selected_list.set(position,false);
        adapter.notifyItemChanged(position);

        dateEdited_list.remove(position);
        noteOriginal_list.remove(position);
        selected_list.remove(position);

        noteList.remove(position);


        int current_pinned_notes = 0;
        if(adapter.Get_Searching_Mode_Status() == true){
            int i = 0 ;
            while (i <= noteList.size()-1) {
                if(noteList.get(i).pin != _note.pin){/// Pin
                    while(_note.date < noteList.get(i).date ){/// Date
                        i++;
                    }
                    current_pinned_notes = i;
                    break;
                }
                i++;
            }

        }else{
            current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(_note.note_id);
        }
        //Log.d("Pin","   current_pin:" + current_pinned_notes+ "    position:" + position);

        dateEdited_list.add(current_pinned_notes,_date);
        noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        //--cambio de estado con referencia al anterior de (0 a 1)
        //_note.setPin(_note.getPin() ^ 1);       //XOR Operator
        _note.setPin(!_note.getPin());
        noteList.add(current_pinned_notes,_note);
        selected_list.add(current_pinned_notes,_selected);
        adapter.notifyItemMoved(position,current_pinned_notes);
        adapter.notifyItemChanged(current_pinned_notes);

        Restart_Selection();
    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        layout_dim.startAnimation(AnimationLayoutDimAppear);

        adapter.Change_is_repeated_value(true);
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, position);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        Note _note = noteList.get(position);
        reminder_PopUp.show(main, _note);
    }
    @Override
    public void OnValueSelected(int position, long alarm_time) {
        Note _note = noteList.get(position);
        selected_list.set(position,false);


        _note.setReminder(alarm_time);
        //!!---- actualizar type and interval
        _note.setReminder_type(0);
        _note.setReminder_interval(0);
        noteList.remove(position);
        noteList.add(position,_note);
        adapter.notifyItemChanged(position);
    }
    @Override
    public void onPopupClosed(int salida, int position) {
        layout_dim.setVisibility(View.VISIBLE);
        Restart_Selection();
        if(salida == 1){//setter
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);

            Toast.makeText(this, "reminder"+" setter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(salida == 2){//cancel
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            Toast.makeText(this, "reminder"+" cancel", Toast.LENGTH_SHORT).show();

            return;
        }
        selected_list.set(position,false);

        adapter.notifyItemChanged(position);
        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);

        ///!!-- duplicated
        //Restart_Selection();

        Toast.makeText(this, "reminder"+" normal", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main,_note.note_id,0, _note.reminder);

        if(DB_N.Send_Note_To_Trash(_note.note_id,_note.date,_note.title,noteOriginal_list.get(position),_note.pin,20)){
            //----Remove Note from Recycler View
            dateEdited_list.remove(position);
            noteOriginal_list.remove(position);
            noteList.remove(position);
            selected_list.remove(position);
            adapter.notifyItemRemoved(position);

            Restart_Selection();
        }
    }
    private void Restart_Selection() {
        selection_count =0;
        selection_mode = false;
        selected_positions_list.clear();
        if(selection_item_menu_PopUp.popupWindow != null){
            selection_item_menu_PopUp.popupWindow.dismiss();
            selection_item_menu_PopUp.popupWindow = null;
        }
        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
        adapter.Change_multi_selection_state(false);
    }

    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }
    public void Go_To_Check_Lists(){
        Intent goTo = new Intent(this, Tasks_List.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }
    private void Go_To_Search() {
        Intent goTo = new Intent(this, Aux_Search.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_search,R.anim.slide_left_out_search);
    }

    @Override
    public void onMemoBoardSelection_PopupClosed(int option) {
        if(option == 1){
            Toast.makeText(this, "pin", Toast.LENGTH_SHORT).show();

            pin_multi_change = true;

            if(pin_initial_state_MS){
                for(int i = selected_list.size()-1;i >= 0; i--) {
                    if (selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }else{
                for(int i = 0;i < selected_list.size(); i++) {
                    if (selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }

            pin_multi_change = false;
            Restart_Selection();
            return;
        }

        if(option == 2){
            Toast.makeText(this, "reminder", Toast.LENGTH_SHORT).show();
        //    int count = 0;
        //    for(int i = 0;i-count < selected_list.size(); i++){
        //        if(selected_list.get(i-count)){
        //            RemoveItem(i-count);
        //            count ++;
        //        }
        //    }
        //    selected_positions_list.clear();
        }
        if(option == 3){
            Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
            int count = 0;
            for(int i = 0;i-count < selected_list.size(); i++){
                if(selected_list.get(i-count)){
                    RemoveItem(i-count);
                    count ++;
                }
            }
            selected_positions_list.clear();
        }

    }
}