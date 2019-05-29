package com.example.commonlibrary.baseadapter.adapter;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.commonlibrary.baseadapter.empty.BaseEmptyView;
import com.example.commonlibrary.baseadapter.empty.EmptyView;
import com.example.commonlibrary.baseadapter.foot.OnLoadMoreListener;
import com.example.commonlibrary.baseadapter.viewholder.BaseWrappedViewHolder;
import com.example.commonlibrary.rxbus.RxBusManager;
import com.example.commonlibrary.rxbus.event.SkinUpdateEvent;
import com.example.commonlibrary.utils.CommonLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈锦军 on 16/3/12.
 */
public abstract class BaseRecyclerAdapter<T, K extends BaseWrappedViewHolder> extends RecyclerView.Adapter<K> {


    protected static final int HEADER = Integer.MIN_VALUE + 1;
    protected static final int FOOTER = Integer.MAX_VALUE - 1;
    protected static final int LOAD_MORE_FOOTER = Integer.MAX_VALUE;
    protected static final int EMPTY = Integer.MIN_VALUE + 2;



    private LinearLayout mLoadMoreFooterContainer;

    private LinearLayout mHeaderContainer;

    private LinearLayout mFooterContainer;

    private FrameLayout mEmptyContainer;


    protected List<T> data;

    private int layoutId;
    protected RecyclerView display;
    private boolean headerAndEmptyEnable=false;
    private OnLoadMoreListener onLoadMoreListener;
    private int preLoadCount=4;

    public BaseRecyclerAdapter(List<T> data, int layoutId) {
        this.data = data == null ? new ArrayList<T>() : data;
        if (getLayoutId() != 0) {
            this.layoutId = getLayoutId();
        }
        if (layoutId != 0)
            this.layoutId = layoutId;
        if (isApplySkin()) {
            RxBusManager.getInstance().registerEvent(SkinUpdateEvent.class, skinUpdateEvent -> notifyDataSetChanged());
        }
    }

    protected boolean isApplySkin() {
        return false;
    }


    protected abstract int getLayoutId();


    public BaseRecyclerAdapter(List<T> data) {
        this(data, 0);
    }

    public BaseRecyclerAdapter(int layoutId) {
        this(null, layoutId);
    }

    public BaseRecyclerAdapter() {
        this(null);
    }

    private LayoutInflater mLayoutInflater;


