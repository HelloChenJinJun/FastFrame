package com.example.commonlibrary.dagger.component;

/**
 * 项目名称:    FrameDemo
 * 创建人:      陈锦军
 * 创建时间:    2019-05-27     10:52
 */
public interface BaseComponent<T> {
    public void inject(T t);
}
