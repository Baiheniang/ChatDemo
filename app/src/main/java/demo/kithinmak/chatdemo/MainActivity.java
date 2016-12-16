package demo.kithinmak.chatdemo;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;

import demo.kithinmak.chatdemo.domain.MainTabs;
import demo.kithinmak.chatdemo.view.MyLinearLayout;
import demo.kithinmak.chatdemo.view.SlideMenu;

public class MainActivity extends FragmentActivity implements View.OnTouchListener, TabHost.OnTabChangeListener {

    private FragmentTabHost mTabHost;
    private ArgbEvaluator mArgbEvaluator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initView();
        initTab();

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);
    }

    private void init() {
        mArgbEvaluator = new ArgbEvaluator();
    }

    private void initView() {
        //初始化fragmentTabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        //对tabhost做初始化的操作，但是在activity和在fragment里面这两句代码是不一样的，fragment的FragmentManager一定要写成 getChildFragmentManager()，说明是他子fragment的manager。
        //使tabhost和frameLayout有关联
        mTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);

        //初始化slideMenu，进行动画操作
        final SlideMenu slidemenu = (SlideMenu) findViewById(R.id.sliedemenu_main);
        MyLinearLayout linearlayout = (MyLinearLayout) findViewById(R.id.ll_myll);
        //初始化主页面的头像控件
        final ImageView ivHead = (ImageView) findViewById(R.id.iv_head);
        final LinearLayout llTop = (LinearLayout) findViewById(R.id.ll_top);
        TextView tvChange = (TextView) findViewById(R.id.tv_change);

        slidemenu.setOnStateChangedListener(new SlideMenu.onStateChangedListener() {
            @Override
            public void onOpen() {
                Toast.makeText(getApplicationContext(),"打开了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(getApplicationContext(),"关闭了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChanged(float fraction) {
                //使头像透明
                ivHead.setAlpha(1-fraction);
                ViewHelper.setAlpha(llTop, (float) (1-fraction*0.5));
            }
        });

        linearlayout.setSlidemenu(slidemenu);

        //点击头像打开菜单
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(slidemenu.getCurrentState()!= SlideMenu.State.Open)
                slidemenu.open();
            }
        });

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(slidemenu.getCurrentState()!= SlideMenu.State.Close)
                    slidemenu.close();
            }

        });

    }

    private void initTab() {
        MainTabs[] tabs = MainTabs.values();
        for (int i = 0; i < tabs.length; i++) {
            MainTabs tab = tabs[i];//获取tab对象

            //创建选项卡
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tab.getName());

            //获取布局
            // View item = View.inflate(this, R.layout.tab_item, null);
            //要使用这种方法找出parent控件
            View indicator = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_item, null);
            //初始化控件
            ImageView image = (ImageView) indicator.findViewById(R.id.iv_image);
            TextView text = (TextView) indicator.findViewById(R.id.tv_text);

            image.setImageResource(tab.getResourceID());
            text.setText(tab.getText());

            //设置布局
            tabSpec.setIndicator(indicator);

            //添加选项卡
            mTabHost.addTab(tabSpec,tab.getClazz(),null);
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onTabChanged(String s) {

    }
}

/*
注意：
    //新建选项卡
    TabHost.TabSpec tabSpec1 =  tabHost.newTabSpec("news");//标志
    //设置tab的内容
    text.setText("信息");
    image.setBackgroundResource(R.drawable.item_pressed);
    tabSpec1.setIndicator(inflate);
    tabHost.addTab(tabSpec1, NewsFragment.class,null);

    --这里必须再建一个view控件
    View inflate2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_item, null);
    text = (TextView) inflate2.findViewById(R.id.tv_text);
    image = (ImageView) inflate2.findViewById(R.id.iv_image);
    TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("contacts");//标志
    //设置tab的内容
    text.setText("联系人");
    tabSpec2.setIndicator(inflate2);
    tabHost.addTab(tabSpec2, ContactsFragment.class,null);--否则这里会报错，IllegalStateException：表示父类已经有子类了
 */
