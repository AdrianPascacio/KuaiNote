package com.example.kuai_notes_project;

import android.util.Log;

/// 62 V4
public class Body_Note_Preview {
    public String Set_Body_Note_Preview(String title, String note, int max_complete_note_tolerance, int max_preview_note, int lack_of_title_bonus, int line_jump_limit){
        //Introduce un poco mas de espacio si con esto se visualiza la nota completa en la seccion de previsualizacoin
        /// Original values
        ///int max_complete_note_tolerance = 60;
        ///int max_preview_note = 55;
        ///int lack_of_title_bonus = 0;
        ///int line_jump_limit = 2;

        boolean title_inexistent = title.isEmpty();
        int size_of_note= note.length() ;

        if(title_inexistent) lack_of_title_bonus = 10;

        //----Previsualizacion por salto de linea:
        int line_jump= note.indexOf('\n',note.indexOf('\n',note.indexOf('\n')));

        if(note.indexOf('\n') > 0 ) line_jump = note.indexOf('\n')  ;

        //---This can be a loop to set a Line_Jump_limit to the value line_jump:
        for(int i = line_jump_limit -1;i>0;i--){
            if(note.indexOf('\n',line_jump+1) > 0 ) line_jump = note.indexOf('\n',line_jump+1)  ;
        }
        if(title_inexistent) {
            if(note.indexOf('\n',line_jump +1) > 0 ) line_jump = note.indexOf('\n',line_jump+1)  ;
        }
        if(line_jump>-1){

            if(line_jump < max_complete_note_tolerance + lack_of_title_bonus){

                return (note.substring(0,line_jump)+"...").trim();
            }
        }
        //----Previsualizacion si la nota completa cabe dentro del maximo permitido:
        if(size_of_note <= max_complete_note_tolerance + lack_of_title_bonus){
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

}
