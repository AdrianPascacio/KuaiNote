package com.example.kuai_notes_project;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import kotlinx.coroutines.scheduling.Task;

public class Tasks_List extends AppCompatActivity implements Recycler_Tasks_List_Interface, Recycler_Tasks_Sub_List_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener {
    RecyclerView recyclerView;
    Adapter_Recycler_Tasks_List adapter;
    private Check_ViewModel checkViewModel;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;
    ArrayList<Task_Main> taskList;
    ArrayList<Task_Sub> task_subList;
    ArrayList<Task_Element> task_elements;
    ArrayList<Task_Element> task_elements_aux;
    ArrayList<Integer> selected_positions_list;

    DB_Tasks DB_T;
    FloatingActionButton floating_button;


    long start_of_today = 0;
    ///Button btn_config, btn_check_lists;
    View main;
    View layout_dim;
    View fl_return, fl_back_ghost, fl_search_ghost, fl_generate_random_content, fl_delete_all_tasks_database;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;
    FloatingActionButton floatingActionButton;
    private Animation AnimationAddNoteButton;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel, Animation_FloatingButton_Appear, Animation_FloatingButton_Disappear;
    private FloatingActionButton fa_btn;

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

        //recyclerView = findViewById(R.id.Recycler_Check_Lists);
        //adapter = new Adapter_Recycler_Check_Lists(this,this);
        //recyclerView.setAdapter(adapter);

        //checkViewModel = new ViewModelProvider(this).get(Check_ViewModel.class);

        //Clear_Lists();


        ///recyclerView = findViewById(R.id.Recycler_MemoBoard);
        ///adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        ///recyclerView.setAdapter(adapter);

        ///Clear_Lists();
        ///Update_Recycler_View();












        Log.d("CheckList","   OnResume  ");

        //Update_Recycler_View();

        recyclerView = findViewById(R.id.Recycler_Tasks_List);
        adapter = new Adapter_Recycler_Tasks_List(this, dateEdited_list,selected_list,taskList,task_subList,task_elements,this,this);
        //adapter = new Adapter_Recycler_Memo_Board(this, dateEdited_list,selected_list,noteList,this);
        recyclerView.setAdapter(adapter);

        checkViewModel = new ViewModelProvider(this).get(Check_ViewModel.class);

