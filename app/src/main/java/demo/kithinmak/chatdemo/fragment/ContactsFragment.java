package demo.kithinmak.chatdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

import demo.kithinmak.chatdemo.R;

/**
 * Created by kithin mak on 2016/11/16.
 */

public class ContactsFragment extends Fragment {

    ArrayList<String> parentList = new ArrayList<String>();
    ArrayList<String> childList1 = new ArrayList<String>();
    ArrayList<String> childList2 = new ArrayList<String>();
    ArrayList<String> childList3 = new ArrayList<String>();

    ArrayList<Integer> childCount = new ArrayList<Integer>();
    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_contacts, null);
        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.eblv_contacts);
        expandableListView.setGroupIndicator(null);//去掉ExpandableListView 默认的箭头
        initData();
        MyAdapter myAdapter = new MyAdapter();
        expandableListView.setAdapter(myAdapter);
        return view;
    }

    private void initData() {
        for(int i =0;i<3;i++){
            String s = "分组"+i;
            parentList.add(s);
        }

        for(int i=0;i<10;i++){
            String s = "小李子"+i;
            childList1.add(s);
        }

        for(int i=0;i<3;i++){
            String s = "小樱子"+i;
            childList2.add(s);
        }

        for(int i=0;i<5;i++){
            String s = "小疯子"+i;
            childList3.add(s);
        }

        childCount.add(childList1.size());
        childCount.add(childList2.size());
        childCount.add(childList3.size());

        list.add(childList1);
        list.add(childList2);
        list.add(childList3);

    }

    class MyAdapter extends BaseExpandableListAdapter{

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            return parentList.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int i) {
            return childCount.get(i);
        }

        //  获得某个父项
        @Override
        public String getGroup(int i) {
            return parentList.get(i);
        }

        //  获得某个父项的某个子项
        @Override
        public String getChild(int i, int i1) {
            return list.get(i).get(i1);
        }

        //  获得某个父项的id
        @Override
        public long getGroupId(int i) {
            return i;
        }

        //  获得某个父项的某个子项的id
        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //  获得父项显示的view
        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            View parentView = View.inflate(getContext(), R.layout.expandablelist_group, null);
            TextView tvParentName = (TextView) parentView.findViewById(R.id.tv_parentname);
            TextView tvCount = (TextView) parentView.findViewById(R.id.tv_count);
            tvCount.setText(getChildrenCount(i)+"个");
            tvParentName.setText(getGroup(i));
            return parentView;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            View childView = View.inflate(getContext(), R.layout.expandablelist_child, null);
            TextView tvChildName = (TextView) childView.findViewById(R.id.tv_childname);
            tvChildName.setText(getChild(i,i1));
            return childView;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}
