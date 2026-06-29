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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;

///347 V4, 287 V5, 417 V6, 371 V7, 623 V0.7.3.1
public class Wasted_Note_Visualizer extends AppCompatActivity implements Reminder_PopUpWindow.OnValueSelectedListener,Reminder_PopUpWindow.PopupDismissListener,Note_Update_Listener{
    private DB_Notes DB_N;
    private TextView tv_Date ,tv_Info;
    private EditText et_Title, et_Note ;
    private Note note = new Note();
    private int expire_days = 0;
    private long received_note_id = 0;
    private boolean change_in_note = false;
    private boolean note_recycled = false;
    private boolean show_note_info = false;
    private FrameLayout fl_back, fl_Change_Recycler_Status, fl_Change_Pin_Status, fl_Change_Reminder_Status;
    private FrameLayout fl_Change_Recycler_Status_Ghost, fl_Back_Ghost, fl_Delete_Ghost, fl_Change_Pin_Status_Ghost, fl_Change_Reminder_Status_Ghost;
    private View layout_date_and_info, layout_body_note, wasted_note_global, layout_dim;
    private Date_of_Note DoN;
    private Animation AnimationDate , AnimationDateInvert, AnimationInfo, AnimationInfoInvert, AnimationPin, AnimationReminder, AnimationRecycler;
    private Animation AnimationFade , AnimationAppear;
    private Animation AnimationLayoutDimAppear, AnimationLayoutDimDisappear_Normal,AnimationLayoutDimDisappear_Setter,AnimationLayoutDimDisappear_Cancel;
    int previous_note_size = -1;
    private char last_deleted_char = '0';
    Indent_Replicator indentReplicator;

