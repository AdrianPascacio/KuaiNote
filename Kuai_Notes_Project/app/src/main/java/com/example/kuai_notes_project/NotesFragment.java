package com.example.kuai_notes_project;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

///777 13jul2026
public class NotesFragment extends Fragment implements Recycler_Memo_Board_Interface, Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener, FragmentRefreshable, MainActivity.NoteVisualizer_Modification_in_Notes{

    private ActivityResultLauncher<Intent> lanzadorActivityC;
    long start_of_today = 0;
    RecyclerView recyclerView_Notes;
    ArrayList<String> dateEdited_list;
    ArrayList<String> noteOriginal_list;
    ArrayList<Boolean> selected_list;
    ArrayList<Note> noteList;

    EditText et_searched_Text;

    ArrayList<Integer> selected_positions_list;

    DB_Notes DB_N;
    public Adapter_Recycler_Memo_Board adapter_noteFragment;

    FrameLayout fl_search;

    View main_NotesFragment;
    Date_of_Note DoN;

    Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp;
    Memo_Board_New_Aux memoBoardNewAux;
    MainActivity mainActivity;
    public void iniciarFlujo(){
        memoBoardNewAux.ejecutarPopUP((salida, position) -> {///  Implementacion de interfaz sin tener que  implementarla en la clase:

            Log.d("implementacion", "implementancion");
            memoBoardNewAux.btn_search.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafa99")));
            Toast.makeText(memoBoardNewAux, "Ejecutado desde Note"+salida + position, Toast.LENGTH_SHORT).show();

        });
    }
    private int selection_count = 0;
    private boolean pin_initial_state_MS= false;
    private boolean selection_mode = false;
    private boolean pin_multi_change = false;

