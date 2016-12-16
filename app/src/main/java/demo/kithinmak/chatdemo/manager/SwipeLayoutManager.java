package demo.kithinmak.chatdemo.manager;

import demo.kithinmak.chatdemo.view.SwipeLayout;

/**
 * Created by kithin mak on 2016/11/19.
 */

public class SwipeLayoutManager {
    private static SwipeLayoutManager manager = new SwipeLayoutManager();
    private SwipeLayoutManager(){

    }
    public static SwipeLayoutManager getInstance(){
        return manager;
    }

    private static SwipeLayout currentLayout ;//用来记录当前打开的控件

    public void setLayout(SwipeLayout layout){
        currentLayout = layout;
    }

    /**
     * 返回操作的是否是当前打开的layout
     * @param layout 当前操作的layout
     * @return 是：是当前的layout或当前没有打开的layout；否：不是当前的layout
     */
    public boolean isCurrentLayout(SwipeLayout layout){
        if (currentLayout==null){
            //没有滑动的layout，不需要阻止父类拦截
            return true;
        }else{
            return currentLayout==layout;
        }
    }

    /**
     * 返回是否打开了
     * @param layout
     * @return
     */
    public boolean isOpenLayout(SwipeLayout layout){
        if(currentLayout==null){
            return false;
        }else{
            return true;
        }
    }

    //关闭当前layout
    public void closeCurrentLayout(){
        if(currentLayout!=null){
            currentLayout.close();
        }
    }

    //清理layout
    public void clearCurrentLayout(){
        currentLayout=null;
    }
}
