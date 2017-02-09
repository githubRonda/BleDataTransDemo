package com.ronda.bledatatransdemo.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/01/15
 * Version: v1.0
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context        mContext;
    protected List<T>        mDatas;
    protected LayoutInflater mInflater; //这个其实没有用上，
    protected int            layoutId;

    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mDatas = (datas != null ? datas : new ArrayList<T>());
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    public void addData(T data){
        mDatas.add(data);
        notifyDataSetChanged();
    }

    public T getData(int position){
        return mDatas.get(position);
    }

    public void clear(){
        mDatas.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    /**
     * 控件赋值
     *
     * @param holder
     * @param bean
     */
    public abstract void convert(ViewHolder holder, T bean);
}
