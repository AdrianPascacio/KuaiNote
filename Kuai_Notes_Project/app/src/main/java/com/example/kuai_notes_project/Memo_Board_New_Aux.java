package com.example.kuai_notes_project;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;

///734 13jul2026, 484 16jul2026
public class Memo_Board_New_Aux extends AppCompatActivity implements NotesFragment.Note_Fragment_ReminderListener, NotesFragment.Note_Fragment_Out_ReminderListener, NotesFragment.Note_Fragment_Adding_Option_Available, TasksFragment.Task_Fragment_Adding_Option_Available{
    private ActivityResultLauncher<Intent> lanzadorActivityC;

    ArrayList<Integer> selected_positions_list;

    DB_Notes DB_N;
    DB_Tasks DB_T;

    Random_Content_Generator_For_Test Random_G;
    Stable_Content_Generator_For_Test Stable_G;

    Button btn_config, btn_check_lists, btn_generate_random_content, btn_generate_stable_content, btn_delete_all_notes_database;

    View main, layout_dim;
    private View btn_go_trash_can, menuTab;
    private int Journal_Section = -1;
    private Animation AnimationAddNoteButton, AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel, Animation_FloatingButton_Appear, Animation_FloatingButton_Disappear;
    private FloatingActionButton fa_btn;

    private LinearLayout floating_TrashCan_Access;
    private TextView tabArrow;

    private boolean isExpanded = false;
    private float initialX;
    private float HIDDEN_OFFSET = 40; // Ajusta según el XML
    private float HIDDEN_OFFSET_ARROW = 6; // Ajusta según el XML

    private static final String CHANNEL_ID = "My_App_Channel";
    private ViewPager2 viewPager;
    NotesFragment notesFragment;
    TasksFragment tasksFragment;

    @Override
    protected void onResume(){
        super.onResume();

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
        //--Now is empty
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
        viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);


        lanzadorActivityC = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("MemoBoard_NewAux" , "Result OK: " + MainActivity.RESULT_OK );
                    Log.d("MemoBoard_NewAux" , "result.getData() != null : " + (result.getData() != null) );
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // ¡Activity C terminó con éxito! Ahora avisamos al Fragment actual
                        Intent data = result.getData();
                        int modificacion = data.getIntExtra("extra_modificacion", -1);
                        long id = data.getLongExtra("extra_id", -1 != -1 ? data.getLongExtra("extra_id", -1) : -1);

