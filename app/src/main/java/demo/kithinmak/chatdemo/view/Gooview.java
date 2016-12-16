package demo.kithinmak.chatdemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import demo.kithinmak.chatdemo.util.GeometryUtil;
import demo.kithinmak.chatdemo.util.Utils;

/**
 * 自定义一个实现拖拽粘性的控件
 * Created by kithin mak on 2016/11/23.
 */

public class Gooview extends View {

    private float dragRadius=0;//拖拽圆的半径
    private PointF dragCenter;//拖拽圆的圆心

    private float stayRadius=0;//固定圆的半径
    private PointF stayCenter;//固定圆的圆心

    private Paint mPaintCircle;//画圆的画笔
    private Paint mPaintText;//画文字的画笔

    private PointF dragCircle[];
    private PointF stayCircle[];
    private PointF controlPoint;

    private double lineK;//斜率

    private float MaxDistance = 0;//最大的距离

    private boolean isDisappear =false;//判断是否消失
    private boolean isOutOfRange = false;//判断是否超过范围

    private ValueAnimator mValueAnimator;
    private String text="";

    public Gooview(Context context) {
        super(context);
        init(context);
    }

    public Gooview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Gooview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        mPaintCircle.setColor(Color.RED);
        dragRadius = Utils.dip2Dimension(10.0f, context);
        stayRadius = Utils.dip2Dimension(10.0f, context);
        MaxDistance= Utils.dip2Dimension(60.0f, context);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextAlign(Paint.Align.CENTER);//文字在中间
        mPaintText.setTextSize(stayRadius * 1.2f);
    }

    /**
     * 初始化固定和拖动圆
     */
    public void initCircle(float x,float y){
        stayCenter = new PointF(x,y);
        dragCenter = new PointF(x,y);
        invalidate();//刷新屏幕，调用onDraw
    }

    /**
     * 设置固定圆的位置
     * @param x
     * @param y
     */
    private void setStayCenter(float x,float y) {
        stayCenter.set(x,y);
    }

    /**
     * 设置拖动圆的位置的改变
     * @param x
     * @param y
     */
    private void setDragCenter(float x,float y) {
        dragCenter.set(x,y);
        invalidate();
    }

    /**
     * 设置文字上的数字
     * @param number
     */
    public void setNumber(int number){
        text = String.valueOf(number);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int status_bar_height_id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        //让画布整体下移，否则因为状态栏的高度使点的位置会在所控制的控件的上方
        canvas.translate(0,-getResources().getDimension(status_bar_height_id));

        if(!isDisappear){
            //绘制拖拽圆
            canvas.drawCircle(dragCenter.x,dragCenter.y,dragRadius,mPaintCircle);
            //绘制固定圆
            canvas.drawCircle(stayCenter.x,stayCenter.y,stayRadius,mPaintCircle);
            //绘制文字
            canvas.drawText(text,dragCenter.x,dragCenter.y + dragRadius /2f,mPaintText);

            if(!isOutOfRange){
                //没有超过范围才画贝塞尔曲线
                drawPath(canvas);
            }
        }

    }

    /**
     * 动态求出因为拖动圆的距离而改变的固定圆半径
     */
    public float getStayCircleRadius(){
        float radius;
        //求出两个圆心之间的距离
        float distance = GeometryUtil.getDistanceBetween2Points(dragCenter, stayCenter);
        float fraction = distance/MaxDistance;//圆心距离占最大距离的百分比
        radius = GeometryUtil.evaluateValue(fraction,12f,4f);
        return radius;
    }

    public void drawPath(Canvas canvas){
        //动态求出因为拖动圆的距离而改变的固定圆半径
        stayRadius = getStayCircleRadius();

       //获取斜率
        float xOffset = stayCenter.x - dragCenter.x;
        float yOffset = stayCenter.y - dragCenter.y;
        if(xOffset!=0){
            lineK = yOffset/xOffset;
        }
        //根据dragCenter动态求出draCircle和stayCircle的上下两个点
        dragCircle = GeometryUtil.getIntersectionPoints(dragCenter,dragRadius,lineK);
        stayCircle = GeometryUtil.getIntersectionPoints(stayCenter,stayRadius,lineK);

        //动态计算控制点. 以两圆连线的0.618处作为 贝塞尔曲线 的控制点。（选一个中间点附近的控制点）
        controlPoint = GeometryUtil.getPointByPercent(dragCenter,stayCenter,0.618f);

        //使用贝塞尔曲线绘制连接部分
        // 绘制两圆连接
        //此处参见示意图{@link https://github.com/PoplarTang/DragGooView }
        Path path = new Path();
        path.moveTo(stayCircle[0].x,stayCircle[1].y);//设置起点
        path.quadTo(controlPoint.x,controlPoint.y,dragCircle[0].x,dragCircle[0].y);//使用贝塞尔曲线连接起来,控制点坐标和连接的坐标
        path.lineTo(dragCircle[1].x,dragCircle[1].y);//连接下一个点
        path.quadTo(controlPoint.x,controlPoint.y, stayCircle[1].x,stayCircle[1].y);
        //path.close();//默认会闭合，所以不用调用
        canvas.drawPath(path,mPaintCircle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(isAnimRunning()){
            return false;//不调用onTouch事件
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                //若正在动画中，则不执行
                if(isAnimRunning())
                {
                    return false;
                }

                isDisappear = false;
                isOutOfRange = false;
                setDragCenter(event.getRawX(),event.getRawY());
                //dragCenter.set(event.getRawX(),event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                //超出范围后，不在执行拖拽事件
                if(GeometryUtil.getDistanceBetween2Points(dragCenter,stayCenter)>MaxDistance){
                    //超过范围
                    isOutOfRange=true;

                    setDragCenter(event.getRawX(),event.getRawY());
                    return false;
                }
                //System.out.println("继续拖动");
                setDragCenter(event.getRawX(),event.getRawY());
                //dragCenter.set(event.getRawX(),event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                //System.out.println("松开了");
                handlerUp();
                break;
        }
        return true;
    }

    private void handlerUp() {
        if(isOutOfRange){
            //判断是否回去范围了范围之中
            if(GeometryUtil.getDistanceBetween2Points(dragCenter,stayCenter)>MaxDistance){
                System.out.println("爆破！");
                //超过范围了，做爆破动画
                disappear();
            }else{
                //System.out.println("");
                //设置成初始坐标
                setDragCenter(stayCenter.x,stayCenter.y);
                isOutOfRange=false;
                if(listener!=null){
                    listener.onReset(isOutOfRange);
                }
            }
        }else{
            //做弹回去的动画
            boundsBack();
            isOutOfRange=false;
        }
    }

    private void disappear() {
        isDisappear = true;
        invalidate();

        if(listener!=null){
            System.out.println("进来！");
            listener.onDisappear(dragCenter);
        }

    }

    /**
     * 弹回去的动画
     */
    private void boundsBack() {
        mValueAnimator = ValueAnimator.ofFloat(1);//随便填，真正使用平滑过渡的是下面的方法。addUpdateListener
        final PointF startPoint = new PointF(dragCenter.x,dragCenter.y);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //动画执行的百分比
                float fraction = valueAnimator.getAnimatedFraction();
                PointF pointF = GeometryUtil.getPointByPercent(startPoint, stayCenter, fraction);
                dragCenter.set(pointF);
                //改变位置需要刷新
                invalidate();
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            //动画结束再做重置动作
            @Override
            public void onAnimationEnd(Animator animator) {
                if(listener!=null){
                    listener.onReset(isOutOfRange);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        if(GeometryUtil.getDistanceBetween2Points(startPoint,stayCenter) < 10)	{
            mValueAnimator.setDuration(10);
        }else {
            mValueAnimator.setDuration(500);
        }
        mValueAnimator.setInterpolator(new OvershootInterpolator(3));//弹的动作
        mValueAnimator.start();
    }

    /**
     * 判断动画是否正在执行
     * @return
     */
    private boolean isAnimRunning(){
        if(mValueAnimator!=null && mValueAnimator.isRunning()){
            return true;
        }
        return false;
    }

    public interface onDisappearListener{
        void onReset(boolean isOutOfRange);//气泡弹回之后回调
        void onDisappear(PointF dragCenter);//消失时回调
    }

    private onDisappearListener listener;

    public void setOnDisaapearListener(onDisappearListener listener){
        this.listener = listener;
    }
}
