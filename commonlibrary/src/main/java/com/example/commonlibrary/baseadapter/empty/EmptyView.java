package com.example.commonlibrary.baseadapter.empty;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.commonlibrary.R;

/**
 * 项目名称:    FastFrame
 * 创建人:      陈锦军
 * 创建时间:    2019-05-28     10:58
 */
public class EmptyView extends BaseEmptyView {




    public EmptyView(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getErrorViewLayoutId() {
        return R.layout.view_empty_no_net;
    }

    @Override
    public int getNoDataViewLayoutId() {
        return R.layout.view_empty_no_net;
    }

    @Override
    public int getNoNetViewLayoutId() {
        return R.layout.view_empty_no_net;
    }

    @Override
    public int getLoadingViewLayoutId() {
        return R.layout.view_empty_loading;
    }
}
