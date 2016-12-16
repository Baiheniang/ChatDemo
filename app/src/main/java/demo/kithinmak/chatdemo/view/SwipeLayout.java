package demo.kithinmak.chatdemo.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import demo.kithinmak.chatdemo.manager.SwipeLayoutManager;

/**
 * Created by kithin mak on 2016/11/17.
 */

//继承已有的layout，完成onMeasure
public class SwipeLayout extends FrameLayout {

    private View rightView;
    private View leftView;
    private int leftHeight,rightWidth;
    private int leftWidth,rightHieght;
    private ViewDragHelper mViewDragHelper;
    private float startY;
    private float startX;

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    SwipeState currentState = SwipeState.Close;//设置默认为关闭

    enum SwipeState{
        Open,Close;
    }

    private void init() {
        //初始化viewdraghelper
        mViewDragHelper = ViewDragHelper.create(this,mCallback);
    }

    //xml结束，初始化控件
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        leftView = getChildAt(0);
        rightView = getChildAt(1);
    }

    //在onMeasure后执行，用于获取子控件高度和宽度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        leftWidth = leftView.getMeasuredWidth();
        leftHeight = leftView.getMeasuredHeight();
        rightWidth = rightView.getMeasuredWidth();
        rightHieght = rightView.getMeasuredHeight();
//        System.out.println(leftWidth+"."+rightWidth);
//        System.out.println(leftView.getRight());
//        System.out.println(leftHeight+">"+rightHieght);
    }

    //初始化布局，使rightview位于右边
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        leftView.layout(0,0,leftWidth,leftHeight);
        rightView.layout(leftWidth,0,leftWidth+rightWidth,rightHieght);
//        System.out.println(leftWidth+"."+rightWidth);
//        System.out.println(leftView.getRight());
//        System.out.println(leftHeight+">"+rightHieght);
    }

    //是否拦截子类触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
        //如果当前有打开的，则需要直接拦截，交给onTouch处理
        if(!SwipeLayoutManager.getInstance().isCurrentLayout(this)){
            //操作的不是当前打开的，先关闭当前控件
            SwipeLayoutManager.getInstance().closeCurrentLayout();
            result=true;
            //System.out.println("onInterceptTouchEvent");
        }
        //System.out.println("onInterceptTouchEvent="+result);

//        //如果当前有打开的，则需要直接拦截，交给onTouch处理
//        if(SwipeLayoutManager.getInstance().isOpenLayout(this)){
//            SwipeLayoutManager.getInstance().closeCurrentLayout();
//            result=true;
//        }

        return result;
    }

    //使viewDragHelper可用
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //坐标原点是view的左上角
                startX =  getX();
                startY = getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = getX();
                float moveY = getY();
                if(Math.abs(startX -moveX)>=Math.abs(startY -moveY)){
                    if(currentState==SwipeState.Open){
                        //当前为打开且水平滑动，交给当前控件处理，阻止侧滑父类的事件
                        requestDisallowInterceptTouchEvent(true);
                    }else {
                        if(startX-moveX>0){
                            //向左划，交给当前控件处理，阻止侧滑父类的事件
                            requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                //更新开始的点
                startX = getX();
                startY = getY();
                break;
           case MotionEvent.ACTION_UP:
               if(SwipeLayoutManager.getInstance().isOpenLayout(this)){
                  //手抬起的时候若已经有打开的layout，关闭layout
                  SwipeLayoutManager.getInstance().closeCurrentLayout();
               }
                break;
        }

        //System.out.println(SwipeLayoutManager.getInstance().isCurrentLayout(this));
        if(!SwipeLayoutManager.getInstance().isCurrentLayout(this)){
            return true;
        }

        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        //判断是否捕获到当前控件
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==leftView||child==rightView;
        }

        //水平拖动的距离限制
        @Override
        public int getViewHorizontalDragRange(View child) {
            return rightWidth;
        }

        //控制水平拖动调用
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
           if(child==leftView){
               //限制左view的拖动范围
               if(left<-rightWidth)
                   left=-rightWidth;
               else if(left>0)
                   left=0;
           }else if(child==rightView){
               //限制右view的拖动范围
                //System.out.println("rightview="+left);
               if(left<leftWidth-rightWidth)
                   left=leftWidth-rightWidth;
               else if(left>leftWidth)
                   left=leftWidth;
            }
            return left;
        }

        //移动的时候调用
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(changedView==leftView){
                //使右控件跟着移动,移动的时候已经限制了左控件的移动
                rightView.layout(dx+rightView.getLeft(),rightView.getTop(),dx+rightView.getRight(),rightView.getBottom());
            }else if(changedView==rightView){
                //使左控件跟着移动
                int newLeft = leftView.getLeft()+dx;
                if(newLeft<-rightWidth){
                    newLeft=-rightWidth;
                }else if(newLeft>0){
                    newLeft=0;
                }
                leftView.layout(newLeft,leftView.getTop(),newLeft+leftWidth,leftView.getBottom());
            }

            //判断当前状态
            if(rightView.getLeft()==leftWidth-rightWidth && currentState!=SwipeState.Open){
                //当前为打开
                currentState=SwipeState.Open;
                //让manager记录打开的控件
                SwipeLayoutManager.getInstance().setLayout(SwipeLayout.this);
            }else if(rightView.getLeft()==leftWidth && currentState!=SwipeState.Close){
                //当前为关闭
                currentState=SwipeState.Close;
                //清理当前打开控件
                SwipeLayoutManager.getInstance().clearCurrentLayout();
            }
        }

        //释放控件的时候调用
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(rightView.getLeft()>leftWidth-rightWidth/2){
                //关闭
                close();
            }else{
                //打开
                open();
            }

            //System.out.println(xvel);
            //处理用户的稍微滑动
            if(xvel>500 && currentState!=SwipeState.Close){
                //关闭
                close();
            }else if(xvel<-500 && currentState!=SwipeState.Open){
                //打开
                open();
            }
        }
    };

    //打开的方法
    private void open() {
        mViewDragHelper.smoothSlideViewTo(rightView,leftWidth-rightWidth,rightView.getTop());//平滑移动为打开
        mViewDragHelper.smoothSlideViewTo(leftView,-rightWidth,leftView.getTop());
        //整个控件刷新！！
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    //关闭的方法
    public void close() {
        mViewDragHelper.smoothSlideViewTo(rightView,leftWidth,rightView.getTop());//平滑移动为关闭
        mViewDragHelper.smoothSlideViewTo(leftView,0,leftView.getTop());
        //整个控件刷新！！
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }
}
