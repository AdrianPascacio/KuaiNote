package com.example.kuai_notes_project;

import android.text.Editable;
import android.util.Log;

public class Indent_Replicator {
    private Note_Update_Listener listener;

    private int indent_type, cursor_selection = 0, previous_note_size, cursor_position;
    private char last_deleted_char;
    private Editable note_editable;

    public Indent_Replicator(Note_Update_Listener listener){
        this.listener = listener;
    }
    public void ejecutar_Accion(Editable note_editable, int previous_note_size, int cursor_position, char last_deleted_char){
        this.note_editable = note_editable;
        this.previous_note_size = previous_note_size;
        this.cursor_position = cursor_position;
        this.last_deleted_char = last_deleted_char;

        indent_type = 0;
        Set_Indent_Replicator();
        this.previous_note_size = note_editable.length();
        listener.Update_Note_Content(indent_type,this.last_deleted_char,this.previous_note_size,cursor_selection);
    }


    private void Set_Indent_Replicator() {
        if(previous_note_size > note_editable.length()){
            Indent_Suppressor();
            return;
        }
        Indent_Maker();
    }
    private void Indent_Maker() {
        last_deleted_char = '0';

        if(previous_note_size == note_editable.length()){
            Log.d("Indent_Replicator","  size previo igual al actual. SALIR");
            return;
        }

        Log.d("Indent_Replicator","   cursorPosition:"+ cursor_position+
                "        previo_size: "+ previous_note_size + "   size: "+note_editable.length());


        int jump_before_cursor = Editable_last_IndexOf(note_editable,'\n',cursor_position -1);
        int penultimum__newLineIndex = Editable_last_IndexOf(note_editable,'\n',cursor_position -2);
        Log.d("Indent_Replicator","  indx_salto_prev_cursor: "+jump_before_cursor+"   indx_penultimum_salto:"+ penultimum__newLineIndex);


        if (jump_before_cursor == -1){
            Log.d("Indent_Replicator","  no se encuentra salto previo.");
            return;
        }

        if (jump_before_cursor != (cursor_position - 1)){
            Log.d("Indent_Replicator","  no existe salto justo previo al cursor ");
            return;
        }

        if(penultimum__newLineIndex == jump_before_cursor - 1){
            Log.d("Indent_Replicator","  Existen dos saltos seguidos. SALIR");
            return;
        }

        Log.d("Indent_Replicator","     Existe salto justo antes de cursor: GENERAR INDENTADO ");

        int indentation_start = penultimum__newLineIndex + 1;
        StringBuilder indentation = new StringBuilder();

        for (int i = indentation_start; i < cursor_position ; i++){
            char c = note_editable.charAt(i);
            //!!-- optimizar para evaluar vi~etas aparte de los espacios
            if(Character_Equal_To_Indent(c)){
                indentation.append(c);
            }else{
                break;
            }
        }

        Log.d("Indent_Replicator","  Indentation:(" + indentation + ")");

        if(indentation.length() > 0){
            note_editable.insert(cursor_position, indentation);
            cursor_selection = indentation.length() + cursor_position;
        }
        last_deleted_char = ' ';

        indent_type = 1;
    }

    private void Indent_Suppressor() {

        if (cursor_position <= 1){ //debe ser mayor que uno
            Log.d("Indent_Replicator","  Cursor no es mayor que uno. SALIR");
            return;
        }

        char c = note_editable.charAt(cursor_position - 1);

        if (!Character_Equal_To_Indent(last_deleted_char)) {
            Log.d("Indent_Replicator","  el caracter borrado en la vez anterior no era indentado. SALIR");
            last_deleted_char = c;
            return;
        }

        last_deleted_char = c;

        if (!Character_Equal_To_Indent(c)) {
            Log.d("Indent_Replicator","  no hay indentado previo al cursor.");
            return;
        }
        int jump_before_cursor = Editable_last_IndexOf(note_editable,'\n',cursor_position -1 );

        if (jump_before_cursor == -1){
            Log.d("Indent_Replicator","  no se encuentra salto previo. SALIR");
            return;
        }

        Log.d("Indent_Replicator","  caracter_current:"+c+ "    caracter_previo:"+last_deleted_char);

        int indent_length = 0;

        for (int i = jump_before_cursor + 1; i < cursor_position ; i++){
            c = note_editable.charAt(i);
            if (Character_Equal_To_Indent(c)) {
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
        Log.d("Indent_Replicator","     Existe indentado antes de cursor: ELIMINAR INDENTADO ");

        indent_type = 2;
    }

    private int Editable_last_IndexOf(CharSequence charSequence, char c, int fromIndex){
        if(fromIndex >= charSequence.length()){
            fromIndex = charSequence.length() - 1;
        }
        for (int i = fromIndex; i >= 0; i --){
            if (charSequence.charAt(i) == c){
                return i;
            }
        }
        return -1;
    }


    private boolean Character_Equal_To_Indent(char c) {
        return (    c == ' '
                ||  c == '\t'
                ||  c == '-'
                ||  c == '*' );
    }

}