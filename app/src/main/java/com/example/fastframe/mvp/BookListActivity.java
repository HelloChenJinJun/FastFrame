package com.example.fastframe.mvp;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.commonlibrary.BaseActivity;
import com.example.commonlibrary.base.ToolBarOption;
import com.example.commonlibrary.baseadapter.empty.BaseEmptyView;
import com.example.commonlibrary.baseadapter.empty.EmptyView;
import com.example.commonlibrary.baseadapter.foot.LoadMoreView;
import com.example.commonlibrary.baseadapter.foot.OnLoadMoreListener;
import com.example.commonlibrary.bean.BaseBean;
import com.example.commonlibrary.utils.CommonLogger;
import com.example.fastframe.R;
import com.example.fastframe.adapter.BookListAdapter;
import com.example.fastframe.base.ConstantUtil;
import com.example.fastframe.bean.BookBean;
import com.example.fastframe.dagger.BookListModule;
import com.example.fastframe.dagger.DaggerBookListComponent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * 项目名称:    FrameDemo
 * 创建人:      陈锦军
 * 创建时间:    2019-05-27     10:26
 */
public class BookListActivity extends BaseActivity<BaseBean,BookListPresenter> implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
    private RecyclerView      display;
    private SwipeRefreshLayout refresh;

    @Inject
    BookListAdapter bookListAdapter;

    public static void start(Activity activity) {
        Intent intent=new Intent(activity,BookListActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected int getContentLayout() {
        return R.layout.activity_book_list;
    }

    @Override
    protected void initView() {
        refresh=findViewById(R.id.refresh_activity_book_list_refresh);
        display=findViewById(R.id.rcv_activity_book_list_display);
        refresh.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        DaggerBookListComponent.builder().appComponent(getAppComponent())
                .bookListModule(new BookListModule(this))
                .build().inject(this);
        display.setLayoutManager(new LinearLayoutManager(this));
        bookListAdapter.setEmptyView(new EmptyView(this));
        bookListAdapter.setLoadMoreView(new LoadMoreView(this),this);
//        bookListAdapter.addHeaderView(getHeaderView());
//        bookListAdapter.addFooterView(getHeaderView());
        display.setAdapter(bookListAdapter);
        runOnUiThread(() -> presenter.getData(true));
    }


    @Override
    protected ToolBarOption getToolBar() {
        ToolBarOption toolBarOption=new ToolBarOption();
        toolBarOption.setTitle("上架详情");
        toolBarOption.setNeedNavigation(true);
        return toolBarOption;
    }

    private View getHeaderView() {
        LoadMoreView loadMoreView=new LoadMoreView(this);
        loadMoreView.updateStatus(BaseEmptyView.STATUS_LOADING);
        return loadMoreView;
    }

    @Override
    public void showLoading(String loadMessage) {
//        super.showLoading(loadMessage);
        refresh.setRefreshing(true);
    }

    @Override
    public void showError(String errorMsg) {
        super.showError(errorMsg);
        refresh.setRefreshing(false);
    }

    @Override
    public void updateData(BaseBean baseBean) {
        if (baseBean.getType() == ConstantUtil.BASE_TYPE_BOOK_LIST) {
            bookListAdapter.refreshData((List<BookBean>) baseBean.getData());
        }

    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        refresh.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        display.postDelayed(new Runnable() {
            @Override
            public void run() {
//                presenter.getData(true);

                bookListAdapter.refreshData(null);
                refresh.setRefreshing(false);

            }
        },2500);

    }

    @Override
    public void loadMore() {
        CommonLogger.e("loadMore11");
        display.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<BookBean>  list=new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    list.add(new BookBean());
                }
                bookListAdapter.updateLoadMoreStatus(BaseEmptyView.STATUS_NO_DATA);
            }
        },2000);
    }
}
