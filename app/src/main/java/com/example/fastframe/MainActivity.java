package com.example.fastframe;

import android.view.View;

import com.example.commonlibrary.BaseActivity;
import com.example.commonlibrary.base.ToolBarOption;
import com.example.fastframe.mvp.BookListActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {





    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        findViewById(R.id.tv_activity_main_book_on_detail)
                .setOnClickListener(this);


    }

    @Override
    protected void initData() {
    }

    @Override
    protected ToolBarOption getToolBar() {
        ToolBarOption toolBarOption=new ToolBarOption();
        toolBarOption.setTitle("首页");
        toolBarOption.setNeedNavigation(false);
        return toolBarOption;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_activity_main_book_on_detail:
                BookListActivity.start(this);
                break;
            default:
                break;
        }

    }

    @Override
    public void updateData(Object o) {

    }
}
