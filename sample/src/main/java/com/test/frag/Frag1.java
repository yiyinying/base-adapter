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
import com.test.domain.ModelData;
import com.test.model.ButtonHolder;
import com.test.model.ImgHolder;
import com.test.model.TextHolder;
import com.test.tools.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：lx
 * 创建日期：2016/11/8
 * 描    述：有下拉有加载更多
 * ================================================
 */
public class Frag1 extends BaseFrag implements View.OnClickListener {
    RecyclerViewParent mRecyclerViewParent;
    List<ModelData> mDatas = new ArrayList<>();
    MyAdapter1 mAdapter1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag1_layout, container, false);
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
                //设置Item总数
                mAdapter1.setItemTotal(50);
                List<ModelData> datas = DataManager.loadModelData(9);
                mAdapter1.addDatas(datas, false, 0);
            }
        }, 1100);
        initAp();
        //设置空布局
        setEmptyLayout();
    }




    private void initAp() {
        mRecyclerViewParent.setRefreshing(true);
        mAdapter1 = new MyAdapter1(getContext(), mDatas, mRecyclerViewParent);


        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_layout, mRecyclerViewParent, false);
        mAdapter1.setHeadView(headView);
        setloadmorel();

        mRecyclerViewParent.setAdapter(mAdapter1);
        setOnRefresh();

    }

    private void setEmptyLayout() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.empty_layout, mRecyclerViewParent, false);
        mAdapter1.setEmptyView(v);
    }


    private void setOnRefresh() {
        mRecyclerViewParent.setOnRecyclerViewListener(new RecyclerViewParent.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.d("你点的是：" + position);
            }

            @Override
            public void onItemRefresh() {
               mRecyclerViewParent.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       List<ModelData> datas = DataManager.loadModelData(15);
                       mAdapter1.addDatas(datas.size(), datas, false);
                   }
               },1000);
            }
        });
    }

    private void setloadmorel() {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.loadmore_layout,mRecyclerViewParent,false);
        View loadingContain=view.findViewById(R.id.loading_ll);
        View loadFail=view.findViewById(R.id.load_fail_fl);
        mAdapter1.setLoadMoreView(view,loadingContain,loadFail);
        mAdapter1.setOnLoadMoreListener(new BaseRecyAP.RequestLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerViewParent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<ModelData> datas = DataManager.loadModelData(5);
                        mAdapter1.addDatas(datas, true, 0);
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                int pos = mDatas.size();
                LogUtils.d("insertData  pos:" + pos);
                ModelData model = new ModelData();
                model.type = "text";
                model.content = "add ";
                mAdapter1.insertData(pos, model);
                break;
            case R.id.delete:
                int pos2 = mDatas.size() - 1;
                mAdapter1.removeData(pos2);
                break;
        }
    }

    class MyAdapter1 extends BaseRecyAP<ModelData> {
        public MyAdapter1(Context context, List<ModelData> datas,
                          RecyclerViewParent recyParent) {
            super(context, datas, recyParent);
        }

        @Override
        public String getItemType(ModelData bean) {
            return bean.type;
            //return "text";
        }

        @Override
        public IAdapterHolder createItem(Object type) {
            switch (((String) type)) {
                case "text":
                    return new TextHolder();
                case "button":
                    return new ButtonHolder();
                case "image":
                    return new ImgHolder();
                default:
                    throw new IllegalArgumentException("不合法的type");
            }
        }

    }
}
