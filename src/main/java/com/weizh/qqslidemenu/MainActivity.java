package com.weizh.qqslidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weizh.qqslidemenu.constant.Constant;
import com.weizh.qqslidemenu.widget.MyLinearLayout;
import com.weizh.qqslidemenu.widget.SlideMenu;

public class MainActivity extends AppCompatActivity {

    private ListView lvMenu,lvMain;
    private SlideMenu slideMenu;
    private ImageView ivHead;
    private MyLinearLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        lvMenu.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Constant.sCheeseStrings){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        lvMain.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Constant.NAMES));
        slideMenu.setOnDragStateChangeListener(new SlideMenu.onDragStateChangeListener() {
            @Override
            public void onOpen() {
                Log.e("tag","open");
            }

            @Override
            public void onDrag(float fraction) {
                Log.e("tag",fraction+"");
            }

            @Override
            public void onClose() {
                Log.e("tag","close");
                ViewPropertyAnimator.animate(ivHead).translationXBy(15)
                .setInterpolator(new CycleInterpolator(5))
                .setDuration(300)
                .start();
            }
        });
        myLayout.setSlideMenu(slideMenu);
    }

    private void initView() {
        ivHead =(ImageView) findViewById(R.id.iv_head);
        slideMenu =(SlideMenu) findViewById(R.id.slideMenu);
        lvMenu =(ListView) findViewById(R.id.menu_listview);
        lvMain =(ListView) findViewById(R.id.main_listview);
        myLayout =(MyLinearLayout) findViewById(R.id.my_layout);
    }

}
