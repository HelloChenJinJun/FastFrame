package com.example.fastframe.mvp;

import com.example.commonlibrary.bean.BaseBean;
import com.example.commonlibrary.mvp.model.DefaultModel;
import com.example.commonlibrary.mvp.presenter.RxBasePresenter;
import com.example.commonlibrary.mvp.view.IView;
import com.example.fastframe.base.ConstantUtil;
import com.example.fastframe.bean.BookBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称:    FrameDemo
 * 创建人:      陈锦军
 * 创建时间:    2019-05-27     10:26
 */
public class BookListPresenter extends RxBasePresenter<IView<BaseBean>, DefaultModel> {
    private int page=0;

    public BookListPresenter(IView<BaseBean> iView, DefaultModel baseModel) {
        super(iView, baseModel);
    }

    public void getData(boolean isRefresh) {
        if (isRefresh) {
            page=0;
            iView.showLoading(null);
        }
        page++;
//        模拟获取数据
        List<BookBean> list=new ArrayList<>();
        BaseBean baseBean=new BaseBean();
        for (int i = 0; i < 10; i++) {
            list.add(new BookBean());
        }
        baseBean.setData(list);
        baseBean.setType(ConstantUtil.BASE_TYPE_BOOK_LIST);
        iView.updateData(baseBean);
        iView.hideLoading();
    }
}
