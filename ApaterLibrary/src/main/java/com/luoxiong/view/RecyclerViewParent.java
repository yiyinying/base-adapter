package com.luoxiong.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.luoxiong.R;
import com.luoxiong.adapter.BaseRecyAP;
import com.luoxiong.adapter.CommonAp;
import com.luoxiong.interfac.RecyclerViewListener;


/**
 * ================================================
 * 作    者：lx
 * 创建日期：2016/10/27
 * 描    述：
 * ================================================
 */
public class RecyclerViewParent extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerViewListener mRecyListener;

    public RecyclerViewParent(Context context) {
        this(context, null);
    }

    public RecyclerViewParent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //init();
        initView();
    }

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //private View mFooterView;
    //private LinearLayout mEmptyViewContainer;
    private BaseRecyAP mAdapter;

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void initView() {
        //填充布局
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.lx_base_recyclerview_layout, this, false);
//        mFooterView = view.findViewById(R.id.footerView);
//        mEmptyViewContainer = (LinearLayout) view.findViewById(R.id.emptyView);

        // mFooterView.setVisibility(View.GONE);
        //  mEmptyViewContainer.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_SwipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.c1);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_RecyclerView);
        //设置右侧滚动条可见
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //提高效率
        mRecyclerView.setHasFixedSize(true);
        //设置动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //添加滚动监听和触摸监听

        mRecyListener = new RecyclerViewListener(this);
        mRecyclerView.addOnItemTouchListener(mRecyListener);
        mRecyclerView.addOnScrollListener(mRecyListener);

        this.addView(view);
    }

    public RecyclerViewListener getRecyclerViewListener() {
        return mRecyListener;
    }

    /**
     * LinearLayoutManager
     */
    public void setLinearLayoutManager(boolean isVertical) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        if (isVertical) {
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        } else {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * GridLayoutManager
     */

    public void setGridLayoutManager(int spanCount) {
        GridLayoutManager gridManager = null;
        gridManager = new GridLayoutManager(mContext, spanCount);
        gridManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridManager);
        if (mAdapter != null) {
            final GridLayoutManager finalGridManager = gridManager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return mAdapter.getItemViewType(position) == BaseRecyAP.HEAD_TYPE ||
                            mAdapter.getItemViewType(position) == BaseRecyAP.LOADMORE_TYPE
                            || mAdapter.getItemViewType(position) == BaseRecyAP.FOOT_TYPE
                            ? finalGridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * StaggeredGridLayoutManager
     */

    public void setStaggeredGridLayout(int spanCount) {
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }

    /**
     * 获取布局管理器
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    /**
     * 设置Item的动画
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    /**
     * 添加Item分割线，指定位置
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        mRecyclerView.addItemDecoration(decor, index);
    }

    /**
     * 添加Item分割线，不指定位置
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    /**
     * 设置适配器
     */
    public void setAdapter(RecyclerView.Adapter adapter) {

        if (adapter instanceof CommonAp) {
            mRecyclerView.setAdapter(adapter);
        } else {
            this.mAdapter = (BaseRecyAP) adapter;
            mRecyclerView.setAdapter(adapter);
            mRecyListener.setAp(adapter);
        }
    }

    /**
     * Item点击
     */
    public void onItemClick(RecyclerView.ViewHolder vh, int postion) {
        if (mListener != null) {
            mListener.onItemClick(postion);
        }
    }

//    @Override
//    public void onRefresh() {
//        if (!mRecyclerViewParent.isRefreshing()) {
//            mRecyclerViewParent.setIsRefresh(true);
//            mRecyclerViewParent.onRefresh();
//        }
//    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        if (!getIsRefreshing()) {
            if (mListener != null) {
                isRefreshing = true;
                mListener.onItemRefresh();
            }
        }
    }
    /**
     * 执行加载更多
     */
//    public void onLoadMore() {
//        if (mListener != null) {
//            LogUtils.d("执行加载更多逻辑。。。666666.。。。。。");
//            mListener.onLoadMore();
//        }
//    }

    /**
     * 刷新完成
     */
    public void setRefreshComplete() {
        isRefreshing = false;
        setRefreshing(false);
    }

    //是否支持下拉刷新
    private boolean mRefreshEnable = true;

    public boolean getPullRefreshEnable() {
        return mRefreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        mRefreshEnable = refreshEnable;
        mSwipeRefreshLayout.setEnabled(refreshEnable);
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mAdapter.setLoadMoreEnable(loadMoreEnable);
    }

    //设置是否正在刷新中..
    private boolean isRefreshing = false;

    public boolean getIsRefreshing() {
        return isRefreshing;
    }


    /**
     * 设置加载更多完成
     */
    public void setloadMoreComplete() {
        isRefreshing = false;
        setRefreshing(false);
    }

    /**
     * 设置是否正在刷新中....
     */
    public void setRefreshing(final boolean refreshing) {
        if (mRefreshEnable) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    /**
     * 接口回调监听
     */
    private OnRecyclerViewListener mListener;


    public interface OnRecyclerViewListener {
        void onItemClick(int position);

        void onItemRefresh();
        //void onLoadMore();
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener listener) {
        this.mListener = listener;
    }
}
