package com.example.kuai_notes_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

///347 V4, 287 V5, 417 V6, 371 V7
public class Wasted_Note_Visualizer extends AppCompatActivity implements Reminder_PopUpWindow.OnValueSelectedListener{
    private DB_Notes DB_N;
    private TextView tv_Date ,tv_Info;
    private EditText et_Title, et_Note ;
    private Note note = new Note();
    private int expire_days = 0;

    private String previous_date = null;
    private long received_note_id = 0;
    private String complete_current_time = null;
    private boolean change_in_note = false;
    private boolean note_recycled = false;
    private boolean change_to_add_information = false;
    private FrameLayout fl_back, fl_Change_Recycler_Status, fl_Change_Pin_Status, fl_Change_Reminder_Status;
    private FrameLayout fl_Change_Recycler_Status_Ghost, fl_Back_Ghost, fl_Delete_Ghost, fl_Change_Pin_Status_Ghost, fl_Change_Reminder_Status_Ghost;
    private View layout_date_and_info;
    private View layout_body_note;

    private Date_of_Note DoN;
    private View wasted_note_global;
    private Animation AnimationDate , AnimationDateInvert, AnimationInfo, AnimationInfoInvert, AnimationPin, AnimationReminder, AnimationRecycler;
    private Animation AnimationFade , AnimationAppear;
    int previous_note_size = -1;
    private char last_deleted_char = '0';

    @Override
    protected void onPause(){
        super.onPause();
        if (Verify_if_it_is_not_empty() && change_in_note) {
            Save_Note();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (Verify_if_it_is_not_empty() && change_in_note) {
            Save_Note();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wasted_note_visualizer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.wasted_note_global), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.Brown_Navigation_bar_Wasted_Note));

        DB_N = new DB_Notes(this);

        tv_Date = findViewById(R.id.Note_Time);
        tv_Info = findViewById(R.id.Note_Info);

        et_Title = findViewById(R.id.Title);
        et_Note = findViewById(R.id.Body_Note);

        layout_body_note = findViewById(R.id.Layout_Body_Note);

        fl_Change_Recycler_Status = findViewById(R.id.FrameLayout_Change_Recycler_Status);
        fl_Change_Pin_Status = findViewById(R.id.FrameLayout_Change_Pin_Status);
        fl_Change_Reminder_Status = findViewById(R.id.FrameLayout_Change_Reminder_Status);
        fl_back = findViewById(R.id.fl_Back);

        fl_Back_Ghost = findViewById(R.id.fl_Back_Ghost);
        fl_Delete_Ghost = findViewById(R.id.fl_Delete_Ghost);
        fl_Change_Recycler_Status_Ghost = findViewById(R.id.FrameLayout_Change_Recycler_Status_Ghost);
        fl_Change_Pin_Status_Ghost = findViewById(R.id.FrameLayout_Change_Pin_Status_Ghost);
        fl_Change_Reminder_Status_Ghost = findViewById(R.id.FrameLayout_Change_Reminder_Status_Ghost);

        previous_date = getIntent().getStringExtra("send_date_of_note");
        received_note_id = getIntent().getLongExtra("send_note_id",0);

        complete_current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        DoN = new Date_of_Note();
        wasted_note_global = findViewById(R.id.wasted_note_global);

        AnimationDate = AnimationUtils.loadAnimation(this,R.anim.date_visualizer);
        AnimationDateInvert = AnimationUtils.loadAnimation(this,R.anim.date_visualizer_invert);
        AnimationInfo = AnimationUtils.loadAnimation(this,R.anim.info_visualizer);
        AnimationInfoInvert = AnimationUtils.loadAnimation(this,R.anim.info_visualizer_invert);
        AnimationPin = AnimationUtils.loadAnimation(this,R.anim.pin_visualizer_change_status);
        AnimationReminder = AnimationUtils.loadAnimation(this, R.anim.reminder_visualizer_change_status);
        AnimationRecycler = AnimationUtils.loadAnimation(this,R.anim.recycler_function_wastednote);
        AnimationAppear = AnimationUtils.loadAnimation(this, R.anim.appear_visualizer);
        AnimationFade = AnimationUtils.loadAnimation(this, R.anim.fade_visualizer);

        layout_date_and_info = findViewById(R.id.Layout_date_and_info);

        Bring_Note_From_DB( received_note_id );
        Set_Pin_Status();

