package com.test.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luoxiong.adapter.BaseRecyAP;
import com.luoxiong.interfac.IAdapterHolder;
import com.luoxiong.tools.LogUtils;
import com.luoxiong.view.RecyclerViewParent;
import com.test.R;
import com.test.domain.TextBean;
import com.test.model.TextBeanHolder;
import com.test.tools.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：lx
 * 创建日期：2016/11/8
 * 描    述：无下拉无加载更多
 * ================================================
 */
public class Frag3 extends BaseFrag implements View.OnClickListener {
    RecyclerViewParent mRecyclerViewParent;
    List<TextBean> mDatas = new ArrayList<>();
    MyAdapter1 mAdapter1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag3_layout, container, false);

        mRecyclerViewParent = (RecyclerViewParent) view.findViewById(R.id.id_RecyclerViewParent);
        mRecyclerViewParent.setLinearLayoutManager(true);
        view.findViewById(R.id.add).setOnClickListener(this);
        view.findViewById(R.id.delete).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerViewParent.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<TextBean> datas = DataManager.loadTextData(9);
                mAdapter1.addDatas(datas, false, 0);
            }
        }, 1500);
        initAp();
        //设置空布局
        setEmptyLayout();
    }


    private void initAp() {
        //禁止下拉刷新
        mRecyclerViewParent.setRefreshEnable(false);
        mAdapter1 = new MyAdapter1(getContext(), mDatas, mRecyclerViewParent);
        mRecyclerViewParent.setAdapter(mAdapter1);
        mRecyclerViewParent.setOnRecyclerViewListener(new RecyclerViewParent.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemRefresh() {

            }
        });
    }

    private void setEmptyLayout() {
        LogUtils.d("Frag1。。。setEmptyLayout()。。。。"+mAdapter1.getEmptyCount());

        View vvv = LayoutInflater.from(getActivity()).inflate(R.layout.empty_layout, mRecyclerViewParent, false);
        mAdapter1.setEmptyView(vvv);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                int pos = mDatas.size();
                LogUtils.d("insertData  pos:" + pos);
                TextBean bean=new TextBean();
                bean.setAge(mDatas.size());
                bean.setName("add");
                mAdapter1.insertData(pos, bean);

                break;
            case R.id.delete:
                int pos2 = mDatas.size() - 1;
                mAdapter1.removeData(pos2);
                break;
        }
    }

    class MyAdapter1 extends BaseRecyAP<TextBean> {
        public MyAdapter1(Context context, List<TextBean> datas,
                          RecyclerViewParent recyParent) {
            super(context, datas, recyParent);
        }

        @Override
        public IAdapterHolder createItem(Object type) {
            return new TextBeanHolder();
        }
    }
}
