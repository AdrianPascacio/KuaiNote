package com.example.kuai_notes_project;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;

///324 V3, 305 V4, 358 V6, 306 V7, 450 V7.2, 570 v9.0B
public class Memo_Board_New_Aux extends AppCompatActivity implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener , NotesFragment.Note_Fragment_ReminderListener {
    public interface MemoBoardNewAux_OutReminder_Listener {//esto puede ir tambien en una clase separada
        void onMemoBoardNewAux_OutReminder(int salida, int position); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private MemoBoardNewAux_OutReminder_Listener memoBoardNewAuxOutReminderListener;

    public void setMemoBoardNewAuxOutReminderListener(MemoBoardNewAux_OutReminder_Listener outReminderListener){
        this.memoBoardNewAuxOutReminderListener = outReminderListener;
    }

    ///RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;

    //EditText et_searched_Text;
    int item_count = 0 ;

    ArrayList<Integer> selected_positions_list;

    DB_Notes DB_N;
    Random_Content_Generator_For_Test Random_G;
    Stable_Content_Generator_For_Test Stable_G;

    //Adapter_Recycler_Memo_Board adapter;

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
    NotesFragment notesFragment;

    @Override
    protected void onResume(){
        super.onResume();
        getStartOfToday();

        //recyclerView = findViewById(R.id.Recycler_MemoBoard);
        //adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        //recyclerView.setAdapter(adapter);

        //et_searched_Text.setText("");
        //adapter.Change_Searching_Mode_Status(false);
        item_count = 0;

        //Clear_Lists();
        //Update_Recycler_View();

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
        setContentView(R.layout.activity_memo_board_new_aux);
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

        notesFragment = new NotesFragment();
        notesFragment.setNoteFragment_Reminder_Listener(this);

        DB_N = new DB_Notes(this);
        Random_G = new Random_Content_Generator_For_Test();
        Stable_G = new Stable_Content_Generator_For_Test();

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        //et_searched_Text = findViewById(R.id.Searched_Text);

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
        //et_searched_Text.addTextChangedListener(new TextWatcher() {
        //    @Override
        //    public void afterTextChanged(Editable s) {
        //        String searched_Text = et_searched_Text.getText().toString();

        //        Update_Recycler_View_ftsValues_Snipped4(searched_Text);
        //    }

        //    @Override
        //    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        //    @Override
        //    public void onTextChanged(CharSequence s, int start, int before, int count) {}
        //});

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
                finish();
            }
        });
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
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    }

    @Override
    public void onItemHold(int position,View v) {
        Select_Item(position, v);
    }
    private void Select_Item(int position, View v) {
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {
    }
    public void RecyclerView_Pin_Update(int position){
    }

    /// Reminder
    @Override
    public void SetReminder(int position) {

        ///layout_dim.setVisibility(View.VISIBLE);
        ///layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        ///layout_dim.startAnimation(AnimationLayoutDimAppear);

        /////adapter.Change_is_repeated_value(true);
        ///Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, position);
        ///reminder_PopUp.setListener(this);
        ///reminder_PopUp.setListener_dismiss(this);

        ///Note _note = noteList.get(position);
        ///reminder_PopUp.show(main, _note);
    }
    @Override
    public void OnValueSelected(int position, long alarm_time) {
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

        //////Have to put this into the NoteFragment:
        ///    //selected_list.set(position,false);

        ///    //adapter.notifyItemChanged(position);
        //Reminder_PopUpWindow reminderPopUpWindow = Reminder_PopUpWindow(new Reminder_PopUpWindow(){
        //    @Override
        //    public void onPopupClosed_Repeater(int salida) {

        //        layout_dim.setVisibility(View.VISIBLE);

        //        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);

        //    }

        //});

        //notesFragment.adapter_noteFragment.notifyItemChanged(position);
        //memoBoardNewAuxOutReminderListener.onMemoBoardNewAux_OutReminder(salida,position);


        if(memoBoardNewAuxOutReminderListener != null){
            memoBoardNewAuxOutReminderListener.onMemoBoardNewAux_OutReminder(salida,position);
        }

        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);

        ///!!-- duplicated
        //Restart_Selection();

        Toast.makeText(this, "reminder"+" normal", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void RemoveItem(int position) {
    }
    private void Restart_Selection() {
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

    @Override
    public void onNoteFragment_Reminder_Open(int salida, int position, Note note) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        layout_dim.startAnimation(AnimationLayoutDimAppear);

        //adapter.Change_is_repeated_value(true);
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, position);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);



        //Note _note = noteList.get(position);
        reminder_PopUp.show(main, note);
    }
    public void ejecutarPopUP(MemoBoardNewAux_OutReminder_Listener listener, Note note){
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, 0);
        //reminder_PopUp.setListener(this);
        //reminder_PopUp.setListener_dismiss(this);



        ////Note _note = noteList.get(position);
        //reminder_PopUp.show(main, note);
        this.memoBoardNewAuxOutReminderListener = listener;
        reminder_PopUp = new Reminder_PopUpWindow(new Reminder_PopUpWindow.PopupDismissListener(){
            @Override
            public void onPopupClosed(int salida, int position){
                if(memoBoardNewAuxOutReminderListener != null){
                    memoBoardNewAuxOutReminderListener.onMemoBoardNewAux_OutReminder(salida,position);
                }
            }

        });
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);
        reminder_PopUp.show(main, note);
    }
}