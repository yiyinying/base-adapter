package com.test.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

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
    public class ButtonHolder implements IAdapterHolder<ModelData> {
        Button bt;

        @Override
        public int getLayoutResId(RecyclerView.Adapter adapter) {
            return R.layout.item_button;
        }

//        @Override
//        public int getLayoutResId() {
//            return R.layout.item_button;
//        }

        @Override
        public void bindViews(View root, int position) {
            bt = (Button) root.findViewById(R.id.bt_item);
        }

        @Override
        public void setViews() {

        }

        @Override
        public void handleData(ModelData bean, int position) {
            bt.setText(bean.content+"  "+position);
        }
    }