    @Override
    protected void onPause(){
        super.onPause();
        if (Note_is_not_empty() && change_in_note) {
            Save_Note();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (Note_is_not_empty() && change_in_note) {
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
        getWindow().setStatusBarColor(getResources().getColor(R.color.Trashcan_status_bar));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.Trashcan_navigation_bar));

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

        received_note_id = getIntent().getLongExtra("send_note_id",0);

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

        AnimationLayoutDimAppear = AnimationUtils.loadAnimation(this, R.anim.layout_dim_appear);
        AnimationLayoutDimDisappear_Normal = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_normal);
        AnimationLayoutDimDisappear_Setter = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);
        AnimationLayoutDimDisappear_Cancel = AnimationUtils.loadAnimation(this, R.anim.layout_dim_disappear_setter);

        layout_date_and_info = findViewById(R.id.Layout_date_and_info);

        layout_dim = findViewById(R.id.layout_dim_wastedNoteVisualizer);
        indentReplicator = new Indent_Replicator(this);

        Initialize_Received_Note( received_note_id );

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
                if(!change_in_note && !note_recycled){
                    Change_to_Recycled_View();
                }
                change_in_note = true;
                if(show_note_info){
                    tv_Info.setText(DoN.Set_Date_Note_Only_Information( et_Note.getText().toString()));
                }

                int _cursor_position = et_Note.getSelectionStart();
                indentReplicator.ejecutar_Accion(s,previous_note_size,_cursor_position,last_deleted_char);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        fl_Change_Recycler_Status_Ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecycleItem();
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
                    layout_dim.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_sand_light_trans)));
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
                if(!Note_is_not_empty()){
                    Delete_Note();
                }else{
                    Return_To_Memo_Board();
                }
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!Note_is_not_empty()){
                    Delete_Note();
                }else{
                    Return_To_Memo_Board();
                }
            }
        });
    }

    private void Initialize_Received_Note(long received_note_id){
        if(!note_recycled){
            note = DB_N.getASpecificNote_In_Trash(received_note_id);
        }else{
            note = DB_N.getASpecificNote(received_note_id);
        }
        et_Title.setText(note.title);
        et_Note.setText(note.note);
        tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        expire_days = DB_N.get_expire_Day(received_note_id);

        Change_Pin_Status_Style();
    }

    private void Change_to_Recycled_View(){
        wasted_note_global.setBackgroundColor(ContextCompat.getColor(this, R.color.note_visualizer_main_background));
        //wasted_note_global.setBackgroundColor(Color.parseColor("#FFF9EF"));

        getWindow().setStatusBarColor(getResources().getColor(R.color.Light_Status_Bar_Color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.Light_Navigation_Bar_Color));

        fl_Change_Recycler_Status.startAnimation(AnimationRecycler);
        //!!-- set proper animation to fade recycler status
        fl_Change_Recycler_Status_Ghost.setVisibility(View.GONE);

        fl_Change_Pin_Status_Ghost.setVisibility(View.VISIBLE);
        fl_Change_Pin_Status.setVisibility(View.VISIBLE);
        fl_Change_Pin_Status.startAnimation(AnimationInfo);

        fl_Change_Reminder_Status_Ghost.setVisibility(View.VISIBLE);
        fl_Change_Reminder_Status.setVisibility(View.VISIBLE);
        fl_Change_Reminder_Status.startAnimation(AnimationInfo);

        //fl_back.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A4A4A4")));
        //fl_back.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.trashcan_item_visualizer_main_background)));
        //et_Title.setTextColor(Color.parseColor("#1B1919"));
        //tv_Info.setTextColor(Color.parseColor("#A0A0A0"));
        //tv_Date.setTextColor(Color.parseColor("#A0A0A0"));
        //et_Title.setTextColor(Color.parseColor("#1B1919"));
        et_Title.setTextColor(ContextCompat.getColor(this, R.color.note_visualizer_wasted_note));
        tv_Info.setTextColor(ContextCompat.getColor(this, R.color.note_visualizer_information_text_color));
        tv_Date.setTextColor(ContextCompat.getColor(this, R.color.note_visualizer_information_text_color));
        fl_back.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.Neutral_gray_icon_note))); ///Ternary Operator
    }
    @Override
    public void Update_Note_Content( int indent_type, char last_deleted_char, int previous_note_size, int cursor_selection) {
        this.previous_note_size = previous_note_size;
        this.last_deleted_char = last_deleted_char;
    }

    private boolean Note_is_not_empty(){
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        return !_title.isEmpty() || !_note.isEmpty();
    }
    public void RecycleItem() {
        if(DB_N.Recycle_Note(note.note_id)) {
            fl_Change_Recycler_Status.startAnimation(AnimationRecycler);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.return_activity_slide_right_in_after_recycle,R.anim.return_activity_slide_right_out_after_recycle);
                }
            }, 1150); // Realiza accion luego de 300 milisegundos
        }
    }

    /// Pin Note
    private void Pin_Note(){
        if (!DB_N.Note_Exist(note.note_id)) {
            Save_Note();
        }
        //!!Verificar. ya no le encuentro sentido

        //note.setPin(note.getPin() ^ 1); ///XOR Operator
        note.setPin(!note.getPin());

        if(note.note_id == 0){
            Change_Pin_Status_Style();
            return;
        }

        if (DB_N.Modify_Pin_Status(note.note_id, note.pin)) {
            Toast.makeText(Wasted_Note_Visualizer.this, "Modified_Pin_Status", Toast.LENGTH_SHORT).show();
            Change_Pin_Status_Style();
        } else {
            Log.d("Main Activity", "Not_Pin_Modified");
        }
    }
    private void Change_Pin_Status_Style(){
        fl_Change_Pin_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                note.getPin() == true ? R.color.ex_orange :R.color.Neutral_gray_icon_note)));  ///Ternary Operator
    }

    /// Reminder Note
    private void Set_Reminder_Note() {
        if (!DB_N.Note_Exist(note.note_id)) {
            Save_Note();
        }

        Reminder_PopUpWindow reminder_PopUp = new Reminder_PopUpWindow(this, -1);
        reminder_PopUp.setListener(this);
        reminder_PopUp.setListener_dismiss(this);
        reminder_PopUp.show(layout_body_note, note);
    }
    private void Change_Reminder_Status_Style() {
        fl_Change_Reminder_Status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                note.getReminder() > 0 ? R.color.light_blue_x2 :R.color.Neutral_gray_icon_note)));  ///Ternary Operator
    }
    @Override
    public void OnValueSelected(int position, long alarm_Time, int reminder_type, int reminder_interval) {
        note.setReminder(alarm_Time);
        Change_Reminder_Status_Style();
    }
    @Override
    public void onPopupClosed(int salida, int position) {
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

    private void Save_Note(){
        long _current_time = System.currentTimeMillis();

        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        note_recycled = !note_recycled ? DB_N.Recycle_Note(note.note_id) : false;   ///Ternary Operator

        if(DB_N.Modify_Note(note.note_id,_current_time, _title, _note,note.pin,note.reminder,note.reminder_type,note.reminder_interval)) {
            change_in_note = false;
            note.date = _current_time;
            tv_Date.setText(DoN.Set_Date_of_Note_In_Visualizer(note.date));
        }
    }

    private void Delete_Note(){
        if(DB_N.Delete_Hard_Specific_Note(note.note_id)) {
            Toast.makeText(Wasted_Note_Visualizer.this, "Note Burned.", Toast.LENGTH_SHORT).show();
            Reminder_Notification.Cancel_Reminder_Alarm(layout_body_note, note.note_id,0, note.reminder);
            Return_To_Memo_Board();
        }
    }

    private void Date_Format_Change(){
            show_note_info = !show_note_info;
            if (show_note_info) {
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
}