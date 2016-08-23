package com.weizh.qqslidemenu.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.weizh.qqslidemenu.utils.ColorUtil;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by weizh_000 on 2016/8/22.
 */

public class SlideMenu extends FrameLayout {
    private View menuView;
    private View mainView;
    private ViewDragHelper mDragHelper;
    private int width;
    private float dragRange;
    private FloatEvaluator floatEvaluator;
    private IntEvaluator intEvaluator;
    private DragState mCurrentState = DragState.close;


    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    public DragState getDragState() {
        return mCurrentState;
    }

    enum DragState{
        open,close;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 2) {
            throw new IllegalArgumentException("SlideMenu only have 2 children!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    /**
     * 该方法在onMeasure()方法执行完之后执行，因此可以在次方法中获取自己和子view的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.6f;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param child 当前触摸的子view
         * @param pointerId
         * @return true就捕获并解析；false不捕获
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        /**
         * 获取水平方向上的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 最好不要返回0
         * @param child 被触摸的子view
         * @return 拖拽的距离
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        /**
         * 控制水平方向上的位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                if (left < 0) left = 0;//限制mainView可向左移动到的位置
                if (left > dragRange) left = (int) dragRange;//限制mainView可向右移动到的位置
            }
            return left;
        }

        /**
         * 当子view移动的时候
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //让另一个子view跟着移动
            if (changedView == menuView) {
                //让menuView固定住
                menuView.layout(0,0,menuView.getRight(),menuView.getBottom());

                int mainLeft = mainView.getLeft() + dx;
                if (mainLeft<0)mainLeft=0;//限制mainView可向左移动到的位置
                if(mainLeft>dragRange)mainLeft= (int) dragRange;//限制mainView可向右移动到的位置
                mainView.layout(mainLeft, mainView.getTop() + dy, mainLeft+mainView.getMeasuredWidth(), mainView.getBottom() + dy);
            }
            //1.计算移动的百分比
            float fraction = mainView.getLeft()/dragRange;
            if (fraction>0.999)fraction=1;
            //2.执行伴随动画
            excuteAnimation(fraction);
            //3.更改状态，并回调相应方法
            if (fraction==0f){
                //关闭状态
                mCurrentState=DragState.close;
                if(dragStateChangeListener!=null)dragStateChangeListener.onClose();
            }else if (fraction==1f){
                //打开状态
                mCurrentState=DragState.open;
                if(dragStateChangeListener!=null)dragStateChangeListener.onOpen();
            }
            //拖拽过程中
                if(dragStateChangeListener!=null)dragStateChangeListener.onDrag(fraction);

        }

        /**
         * 当手指抬起的时候
         * @param releasedChild 在此child抬起
         * @param xvel x方向移动的速度 正：向右移动，负：向左移动
         * @param yvel y方向移动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft()<dragRange/2){
                mDragHelper.smoothSlideViewTo(mainView,0,0);
            }else {
                mDragHelper.smoothSlideViewTo(mainView, (int) dragRange,0);
            }
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    };

    //执行伴随动画
    private void excuteAnimation(float fraction) {
        //缩放mainView
        ViewHelper.setScaleX(mainView,floatEvaluator.evaluate(fraction,1,0.8));//x方向上缩小
        ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fraction,1,0.8));//y方向上缩小
        //缩放menuView
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5,1));//x方向上缩小
        ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5,1));//y方向上缩小
        //移动menuView
        ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
        //设置menuView的透明度
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3,1));
        //给SlideMenu背景添加黑色遮罩
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = mDragHelper.shouldInterceptTouchEvent(ev);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private onDragStateChangeListener dragStateChangeListener;

    //暴露方法给别的对象调用
    public void setOnDragStateChangeListener(onDragStateChangeListener dragStateChangeListener){
        this.dragStateChangeListener = dragStateChangeListener;
    }

    public interface onDragStateChangeListener{
        public void onOpen();
        public void onDrag(float fraction);
        public void onClose();
    }
}
