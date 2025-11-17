package com.example.kuai_notes_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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

///290 V3 , 347 V4, 281 V5, 485 V6, 429 V7, 529 V7antes de refactorizar DB con _id, DB con date = long, DB unificado (soft deleted flag)
public class MainActivity extends AppCompatActivity implements Reminder_PopUpWindow.OnValueSelectedListener {
    private DB_Notes DB_N;
    private TextView tv_Date, tv_Info;
    private EditText et_Title, et_Note;
    private Note note = new Note();

    private long received_note_id = 0;
    private String previous_date = null;
    private long reversed_reminder = 0;
    private String complete_current_time = null;
    private boolean change_in_note = false;
    private boolean change_in_date = false;
    private boolean now_is_something_writed = false;
    private FrameLayout fl_Change_Pin_Status, fl_Change_Reminder_Status, fl_Back, fl_Delete;
    private FrameLayout fl_Change_Pin_Status_Ghost, fl_Change_Reminder_Status_Ghost, fl_Back_Ghost, fl_Delete_Ghost;
    private Date_of_Note DoN;
    private View layout_date_and_info;
    private View layout_body_note;
    private Animation AnimationPin, AnimationReminder, AnimationDate, AnimationDateInvert, AnimationInfo, AnimationInfoInvert, AnimationPinAppear, AnimationPinFade;
    private Animation AnimationNoteAppear, AnimationTitleAppear, AnimationNoteHintFading;
    //private ScrollView sv_note;


