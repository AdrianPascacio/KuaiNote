package com.example.kuai_notes_project;

import android.util.Log;

import java.util.ArrayList;

/// 62 V4, 56 V5
public class Body_Note_Preview {
    public String Set_Body_Note_Preview(String title, String note, int max_complete_note_tolerance, int max_preview_note, int bonus_for_lack_of_title, int line_jump_limit, int aux_int, int max_char_per_line){
        /// Original values:    int max_complete_note_tolerance = 60;    int max_preview_note = 55;  int lack_of_title_bonus = 0;    int line_jump_limit = 2;

        boolean title_inexistent = title.isEmpty();
        int note_size = note.length() ;

        if(title_inexistent) bonus_for_lack_of_title = 10;
        int max_character_with_bonus = max_complete_note_tolerance + bonus_for_lack_of_title;

        //----Previsualizacion por salto de linea:
        int line_jump_index = Line_Jumps_Within_Limit(note,line_jump_limit,title_inexistent,max_character_with_bonus,aux_int);

        //----Previsualizacion si la nota completa cabe dentro del maximo permitido:
        boolean note_smaller_than_max_tolerance = note_size <= max_character_with_bonus;

        if(line_jump_index != -1){
            int unsupported_line_jump = UnSupported_Line_Jumps(note,max_char_per_line,line_jump_index);
            if(unsupported_line_jump != -1){
                int limits_words_after_jump = Limits_of_Words_After_jumps(note, line_jump_index, unsupported_line_jump);
                return (note.substring(0,limits_words_after_jump)+"...").trim();
            }else{
                if(note_size < (max_char_per_line /* -> limite maximo de linea*/ + line_jump_index) ){
                    return (note).trim();
                }else{
                    return (note).substring(0, line_jump_index + max_char_per_line)+"...".trim();
                }
            }
        }else if(note_smaller_than_max_tolerance){
            return (note).trim();
        }else{
            return (note).substring(0, max_character_with_bonus)+"...".trim();
        }
    }

    private int Line_Jumps_Within_Limit(String note, int line_jump_limit, boolean title_inexistent, int max_character_with_bonus, int jumps_bonus_for_title_inexistent){
        if (title_inexistent) line_jump_limit += jumps_bonus_for_title_inexistent;

        int _current_jump_index = note.indexOf('\n');
        if (_current_jump_index == -1 || _current_jump_index > max_character_with_bonus){
            return -1;
        }

        int next_return;
        for(int i = 0 ;i < line_jump_limit - 2; i++ ){ // menos dos ya que el primer jump esta calculado arriba y el segundo jumps es el caracter de salto "\n" antes de la ultima linea deseada
            next_return = note.indexOf('\n',_current_jump_index+1) ;

            if(next_return <= max_character_with_bonus && next_return > -1){
                _current_jump_index = next_return;
            }else{
                return _current_jump_index;
            }
        }
        return _current_jump_index;
    }
    private int UnSupported_Line_Jumps (String note, int max_character_with_bonus, int line_jump_index){
        int _current_jump_index = note.indexOf('\n',line_jump_index +1);
        if (_current_jump_index == -1 || _current_jump_index > ( max_character_with_bonus + line_jump_index + 1)){
            return -1;
        }else{
            //_current_jump_index += line_jump+1;
            return _current_jump_index ;
        }
    }
    private int Limits_of_Words_After_jumps(String note, int first_limit, int last_limit){
        //----Previsualizacion recortada por las palabras que quepan en el maximo de caracteres permitidos:
        for (int i = last_limit; i >= first_limit ; i--){

            if (note.charAt(i) !=' ') {
                return  i;
            }
        }
        return -1;
    }
}