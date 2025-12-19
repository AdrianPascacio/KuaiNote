package com.example.kuai_notes_project;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class Selection_Item_Menu_MemoBoard_PopUpWindow {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    Animation Animation_selection_menu, Animation_Pin_Status_Change_Remove, Animation_Pin_Status_Change_Apply, Animation_Delete_Item;
    public interface SM_PopupDismissListener {//esto puede ir tambien en una clase separada
        void onMemoBoardSelection_PopupClosed(int option); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    public void setListener_dismiss(SM_PopupDismissListener listener){
        this.listener_option_dismiss = listener;
    }
    private SM_PopupDismissListener listener_option_dismiss;

    private final Context context;
    private int position = -1;

    public Selection_Item_Menu_MemoBoard_PopUpWindow(Context context, int position){//!!position value is not making sense
        this.context = context;
        this.position = position;
    }

    public void show(View view_brought,boolean pin_initial_state){
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.selection_menu_memoboard,null);
        FrameLayout fl_pin = container.findViewById(R.id.FL_SM_M_Pin);
        FrameLayout fl_reminder = container.findViewById(R.id.FL_SM_M_Reminder);
        FrameLayout fl_delete = container.findViewById(R.id.FL_SM_M_Delete);
        Animation_selection_menu = AnimationUtils.loadAnimation(context,R.anim.reminder_setter_btn_need_update);
        Animation_Pin_Status_Change_Remove = AnimationUtils.loadAnimation(context,R.anim.pin_change_status_multiple_selection_remove);
        Animation_Pin_Status_Change_Apply = AnimationUtils.loadAnimation(context,R.anim.pin_change_status_multiple_selection_apply);
        Animation_Delete_Item = AnimationUtils.loadAnimation(context,R.anim.delete_icon_selection_multiple);

        //fl_pin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor( pin_initial_state ? "#F4B183" : "#A4A4A4" )));///Ternary Operator
        fl_pin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, pin_initial_state ? R.color.ex_orange : R.color.Neutral_gray_icon_note))); ///Ternary Operator

        //Valores con 3 iconos: popupWindow = new PopupWindow(container,140,510,false);//!!verificar height apropiado
        popupWindow = new PopupWindow(container,140,360,false);//!!verificar height apropiado
        popupWindow.setAnimationStyle(R.style.SelectionMenuAnimationInOut_ItemVisualizer);//!!set proper animation

        //popupWindow.showAtLocation(view_brought, Gravity.RIGHT,0,-400);
        //popupWindow.showAsDropDown(view_brought,60,-300,Gravity.TOP|Gravity.RIGHT);
        popupWindow.showAsDropDown(view_brought,60,-150,Gravity.TOP|Gravity.RIGHT);


        fl_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "pin", Toast.LENGTH_SHORT).show();
                if (pin_initial_state){
                    fl_pin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Neutral_gray_icon_note)));
                    fl_pin.startAnimation(Animation_Pin_Status_Change_Remove);
                } else{

                    fl_pin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.ex_orange))); ///Ternary Operator
                    fl_pin.startAnimation(Animation_Pin_Status_Change_Apply);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.dismiss();
                        if (listener_option_dismiss != null) {
                            listener_option_dismiss.onMemoBoardSelection_PopupClosed(1);
                        }
                    }
                }, 300); // Realiza accion luego de 300 milisegundos
            }
        });
        fl_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "reminder", Toast.LENGTH_SHORT).show();

                popupWindow.dismiss();
                if (listener_option_dismiss != null) {
                    listener_option_dismiss.onMemoBoardSelection_PopupClosed(2); // Devolver el valor
                }
            }
        });
        fl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                fl_delete.startAnimation(Animation_Delete_Item);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.dismiss();
                        if (listener_option_dismiss != null) {
                            listener_option_dismiss.onMemoBoardSelection_PopupClosed(3);
                        }
                    }
                }, 300); // Realiza accion luego de 300 milisegundos
                ///popupWindow.dismiss();
                ///if (listener_option_dismiss != null) {
                ///    listener_option_dismiss.onMemoBoardSelection_PopupClosed(3); // Devolver el valor
                ///}
            }
        });
    }
}
