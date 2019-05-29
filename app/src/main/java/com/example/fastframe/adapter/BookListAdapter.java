package com.example.fastframe.adapter;

import com.example.commonlibrary.baseadapter.adapter.BaseRecyclerAdapter;
import com.example.commonlibrary.baseadapter.viewholder.BaseWrappedViewHolder;
import com.example.fastframe.R;
import com.example.fastframe.bean.BookBean;

/**
 * 项目名称:    FrameDemo
 * 创建人:      陈锦军
 * 创建时间:    2019-05-27     10:30
 */
public class BookListAdapter extends BaseRecyclerAdapter<BookBean, BaseWrappedViewHolder> {
    @Override
    protected int getLayoutId() {
        return R.layout.item_activity_book_list;
    }

    @Override
    protected void convert(BaseWrappedViewHolder holder, BookBean data) {
        holder.setText(R.id.tv_item_activity_book_list_order,(holder
                .getAdapterPosition()+1)+"");

    }
}
