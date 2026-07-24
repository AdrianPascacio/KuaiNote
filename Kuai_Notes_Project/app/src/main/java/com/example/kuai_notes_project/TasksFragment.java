package com.example.kuai_notes_project;

import static android.view.View.VISIBLE;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
//1076 v9.OB 05jul2026, 861 v9.OA 16jul2026
public class TasksFragment extends Fragment implements Recycler_Tasks_List_Interface, Recycler_Tasks_Sub_List_Interface, Selection_Item_Menu_MemoBoard_PopUpWindow.SM_PopupDismissListener, FragmentRefreshable {
    @Override
    public void onFragmentSelected() {
        if(et_searched_Text_Task.getVisibility() == VISIBLE){/// If is in Searching mode → Restart
            adapter_taskFragment.Change_Searching_Mode_Status(false);

            Clear_Lists();

            recyclerView_Tasks.setAdapter(adapter_taskFragment);
            recyclerView_Tasks.setLayoutManager(new LinearLayoutManager(getContext()));

            Update_Recycler_View();
            task_fragment_adding_option_available.onTaskFragment_Adding_Option_Available(true);

            fl_search.setVisibility(VISIBLE);
            et_searched_Text_Task.setText("");
            et_searched_Text_Task.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFragmentNewElement(int modification_in_notes, long element_id) {
        Log.d("TaskFragment", "     New Task to the Journal: Journal Insertion");
        Task_Main task_main = DB_T.getASpecificTask(element_id);
        int current_position = DB_T.get_Specific_Task_Sorted_by_Pin_and_Date(element_id);
        Add_Element_In_Every_List(current_position,task_main,false);

        adapter_taskFragment.notifyItemInserted(current_position);
    }

    @Override
    public void onFragmentElementModification(int modification_in_element, long element_id) {
        Modification_In_Journal_Task(element_id);
    }

    @Override
    public void onFragmentElementElimination(int modification_in_element, long element_id) {
        for(int i = 0; i <= task_elements.size()-1; i++){
            if( task_elements.get(i).getViewType() == 0 && element_id == task_elements.get(i).getId()){
                RemoveItem(i);
                break;
            }
        }
    }

    private void Modification_In_Journal_Task(long element_id) {
        Log.d("TaskFragment", "Journal Modification Update");
        Task_Main _task_main_2 = DB_T.getASpecificTask(element_id);
        for(int i = 0 ; i <= task_elements.size()-1 ; i ++){
            if(element_id == task_elements.get(i).getId() && task_elements.get(i).getViewType()== 0){
                Task_Main _task_main = (Task_Main) task_elements.get(i);

                task_elements.set(i,_task_main_2);
                if(_task_main.unfolded){
                    RecyclerView_Unfold_Update(i, false, _task_main.task_id);
                    RecyclerView_Unfold_Update(i, true, _task_main.task_id);
                }
                adapter_taskFragment.notifyItemChanged(i);
                return;
            }
        }
    }

    public interface Task_Fragment_Adding_Option_Available {//esto puede ir tambien en una clase separada
        void onTaskFragment_Adding_Option_Available(boolean adding_option_available); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private Task_Fragment_Adding_Option_Available task_fragment_adding_option_available;
    public void setTaskFragment_Adding_Option_Available(Task_Fragment_Adding_Option_Available listener){
        this.task_fragment_adding_option_available = listener;
    }

    RecyclerView recyclerView_Tasks;
    Adapter_Recycler_Tasks_List adapter_taskFragment;
    ArrayList<Boolean> selected_list;
    ArrayList<Task_Element> task_elements, task_elements_aux;
    ArrayList<Integer> selected_positions_list;

    DB_Tasks DB_T;

    EditText et_searched_Text_Task;

    FrameLayout fl_search;

    long start_of_today = 0;
    View main_TasksFragment;
    Selection_Item_Menu_MemoBoard_PopUpWindow selection_item_menu_PopUp;

    private int order_type = 0;
    private int selection_count = 0;
    private boolean pin_initial_state_MS= false;
    private boolean selection_mode = false;
    private boolean pin_multi_change = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Task_Fragment_Adding_Option_Available) {
            task_fragment_adding_option_available = (Task_Fragment_Adding_Option_Available) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar Task_Fragment_DismissListener 3333");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        task_fragment_adding_option_available = null; // Evita fugas de memoria
    }

    @Override
    public void onResume(){
        super.onResume();
        getStartOfToday();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        selection_item_menu_PopUp = new Selection_Item_Menu_MemoBoard_PopUpWindow(view.getContext(),-1);

        recyclerView_Tasks = view.findViewById(R.id.rvTasks);

        DB_T = new DB_Tasks(getContext());

        selected_list = new ArrayList<>();
        task_elements = new ArrayList<>();
        task_elements_aux = new ArrayList<>();
        selected_positions_list = new ArrayList<>();

        main_TasksFragment = view.findViewById(R.id.Layout_Main_Task_Fragment);

        et_searched_Text_Task = view.findViewById(R.id.Searched_Text_Task);

        fl_search = view.findViewById(R.id.button_Search);

        recyclerView_Tasks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_taskFragment = new Adapter_Recycler_Tasks_List(getContext(),selected_list,task_elements,this,this);

        Update_Recycler_View();

        et_searched_Text_Task.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String searched_Text = et_searched_Text_Task.getText().toString();
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
                et_searched_Text_Task.setVisibility(VISIBLE);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(et_searched_Text_Task.isFocused()){
                    et_searched_Text_Task.setText("");
                    et_searched_Text_Task.setVisibility(View.GONE);
                    fl_search.setVisibility(View.VISIBLE);
                    fl_search.requestFocus();
                }else{
                    requireActivity().finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }
    private void Update_Recycler_View_ftsValues_Snipped4(String searched_Text) {
        try (Cursor cursor_Tasks = DB_T.get_All_Tasks_fts_2(searched_Text)) {
            if(cursor_Tasks.getCount()==0 || et_searched_Text_Task.getTextSize()==0){
                adapter_taskFragment.Change_Searching_Mode_Status(false);
                Clear_Lists();
                recyclerView_Tasks.setAdapter(adapter_taskFragment);
                recyclerView_Tasks.setLayoutManager(new LinearLayoutManager(getContext()));
                Update_Recycler_View();
                task_fragment_adding_option_available.onTaskFragment_Adding_Option_Available(true);
            }else{
                task_fragment_adding_option_available.onTaskFragment_Adding_Option_Available(false);
                adapter_taskFragment.Change_Searching_Mode_Status(true);
                if(cursor_Tasks.getCount() < task_elements.size()){
                    int i = 0;
                    while(i <= cursor_Tasks.getCount() -1 && i <= task_elements.size() -1){
                        Task_Main task_main = (Task_Main) task_elements.get(i);
                        if(task_main.getUnfolded()){
                            task_main.setUnfolded(!task_main.unfolded);
                            RecyclerView_Unfold_Update(i,task_main.unfolded, task_main.task_id);
                        }
                        cursor_Tasks.moveToPosition(i);
                        if(task_main.task_id != cursor_Tasks.getLong(0)){
                            Remove_Element_In_Every_List(i);
                            adapter_taskFragment.notifyItemRemoved(i);
                        }else{
                            ((Task_Main) task_elements.get(i)).setTitle(cursor_Tasks.getString(4));
                            ((Task_Main) task_elements.get(i)).setNote(cursor_Tasks.getString(3));
                            adapter_taskFragment.notifyItemChanged(i);
                            i++;
                        }
                    }
                    if(cursor_Tasks.getCount() < task_elements.size()){
                        int j = 0  ;
                        while (i <=task_elements.size()-1) {
                            Log.d("2Search", "            Removing: Another Title: "+task_elements.get(i).getContent()+ "    i: "+ i);
                            Remove_Element_In_Every_List(i);
                            j++;
                        }
                        adapter_taskFragment.notifyItemRangeRemoved(i,j);
                    }
                }else if (cursor_Tasks.getCount() > task_elements.size()){
                    int i = 0;
                    while(i <= task_elements.size() -1){
                        cursor_Tasks.moveToPosition(i);
                        Task_Main task_main = (Task_Main) task_elements.get(i);
                        if(task_main.task_id != cursor_Tasks.getLong(0)){
                            Task_Main task_adding = DB_T.getASpecificTask(cursor_Tasks.getLong(0));
                            task_adding.setTitle(cursor_Tasks.getString(4));
                            task_adding.setNote(cursor_Tasks.getString(3));
                            Add_Element_In_Every_List(i,task_adding,false);
                            adapter_taskFragment.notifyItemInserted(i);
                        }else{
                            ((Task_Main) task_elements.get(i)).setTitle(cursor_Tasks.getString(4));
                            ((Task_Main) task_elements.get(i)).setNote(cursor_Tasks.getString(3));
                            adapter_taskFragment.notifyItemChanged(i);
                        }
                        i++;
                    }
                    if(cursor_Tasks.getCount() > task_elements.size()){
                        int j = 0  ;
                        int start = i;
                        while (i <=cursor_Tasks.getCount()-1) {
                            cursor_Tasks.moveToPosition(i);
                            Task_Main task_main = DB_T.getASpecificTask(cursor_Tasks.getLong(0));
                            Log.d("2Search", "            Adding: Another Title: "+task_main.note+ "    i: "+ i);
                            task_main.setTitle(cursor_Tasks.getString(4));
                            task_main.setNote(cursor_Tasks.getString(3));
                            Add_Element_In_Every_List(task_elements.size(),task_main,false);
                            j++;
                            i++;
                        }
                        adapter_taskFragment.notifyItemRangeRemoved(start,j);
                    }
                }else{
                    while(cursor_Tasks.moveToNext()){
                        int i = cursor_Tasks.getPosition();
                        ((Task_Main) task_elements.get(i)).setTitle(cursor_Tasks.getString(4));
                        ((Task_Main) task_elements.get(i)).setNote(cursor_Tasks.getString(3));
                    }
                    adapter_taskFragment.notifyItemRangeChanged(0,task_elements.size());
                }
            }
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
                    Add_Element_In_Every_List(task_elements.size(),task,false);
                    if(task.has_sub_tasks && task.unfolded){//--the double verification is correct. Its allow to remember that an specific Task is preferred as unfolded even if currently it has no subTasks
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
                                    Add_Element_In_Every_List(task_elements.size(),task_sub,false);
                                }
                            }
                        }
                    }
                }
            }
        }
        recyclerView_Tasks.setAdapter(adapter_taskFragment);
        recyclerView_Tasks.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void Clear_Lists(){
        if(selected_list.isEmpty()){
            return;
        }
        selected_list.clear();
        task_elements.clear();
    }
    private void Remove_Element_In_Every_List(int position){
        selected_list.remove(position);
        task_elements.remove(position);
    }
    private void Add_Element_In_Every_List(int position,Task_Element task_element, boolean selection){
        task_elements.add(position, task_element);
        selected_list.add(position, selection);
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

        Task_Element element = task_elements.get(position);
        long task_id = (element.getViewType() == 0)    ?    element.getId()    :    ((Task_Sub) element).getParent_id();

        if (getActivity() instanceof Memo_Board_New_Aux) {
            Intent intent = new Intent(getActivity(), Task_Visualizer.class);
            intent.putExtra("send_task_id",task_id);
            ((Memo_Board_New_Aux) getActivity()).getLanzadorActivityC().launch(intent);/// Le pedimos al launcher de la MainActivity que la ejecute
        }
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
        selected_list.set(position,!selected_list.get(position));

        ///--Select sub task if the main one have subtasks:
        int sub_task_selected_count = 0;
        for(int i = position + 1; i <= task_elements.size() - 1; i++){
            if (task_elements.get(i).getViewType() != 1) break;
            selected_list.set(i,!selected_list.get(i));
            sub_task_selected_count ++;
        }
        if(sub_task_selected_count > 0){
            adapter_taskFragment.notifyItemRangeChanged(position + 1, sub_task_selected_count);
        };

        selection_count += selected_list.get(position) ? 1 : -1; /// Ternary Operator!

        selection_mode = selection_count > 0;

        selected_positions_list.add(0,position);

        if(selection_item_menu_PopUp.popupWindow == null && selection_count >= 2){
            //--Buscar estado del pin de las dos primeras notas seleccionadas:
            Task_Main _task_main = (Task_Main) task_elements.get(selected_positions_list.get(0));
            Task_Main _task_main2 = (Task_Main) task_elements.get(selected_positions_list.get(1));

            pin_initial_state_MS = _task_main.getPin() & _task_main2.getPin() || _task_main2.getPin(); /// AND Operator

            selection_item_menu_PopUp.setListener_dismiss(this);
            selection_item_menu_PopUp.show(v, pin_initial_state_MS);

            adapter_taskFragment.Change_multi_selection_state(selection_mode);
            adapter_taskFragment.Set_Selection_Mode_On();

            adapter_taskFragment.notifyItemChanged(selected_positions_list.get(1),this);

            task_fragment_adding_option_available.onTaskFragment_Adding_Option_Available(false);
        }
        if(selection_item_menu_PopUp.popupWindow != null && !selection_mode){
            Restart_Selection();
        }
        if(selection_item_menu_PopUp.popupWindow != null && selection_mode){
        }

        adapter_taskFragment.notifyItemChanged(position);

        //---Set unselecting_view to repeated unselect
        if(selected_positions_list.size()==2) {
            if(Objects.equals(position, selected_positions_list.get(1))){
                adapter_taskFragment.Change_is_repeated_value(true);
                selected_positions_list.clear();
            }
        }

        if(selected_positions_list.size()==3) selected_positions_list.remove(2);
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
        Sort_Sub_Task_According_Original_Order(position);
    }
    private void Sort_Sub_Task_According_Original_Order(int position) {
        if(order_type == 2) return;
        int first_sub = position + 1;
        int last_sub = position;
        while(task_elements.size() - 1 >= (last_sub + 1) && task_elements.get(last_sub + 1).getViewType() == 1){
            last_sub++;
        }
        if ((last_sub - first_sub) < 1) return;
        int looking_position = 0 ;
        for (int i = first_sub; i <= last_sub; i++) {
            looking_position ++;
            Task_Sub task_sub_i = (Task_Sub) task_elements.get(i);
            if (task_sub_i.getTask_sub_position() != (looking_position)) {
                for (int j = i + 1; j <= last_sub; j++) {
                    Task_Sub task_sub_j = (Task_Sub) task_elements.get(j);
                    if (task_sub_j.getTask_sub_position() == (looking_position)) {
                        task_elements.remove(j);
                        task_elements.add(i, task_sub_j);
                        adapter_taskFragment.notifyItemMoved(j, i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void Complete_Sub_Task(int position) {
        Task_Sub task_sub = (Task_Sub) task_elements.get(position);
        Change_Sub_Task_Completed_Status(position, task_sub);
        int result = DB_T.Verify_If_All_Sub_Task_Completed(task_sub.parent_id);

        Task_Main _task_main= new Task_Main();
        int _task_main_position = 0;
        for(int i = position -1; i >= 0; i --){
            if(task_elements.get(i).getViewType() == 0){
                _task_main = (Task_Main) task_elements.get(i);
                _task_main_position = i;
                break;
            }
        }

        Set_Sub_Tasks_Order_When_Complete(position);

        if(result > 0){
            if(_task_main.completed) return;
            Change_Complete_Main_Task_Status(_task_main, _task_main_position);
        }else{
            if(!_task_main.completed) return;
            Change_Complete_Main_Task_Status(_task_main, _task_main_position);
        }
    }

    private void Set_Sub_Tasks_Order_When_Complete(int position) {
        Task_Sub _task_sub = (Task_Sub) task_elements.get(position);
        int first_sub = position;
        int last_sub = position;
        while(task_elements.get(first_sub-1).getViewType()==1){
            first_sub--;
        }
        while(task_elements.size() - 1 >= (last_sub + 1) && task_elements.get(last_sub + 1).getViewType() == 1){
            last_sub++;
        }
        if ((last_sub - first_sub) < 1) return;
        if((order_type == 1) == _task_sub.completed){
            for (int i = first_sub; i <= position ; i++) { /// Move to the superior opposite group  (completed / uncompleted)
                Task_Sub task_sub_i = (Task_Sub) task_elements.get(i);
                if (task_sub_i.getCompletion() != _task_sub.completed || task_sub_i.getTask_sub_position() >= _task_sub.getTask_sub_position()) {/// if complete value is different let it pass, else , verify if the current task have a GreaterOrEqual positon
                    Update_Sub_Tasks_Order_When_Complete(position, i, _task_sub);
                    break;
                }
            }
        }else{//--   Default (Uncomplete first) ///--Esta solucion asume el order_type == 0
            for (int i = last_sub; i >= position; i--) {/// Move to the inferior opposite group (completed / uncompleted)
                Task_Sub task_sub_i = (Task_Sub) task_elements.get(i);
                if (task_sub_i.getCompletion() != _task_sub.completed || task_sub_i.getTask_sub_position() <= _task_sub.getTask_sub_position()) {/// if complete value is different let it pass, else , verify if the current task have a LesserOrEqual positon
                    Update_Sub_Tasks_Order_When_Complete(position, i, _task_sub);
                    break;
                }
            }
        }
    }
    private void Update_Sub_Tasks_Order_When_Complete(int from_position, int to_position, Task_Sub _task_sub){
        task_elements.remove(from_position);
        task_elements.add(to_position, _task_sub);
        adapter_taskFragment.notifyItemMoved(from_position, to_position);
        if(order_type != 2) recyclerView_Tasks.scrollToPosition(from_position);
    }

    private void Change_Complete_Main_Task_Status(Task_Main _task_main, int _task_main_position) {
        _task_main.setCompleted(!_task_main.completed);///cambio en task_elements al ser un puntero.
        long _current_time = System.currentTimeMillis();
        if(DB_T.Modify_Main_Task_Completed_Status(_task_main.task_id, _task_main.completed, _current_time)) {
            adapter_taskFragment.notifyItemChanged(_task_main_position);
        }
    }
    private void Change_Sub_Task_Completed_Status(int position, Task_Sub task_sub) {
        task_sub.setCompleted(!task_sub.completed);///cambio en task_elements al ser un puntero.
        if(DB_T.Modify_Sub_Task_Completed_Status(task_sub.task_sub_id, task_sub.completed)){
            adapter_taskFragment.notifyItemChanged(position);
        }
    }

    /// Pin Items:
    @Override
    public void PinItem(int position) {
        long start_nano_time = System.nanoTime();
        Task_Main _task = (Task_Main) task_elements.get(position);

        if(pin_multi_change && pin_initial_state_MS ^ _task.getPin()){///XOR Operator (XOR == Return 1 if both bits are different)
            selected_list.set(position,!selected_list.get(position));// invert value
            adapter_taskFragment.notifyItemChanged(position);
            return;
        }

        Toast.makeText(getContext(), "Repeated pinned", Toast.LENGTH_SHORT).show();
        adapter_taskFragment.Change_is_repeated_value(true);

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
            RecyclerView_Pin_Unfold_Update(position,false);
        }

        Remove_Element_In_Every_List(position);
        adapter_taskFragment.notifyItemChanged(position);

        int current_pinned_tasks = 0;

        if(adapter_taskFragment.Get_Searching_Mode_Status() == true){
            int i = 0 ;
            while (i <= task_elements.size()-1) {
                Task_Main task_main = (Task_Main) task_elements.get(i);
                if(task_main.getPin() != _task_main.pin){/// Pin
                    while(i <= task_elements.size()-1 && _task_main.completed != ((Task_Main) task_elements.get(i)).completed ){/// Completed
                        i++;
                        Log.d("Update Pin Recycler", "        While (completed!=) adding:  i=" + i);
                    }
                    while(i <= task_elements.size()-1 && _task_main.date_modified < ((Task_Main) task_elements.get(i)).date_modified && _task_main.completed == ((Task_Main) task_elements.get(i)).completed ){/// Date
                        i++;
                        Log.d("Update Pin Recycler", "        While (date<) adding:  i=" + i);
                    }
                    current_pinned_tasks = i;
                    Log.d("Update Pin Recycler", "        Current=" + i);
                    break;
                }
                i++;
                Log.d("Update Pin Recycler", "        while (pin==) adding  i=" + i);
            }
        }else{
            current_pinned_tasks = DB_T.get_Specific_Task_Sorted_by_Pin_and_Date(_task_main.task_id);
            if(current_pinned_tasks > 0){
                int main_task_counter = 0;
                for(int i = 0; i <= task_elements.size()-1; i++ ){
                    if(task_elements.get(i).getViewType()==0){
                        if( main_task_counter == current_pinned_tasks) {
                            //--approved
                            break;
                        }
                        main_task_counter ++;
                    }else{
                        current_pinned_tasks ++;
                    }
                }
            }
        }

        _task_main.setPin(!_task_main.getPin());

        Add_Element_In_Every_List(current_pinned_tasks,_task_main,false);
        adapter_taskFragment.notifyItemMoved(position,current_pinned_tasks);
        adapter_taskFragment.notifyItemChanged(current_pinned_tasks);

        if(was_unfolded){
            RecyclerView_Pin_Unfold_Update(current_pinned_tasks,true);
        }

        Restart_Selection();
    }
    private void RecyclerView_Pin_Unfold_Update(int position, boolean unfolded) {
        /// Fold
        if(!unfolded){   //--Delete all sub task from the task_elements list and update
            int sub_tasks_count = 0;
            while( position + 1 <= task_elements.size()-1){
                if (task_elements.get(position+1).getViewType() == 0) break;
                task_elements_aux.add(task_elements.get(position +1));
                Remove_Element_In_Every_List(position +1);
                sub_tasks_count ++;
            }
            adapter_taskFragment.notifyItemRangeRemoved(position+1,sub_tasks_count);
            adapter_taskFragment.notifyItemChanged(position);
        }
        ///Unfold
        if(unfolded){
            for(int i = task_elements_aux.size()-1; i >= 0 ; i --){
                Add_Element_In_Every_List(position +1, task_elements_aux.get(i), false );
            }
            adapter_taskFragment.notifyItemRangeInserted(position + 1,task_elements_aux.size());
            task_elements_aux.clear();
        }
    }

    /// Reminder
    @Override
    public void SetReminder(int position) {
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
        boolean Main_IsSelected = selected_list.get(position);
        /// Fold
        if(!unfolded){///--Delete all sub task from the task_elements list and update
            int sub_tasks_count = 0;
            while(position+1 <= task_elements.size()-1){
                if (task_elements.get(position+1).getViewType() == 0) break;
                Remove_Element_In_Every_List(position +1);
                sub_tasks_count ++;
            }
            adapter_taskFragment.notifyItemRangeRemoved(position+1,sub_tasks_count);

            Correction_When_Unfold_Before_Select_Second_Item(position,sub_tasks_count, -1);
        }
        /// Unfold
        if(unfolded){///--Look for all the subs task that have for parent the present main task (bring a cursor), add them in the position

            try (Cursor cursor_Tasks_Sub= DB_T.get_All_Tasks_Sub_For_Specific_Task_Main(task_id)) {
                if(cursor_Tasks_Sub.getCount()==0) return;

                int id_indx_sub = cursor_Tasks_Sub.getColumnIndex("_id");
                int parent_indx_sub = cursor_Tasks_Sub.getColumnIndex("parent_id");
                int note_indx_sub = cursor_Tasks_Sub.getColumnIndex("note");
                int completed_indx_sub = cursor_Tasks_Sub.getColumnIndex("completed");
                int task_sub_position_indx_sub = cursor_Tasks_Sub.getColumnIndex("task_sub_position");

                while (cursor_Tasks_Sub.moveToNext()){
                    Task_Sub task_sub = new Task_Sub(cursor_Tasks_Sub.getLong(id_indx_sub),
                            cursor_Tasks_Sub.getLong(parent_indx_sub),
                            cursor_Tasks_Sub.getString(note_indx_sub),
                            cursor_Tasks_Sub.getInt(completed_indx_sub)==1,
                            cursor_Tasks_Sub.getInt(task_sub_position_indx_sub));
                    Add_Element_In_Every_List(position+1+ cursor_Tasks_Sub.getPosition(), task_sub , Main_IsSelected ? true :false );///TERNARY Operator
                }
                adapter_taskFragment.notifyItemRangeInserted(position+1,cursor_Tasks_Sub.getCount());

                Correction_When_Unfold_Before_Select_Second_Item(position, cursor_Tasks_Sub.getCount(), 1);
            }
        }
        adapter_taskFragment.notifyItemChanged(position);
    }
    private void Correction_When_Unfold_Before_Select_Second_Item(int position, int range, int positivity) {/// Correction when the user fold/unfold before choose the second multiselection item:
        if(selected_positions_list.size()==1){
            if(position < selected_positions_list.get(0)){
                selected_positions_list.set(0,selected_positions_list.get(0)+ (range * positivity ));
            }
        }
    }

    @Override
    public void RemoveItem(int position) {
        Task_Main _task = (Task_Main) task_elements.get(position);

        Reminder_Notification.Cancel_Reminder_Alarm(main_TasksFragment,_task.task_id,1,_task.reminder);

        if(DB_T.Send_Task_To_Trash(_task.task_id,_task.date,_task.note,_task.note,_task.pin,20,_task.completed,_task.has_sub_tasks)){
            if(_task.has_sub_tasks){
                DB_T.Send_Previous_Sub_Task_To_Trash_With_Out_Modification(_task.task_id);
                if(_task.unfolded){
                    int sub_tasks_count = 0;
                    while(position+1 <= task_elements.size()-1){
                        if(task_elements.get(position + 1).getViewType()==0) break;
                        Remove_Element_In_Every_List(position + 1);
                        sub_tasks_count ++;
                    }
                    adapter_taskFragment.notifyItemRangeRemoved(position + 1, sub_tasks_count);
                }
            }

            Remove_Element_In_Every_List(position);
            adapter_taskFragment.notifyItemRemoved(position);

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
        adapter_taskFragment.Change_multi_selection_state(false);
        adapter_taskFragment.Set_Selection_Mode_Off();

        task_fragment_adding_option_available.onTaskFragment_Adding_Option_Available(true);
    }

    @Override
    public void onMemoBoardSelection_PopupClosed(int option) {
        if(option == 1){/// Option Pin
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
        if(option == 2){/// Option Reminder
        }
        if(option == 3){/// Option Delete
            for(int i = selected_list.size() -1 ;i >= 0; i--){
                if(selected_list.get(i)) {
                    RemoveItem(i);
                }
            }
            selected_positions_list.clear();
        }
    }
}