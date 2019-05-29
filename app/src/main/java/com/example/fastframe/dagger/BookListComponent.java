package com.example.fastframe.dagger;

import com.example.commonlibrary.dagger.component.AppComponent;
import com.example.commonlibrary.dagger.component.BaseComponent;
import com.example.commonlibrary.dagger.scope.PerActivity;
import com.example.fastframe.mvp.BookListActivity;

import dagger.Component;

/**
 * 项目名称:    FrameDemo
 * 创建人:      陈锦军
 * 创建时间:    2019-05-27     10:50
 */
@PerActivity
@Component(dependencies = AppComponent.class,modules = BookListModule.class)
public interface BookListComponent extends BaseComponent<BookListActivity> {

}
