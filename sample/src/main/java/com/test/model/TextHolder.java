package com.test.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.luoxiong.interfac.IAdapterHolder;
import com.test.R;
import com.test.domain.ModelData;

/**
 * ================================================
 * 作    者：罗雄
 * 创建日期：2016/11/8
 * 描    述：
 * ================================================
 */
public class TextHolder implements IAdapterHolder<ModelData> {
    TextView tvName;

    @Override
    public int getLayoutResId(RecyclerView.Adapter adapter) {
        return R.layout.item_textview;
    }

//    @Override
//    public int getLayoutResId() {
//        return R.layout.item_textview;
//    }

    @Override
    public void bindViews(View root, int position) {
        tvName = (TextView) root.findViewById(R.id.tv_name);
    }

    @Override
    public void setViews() {

    }

    @Override
    public void handleData(ModelData bean, int position) {
        tvName.setText(bean.content+"  "+position);
    }
}
