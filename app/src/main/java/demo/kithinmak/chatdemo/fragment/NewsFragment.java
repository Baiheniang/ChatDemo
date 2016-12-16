package demo.kithinmak.chatdemo.fragment;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.kithinmak.chatdemo.R;
import demo.kithinmak.chatdemo.domain.News;
import demo.kithinmak.chatdemo.view.SwipeLayout;
import demo.kithinmak.chatdemo.view.listener.GooViewListener;

import static demo.kithinmak.chatdemo.R.id.swipe;

/**
 * Created by kithin mak on 2016/11/16.
 */

public class NewsFragment extends Fragment {

    //private HashSet<Integer> mRemoved = new HashSet<Integer>();//记住已经消除了的控件的位置
    private ArrayList<News> list = new ArrayList<News>();
    private ListView mListview;
    private Handler mHandler = new Handler();
    private int tvReadWidth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_news, null);
        initData();
        mListview = (ListView) view.findViewById(R.id.lv_news);
        mListview.setAdapter(new MyAdapter());
        return view;
    }

    private void initData() {
        for(int i=0;i<10;i++){
            News news = new News();
            news.number = 1;
            news.content = "你好~";
            news.title =  "小李子"+i;
            news.drawableResouce = R.drawable.headportrait;
            news.isVisiable = true;//默认可显示
            news.position = i;
            news.isTop = false;//默认都是未置顶
            if(news.isVisiable)
            {
                news.isRead = false;//标记已读
            }else{
                news.isRead = true;//标记未读
            }

            list.add(news);
        }
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public News getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View converVIew, ViewGroup viewGroup) {

            if(converVIew==null){
                converVIew = View.inflate(getContext(),R.layout.adapter_list,null);
            }

            final ViewHolder holder = ViewHolder.getHolder(converVIew);
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.swipelayout.close();
                    list.remove(position);
                    notifyDataSetChanged();

                }
            });

            holder.tvName.setText(getItem(position).title);
            holder.tvContent.setText(getItem(position).content);

            //boolean visiable = !mRemoved.contains(position);
            boolean visiable = getItem(position).isVisiable;//判断气泡是否可显示
            boolean isTop = getItem(position).isTop;//判断是否点击了置顶按钮
            holder.tvPoint.setVisibility(visiable?View.VISIBLE:View.GONE);

            //holder.tvRead.setVisibility(visiable?View.VISIBLE:View.GONE);//剩下两个控件往前走
            //System.out.println(holder.tvRead.getWidth());//为0
            //holder.tvRead.setWidth(visiable?holder.tvRead.getMeasuredWidth():0);

            //获取真实宽度
//            holder.tvRead.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    holder.tvRead.getViewTreeObserver().removeGlobalOnLayoutListener(this);//得到后取消监听
//                    tvReadWidth = holder.tvRead.getWidth();
//                    System.out.println("holder.tvRead.getWidth()="+holder.tvRead.getWidth());
//                }
//            });

            if(isTop){
                //点击了置顶按钮
                holder.tvTop.setText("取消置顶");
                converVIew.setBackgroundColor(Color.rgb(214,214,214));
            }else{
                holder.tvTop.setText("置顶");
                converVIew.setBackgroundColor(Color.WHITE);
            }


            if(visiable){
                holder.tvPoint.setText(getItem(position).number+"");
                holder.tvPoint.setTag(1);
                holder.tvRead.setText("标为已读");

                GooViewListener mGooListener = new GooViewListener(getContext(),holder.tvPoint){
                    @Override
                    public void onDisappear(PointF dragCenter) {
                        //System.out.println("NewsFragment-onDisappear");
                        super.onDisappear(dragCenter);
                        //mRemoved.add(position);
                        getItem(position).isVisiable = false;
                        getItem(position).isRead = true;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onReset(boolean isOutOfRange) {
                        super.onReset(isOutOfRange);
                        notifyDataSetChanged();
                    }
                };
                holder.tvPoint.setOnTouchListener(mGooListener);
            }else{
                holder.tvRead.setText("标为未读");
            }

            holder.tvRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //关闭layout
                    holder.swipelayout.close();
                    //System.out.println("isRead="+getItem(position).isRead);

                    if(getItem(position).isRead){
                        //点击了标记未读，使气泡出现
                        getItem(position).isVisiable=true;
                        getItem(position).isRead = false;
                        holder.tvRead.setText("标为已读");
                    }else{
                        //点击了标记已读，使气泡消失
                        getItem(position).isVisiable=false;
                        getItem(position).isRead = true;
                        holder.tvRead.setText("标为未读");
                    }
                    notifyDataSetChanged();
                }
            });

            holder.tvTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //关闭layout
                    holder.swipelayout.close();

                    if(holder.tvTop.getText().toString().equals("置顶")){
                        //点击了置顶
                        getItem(position).isTop = true;
                        list.add(0,list.get(position));
                        list.remove(position+1);
                    }else{
                        getItem(position).isTop = false;
                        News news1 = list.get(position);
                        int goBack = list.size()+1;
                        for (int i =0;i<list.size();i++){
                            //找到没有置顶且位置比以前位置后的
                            if(getItem(i).position> news1.position && !getItem(i).isTop){
                                goBack = i;
                                break;
                            }
                        }
                        list.add(goBack,news1);
                        list.remove(position);
                    }

                    notifyDataSetChanged();
                }
            });

            return converVIew;
        }
    }

    //viewholder的优化
    static class ViewHolder {
        TextView tvName,tvContent,tvTime,tvTop,tvDelete,tvPoint,tvRead;
        CircleImageView civLeft;
        SwipeLayout swipelayout;

        private ViewHolder(View convertView){
            initView(convertView);
        }

        private static ViewHolder getHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder==null){
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }

        private void initView(View convertView) {
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            tvTop = (TextView) convertView.findViewById(R.id.tv_top);
            tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
            tvPoint = (TextView) convertView.findViewById(R.id.tv_point);
            civLeft = (CircleImageView) convertView.findViewById(R.id.civ_left);
            swipelayout = (SwipeLayout) convertView.findViewById(swipe);
            tvRead = (TextView) convertView.findViewById(R.id.tv_read);
        }
    }
}
