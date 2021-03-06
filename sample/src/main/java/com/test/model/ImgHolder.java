package com.test.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.luoxiong.interfac.IAdapterHolder;
import com.test.R;
import com.test.domain.ModelData;

/**
 * ================================================
 * 作    者：罗雄
 * 创建日期：2016/11/13
 * 描    述：
 * ================================================
 */

public class ImgHolder implements IAdapterHolder<ModelData> {
    ImageView img;
   // TextView tvName;

    @Override
    public int getLayoutResId(RecyclerView.Adapter adapter) {
        return R.layout.item_img;
    }

//    @Override
//    public int getLayoutResId() {
//        return R.layout.item_img;
//    }

    @Override
    public void bindViews(View root, int position) {
        img = (ImageView) root.findViewById(R.id.iv_item);
        //tvName = (TextView) root.findViewById(R.id.tv_name);
    }

    @Override
    public void setViews() {

    }

    @Override
    public void handleData(ModelData bean, int position) {
        Integer integer = Integer.valueOf(bean.content);
        img.setImageResource(integer);
       // tvName.setText("image: " + position);
    }
}
