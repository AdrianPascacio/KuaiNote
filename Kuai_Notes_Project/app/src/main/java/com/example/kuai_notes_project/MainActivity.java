package com.example.kuai_notes_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.BuildCompat;
import androidx.core.view.SoftwareKeyboardControllerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

///290 V3 , 347 V4, 281 V5, 485 V6, 429 V7
public class MainActivity extends AppCompatActivity implements Reminder_PopUpWindow.OnValueSelectedListener {
    private DB_Notes DB_N;
    private DB_Trash_Can DB_TC;
    private TextView tv_Date, tv_Info;
    private EditText et_Title, et_Note ;
    private Note note = new Note();
    private String previous_date = null;
    private long reversed_reminder = 0;
    private String complete_current_time = null;
    private boolean change_in_note = false;
    private boolean change_in_date = false;
    private boolean now_is_something_writed = false;
    private FrameLayout fl_Change_Pin_Status,fl_Change_Reminder_Status,fl_Back ,fl_Delete;
    private FrameLayout fl_Change_Pin_Status_Ghost,fl_Change_Reminder_Status_Ghost,fl_Back_Ghost ,fl_Delete_Ghost;
    private Date_of_Note_in_Visualizer DoN;
    private View layout_date_and_info;
    private View layout_body_note;
    private Animation AnimationPin, AnimationDate , AnimationDateInvert, AnimationInfo, AnimationInfoInvert, AnimationPinAppear, AnimationPinFade;
    private Animation AnimationNoteAppear,AnimationTitleAppear, AnimationNoteHintFading;
    //private ScrollView sv_note;


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
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DB_N = new DB_Notes(this);
        DB_TC = new DB_Trash_Can(this);

        tv_Date = findViewById(R.id.Note_Time);
        tv_Info = findViewById(R.id.Note_Info);

        et_Title = findViewById(R.id.Title);
        et_Note = findViewById(R.id.Body_Note);

        layout_body_note = findViewById(R.id.Layout_Body_Note);

        fl_Change_Pin_Status = findViewById(R.id.FrameLayout_Change_Pin_Status);
        fl_Change_Reminder_Status = findViewById(R.id.FrameLayout_Change_Reminder_Status);
        fl_Back = findViewById(R.id.fl_Back);
        fl_Delete= findViewById(R.id.fl_Delete);

        fl_Change_Pin_Status_Ghost = findViewById(R.id.FrameLayout_Change_Pin_Status_Ghost);
        fl_Change_Reminder_Status_Ghost = findViewById(R.id.FrameLayout_Change_Reminder_Status_Ghost);
        fl_Back_Ghost = findViewById(R.id.fl_Back_Ghost);
        fl_Delete_Ghost = findViewById(R.id.fl_Delete_Ghost);


        DoN = new Date_of_Note_in_Visualizer();

        layout_date_and_info = findViewById(R.id.Layout_date_and_info);


        AnimationPin = AnimationUtils.loadAnimation(this,R.anim.pin_visualizer_change_status);
        AnimationDate = AnimationUtils.loadAnimation(this,R.anim.date_visualizer);
        AnimationDateInvert = AnimationUtils.loadAnimation(this,R.anim.date_visualizer_invert);
        AnimationInfo = AnimationUtils.loadAnimation(this,R.anim.info_visualizer);
        AnimationInfoInvert = AnimationUtils.loadAnimation(this,R.anim.info_visualizer_invert);
        AnimationPinAppear = AnimationUtils.loadAnimation(this,R.anim.appear_visualizer);
        AnimationPinFade = AnimationUtils.loadAnimation(this,R.anim.fade_visualizer);
        AnimationNoteAppear = AnimationUtils.loadAnimation(this,R.anim.note_appear_mainvisualizer);
        AnimationTitleAppear = AnimationUtils.loadAnimation(this,R.anim.title_appear_mainvisualizer);
        AnimationNoteHintFading = AnimationUtils.loadAnimation(this,R.anim.hint_note_fading_visualizer);

        reversed_reminder = getIntent().getLongExtra("send_reversed_alarm",0);
        if(reversed_reminder<0){
            Toast.makeText(this, "reveersed menor: " + reversed_reminder, Toast.LENGTH_SHORT).show();

            //test
            Note note = DB_N.getASpecificNote_ByReminder(reversed_reminder);
            if(reversed_reminder == note.reminder){
                Toast.makeText(this, "reveersed menor are equal: ", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "reveersed menor are diff: ", Toast.LENGTH_SHORT).show();

            }


           previous_date= DB_N.getASpecificNoteDate_ByReminder(reversed_reminder);
            Toast.makeText(this, "reveersed menor: " + reversed_reminder +" "+ previous_date, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "reveersed 0 looking previous send previous date: " , Toast.LENGTH_SHORT).show();
            previous_date = getIntent().getStringExtra("send_date_of_note");
        }


