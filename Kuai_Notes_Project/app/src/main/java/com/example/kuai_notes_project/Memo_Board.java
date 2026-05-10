package com.example.kuai_notes_project;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

///324 V3, 305 V4, 358 V6, 306 V7, 450 V7.2, 570 v9.0B
public class Memo_Board extends AppCompatActivity implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener {
    RecyclerView recyclerView;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;
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
        setContentView(R.layout.activity_memo_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_navigation_bar));

        DB_N = new DB_Notes(this);
        Random_G = new Random_Content_Generator_For_Test();
        Stable_G = new Stable_Content_Generator_For_Test();

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

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


    private void Delete_All_Notes_From_DataBase() {
        DB_N.Delete_Hard_All_Notes();
    }

    private void Generate_Random_Content_For_Test() {
        Random_G.Random_Note_Generator(this,40);
    }
    private void Generate_Stable_Content_For_Test() {
        Stable_G.Stable_Note_Generator(this,40,0,100);
    }
    private void Generate_Random_Content_For_Test_Old() {
        //Toast.makeText(this, "Random Content Generator", Toast.LENGTH_SHORT).show();

        //Create Randome Note (Title, note, pin, reminder, reminder_type, reminder_interval)

        String seed_text = "Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro caparazón y, al alzar la cabeza, vio su vientre convexo y oscuro, surcado por curvadas callosidades, sobre el que casi no se aguantaba la colcha, que estaba a punto de escurrirse hasta el suelo. Numerosas patas, penosamente delgadas en comparación con el grosor normal de sus piernas, se agitaban sin concierto. —¿Qué me ha ocurrido? No estaba soñando. Su habitación, una habitación normal, aunque muy pequeña, tenía el aspecto habitual. Sobre la mesa había desparramado un muestrario de paños —Samsa era viajante de comercio—, y de la pared colgaba una estampa recientemente recortada de una revista ilustrada y puesta en un marco dorado. La estampa mostraba a una mujer tocada con un gorro de pieles, envuelta en una estola también de pieles, y que, muy erguida, esgrimía un amplio manguito, asimismo de piel, que ocultaba todo su antebrazo. Gregorio miró hacia la ventana; estaba nublado, y sobre el cinc del alféizar repiqueteaban las gotas de lluvia, lo que le hizo sentir una gran melancolía. «Bueno —pensó—; ¿y si siguiese durmiendo un rato y me olvidase de todas estas locuras?» Pero no era posible, pues Gregorio tenía la costumbre de dormir sobre el lado derecho, y su actual estado no le permitía adoptar tal postura. Por más que se esforzara volvía a quedar de espaldas. Intentó en vano esta operación numerosas veces; cerró los ojos para no tener que ver aquella confusa agitación de patas, que no cesó hasta que notó en el costado un dolor leve y punzante, un dolor jamás sentido hasta entonces. —¡Qué cansada es la profesión que he elegido! —se dijo—. Siempre de viaje. Las preocupaciones son mucho mayores cuando se trabaja fuera, por no hablar de las molestias propias de los viajes: estar pendiente de los enlaces de los trenes; la comida mala, irregular; relaciones que cambian constantemente, que nunca llegan a ser verdaderamente cordiales, y en las que no tienen cabida los sentimientos. ¡Al diablo con todo! Sintió en el vientre una ligera picazón. Lentamente, se estiró sobre la espalda en dirección a la cabecera de la cama, para poder alzar mejor la cabeza. Vio que el sitio que le picaba estaba cubierto de extraños puntitos blancos. Intentó rascarse con una pata; pero tuvo que retirarla inmediatamente, pues el roce le producía escalofríos. —Estoy atontado de tanto madrugar —se dijo—. No duermo lo suficiente. Hay viajantes que viven mucho mejor. Cuando a media mañana regreso a la fonda para anotar los pedidos, me los encuentro desayunando cómodamente sentados. Si yo, con el jefe que tengo, hiciese lo mismo, me despedirían en el acto. Lo cual, probablemente sería lo mejor que me podría pasar. Si no fuese por mis padres, ya hace tiempo que me hubiese marchado. Hubiera ido a ver el director y le habría dicho todo lo que pienso. Se caería de la mesa, ésa sobre la que se sienta para, desde aquella altura, hablar a los empleados, que, como es sordo, han de acercársele mucho. Pero todavía no he perdido la esperanza. En cuanto haya reunido la cantidad necesaria para pagarle la deuda de mis padres —unos cinco o seis años todavía—, me va a oír. Bueno; pero, por ahora, lo que tengo que hacer es levantarme, que el tren sale a las cinco. Eran más de las seis y media, y las manecillas seguían avanzando tranquilamente. En realidad, ya eran casi las siete menos cuarto. ¿Es que no había sonado el despertador? Desde la cama se veía que estaba puesto a las cuatro; por tanto, tenía que haber sonado. Pero ¿era posible seguir durmiendo a pesar de aquel sonido que hacía estremecer hasta los muebles? Su sueño no había sido tranquilo. Pero, por eso mismo, debía de haber dormido al final más profundamente. ¿Qué podía hacer ahora? El tren siguiente salía a las siete; para cogerlo tendría que darse muchísima prisa. El muestrario no estaba aún empaquetado, y él mismo no se sentía nada dispuesto. Además, aunque alcanzase el tren, no evitaría reprimenda del amo, pues el mozo del almacén, que había acudido al tren a las cinco,";
        for(int i = 20 ; i>=0; i--){
            long _current_time = System.currentTimeMillis();
            //int random_number = (int) (_current_time & 1023);   /// bitwise & long & 1023 (binary = 1111111111(1 diez veces)) → para tomar los numeros menores de 1023
            int random_number = (int) (_current_time & 4095);   /// bitwise & long & 4095 (binary = 111111111111 (1 doce veces)) → para tomar los numeros menores de 4095
            //int random_title = 0;
            int end_of_title = random_number & 15;  ///Bitwise & → int & 15 (binary = 1111) → para tomar los numeros menores de 15;
            int random_start_of_note  = (random_number >> 1) ;
            int random_end_of_note = random_start_of_note & 31;
            int random_pin = random_end_of_note & 1;

            Log.d("Random", "Random end of note: " + random_end_of_note + "    Random pin: " + random_pin);

            //String _title = et_Title.getText().toString();
            String _title = seed_text.substring(random_number,random_number + end_of_title);
            String _note = seed_text.substring(random_start_of_note,random_end_of_note + random_start_of_note);

            long save_Success;

            save_Success = DB_N.Insert_Note_L(_current_time, _title, _note, random_pin == 1, 0L, 0, 0);
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

    public void Go_To_Add_New_Note(View view){
        if(!selection_mode) {
            Intent goTo = new Intent(this, MainActivity.class);
            startActivity(goTo);
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
    }

    @Override
    public void onItemClick(int position, View v) {
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

        int current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(_note.note_id);
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