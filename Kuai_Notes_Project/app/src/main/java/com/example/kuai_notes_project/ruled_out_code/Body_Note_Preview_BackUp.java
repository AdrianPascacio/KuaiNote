package com.example.kuai_notes_project.ruled_out_code;

import java.util.ArrayList;

public class Body_Note_Preview_BackUp {
    public String Set_Body_Note_Preview(String title, String note, int max_complete_note_tolerance, int max_preview_note, int bonus_for_lack_of_title, int line_jump_limit){
        /// Original values:    int max_complete_note_tolerance = 60;    int max_preview_note = 55;  int lack_of_title_bonus = 0;    int line_jump_limit = 2;

        boolean title_inexistent = title.isEmpty();
        int size_of_note = note.length() ;

        if(title_inexistent) bonus_for_lack_of_title = 10;

        //----Previsualizacion por salto de linea:
        int line_jump = note.indexOf('\n',note.indexOf('\n',note.indexOf('\n')));

        if(note.indexOf('\n') > 0 ) line_jump = note.indexOf('\n')  ;

        //---This can be a loop to set a Line_Jump_limit to the value line_jump:
        for(int i = line_jump_limit -1;i>0;i--){
            if(note.indexOf('\n',line_jump+1) > 0 ) line_jump = note.indexOf('\n',line_jump+1)  ;
        }
        if(title_inexistent) {
            if(note.indexOf('\n',line_jump +1) > 0 ) line_jump = note.indexOf('\n',line_jump+1)  ;
        }
        if(line_jump>-1){

            if(line_jump < max_complete_note_tolerance + bonus_for_lack_of_title){

                return (note.substring(0,line_jump)+"...").trim();
            }
        }

        //----Previsualizacion si la nota completa cabe dentro del maximo permitido:
        if(size_of_note <= max_complete_note_tolerance + bonus_for_lack_of_title){
            return (note).trim();
        }else{

            //----Previsualizacion recortada por las palabras que quepan en el maximo de caracteres permitidos:
            int last_character_index = 0;

            int preview_limit = max_preview_note + last_character_index;
            for (int i = 0; i <= preview_limit; i++){

                if (String.valueOf(note.charAt(i)).equals(" ")){
                    if(i < preview_limit){
                        last_character_index = i;
                    }
                }
            }
            return (note.substring(0,last_character_index)+"...").trim();
        }

    }
    public String Set_Body_Note_Preview_BackUp_27nov2025(String title, String note, int max_complete_note_tolerance, int max_preview_note, int bonus_for_lack_of_title, int line_jump_limit, int aux_int){
        /// Original values:    int max_complete_note_tolerance = 60;    int max_preview_note = 55;  int lack_of_title_bonus = 0;    int line_jump_limit = 2;

        boolean title_inexistent = title.isEmpty();
        int note_size = note.length() ;

        if(title_inexistent) bonus_for_lack_of_title = 10;
        int max_character_with_bonus = max_complete_note_tolerance + bonus_for_lack_of_title;

        //----Previsualizacion por salto de linea:
        int line_jump = Line_Jumps_Within_Limit_BackUp_27nov2025(note,line_jump_limit,title_inexistent,max_character_with_bonus);

        //----Previsualizacion si la nota completa cabe dentro del maximo permitido:
        boolean note_smaller_than_max_tolerance = note_size <= max_character_with_bonus;


        if(line_jump != -1){
            int unsupported_line_jump = UnSupported_Line_Jumps_BackUp_27nov2025(note.substring(line_jump+1),30,line_jump);
            if(unsupported_line_jump != -1){
                int limits_words_after_jump = Limits_of_Words_After_jumps_BackUp_27nov2025(note, line_jump, unsupported_line_jump);
                return (note.substring(0,limits_words_after_jump)+"...").trim();
            }else{
                if(note_size < (30 /* -> limite maximo de linea*/ + line_jump) ){
                    return (note).trim();
                }else{
                    return (note).substring(0, line_jump + 30)+"...".trim();
                }
            }
        }else if(note_smaller_than_max_tolerance){
            return (note).trim();
        }else{
            return (note).substring(0, max_character_with_bonus)+"...".trim();
        }
        //!!----- puede mejorar si se ingresa la opcion de extender el maximo soportado y agregar una linea mas de previsualizacion si con esto se puede ver la nota completa

    }

    private int Line_Jumps_Within_Limit_BackUp_27nov2025(String note, int line_jump_limit, boolean title_inexistent, int max_character_with_bonus){
        if (title_inexistent) line_jump_limit = line_jump_limit + 1;

        int _current_jump_index = note.indexOf('\n');
        if (_current_jump_index == -1 || _current_jump_index > max_character_with_bonus){
            return -1;
        }

        ArrayList<Integer> jumps_list = new ArrayList<>();
        jumps_list.add(_current_jump_index);
        _current_jump_index = -1;

        for(int i = 0 ;i<line_jump_limit - 1;i++){ // menos uno ya que el jumps limit debe devolver solo el caracter de salto "\n" antes de la ultima linea deseada
            _current_jump_index = note.indexOf('\n',_current_jump_index+1);
            if(_current_jump_index <= max_character_with_bonus && _current_jump_index > -1){
                jumps_list.add(_current_jump_index);
            }else{
                return jumps_list.get(i);
            }
        }
        return jumps_list.get(jumps_list.size()-1);
    }
    private int Limits_of_Words_After_jumps_BackUp_27nov2025(String note, int first_limit, int last_limit){
        //----Previsualizacion recortada por las palabras que quepan en el maximo de caracteres permitidos:
        for (int i = last_limit; i >= first_limit ; i--){

            if (!String.valueOf(note.charAt(i)).equals(" ")) {
                return  i;
            }
        }
        return -1;
    }
    private int UnSupported_Line_Jumps_BackUp_27nov2025 (String note, int max_character_with_bonus, int line_jump){
        int _current_jump_index = note.indexOf('\n');
        if (_current_jump_index == -1 || _current_jump_index > max_character_with_bonus){
            return -1;
        }else{
            _current_jump_index += line_jump+1;
            return _current_jump_index ;
        }
    }
}
