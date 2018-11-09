package com.huaxi.hailuo.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huaxi.hailuo.R;

import java.util.List;

/**
 * Created by zhangliuguang  on 2018/4/27.
 */
public class OpinionBackAdapter extends RecyclerView.Adapter<OpinionBackAdapter.MyViewHolder> {
    private List<String> mList;
    private Context mContext;
    private OnItemClickListener mListener;// 声明自定义的接口
    private TextView textView;
    private View view;
    private TextView tv;

    private String flag = "";

    public OpinionBackAdapter(Context context, List<String> datas) {
        this.mContext = context;
        this.mList = datas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_feed_back, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
//        tv = (TextView) view.findViewById(R.id.tv_feed_back);
        textView.setText(mList.get(position));
//        if (flag.equals(mList.get(position))) {
//            textView.setTextColor(Color.RED);
//        } else {
//            textView.setTextColor(Color.BLACK);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
//                flag = mList.get(position);
//                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // 构造函数中添加自定义的接口的参数
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_feed_back);

        }


    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }

}