    @Override
    protected void onPause() {
        super.onPause();
        if (Verify_if_it_is_not_empty() && change_in_note) {
            Save_Note();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Verify_if_it_is_not_empty() && change_in_note) {
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
        AnimationInfo = AnimationUtils.loadAnimation(this, R.anim.info_visualizer);
        AnimationInfoInvert = AnimationUtils.loadAnimation(this, R.anim.info_visualizer_invert);
        AnimationPinAppear = AnimationUtils.loadAnimation(this, R.anim.appear_visualizer);
        AnimationPinFade = AnimationUtils.loadAnimation(this, R.anim.fade_visualizer);
        AnimationNoteAppear = AnimationUtils.loadAnimation(this, R.anim.note_appear_mainvisualizer);
        AnimationTitleAppear = AnimationUtils.loadAnimation(this, R.anim.title_appear_mainvisualizer);
        AnimationNoteHintFading = AnimationUtils.loadAnimation(this, R.anim.hint_note_fading_visualizer);

        ///reversed_reminder = getIntent().getLongExtra("send_reversed_alarm", 0);
        received_note_id = getIntent().getLongExtra("send_note_id", 0);

        ///if (reversed_reminder < 0) {
        ///    //Toast.makeText(this, "reversed minor: " + reversed_reminder, Toast.LENGTH_SHORT).show();

        ///    //test
        ///    Note note = DB_N.getASpecificNote_ByReminder(reversed_reminder);
        ///    if (reversed_reminder == note.reminder) {
        ///        //Toast.makeText(this, "reversed minor are equal: ", Toast.LENGTH_SHORT).show();

        ///    } else {
        ///        //Toast.makeText(this, "reversed minor not equal", Toast.LENGTH_SHORT).show();

        ///    }

        ///    previous_date = DB_N.getASpecificNoteDate_ByReminder(reversed_reminder);
        ///    //Toast.makeText(this, "previous date: " + previous_date, Toast.LENGTH_SHORT).show();
        ///} else {
        ///    //Toast.makeText(this, "reversed => 0 looking previous send previous date: " , Toast.LENGTH_SHORT).show();
        ///    previous_date = getIntent().getStringExtra("send_date_of_note");
        ///}


        complete_current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        if (received_note_id != 0) {
            //Toast.makeText(this, "received id note:" + received_note_id, Toast.LENGTH_SHORT).show();
            now_is_something_writed = true;
            //Toast.makeText(this, "reversed => 0 looking previous send previous date: " , Toast.LENGTH_SHORT).show();

            //---- Trae la nota si esta existe, luego coloca la informacion en los text view correspondientes
            Bring_Note_From_DB(received_note_id);
            Set_Pin_Status();
            Set_Reminder_Status();
            et_Title.startAnimation(AnimationTitleAppear);
        } else {
            Set_Icons_To_Empty_Note_Style();
            //Enfocar edit text luego de cargar todo garcias a Runnable
            //Se enfoca en cuerpo de la nota y se abre el teclado solo si el texto es nuevo
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    et_Note.requestFocus();
                    //Abrir teclado luego de realizar el enfoque:
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(et_Note, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }, 300); // Realiza accion luego de 300 milisegundos

            et_Note.startAnimation(AnimationNoteAppear);
        }

        fl_Back_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Out_Of_Activity();
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
        fl_Delete_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Note();
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
                    //inputMethodManager.showSoftInput(et_Note, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        et_Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                Verify_if_exist_something();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        //et_Note.setOnKeyListener(new View.OnKeyListener() {
        //    @Override
        //    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //        //Toast.makeText(MainActivity.this, "Salto", Toast.LENGTH_SHORT).show();
        //        if (event.getAction() == KeyEvent.ACTION_DOWN){
        //            Toast.makeText(MainActivity.this, "down", Toast.LENGTH_SHORT).show();
        //            if (keyCode == KeyEvent.KEYCODE_ENTER){
        //                //Toast.makeText(MainActivity.this, "enter", Toast.LENGTH_SHORT).show();
        //                //Indent_Replicator();
        //                return true;
        //            }
        //            //if (keyCode == KeyEvent.KEYCODE_SPACE){
        //            //    Toast.makeText(MainActivity.this, "space", Toast.LENGTH_SHORT).show();
        //            //    Indent_Replicator();
        //            //    return true;
        //            //}
        //            //if (keyCode == KeyEvent.KEYCODE_A){
        //            //    Toast.makeText(MainActivity.this, "a", Toast.LENGTH_SHORT).show();
        //            //    Indent_Replicator();
        //            //    return true;
        //            //}
        //        }
        //        return false;
        //    }
        //});
        et_Note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                if (change_in_date) {
                    tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
                }
                Verify_if_exist_something();
                if(now_is_something_writed){
                    //!!--se debe optimizar: era bueno solo para OnKeyListener
                    Indent_Replicator();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        //et_Note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        //    @Override
        //    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //        if(actionId == EditorInfo.IME_ACTION_DONE ||actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT){
        //            Toast.makeText(MainActivity.this, "dentro", Toast.LENGTH_SHORT).show();
        //            Indent_Replicator();
        //        }
        //        return false;
        //    }
        //});

        //--- Back button function:
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ///if (!now_is_something_writed && previous_date != null) {
                Out_Of_Activity();
            }
        });
    }

    private void Indent_Replicator() {
        //!!---como se puede obtener solo una parte del text (para optimizar. solo necesito los ultimos caracteres)
        Editable note_editable = et_Note.getText();
        int lastlast = note_editable.toString().indexOf('\n',note_editable.length()-1);
        int cursor_position = et_Note.getSelectionStart();
        Log.d("dd","  lastSpace: "+lastlast+"   cursorPosition:"+ cursor_position);
        //int cursor_position = et_Note.getSelectionStart();


        int previous_newLineIndex = note_editable.toString().lastIndexOf('\n',cursor_position - 1);
        int penultimum__newLineIndex = note_editable.toString().lastIndexOf('\n',cursor_position - 2);
        Log.d("dd","  prev: "+previous_newLineIndex+"   penultimum:"+ penultimum__newLineIndex);

        if (previous_newLineIndex == -1){
                Toast.makeText(MainActivity.this, "no salto previo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(penultimum__newLineIndex == previous_newLineIndex - 1){
            return;
        }

        //int indentation_start = previous_newLineIndex + 1;
        //StringBuilder indentation = new StringBuilder();

        //for (int i = indentation_start; i < cursor_position ; i++){
        //    char c = note_editable.charAt(i);
        //    //!!-- optimizar para evaluar vi~etas aparte de los espacios
        //    if(c == ' ' || c == '\t' || c == '-'){
        //        indentation.append(c);
        //    }else{
        //        break;
        //    }
        //}

        ////!!-- verifica cual metodo es el mas eficiente para saber si esta vacio
        //if(indentation.length() > 0){
        //    et_Note.append(indentation.toString());
        //}
    }


    private void Out_Of_Activity() {
        if (!now_is_something_writed) {
        //if (!now_is_something_writed && received_note_id != 0) {
            Delete_Note();
        } else {
            if (tv_Date.getText().toString().isEmpty()) {
                tv_Date.setVisibility(View.GONE);
            }
            Return_To_Memo_Board();
        }
    }


    private void Set_Icons_To_Empty_Note_Style() {
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
    }


    private void Bring_Note_From_DB(long received_note_id) {
        note = DB_N.getASpecificNote(received_note_id);
        et_Title.setText(note.title);
        et_Note.setText(note.note);
        tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
    }

    private boolean Verify_if_it_is_not_empty() {
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        return !_title.isEmpty() || !_note.isEmpty();
    }

    private void Verify_if_exist_something() {
        if (!Verify_if_it_is_not_empty()) {
            if (now_is_something_writed) {
                now_is_something_writed = false;        //si ahora no existe nada entonces:

                //if (note.note_id == 0) {
                fl_Change_Pin_Status.startAnimation(AnimationPinFade);
                fl_Change_Reminder_Status.startAnimation(AnimationPinFade);
                et_Note.startAnimation(AnimationNoteHintFading);
                if(note.note_id == 0){
                    fl_Delete.startAnimation(AnimationPinFade);
                }
               // }
            }
        } else {
            if (!now_is_something_writed) {
                now_is_something_writed = true;        //si ahora existe algo:
                fl_Change_Pin_Status.setAlpha(1f);
                fl_Change_Reminder_Status.setAlpha(1f);
                fl_Delete.setAlpha(1f);
                fl_Change_Pin_Status.startAnimation(AnimationPinAppear);
                fl_Change_Reminder_Status.startAnimation(AnimationPinAppear);
                fl_Delete.startAnimation(AnimationPinAppear);
                et_Note.clearAnimation();
            }
        }
    }

    private void Save_Note() {
        boolean save_Success = false;

        //!!---- corregir formato de currenttime, ya no es necesario agregar tanta informacion poo
        //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
        //System.currentTimeMillis() es la forma mas directa y eficiente de obtener un timestampMillis directo del SO
        long _current_time = System.currentTimeMillis();

        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        if (note.note_id == 0) {
            //-------Insert new note
            if (DB_N.Insert_Note(_current_time, _title, _note, note.pin, note.reminder, note.reminder_type, note.reminder_interval)) {
                Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
                save_Success = true;
                note.setNote_id(DB_N.Get_Last_RowId());
            }
        } else {
            //-------Modify the Note
            if (DB_N.Modify_Note(note.note_id, _current_time, _title, _note, note.pin, note.reminder, note.reminder_type, note.reminder_interval)) {
                Toast.makeText(MainActivity.this, "Modified", Toast.LENGTH_SHORT).show();
                save_Success = true;
            }
        }

        //-------Update the view of the date of last modification:
        if (save_Success) {
            //!!---Verificar, no se esta actualizando los datos recien agragados al objeto nota.
            change_in_note = false;
            note.date = _current_time;
            tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        }
    }

    private void Pin_Note() {

        boolean pin_modify_Success = false;

        note.setPin(note.getPin() ^ 1); //XOR Operator

        if (note.note_id != 0) {
            //-------Modify pin status of the Note
            Boolean Modify_Pin_Status = DB_N.Modify_Pin_Status(note.note_id, note.pin);
            if (Modify_Pin_Status) {
                Toast.makeText(MainActivity.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
                pin_modify_Success = true;
            } else {
                Log.d("Main Activity", "Not_Pin_Modified");
            }
        } else {
            //!!-- debe corregirse
            pin_modify_Success = true;
        }

        //-------Update the pin status:
        if (pin_modify_Success) {
            Set_Pin_Status();
        }
    }

    private void Set_Reminder_Note() {
        //if (!DB_N.Note_Exist(note.note_id)) {
        //    Log.d("from reminder", "NO EXISTE. sera salvada");
        //    Save_Note();
        //}else{
        //    Log.d("from reminder", "La nota existe");
        //}

        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, -1);
        reminder_PopUp.setListener(this);

        note.note = et_Note.getText().toString();
        note.title = et_Title.getText().toString();
        //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
        long _current_time = System.currentTimeMillis();
        note.date = _current_time;
        reminder_PopUp.show(layout_body_note, note);

    }

    private void Set_Pin_Status() {
        if (note.getPin() == 1) {
            fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ex_orange)));
        } else {
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

    private void Delete_Note() {
        boolean delete_Success = false;
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();
        //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
        long _current_time = System.currentTimeMillis();
        Boolean Insert_Note_In_TrashCan = false;

        if (!now_is_something_writed) { //if there_is_nothing__wrote > Send to trashcan what was in the database before save
            if (note.title != null || note.note != null) {
                Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,note.title,note.note, 20);
                Toast.makeText(MainActivity.this, "1-Insertado datos previous", Toast.LENGTH_SHORT).show();//si se elimina todo y luego se sale
            } else {
                //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
                Toast.makeText(MainActivity.this, "2- No hay nada que guardar ", Toast.LENGTH_SHORT).show();//si se utiliza reminder y luego se borra
                Insert_Note_In_TrashCan = true;
            }
        } else if (!change_in_note) {   //if there_is_something save in database > Send to trashcan what was in the database before save
            Insert_Note_In_TrashCan = getNoteInTrashCan(note.date,_title,_note, 20);
            Toast.makeText(MainActivity.this, "3- Sin cambios, save proyectado en edit.T ", Toast.LENGTH_SHORT).show();//si se borra intencionalmente
        } else {
            Insert_Note_In_TrashCan = getNoteInTrashCan(_current_time,_title,_note, 20);
            Toast.makeText(MainActivity.this, "4- Cambios realizados, salvando cambios ", Toast.LENGTH_SHORT).show();//salvado previo con cambios sin guardar
        }

        if (note.note_id != 0) {      //Delete and save in the trashcan
            Reminder_Notification.Cancel_Reminder_Alarm(layout_body_note, note.note_id);
        }
        if (Insert_Note_In_TrashCan) {
                delete_Success = true;
        }else{
            Log.d("Delete","not inserted en trashcan");
        }

        if (delete_Success) {
            et_Title.setText("");
            et_Note.setText("");
            //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
            Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
        }else{
            Log.d("Delete","not success");
        }
    }

    private Boolean getNoteInTrashCan(long date, String title, String _note, int expire_days) {

        if (note.note_id == 0) {
            //String _current_time = new SimpleDateFormat("dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
            long _current_time = System.currentTimeMillis();
            return DB_N.Insert_Note_Directly_in_Trash(_current_time,title,_note,note.pin,0,0,0);
        }
        return DB_N.Send_Note_To_Trash(note.note_id, date, title, _note, note.pin, 0, 0, 0, expire_days);
    }

    private void Date_Format_Change() {
        change_in_date = !change_in_date;
        //Se creo una refactorizacion que agrega una evaluacion adicional de 2 >  a 3 evaluaciones, en post de no duplicar codigo
        boolean note_exist = note.note_id != 0;
        tv_Date.setText(note_exist ? DoN.Set_Date_of_Note_In_Visualizer(note.date) : ""); ///Ternary Operator
        if (change_in_date) {
            if (note_exist) tv_Date.startAnimation(AnimationDate);
            tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
            tv_Info.startAnimation(AnimationInfo);
        } else {
            if (note_exist) tv_Date.startAnimation(AnimationDateInvert);
            tv_Info.startAnimation(AnimationInfoInvert);
        }
    }
    private void Date_Format_Change_BackUp_V7_1_13nov2025() {
        change_in_date = !change_in_date;
        //!!--optimizar
        if (change_in_date) {
            if (note.note_id != 0) {
                tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
                tv_Date.startAnimation(AnimationDate);
            } else {
                tv_Date.setText("");
            }
            tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
            tv_Info.startAnimation(AnimationInfo);
        } else {
            if (note.note_id != 0) {
                tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
                tv_Date.startAnimation(AnimationDateInvert);
            } else {
                tv_Date.setText("");
            }
            tv_Info.startAnimation(AnimationInfoInvert);
        }
    }

    public void Return_To_Memo_Board() {
        View view = this.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in, R.anim.return_activity_slide_right_out);
    }

    @Override
    public void OnValueSelected(int position, long alarm_Time) {
        note.setReminder(alarm_Time);
        Set_Reminder_Status();
        //!!---- Set animations
        if(note.note_id==0){
            note.setNote_id(DB_N.Get_Last_RowId());
        }
    }
}
