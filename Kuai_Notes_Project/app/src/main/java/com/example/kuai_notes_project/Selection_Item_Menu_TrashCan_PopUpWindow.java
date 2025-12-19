package com.example.kuai_notes_project;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class Selection_Item_Menu_TrashCan_PopUpWindow {
    LayoutInflater layoutInflater;
    PopupWindow popupWindow;
    Animation Animation_selection_menu, Animation_Recycle_Item, Animation_Fire_Item;
    public interface ST_PopupDismissListener{//esto puede ir tambien en una clase separada
        void onTrashCanSelection_PopupClosed(int option); // 0 nada/normal, 1 cambio realizado, 2 cancelado
    }
    public void setListener_dismiss(ST_PopupDismissListener listener){
        this.listener_option_dismiss = listener;
    }
    private ST_PopupDismissListener listener_option_dismiss;

    private final Context context;
    private int position = -1;

    public Selection_Item_Menu_TrashCan_PopUpWindow(Context context, int position){//!!position value is not making sense
        this.context = context;
        this.position = position;
    }

    public void show(View view_brought){
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.selection_menu_trashcan,null);
        FrameLayout fl_recycle = container.findViewById(R.id.FL_SM_T_Recycle);
        FrameLayout fl_delete = container.findViewById(R.id.FL_SM_T_Delete);
        Animation_selection_menu = AnimationUtils.loadAnimation(context,R.anim.reminder_setter_btn_need_update);
        Animation_Recycle_Item = AnimationUtils.loadAnimation(context,R.anim.recycler_item_selection_multiple);
        Animation_Fire_Item = AnimationUtils.loadAnimation(context,R.anim.fire_icon_selection_multiple);


        popupWindow = new PopupWindow(container,140,360,false);
        popupWindow.setAnimationStyle(R.style.SelectionMenuAnimationInOut_ItemVisualizer);//!!set proper animation
        //popupWindow.showAtLocation(view_brought, Gravity.RIGHT,0,-400);
        popupWindow.showAsDropDown(view_brought,60,-150,Gravity.TOP|Gravity.RIGHT);


        fl_recycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "recycle", Toast.LENGTH_SHORT).show();

                fl_recycle.startAnimation(Animation_Recycle_Item);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.dismiss();
                        if (listener_option_dismiss != null) {
                            listener_option_dismiss.onTrashCanSelection_PopupClosed(1);
                        }
                    }
                }, 300); // Realiza accion luego de 300 milisegundos
                ///popupWindow.dismiss();
                ///if (listener_option_dismiss != null) {
                ///    listener_option_dismiss.onTrashCanSelection_PopupClosed(1); // Devolver el valor
                ///}
            }
        });
        fl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();

                fl_delete.startAnimation(Animation_Fire_Item);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.dismiss();
                        if (listener_option_dismiss != null) {
                            listener_option_dismiss.onTrashCanSelection_PopupClosed(2);
                        }
                    }
                }, 300); // Realiza accion luego de 300 milisegundos
                ///popupWindow.dismiss();
                ///if (listener_option_dismiss != null) {
                ///    listener_option_dismiss.onTrashCanSelection_PopupClosed(2); // Devolver el valor
                ///}
            }
        });

    }
}
