package com.example.kuai_notes_project;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

///290 V03 , 347 V04, 281 V05, 485 V06, 429 V07, 529 V07antes de refactorizar DB con _id, DB con date = long, DB unificado (soft deleted flag)A , 740L 32264c V07.02 indentado repeticiones diarias en reminder, 784 V07.3.1 antes de optimizar y refactorizar
public class MainActivity extends AppCompatActivity implements Reminder_PopUpWindow.OnValueSelectedListener, Reminder_PopUpWindow.PopupDismissListener,Note_Update_Listener {
    private int note_modification_result = 0; /// 0 Element Modification, 1 New Element, 2 Element Deleted
    public interface NoteVisualizer_Modification_in_Notes {//esto puede ir tambien en una clase separada
        void onNoteVisualizer_Modification_in_Notes(boolean modification_in_notes, long note_id); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    private NoteVisualizer_Modification_in_Notes modification_in_notes;
    public void setNoteVisualizer_Modification_in_Notes(NoteVisualizer_Modification_in_Notes listener){
        this.modification_in_notes = listener;
    }
    private boolean journal_notes_update;/// If there is a modification or A new Note, there must be an update on the journal note list recyclerview.

    private DB_Notes DB_N;
    private TextView tv_Date, tv_Info;
    private EditText et_Title, et_Note;
    private Note note = new Note();

    private long received_note_id = 0;
    private boolean change_in_note = false, show_note_info = false, now_is_something_written = false;
    private FrameLayout fl_Change_Pin_Status, fl_Change_Reminder_Status, fl_Back, fl_Delete;
    private FrameLayout fl_Change_Pin_Status_Ghost, fl_Change_Reminder_Status_Ghost, fl_Back_Ghost, fl_Delete_Ghost;
    private Date_of_Note DoN;
    private View layout_date_and_info, layout_body_note, layout_dim;
    private Animation AnimationPin, AnimationReminder, AnimationDate, AnimationDateInvert, AnimationDateInvert_Debounce_Slower, AnimationInfo, AnimationInfoInvert, AnimationInfoInvert_Debounce_Slower, AnimationPinAppear, AnimationPinFade;
    private Animation AnimationNoteAppear, AnimationTitleAppear, AnimationNoteHintFading;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel;
    private int previous_note_size = -1;
    private char last_deleted_char = '0';
    Indent_Replicator indentReplicator;
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private static final long DEBOUNCE_DELAY_OF_HIDE_INFO = 1700;

    private void Date_Format_Change_With_Debounce(){
        if (debounceHandler != null){//Si existia previamente se cancela
            debounceHandler.removeCallbacks(debounceRunnable);
        }

        debounceRunnable = new Runnable() {//Creacion de tarea para ejecucion con debounce:
            @Override
            public void run() {
                Date_Format_Change_Slower();
            }
        };

        debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY_OF_HIDE_INFO); //programacion de la tarea
    }


    ///private Space space_below_note;



    @Override
    protected void onPause() {
        super.onPause();
        if (Note_is_not_empty() && change_in_note) {
            Log.d("Delete", "onPause, saving");
            Save_Note();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Note_is_not_empty() && change_in_note) {
            Log.d("onResume", "-------------------------onResume, saving");
            Save_Note();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_navigation_bar));

        DB_N = new DB_Notes(this);

        tv_Date = findViewById(R.id.Note_Time);
        tv_Info = findViewById(R.id.Note_Info);

        et_Title = findViewById(R.id.Title);
        et_Note = findViewById(R.id.Body_Note);

        layout_body_note = findViewById(R.id.Layout_Body_Note);

        fl_Change_Pin_Status = findViewById(R.id.FrameLayout_Change_Pin_Status);
        fl_Change_Reminder_Status = findViewById(R.id.FrameLayout_Change_Reminder_Status);
        fl_Back = findViewById(R.id.fl_Back);
        fl_Delete = findViewById(R.id.fl_Delete);

        fl_Change_Pin_Status_Ghost = findViewById(R.id.FrameLayout_Change_Pin_Status_Ghost);
        fl_Change_Reminder_Status_Ghost = findViewById(R.id.FrameLayout_Change_Reminder_Status_Ghost);
        fl_Back_Ghost = findViewById(R.id.fl_Back_Ghost);
        fl_Delete_Ghost = findViewById(R.id.fl_Delete_Ghost);

        DoN = new Date_of_Note();

        layout_date_and_info = findViewById(R.id.Layout_date_and_info);

