package demo.kithinmak.chatdemo.domain;

import demo.kithinmak.chatdemo.R;
import demo.kithinmak.chatdemo.fragment.BlankFragment;
import demo.kithinmak.chatdemo.fragment.ContactsFragment;
import demo.kithinmak.chatdemo.fragment.NewsFragment;

/**
 * Created by kithin mak on 2016/11/17.
 * tab对象枚举
 */

public enum MainTabs {

    NewsTab(0, NewsFragment.class,"news","消息", R.drawable.item_pressed),

    ContactsTab(1,ContactsFragment.class, "contacts","联系人",R.drawable.item_pressed),

    FindsTab(2, BlankFragment.class,"finds","发现", R.drawable.item_pressed);

    private int id;
    private Class<?> clazz;//fragment的字节码文件
    private String name;//tab的标志
    private String text;//tab布局的文字
    private int resourceID;//tab布局的图片


    MainTabs(int id, Class<?> clazz, String name, String text, int resourceID) {
        this.id=id;
        this.clazz=clazz;
        this.name=name;
        this.text=text;
        this.resourceID=resourceID;
    }

    public int getId() {
        return id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getResourceID() {
        return resourceID;
    }
}
