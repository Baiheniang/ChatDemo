package demo.kithinmak.chatdemo.view.listener;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import demo.kithinmak.chatdemo.R;
import demo.kithinmak.chatdemo.util.Utils;
import demo.kithinmak.chatdemo.view.BubbleLayout;
import demo.kithinmak.chatdemo.view.Gooview;

/**
 * Created by kithin mak on 2016/11/28.
 */

//使用WindowManager添加gooview使它能在整个屏幕拖动。
public class GooViewListener implements View.OnTouchListener,Gooview.onDisappearListener {

    private View point;//圆的控件
    WindowManager mWM;//屏幕管理器
    private int number;
    private Gooview mGooview;//拖动的控件
    private Context contetxt;
    private final WindowManager.LayoutParams mParams;

    private Handler mHandler;

    public GooViewListener(Context context,View point) {
        this.contetxt = context;
        this.point = point;
        number = (int) point.getTag();

        mGooview = new Gooview(context);

        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.format= PixelFormat.TRANSLUCENT;

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int action = MotionEventCompat.getActionMasked(motionEvent);
        if(action==MotionEvent.ACTION_DOWN){

            ViewParent parent = view.getParent();
            // 请求其父级View不拦截Touch事件
            parent.requestDisallowInterceptTouchEvent(true);

            //使原先的textview消失不见
            point.setVisibility(View.INVISIBLE);

            // 初始化当前点击的item的信息，数字及坐标
            mGooview.initCircle(motionEvent.getRawX(),motionEvent.getRawY());
            mGooview.setNumber(number);
            mGooview.setOnDisaapearListener(this);

            // 当按下时，将自定义View添加到WindowManager中
            mWM.addView(mGooview,mParams);
        }

        // 将所有touch事件转交给GooView处理
        mGooview.onTouchEvent(motionEvent);
        return true;
    }

    @Override
    public void onReset(boolean isOutOfRange) {
        //退回去之后，消除控件
        if(mWM!=null && mGooview.getParent() != null){
            mWM.removeView(mGooview);
        }
    }

    @Override
    public void onDisappear(PointF dragCenter) {
        //消除控件
        if(mWM!=null && mGooview.getParent() != null){
            mWM.removeView(mGooview);

            //播放气泡爆炸动画
            ImageView imageView = new ImageView(contetxt);
            imageView.setImageResource(R.drawable.anim_bubble_pop);

            AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();

            final BubbleLayout bubbleLayout = new BubbleLayout(contetxt);
            bubbleLayout.setCenter((int) dragCenter.x, (int) dragCenter.y
                    - Utils.getStatusBarHeight(mGooview));

            bubbleLayout.addView(imageView, new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT));

            mWM.addView(bubbleLayout, mParams);

            drawable.start();

            // 播放结束后，删除该bubbleLayout
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mWM.removeView(bubbleLayout);
                }
            }, 501);
        }
    }

}
