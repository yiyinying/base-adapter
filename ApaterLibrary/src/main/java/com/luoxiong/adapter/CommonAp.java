package com.luoxiong.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luoxiong.interfac.IAdapter;
import com.luoxiong.interfac.IAdapterHolder;
import com.luoxiong.tools.ItemTypeUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * ================================================
 * 作    者：lx
 * 创建日期：2016/11/10
 * 描    述：
 * ================================================
 */
public abstract class CommonAp<T> extends RecyclerView.Adapter<CommonAp.CommonVH> implements IAdapter<T> {

    //数据源
    private List<T> mDatas;

    private int mCurrentPos;

    private Object mType;
    private ItemTypeUtil mUtil;

    public CommonAp(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.mDatas=data;
        mUtil = new ItemTypeUtil();
    }

    @Override
    public CommonAp.CommonVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommonVH(parent.getContext(), parent, createItem(1));
    }

    @Override
    public void onBindViewHolder(CommonAp.CommonVH holder, int position) {
        holder.position = position;
        holder.item.handleData(getConvertedData(mDatas.get(position), mType), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        this.mCurrentPos = position;
        mType = getItemType(mDatas.get(position));
        if(mType instanceof Integer){
            int aa= (int) mType;
            if (aa == -1)
                return -1;
        }

        return mUtil.getIntType(mType);
    }

     class CommonVH extends RecyclerView.ViewHolder {

        IAdapterHolder item;
        int position;

        public CommonVH(Context context, ViewGroup parent, IAdapterHolder item) {
            super(LayoutInflater.from(context).inflate(item.getLayoutResId(CommonAp.this), parent, false));
            this.item = item;
            this.item.bindViews(itemView, position);
            this.item.setViews();
        }
    }

    /**
     * =======接口实现start========
     */

    @Override
    public void setData(@NonNull List<T> data) {
        this.mDatas = data;
    }

    @Override
    public List<T> getData() {
        return mDatas;
    }

    @Override
    public String getItemType(T t) {
        return null;  //默认-1
    }


    @NonNull
    @Override
    public Object getConvertedData(T data, Object type) {
        return data;
    }

    @Override
    public int getCurrentPosition() {
        return mCurrentPos;
    }
    /**=======接口实现end========*/
}
