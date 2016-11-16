package com.luoxiong.interfac;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * ================================================
 * 作    者：罗雄
 * 创建日期：2016/10/27
 * 描    述： RecyclerView 列表的每个Holder
 * ================================================
 */
public interface IAdapterHolder<T> {



    /**
     * @return item布局文件
     */
    @LayoutRes
    int getLayoutResId(RecyclerView.Adapter adapter);

//    @LayoutRes
//    int getLayoutResId();

    /**
     * 初始化views , position获取不到
     */
    void bindViews(final View root, int position);

    /**
     * 设置view的参数(点击事件)
     */
    void setViews();

    /**
     * 根据数据来设置item的内部views
     *
     * @param t    数据list内部的model
     * @param position 当前adapter调用item的位置
     */
    void handleData(T t, int position);

}