    public void onMemoBoardNewAux_OutReminder(int salida, int position) {
        adapter_noteFragment.Change_Searching_Mode_Status(false);
        Log.d("2Search", "Zero : adapter itemcount:" + adapter_noteFragment.getItemCount());

        Clear_Lists();

        recyclerView_Notes.setAdapter(adapter_noteFragment);
        recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));

        note_fragment_adding_option_available.onNoteFragment_Adding_Option_Available(true);
        Update_Recycler_View();

    }

    @Override
    public void onFragmentSelected() {
        if(et_searched_Text.getVisibility() == VISIBLE ){/// If is in Searching mode → Restart
            adapter_noteFragment.Change_Searching_Mode_Status(false);
            Log.d("2Search", "Zero : adapter itemcount:" + adapter_noteFragment.getItemCount());

            Clear_Lists();

            recyclerView_Notes.setAdapter(adapter_noteFragment);
            recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));

            note_fragment_adding_option_available.onNoteFragment_Adding_Option_Available(true);
            Update_Recycler_View();

            //!!---Faltan las anicamiones
            fl_search.setVisibility(VISIBLE);
            et_searched_Text.setText("");
            et_searched_Text.setVisibility(View.GONE);
        }
    }
    @Override
    public void onFragmentNewElement(int modification_in_notes, long element_id) {
        New_Note_To_The_Journal(element_id);
    }
    @Override
    public void onFragmentElementModification(int modification_in_element, long element_id) {
        Modification_In_Journal_Note(element_id);
    }

    @Override
    public void onFragmentElementElimination(int modification_in_element, long element_id) {

        for(int i = 0; i <= noteList.size()-1; i++){
            if(element_id == noteList.get(i).note_id){

                Remove_Element_In_Every_List(i);
                adapter_noteFragment.notifyItemChanged(i);
                break;
            }
        }

    }

    private void New_Note_To_The_Journal(long note_id) {
        Log.d("NoteFragment", "     New Note to the Journal: Journal Insertion");
        Note _note = DB_N.getASpecificNote(note_id);
        int current_position = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(note_id);
        noteList.add(current_position,_note);
        selected_list.add(current_position,false);
        dateEdited_list.add(current_position,DoN.Set_Date_of_Note_Item_View(_note.date,start_of_today));
        noteOriginal_list.add(current_position,_note.note);

        adapter_noteFragment.notifyItemInserted(current_position);

    }
    private void Modification_In_Journal_Note(long element_id) {
        Update_On_Journal_Notes(0,element_id);
    }

    @Override
    public void onNoteVisualizer_Modification_in_Notes(boolean modification_in_notes, long note_id) {
    }

    private void Update_On_Journal_Notes(int modification_in_notes, long note_id){
        /// Actualmente se debe buscar en la lista el Id para luego actualizarlo
        /// En caso de no existir entonces se debe introducir a la lista.
        /// !!---Tal vez, si se puede especificar desde antes si es una modificacion o es una nota nueva se pueda optimizar y no tenga que buscarse si existe en la lista actual de la journal list

        Log.d("NoteFragment", "Journal Modification Update");
        Note _note = DB_N.getASpecificNote(note_id);
        for(int i = 0 ; i <= noteList.size()-1 ; i ++){
            if(note_id == noteList.get(i).getNote_id()){
                noteList.set(i,_note);
                adapter_noteFragment.notifyItemChanged(i);
                return;
            }
        }

        Log.d("NoteFragment", "Journal Insertion");
        int current_position = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(note_id);

        noteList.add(current_position,_note);
        selected_list.add(current_position,false);
        dateEdited_list.add(current_position,DoN.Set_Date_of_Note_Item_View(_note.date,start_of_today));
        noteOriginal_list.add(current_position,_note.note);

        adapter_noteFragment.notifyItemInserted(current_position);
    }





    public interface Note_Fragment_Adding_Option_Available {//esto puede ir tambien en una clase separada
        void onNoteFragment_Adding_Option_Available(boolean adding_option_available); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private Note_Fragment_Adding_Option_Available note_fragment_adding_option_available;
    public void setNoteFragment_Adding_Option_Available(Note_Fragment_Adding_Option_Available listener){
        this.note_fragment_adding_option_available = listener;
    }

    public interface Note_Fragment_Out_ReminderListener {
        void onNoteFragment_Out_Reminder_Open(int salida); /// 0 No Changes, 1 Reminder Set, 2 Canceled
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
        if (context instanceof Note_Fragment_Adding_Option_Available) {
            note_fragment_adding_option_available = (Note_Fragment_Adding_Option_Available) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar Note_Fragment_DismissListener 3333");
        }
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;

            mainActivity.setNoteVisualizer_Modification_in_Notes(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notefragment_reminder_listener = null; //  Para evitar fugas de memoria
        notefragment_out_reminder_listener = null; //  Para evitar fugas de memoria
        note_fragment_adding_option_available = null; //  Para evitar fugas de memoria

    }
    @Override
    public void onResume(){
        super.onResume();
        getStartOfToday();
        //mainActivity.setNoteVisualizer_Modification_in_Notes(this);

        if(adapter_noteFragment.Get_Searching_Mode_Status()){
            adapter_noteFragment.Change_Searching_Mode_Status(false);
            Clear_Lists();
            Update_Recycler_View();
        }
    }
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(view.getContext(),-1);
        memoBoardNewAux = new Memo_Board_New_Aux();
        //memoBoardNewAux.setMemoBoardNewAuxOutReminderListener(getContext());

        recyclerView_Notes = view.findViewById(R.id.rvNotes);
        DB_N = new DB_Notes(getContext());

        dateEdited_list = new ArrayList<>();
        noteOriginal_list = new ArrayList<>();
        selected_list = new ArrayList<>();
        noteList = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        fl_search = view.findViewById(R.id.button_Search);

        et_searched_Text = view.findViewById(R.id.Searched_Text);
        main_NotesFragment = view.findViewById(R.id.Layout_Main_Note_Fragment);

        DoN = new Date_of_Note();
        recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_noteFragment = new Adapter_Recycler_Memo_Board(getContext(), dateEdited_list,selected_list,noteList,this);
        recyclerView_Notes.setAdapter(adapter_noteFragment);

        //mainActivity.setNoteVisualizer_Modification_in_Notes(this);
        Update_Recycler_View();

        lanzadorActivityC = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("NotesFragment" , "Result OK: " + MainActivity.RESULT_OK );
                    Log.d("NotesFragment" , "result.getData() != null : " + (result.getData() != null) );

                    if (result.getResultCode() == MainActivity.RESULT_OK && result.getData() != null) {
                        // ¡Aquí recibimos los datos de regreso seguros!
                        Intent data = result.getData();
                        int modificacion = data.getIntExtra("extra_modificacion", 0);
                        long id = data.getLongExtra("extra_id", -1 != -1 ? data.getLongExtra("extra_id", -1) : -1);

                        // Aquí ejecutas tu lógica para actualizar el RecyclerView
                        Log.d("NotesFragment" , "Just Before Update Journal Notes:");
                        Update_On_Journal_Notes(modificacion, id);
                    }
                }
        );

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
        fl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fl_search.setVisibility(View.GONE);
                et_searched_Text.setVisibility(VISIBLE);
            }
        });
        return view;
    }

    private void  getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        start_of_today = today.getTimeInMillis();
    }

    private void Update_Recycler_View_ftsValues_Snipped4(String searched_Text) {
        //!!--Optimizar

        try (Cursor cursor_Notes = DB_N.get_All_Notes_fts_2(searched_Text)) {
            if(cursor_Notes.getCount()==0 || et_searched_Text.getTextSize()==0){
                adapter_noteFragment.Change_Searching_Mode_Status(false);
                Log.d("2Search", "Zero : adapter itemcount:" + adapter_noteFragment.getItemCount());

                Clear_Lists();

                recyclerView_Notes.setAdapter(adapter_noteFragment);
                recyclerView_Notes.setLayoutManager(new LinearLayoutManager(getContext()));

                note_fragment_adding_option_available.onNoteFragment_Adding_Option_Available(true);
                Update_Recycler_View();
            }else{
                iniciarFlujo();

                note_fragment_adding_option_available.onNoteFragment_Adding_Option_Available(false);
                adapter_noteFragment.Change_Searching_Mode_Status(true);

                if(cursor_Notes.getCount() < noteList.size()){
                    int i = 0;
                    while(i <= cursor_Notes.getCount() -1 && i <= noteList.size() -1){
                        Note _note = noteList.get(i);
                        cursor_Notes.moveToPosition(i);
                        if(_note.note_id != cursor_Notes.getLong(0)){
                            Log.d("2Search", "            Removing: Title: "+_note.title+ "    i: "+ i);
                            Remove_Element_In_Every_List(i);
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
                            Remove_Element_In_Every_List(j);
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
                    Note note = new Note(cursor_Notes.getLong(id_indx),
                            cursor_Notes.getLong(date_indx),
                            cursor_Notes.getString(title_indx),
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
        if(noteOriginal_list.isEmpty()&&selected_list.isEmpty())    return;
        dateEdited_list.clear();
        noteOriginal_list.clear();
        selected_list.clear();
        noteList.clear();
    }

    @Override
    public void onItemClick(int position, View v) {
        if(selection_mode) {
            Select_Item(position, v);
            return;
        }

        if (getActivity() instanceof Memo_Board_New_Aux) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("send_note_id",noteList.get(position).getNote_id());
            // Le pedimos al launcher de la MainActivity que la ejecute
            ((Memo_Board_New_Aux) getActivity()).getLanzadorActivityC().launch(intent);
        }


        //Intent goTo = new Intent(getContext(), MainActivity.class);
        //goTo.putExtra("send_note_id",noteList.get(position).getNote_id());
        //lanzadorActivityC.launch(goTo);
        ////startActivity(goTo);
    }

    @Override
    public void onItemHold(int position,View v) {
        Select_Item(position, v);
    }
    private void Select_Item(int position, View v) {
        selected_list.set(position,!selected_list.get(position));// invert value
        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!
        selection_mode = selection_count > 0;
        selected_positions_list.add(0,position);

        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            //--Buscar estado del pin de las dos primeras notas seleccionadas:
            Note _note = noteList.get(selected_positions_list.get(0));
            Note _note2 = noteList.get(selected_positions_list.get(1));

            pin_initial_state_MS = _note.getPin() & _note2.getPin() || _note2.getPin(); /// AND Operator !!--Verificar si la opcion de elegir lo primero que escoja el usuario es lo mejor

            selection_item_menu_PopUp.setListener_dismiss(this);
            selection_item_menu_PopUp.show(v, pin_initial_state_MS);

            adapter_noteFragment.Change_multi_selection_state(selection_mode);
            adapter_noteFragment.notifyItemChanged(position,this);
            adapter_noteFragment.notifyItemChanged(selected_positions_list.get(1),this);//!!se estan desvaneciendo sin las animaciones

            note_fragment_adding_option_available.onNoteFragment_Adding_Option_Available(false);
        }
        if(selection_item_menu_PopUp.popupWindow != null && !selection_mode){
            Restart_Selection();
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
    }
    @Override
    public void onMemoBoardSelection_PopupClosed(int option) {
        if(option == 1){/// Option Pin

            pin_multi_change = true;

            if(pin_initial_state_MS){
                for(int i = selected_list.size()-1; i >= 0; i--) {
                    if (selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }else{
                for(int i = 0;i <= selected_list.size()-1; i++) {
                    if (selected_list.get(i)) {
                        PinItem(i);
                    }
                }
            }

            pin_multi_change = false;
            Restart_Selection();
            return;
        }

        if(option == 2){/// Option Reminder
        }
        if(option == 3){/// Option Delete
            for(int i = selected_list.size() -1 ;i >= selected_list.size() -1; i--){
                if(selected_list.get(i))    RemoveItem(i);
            }
            selected_positions_list.clear();
        }
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {
        Note _note = noteList.get(position);

        //--Cambiar estado de Pin solo cuando aplica:
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
        }
    }

    public void RecyclerView_Pin_Update(int position){

        Note _note = noteList.get(position);
        String _date= dateEdited_list.get(position);
        String _noteOriginal= noteOriginal_list.get(position);
        selected_list.set(position,false);
        adapter_noteFragment.notifyItemChanged(position);

        Remove_Element_In_Every_List(position);

        int current_pinned_notes = 0;
        if(adapter_noteFragment.Get_Searching_Mode_Status() == true){
            int i = 0 ;
            while (i <= noteList.size()-1) {
                if(noteList.get(i).pin != _note.pin){/// Pin
                    while( i <= noteList.size()-1 && _note.date < noteList.get(i).date ){/// Date
                        i++;
                        Log.d("Update Pin Recycler", "        While (date<) adding:  i=" + i);

                    }
                    current_pinned_notes = i;
                    Log.d("Update Pin Recycler", "        Current=" + i);
                    break;
                }
                i++;
                Log.d("Update Pin Recycler", "        while (pin==) adding  i=" + i);
            }
        }else{
            current_pinned_notes = DB_N.get_Specific_Note_Sorted_by_Pin_and_Date(_note.note_id);
        }

        dateEdited_list.add(current_pinned_notes,_date);
        noteOriginal_list.add(current_pinned_notes,_noteOriginal);
        //--cambio de estado con referencia al anterior de (0 a 1)
        //_note.setPin(_note.getPin() ^ 1);       //XOR Operator
        _note.setPin(!_note.getPin());
        noteList.add(current_pinned_notes,_note);
        selected_list.add(current_pinned_notes,false);
        adapter_noteFragment.notifyItemMoved(position,current_pinned_notes);
        adapter_noteFragment.notifyItemChanged(current_pinned_notes);

        Restart_Selection();
    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
        adapter_noteFragment.Change_is_repeated_value(true);
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(getContext(), position);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        Note _note = noteList.get(position);
        notefragment_reminder_listener.onNoteFragment_Reminder_Open(1,position, _note);
        reminder_PopUp.show(main_NotesFragment, _note);
    }
    @Override
    public void onPopupClosed(int salida, int position) {
        Restart_Selection();
        notefragment_out_reminder_listener.onNoteFragment_Out_Reminder_Open(salida);

        selected_list.set(position,false);

        adapter_noteFragment.notifyItemChanged(position);
        Toast.makeText(getContext(), "reminder"+" from fragment: normal", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void OnValueSelected(int position, long alarm_time, int reminder_type, int reminder_interval) {
        Note _note = noteList.get(position);
        selected_list.set(position,false);

        _note.setReminder(alarm_time);
        _note.setReminder_type(reminder_type);
        _note.setReminder_interval(reminder_interval);
        noteList.remove(position);
        noteList.add(position,_note);
        adapter_noteFragment.notifyItemChanged(position);
    }
    @Override
    public void RemoveItem(int position) {
        Note _note = noteList.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main_NotesFragment,_note.note_id,0, _note.reminder);

        if(DB_N.Send_Note_To_Trash(_note.note_id,_note.date,_note.title,noteOriginal_list.get(position),_note.pin,20)){
            Remove_Element_In_Every_List(position);
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

        note_fragment_adding_option_available.onNoteFragment_Adding_Option_Available(true);
        adapter_noteFragment.Change_multi_selection_state(false);
    }
    private void Remove_Element_In_Every_List(int position){
        noteList.remove(position);
        noteOriginal_list.remove(position);
        dateEdited_list.remove(position);
        selected_list.remove(position);
    }
}