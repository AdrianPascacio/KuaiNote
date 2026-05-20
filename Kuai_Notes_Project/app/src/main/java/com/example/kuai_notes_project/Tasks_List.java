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
import androidx.appcompat.app.AppCompatActivity;
import androidx.benchmark.junit4.BenchmarkRule;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/// 1168 v9.0B, 930 just cleaning
public class Tasks_List extends AppCompatActivity implements Recycler_Tasks_List_Interface, Recycler_Tasks_Sub_List_Interface, Reminder_PopUpWindow_Tasks.OnValueSelectedListener,Reminder_PopUpWindow_Tasks.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener {
    RecyclerView recyclerView;
    Adapter_Recycler_Tasks_List adapter;
    //private Check_ViewModel checkViewModel;
    private Random_Content_Generator_For_Test Random_G;
    private Stable_Content_Generator_For_Test Stable_G;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Task_Element> task_elements;
    ArrayList<Task_Element> task_elements_aux;
    ArrayList<Integer> selected_positions_list;

    DB_Tasks DB_T;
    FloatingActionButton floating_button;


    long start_of_today = 0;
    ///Button btn_config, btn_check_lists;
    View main;
    View layout_dim;
    View fl_return, fl_back_ghost, fl_search_ghost, fl_generate_random_content, fl_generate_stable_content, fl_delete_all_tasks_database;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;

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
        super.onResume()        ///Delete_Task();
        ///Return_To_Task_List();
;
        getStartOfToday();

        recyclerView = findViewById(R.id.Recycler_Tasks_List);
        adapter = new Adapter_Recycler_Tasks_List(this,selected_list,task_elements,this,this);
        recyclerView.setAdapter(adapter);

        //checkViewModel = new ViewModelProvider(this).get(Check_ViewModel.class);

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

        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        task_elements = new ArrayList<>();
        task_elements_aux = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();
        Random_G = new Random_Content_Generator_For_Test();
        Stable_G = new Stable_Content_Generator_For_Test();
        fa_btn = findViewById(R.id.floatingActionButton);
        main = findViewById(R.id.main);
        layout_dim = findViewById(R.id.layout_dim_itemVisualizer);

        fl_return = findViewById(R.id.FrameLayout_Return);
        fl_back_ghost = findViewById(R.id.fl_Back_Ghost);
        fl_search_ghost = findViewById(R.id.fl_Search_Ghost);
        fl_generate_random_content = findViewById(R.id.FrameLayout_Generate_Random_Content);
        fl_generate_stable_content = findViewById(R.id.FrameLayout_Generate_Stable_Content);
        fl_delete_all_tasks_database = findViewById(R.id.FrameLayout_Delete_All_Tasks_DataBase);

        floating_button = findViewById(R.id.floatingActionButton);

        AnimationAddNoteButton = AnimationUtils.loadAnimation(this,R.anim.add_note_button_zoom);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        Animation_FloatingButton_Appear = AnimationUtils.loadAnimation(this, R.anim.floating_button_appear);
        Animation_FloatingButton_Disappear = AnimationUtils.loadAnimation(this, R.anim.floating_buttton_disappear);

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
        fl_generate_stable_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Generate_Stable_Content_For_Test();
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

