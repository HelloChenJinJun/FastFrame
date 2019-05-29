package com.example.commonlibrary.baseadapter.empty;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 项目名称:    FastFrame
 * 创建人:      陈锦军
 * 创建时间:    2019-05-28     10:59
 */
public abstract class BaseEmptyView extends FrameLayout implements IEmptyView {


    public static final int STATUS_LOADING = 0;
    public static final int STATUS_ERROR=1;
    public static final int STATUS_NO_NET = 2;
    public static final int STATUS_NO_DATA = 3;
    public static final int STATUS_HIDE = 4;
    private int currentStatus;
    private View loadingView,noDataView,noNetView,errorView;



    public BaseEmptyView(@NonNull Context context) {
        this(context,null);
    }

    public BaseEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        if (getErrorViewLayoutId() != 0) {
            errorView=layoutInflater.inflate(getErrorViewLayoutId(),this,false);
        }
        if (getNoDataViewLayoutId() != 0) {
            noDataView= layoutInflater.inflate(getNoDataViewLayoutId(),this,false);
        }
        if (getNoNetViewLayoutId() != 0) {
            noNetView= layoutInflater.inflate(getNoNetViewLayoutId(),this,false);
        }
        if (getLoadingViewLayoutId() != 0) {
            loadingView=layoutInflater.inflate(getLoadingViewLayoutId(),this,false);
        }
        addView(errorView);
        addView(noDataView);
        addView(noNetView);
        addView(loadingView);
        currentStatus=STATUS_LOADING;
        updateStatus(STATUS_HIDE);
    }

    public void updateStatus(int status) {
        if (currentStatus==status)return;
        currentStatus=status;
        if (status==STATUS_LOADING){
            updateViewStatus(loadingView,VISIBLE);
            updateViewStatus(errorView,GONE);
            updateViewStatus(noDataView,GONE);
            updateViewStatus(noNetView,GONE);
        } else if (status == STATUS_NO_NET) {
            updateViewStatus(loadingView,GONE);
            updateViewStatus(errorView,GONE);
            updateViewStatus(noDataView,GONE);
            updateViewStatus(noNetView,VISIBLE);
        } else if (status == STATUS_NO_DATA) {
            updateViewStatus(loadingView,GONE);
            updateViewStatus(errorView,GONE);
            updateViewStatus(noDataView,VISIBLE);
            updateViewStatus(noNetView,GONE);
        } else if (status == STATUS_ERROR) {
            updateViewStatus(loadingView,GONE);
            updateViewStatus(errorView,VISIBLE);
            updateViewStatus(noDataView,GONE);
            updateViewStatus(noNetView,GONE);
        }else {
            updateViewStatus(loadingView,GONE);
            updateViewStatus(errorView,GONE);
            updateViewStatus(noDataView,GONE);
            updateViewStatus(noNetView,GONE);
        }
    }


    private void updateViewStatus(View view ,int visibility){
        if (view != null) {
            view.setVisibility(visibility);
        }
    }


    public int getStatus() {
        return currentStatus;
    }
}
