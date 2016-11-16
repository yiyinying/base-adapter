package com.luoxiong.interfac;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.luoxiong.adapter.BaseRecyAP;
import com.luoxiong.tools.LogUtils;
import com.luoxiong.view.RecyclerViewParent;


/**
 * ================================================
 * 作    者：罗雄
 * 创建日期：2016/10/27
 * 描    述：
 * ================================================
 */
public class RecyclerViewListener extends RecyclerView.OnScrollListener
        implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView mRecyclerView;
    private RecyclerViewParent mRecyParent;
    private BaseRecyAP mAdapter;

    /**
     * =============Item触摸监听开始===============
     */
    public RecyclerViewListener(RecyclerViewParent recyclerView) {
        this.mRecyParent = recyclerView;
        this.mRecyclerView = recyclerView.getRecyclerView();
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(),
                new ItemTouchHelperGestureListener());
    }

    /**
     * 获取适配器的引用
     *
     * @param ap
     */
    public void setAp(RecyclerView.Adapter ap) {
        mAdapter = (BaseRecyAP) ap;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder vh = mRecyclerView.getChildViewHolder(child);
             //   mRecyParent.onItemClick(vh,1);
//                onItemClick(vh);
            }
            return true;
        }

        //长点击事件，本例不需要不处理
        //@Override
        //public void onLongPress(MotionEvent e) {
        //    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        //    if (child!=null) {
        //        RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
        //        onItemLongClick(vh);
        //    }
        //}
    }

    /**
     * =============Item触摸监听结束===============
     */
    //public abstract void onItemClick(RecyclerView.ViewHolder vh);
    //public abstract void onItemLongClick(RecyclerView.ViewHolder vh);
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastCompleteItem = 0;
        int lastVisibleItem = 0;
        int firstItem = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = mAdapter.getItemCount();
        //如果布局管理器属于网格布局
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gm = ((GridLayoutManager) layoutManager);
            firstItem = gm.findFirstCompletelyVisibleItemPosition();
            //从当前布局管理器中找到最后完整可见的position
            lastVisibleItem = gm.findLastVisibleItemPosition();
            lastCompleteItem = gm.findLastCompletelyVisibleItemPosition();
            
            //如果属于线性布局
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = ((LinearLayoutManager) layoutManager);
            firstItem = lm.findFirstCompletelyVisibleItemPosition();
            lastVisibleItem = lm.findLastVisibleItemPosition();
            lastCompleteItem = lm.findLastCompletelyVisibleItemPosition();
            //如果属于网状布局
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
            lastCompleteItem = findMax(lastPositions);
            firstItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
        }
        int finishPos=totalItemCount-1;

        //最后底部加载成功的位置，加上头和集合的位置
        int currPos= mAdapter.getListSize()+ mAdapter.getHeaderCount()+mAdapter.getFooterCount();
//        LogUtils.d("RecyclerListener。。。onScrolled（）:"+lastVisibleItem+"  totalItemCount:"+totalItemCount
//        +"   lastCompleteItem:"+lastCompleteItem+"   currPos:"+currPos);

//        LogUtils.d("lastVisibleItem:"+lastVisibleItem+"  totalItemCount:"+currPos
//        +(mAdapter.getLoadMoreEnable()
//                && !mRecyParent.isRefresh()
//                && (lastVisibleItem == currPos)
//                && !mAdapter.getisLoadMoreing()
//                && (dx > 0 || dy > 0)));
        //如果第0个可见的  或者 firstItem==-1 ， 可以下拉刷新
        if (firstItem == 0 || firstItem == RecyclerView.NO_POSITION) {
            if (mRecyParent.getPullRefreshEnable()) {
                mRecyParent.setRefreshEnable(true);
            }else {
                mRecyParent.setRefreshEnable(false);
            }
        }else if (false){

            //切记、这里不能设置，因为getItemViewType在这之前就已经走了,只有pos<size/2设置才有效
            // mAdapter.setLoadMoreEnable(false);


            /**
             * 1、如果支持加载更多
             * 2、不是处于正在刷新中..
             * 3、可见条目是最后一个
             * 4、不是处于正在加载中..
             * 5、x或y滑动距离大于0
             */
        } else if (mAdapter.getLoadMoreEnable()
                && !mRecyParent.getIsRefreshing()
                && (currPos == lastCompleteItem)
                && !mAdapter.getisLoadMoreing()
                && (dx > 0 || dy > 0)) {
            LogUtils.d("到底了。。。"+mRecyParent.getIsRefreshing());
//            mAdapter.setLoadMoreState(mAdapter.LOADING_STATE);
//            //设置正在加载中。。
//            mAdapter.setisLoadMoreing(true);
//            //执行加载更多
//            mRecyParent.onLoadMore();
        }

    }

    //To find the maximum value in the array
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
