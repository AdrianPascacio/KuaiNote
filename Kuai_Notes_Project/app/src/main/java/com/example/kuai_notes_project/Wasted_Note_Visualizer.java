package com.example.kuai_notes_project;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

///347 V4
public class Wasted_Note_Visualizer extends AppCompatActivity {
    private DB_Notes DB_N;
    private DB_Trash_Can DB_TC;
    //String time, title, note;
    private TextView tv_Date ;
    private EditText et_Title, et_Note ;
    private Note note = new Note();
    private int expire_days = 0;

    private String previous_date = null;
    private String complete_current_time = null, short_current_time = null;
    private boolean change_in_note = false;
    private boolean change_in_date = false;
    private FrameLayout fl_Change_Pin_Status;

    @Override
    protected void onPause(){
        super.onPause();
        //Verify if title or Note have been changed and have something to save
        ///if (Verify_if_it_is_not_empty() && change_in_note) {
        ///    Save_Note();
        ///}
    }
    @Override
    protected void onResume(){
        super.onResume();
        //Verify if title or Note have been changed and have something to save
        ///if (Verify_if_it_is_not_empty() && change_in_note) {
        ///    Save_Note();
        ///}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wasted_note_visualizer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_brown_natural));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.light_brown_natural_trans));

        DB_N = new DB_Notes(this);
        DB_TC = new DB_Trash_Can(this);

        tv_Date = findViewById(R.id.Note_Time);

        et_Title = findViewById(R.id.Title);
        et_Note = findViewById(R.id.Body_Note);


        FrameLayout fl_Back = findViewById(R.id.fl_Back);
        FrameLayout fl_Delete = findViewById(R.id.fl_Delete);

        fl_Change_Pin_Status = findViewById(R.id.FrameLayout_Change_Pin_Status);

        previous_date = getIntent().getStringExtra("send_date_of_note");

        complete_current_time = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());


        if(previous_date!=null){
            //Trae la nota si esta existe, luego coloca la informacion en los text view correspondientes
            Bring_Note_From_DB( previous_date );
            Set_Pin_Status();
            Set_Date_of_Note();
        }else {
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
        }

