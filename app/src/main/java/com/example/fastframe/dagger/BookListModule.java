package com.example.fastframe.dagger;

import com.example.commonlibrary.mvp.model.DefaultModel;
import com.example.fastframe.adapter.BookListAdapter;
import com.example.fastframe.mvp.BookListActivity;
import com.example.fastframe.mvp.BookListPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 项目名称:    FrameDemo
 * 创建人:      陈锦军
 * 创建时间:    2019-05-27     10:51
 */
@Module
public class BookListModule {
    private BookListActivity bookListActivity;

    public BookListModule(BookListActivity bookListActivity) {
        this.bookListActivity = bookListActivity;
    }

    @Provides
    public BookListAdapter provideAdapter(){
        return new BookListAdapter();
    }

    @Provides
    public BookListPresenter providePresenter(DefaultModel defaultModel){
        return new BookListPresenter(bookListActivity,defaultModel);
    }
}