        AnimationPin = AnimationUtils.loadAnimation(this, R.anim.pin_visualizer_change_status);
        AnimationReminder = AnimationUtils.loadAnimation(this, R.anim.reminder_visualizer_change_status);
        AnimationDate = AnimationUtils.loadAnimation(this, R.anim.date_visualizer);
        AnimationDateInvert = AnimationUtils.loadAnimation(this, R.anim.date_visualizer_invert);
        AnimationDateInvert_Debounce_Slower = AnimationUtils.loadAnimation(this, R.anim.date_visualizer_invert_slower);
        AnimationInfo = AnimationUtils.loadAnimation(this, R.anim.info_visualizer);
        AnimationInfoInvert = AnimationUtils.loadAnimation(this, R.anim.info_visualizer_invert);
        AnimationInfoInvert_Debounce_Slower = AnimationUtils.loadAnimation(this, R.anim.info_visualizer_invert_debounce_slower);
        AnimationPinAppear = AnimationUtils.loadAnimation(this, R.anim.appear_visualizer);
        AnimationPinFade = AnimationUtils.loadAnimation(this, R.anim.fade_visualizer);
        AnimationNoteAppear = AnimationUtils.loadAnimation(this, R.anim.note_appear_mainvisualizer);
        AnimationTitleAppear = AnimationUtils.loadAnimation(this, R.anim.title_appear_mainvisualizer);
        AnimationNoteHintFading = AnimationUtils.loadAnimation(this, R.anim.hint_note_fading_visualizer);

        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);

        received_note_id = getIntent().getLongExtra("send_note_id", 0);

        layout_dim = findViewById(R.id.layout_dim_noteVisualizer);
        indentReplicator = new Indent_Replicator(this);

        ///space_below_note = findViewById(R.id.Space_Below_Note_Main);


        if (received_note_id != 0) {
            Initialize_Received_Note(received_note_id);
            Set_Written_Note_Style();
        } else {
            Set_Blank_Note_Style();

            new Handler().postDelayed(new Runnable() {//Se enfoca en cuerpo de la nota y se abre el teclado solo si el texto es nuevo
                @Override
                public void run() {
                    et_Note.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //Abrir teclado luego de realizar el enfoque:
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(et_Note, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }, 300); // Realiza accion luego de 300 milisegundos
        }

        et_Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                Verify_if_exist_something();
                if(show_note_info){
                    Date_Format_Change_With_Debounce();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        et_Note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                if (show_note_info) {
                    tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));

                    Date_Format_Change_With_Debounce();
                }
                Verify_if_exist_something();

                int _cursor_position = et_Note.getSelectionStart();
                indentReplicator.ejecutar_Accion(s,previous_note_size,_cursor_position,last_deleted_char);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        fl_Change_Pin_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Note_is_not_empty()) {
                    Pin_Note();
                    fl_Change_Pin_Status.startAnimation(AnimationPin);
                }
            }
        });
        fl_Change_Reminder_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Note_is_not_empty()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(et_Note.getWindowToken(),0);
                    }

                    tv_Date.setAlpha(0.9f);
                    tv_Info.setAlpha(0.9f);
                    et_Note.setAlpha(0.8f);
                    layout_dim.setVisibility(View.VISIBLE);
                    layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light)));
                    layout_dim.startAnimation(AnimationLayoutDimAppear);

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

        fl_Delete_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Note();
            }
        });
        fl_Back_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Out_Of_Activity();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Out_Of_Activity();
            }
        });
    }

    private void Initialize_Received_Note(long received_note_id) {
        note = DB_N.getASpecificNote(received_note_id);
        now_is_something_written = true;
        et_Title.setText(note.title);
        et_Note.setText(note.note);
        tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        Change_Pin_Status_Style();
        Change_Reminder_Status_Style();
        previous_note_size = note.note.length();
    }

    private void Set_Written_Note_Style() {
        et_Title.startAnimation(AnimationTitleAppear);
    }

    private void Set_Blank_Note_Style() {
        fl_Change_Reminder_Status.setScaleX(0.9f);
        fl_Change_Reminder_Status.setScaleY(0.9f);
        fl_Change_Pin_Status.setScaleX(0.9f);
        fl_Change_Pin_Status.setScaleY(0.9f);
        fl_Delete.setScaleX(0.9f);
        fl_Delete.setScaleY(0.9f);
        fl_Delete.setScaleY(0.9f);
        fl_Change_Reminder_Status.setAlpha(0.4f);
        fl_Change_Pin_Status.setAlpha(0.4f);
        fl_Delete.setAlpha(0.4f);

        et_Note.startAnimation(AnimationNoteAppear);
    }

    private void Verify_if_exist_something() {
        if (Note_is_not_empty() != now_is_something_written) {//    si el estado de la nota ha cambiado:
            now_is_something_written = now_is_something_written ^ true; ///Bitwise ^  1 ^ 1 = 0 (Only true [1] when is different)
            Update_Note_Status(now_is_something_written);
        }
    }

    private boolean Note_is_not_empty() {
        Editable __title = et_Title.getText();
        Editable __note = et_Note.getText();
        boolean title_empty = __title.length()==0;
        boolean note_empty = __note.length()==0;
        return !title_empty || !note_empty;
        /// Original: Using toString:
        //!!--Verify if this is more effiecient that a String verification
        //String _title = et_Title.getText().toString();
        //String _note = et_Note.getText().toString();
        //return !_title.isEmpty() || !_note.isEmpty();
        /// Secure Option: Verify if the EditText is Null:
        //boolean title_emptyx = TextUtils.isEmpty(et_Title.getText());
    }

    private void Update_Note_Status(boolean current_status) {
        if(current_status) {
            fl_Change_Pin_Status.setAlpha(1f);
            fl_Change_Reminder_Status.setAlpha(1f);
            fl_Delete.setAlpha(1f);

            fl_Change_Pin_Status.startAnimation(AnimationPinAppear);
            fl_Change_Reminder_Status.startAnimation(AnimationPinAppear);
            fl_Delete.startAnimation(AnimationPinAppear);
            et_Note.clearAnimation();
        }else{
            fl_Change_Pin_Status.startAnimation(AnimationPinFade);
            fl_Change_Reminder_Status.startAnimation(AnimationPinFade);
            et_Note.startAnimation(AnimationNoteHintFading);
            if(note.note_id == 0){
                fl_Delete.startAnimation(AnimationPinFade);
            }
        }
    }

    @Override
    public void Update_Note_Content(int indent_type, char last_deleted_char, int previous_note_size, int cursor_selection) {
        this.previous_note_size = previous_note_size;
        this.last_deleted_char = last_deleted_char;
    }

    /// Pin Note
    private void Pin_Note() {
        //note.setPin(note.getPin() ^ 1); ///XOR Operator
        note.setPin(!note.getPin());

        if(note.note_id == 0){
            Change_Pin_Status_Style();
            return;
        }

        if (DB_N.Modify_Pin_Status(note.note_id, note.pin)) {
            Toast.makeText(MainActivity.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
            Change_Pin_Status_Style();
        } else {
            Log.d("Main Activity", "Not_Pin_Modified");
        }
    }
    private void Change_Pin_Status_Style() {
        fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                note.getPin() ? R.color.ex_orange :R.color.Neutral_gray_icon_note)));  ///Ternary Operator
    }

    /// Reminder Note
    private void Set_Reminder_Note() {
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, -1);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);

        note.note = et_Note.getText().toString();
        note.title = et_Title.getText().toString();
        long _current_time = System.currentTimeMillis();
        note.date = _current_time;
        reminder_PopUp.show(layout_body_note, note);
    }
    private void Change_Reminder_Status_Style() {
        fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                note.getReminder() > 0 ? R.color.item_visualizer_icon_reminder_tint :R.color.Neutral_gray_icon_note)));  ///Ternary Operator
    }
    @Override
    public void OnValueSelected(int position, long alarm_Time, int reminder_type, int reminder_interval) {
        note.setReminder(alarm_Time);
        Change_Reminder_Status_Style();
        if(note.note_id==0){//!!---Verificar si realmente es necesario, deberia ya tener un ID si fue guardado
            note.setNote_id(DB_N.Get_Last_RowId());
        }
    }
    @Override
    public void onPopupClosed(int salida, int position) { //  0 nada/normal, 1 setter, 2 cancelado
        tv_Date.setAlpha(1f);
        tv_Info.setAlpha(1f);
        et_Note.setAlpha(1f);
        layout_dim.setVisibility(View.VISIBLE);

        if(salida == 1){//setter
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_confirm)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Setter);
            return;
        }

        if(salida == 2){//cancel
            layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reminder_discard)));
            layout_dim.startAnimation(AnimationLayoutDimDisappear_Cancel);
            return;
        }

        layout_dim.startAnimation(AnimationLayoutDimDisappear_Normal);
    }

    private void Save_Note() {
        boolean save_Success;

        long _current_time = System.currentTimeMillis();

        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        if (note.note_id == 0) {
            note.note_id = DB_N.Insert_Note_L(_current_time, _title, _note, note.pin, note.reminder, note.reminder_type, note.reminder_interval);
            save_Success = note.note_id > 0;
        } else {
            save_Success = DB_N.Modify_Note(note.note_id, _current_time, _title, _note, note.pin, note.reminder, note.reminder_type, note.reminder_interval);
        }

        if (save_Success) {
            //!!---Verificar, no se esta actualizando los datos recien agragados al objeto nota.
            change_in_note = false;
            note.date = _current_time;
            tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        }
    }

    private void Delete_Note() {
        if (Save_Note_in_TrashCan()) {

            //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
            Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later

            if (note.note_id != 0) {      //Delete Reminder if exist
                Reminder_Notification.Cancel_Reminder_Alarm(layout_body_note, note.note_id, 0,note.reminder);
            }
        }
    }
    private Boolean Save_Note_in_TrashCan() {
        note_modification_result = 2;
        if (!now_is_something_written) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
            if (note.title != null || note.note != null) {
                Log.d("Delete","Delete 1-");
                return  getNoteInTrashCan(note.date,note.title,note.note, 20, "1-Insertado datos previous");
            } else {
                if(note.note_id > 0 && DB_N.Note_Exist(note.note_id)){
                    Log.d("Delete","Delete 6-");
                    change_in_note = false;
                    Toast.makeText(MainActivity.this, "6.1-Insertado datos previous MOD", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
                    return DB_N.Send_Note_To_Trash_With_Out_DataBase_Modification(note.getNote_id(),note.pin,20); //!!--Check cual es la mejor opcion para este valor de expire days
                }
                Log.d("Delete","Delete 2-");
                note_modification_result = -1;
                Toast.makeText(MainActivity.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
                return true;
            }
        }
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();
        long _current_time = System.currentTimeMillis();
        if (!change_in_note) {   //if there_is_something save in database > Send to trashcan what was in the database before save
            Log.d("Delete","Delete 3-");
            return getNoteInTrashCan(note.date, _title, _note, 20,"3- Sin cambios, save proyectado en edit.T ");
        } else {
            Log.d("Delete","Delete 4-");
            return getNoteInTrashCan(_current_time, _title, _note, 20,"4- Cambios realizados, moving to trash ");
        }
    }
    private Boolean getNoteInTrashCan(long date, String title, String _note, int expire_days, String Delete_Case) {
        if ( note.note_id == 0 ) {
            Log.d("Delete","Delete 5-");
            Toast.makeText(MainActivity.this, "5- Cambios realizados, directo a TrashCan ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
            change_in_note = false;
            return DB_N.Insert_Note_Directly_in_Trash(date,title,_note,note.pin,20); //!!--Check cual es la mejor opcion para este valor de expire days
        }
        Toast.makeText(MainActivity.this, Delete_Case, Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
        return DB_N.Send_Note_To_Trash(note.note_id, date, title, _note, note.pin,  expire_days);
    }

    private void Date_Format_Change() { //Se creo una refactorizacion que agrega una evaluacion adicional de 2 >  a 3 evaluaciones, en post de no duplicar codigo
        show_note_info = !show_note_info;
        boolean note_exist = note.note_id != 0;
        tv_Date.setText(note_exist ? DoN.Set_Date_of_Note_In_Visualizer(note.date) : ""); ///Ternary Operator
        if (show_note_info) {
            if (note_exist) tv_Date.startAnimation(AnimationDate);
            tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
            tv_Info.startAnimation(AnimationInfo);
        } else {
            if (note_exist) tv_Date.startAnimation(AnimationDateInvert);
            tv_Info.startAnimation(AnimationInfoInvert);
        }
    }
    private void Date_Format_Change_Slower() { //Se creo una refactorizacion que agrega una evaluacion adicional de 2 >  a 3 evaluaciones, en post de no duplicar codigo
        show_note_info = !show_note_info;
        boolean note_exist = note.note_id != 0;
        tv_Date.setText(note_exist ? DoN.Set_Date_of_Note_In_Visualizer(note.date) : ""); ///Ternary Operator
        if (show_note_info) {
            if (note_exist) tv_Date.startAnimation(AnimationDate);
            tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
            tv_Info.startAnimation(AnimationInfo);
        } else {
            if (note_exist) tv_Date.startAnimation(AnimationDateInvert_Debounce_Slower);
            tv_Info.startAnimation(AnimationInfoInvert_Debounce_Slower);
        }
    }

    private void Out_Of_Activity() {
        if (!now_is_something_written) {
            Delete_Note();
        } else {
            if (tv_Date.getText().toString().isEmpty()) {
                tv_Date.setVisibility(View.GONE);
            }

            Return_To_Memo_Board();
        }

    }

    public void Return_To_Memo_Board() {

        View view = this.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        /// Verificacion:::::
        if (note.note_id == 0 && Note_is_not_empty() && change_in_note) {
            Log.d("MainActivity", "Return to memo board, saving before");
            Save_Note();
            note_modification_result = 1;
        }

        Intent  resultadoIntent = new Intent();
        resultadoIntent.putExtra("extra_modificacion", note_modification_result);
        resultadoIntent.putExtra("extra_id", note.note_id);
        Log.d("MainActivity", "Result_OK: " + MainActivity.RESULT_OK);
        Log.d("MainActivity", "Return to memo board, note id: " + note.note_id);
        setResult(MainActivity.RESULT_OK,resultadoIntent);

        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in, R.anim.return_activity_slide_right_out);

    }
}