    private void ensureLoadMoreFooterContainer(Context context) {
        if (mLoadMoreFooterContainer == null) {
            mLoadMoreFooterContainer = new LinearLayout(context);
            mLoadMoreFooterContainer.setOrientation(LinearLayout.VERTICAL);
            mLoadMoreFooterContainer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void ensureHeaderViewContainer(Context context) {
        if (mHeaderContainer == null) {
            mHeaderContainer = new LinearLayout(context);
            mHeaderContainer.setOrientation(LinearLayout.VERTICAL);
            mHeaderContainer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    private void ensureEmptyViewContainer(Context context) {
        if (mEmptyContainer == null) {
            mEmptyContainer = new FrameLayout(context);
            mEmptyContainer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void ensureFooterViewContainer(Context context) {
        if (mFooterContainer == null) {
            mFooterContainer = new LinearLayout(context);
            mFooterContainer.setOrientation(LinearLayout.VERTICAL);
            mFooterContainer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }



    public void addFooterView(View footerView) {
        if (footerView==null)return;
        ensureFooterViewContainer(footerView.getContext());
        mFooterContainer.addView(footerView);
    }

    public void addHeaderView(View headerView){
        if (headerView==null)return;
        ensureHeaderViewContainer(headerView.getContext());
        mHeaderContainer.addView(headerView);
    }

    public void setEmptyView(BaseEmptyView emptyView){
        if (emptyView==null)return;
        ensureEmptyViewContainer(emptyView.getContext());
        mEmptyContainer.removeAllViews();
        mEmptyContainer.addView(emptyView);
    }

    public void updateEmptyView(int status){
        if (hasEmptyView()) {
            ((BaseEmptyView) mEmptyContainer.getChildAt(0))
                    .updateStatus(status);
        }
    }



    public void setLoadMoreView(BaseEmptyView loadMoreView,OnLoadMoreListener onLoadMoreListener){
        if (loadMoreView==null)return;
        ensureLoadMoreFooterContainer(loadMoreView.getContext());
        mLoadMoreFooterContainer.removeAllViews();
        mLoadMoreFooterContainer.addView(loadMoreView);
        this.onLoadMoreListener=onLoadMoreListener;
    }









    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        this.display = recyclerView;
        ensureEmptyViewContainer(display.getContext());
        ensureFooterViewContainer(display.getContext());
        ensureHeaderViewContainer(display.getContext());
        ensureLoadMoreFooterContainer(display.getContext());
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    BaseRecyclerAdapter wrapperAdapter = (BaseRecyclerAdapter) recyclerView.getAdapter();
                    if (isFullSpanType(wrapperAdapter.getItemViewType(position))) {
                        return gridLayoutManager.getSpanCount();
                    } else if (spanSizeLookup != null) {
                        return spanSizeLookup.getSpanSize(position - getItemUpCount());
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(K holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getAdapterPosition();
        int type = getItemViewType(position);
        if (isFullSpanType(type)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }


    private int marginSize = 0;


    public int getMarginSize() {
        return marginSize;
    }

    public void setMarginSize(int marginSize) {
        this.marginSize = marginSize;
    }

    private boolean isFullSpanType(int type) {
        return  type == HEADER || type == FOOTER || type == LOAD_MORE_FOOTER || type == EMPTY;
    }

    @Override
    public int getItemCount() {
        if (hasEmptyView()) {
            int count=1;
            if (headerAndEmptyEnable) {
                count++;
            }
            return count;
        }
        int size=data.size() + getItemUpCount() + getItemDownCount();
        CommonLogger.e("getItemCount"+size);
        return size;
    }

    private int getItemDownCount() {
        int position = 0;
        if (hasFootView()) {
            position++;
        }
        if (hasMoreLoadView()) {
            position++;
        }
        return position;
    }




    private boolean hasHeaderView() {
        return mHeaderContainer != null && mHeaderContainer.getChildCount() > 0;
    }


    private boolean hasFootView() {
        return mFooterContainer != null && mFooterContainer.getChildCount() > 0;
    }


    private boolean hasMoreLoadView() {
        return mLoadMoreFooterContainer != null && mLoadMoreFooterContainer.getChildCount() > 0;
    }

    private boolean hasEmptyView(){
        return mEmptyContainer!=null&&mEmptyContainer.getChildCount()>0&&(data==null||data.size()==0);
    }





    public int getItemUpCount() {
        int position = 0;
        if (hasHeaderView()) {
            position++;
        }
        return position;
    }




    @Override
    public int getItemViewType(int position) {
        if (hasEmptyView()) {
            boolean header=headerAndEmptyEnable&&hasHeaderView();
            switch (position){
                case 0:
                    if (header) {
                        return HEADER;
                    }else {
                        return EMPTY;
                    }
                case 1:
                    if (header) {
                        return EMPTY;
                    }else {
                        return FOOTER;
                    }
                case 2:
                    return FOOTER;
            }
            return EMPTY;
        }

        if (position<getHeaderViewCount()) {
            return HEADER;
        }else {
            int size = data.size();
            int adjustPosition=position-getHeaderViewCount();
            if (adjustPosition < size) {
                return getDefaultItemViewType(adjustPosition);
            }else {
                adjustPosition=adjustPosition-size;
                if (adjustPosition < getFooterViewCount()) {
                    return FOOTER;
                }else {
                    CommonLogger.e("getItemViewType"+"more");
                    return LOAD_MORE_FOOTER;
                }
            }
        }
    }


    public int getHeaderViewCount(){
        return hasHeaderView()?1:0;
    }

    private int getFooterViewCount(){
        return hasFootView()?1:0;
    }



    protected int getDefaultItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(K holder, int position) {
        CommonLogger.e("onBindViewHolder");
        loadMore(position);
       switch (holder.getItemViewType()){
           case HEADER:
               break;
           case EMPTY:
               updateEmptyView(BaseEmptyView.STATUS_NO_DATA);
               break;
           case FOOTER:
               break;
           case LOAD_MORE_FOOTER:
               CommonLogger.e("onBindViewHolder"+"more");
               break;
               default:
                   Animator[] itemAnimator = getItemAnimator(holder);
                   if (itemAnimator != null) {
                       for (Animator anim : itemAnimator) {
                           anim.setInterpolator(interpolator);
                           anim.setDuration(duration).start();
                       }
                   }
                   convert(holder,data.get(position - getHeaderViewCount()));
                   break;
       }
    }

    private void loadMore(int position) {
        if (!hasMoreLoadView())return;
        if (position<getItemCount()-preLoadCount)return;
        if (((BaseEmptyView) mLoadMoreFooterContainer.getChildAt(0)).getStatus()==BaseEmptyView.STATUS_LOADING)return;
        updateLoadMoreStatus(EmptyView.STATUS_LOADING);
        if (onLoadMoreListener != null) {
            onLoadMoreListener.loadMore();
        }
    }

    public void updateLoadMoreStatus(int status){
        if (!hasMoreLoadView())return;
        BaseEmptyView loadMoreView = (BaseEmptyView) mLoadMoreFooterContainer.getChildAt(0);
        loadMoreView.updateStatus(status);
    }


    protected Animator[] getItemAnimator(BaseWrappedViewHolder holder) {
        return null;
    }


    private int duration = 300;
    private LinearInterpolator interpolator = new LinearInterpolator();


    protected abstract void convert(K holder, T data);

    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        K k = createBaseViewHolder(getLayoutFromViewType(parent, viewType));
        return k;
    }


    private View getLayoutFromViewType(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return mHeaderContainer;
            case FOOTER:
                return mFooterContainer;
            case LOAD_MORE_FOOTER:
                CommonLogger.e("onCreateViewHolder"+"more");
                return mLoadMoreFooterContainer;
            case EMPTY:
                return mEmptyContainer;
            default:
                return mLayoutInflater.inflate(layoutId, parent, false);
        }
    }

    K createBaseViewHolder(View view) {
        Class adapterClass = getClass();
        Class resultClass = null;
        while (resultClass == null && adapterClass != null) {
            resultClass = getInstancedGenericKClass(adapterClass);
            adapterClass = adapterClass.getSuperclass();
        }
        K k = createGenericKInstance(resultClass, view);
        if (k != null) {
            k.bindAdapter(this);
        }
        return k != null ? k : (K) new BaseWrappedViewHolder(view);
    }

    private K createGenericKInstance(Class aClass, View view) {
        try {
            //                构造函数
            Constructor constructor;
            //                获取修饰符
            String modifier = Modifier.toString(aClass.getModifiers());
            String name = aClass.getName();

            //                内部类并且不是静态类
            if (name.contains("$") && !modifier.contains("static")) {
                constructor = aClass.getDeclaredConstructor(getClass(), View.class);
                return (K) constructor.newInstance(this, view);
            }
            constructor = aClass.getDeclaredConstructor(View.class);
            return (K) constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class getInstancedGenericKClass(Class adapterClass) {
        Type type = adapterClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type type1 :
                    types) {
                if (type1 instanceof Class) {
                    Class class1 = (Class) type1;
                    if (BaseWrappedViewHolder.class.isAssignableFrom(class1)) {
                        return class1;
                    }
                }
            }
        }
        return null;
    }


    public void addData(int position, List<T> newData) {
        if (newData == null || newData.size() == 0) {
            return;
        }
        List<T> temp = new ArrayList<>();
        List<T> copyData = new ArrayList<>(data);
        for (T data :
                newData) {
            for (T item :
                    copyData) {
                if (data.equals(item)) {
                    temp.add(data);
                }
            }
        }
        if (temp.size() > 0) {
            int index;
            for (T item :
                    temp) {
                index = data.indexOf(item);
                data.set(index, item);
                newData.remove(item);
            }
            notifyDataSetChanged();
            addData(position, newData);
        } else {
            data.addAll(position, newData);
            notifyItemRangeInserted(position + getItemUpCount(), newData.size());
        }
    }




    public void addData(T newData) {
        addData(data.size(), newData);
    }


    public void addData(int position, T newData) {
        if (newData != null) {
            if (!data.contains(newData)) {
                data.add(position, newData);
                notifyItemInserted(position + getItemUpCount());
            } else {
                int index = data.indexOf(newData);
                data.set(index, newData);
                notifyItemChanged(index + getItemUpCount());
            }
        }
    }

    public void addData(List<T> newData) {
        addData(data.size(), newData);
    }

    public List<T> getData() {
        return data;
    }

    public void clearAllData() {
        data.clear();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public T getData(int position) {
        if (position < 0 || position > data.size() - 1) {
            return null;
        } else {
            return data.get(position);
        }
    }

    public T removeData(int position) {
        if (position >= 0 && position < data.size()) {
            T t = data.remove(position);
            notifyItemRemoved(position + getItemUpCount());
            return t;
        }
        return null;
    }

    public T removeData(T data) {
        return removeData(this.data.indexOf(data));
    }


    protected RecyclerView.LayoutManager layoutManager;

    public void bindManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;

    }


    @Override
    public void onViewRecycled(K holder) {
        super.onViewRecycled(holder);
        holder.clear();
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void refreshData(List<T> list) {
       clear();
        addData(list);
        if (layoutManager != null) {
            layoutManager.scrollToPosition(0);
        }
    }

    public void removeEndData(int size) {
        if (data.size() >= size) {
            int allSize = data.size();
            for (int i = 1; i <= size; i++) {
                data.remove(allSize - i);
            }
            notifyItemRangeRemoved(allSize - size, size);
//
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);

        boolean onItemLongClick(int position, View view);

        boolean onItemChildLongClick(int position, View view, int id);

        void onItemChildClick(int position, View view, int id);
    }


    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }
}
