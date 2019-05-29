package com.example.commonlibrary.baseadapter.foot;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.commonlibrary.R;
import com.example.commonlibrary.baseadapter.empty.BaseEmptyView;

/**
 * 项目名称:    FastFrame
 * 创建人:      陈锦军
 * 创建时间:    2019-05-28     14:22
 */
public class LoadMoreView extends BaseEmptyView {
    public LoadMoreView(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getErrorViewLayoutId() {
        return R.layout.layout_superrecyclerview_load_more_footer_error_view;
    }

    @Override
    public int getNoDataViewLayoutId() {
        return R.layout.layout_superrecyclerview_load_more_footer_the_end_view;
    }

    @Override
    public int getNoNetViewLayoutId() {
        return R.layout.layout_superrecyclerview_load_more_footer_error_view;
    }

    @Override
    public int getLoadingViewLayoutId() {
        return R.layout.layout_superrecyclerview_load_more_footer_loading_view;
    }
}
