package com.weizh.qqslidemenu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by weizh_000 on 2016/8/22.
 */

public class MyLinearLayout extends LinearLayout {
    private SlideMenu slideMenu;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSlideMenu(SlideMenu slideMenu){
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(slideMenu!=null&&slideMenu.getDragState()== SlideMenu.DragState.open) {
            //当slideMenu处于打开的状态，拦截事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(slideMenu!=null&&slideMenu.getDragState()== SlideMenu.DragState.open) {
            //当slideMenu处于打开的状态，消费掉拦截到的事件
            return true;
        }

        return super.onTouchEvent(event);
    }
}
