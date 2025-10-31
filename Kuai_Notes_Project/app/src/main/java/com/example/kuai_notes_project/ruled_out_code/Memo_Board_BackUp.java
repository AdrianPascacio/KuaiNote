package com.example.kuai_notes_project.ruled_out_code;

import java.util.Objects;

public class Memo_Board_BackUp {


    private void Set_Unselected_List_V1_BackUp(int position) {
        //!!--- if the selected item is selected again, if you select another one, the first selected item get changed with no need
        //previous_selected_list.add(0, position);


        //if(previous_selected_list.size()==2){       //--size 2 : current and just unselected:
        //    unselected_list.set(previous_selected_list.get(0),false);
        //    unselected_list.set(previous_selected_list.get(1),true);
        //    adapter.notifyItemChanged(previous_selected_list.get(0));
        //    adapter.notifyItemChanged(previous_selected_list.get(1));

        //}

        //if(previous_selected_list.size()==3){       //--size 3 : current, just unselected and previous unselected:
        //    boolean previous_unselected = unselected_list.get(previous_selected_list.get(2));
        //    unselected_list.set(previous_selected_list.get(2),false);
        //    unselected_list.set(previous_selected_list.get(0),false);
        //    unselected_list.set(previous_selected_list.get(1),true);


        //    boolean current_eq_previous = Objects.equals(previous_selected_list.get(0), previous_selected_list.get(1));
        //    boolean unselected_eq_previous_unselected = Objects.equals(previous_selected_list.get(1), previous_selected_list.get(2));

        //    if(unselected_eq_previous_unselected && current_eq_previous){
        //        if(selected_list.get(position) == false){
        //            unselected_list.set(previous_selected_list.get(1),true);

        //        }else{
        //            unselected_list.set(previous_selected_list.get(1),false);
        //        }
        //    }

        //    if(unselected_eq_previous_unselected && !current_eq_previous){
        //        if(selected_list.get(position) == true && previous_unselected == false){
        //            unselected_list.set(previous_selected_list.get(1),true);

        //        }else if(selected_list.get(position) == true && previous_unselected == true){
        //            unselected_list.set(previous_selected_list.get(1),false);

        //        }
        //    }

        //    adapter.notifyItemChanged(previous_selected_list.get(0));
        //    adapter.notifyItemChanged(previous_selected_list.get(1));
        //    //adapter.notifyItemChanged(previous_selected_list.get(2));

        //    if(previous_selected_list.size()>=3){
        //        previous_selected_list.remove(2);
        //    }
        //}
    }

}
