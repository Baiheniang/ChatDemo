package demo.kithinmak.chatdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by kithin mak on 2016/11/19.
 */

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slidemenu;
    //设置slidemenu
    public void setSlidemenu(SlideMenu slidemenu){
        this.slidemenu = slidemenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(slidemenu!=null && slidemenu.getCurrentState()== SlideMenu.State.Open){
            //若打开，则拦截子类的触摸事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(slidemenu!=null && slidemenu.getCurrentState()== SlideMenu.State.Open){
            if(event.getAction()==MotionEvent.ACTION_UP){
                slidemenu.close();
            }
            //消费了事件，继续监听
            return true;
        }
        return super.onTouchEvent(event);
    }
}