        Clear_Lists();
        Update_Recycler_View();

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
        setContentView(R.layout.activity_tasks_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_navigation_bar));

        DB_T = new DB_Tasks(this);

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        taskList = new ArrayList<>();
        task_subList = new ArrayList<>();
        task_elements = new ArrayList<>();
        task_elements_aux = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();
        fa_btn = findViewById(R.id.floatingActionButton);
        main = findViewById(R.id.main);
        layout_dim = findViewById(R.id.layout_dim_itemVisualizer);

        fl_return = findViewById(R.id.FrameLayout_Return);
        fl_back_ghost = findViewById(R.id.fl_Back_Ghost);
        fl_search_ghost = findViewById(R.id.fl_Search_Ghost);
        fl_generate_random_content = findViewById(R.id.FrameLayout_Generate_Random_Content);
        fl_delete_all_tasks_database = findViewById(R.id.FrameLayout_Delete_All_Tasks_DataBase);

        floating_button = findViewById(R.id.floatingActionButton);

        AnimationAddNoteButton = AnimationUtils.loadAnimation(this,R.anim.add_note_button_zoom);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        Animation_FloatingButton_Appear = AnimationUtils.loadAnimation(this, R.anim.floating_button_appear);
        Animation_FloatingButton_Disappear = AnimationUtils.loadAnimation(this, R.anim.floating_buttton_disappear);

        ///btn_config = findViewById(R.id.button_Config);
        ///btn_check_lists = findViewById(R.id.button_Check_Lists);
        fa_btn.startAnimation(AnimationAddNoteButton);



        fl_search_ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Search_In_Tasks();
            }
        });
        fl_generate_random_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Generate_Random_Content_For_Test();
            }
        });
        fl_delete_all_tasks_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_All_Tasks_From_DataBase();
            }
        });
        fl_back_ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
            }
        });
        floating_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Go_To_Add_New_Task(view);
                //New_check_list();
                //Update_Recycler_View();
            }
        });

        ///btn_config.setOnClickListener(new View.OnClickListener() {
        ///    @Override
        ///    public void onClick(View view) {
        ///        Go_To_Trash_Can();
        ///    }
        ///});
        ///btn_check_lists.setOnClickListener(new View.OnClickListener() {
        ///    @Override
        ///    public void onClick(View view) {
        ///        Go_To_Check_Lists();
        ///    }
        ///});
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

    private void Delete_All_Tasks_From_DataBase() {
        DB_T.Delete_Hard_All_Tasks();
    }

    private void Generate_Random_Content_For_Test() {
        String seed_text = "Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro caparazón y, al alzar la cabeza, vio su vientre convexo y oscuro, surcado por curvadas callosidades, sobre el que casi no se aguantaba la colcha, que estaba a punto de escurrirse hasta el suelo. Numerosas patas, penosamente delgadas en comparación con el grosor normal de sus piernas, se agitaban sin concierto. —¿Qué me ha ocurrido? No estaba soñando. Su habitación, una habitación normal, aunque muy pequeña, tenía el aspecto habitual. Sobre la mesa había desparramado un muestrario de paños —Samsa era viajante de comercio—, y de la pared colgaba una estampa recientemente recortada de una revista ilustrada y puesta en un marco dorado. La estampa mostraba a una mujer tocada con un gorro de pieles, envuelta en una estola también de pieles, y que, muy erguida, esgrimía un amplio manguito, asimismo de piel, que ocultaba todo su antebrazo. Gregorio miró hacia la ventana; estaba nublado, y sobre el cinc del alféizar repiqueteaban las gotas de lluvia, lo que le hizo sentir una gran melancolía. «Bueno —pensó—; ¿y si siguiese durmiendo un rato y me olvidase de todas estas locuras?» Pero no era posible, pues Gregorio tenía la costumbre de dormir sobre el lado derecho, y su actual estado no le permitía adoptar tal postura. Por más que se esforzara volvía a quedar de espaldas. Intentó en vano esta operación numerosas veces; cerró los ojos para no tener que ver aquella confusa agitación de patas, que no cesó hasta que notó en el costado un dolor leve y punzante, un dolor jamás sentido hasta entonces. —¡Qué cansada es la profesión que he elegido! —se dijo—. Siempre de viaje. Las preocupaciones son mucho mayores cuando se trabaja fuera, por no hablar de las molestias propias de los viajes: estar pendiente de los enlaces de los trenes; la comida mala, irregular; relaciones que cambian constantemente, que nunca llegan a ser verdaderamente cordiales, y en las que no tienen cabida los sentimientos. ¡Al diablo con todo! Sintió en el vientre una ligera picazón. Lentamente, se estiró sobre la espalda en dirección a la cabecera de la cama, para poder alzar mejor la cabeza. Vio que el sitio que le picaba estaba cubierto de extraños puntitos blancos. Intentó rascarse con una pata; pero tuvo que retirarla inmediatamente, pues el roce le producía escalofríos. —Estoy atontado de tanto madrugar —se dijo—. No duermo lo suficiente. Hay viajantes que viven mucho mejor. Cuando a media mañana regreso a la fonda para anotar los pedidos, me los encuentro desayunando cómodamente sentados. Si yo, con el jefe que tengo, hiciese lo mismo, me despedirían en el acto. Lo cual, probablemente sería lo mejor que me podría pasar. Si no fuese por mis padres, ya hace tiempo que me hubiese marchado. Hubiera ido a ver el director y le habría dicho todo lo que pienso. Se caería de la mesa, ésa sobre la que se sienta para, desde aquella altura, hablar a los empleados, que, como es sordo, han de acercársele mucho. Pero todavía no he perdido la esperanza. En cuanto haya reunido la cantidad necesaria para pagarle la deuda de mis padres —unos cinco o seis años todavía—, me va a oír. Bueno; pero, por ahora, lo que tengo que hacer es levantarme, que el tren sale a las cinco. Eran más de las seis y media, y las manecillas seguían avanzando tranquilamente. En realidad, ya eran casi las siete menos cuarto. ¿Es que no había sonado el despertador? Desde la cama se veía que estaba puesto a las cuatro; por tanto, tenía que haber sonado. Pero ¿era posible seguir durmiendo a pesar de aquel sonido que hacía estremecer hasta los muebles? Su sueño no había sido tranquilo. Pero, por eso mismo, debía de haber dormido al final más profundamente. ¿Qué podía hacer ahora? El tren siguiente salía a las siete; para cogerlo tendría que darse muchísima prisa. El muestrario no estaba aún empaquetado, y él mismo no se sentía nada dispuesto. Además, aunque alcanzase el tren, no evitaría reprimenda del amo, pues el mozo del almacén, que había acudido al tren a las cinco,";
        for(int i = 20 ; i>=0; i--){
            long _current_time = System.currentTimeMillis();
            //int random_number = (int) (_current_time & 1023);   /// bitwise & long & 1023 (binary = 1111111111(1 diez veces)) → para tomar los numeros menores de 1023
            int random_number = (int) (_current_time & 4095);   /// bitwise & long & 4095 (binary = 111111111111 (1 doce veces)) → para tomar los numeros menores de 4095
            //int random_title = 0;
            int end_of_title = random_number & 31;  ///Bitwise & → int & 15 (binary = 1111) → para tomar los numeros menores de 15;
            boolean random_pin = (random_number & 1) == 1;
            boolean random_has_sub_task  = ((random_number >> 1) & 1)  == 1;
            boolean random_complete  = ((random_number >> 2) & 1)  == 1;

            Log.d("Random", "Main Task:  Random has sub task: " + random_has_sub_task + "    Random pin: " + random_pin + "    Random complete: " + random_complete);

            //String _title = et_Title.getText().toString();
            String _title = seed_text.substring(random_number,random_number + end_of_title);

            long save_Success;

            save_Success = DB_T.Insert_Task_L_for_test_random_generator(_current_time, _title, _title, random_pin, 0L, 0, 0,random_has_sub_task,random_complete);
            if( random_has_sub_task == true){
               for(int j = 1; j <= 29;j++){
                   boolean sub_task_random_complete  = random_complete;
                   if(random_complete == false){
                       sub_task_random_complete  = ((random_number >> j+2) & 1)  == 1;
                   }
                   Log.d("Random", "    Sub Task:  Random complete: " + sub_task_random_complete);
                   DB_T.Insert_Task_Sub_L(save_Success,j+_title,sub_task_random_complete,j);
                   if(((random_number >> j+2) & 1)  == 1) break;
               }
            }
        }
    }

    private void Save_Sub_Tasks_New_Positions() {
    }


    public void Go_To_Add_New_Task(View view){
        if(!selection_mode) {
            Intent goTo = new Intent(this, Task_Visualizer.class);
            startActivity(goTo);
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
    }
    private void Go_To_Search_In_Tasks() {
        if(!selection_mode) {
            Intent goTo = new Intent(this, Aux_Search_In_Tasks.class);
            startActivity(goTo);
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
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
        try (Cursor cursor_Tasks= DB_T.get_All_Tasks()) {
            if(cursor_Tasks.getCount()==0){
                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
            }else{
                int id_indx = cursor_Tasks.getColumnIndex("_id");
                int date_indx = cursor_Tasks.getColumnIndex("date");
                int title_indx = cursor_Tasks.getColumnIndex("title");
                int note_indx = cursor_Tasks.getColumnIndex("note");
                int pin_indx = cursor_Tasks.getColumnIndex("pin");
                int reminder_indx = cursor_Tasks.getColumnIndex("reminder");
                int reminder_type_indx = cursor_Tasks.getColumnIndex("reminder_type");
                int reminder_interval_indx = cursor_Tasks.getColumnIndex("reminder_interval");

                //while (cursor_Tasks.moveToNext()){
                //    //!!---debe actualizarse
                //    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks.getLong(id_indx));
                //    Note note = new Note(cursor_Tasks.getLong(id_indx),
                //            cursor_Tasks.getLong(date_indx),
                //            cursor_Tasks.getString(title_indx),
                //            ///BNP.Set_Body_Note_Preview(cursor_Notes.getString(title_indx),
                //            ///        cursor_Notes.getString(note_indx),
                //            ///        60,
                //            ///        55,
                //            ///        0,
                //            ///        3,
                //            ///        1,
                //            ///        30),
                //            cursor_Tasks.getString(note_indx),
                //            cursor_Tasks.getInt(pin_indx)==1,
                //            cursor_Tasks.getLong(reminder_indx),
                //            cursor_Tasks.getInt(reminder_type_indx),
                //            cursor_Tasks.getInt(reminder_interval_indx));
                //    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(note.date,start_of_today));
                //    noteOriginal_list.add(cursor_Tasks.getString(note_indx));
                //    selected_list.add(false);
                //    noteList.add(note);
                //}
                while (cursor_Tasks.moveToNext()){
                    //!!---debe actualizarse
                    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks.getLong(id_indx));
                    Task_Main task = new Task_Main(cursor_Tasks.getLong(id_indx),
                            cursor_Tasks.getLong(date_indx),
                            cursor_Tasks.getLong(2),
                            cursor_Tasks.getLong(3),
                            cursor_Tasks.getLong(4),
                            cursor_Tasks.getString(title_indx),
                            cursor_Tasks.getString(note_indx),
                            cursor_Tasks.getInt(pin_indx)==1,
                            cursor_Tasks.getLong(reminder_indx),
                            cursor_Tasks.getInt(reminder_type_indx),
                            cursor_Tasks.getInt(reminder_interval_indx),
                            cursor_Tasks.getInt(13)==1,
                            cursor_Tasks.getInt(14)==1,
                            cursor_Tasks.getInt(15)==1);
                    dateEdited_list.add(DoN.Set_Date_of_Note_Item_View(task.date,start_of_today));
                    noteOriginal_list.add(cursor_Tasks.getString(note_indx));
                    selected_list.add(false);
                    taskList.add(task);
                    task_elements.add(task);
                    if(task.has_sub_tasks && task.unfolded){
                        try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task.task_id)) {
                            if(cursor_Tasks_Sub.getCount()==0){
                                Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
                            }else{
                                int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                                int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                                int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                                int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                                int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                                while (cursor_Tasks_Sub.moveToNext()){
                                    //!!---debe actualizarse
                                    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                                            cursor_Tasks_Sub.getLong(parent_indx_sub),
                                            cursor_Tasks_Sub.getString(note_indx_sub),
                                            cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                                            cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                                    task_subList.add(task_sub);
                                    task_elements.add(task_sub);
                                    selected_list.add(false);
                                }
                                Log.d("TasksSubList","   TaskSub size:  "+ task_subList.size());
                            }
                        }
                    }
                }
            }
        }



        ///try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub()) {
        ///    if(cursor_Tasks_Sub.getCount()==0){
        ///        Log.d("Read cursor_Notes", "Cursor_Notes : readcycleplanrecord: No Entry Exist");
        ///    }else{
        ///        int id_indx = cursor_Tasks_Sub.getColumnIndex("_id");
        ///        int parent_indx = cursor_Tasks_Sub.getColumnIndex("parent_id");
        ///        int note_indx = cursor_Tasks_Sub.getColumnIndex("note");
        ///        int completed_indx = cursor_Tasks_Sub.getColumnIndex("completed");
        ///        int task_sub_position_indx = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

        ///        while (cursor_Tasks_Sub.moveToNext()){
        ///            //!!---debe actualizarse
        ///            Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks_Sub.getLong(id_indx));
        ///            Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx),
        ///                    cursor_Tasks_Sub.getLong(parent_indx),
        ///                    cursor_Tasks_Sub.getString(note_indx),
        ///                    cursor_Tasks_Sub.getInt(completed_indx)==1,
        ///                    cursor_Tasks_Sub.getInt(task_sub_position_indx));
        ///            task_subList.add(task_sub);
        ///            task_elements.add(task_sub);
        ///        }
        ///        Log.d("TasksSubList","   TaskSub size:  "+ task_subList.size());
        ///    }
        ///}



        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ///checkViewModel.getAllChecks().observe(this,checkWithSubs -> {

        ///    List<DB_Check_Main> onlyMain = new ArrayList<>();
        ///    for (Check_With_Subs item : checkWithSubs ){
        ///        Log.d("CheckList","   hh  "+item.checkMain.note);
        ///        onlyMain.add(item.checkMain);
        ///    }
        ///    adapter.setChecks(onlyMain);

        ///});
    }
    private void Clear_Lists(){
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty()){
            return;
        }
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
        taskList.clear();
        task_subList.clear();
        task_elements.clear();
    }

    public void New_check_list(){
        if(!selection_mode) {
            ///Intent goTo = new Intent(this, Main_Check_Visualizer.class);
            ///startActivity(goTo);
            ///overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

            //!!-- pasar al main check visualizer
            long _current_time = System.currentTimeMillis();
            long task_new_id = DB_T.Insert_Task_L(_current_time, "Task Title", "Task 1", false, 0, 0, 0);
            long task_sub_new_id = DB_T.Insert_Task_Sub_L(task_new_id,"sub_note",false,0);
            //checkViewModel.saveCheck("2", null);
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        if(selection_mode) {
            if(task_elements.get(position).getViewType()==0){

                Select_Item(position, v);
            }else{
                for(int i = position; i >= 0; i-- ){
                    if(task_elements.get(i).getViewType()==0){
                        Select_Item(i, v);
                    }
                }
            }
            return;
        }

        //Note _note = noteList.get(position);
        //Task _task = task_elements.get(position);
        long task_id  = 0;
        if(task_elements.get(position).getViewType() == 0){
            task_id = task_elements.get(position).getId();
        }else{
            Task_Sub task_sub = (Task_Sub) task_elements.get(position);
            task_id  = task_sub.getParent_id();

        }
        Intent goTo = new Intent(this, Task_Visualizer.class);
        goTo.putExtra("send_date_of_task",task_id);
        goTo.putExtra("send_task_id",task_id);
        startActivity(goTo);
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Override
    public void onItemHold(int position,View v) {

        Select_Item(position, v);

    }
    private void Select_Item(int position, View v) {
        selected_list.set(position,!selected_list.get(position));// invert value


        //Select sub task if the main one have subtasks:
        boolean has_sub_task = false;
        int last_subTask_change_position = 0;
        for(int i = position + 1; i <= task_elements.size() - 1; i++){
            if(task_elements.get(i).getViewType() == 1){
                selected_list.set(i,!selected_list.get(i));// invert value
                has_sub_task = true;
                last_subTask_change_position = i;
            }else{
                break;
            }
        }
        if(has_sub_task == true){
            adapter.notifyItemRangeChanged(position + 1, last_subTask_change_position);
        };




        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Disappear);

        selection_mode = selection_count > 0;

        selected_positions_list.add(0,position);


        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            //--Buscar estado del pin de las dos primeras notas seleccionadas:
            Task_Main _task_main = (Task_Main) task_elements.get(selected_positions_list.get(0));
            Task_Main _task_main2 = (Task_Main) task_elements.get(selected_positions_list.get(1));
            ///Note _note = noteList.get(selected_positions_list.get(0));
            ///Note _note2 = noteList.get(selected_positions_list.get(1));

            //pin_initial_state_MS = false;
            ///pin_initial_state_MS = _note.getPin() & _note2.getPin() || _note2.getPin(); /// AND Operator !!--Verificar si la opcion de elegir lo primero que escoja el usuario es lo mejor
            pin_initial_state_MS = _task_main.getPin() & _task_main2.getPin() || _task_main2.getPin(); /// AND Operator !!--Verificar si la opcion de elegir lo primero que escoja el usuario es lo mejor


            selection_item_menu_PopUp.setListener_dismiss(this);
            selection_item_menu_PopUp.show(v, pin_initial_state_MS);

            adapter.Change_multi_selection_state(selection_mode);
            adapter.Set_Selection_Mode_On();

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
    /// Complete Main Task:
    @Override
    public void Complete_Main_Task(int position) {
        if(task_elements.get(position).getViewType() == 0){
            Task_Main _task = (Task_Main) task_elements.get(position);


            ///Change_Complete_Main_Task_Status();
            _task.setCompleted(!_task.completed);

            long _current_time = System.currentTimeMillis();
            if(DB_T.Modify_Main_Task_Completed_Status(_task.task_id, _task.completed, _current_time)) {
                adapter.notifyItemChanged(position);
            }

            if(_task.has_sub_tasks) {
                ///!!Change_Sub_task_Completed_Status(duplicated:

                //!!-- aqui solo tiene 2 opciones, unfolded y folded. si se planea utilizar una tercera opcion debe agregarse aqui tambien una funcion para esa tercera posibilidad.
                if(_task.unfolded){
                    for(int i = position + 1 ; i <= task_elements.size() - 1 ; i ++){
                        if(task_elements.get(i).getViewType() == 1){
                            Task_Sub _task_sub = (Task_Sub) task_elements.get(i);
                            if(_task_sub.getParent_id() == _task.task_id){
                                if(_task_sub.completed != _task.completed){
                                    _task_sub.setCompleted(!_task_sub.completed);
                                    if(DB_T.Modify_Sub_Task_Completed_Status(_task_sub.task_sub_id, _task_sub.completed)){
                                        task_elements.set(i,_task_sub);
                                        adapter.notifyItemChanged(i);
                                    }

                                }
                            }
                        }else{
                            break;
                        }
                    }
                }else{
                    DB_T.Modify_All_Sub_Task_Completed_Status(_task.task_id, _task.completed);

                }
            }
        }else{
            Task_Sub _task_sub = (Task_Sub) task_elements.get(position);
        }
    }

    /// Complete Sub Task:
    @Override
    public void Complete_Sub_Task(int position) {
        Task_Sub task_sub = (Task_Sub) task_elements.get(position);
        task_sub.setCompleted(!task_sub.completed);
        if(DB_T.Modify_Sub_Task_Completed_Status(task_sub.task_sub_id, task_sub.completed)){
            task_elements.set(position,task_sub);
            adapter.notifyItemChanged(position);
        }
        ///Main_Task_Completed(received_task_id);
        int result = DB_T.Verify_If_All_Sub_Task_Completed(task_sub.parent_id);
        Log.d("Task Visualizer", "Verify if all sub task are completed: " + result);

        Task_Main _task_main= new Task_Main();
        int _task_main_position = 0;
        for(int i = position -1; i >= 0; i --){
            if(task_elements.get(i).getViewType() == 0){
                _task_main = (Task_Main) task_elements.get(i);
                _task_main_position = i;
                break;
            }
        }

        if(result > 0){
            if(!_task_main.completed){
                ///!!Change_Complete_Main_Task_Status(duplicated)
                _task_main.setCompleted(!_task_main.completed);
                task_elements.set(_task_main_position,_task_main);
                long _current_time = System.currentTimeMillis();
                if(DB_T.Modify_Main_Task_Completed_Status(_task_main.task_id, _task_main.completed, _current_time)) {
                    adapter.notifyItemChanged(_task_main_position);
                }

            }

        }else{
            if(_task_main.completed){
                ///!!Change_Complete_Main_Task_Status(duplicated)
                _task_main.setCompleted(!_task_main.completed);
                long _current_time = System.currentTimeMillis();
                task_elements.set(_task_main_position,_task_main);
                if(DB_T.Modify_Main_Task_Completed_Status(_task_main.task_id, _task_main.completed, _current_time)) {
                    adapter.notifyItemChanged(_task_main_position);
                }
            }
        }
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {
        ///Note _note = noteList.get(position);
        Task_Main _task = (Task_Main) task_elements.get(position);
        //int _pin = _note.getPin() ^ 1;      //XOR Operator


        //!!--en modo multiple seleccion, cambiar el pin dependiendo del color del pin
        //!!--no invertir todo
        if(pin_multi_change && pin_initial_state_MS ^ _task.getPin()){///XOR Operator
            selected_list.set(position,!selected_list.get(position));// invert value
            adapter.notifyItemChanged(position);
            return;
        }

        Toast.makeText(this, "Repeated pinned", Toast.LENGTH_SHORT).show();
        adapter.Change_is_repeated_value(true);

        boolean _pin = pin_multi_change ? !pin_initial_state_MS : !_task.getPin();///Ternary Operator


        if(DB_T.Modify_Pin_Status(_task.task_id,_pin)){
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(Tasks_List.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }
    public void RecyclerView_Pin_Update(int position){
        /// List:
        ///dateEdited_list.clear();
        ///noteOriginal_list.clear();
        ///selected_list.clear();
        ///noteList.clear();
        ///taskList.clear();
        ///task_subList.clear();
        ///task_elements.clear();

        Task_Main _task_main = (Task_Main) task_elements.get(position);
        ///task_elements_aux.add(task_elements.get(position));
        boolean _selected = false;
        boolean was_unfolded = _task_main.unfolded;
        //selected_list.set(position,false);
        //adapter.notifyItemChanged(position);

        if(was_unfolded){
            RecyclerView_Pin_Unfold_Update(position,false);
        }

        selected_list.remove(position);
        task_elements.remove(position);
        adapter.notifyItemChanged(position);


        int current_pinned_tasks = DB_T.get_Specific_Task_Sorted_by_Pin_and_Date(_task_main.task_id);
        Log.d("TasksList","   Task List Pin current pinned tasks:  :"+ current_pinned_tasks);


        //!!--Esta seccion debe optimizarse:
            //!!--Actualmente funciona correctamente pero puedo optimizarse:
        if(current_pinned_tasks > 0){
            int main_task_counter = 0;
            for(int i = 0; i <= task_elements.size()-1; i++ ){
                Log.d("TasksList","   Task List Unfold:  current task: "+ task_elements.get(i).getContent() + "  " +main_task_counter+"/"+current_pinned_tasks);
                if(task_elements.get(i).getViewType()==0){
                    if( main_task_counter == current_pinned_tasks){
                        ///Task_Main _task_main_before = (Task_Main) task_elements.get(i-1);
                        ///Log.d("TasksList","   Task List Unfold:  task before: "+ _task_main_before.getNote());
                        //if(_task_main_before.unfolded){
                            //--If the previous main task have sub task count them and add it to the count:
                            ///for(int j = i; j <= task_elements.size()-1; j++ ){
                            ///    ///for(int j = i+1; j <= task_elements.size()-1; j++ ){
                            ///    if(task_elements.get(j).getViewType()==0) {
                            ///        Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(j).getViewType() + "  j:" + j);
                            ///        current_pinned_tasks = j;
                            ///        break;
                            ///    }
                            ///}

                            ///break;
                        {
                            //--approved
                            Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(i).getContent() + "  i:" + i);
                            //current_pinned_tasks = i ;
                            current_pinned_tasks = main_task_counter ;
                            break;
                        }
                    }

                    main_task_counter ++;

                }else{
                    current_pinned_tasks ++;

                }

            }
        }


        _task_main.setPin(!_task_main.getPin());

        task_elements.add(current_pinned_tasks,_task_main);
        selected_list.add(current_pinned_tasks,_selected);
        adapter.notifyItemMoved(position,current_pinned_tasks);
        adapter.notifyItemChanged(current_pinned_tasks);


        if(was_unfolded){
            RecyclerView_Pin_Unfold_Update(current_pinned_tasks,true);
        }


        Restart_Selection();

    }

    private void RecyclerView_Pin_Unfold_Update(int position, boolean unfolded) {
        ///Log.d("TasksList","   Task List Unfold:  now unfolded is:"+ unfolded);
        Task_Main _task = (Task_Main) task_elements.get(position);
        /// Fold
        if(!unfolded){
            //--Delete all sub task from the task_elements list and update
            for(int i = position + 1; i <= task_elements.size()-1 ; i ++){
                Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(i).getViewType());
                if (task_elements.get(i).getViewType() == 1) {
                    Task_Sub _task_sub = (Task_Sub) task_elements.get(i);
                    if (_task_sub.getParent_id() == _task.getTask_id()) {
                        Log.d("TasksList","     Task List Unfold:  sub task description deleted: "+ task_elements.get(i).getContent());
                        task_elements_aux.add(task_elements.get(i));
                        task_elements.remove(i);
                        selected_list.remove(i);
                        adapter.notifyItemRemoved(i);
                        i--;
                        //!!--Verify if, in the adapter, remove one by one is better than remove a range
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            adapter.notifyItemChanged(position);
        }
        ///Unfold
        if(unfolded){
            Log.d("TasksSubList","--------(Pin Unfolded)--------------Task Elements Aux size:  " + task_elements_aux.size());
            int sub_task_elements_size = task_elements_aux.size();
            Log.d("TasksSubList","   Task Base Position:  " + position);
            for(int i = task_elements_aux.size()-1; i >= 0 ; i --){

                task_elements.add(position+1 ,task_elements_aux.get(i));
                selected_list.add(position+1 ,false);

                adapter.notifyItemInserted(position + 1);
                //Log.d("TasksSubList","      Task first Elements content update:  "+ task_elements.get(position + 1).getContent());
                //adapter.notifyItemRangeInserted(position+1,position+cursor_Tasks_Sub.getCount());
                ///adapter.notifyItemRangeInserted(position+1,position+1+task_elements_aux.size()-1);
                ///adapter.notifyItemChanged(position);
                //Log.d("TasksSubList","      Task Elements size:  "+ task_elements.size()+"    Task content:  "+ task_elements_aux.get(i).getContent());

            }
            Log.d("TasksSubList","      Task first Elements content update:  "+ task_elements.get(position + 1).getContent());
            Log.d("TasksSubList","      Task last Elements content update:  "+ task_elements.get(position + 1+ sub_task_elements_size - 1).getContent());
            ///adapter.notifyItemRangeInserted(position + 1, position + 1 +  sub_task_elements_size - 1 );
            task_elements_aux.clear();
        }
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
    public void onPopupClosed(int salida, int postion) {
        layout_dim.setVisibility(View.VISIBLE);
        Restart_Selection();
        if(salida == 1){//setter
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);

            Toast.makeText(this, "reminder dismiss"+"setter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(salida == 2){//cancel
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            Toast.makeText(this, "reminder dismiss"+"cancel", Toast.LENGTH_SHORT).show();

            return;
        }
        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);

        Toast.makeText(this, "reminder dismiss"+"normal", Toast.LENGTH_SHORT).show();
    }

    /// Unfold
    @Override
    public void Unfold(int position, long element_id) {
        //Task_Main _task = DB_T.getASpecificTask(element_id);
        Task_Main _task = null;
        
        for( int i = taskList.size() - 1; i>=0;i-- ){
            if(taskList.get(i).getTask_id() == element_id){
                 _task = taskList.get(i);
                _task.setUnfolded(!_task.unfolded);
                taskList.set(i,_task);
            }
        }
        //Task_Main _task = taskList.get(position);





        if(DB_T.Modify_Unfold_Status(_task.task_id,_task.unfolded)){
            RecyclerView_Unfold_Update(position,_task.unfolded);
        }else{
            Toast.makeText(Tasks_List.this, "Not_Unfold_Modified", Toast.LENGTH_SHORT).show();
        }

    }

    private void RecyclerView_Unfold_Update(int position, boolean unfolded) {
        Log.d("TasksList","   Task List Unfold:  now unfolded is:"+ unfolded);
        //!!--seems to be adapted to note and not for a task:
        Task_Main _task = (Task_Main) task_elements.get(position);
        boolean Main_IsSelected = selected_list.get(position);
        /// Fold
        if(!unfolded){
            //--Delete all sub task from the task_elements list and update
            for(int i = position + 1; i <= task_elements.size()-1 ; i ++){
                Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(i).getViewType());
                Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(i).getViewType());
                if (task_elements.get(i).getViewType() == 1) {
                    Task_Sub _task_sub = (Task_Sub) task_elements.get(i);
                    if (_task_sub.getParent_id() == _task.getTask_id()) {
                        Log.d("TasksList","   Task List Unfold:  sub task description:"+ task_elements.get(i).getContent());
                        task_elements.remove(i);
                        selected_list.remove(i);
                        adapter.notifyItemRemoved(i);
                        i--;
                        //!!--Verify if, in the adapter, remove one by one is better than remove a range
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            adapter.notifyItemChanged(position);
        }
        /// Unfold
        if(unfolded){
            //--Look for all the subs task that have for parent the present main task (bring a cursor), add them in the position

            try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(_task.task_id)) {
                if(cursor_Tasks_Sub.getCount()==0){
                    Log.d("Read cursor_Tasks", "Cursor_Tasks : readcycleplanrecord: No Entry Exist");
                }else{
                    int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                    int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                    int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                    int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                    int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                    Log.d("Read cursor_Task", "    TaskMain_id: " + _task.getContent());
                    while (cursor_Tasks_Sub.moveToNext()){
                        //!!---debe actualizarse
                        Log.d("Read cursor_Task", "    Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                        Log.d("Read cursor_Task", "    Task_content: " + cursor_Tasks_Sub.getString(note_indx_sub));
                        Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                                cursor_Tasks_Sub.getLong(parent_indx_sub),
                                cursor_Tasks_Sub.getString(note_indx_sub),
                                cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                                cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                        //task_subList.add(task_sub);
                        task_elements.add(position+1+ cursor_Tasks_Sub.getPosition(),task_sub);
                        selected_list.add(position+1+ cursor_Tasks_Sub.getPosition(), Main_IsSelected ? true :false);///TERNARY Operator;
                    }
                    //adapter.notifyItemRangeInserted(position+1,position+cursor_Tasks_Sub.getCount());
                    adapter.notifyItemRangeInserted(position+1,cursor_Tasks_Sub.getCount());
                    adapter.notifyItemChanged(position);
                    Log.d("TasksSubList","   TaskSub size:  "+ task_elements.size());
                }
            }


















        }
        //Task_Sub _task_sub =
        ///Task_Main _task = (Task_Main) task_elements.get(position);
        ///String _date= dateEdited_list.get(position);
        ///String _noteOriginal= noteOriginal_list.get(position);
        ///boolean _selected=false;
        ///selected_list.set(position,false);
        ///adapter.notifyItemChanged(position);

        ///dateEdited_list.remove(position);
        ///noteOriginal_list.remove(position);
        ///selected_list.remove(position);

        ///noteList.remove(position);

        ///int current_pinned_notes = DB_T.get_Specific_Note_Sorted_by_Pin_and_Date(_note.note_id);
        /////Log.d("Pin","   current_pin:" + current_pinned_notes+ "    position:" + position);

        ///dateEdited_list.add(current_pinned_notes,_date);
        ///noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        /////--cambio de estado con referencia al anterior de (0 a 1)
        /////_note.setPin(_note.getPin() ^ 1);       //XOR Operator
        ///_note.setPin(!_note.getPin());
        ///noteList.add(current_pinned_notes,_note);
        ///selected_list.add(current_pinned_notes,_selected);
        ///adapter.notifyItemMoved(position,current_pinned_notes);
        ///adapter.notifyItemChanged(current_pinned_notes);

        ///Restart_Selection();
    }

    @Override
    public void RemoveItem(int position) {
        Task_Main _task = (Task_Main) task_elements.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main,_task.task_id,1,_task.reminder);

        //!!--Verificar si es mas viable crear otro metodo en DB_Tasks para mover el Main Tasks sin modificaciones.
        if(DB_T.Send_Task_To_Trash(_task.task_id,_task.date,_task.note,_task.note,_task.pin,20,_task.completed,_task.has_sub_tasks)){
            if(_task.has_sub_tasks){
                DB_T.Send_Previous_Sub_Task_To_Trash_With_Out_Modification(_task.task_id);
                if(_task.unfolded){
                    for(int i = position + 1; i <= task_elements.size() - 1 ;i++){
                        if(task_elements.get(i).getViewType()==0){
                            break;
                        }
                        task_elements.remove(i);
                        selected_list.remove(i);
                        i--;
                        adapter.notifyItemRemoved(position);
                    }
                }
            }

            dateEdited_list.remove(position);
            noteOriginal_list.remove(position);
            selected_list.remove(position);
            ///noteList.remove(position);
            taskList.remove(position);
            ///task_subList.remove(position);
            task_elements.remove(position);


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
        adapter.Set_Selection_Mode_Off();
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

    @Override
    public void onMemoBoardSelection_PopupClosed(int option) {
        if(option == 1){
            Toast.makeText(this, "pin", Toast.LENGTH_SHORT).show();

            pin_multi_change = true;

            if(pin_initial_state_MS){
                for(int i = selected_list.size()-1;i >= 0; i--) {
                    if (task_elements.get(i).getViewType() == 0 && selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }else{
                for(int i = 0;i < selected_list.size(); i++) {
                    if (task_elements.get(i).getViewType() == 0 && selected_list.get(i)) {
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
    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
    }
}