        layout_body_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_Note.requestFocus();
                et_Note.setSelection(et_Note.getText().length());
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(et_Note, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        fl_Back_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Verify_if_it_is_not_empty()){
                    Delete_Note();
                }else{
                    Return_To_Memo_Board();
                }
            }
        });
        fl_Change_Recycler_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecycleItem();
            }
        });
        fl_Delete_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Note();
            }
        });
        fl_Change_Pin_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Verify_if_it_is_not_empty()) {
                    Pin_Note();
                    fl_Change_Pin_Status.startAnimation(AnimationPin);
                }
            }
        });
        fl_Change_Reminder_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Verify_if_it_is_not_empty()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(et_Note.getWindowToken(),0);
                    }
                    Set_Reminder_Note();
                    fl_Change_Reminder_Status.startAnimation(AnimationReminder);
                }
            }
        });
        layout_date_and_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date_Format_Change();
            }
        });
        et_Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!change_in_note){
                    change_in_note = true;
                    Change_to_Recycled_View();
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        et_Note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!change_in_note){
                    change_in_note = true;
                    Change_to_Recycled_View();
                }
                if(change_to_add_information){
                    tv_Info.setText(DoN.Set_Date_Note_Only_Information( et_Note.getText().toString()));
                }

                Indent_Replicator();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //--- Back button function:
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!Verify_if_it_is_not_empty()){
                    Delete_Note();
                }else{
                    Return_To_Memo_Board();
                }
            }
        });
    }
    private void Indent_Replicator() {
        //!!---como se puede obtener solo una parte del text (para optimizar. solo necesito los ultimos caracteres)
        Editable note_editable = et_Note.getText();
        if(previous_note_size > note_editable.length()){
            Log.d("Indent_Replicator","  menor que previo. SALIR");
            previous_note_size = note_editable.length();

            //!!--separar metodo para quitar indentado

            int cursor_position = et_Note.getSelectionStart();
            if (cursor_position <= 1){ //debe ser mayor que uno
                Log.d("Indent_Replicator","  Cursor no es mayor que uno. SALIR");
                return;
            }

            char c = note_editable.charAt(cursor_position - 1);
            if (c != ' ' && c != '\t' && c != '-' && c != '*') {
                Log.d("Indent_Replicator","  no hay indentado previo al cursor.");
                last_deleted_char = c;
                return;
            }

            int jump_before_cursor = note_editable.toString().lastIndexOf('\n',cursor_position - 1);

            if (jump_before_cursor == -1){
                Log.d("Indent_Replicator","  no se encuentra salto previo. SALIR");
                last_deleted_char = c;
                return;
            }


            int indent_length = 0;

            Log.d("Indent_Replicator","  caracter_current:"+c+ "    caracter_previo:"+last_deleted_char);
            if (last_deleted_char != ' ' && last_deleted_char != '\t' && last_deleted_char != '-' && last_deleted_char != '*') {
                Log.d("Indent_Replicator","  el caracter borrado en la vez anterior no era indentado. SALIR");
                last_deleted_char = c;
                return ;
            }
            last_deleted_char = c;

            for (int i = jump_before_cursor + 1; i < cursor_position ; i++){
                c = note_editable.charAt(i);
                //!!-- optimizar para evaluar vi~etas aparte de los espacios
                if (c == ' ' || c == '\t' || c == '-' || c == '*') {
                    Log.d("Indent_Replicator","  ++c:("+c+")");
                    indent_length++;
                }else{
                    Log.d("Indent_Replicator","  --c:("+c+")");
                    //--Existe texto importante antes del indentado que no debe borrarse.
                    Log.d("Indent_Replicator","  Existe texto importante que no puede borrarse. SALIR");
                    return;
                }
            }

            note_editable.delete(cursor_position - indent_length, cursor_position);
            Log.d("Indent_Replicator","  Existe indentado antes de cursor: ELIMINAR INDENTADO ");

            return;
        }
        last_deleted_char = '0';

        if(previous_note_size == note_editable.length()){
            Log.d("Indent_Replicator","  igual a previo. SALIR");
            previous_note_size = note_editable.length();
            return;
        }
        previous_note_size = note_editable.length();

        int jumpBeforeEnd = note_editable.toString().indexOf('\n',note_editable.length()-1);


        //if(jumpBeforeEnd == -1){
        //    Log.d("Indent_Replicator","  no existe salto previo al cursor: ");
        //    return;
        //}

        int cursor_position = et_Note.getSelectionStart();
        Log.d("Indent_Replicator","Jump Before End: "+jumpBeforeEnd+"   cursorPosition:"+ cursor_position+
                "        previo_size: "+ previous_note_size + "   size: "+note_editable.length());
        //int cursor_position = et_Note.getSelectionStart();


        int jump_before_cursor = note_editable.toString().lastIndexOf('\n',cursor_position - 1);
        int penultimum__newLineIndex = note_editable.toString().lastIndexOf('\n',cursor_position - 2);
        Log.d("Indent_Replicator","  indx_salto_prev_cursor: "+jump_before_cursor+"   indx_penultimum_salto:"+ penultimum__newLineIndex);


        if (jump_before_cursor == -1){
            Log.d("Indent_Replicator","  no se encuentra salto previo.");
            return;
        }

        if (jump_before_cursor != (cursor_position - 1)){
            Log.d("Indent_Replicator","     indx_salto_prev_cursor: "+jump_before_cursor+
                    "   cursor_position:"+ cursor_position+" cursor --:"+(cursor_position - 1));
            Log.d("Indent_Replicator","  no existe salto previo al cursor ");
            return;
        }

        if(penultimum__newLineIndex == jump_before_cursor - 1){
            Log.d("Indent_Replicator","  Existen dos saltos seguidos. SALIR");
            return;
        }


        //tal vez aqui ayude el tamano del texto antes de la modificacion para saber cuando se borro, sin embargo esto es muy costoso
        Log.d("Indent_Replicator","  Existe salto justo antes de cursor: GENERAR INDENTADO ");



        int indentation_start = penultimum__newLineIndex + 1;
        StringBuilder indentation = new StringBuilder();

        for (int i = indentation_start; i < cursor_position ; i++){
            char c = note_editable.charAt(i);
            //!!-- optimizar para evaluar vi~etas aparte de los espacios
            if(c == ' ' || c == '\t' || c == '-'|| c == '*'){
                indentation.append(c);
            }else{
                break;
            }
        }

        Log.d("Indent_Replicator","  Indentation:(" + indentation + ")");

        //!!-- verifica cual metodo es el mas eficiente para saber si esta vacio

        if(indentation.length() > 0){
            ///Editable note_editable = et_Note.getText(); // O usa la variable que ya tenías
            ///int cursor_position = et_Note.getSelectionStart(); // Vuelves a obtener la posición actual del cursor

            // 1. Insertar el indentado en la posición del cursor
            note_editable.insert(cursor_position, indentation.toString());

            // 2. Opcional: Mover el cursor al final del texto insertado
            // Esto es lo que probablemente quieres para que el usuario pueda empezar a escribir
            et_Note.setSelection(cursor_position + indentation.length());
        }
        last_deleted_char = ' ';

        previous_note_size = note_editable.length();


        //if(indentation.length() > 0){
        //    et_Note.append(indentation.toString());
        //}
    }

    private void Change_to_Recycled_View(){
        wasted_note_global.setBackgroundColor(Color.parseColor("#FFF9EF"));

        getWindow().setStatusBarColor(getResources().getColor(R.color.Light_Status_Bar_Color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.Light_Navigation_Bar_Color));

        //fl_Change_Recycler_Status.setVisibility(View.GONE);
        fl_Change_Recycler_Status.startAnimation(AnimationRecycler);
        //!!-- set proper animation to fade recycler status
        fl_Change_Recycler_Status_Ghost.setVisibility(View.GONE);

        fl_Change_Pin_Status_Ghost.setVisibility(View.VISIBLE);
        fl_Change_Pin_Status.setVisibility(View.VISIBLE);
        fl_Change_Pin_Status.startAnimation(AnimationInfo);

        fl_Change_Reminder_Status_Ghost.setVisibility(View.VISIBLE);
        fl_Change_Reminder_Status.setVisibility(View.VISIBLE);
        fl_Change_Reminder_Status.startAnimation(AnimationInfo);

        fl_back.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A4A4A4")));
        et_Title.setTextColor(Color.parseColor("#1B1919"));
        tv_Info.setTextColor(Color.parseColor("#A0A0A0"));
        tv_Date.setTextColor(Color.parseColor("#A0A0A0"));

    }

    private void Bring_Note_From_DB(long received_note_id){
        if(!note_recycled){
            note = DB_N.getASpecificNote_In_Trash(received_note_id);
        }else{
            note = DB_N.getASpecificNote(received_note_id);
        }
        //Log.d("Bring note Wasted","id: "+note.note_id+"\ntitle: "+note.title+"\ndate: "+note.date);
        et_Title.setText(note.title);
        et_Note.setText(note.note);
        tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        expire_days = DB_N.get_expire_Day(received_note_id);
        //!! Need to add the reminder column when reminder function is working
    }

    private boolean Verify_if_it_is_not_empty(){
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        return !_title.isEmpty() || !_note.isEmpty();
    }
    private void Save_Note(){
        boolean _save_Success = false;

        //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
        long _current_time = System.currentTimeMillis();

        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        if(!note_recycled){

            //!!---reminder
            note_recycled = DB_N.Insert_Note(_current_time, _title, _note,note.pin,0,0,0);
            if(note_recycled){
                if (DB_N.Delete_Hard_Specific_Note(note.note_id)) {
                    Toast.makeText(Wasted_Note_Visualizer.this, "out of trash and saved", Toast.LENGTH_SHORT).show();
                    _save_Success = true;
                    note.setNote_id(DB_N.Get_Last_RowId());
                }
            }
        }else{
            //!!---reminder
            if (DB_N.Modify_Note(note.note_id, _current_time, _title, _note,note.pin,0,0,0)) {
                Toast.makeText(Wasted_Note_Visualizer.this, "Modified", Toast.LENGTH_SHORT).show();
                _save_Success = true;
            }
        }

        //-------Update the view of the date of last modification:
        if(_save_Success) {
            change_in_note = false;
            //previous_date = _current_time;
            note.date = _current_time;
            tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        }
    }
    private void Pin_Note(){

        if (!DB_N.Note_Exist(note.note_id)) {
            Save_Note();
        }
        //!!Verificar. ya no le encuentro sentido


        boolean pin_modify_Success = false;

        note.setPin( note.getPin() ^ 1); //XOR Operator

        //-------Modify pin status of the Note
        Boolean Modify_Pin_Status = DB_N.Modify_Pin_Status(note.note_id,note.pin);
        if (Modify_Pin_Status) {
            Toast.makeText(Wasted_Note_Visualizer.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
            pin_modify_Success = true;
        } else {
            Toast.makeText(Wasted_Note_Visualizer.this, "Not_Pin_Modified", Toast.LENGTH_SHORT).show();
        }

        //-------Set update the pin status:
        if(pin_modify_Success) {
            Set_Pin_Status();
        }
    }
    private void Set_Reminder_Note() {
        if (!DB_N.Note_Exist(note.note_id)) {
            Save_Note();
        }

        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, -1);
        reminder_PopUp.setListener(this);

        reminder_PopUp.show(layout_body_note, note);

    }
    private void Set_Pin_Status(){
        //fl_Change_Recycler_Status.setVisibility(View.VISIBLE);
        if(note.getPin() == 1){
            fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ex_orange)));
        }else{
            fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Neutral_gray_icon_note)));
        }
    }

    private void Set_Reminder_Status() {
        if (note.getReminder() > 0) {
            fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue_x2)));
        } else {
            fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Neutral_gray_icon_note)));
        }
    }

    private void Delete_Note(){
        if(note.note_id == 0){      //Esto es solo por precaucion pero creo que es inecesario
            Toast.makeText(Wasted_Note_Visualizer.this, "The note was not saved in the data base before, there is not need to delete.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean _Delete_Note_Checker = false;

        if (!note_recycled){
            _Delete_Note_Checker = DB_N.Delete_Hard_Specific_Note(note.note_id);
            Toast.makeText(Wasted_Note_Visualizer.this, "Note Burned.", Toast.LENGTH_SHORT).show();
        }///else{
         ///   _Delete_Note_Checker = DB_N.Delete_DB_DEPRECATED(note.note_id,20);
         ///   Toast.makeText(Wasted_Note_Visualizer.this, "Note Burned.", Toast.LENGTH_SHORT).show();
        ///}
        if (!_Delete_Note_Checker) {
            Toast.makeText(Wasted_Note_Visualizer.this, "NOT Deleted", Toast.LENGTH_SHORT).show();
        }

        if(_Delete_Note_Checker) {
            et_Title.setText("");
            et_Note.setText("");
            Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
        }
    }
    private void Date_Format_Change(){
            change_to_add_information = !change_to_add_information;
            if (change_to_add_information) {
                tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
                tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
                tv_Date.startAnimation(AnimationDate);
                tv_Info.startAnimation(AnimationInfo);
            } else {
                tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
                tv_Date.startAnimation(AnimationDateInvert);
                tv_Info.startAnimation(AnimationInfoInvert);
            }
    }

    public void Return_To_Memo_Board(){
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in,R.anim.return_activity_slide_right_out);
    }
    public void RecycleItem() {
        //----Remove Note from Data Base:
        //!!---reminder
        if(DB_N.Insert_Note(note.date,note.title,note.note,note.pin,0,0,0)){
            if(DB_N.Delete_Hard_Specific_Note(note.note_id)) {
                fl_Change_Recycler_Status.startAnimation(AnimationRecycler);
                //fl_Change_Recycler_Status.startAnimation(Animationtrans);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(R.anim.return_activity_slide_right_in_after_recycle,R.anim.return_activity_slide_right_out_after_recycle);
                    }
                }, 1150); // Realiza accion luego de 300 milisegundos

            }
        }
    }

    @Override
    public void OnValueSelected(int position, long alarm_Time) {
        note.setReminder(alarm_Time);
        Set_Reminder_Status();
        //!!---- Set animations
    }
}