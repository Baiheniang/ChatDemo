package demo.kithinmak.chatdemo.view;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by kithin mak on 2016/11/13.
 */

//继承帧布局，自动完成onMeasure
public class SlideMenu extends FrameLayout {

    private View menuView;//菜单view
    private View mainView;//主页面view
    private ViewDragHelper mViewDragHelper;
    private int width;
    private float dragRange;//拖拽范围
    private ArgbEvaluator argbEvaluator;//颜色的计算器
    private FloatEvaluator floatEvaluator;
    private State currentState = State.Close;//默认为关闭

    public State  getCurrentState() {
        return currentState;
    }

    public enum State{
        Open,Close;
    }

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
        //初始化viewDragHelper
        mViewDragHelper = ViewDragHelper.create(this,mCallback);
        //初始化颜色计算器
        argbEvaluator = new ArgbEvaluator();
        floatEvaluator = new FloatEvaluator();
    }

    //初始化布局
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //使menuview在mainview的左边
        menuView.layout((int) -dragRange,0,0,getBottom());
    }

    //当布局xml结束后，就能知道有多少个子类
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //简单的异常处理
        if(getChildCount()!=2){
            throw new IllegalArgumentException("子类必须是两个");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    //onMeasure结束之后，实行这个方法，通常用来初始化宽高
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width *0.8f;
        ViewGroup.LayoutParams params = menuView.getLayoutParams();
        params.width= (int) dragRange;
        menuView.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        //判断是否捕获到当前控件
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==menuView || child==mainView;
        }

        //获取水平移动的范围
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        //获取view水平移动
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==mainView) {
                if (left < 0) {
                    left = 0;
                } else if (left > dragRange) {
                    left = (int) dragRange;
                }
            }
//            }else if(child==menuView){
//              //只要一移动left才会开始计算。所以无法推断mainview的getLeft是否已经到达了dragRange
//                //所以选择在做伴随移动到时候做限制
//               Log.e("clampViewPosition","left="+left);
//           }
            return left;
        }

        //当移动的时候，通常用来做伴随移动
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //使menuview和mainview互相伴随移动。
            if(changedView == mainView){
                //Log.e("onViewPositionChanged","menuview,getLeft="+menuView.getLeft());
                //Log.e("onViewPositionChanged","menuview,left="+left);
                menuView.layout(menuView.getLeft()+dx,0,menuView.getRight()+dx,menuView.getBottom()+dy);
            }else if(changedView ==menuView){
                int newLeft = mainView.getLeft()+dx;
                //限制mainview的位置和固定menuView的位置
                if(newLeft<0){
                    //限制左边界
                    newLeft =0;
                    menuView.layout((int) -dragRange,0,0,mainView.getBottom());
                }else if(newLeft>dragRange){
                    //限制右边界
                    newLeft = (int) dragRange;
                    menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getBottom());
                }
                mainView.layout(newLeft,0,mainView.getMeasuredWidth()+newLeft,mainView.getBottom()+dy);
                //Log.e("onViewPositionChanged","mainview,getRight()="+mainView.getRight());
            }

            //初始化动画的百分比
            float fraction = mainView.getLeft()/dragRange;
            //执行伴随的动画
            executeAnimation(fraction);
            //回调状态在变化的时候的方法给调用者
            if(listener!=null){
                listener.onChanged(fraction);
            }

            //
            if(fraction==0 && currentState!=State.Close){
                //改变当前状态为关闭，且调用关闭方法
                currentState = State.Close;
                if(listener!=null)
                listener.onClose();
            }
            if(fraction==1 && currentState!=State.Open){
                //改变当前状态为打开，且调用打开方法
                currentState = State.Open;
                if(listener!=null)
                    listener.onOpen();
            }

        }

//        //当view开始被捕获或解析的回调
//        @Override
//        public void onViewCaptured(View capturedChild, int activePointerId) {
//            super.onViewCaptured(capturedChild, activePointerId);
//        }

        //当控件被释放的时候调用
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //判断mainview的左边界的位置，做平滑移动动画
            if(mainView.getLeft()>=dragRange/2){
                open();

            }else{
                close();
            }
        }

    };

    public void close() {
        //平滑移动到左边
        mViewDragHelper.smoothSlideViewTo(mainView,0,0);
        mViewDragHelper.smoothSlideViewTo(menuView, (int) -dragRange,0);
        //然后整个控件刷新
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void open() {
        //平滑移动到右边
        mViewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,0);
        mViewDragHelper.smoothSlideViewTo(menuView,0,0);
        //然后整个控件刷新，因为底层是用scroller来移动的
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void executeAnimation(float fraction) {
        //给mainView设置透明效果
        //ViewHelper.setAlpha(mainView,floatEvaluator.evaluate(fraction,1,0.6));

        //给mainView的背景添加黑色的遮罩效果
       // mainView.getBackground().setColorFilter((Integer) argbEvaluator.evaluate(fraction/2, Color.TRANSPARENT, R.color.colorGray), PorterDuff.Mode.SRC_OVER);//这个模式就是覆盖在上面
    }

    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            //表示动画还没结束,刷新
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }


    private onStateChangedListener listener;
    public void setOnStateChangedListener(onStateChangedListener listener){
        this.listener =listener;
    }

    public interface onStateChangedListener{
        /**
         * 打开的回调
         */
        void onOpen();

        /**
         * 关闭的回调
         */
        void onClose();

        /**
         * 正在改变的回调
         * @param fraction 滑动比例
         */
        void onChanged(float fraction);
    }
}
