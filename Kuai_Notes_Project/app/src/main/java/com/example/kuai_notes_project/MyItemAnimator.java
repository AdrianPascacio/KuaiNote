package com.example.kuai_notes_project;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemAnimator extends DefaultItemAnimator {
    private ItemAnimatorListener listener;
    public interface ItemAnimatorListener {
        void onAnimationFinished();
    }

    public MyItemAnimator (ItemAnimatorListener listener){
        this.listener = listener;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY){
        boolean result = super.animateChange(oldHolder,newHolder,fromX,fromY,toX,toY);
        return result;
    }
    @Override
    public void onAnimationFinished(RecyclerView.ViewHolder viewHolder){
        super.onAnimationFinished(viewHolder);
        if (listener != null){
            listener.onAnimationFinished();
        }
    }

}
