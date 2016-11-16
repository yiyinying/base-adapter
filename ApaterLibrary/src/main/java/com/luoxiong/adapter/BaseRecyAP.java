package com.luoxiong.adapter;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.luoxiong.R;
import com.luoxiong.interfac.IAdapter;
import com.luoxiong.interfac.IAdapterHolder;
import com.luoxiong.tools.ItemTypeUtil;
import com.luoxiong.tools.LogUtils;
import com.luoxiong.view.RecyclerViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者： luoxiong
 * 创建时间：2016/10/30 10:31
 * 描述: 适配器的基类
 */
public abstract class BaseRecyAP<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements IAdapter<T> {

    //顶部Item类型
    public static final int HEAD_TYPE = 0x00000111; //273
    //空布局类型
    public static final int EMPTY_TYPE = 0x00000222; //546
    //普通Item类型
    //public static final int NORMAL_TYPE = 0x00000333; //819
    //底部Item类型
    public static final int FOOT_TYPE = 0x00000444;  //1092
    //最后的加载更多Item
    public static final int LOADMORE_TYPE = 0x00000555; //1365

    /**
     * 显示加载中
     */
    public static final String LOADING_STATE = "loading_state";
    /**
     * 加载失败
     */
    public static final String FAIL_STATE = "fail_state";
    /**
     * 隐藏加载更多
     */
    public static final String HIDE_STATE = "hide_state";

    /**
     * 页脚加载更多的状态
     */
    private String loadMoreState;

    /**
     * holder类型
     */
    private Object mHolderType;

    /**
     * 是否显示空的View
     */
    private boolean hasShownEmptyView = false;

    //头布局
    private View mHeadView;
    //空布局
    private View mEmptyView;
    //底部布局
    private View mFootView;


    //自定义加载更多布局
    private View mLoadMoreView;

    /**
     * 通过setEmptyView传递过来
     */
    private RecyclerView mRecyclerView;
    private RecyclerViewParent mRecyParent;


    public List<T> mDatas;
    private Context mContext;
    /**
     * 产品总数、如果列表数量大于产品总数 就不显示加载更多了
     */
    private int mItemTotal = 0;
    /**
     * 当服务器不返回总数的时候，根据自己每次请求的集合大小判断。
     * <p>
     * 如果请求的是20条数据，但服务器返回了19条，此时就可以判断服务器没有数据了，隐藏加载更多
     */
    private int mDefaultPageSize = 0;

    private LoadMoreVH footHolder;