        fl_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_To_Memo_Board();
                //finish();
            }
        });
        fl_Change_Pin_Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecycleItem();
            }
        });
        tv_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date_Format_Change();
            }
        });
        fl_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Note();
            }
        });
        //!!-----hacer la misma funcion para el boton de atras del control de navegacion
        et_Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        et_Note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                change_in_note = true;
                if(change_in_date){
                    Set_Date_of_Note();
                    Set_Date_Note_Information();
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }
    private void Bring_Note_From_DB(String date_of_note){
        note = DB_TC.getASpecificNote(date_of_note);
        et_Title.setText(note.title);
        et_Note.setText(note.note);
        expire_days = DB_TC.get_expire_Day(date_of_note);
        //!! Need to add the reminder column when reminder function is working
    }

    private boolean Verify_if_it_is_not_empty(){
        //Verify if title or Note have been changed
        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();

        return !_title.isEmpty() || !_note.isEmpty();
    }
    private void Save_Note(){
        boolean save_Success = false;
        ////LocalDate today = null;
        ////today = LocalDate.now();
        ////DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy hh:mm:ss");
        ////String _current_time = today.format(formatter);

        //String _current_time = new SimpleDateFormat("dd-MMMM-yyyy hh:mmaSSS", Locale.getDefault()).format(new Date());
        //String _current_time = new SimpleDateFormat("yyMMdd MMMM yyyy hh:mmaSSS", Locale.getDefault()).format(new Date());
        String _current_time = new SimpleDateFormat("yyMMddHHmmss dd MMMM yyyy hh:mma", Locale.getDefault()).format(new Date());

        String _title = et_Title.getText().toString();
        String _note = et_Note.getText().toString();


        if(previous_date == null){

            //there is no previous date -> The Note Is New:
            //-------Insert new note
            Boolean Insert_Note_Checker = DB_N.Insert_Note(_current_time, _title, _note,note.pin);
            if (Insert_Note_Checker) {
                save_Success = true;
            } else {
            }

        }else{
            //if there is a previous date -> have to modify the note:
            //-------Modify the Note
            Boolean Modify_Note_Checker = DB_N.Modify_Note(previous_date, _current_time, _title, _note,note.pin);
            if (Modify_Note_Checker) {
                save_Success = true;
            } else {
                Toast.makeText(Wasted_Note_Visualizer.this, "Not_Modified", Toast.LENGTH_SHORT).show();
            }

        }

        //-------Update the view of the date of last modification:
        if(save_Success) {
            previous_date = _current_time;
            ////tv_Date.setText(previous_date);
            Set_Date_of_Note();
        }

    }
    private void Pin_Note(){

        boolean pin_modify_Success = false;

        if(note.pin == 0 ){
            note.setPin(1);
        }else{
            note.setPin(0);
        }


        //if there is a previous date -> have to modify the note:
        //-------Modify pin status of the Note
        Boolean Modify_Pin_Status = DB_N.Modify_Pin_Status(previous_date,note.pin);
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
    private void Set_Pin_Status(){
        fl_Change_Pin_Status.setVisibility(View.VISIBLE);
    }
    private void Delete_Note(){
        boolean delete_Success = false;

        if(previous_date != null){
            //if there is a previous date -> have to delete the note:
            //-------Delete the Note

            if(DB_TC.Insert_Note(note.date,note.title,note.note,note.pin,20)){
                Boolean Delete_Note_Checker = DB_N.Delete_Specific_Note(previous_date);
                if (Delete_Note_Checker) {
                    Toast.makeText(Wasted_Note_Visualizer.this, "Deleted", Toast.LENGTH_SHORT).show();
                    delete_Success = true;
                } else {
                    Toast.makeText(Wasted_Note_Visualizer.this, "NOT Deleted", Toast.LENGTH_SHORT).show();
                }
            }

        }else{
            //The note was not saved in the data base before, there is not need to delete
            Toast.makeText(Wasted_Note_Visualizer.this, "Note not deleted.", Toast.LENGTH_SHORT).show();
        }
        //-------Finish activity if Delete of note is complete
        if(delete_Success) {
            Return_To_Memo_Board(); //is a method with the finish() method inside, but is there to add animations later
        }

    }
    private void Set_Date_of_Note(){
        //-------Set Date according to format
        int size_of_date = previous_date.length() ;
        String date_dMy = previous_date.substring(13,size_of_date - 8);
        String date_hm = previous_date.substring(size_of_date - 6);
        Log.d("WastedActivity", "Set_Date_Note : "+complete_current_time + " ::: " + date_dMy);

        //If current date is equal to date of last modification then is "Today"
        if(complete_current_time.equals(date_dMy)){
            if(change_in_date){
                tv_Date.setText("Today"+"    " + complete_current_time + "   "+date_hm);
            }else{
                short_current_time = complete_current_time.substring(0,6);
                tv_Date.setText("Today"+"    " + short_current_time + "   "+date_hm);
            }

            //!!----If current date is equal Yesteday then "yesterday" (Verificar si el costo es muy elevado
        }else{
            //Just complete the date and hour in the format "dd MMMM yyyy    hh:mma"
            if(change_in_date){
                tv_Date.setText(date_dMy+"   "+date_hm);
            }else{
                tv_Date.setText(date_dMy+"   "+date_hm);
            }
        }
    }
    private void Set_Date_Note_Information(){
        //!!-------Se debe optimizar, se esta ejecutando todo el metodo Set_Date_of_Note cada vez que se actualiza el texto
        tv_Date.setText(tv_Date.getText()+ "\n " + "Character" + ": " + et_Note.length() + "   |   " + "Words" + ": " + Word_Counter(et_Note.getText().toString())) ;
    }
    private int Word_Counter(String text){
        if (text == null || text.trim().isEmpty()){
            return 0;
        }
        String [] words = text.trim().split("\\s+");

        return words.length;
    }
    private void Date_Format_Change(){
        change_in_date = !change_in_date;
        if(change_in_date){
            tv_Date.setTextSize(14);
            Set_Date_of_Note();
            Set_Date_Note_Information();
        }else{
            tv_Date.setTextSize(13);
            Set_Date_of_Note();
        }
    }

    public void Return_To_Memo_Board(){
        finish();
    }
    public void RecycleItem() {
        //----Remove Note from Data Base:
        if(DB_N.Insert_Note(note.date,note.title,note.note,note.pin)){
            if(DB_TC.Delete_Specific_Note(note.date)) {
                //!!---podria volver al menu anterior
                finish();
            }

        }
    }



}