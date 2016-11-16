package com.test;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.luoxiong.tools.LogUtils;

import java.util.List;

/**
 * 创建者： lx
 * 创建时间：2016/9/21 10:31
 * 描述: 适配器的基类
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEAD_TYPE = 0;
    public static final int NORMAL_TYPE = 1;
    public static final int FOOT_TYPE = 2;

    private View mHeadView;
    private View mFootView;

    public List<T> mDatas;

    private int mItemViewLayoutId;
    public Context mContext;
    /**
     * 显示加载中
     */
    public static final String FOOT_TYPE_LOAD = "load";
    /**
     * 加载失败
     */
    public static final String FOOT_TYPE_FAIL = "fail";
    /**
     * 隐藏加载更多
     */
    public static final String FOOT_TYPE_HIDE = "hide";

    public String footViewState;
    public void setFootViewState(String footState) {
        this.footViewState = footState;
    }

    public String getFootViewState() {
        return footViewState;
    }

    public BaseRecyclerAdapter(Context context, List<T> datas, int itemViewLayoutId) {
        this.mContext = context;
        this.mDatas = datas;
        this.mItemViewLayoutId = itemViewLayoutId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case HEAD_TYPE:
                v = mHeadView;
                return new HeadViewHolder(v);
            case NORMAL_TYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(mItemViewLayoutId, parent, false);
                return new BaseViewHolder(v);

        }
        return new BaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

    }


    @Override
    public int getItemCount() {
        if (mHeadView == null && getFooterCount() == 0) {
            return mDatas.size() ;
        } else if (mHeadView !=null && getFooterCount() != 0) {
            return mDatas.size() + 2;
        }else{
            return mDatas.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView == null) {
            if (position == getItemCount() - 1 && getFooterCount() != 0) {
                return FOOT_TYPE;
            } else {
                return NORMAL_TYPE;
            }
        } else {
            if (position == 0) {
                return HEAD_TYPE;
            } else {
                if (position == getItemCount() - 1 && getFooterCount() != 0) {
                    return FOOT_TYPE;
                } else {
                    return NORMAL_TYPE;
                }
            }
        }
    }


    /**
     * 当前位置是header的位置，那么该item占据2个单元格，正常情况下占据1个单元格
     * 如果你是下拉刷新的Recy。那么要手动调用这个方法
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == HEAD_TYPE
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }


    /**
     * 正常Item ViewHolder
     */
    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View root;
        public SparseArray<View> viewSparseArray; //HashMap<Integer, E>的封装
        public int position;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.root = itemView;
            viewSparseArray = new SparseArray<>();
            if (mListener != null) {
                itemView.setOnClickListener(this);
            }
            initItemListener(this,position);
        }

        /**
         * 通过ViewHolder查找view然后存至SparseArray
         */
        public <T extends View> T findView(int viewId) {
            View view = viewSparseArray.get(viewId);
            if (view == null) {
                view = root.findViewById(viewId);
                viewSparseArray.put(viewId, view);
            }
            return (T) root.findViewById(viewId);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, position);
        }
    }

    /**
     * 子类用来重写Item里面的widget点击事件
     */
    public void initItemListener(BaseViewHolder holder,int position) {

    }

    /**
     * 头部ViewHolder
     */
    public class HeadViewHolder extends RecyclerView.ViewHolder {
        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }



    /**
     * 清空集合更新adapter
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 给Adapter添加数据
     */
    public void addAll(List<T> data) {
        if (data != null && !data.isEmpty()) {
            this.mDatas.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 接口回调监听
     */
    private OnRecyclerViewClickListener mListener;

    public interface OnRecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 底部接口回调
     */
    protected OnFootRecyClickListener mFootListener;

    public interface OnFootRecyClickListener {
        void onFootClick(View v);
    }

    public void setOnFootRecyClickListener(OnFootRecyClickListener listener) {
        this.mFootListener = listener;
    }

    /**
     * 子类必须重写——初始化数据
     */
    public abstract void initData(BaseViewHolder holder, T data, int position);


    /**
     * 如果存在加载更多的布局，子类必须重写。。。
     * @return
     */
    public abstract int getFooterCount();


    public int getHeadViewCount() {
        return mHeadView == null ? 0 : 1;
    }



    /**
     * 添加一个脚布局
     */
    public void setFootView(View footView) {
        this.mFootView = footView;
    }

    /**
     * 添加一个头布局
     */
    public void setHeadView(View headView) {
        this.mHeadView = headView;
    }
}