        complete_current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        if(previous_date!=null){
            now_is_something_writed = true;
            //---- Trae la nota si esta existe, luego coloca la informacion en los text view correspondientes
            Bring_Note_From_DB( previous_date );
            Set_Pin_Status();
            Set_Reminder_Status();
            et_Title.startAnimation(AnimationTitleAppear);
        }else {
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
                if(!now_is_something_writed && previous_date!=null){
                    Delete_Note();
                }else {
                    if(tv_Date.getText().toString().isEmpty()){
                        tv_Date.setVisibility(View.GONE);
                    }
                    Return_To_Memo_Board();
                }
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
                    //Pin_Note();
                    Set_Reminder_Note();
                    fl_Change_Reminder_Status.startAnimation(AnimationPin);
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
                    inputMethodManager.showSoftInput(et_Note, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        et_Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                Verify_if_exist_something();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        et_Note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                if(change_in_date){
                    tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
                }
                Verify_if_exist_something();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //--- Back button function:
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!now_is_something_writed && previous_date!=null){
                    Delete_Note();
                }else {
                    if(tv_Date.getText().toString().isEmpty()){
                        tv_Date.setVisibility(View.GONE);
                    }
                    Return_To_Memo_Board();
                }
            }
        });
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


    private void Bring_Note_From_DB(String date_of_note){
        note = DB_N.getASpecificNote(date_of_note);
        et_Title.setText(note.title);
        et_Note.setText(note.note);
        tv_Date.setText(DoN.Set_Date_of_Note(previous_date,complete_current_time));
        //!! Need to add the reminder column when reminder function is working
    }

    private boolean Verify_if_it_is_not_empty(){
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        return !_title.isEmpty() || !_note.isEmpty();
    }

    private void Verify_if_exist_something(){
        if (!Verify_if_it_is_not_empty()){
            if (now_is_something_writed){
                now_is_something_writed = false;        //si ahora no existe nada entonces:
                if(previous_date == null){
                    fl_Change_Pin_Status.startAnimation(AnimationPinFade);
                    fl_Delete.startAnimation(AnimationPinFade);
                    et_Note.startAnimation(AnimationNoteHintFading);
                }
            }
        }else{
            if (!now_is_something_writed){
                now_is_something_writed = true;        //si ahora existe algo:
                fl_Change_Pin_Status.setAlpha(1f);
                fl_Delete.setAlpha(1f);
                fl_Change_Pin_Status.startAnimation(AnimationPinAppear);
                fl_Delete.startAnimation(AnimationPinAppear);
                et_Note.clearAnimation();
            }
        }
    }

    private void Save_Note(){
        boolean save_Success = false;

        String _current_time = new SimpleDateFormat("yyMMddHHmmss dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());

        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        if(previous_date == null){
            //-------Insert new note
            //!!--- set reminder
            if (DB_N.Insert_Note(_current_time, _title, _note,note.pin,note.reminder,note.reminder_type,note.reminder_interval)) {
                Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
                save_Success = true;
            }
        }else{
            //-------Modify the Note
            if (DB_N.Modify_Note(previous_date, _current_time, _title, _note,note.pin,note.reminder,note.reminder_type,note.reminder_interval)) {
                Toast.makeText(MainActivity.this, "Modified", Toast.LENGTH_SHORT).show();
                save_Success = true;
            }
        }

        //-------Update the view of the date of last modification:
        if(save_Success) {
            change_in_note = false;
            previous_date = _current_time;
            tv_Date.setText(DoN.Set_Date_of_Note(previous_date,complete_current_time));
        }
    }

    private void Pin_Note(){

        boolean pin_modify_Success = false;

        note.setPin( note.getPin() ^ 1); //XOR Operator

        if(previous_date != null){
            //-------Modify pin status of the Note
            Boolean Modify_Pin_Status = DB_N.Modify_Pin_Status(previous_date,note.pin);
            if (Modify_Pin_Status) {
                Toast.makeText(MainActivity.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
                pin_modify_Success = true;
            } else {
                Log.d("Main Activity","Not_Pin_Modified");
            }
        }else{
            pin_modify_Success = true;
        }

        //-------Update the pin status:
        if(pin_modify_Success) {
            Set_Pin_Status();
        }
    }

    private void Set_Reminder_Note() {
        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, -1);
        reminder_PopUp.setListener(this);

        reminder_PopUp.show(layout_body_note, note);

    }

    private void Set_Pin_Status(){
        if(note.getPin() == 1){
            fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ex_orange)));
        }else{
            fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Neutral_gray_icon_note)));
        }
    }

    private void Set_Reminder_Status(){
        if(note.getReminder() > 0){
            fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue_x2)));
        }else{
            fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Neutral_gray_icon_note)));
        }
    }

    private void Delete_Note(){
        boolean delete_Success = false;
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();
        String _current_time = new SimpleDateFormat("yyMMddHHmmss dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());
        Boolean Insert_Note_In_TrashCan = false;

        if(previous_date != null){      //Delete and save in the trashcan

            //Toast.makeText(MainActivity.this, "se intenta grabar sin previo", Toast.LENGTH_SHORT).show();

            //if now_is_there_something_wrote > Send to trashcan what is wrote
            if (!now_is_something_writed) {
                //if there_is_nothing__wrote > Send to trashcan what was in the database before save
                //if
                if( note.title == null && note.note== null ){
                    //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
                    Toast.makeText(MainActivity.this, "se debe borrar del todo ", Toast.LENGTH_SHORT).show();
                    Insert_Note_In_TrashCan = true;
                }else{
                    Toast.makeText(MainActivity.this, "incertado vacio", Toast.LENGTH_SHORT).show();
                    //!!---reminder
                    Insert_Note_In_TrashCan = DB_TC.Insert_Note(previous_date,note.title,note.note,note.pin,0L,0,0,20);

                }
            } else if(!change_in_note){
                //!!---reminder
                Insert_Note_In_TrashCan = DB_TC.Insert_Note(previous_date,_title,_note,note.pin,0L,0,0,20);
            }else{
                //!!---reminder
                Insert_Note_In_TrashCan = DB_TC.Insert_Note(_current_time,_title,_note,note.pin,0L,0,0,20);
            }

            if(Insert_Note_In_TrashCan){
                long previous_reminder = note.reminder;
                Boolean Delete_Note_Checker = DB_N.Delete_Specific_Note(previous_date);
                if (Delete_Note_Checker) {
                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    delete_Success = true;
                    //Cancel_Reminder(previous_reminder);
                    Reminder_Notification.Cancel_Reminder_just_reminder(layout_body_note,previous_reminder);
                } else {
                    Toast.makeText(MainActivity.this, "NOT Deleted", Toast.LENGTH_SHORT).show();
                }
            }

        }else{      //Save the note directly in the TrashCan
            Toast.makeText(MainActivity.this, "pasa directo", Toast.LENGTH_SHORT).show();

            if(now_is_something_writed){
                //!!---reminder
                Insert_Note_In_TrashCan = DB_TC.Insert_Note(_current_time,_title,_note,note.pin,0L,0,0,20);
            }else{
                //!! se debe arreglar la razon por la que se indica como cierto es solo para que prosiga con la salida. de lo contrario se guardaria en pause lo que quede en title y note
                Insert_Note_In_TrashCan = true;
            }
            if (Insert_Note_In_TrashCan) {
                delete_Success = true;
            }
        }

        if(delete_Success) {
            et_Title.setText("");
            et_Note.setText("");
            //!!---Deberia crearse algunas animaciones para eliminar el title y la nota, al igual que el date y la info
            Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
        }
    }

    private void Date_Format_Change(){
        change_in_date = !change_in_date;
        if(change_in_date){
            if(previous_date != null){
                tv_Date.setText(DoN.Set_Date_of_Note(previous_date,complete_current_time));
                tv_Date.startAnimation(AnimationDate);
            }else{
                tv_Date.setText("");
            }
            tv_Info.setText(DoN.Set_Date_Note_Only_Information(et_Note.getText().toString()));
            tv_Info.startAnimation(AnimationInfo);
        }else{
            if(previous_date != null){
                tv_Date.setText(DoN.Set_Date_of_Note(previous_date,complete_current_time));
                tv_Date.startAnimation(AnimationDateInvert);
            }else{
                tv_Date.setText("");
            }
            tv_Info.startAnimation(AnimationInfoInvert);
        }
    }

    public void Return_To_Memo_Board(){
        View view = this.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
                finish();
        overridePendingTransition(R.anim.return_activity_slide_right_in,R.anim.return_activity_slide_right_out);
    }

    @Override
    public void OnValueSelected(int position, long alarm_Time) {
        note.setReminder(alarm_Time);
        Set_Reminder_Status();
        //!!---- Set animations
    }
}