    public BaseRecyAP(Context context, List<T> datas, RecyclerViewParent recyParent) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.mRecyParent = recyParent;
        this.mRecyclerView = recyParent.getRecyclerView();
        this.mContext = context;
        this.mDatas = datas;
        mUtil = new ItemTypeUtil();
        //initObserver(datas);
        //registerApDataObserver();
    }

    /**
     * 初始化观察者
     */
    protected void initObserver(List<T> datas) {
        /**
         * 判断是否开启了Databinding
         */
        // if (DataBindingJudgement.SUPPORT_DATABINDING && datas instanceof ObservableList) {
        if (true) {
            /**
             * 数据源 添加一个观察者 ， 观察者就是List列表
             *
             * list列表检测到了数据变化，就notify对应的方法
             */

            // delete走的流程：  4、10、6、2、8
            // add走的流程：     3、9、6、2、8

            //我写的 add ：      3、6、2
            //我写的 delete ：  3、6、2
            ((ObservableList<T>) datas).addOnListChangedCallback(
                    new ObservableList.OnListChangedCallback<ObservableList<T>>() {
                        @Override
                        public void onChanged(ObservableList<T> sender) {
                            LogUtils.d("111111。。。。。onChanged()。。。。。");
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
                            LogUtils.d("22222222。。。。。onItemRangeChanged()。。。。。");
                            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
                        }

                        @Override
                        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
                            LogUtils.d("33333333。。。。。onItemRangeInserted()。。。。。");
                            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
//                            if (hasShownEmptyView && getItemCount() != 0) {
//                                notifyItemRemoved(getHeaderCount());
//                                hasShownEmptyView = false;
//                            }
//                            notifyItemRangeInserted(positionStart, itemCount);
                            notifyChange(sender, positionStart + getHeaderCount());
                        }

                        @Override
                        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
                            LogUtils.d("4444444444。。。。。onItemRangeRemoved()。。。。。");
                            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
                            notifyChange(sender, positionStart + getHeaderCount());
                        }

                        @Override
                        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
                            LogUtils.d("555555555。。。。。onItemRangeMoved()。。。。。");
                            notifyChange(sender, Math.min(fromPosition, toPosition));
                        }

                        private void notifyChange(ObservableList<T> sender, int start) {
                            LogUtils.d("6666666666。。。。。notifyChange()。。。。。");
                            onItemRangeChanged(sender, start, getItemCount() - start);
                        }
                    });
        }

    }

    /**
     * 注册适配器观察者
     */
    public void registerApDataObserver() {
        /**
         * RecyclerView.Adapter也添加了观察者，是 RecyclerView 的成员变量
         *
         * 这里有两个观察者，一个是下面这个匿名类，一个是RecyclerView自带的观察者
         *
         * private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver();
         */
        //必须要创建一个的Adapter，再添加观察者
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                LogUtils.d("77777777。。。。。onChanged()。。。。。");
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                LogUtils.d("88888888。。。。。onItemRangeChanged()。。。。。");
                notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                LogUtils.d("999999999999。。。。。onItemRangeInserted()。。。。。");
                //               notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
//                if (hasShownEmptyView && getItemCount() != 0) {
//                    notifyItemRemoved(getHeaderCount());
//                    hasShownEmptyView = false;
//                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                LogUtils.d("1010101010。。。。。onItemRangeRemoved()。。。。。");
                notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
                /*if (getFooterCount() != 0) {
                    if (positionStart + getFooterCount() + 1 == getItemCount()) { // last one
                        notifyDataSetChanged();
                    }
                }*/
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                // FIXME: 2015/11/23 还没支持"多个"item的转移的操作
            }
        });
    }

    public int getHeaderCount() {
        return mHeadView == null ? 0 : 1;
    }

    public int getFooterCount() {
        return mFootView == null ? 0 : 1;
    }

    public int getEmptyCount() {
        return mEmptyView == null ? 0 : 1;
    }

    public int geLoadMoreCount() {
        return getLoadMoreEnable() ? 1 : 0;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEAD_TYPE:
                return new SimpleViewHolder(mHeadView);
            case EMPTY_TYPE:
                return new SimpleViewHolder(mEmptyView);
            case FOOT_TYPE:
                return new SimpleViewHolder(mFootView);
            case LOADMORE_TYPE:
                View loadMoreView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.lx_base_loadmore, parent, false);
                return new LoadMoreVH(loadMoreView);
            default:
                if (createItem(mHolderType) == null) {
                    throw new NullPointerException("Holder Can't be null,You must create a holder!");
                }
                return new BaseNormalVH(parent.getContext(), parent, createItem(mHolderType));
        }
    }

    //273 head,546 empty
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        // LogUtils.d("onBindViewHolder。。。2222.。。。" + position+"  type："+type);


        //默认的普通类型
        if (type != HEAD_TYPE && type != FOOT_TYPE && type != EMPTY_TYPE && type != LOADMORE_TYPE) {
            // if (type == NORMAL_TYPE) {
            BaseNormalVH holder = (BaseNormalVH) viewHolder;
            if (mHeadView != null)
                position--;
            holder.pos = position;
            //if (!mDatas.isEmpty()) {
            holder.item.handleData(getConvertedData(mDatas.get(position), mHolderType), position);

            // }
            //空布局处理
        } else if (type == EMPTY_TYPE) {
            LogUtils.d("6666。。。。显示空布局了。。222。。。");
            ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams();
            int headerHeight = mHeadView != null ? mHeadView.getHeight() : 0;
            int footHeight = mFootView != null ? mFootView.getHeight() : 0;
            params.height = mRecyclerView.getHeight() - headerHeight - footHeight;
        }

        //加载更多类型
        else if (type == LOADMORE_TYPE && getLoadMoreEnable() && loadMoreState != null) {
            LogUtils.d("BaseRecyAp。。。11显示加载中。。。。" + loadMoreState);

            footHolder = (LoadMoreVH) viewHolder;

            if (TextUtils.equals(loadMoreState, LOADING_STATE)) {
                //加载中
                showLoading();
            } else if (TextUtils.equals(loadMoreState, FAIL_STATE)) {
                //失败，会显示refresh文字，点击可以重新加载
                showLoadFail();
            } else {
                //隐藏所有布局
                showLoadHide();
                //loadMoreState=null;
            }
        }
    }


    public int getListSize() {
        return mDatas == null ? 0 : mDatas.size();
    }

    //标记第一次不能出现空布局
    private int index = 1;

    @Override
    public int getItemCount() {
        int count = getListSize();
        int offset = 0;
        if (mHeadView != null) {
            offset++;
        }
        if (mFootView != null) {
            offset++;
        }
        if (mEmptyView != null) {
            if (count == 0) {
                offset++;
                hasShownEmptyView = true;
            } else {
                hasShownEmptyView = false;
            }
        }
        if (getLoadMoreEnable()) {
            offset++;
        }
        // LogUtils.d("1111。。。getItemCount()。。。。"+(offset + count)+"  count:"+count+"  offset："+offset);
        return offset + count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView != null && position == 0) {
            return HEAD_TYPE;
        } else if (mFootView != null && position == getListSize() + getHeaderCount()) {
            return FOOT_TYPE;
        } else if (mEmptyView != null && getListSize() == 0 && position == getHeaderCount()) {
            return EMPTY_TYPE;
        } else if (getLoadMoreEnable()
                && position == getListSize() + getHeaderCount()
                + getFooterCount()) {
            LogUtils.d("getItemViewType。。。。111.。。。。LOADMORE_TYPE。。");
            return LOADMORE_TYPE;
        } else {
//            if (getMyItemType(position) == -1) {
//                return NORMAL_TYPE;
//            }
            return getMyItemType(getMinusHeadPos(position));
        }
    }

    public int getMinusHeadPos(int pos) {
        return pos - getHeaderCount();
    }

    public int getPlusHeadPos(int pos) {
        return pos + getHeaderCount();
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
                    return getItemViewType(position) == HEAD_TYPE || getItemViewType(position) == LOADMORE_TYPE
                            || getItemViewType(position) == FOOT_TYPE
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 添加一条数据
     *
     * @param pos
     * @param data
     */
    public void insertData(int pos, @NonNull T data) {
        //当没有添加头执行的方法
//        mDatas.add(pos, data);
//        notifyItemInserted(pos );
//        getRecyclerView().scrollToPosition(pos );
//        // notifyItemRangeChanged(pos, getListSize() - pos);
//        // 加入如下代码保证position的位置正确性
//        if (pos != getListSize() - 1) {
//            notifyItemRangeChanged(pos, getListSize() - pos);
//        }
        if (pos < 0) {
            return;
        }
        mDatas.add(pos, data);
        notifyItemInserted(pos + getHeaderCount() + getEmptyCount());
        getRecyclerView().getLayoutManager().scrollToPosition(pos + getHeaderCount());
        // notifyItemRangeChanged(pos, getListSize() - pos);
        LogUtils.d("insertData。。pos:" + pos + "  size:" + getListSize() + "  " + (pos != getListSize() - 1));
        // 加入如下代码保证position的位置正确性
        if (pos != getListSize() - 1) {
            notifyItemRangeChanged(pos, getListSize() - pos);
        }
    }

    /**
     * 添加一条数据
     *
     * @param oldPos
     */
    public void removeData(int oldPos) {
//        if (getListSize() <= oldPos || mDatas.isEmpty()) {
//            return;
//        }
//        mDatas.remove(oldPos);
//        notifyItemRemoved(oldPos);
//        getRecyclerView().scrollToPosition(pos );
//        if (oldPos != getListSize() - 1) {
//            notifyItemRangeChanged(pos, getListSize() - pos);
//        }

        if (getListSize() <= oldPos || mDatas.isEmpty() || oldPos < 0) {
            return;
        }
        mDatas.remove(oldPos);
        //获取加上head的位置
        int newPos = getPlusHeadPos(oldPos);
        LogUtils.d("removeData size:" + getListSize() + " oldPos:" + oldPos + " newPos:" + newPos + "  " + (oldPos != getListSize()));
        notifyItemRemoved(newPos);
        getRecyclerView().getLayoutManager().scrollToPosition(getMinusHeadPos(newPos));

        if (getListSize() == 0) {
            return;
        }
        if (oldPos != getListSize()) {
            notifyItemRangeChanged(newPos, getListSize() - oldPos - geLoadMoreCount());
        }
    }

    /**
     * 根据Item总数，判断是否支持加载更多
     *
     * @param loadData
     * @param isLoadMore true:是加载更多，false：刷新
     * @param itemTotal  Itme总数，用来判断是否有加载更多。默认0不支持加载更多
     */

    public void addDatas(List<T> loadData, boolean isLoadMore, @Nullable int itemTotal) {
        if (!isLoadMore) {
            mDatas.clear();
        }
        if (loadData != null && !loadData.isEmpty()) {
            this.mLoadDataSize = loadData.size();
            if (itemTotal > 0) {
                this.mItemTotal = itemTotal;
                mLoadMoreEnable = true;
            } else {
                // mLoadMoreEnable = false;
            }
            List<T> datas = loadData;
            LogUtils.d("addDatas11。。。。" + datas.size() + "  mItemTotal：" + mItemTotal + "  mLoadMoreEnable:" + mLoadMoreEnable);
            mDatas.addAll(datas);
            notifyChange(isLoadMore);
        } else {
            mRecyParent.setRefreshing(false);
        }
    }

    /**
     * 用来设置加载更多刷新时只刷新加载更多的部分
     */
    private int mLoadDataSize;

    /**
     * 根据pageSize 判断是否支持加载更多
     * <p>
     * pageSize如果是20，loadData大小如果为19，则说明没有数据了，不显示加载更多
     */
    public void addDatas(@Nullable int pageSize, List<T> loadData, boolean isLoadMore) {
        if (!isLoadMore) {
            mDatas.clear();
        }
        if (loadData != null && !loadData.isEmpty()) {
            this.mLoadDataSize = loadData.size();
            List<T> dataList = loadData;
            if (pageSize > 0) {
                //如果大于加载的集合大小，说明没有更多数据了
                if (pageSize != dataList.size()) {
                    mLoadMoreEnable = false;
                } else {
                    this.mDefaultPageSize = pageSize;
                    mLoadMoreEnable = true;
                }
            } else {
                // mLoadMoreEnable = false;
            }
            LogUtils.d("addDatas22。。。size。" + dataList.size() + "  pageSize：" + pageSize + " mLoadMoreEnable:" + mLoadMoreEnable);
            mDatas.addAll(dataList);
            notifyChange(isLoadMore);
        } else {
            mRecyParent.setRefreshing(false);
        }
    }


    /**
     * 正常Item ViewHolder
     */
    public class BaseNormalVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View root;
        public SparseArray<View> viewSparseArray; //HashMap<Integer, E>的封装
        public int pos;
        public IAdapterHolder item;

        public BaseNormalVH(Context context, ViewGroup parent, IAdapterHolder item) {
            super(LayoutInflater.from(context).
                    inflate(item.getLayoutResId(BaseRecyAP.this), parent, false));
            this.item = item;
            this.item.bindViews(itemView, pos);
            this.item.setViews();
            itemView.setOnClickListener(this);
        }

        /**
         * 通过ViewHolder查找view然后存至SparseArray
         */
        public <T extends View> T findView(int viewId) {
            if (viewSparseArray == null) {
                viewSparseArray = new SparseArray<>();
            }
            View view = viewSparseArray.get(viewId);
            if (view == null) {
                view = root.findViewById(viewId);
                viewSparseArray.put(viewId, view);
            }
            return (T) root.findViewById(viewId);
        }

        @Override
        public void onClick(View v) {
            mRecyParent.onItemClick(this, pos);
        }
    }

    /**
     * ==============IAdapter 接口实现 start=================
     */
    private int currentPos;
    private ItemTypeUtil mUtil;

    @Override
    public void setData(@NonNull List<T> datas) {
        mDatas = datas;
    }

    @Override
    public List<T> getData() {
        return mDatas;
    }

    @Override
    public String getItemType(T t) {
        return null; // default
    }


    public int getMyItemType(int position) {
        this.currentPos = position;
        mHolderType = getItemType(mDatas.get(position));
        return mUtil.getIntType(mHolderType);
    }

    @NonNull
    @Override
    public abstract IAdapterHolder createItem(Object type);

    @NonNull
    @Override
    public Object getConvertedData(T data, Object type) {
        return data;
    }

    @Override
    public int getCurrentPosition() {
        return currentPos;
    }

    /**
     * ==============IAdapter 接口实现 end=================
     */

    /**
     * Keep it simple!
     */
    private static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }


    private View mLoadingContaint;
    private View mLoadFailContaint;

    public class LoadMoreVH extends RecyclerView.ViewHolder {
        //loadmore最外层容器
        public FrameLayout loadMoreRootContain;
        //加载中容器
        public View loadingRoot;
        //加载失败容器
        public View loadFailRoot;

        public LoadMoreVH(View itemView) {
            super(itemView);
            loadMoreRootContain = (FrameLayout) itemView.findViewById(R.id.load_more_root);
            if (mLoadMoreView != null) {
                loadMoreRootContain.removeAllViews();
                loadMoreRootContain.addView(mLoadMoreView);
                loadingRoot = mLoadingContaint;
                loadFailRoot = mLoadFailContaint;
            } else {
                loadingRoot = itemView.findViewById(R.id.loading_root);
                loadFailRoot = itemView.findViewById(R.id.load_fail_root);
            }
            loadFailRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d("底部按钮点击。。。" + getItemCount());
                    setLoadMoreState(LOADING_STATE);
                    showLoading();
                }
            });
        }
    }

    /**
     * 显示加载中的布局
     */
    public void showLoading() {
        //mRecyParent.onLoadMore();
        LogUtils.d("showLoading。。22222。。");
        mLoadMoreListener.onLoadMore();
        footHolder.loadMoreRootContain.setVisibility(View.VISIBLE);
        footHolder.loadingRoot.setVisibility(View.VISIBLE);
        footHolder.loadFailRoot.setVisibility(View.GONE);
    }

    /**
     * 显示加载失败的布局
     */
    public void showLoadFail() {
        footHolder.loadMoreRootContain.setVisibility(View.VISIBLE);
        footHolder.loadFailRoot.setVisibility(View.VISIBLE);
        footHolder.loadingRoot.setVisibility(View.GONE);
    }

    /**
     * 隐藏加载更多的布局
     */
    public void showLoadHide() {
        footHolder.loadMoreRootContain.setVisibility(View.GONE);
    }


    public boolean isHeader(int position) {
        return position == 0;
    }

    public boolean isFooter(int position) {
        return position == (mDatas.size() + 1);
    }

    /**
     * 设置头和底部合并列显示
     */
    private void setSpanSizeLookup(final RecyclerView.Adapter adapter, final GridLayoutManager layoutManager) {
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                final int type = adapter.getItemViewType(position);
                if (type == HEAD_TYPE || type == FOOT_TYPE || type == EMPTY_TYPE) {
                    return layoutManager.getSpanCount();
                } else {
                    // 如果是普通的，那么就保持原样1列
                    return 1;
                }
            }
        });
    }


    /**
     * 添加一个头布局
     */
    public void setHeadView(View headView) {
        this.mHeadView = headView;
    }

    /**
     * 添加一个脚布局
     */
    public void setFootView(View footView) {
        this.mFootView = footView;
    }

    /**
     * 创建加载更多的布局
     */

    public void setLoadMoreView(@NonNull View view, @NonNull View loadingContain, @NonNull View loadFailContain) {
        this.mLoadMoreView = view;
        this.mLoadingContaint = loadingContain;
        this.mLoadFailContaint = loadFailContain;
    }

    /**
     * 设置空状态的view
     *
     * @param emptyViewParent 如果需要EmptyView的高度占满RecyclerView，则此参数必填；
     *                        传null，则保持EmptyView的自有高度
     */
    public void setEmptyView(@Nullable View emptyView, @Nullable RecyclerView emptyViewParent) {
        if (this.mEmptyView == emptyView) {
            return;
        }
        this.mEmptyView = emptyView;
        // this.mRecyclerView = emptyViewParent;
    }

    /**
     * ==================监听刷新状态start====================
     */

    //默认不可以加载更多
    private boolean mLoadMoreEnable = true;

    //是否处于加载更多中..
    private boolean isLoadMoreing = false;


    /**
     * 下拉刷新和加载更多完成
     * isRefreshOrLoadMore : true 是刷新
     * isRefreshOrLoadMore : false 是加载更多
     */

    public void notifyChange(boolean isLoadMore) {
        //如果不是加载更多，上次的数据大小为0

        //设置加载更多状态
        judgeLoadMoreEnable();
        //设置不是出于加载更多中。。。
        isLoadMoreing = false;
        //如果是加载更多，设置加载更多完成
        if (isLoadMore) {
            mRecyParent.setloadMoreComplete();
            notifyItemRangeChanged(getListSize() + getHeaderCount()
                    - mLoadDataSize, mLoadDataSize);
        } else {
            mRecyParent.setRefreshComplete();
            notifyDataSetChanged();
        }
    }

    /**
     * 设置产品总数，来判断是否显示加载更多
     */
    public void setItemTotal(int sum) {
        this.mItemTotal = sum;
        //想调用次方法，说明是想使用加载更多的
        //mLoadMoreEnable = true;
    }


    public void setPageSize(int pageSize) {
        this.mDefaultPageSize = pageSize;
        // mLoadMoreEnable = true;
        //judgeLoadMoreEnable();
    }

    /**
     * 根据产品总数、判断能否加载更多
     * <p>
     * 总数：100
     * 集合大小：100  就不显示加载更多
     */
    public void judgeLoadMoreEnable() {
        if (!mLoadMoreEnable) {
            return;
        }
        //根据mProductSum判断
        if (mItemTotal != 0) {
            LogUtils.d("没有数据了。。隐藏加载更多状态。。。9999.。。。" + mItemTotal + "  "
                    + mDatas.size());
            if (mItemTotal > mDatas.size()) {
                loadMoreState = LOADING_STATE;
            } else {
                //loadMoreState = HIDE_STATE;
                // loadMoreState = null;
                mLoadMoreEnable = false;
                mItemTotal = 0;
                //不能在这里判断，因为当下拉刷新，调用setProductSum方法就会走这，此时集合数量还是原来的数量，
                // 然后再调用Notify  mLoadMoreEnable=false就不起作用了
                //mLoadMoreEnable = false;
            }
        }
        //根据pageSize 判断  在上面就判断了
//        else {
//            LogUtils.d("没有数据了。。隐藏加载更多状态。。。66666.。。。" +
//                    (mDefaultPageSize > mDatas.size() - lastDataSize));
//
//            if (mDefaultPageSize > mDatas.size() - lastDataSize) {
//                loadMoreState = HIDE_STATE;
//
//            } else {
//                loadMoreState = LOADING_STATE;
//            }
//        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 设置加载更多状态
     */
    public void setLoadMoreState(String footState) {
        this.loadMoreState = footState;
        if (TextUtils.equals(footState, FAIL_STATE))
            notifyItemRangeChanged(getListSize() + getHeaderCount(), 1);
    }

    /**
     * 获取加载更多的状态
     */
    public String getLoadMoreState() {
        return loadMoreState;
    }

    /**
     * 设置是否处于加载更多中..
     */
    public void setisLoadMoreing(boolean isLoadMoreing) {
        this.isLoadMoreing = isLoadMoreing;
    }

    /**
     * 是否正在加载中？
     */
    public boolean getisLoadMoreing() {
        return isLoadMoreing;
    }


    /**
     * 获取是否支持加载更多
     */
    public boolean getLoadMoreEnable() {
        return mLoadMoreListener != null && getListSize() > 0 && mLoadMoreEnable;
    }

    /**
     * 设置是否支持加载更多
     */
    public void setLoadMoreEnable(boolean flag) {
        this.mLoadMoreEnable = flag;
    }


    private RequestLoadMoreListener mLoadMoreListener;

    public interface RequestLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(RequestLoadMoreListener l) {
        this.mLoadMoreListener = l;
        loadMoreState = LOADING_STATE;
    }
}
