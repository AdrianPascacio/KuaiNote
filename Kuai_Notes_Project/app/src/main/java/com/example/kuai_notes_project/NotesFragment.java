package com.example.kuai_notes_project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuai_notes_project.ruled_out_code.Date_of_Note_Item_View_DEPRECATED;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class NotesFragment extends Fragment implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener{

    long start_of_today = 0;
    RecyclerView recyclerView_Notes;
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

    public Adapter_Recycler_Memo_Board adapter_noteFragment;

    Button btn_config, btn_check_lists, btn_search, btn_generate_random_content, btn_generate_stable_content, btn_delete_all_notes_database;

    View main_NotesFragment;
    View layout_dim;
    Body_Note_Preview BNP;
    Date_of_Note_Item_View_DEPRECATED DoN_IV;
    Date_of_Note DoN;
    FloatingActionButton floatingActionButton;
    private Animation AnimationAddNoteButton;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel, Animation_FloatingButton_Appear, Animation_FloatingButton_Disappear;



    private LinearLayout floating_TrashCan_Access;
    private TextView tabArrow;
    private View menu_show_recycler_trashcan;
    private View btn_go_trash_can;
    private View menuTab;
    private View layout_main_note_fragment;

    private boolean isExpanded = false;
    private float initialX;
    //!!-- have to fix this:
    // private final float HIDDEN_OFFSET = dpToPx(170); // Ajusta según el XML
    private float HIDDEN_OFFSET = 40; // Ajusta según el XML
    private float HIDDEN_OFFSET_ARROW = 6; // Ajusta según el XML




    private static final String CHANNEL_ID = "My_App_Channel";

    //Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(getContext(),-1);
    Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp;
    Memo_Board_New_Aux memoBoardNewAux;

    public void iniciarFlujo(Note note){
        Log.d("NotesFragment", "iniciar flujo");
        //memoBoardNewAux = new Memo_Board_New_Aux();
        memoBoardNewAux.ejecutarPopUP((salida, position) -> {///  Implementacion de interfaz sin tener que  implementarla en la clase:

            selected_list.set(position,false);

            adapter_noteFragment.notifyItemChanged(position);
        },note);
    }
    private int selection_count = 0;
    private boolean pin_initial_state_MS= false;
    private boolean selection_mode = false;
    private boolean pin_multi_change = false;


    public void NoteFragment_Set_Selection_Status(int position, boolean status){
        selected_list.set(position,status);
    }
    public void NoteFragment_Update_Adapter_Position(int position){

        adapter_noteFragment.notifyItemChanged(position);
    }

    ///@Override
    ///public void onMemoBoardNewAux_OutReminder(int salida, int position) {

    ///}
    public interface Note_Fragment_Out_ReminderListener {//esto puede ir tambien en una clase separada
        void onNoteFragment_Out_Reminder_Open(int salida, int position); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private Note_Fragment_Out_ReminderListener notefragment_out_reminder_listener;

    public void setNoteFragment_Out_Reminder_Listener(Note_Fragment_Out_ReminderListener listener){
        this.notefragment_out_reminder_listener = listener;
    }


    public interface Note_Fragment_ReminderListener {//esto puede ir tambien en una clase separada
        void onNoteFragment_Reminder_Open(int salida, int position, Note note); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private Note_Fragment_ReminderListener notefragment_reminder_listener;

    public void setNoteFragment_Reminder_Listener(Note_Fragment_ReminderListener listener){
        this.notefragment_reminder_listener = listener;
    }

    //Memo_Board_New_Aux memoBoardNewAux = new Memo_Board_New_Aux();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Note_Fragment_ReminderListener) {
            notefragment_reminder_listener = (Note_Fragment_ReminderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar Note_Fragment_DismissListener");
        }
        if (context instanceof Note_Fragment_Out_ReminderListener) {
            notefragment_out_reminder_listener = (Note_Fragment_Out_ReminderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar Note_Fragment_DismissListener 2222");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notefragment_reminder_listener = null; // Evita fugas de memoria
        notefragment_out_reminder_listener = null; // Evita fugas de memoria
    }
    @Override
    public void onResume(){
        super.onResume();
        getStartOfToday();

        //recyclerView = findViewById(R.id.Recycler_MemoBoard);

        //et_searched_Text.setText("");
        item_count = 0;
        if(adapter_noteFragment.Get_Searching_Mode_Status()){
            adapter_noteFragment.Change_Searching_Mode_Status(false);
            Clear_Lists();
            Update_Recycler_View();
        }


        //fa_btn.setVisibility(View.VISIBLE);
        //fa_btn.setFocusable(true);
        //fa_btn.setClickable(true);
        //fa_btn.animate().alpha(1f).scaleY(1f).scaleX(1f).setDuration(700);

        if(isExpanded){
            floating_TrashCan_Access.setTranslationX(HIDDEN_OFFSET);
            tabArrow.setTranslationX(0);
            tabArrow.setText("<");
            isExpanded = false;
        }

    }
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(view.getContext(),-1);
        memoBoardNewAux = new Memo_Board_New_Aux();
        //memoBoardNewAux.setMemoBoardNewAuxOutReminderListener(this);


        recyclerView_Notes = view.findViewById(R.id.rvNotes);
        DB_N = new DB_Notes(getContext());
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

        et_searched_Text = view.findViewById(R.id.Searched_Text);
        main_NotesFragment = view.findViewById(R.id.Layout_Main_Note_Fragment);

        BNP = new Body_Note_Preview();
        DoN_IV = new Date_of_Note_Item_View_DEPRECATED();
        DoN = new Date_of_Note();
        //fa_btn = findViewById(R.id.floatingActionButton);
        //main = findViewById(R.id.main);
        //layout_dim = findViewById(R.id.layout_dim_itemVisualizer);
        recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_noteFragment = new Adapter_Recycler_Memo_Board(getContext(), dateEdited_list,selected_list,noteList,this);
        recyclerView_Notes.setAdapter(adapter_noteFragment);

        Update_Recycler_View();

        AnimationAddNoteButton = AnimationUtils.loadAnimation(getContext(),R.anim.add_note_button_zoom);
        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(getContext(), R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(getContext(), R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(getContext(), R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(getContext(), R.anim.layout_dim_disappear_setter);
        Animation_FloatingButton_Appear = AnimationUtils.loadAnimation(getContext(), R.anim.floating_button_appear);
        Animation_FloatingButton_Disappear = AnimationUtils.loadAnimation(getContext(), R.anim.floating_buttton_disappear);
        et_searched_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String searched_Text = et_searched_Text.getText().toString();

                Update_Recycler_View_ftsValues_Snipped4(searched_Text);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        return view;
    }

    /// TrashCan_Access:

    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
    }

    private void Update_Recycler_View_ftsValues_Snipped4(String searched_Text) {
        //-------Intenta utilizar el diffutil eliminar coincidencias entre menos existan,
        //---agregar coincidencias progresivamente al eliminar algunas letras
        //---Limpiar las listas al no tener ninguna coincidencia
        //!!--Optimizar

        ////if(!adapter.Get_Searching_Mode_Status()) item_count = 0;
        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
                adapter_noteFragment.Change_Searching_Mode_Status(false);
                Log.d("2Search", "Zero : adapter itemcount:" + adapter_noteFragment.getItemCount());
                ///if(id_List.size() > 0){
                ///    //Clear_Lists();
                ///    Clear_Searched_Lists();

                ///    recyclerView.setAdapter(adapter);
                ///    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                ///    item_count = 0;
                ///}


                Clear_Lists();
                //Clear_Searched_Lists();

                recyclerView_Notes.setAdapter(adapter_noteFragment);
                recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));




                Update_Recycler_View();
                //adapter.Change_Searching_Mode_Status(false);
            }else{
                adapter_noteFragment.Change_Searching_Mode_Status(true);

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
                            adapter_noteFragment.notifyItemRemoved(i);
                        }else{
                            noteList.get(i).setTitle(cursor_Notes.getString(4));
                            noteList.get(i).setNote(cursor_Notes.getString(3));
                            adapter_noteFragment.notifyItemChanged(i);
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
                            adapter_noteFragment.notifyItemRemoved(j);
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
                            adapter_noteFragment.notifyItemInserted(i);
                        }else{
                            noteList.get(i).setTitle(cursor_Notes.getString(4));
                            noteList.get(i).setNote(cursor_Notes.getString(3));
                            adapter_noteFragment.notifyItemChanged(i);
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
                            adapter_noteFragment.notifyItemInserted(j);
                        }
                    }
                }else{
                    while(cursor_Notes.moveToNext()){
                        int i = cursor_Notes.getPosition();
                        Note _note = noteList.get(i);
                        Log.d("2Search", "            Just updating: Title: "+_note.title+ "    i: "+ i);
                        noteList.get(i).setTitle(cursor_Notes.getString(4));
                        noteList.get(i).setNote(cursor_Notes.getString(3));
                        adapter_noteFragment.notifyItemChanged(i);
                        //i++;
                    }

                }
            }
        }
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
        recyclerView_Notes.setAdapter(adapter_noteFragment);
        recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));
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


    @Override
    public void onItemClick(int position, View v) {
        if(!adapter_noteFragment.Get_Searching_Mode_Status()){
            if(selection_mode) {
                Select_Item(position, v);
                return;
            }

            Note _note = noteList.get(position);
            Intent goTo = new Intent(getContext(), MainActivity.class);
            goTo.putExtra("send_date_of_note",_note.date);
            goTo.putExtra("send_note_id",_note.note_id);
            startActivity(goTo);
            ///overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);

        }else{
            if(selection_mode) {
                //!! Must correct this section
                Select_Item(position, v);
                return;
            }

            Intent goTo = new Intent(getContext(), MainActivity.class);
            goTo.putExtra("send_note_id",noteList.get(position).getNote_id());
            startActivity(goTo);
            ///overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);

        }
    }

    @Override
    public void onItemHold(int position,View v) {
        Select_Item(position, v);
    }
    private void Select_Item(int position, View v) {
        selected_list.set(position,!selected_list.get(position));// invert value

        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        //if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Disappear);

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

            adapter_noteFragment.Change_multi_selection_state(selection_mode);
            adapter_noteFragment.notifyItemChanged(position,this);
            adapter_noteFragment.notifyItemChanged(selected_positions_list.get(1),this);//!!se estan desvaneciendo sin las animaciones

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
        adapter_noteFragment.notifyItemChanged(position);//!! se esta duplicando con la instruccion de arriba

        //---Set unselecting_view to repeated unselect
        if(selected_positions_list.size()==2) {
            if(Objects.equals(position, selected_positions_list.get(1))){
                adapter_noteFragment.Change_is_repeated_value(true);
                selected_positions_list.clear();
            }
        }

        if(selected_positions_list.size()==3) selected_positions_list.remove(2);
        //if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
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
            adapter_noteFragment.notifyItemChanged(position);
            return;
        }

        Toast.makeText(getContext(), "Repeated pinned", Toast.LENGTH_SHORT).show();
        adapter_noteFragment.Change_is_repeated_value(true);

        boolean _pin = pin_multi_change ? !pin_initial_state_MS : !_note.getPin();///Ternary Operator


        if(DB_N.Modify_Pin_Status(_note.note_id,_pin)){
            RecyclerView_Pin_Update(position);
        }else{
            Toast.makeText(getContext(), "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }
    }

    public void RecyclerView_Pin_Update(int position){

        Note _note = noteList.get(position);
        String _date= dateEdited_list.get(position);
        String _noteOriginal= noteOriginal_list.get(position);
        boolean _selected=false;
        selected_list.set(position,false);
        adapter_noteFragment.notifyItemChanged(position);

        dateEdited_list.remove(position);
        noteOriginal_list.remove(position);
        selected_list.remove(position);

        noteList.remove(position);


        int current_pinned_notes = 0;
        if(adapter_noteFragment.Get_Searching_Mode_Status() == true){
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
        adapter_noteFragment.notifyItemMoved(position,current_pinned_notes);
        adapter_noteFragment.notifyItemChanged(current_pinned_notes);

        Restart_Selection();
    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
        //NotesFragment.this.notefragment_listener_dismiss.onNoteFragmentClosed(1,position);

        adapter_noteFragment.Change_is_repeated_value(true);
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(getContext(), position);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        Note _note = noteList.get(position);
        notefragment_reminder_listener.onNoteFragment_Reminder_Open(1,position, _note);
        //notefragment_reminder_listener.onNoteFragment_Reminder_Open(1,position, _note);
        ///notefragment_reminder_listener.onNoteFragment_Reminder_Open((1,position,_note) -> {
        ///    selected_list.set(position,false);

        ///    adapter_noteFragment.notifyItemChanged(position);

        ///});
        //iniciarFlujo(_note);
        reminder_PopUp.show(main_NotesFragment, _note);

        //memoBoardNewAux.ejecutarPopUP((salida, position) -> {

        //    selected_list.set(position,false);

        //    adapter_noteFragment.notifyItemChanged(position);
        //},note);
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
        adapter_noteFragment.notifyItemChanged(position);
    }
    @Override
    public void onPopupClosed(int salida, int position) {
        //layout_dim.setVisibility(View.VISIBLE);

        Restart_Selection();
        notefragment_out_reminder_listener.onNoteFragment_Out_Reminder_Open(salida,position);
        if(salida == 1){//setter
            ///layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            //layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);

            Toast.makeText(getContext(), "reminder"+" setter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(salida == 2){//cancel
            ///layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
            //layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            Toast.makeText(getContext(), "reminder"+" cancel", Toast.LENGTH_SHORT).show();

            return;
        }
        selected_list.set(position,false);

        adapter_noteFragment.notifyItemChanged(position);
        //layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);

        ///!!-- duplicated
        //Restart_Selection();

        Toast.makeText(getContext(), "reminder"+" normal", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main_NotesFragment,_note.note_id,0, _note.reminder);

        if(DB_N.Send_Note_To_Trash(_note.note_id,_note.date,_note.title,noteOriginal_list.get(position),_note.pin,20)){
            //----Remove Note from Recycler View
            dateEdited_list.remove(position);
            noteOriginal_list.remove(position);
            noteList.remove(position);
            selected_list.remove(position);
            adapter_noteFragment.notifyItemRemoved(position);

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
        //if(!selection_mode) fa_btn.startAnimation(Animation_FloatingButton_Appear);
        adapter_noteFragment.Change_multi_selection_state(false);
    }

    @Override
    public void onMemoBoardSelection_PopupClosed(int option) {
        if(option == 1){
            Toast.makeText(getContext(), "pin", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getContext(), "reminder", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
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