    private void Generate_Random_Content_For_Test() {
        Random_G.Random_Tasks_Generator(this,10);
    }
    private void Generate_Stable_Content_For_Test() {
        Stable_G.Stable_Tasks_Generator(this,10, 10,0,100);
    }
    private void Generate_Random_Content_For_Test_Old() {
        String seed_text = "Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro caparazón y, al alzar la cabeza, vio su vientre convexo y oscuro, surcado por curvadas callosidades, sobre el que casi no se aguantaba la colcha, que estaba a punto de escurrirse hasta el suelo. Numerosas patas, penosamente delgadas en comparación con el grosor normal de sus piernas, se agitaban sin concierto. —¿Qué me ha ocurrido? No estaba soñando. Su habitación, una habitación normal, aunque muy pequeña, tenía el aspecto habitual. Sobre la mesa había desparramado un muestrario de paños —Samsa era viajante de comercio—, y de la pared colgaba una estampa recientemente recortada de una revista ilustrada y puesta en un marco dorado. La estampa mostraba a una mujer tocada con un gorro de pieles, envuelta en una estola también de pieles, y que, muy erguida, esgrimía un amplio manguito, asimismo de piel, que ocultaba todo su antebrazo. Gregorio miró hacia la ventana; estaba nublado, y sobre el cinc del alféizar repiqueteaban las gotas de lluvia, lo que le hizo sentir una gran melancolía. «Bueno —pensó—; ¿y si siguiese durmiendo un rato y me olvidase de todas estas locuras?» Pero no era posible, pues Gregorio tenía la costumbre de dormir sobre el lado derecho, y su actual estado no le permitía adoptar tal postura. Por más que se esforzara volvía a quedar de espaldas. Intentó en vano esta operación numerosas veces; cerró los ojos para no tener que ver aquella confusa agitación de patas, que no cesó hasta que notó en el costado un dolor leve y punzante, un dolor jamás sentido hasta entonces. —¡Qué cansada es la profesión que he elegido! —se dijo—. Siempre de viaje. Las preocupaciones son mucho mayores cuando se trabaja fuera, por no hablar de las molestias propias de los viajes: estar pendiente de los enlaces de los trenes; la comida mala, irregular; relaciones que cambian constantemente, que nunca llegan a ser verdaderamente cordiales, y en las que no tienen cabida los sentimientos. ¡Al diablo con todo! Sintió en el vientre una ligera picazón. Lentamente, se estiró sobre la espalda en dirección a la cabecera de la cama, para poder alzar mejor la cabeza. Vio que el sitio que le picaba estaba cubierto de extraños puntitos blancos. Intentó rascarse con una pata; pero tuvo que retirarla inmediatamente, pues el roce le producía escalofríos. —Estoy atontado de tanto madrugar —se dijo—. No duermo lo suficiente. Hay viajantes que viven mucho mejor. Cuando a media mañana regreso a la fonda para anotar los pedidos, me los encuentro desayunando cómodamente sentados. Si yo, con el jefe que tengo, hiciese lo mismo, me despedirían en el acto. Lo cual, probablemente sería lo mejor que me podría pasar. Si no fuese por mis padres, ya hace tiempo que me hubiese marchado. Hubiera ido a ver el director y le habría dicho todo lo que pienso. Se caería de la mesa, ésa sobre la que se sienta para, desde aquella altura, hablar a los empleados, que, como es sordo, han de acercársele mucho. Pero todavía no he perdido la esperanza. En cuanto haya reunido la cantidad necesaria para pagarle la deuda de mis padres —unos cinco o seis años todavía—, me va a oír. Bueno; pero, por ahora, lo que tengo que hacer es levantarme, que el tren sale a las cinco. Eran más de las seis y media, y las manecillas seguían avanzando tranquilamente. En realidad, ya eran casi las siete menos cuarto. ¿Es que no había sonado el despertador? Desde la cama se veía que estaba puesto a las cuatro; por tanto, tenía que haber sonado. Pero ¿era posible seguir durmiendo a pesar de aquel sonido que hacía estremecer hasta los muebles? Su sueño no había sido tranquilo. Pero, por eso mismo, debía de haber dormido al final más profundamente. ¿Qué podía hacer ahora? El tren siguiente salía a las siete; para cogerlo tendría que darse muchísima prisa. El muestrario no estaba aún empaquetado, y él mismo no se sentía nada dispuesto. Además, aunque alcanzase el tren, no evitaría reprimenda del amo, pues el mozo del almacén, que había acudido al tren a las cinco,";
        for(int i = 20 ; i>=0; i--){
            long _current_time = System.currentTimeMillis();
            //int random_number = (int) (_current_time & 1023);   /// bitwise & long & 1023 (binary = 1111111111(1 diez veces)) → para tomar los numeros menores de 1023
            int random_number = (int) (_current_time & 4095);   /// bitwise & long & 4095 (binary = 111111111111 (1 doce veces)) → para tomar los numeros menores de 4095
            int end_of_title = random_number & 31;  ///Bitwise & → int & 15 (binary = 1111) → para tomar los numeros menores de 15;
            boolean random_pin = (random_number & 1) == 1;
            boolean random_has_sub_task  = ((random_number >> 1) & 1)  == 1;
            boolean random_complete  = ((random_number >> 2) & 1)  == 1;

            Log.d("Random", "Main Task:  Random has sub task: " + random_has_sub_task + "    Random pin: " + random_pin + "    Random complete: " + random_complete);

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
                int date_created_indx = cursor_Tasks.getColumnIndex("date_created");
                int date_modified_indx = cursor_Tasks.getColumnIndex("date_modified");
                int date_completed_indx = cursor_Tasks.getColumnIndex("date_completed");
                int title_indx = cursor_Tasks.getColumnIndex("title");
                int note_indx = cursor_Tasks.getColumnIndex("note");
                int pin_indx = cursor_Tasks.getColumnIndex("pin");
                int reminder_indx = cursor_Tasks.getColumnIndex("reminder");
                int reminder_type_indx = cursor_Tasks.getColumnIndex("reminder_type");
                int completed_indx = cursor_Tasks.getColumnIndex("completed");
                int has_sub_tasks_indx = cursor_Tasks.getColumnIndex("has_sub_tasks");
                int unfolded_indx = cursor_Tasks.getColumnIndex("unfolded");
                int reminder_interval_indx = cursor_Tasks.getColumnIndex("reminder_interval");

                while (cursor_Tasks.moveToNext()){
                    //!!---debe actualizarse
                    Log.d("Read cursor_Notes", " Task_id: " + cursor_Tasks.getLong(id_indx));
                    Task_Main task = new Task_Main(cursor_Tasks.getLong(id_indx),
                            cursor_Tasks.getLong(date_indx),
                            cursor_Tasks.getLong(date_created_indx),
                            cursor_Tasks.getLong(date_modified_indx),
                            cursor_Tasks.getLong(date_completed_indx),
                            cursor_Tasks.getString(title_indx),
                            cursor_Tasks.getString(note_indx),
                            cursor_Tasks.getInt(pin_indx)==1,
                            cursor_Tasks.getLong(reminder_indx),
                            cursor_Tasks.getInt(reminder_type_indx),
                            cursor_Tasks.getInt(reminder_interval_indx),
                            cursor_Tasks.getInt(completed_indx)==1,
                            cursor_Tasks.getInt(has_sub_tasks_indx)==1,
                            cursor_Tasks.getInt(unfolded_indx)==1);
                    noteOriginal_list.add(cursor_Tasks.getString(note_indx));
                    selected_list.add(false);
                    task_elements.add(task);
                    if(task.has_sub_tasks && task.unfolded){//!!--has sub task condition is unnecessary
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
                                    Log.d("Read cursor_Tasks", " Sub_Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                                            cursor_Tasks_Sub.getLong(parent_indx_sub),
                                            cursor_Tasks_Sub.getString(note_indx_sub),
                                            cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                                            cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                                    task_elements.add(task_sub);
                                    selected_list.add(false);
                                }
                            }
                        }
                    }
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
        noteOriginal_list.clear();
        selected_list.clear();
        task_elements.clear();
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
                        break;
                    }
                }
            }
            return;
        }

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
    @Override
    public void onItemHold_Sub_Task(int position,View v) {

        for(int i = position -1 ; i >= 0; i--){
            if(task_elements.get(i).getViewType()==0) {
                Select_Item(i, v);
                break;
            }
        }

    }
    private void Select_Item(int position, View v) {
        selected_list.set(position,!selected_list.get(position));// invert value


        ///--Select sub task if the main one have subtasks:
        int sub_task_selected_count = 0;
        for(int i = position + 1; i <= task_elements.size() - 1; i++){
            if (task_elements.get(i).getViewType() != 1) break;
            selected_list.set(i,!selected_list.get(i));// invert value
            sub_task_selected_count ++;
        }
        if(sub_task_selected_count > 0){
            adapter.notifyItemRangeChanged(position + 1, sub_task_selected_count);
        };

        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Disappear);


        selection_mode = selection_count > 0;

        selected_positions_list.add(0,position);


        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            //--Buscar estado del pin de las dos primeras notas seleccionadas:
            Task_Main _task_main = (Task_Main) task_elements.get(selected_positions_list.get(0));
            //!!---Error al unfold y luego seleccionar, creo que el orden cambia y se choca con otro task que no era el original
            Task_Main _task_main2 = (Task_Main) task_elements.get(selected_positions_list.get(1));

            //pin_initial_state_MS = false;
            pin_initial_state_MS = _task_main.getPin() & _task_main2.getPin() || _task_main2.getPin(); /// AND Operator !!--Verificar si la opcion de elegir lo primero que escoja el usuario es lo mejor


            selection_item_menu_PopUp.setListener_dismiss(this);
            selection_item_menu_PopUp.show(v, pin_initial_state_MS);

            adapter.Change_multi_selection_state(selection_mode);
            adapter.Set_Selection_Mode_On();

            adapter.notifyItemChanged(position,this);
            adapter.notifyItemChanged(selected_positions_list.get(1),this);//!!se estan desvaneciendo sin las animaciones

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

    /// Change Tasks Completed Status:
    @Override
    public void Complete_Main_Task(int position) {
        Task_Main _task = (Task_Main) task_elements.get(position);

        Change_Complete_Main_Task_Status(_task, position);

        if(_task.has_sub_tasks) {
            //!!-- aqui solo tiene 2 opciones, unfolded y folded. si se planea utilizar una tercera opcion para ver solo parcialmente los subtask, debe agregarse aqui tambien una funcion para esa tercera posibilidad.
            if(_task.unfolded){
                for(int i = position + 1 ; i <= task_elements.size() - 1 ; i ++){

                    if(task_elements.get(i).getViewType() == 0) break; ///Break if is a Main Task

                    Task_Sub _task_sub = (Task_Sub) task_elements.get(i);
                    if(_task_sub.completed != _task.completed){
                        Change_Sub_Task_Completed_Status(i, _task_sub);
                    }
                }
            }else{
                DB_T.Modify_All_Sub_Task_Completed_Status(_task.task_id, _task.completed);
            }
        }
    }

    @Override
    public void Complete_Sub_Task(int position) {
        Task_Sub task_sub = (Task_Sub) task_elements.get(position);
        Change_Sub_Task_Completed_Status(position, task_sub);
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
            if(_task_main.completed) return;
            Change_Complete_Main_Task_Status(_task_main, _task_main_position);
        }else{
            if(!_task_main.completed) return;
            Change_Complete_Main_Task_Status(_task_main, _task_main_position);
        }
    }
    private void Change_Complete_Main_Task_Status(Task_Main _task_main, int _task_main_position) {
        _task_main.setCompleted(!_task_main.completed);///cambio en task_elements al ser un puntero.
        long _current_time = System.currentTimeMillis();
        if(DB_T.Modify_Main_Task_Completed_Status(_task_main.task_id, _task_main.completed, _current_time)) {
            adapter.notifyItemChanged(_task_main_position);
        }
    }
    private void Change_Sub_Task_Completed_Status(int position, Task_Sub task_sub) {
        task_sub.setCompleted(!task_sub.completed);///cambio en task_elements al ser un puntero.
        if(DB_T.Modify_Sub_Task_Completed_Status(task_sub.task_sub_id, task_sub.completed)){
            adapter.notifyItemChanged(position);
        }
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {

        long start_nano_time = System.nanoTime();
        Task_Main _task = (Task_Main) task_elements.get(position);

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
        }
        long end_nano_time = System.nanoTime();
        long bench_time = end_nano_time - start_nano_time;
        Log.d("TasksList","   Time Consumed in pin:  "+ bench_time/1000);

    }
    public void RecyclerView_Pin_Update(int position){

        Task_Main _task_main = (Task_Main) task_elements.get(position);

        boolean was_unfolded = _task_main.unfolded;

        if(was_unfolded){
            RecyclerView_Pin_Unfold_Update(position,false, _task_main.task_id);
        }

        selected_list.remove(position);
        task_elements.remove(position);
        adapter.notifyItemChanged(position);


        int current_pinned_tasks = DB_T.get_Specific_Task_Sorted_by_Pin_and_Date(_task_main.task_id);
        ///Log.d("TasksList","   Task List Pin current pinned tasks:  :"+ current_pinned_tasks);


        //!!--Esta seccion debe optimizarse:
        //!!--Actualmente funciona correctamente pero puedo optimizarse:
        if(current_pinned_tasks > 0){
            int main_task_counter = 0;
            for(int i = 0; i <= task_elements.size()-1; i++ ){
                ///Log.d("TasksList","   Task List Unfold:  current task: "+ task_elements.get(i).getContent() + "  " +main_task_counter+"/"+current_pinned_tasks);
                if(task_elements.get(i).getViewType()==0){
                    if( main_task_counter == current_pinned_tasks) {
                        //--approved
                        ///Log.d("TasksList", "   Task List Unfold:  task_element:" + task_elements.get(i).getContent() + "  i:" + i);
                        break;
                    }
                    main_task_counter ++;
                }else{
                    current_pinned_tasks ++;
                }
            }
        }

        _task_main.setPin(!_task_main.getPin());

        task_elements.add(current_pinned_tasks,_task_main);
        selected_list.add(current_pinned_tasks,false);
        adapter.notifyItemMoved(position,current_pinned_tasks);
        adapter.notifyItemChanged(current_pinned_tasks);

        if(was_unfolded){
            RecyclerView_Pin_Unfold_Update(current_pinned_tasks,true, _task_main.task_id);
        }

        Restart_Selection();

    }

    private void RecyclerView_Pin_Unfold_Update(int position, boolean unfolded, long _task_main_id) {
        /// Fold
        if(!unfolded){   //--Delete all sub task from the task_elements list and update

            int sub_tasks_count = 0;
            while( position + 1 <= task_elements.size()-1){
                if (task_elements.get(position+1).getViewType() == 0) break;
                task_elements_aux.add(task_elements.get(position +1));
                task_elements.remove(position +1);
                selected_list.remove(position +1);
                sub_tasks_count ++;
            }
            //int i = position + 1;
            //while (i <= task_elements.size()-1){
            //    if (task_elements.get(i).getViewType() == 0) break;
            //    sub_tasks_count ++;
            //    i++;
            //}
            //while (i + 1 >=  position +1) {
            //    task_elements_aux.add(task_elements.get(i));
            //    task_elements.remove(i);
            //    selected_list.remove(i);
            //    i--;
            //}
            ///for(int i = position + 1; i <= task_elements.size()-1 ; i ++){
            ///    //Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(i).getViewType());
            ///    if (task_elements.get(i).getViewType() == 0) break;
            ///    //Log.d("TasksList","     Task List Unfold:  sub task description deleted: "+ task_elements.get(i).getContent());
            ///    task_elements_aux.add(task_elements.get(i));
            ///    task_elements.remove(i);
            ///    selected_list.remove(i);
            ///    sub_tasks_count ++;
            ///    i--;
            ///}
            adapter.notifyItemRangeRemoved(position+1,sub_tasks_count);
            adapter.notifyItemChanged(position);

        }
        ///Unfold
        if(unfolded){
            //Log.d("TasksSubList","--------(Pin Unfolded)--------------Task Elements Aux size:  " + task_elements_aux.size());
            int sub_task_elements_size = task_elements_aux.size();
            //Log.d("TasksSubList","   Task Base Position:  " + position);
            for(int i = task_elements_aux.size()-1; i >= 0 ; i --){
                task_elements.add(position+1 ,task_elements_aux.get(i));
                selected_list.add(position+1 ,false);
            }
            //adapter.notifyItemRangeInserted(position + 1,position + 1+task_elements_aux.size()-1);
            adapter.notifyItemRangeInserted(position + 1,task_elements_aux.size());
            //Log.d("TasksSubList","      Task first Elements content update:  "+ task_elements.get(position + 1).getContent());
            //Log.d("TasksSubList","      Task last Elements content update:  "+ task_elements.get(position + 1+ sub_task_elements_size - 1).getContent());
            task_elements_aux.clear();
        }
    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
        //!!--Actualmente por dise;o no se esta utilizando en esta activity.
        layout_dim.setVisibility(View.VISIBLE);
        layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
        layout_dim.startAnimation(AnimationLayoutDimAppear);

        adapter.Change_is_repeated_value(true);
        Reminder_PopUpWindow_Tasks reminder_PopUp = new Reminder_PopUpWindow_Tasks(this, position);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        Task_Main _task_main = (Task_Main) task_elements.get(position);
        reminder_PopUp.show(main, _task_main);
    }
    @Override
    public void OnValueSelected(int position, long alarm_time) {
        Task_Main _task_main = (Task_Main) task_elements.get(position);
        Log.d("TasksList","   Before Reminder:  "+ _task_main.reminder);
        selected_list.set(position,false);

        _task_main.setReminder(alarm_time);
        Task_Main _task_main2 = (Task_Main) task_elements.get(position);
        Log.d("TasksList","   After Reminder:  "+ _task_main2.reminder);
        //!!--Verificar si realmente se esta cambiando para eliminar lineas innecesarias


        //!!---- actualizar type and interval
        _task_main.setReminder_type(0);
        _task_main.setReminder_interval(0);
        task_elements.remove(position);
        task_elements.add(position,_task_main);
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
        long start_nano_time = System.nanoTime();
        Unfold_New(position,element_id);
        long end_nano_time = System.nanoTime();
        long bench_time = end_nano_time - start_nano_time;
        Log.d("TasksList","   Time Consumed in unfold:  "+ bench_time/1000);
    }
    public void Unfold_New(int position, long element_id) {
        Task_Main _task = (Task_Main) task_elements.get(position);
        _task.setUnfolded(!_task.unfolded);

        if(DB_T.Modify_Unfold_Status(_task.task_id,_task.unfolded)){
            RecyclerView_Unfold_Update(position,_task.unfolded, _task.task_id);
        }
    }
    private void RecyclerView_Unfold_Update(int position, boolean unfolded, long task_id) {
        //Log.d("TasksList","   Task List Unfold:  now unfolded is:"+ unfolded);
        boolean Main_IsSelected = selected_list.get(position);
        /// Fold
        if(!unfolded){
            //--Delete all sub task from the task_elements list and update
            int sub_tasks_count = 0;
            while(position+1 <= task_elements.size()-1){
                if (task_elements.get(position+1).getViewType() == 0) break;
                task_elements.remove(position+1);
                selected_list.remove(position +1);
                sub_tasks_count ++;
            }
            adapter.notifyItemRangeRemoved(position+1,sub_tasks_count);
            /// Correction when the user fold/unfold before choose the second multiselection item:
            if(selected_positions_list.size()==1){
                if(position < selected_positions_list.get(0)){
                    selected_positions_list.set(0,selected_positions_list.get(0)-sub_tasks_count);
                }
            }
        }
        /// Unfold
        if(unfolded){
            //--Look for all the subs task that have for parent the present main task (bring a cursor), add them in the position

            try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task_id)) {
                if(cursor_Tasks_Sub.getCount()==0) return;

                int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                //Log.d("Read cursor_Task", "    TaskMain_id: " + task_elements.get(position).getContent());
                while (cursor_Tasks_Sub.moveToNext()){
                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                            cursor_Tasks_Sub.getLong(parent_indx_sub),
                            cursor_Tasks_Sub.getString(note_indx_sub),
                            cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                            cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                    task_elements.add(position+1+ cursor_Tasks_Sub.getPosition(),task_sub);
                    selected_list.add(position+1+ cursor_Tasks_Sub.getPosition(), Main_IsSelected ? true :false);///TERNARY Operator;
                    //adapter.notifyItemInserted(position+1+cursor_Tasks_Sub.getPosition());
                }
                Log.d("TasksSubList","   SubTask Count:  "+ cursor_Tasks_Sub.getCount() );
                adapter.notifyItemRangeInserted(position+1,cursor_Tasks_Sub.getCount());
                /// Correction when the user fold/unfold before choose the second multiselection item:
                if(selected_positions_list.size()==1){
                    if(position < selected_positions_list.get(0)){
                        selected_positions_list.set(0,selected_positions_list.get(0)+cursor_Tasks_Sub.getCount());
                    }
                }
                Log.d("TasksSubList","   NotifyItemRangeInserted:  "+ task_elements.get(position+1).getContent()+ "    to:"+task_elements.get(position+1+cursor_Tasks_Sub.getCount()-1).getContent() );
            }
        }
        adapter.notifyItemChanged(position);
    }
    private void RecyclerView_Unfold_Update_Err(int position, boolean unfolded, long task_id) {
        //Log.d("TasksList","   Task List Unfold:  now unfolded is:"+ unfolded);
        boolean Main_IsSelected = selected_list.get(position);
        /// Fold
        if(!unfolded){

            int sub_tasks_count = 0;
            for(int i = position + 1; i <= task_elements.size()-1 ; i ++){
                //Log.d("TasksList","   Task List Unfold:  task_element:"+ task_elements.get(i).getViewType());
                if (task_elements.get(i).getViewType() == 0) break;
                //Log.d("TasksList","   Task List Unfold:  sub task description:"+ task_elements.get(i).getContent());
                task_elements.remove(i);
                selected_list.remove(i);
                //adapter.notifyItemRemoved(i);
                sub_tasks_count ++;
                i--;
                //!!--Verify if, in the adapter, remove one by one is better than remove a range
            }
            adapter.notifyItemRangeRemoved(position+1,position+1+sub_tasks_count);
            adapter.notifyItemChanged(position);
        }
        /// Unfold
        if(unfolded){
            //--Look for all the subs task that have for parent the present main task (bring a cursor), add them in the position

            try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task_id)) {
                if(cursor_Tasks_Sub.getCount()==0){
                    //Log.d("Read cursor_Tasks", "Cursor_Tasks : readcycleplanrecord: No Entry Exist");
                }else{
                    int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                    int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                    int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                    int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                    int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                    //Log.d("Read cursor_Task", "    TaskMain_id: " + task_elements.get(position).getContent());
                    while (cursor_Tasks_Sub.moveToNext()){
                        //!!---debe actualizarse
                        //Log.d("Read cursor_Task", "    Task_id: " + cursor_Tasks_Sub.getLong(id_indx_sub));
                        //Log.d("Read cursor_Task", "    Task_content: " + cursor_Tasks_Sub.getString(note_indx_sub));
                        Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                                cursor_Tasks_Sub.getLong(parent_indx_sub),
                                cursor_Tasks_Sub.getString(note_indx_sub),
                                cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                                cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                        task_elements.add(position+1+ cursor_Tasks_Sub.getPosition(),task_sub);
                        selected_list.add(position+1+ cursor_Tasks_Sub.getPosition(), Main_IsSelected ? true :false);///TERNARY Operator;
                    }
                    adapter.notifyItemRangeInserted(position+1,cursor_Tasks_Sub.getCount());
                    adapter.notifyItemChanged(position);
                    //Log.d("TasksSubList","   TaskSub size:  "+ task_elements.size());
                }
            }
        }
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
                    int sub_tasks_count = 0;
                    while(position+1 <= task_elements.size()-1){
                        if(task_elements.get(position+1).getViewType()==0) break;
                        task_elements.remove(position+1);
                        selected_list.remove(position+1);
                        sub_tasks_count ++;
                    }
                    //for(int i = position + 1; i <= task_elements.size() - 1 ;i++){
                    //    if(task_elements.get(i).getViewType()==0) break;
                    //    task_elements.remove(i);
                    //    selected_list.remove(i);
                    //    sub_tasks_count ++;
                    //    i--;
                    //}
                    adapter.notifyItemRangeRemoved(position + 1, sub_tasks_count);
                }
            }

            noteOriginal_list.remove(position);
            selected_list.remove(position);
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
    private void Delete_All_Tasks_From_DataBase() {
        DB_T.Delete_Hard_All_Tasks();
    }
    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in_from_trash,R.anim.return_activity_slide_right_out_from_trash);
    }
}