                        // Aquí ejecutas tu lógica para actualizar el RecyclerView
                        Log.d("MemoBoard_NewAux" , "Just Before Update Journal Notes:");
                        notificarFragmentActual(modificacion, id);
                    }
                }
        );

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0){
                    tab.setText("Notes");
                    tab.setIcon(R.drawable.copy_icon_test_3);
                }else{
                    tab.setText("Tasks");
                    tab.setIcon(R.drawable.icon_completed_task_test_11);
                }
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(Journal_Section > -1){
                    fa_btn.animate().scaleX(0.5f).scaleY(0.5f).setDuration(300).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            fa_btn.animate().scaleX(1f).scaleY(1f).setDuration(400);
                        }
                    });
                }
                Journal_Section = position;

                // Obtenemos el Fragment actual usando el ID del ViewPager2
                Fragment currentFragment = getSupportFragmentManager()
                        .findFragmentByTag("f" + position); // "f" + position es el tag por defecto que usa ViewPager2

                // Si el fragment actual implementa nuestra interfaz, le notificamos
                if (currentFragment instanceof FragmentRefreshable) {
                    ((FragmentRefreshable) currentFragment).onFragmentSelected();
                }
            }
        });

        notesFragment = new NotesFragment();
        notesFragment.setNoteFragment_Reminder_Listener(this);
        notesFragment.setNoteFragment_Out_Reminder_Listener(this);
        notesFragment.setNoteFragment_Adding_Option_Available(this);

        tasksFragment = new TasksFragment();
        tasksFragment.setTaskFragment_Adding_Option_Available(this);

        DB_N = new DB_Notes(this);
        DB_T = new DB_Tasks(this);
        Random_G = new Random_Content_Generator_For_Test();
        Stable_G = new Stable_Content_Generator_For_Test();

        selected_positions_list = new ArrayList<>();

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
        btn_generate_random_content = findViewById(R.id.button_Generate_Random_Content);
        btn_generate_stable_content = findViewById(R.id.button_Generate_Stable_Content);
        btn_delete_all_notes_database = findViewById(R.id.button_Delete_All_Notes_DataBase);
        fa_btn.startAnimation(AnimationAddNoteButton);

        floating_TrashCan_Access = findViewById(R.id.floatingMenu);
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
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
            }
        });
        btn_check_lists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go_To_Check_Lists();
            }
        });
        btn_go_trash_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Trash_Can();
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
                if(isExpanded){
                    toggle_TrashCan_Menu(false); // Cerramos el menú
                    return;
                }
                finish();
            }
        });
    }

    public ActivityResultLauncher<Intent> getLanzadorActivityC() {
        return this.lanzadorActivityC;
    }

    private void notificarFragmentActual(int modification_in_element, long element_id) {
        int indexActual = viewPager.getCurrentItem();

        // Buscamos el fragment usando el Tag por defecto de ViewPager2 ("f" + posición)
        Fragment fragmentActual = getSupportFragmentManager()
                .findFragmentByTag("f" + indexActual);

        // Si el fragment actual implementa nuestra interfaz, le decimos que se actualice
        if (fragmentActual instanceof FragmentRefreshable) {
            Log.d("MemoBoard_New_Aux", "modification_result: " + modification_in_element);
            if(modification_in_element == 0){
                ((FragmentRefreshable) fragmentActual).onFragmentElementModification(modification_in_element,element_id);
            }else if(modification_in_element == 1){
                ((FragmentRefreshable) fragmentActual).onFragmentNewElement(modification_in_element,element_id);
            }else if(modification_in_element ==2){
                ((FragmentRefreshable) fragmentActual).onFragmentElementElimination(modification_in_element,element_id);
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
            fa_btn.animate().alpha(0f).scaleY(0.7f).scaleX(0.7f).setDuration(325).withEndAction(new Runnable() {
                @Override
                public void run() {
                    fa_btn.setVisibility(View.GONE);
                    fa_btn.setClickable(false);
                    fa_btn.setFocusable(false);
                }
            });
        }else{
            fa_btn.setVisibility(View.VISIBLE);
            fa_btn.animate().alpha(1f).scaleY(1).scaleX(1).setDuration(600).withEndAction(new Runnable() {
                @Override
                public void run() {
                    fa_btn.setFocusable(true);
                    fa_btn.setClickable(true);
                }
            });
            fa_btn.startAnimation(AnimationAddNoteButton);
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(floating_TrashCan_Access, "translationX", targetX);
        ObjectAnimator animator_arrow = ObjectAnimator.ofFloat(tabArrow, "translationX", targetX_arrow);
        animator.setDuration(300); // Duración en milisegundos
        animator_arrow.setDuration(350); // Duración en milisegundos
        animator.start();
        animator_arrow.start();

        tabArrow.setText(expand ? ">" : "<");
    }
    private float dpToPx(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {//Solo verifica si el usuario toca la pantalla

            if (isExpanded && floating_TrashCan_Access != null) {//Si esta expandido se verifica en que seccion se realizo el tap

                Rect outRect = new Rect(); //Con esto se obtienen los limites de la pantalla del dispositivo
                floating_TrashCan_Access.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) { //Si el toque no ocurrio dentro de los limites del menu entonces se cancela
                    toggle_TrashCan_Menu(false); // Cerramos el menú

                    //return false;
                    //Si se retorna "true" ademas de cerrar el menu, tocara el objeto que este detras accionando su metodo
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /// Content Generation for Test:
    private void Generate_Random_Content_For_Test() {
        if(Journal_Section == 0){
            Random_G.Random_Notes_Generator(this,20);
        }else{
            Random_G.Random_Tasks_Generator(this,10);
        }
    }
    private void Generate_Stable_Content_For_Test() {
        if(Journal_Section == 0) {
            Stable_G.Stable_Note_Generator(this, 20, 0, 20, 20, 20);
        }else{
            Stable_G.Stable_Tasks_Generator(this,10, 5,0,20);
        }
    }
    private void Delete_All_Notes_From_DataBase() {
        if(Journal_Section == 0) {
            DB_N.Delete_Hard_All_Notes();
        }else {
            DB_T.Delete_Hard_All_Tasks();
        }
    }

    public void Go_To_Add_New_Note(){
        Intent goTo;
        if(Journal_Section == 0) {
            goTo = new Intent(this, MainActivity.class);
        }else{
            goTo = new Intent(this, Task_Visualizer.class);
        }
        lanzadorActivityC.launch(goTo);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    public void Go_To_Trash_Can(){
        Intent goTo = new Intent(this, Trash_Can.class);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in_trash,R.anim.slide_left_out_trash);
    }

    @Override
    public void onNoteFragment_Reminder_Open(int salida, int position, Note note) {
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        layout_dim.startAnimation(AnimationLayoutDimAppear);
    }
    @Override
    public void onNoteFragment_Out_Reminder_Open(int salida) {
        layout_dim.setVisibility(View.VISIBLE);

        if(salida == 1){///Reminder Set
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);
            return;
        }
        if(salida == 2){///Canceled
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            return;
        }

        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);
    }
    @Override
    public void onNoteFragment_Adding_Option_Available(boolean adding_option_available) {
        Add_Element_Visibility(adding_option_available);
    }
    @Override
    public void onTaskFragment_Adding_Option_Available(boolean adding_option_available) {
        Add_Element_Visibility(adding_option_available);
    }
    private void Add_Element_Visibility(boolean adding_option_available){
        if(adding_option_available){
            fa_btn.setVisibility(View.VISIBLE);
            fa_btn.setFocusable(true);
            fa_btn.setClickable(true);
            fa_btn.startAnimation(Animation_FloatingButton_Appear);
            fa_btn.animate().alpha(1f).scaleY(1f).scaleX(1f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    fa_btn.startAnimation(AnimationAddNoteButton);
                }
            });
        }else{
            fa_btn.setVisibility(View.GONE);
            fa_btn.setFocusable(false);
            fa_btn.setClickable(false);
            fa_btn.startAnimation(Animation_FloatingButton_Disappear);
        }
    }
}