package com.example.commonlibrary;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.commonlibrary.base.ToolBarOption;
import com.example.commonlibrary.dagger.component.AppComponent;
import com.example.commonlibrary.mvp.presenter.BasePresenter;
import com.example.commonlibrary.mvp.view.IView;
import com.example.commonlibrary.utils.StatusBarUtil;
import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;

/**
 * 项目名称:    Cugappplat
 * 创建人:        陈锦军
 * 创建时间:    2017/4/3      14:24
 * QQ:             1981367757
 */

public abstract class BaseFragment<T, P extends BasePresenter> extends Fragment implements IView<T> {

    /**
     * 采用懒加载
     */
    private View root;
    private boolean hasInit = false;
    private View headerLayout;
    private ImageView icon;
    private TextView right;
    private TextView title;
    private ImageView rightImage;
    protected ImageView back;


    @Nullable
    @Inject
    protected P presenter;
    private CompositeDisposable compositeDisposable;
    private ViewGroup bg;


    protected boolean needRefreshData() {
        return false;
    }


    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }


    protected AppComponent getAppComponent() {
        return BaseApplication.getAppComponent();
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            ToolBarOption toolBarOption=getToolBarOption();
            if (toolBarOption!=null) {
                bg = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.content_layout_ll, null);
                headerLayout = LayoutInflater.from(getContext()).inflate(R.layout.header_layout, null);
                ((TextView) headerLayout.findViewById(R.id.tv_header_layout_title)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                bg.addView(headerLayout);
                if (getContentLayout() != 0) {
                    LayoutInflater.from(getContext()).inflate(getContentLayout(), bg);
                }
                root = bg;
                icon = headerLayout.findViewById(R.id.riv_header_layout_icon);
                title = headerLayout.findViewById(R.id.tv_header_layout_title);
                right = headerLayout.findViewById(R.id.tv_header_layout_right);
                back = headerLayout.findViewById(R.id.iv_header_layout_back);
                rightImage = headerLayout.findViewById(R.id.iv_header_layout_right);
                rightImage.setVisibility(View.GONE);
                right.setVisibility(View.VISIBLE);
                ((AppCompatActivity) getActivity()).setSupportActionBar(headerLayout.findViewById(R.id.toolbar));
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
                setToolBar(toolBarOption);
            } else {
                root = LayoutInflater.from(getActivity()).inflate(getContentLayout(), null);
            }
            if (root.getParent() != null) {
                ((ViewGroup) root.getParent()).removeView(root);
            }
            if (container != null) {
                container.addView(root);
            }
            initBaseView();
            initData();
            if (needStatusPadding()) {
                StatusBarUtil.setStatusPadding(getPaddingView());
            }
        }
        if (root.getParent() != null) {
            ((ViewGroup) root.getParent()).removeView(root);
        }
        return root;
    }

    protected ToolBarOption getToolBarOption() {
        return null;
    }


    private View getPaddingView() {
        if (needStatusPadding()) {
            return headerLayout != null ? headerLayout : root;
        }
        return null;
    }

    protected boolean needStatusPadding() {
        return true;
    }

    private void initBaseView() {
        initView();
    }






    protected <V extends View> V findViewById(int id) {
        if (root != null) {
            return root.findViewById(id);
        }
        return null;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (root != null && getUserVisibleHint() && !hasInit) {
            hasInit = true;
            updateView();
        }
    }


    /**
     * 视图真正可见的时候才调用
     */

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (root != null && isVisibleToUser) {
            if (!hasInit) {
                hasInit = true;
                updateView();
            } else if (needRefreshData()) {
                updateView();
            }
        }

    }





    private boolean hasRecord = false;



    protected abstract int getContentLayout();


    protected abstract void initView();

    protected abstract void initData();

    protected abstract void updateView();


    private void setToolBar(ToolBarOption option) {
        if (option==null) {
            return;
        }
        if (option.getBgColor() != 0) {
            headerLayout.setBackgroundColor(option.getBgColor());
        }
        if (option.getCustomView() != null) {
            ViewGroup container = headerLayout.findViewById(R.id.toolbar);
            container.removeAllViews();
            container.addView(option.getCustomView());
            return;
        }
        if (option.getAvatar() != null) {
            icon.setVisibility(View.VISIBLE);
            Glide.with(BaseApplication.getInstance()).load(option.getAvatar()).into(icon);
        } else {
            icon.setVisibility(GONE);
        }

        if (option.getRightResId() != 0) {
            right.setVisibility(GONE);
            rightImage.setVisibility(View.VISIBLE);
            rightImage.setImageResource(option.getRightResId());
            rightImage.setOnClickListener(option.getRightListener());
        } else if (option.getRightText() != null) {
            right.setVisibility(View.VISIBLE);
            rightImage.setVisibility(GONE);
            right.setText(option.getRightText());
            right.setOnClickListener(option.getRightListener());
        } else {
            right.setVisibility(GONE);
            rightImage.setVisibility(GONE);
        }
        if (option.getTitle() != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(option.getTitle());
        } else {
            title.setVisibility(GONE);
        }
        if (option.isNeedNavigation()) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(v -> getActivity().finish());
        } else {
            back.setVisibility(GONE);
        }

    }


    @Override
    public void showLoading(String loadingMsg) {
    }


    protected void showLoadDialog(String message) {
        if (!getActivity().isFinishing()) {
            if (getActivity() instanceof BaseActivity) {
                ((BaseActivity) getActivity()).showLoadDialog(message);
            }
        }
    }

    @Override
    public void hideLoading() {

    }






    @Override
    public void showError(String errorMsg) {

    }


    public void showEmptyLayout(int status) {

    }


    public int getLayoutStatus() {
        return 0;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
        }
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void showEmptyView() {
    }


    public ImageView getIcon() {
        return icon;
    }


    public void addOrReplaceFragment(Fragment fragment) {
        addOrReplaceFragment(fragment, 0);
    }


    protected int fragmentContainerResId = 0;
    protected Fragment currentFragment;

    /**
     * 第一次加载的时候调用该方法设置resId
     *
     * @param fragment
     * @param resId
     */
    public void addOrReplaceFragment(Fragment fragment, int resId) {
        if (resId != 0) {
            fragmentContainerResId = resId;
        }
        if (fragment == null) {
            return;
        }
        if (currentFragment == null) {
            getChildFragmentManager().beginTransaction().add(resId, fragment).show(fragment).commitAllowingStateLoss();
            currentFragment = fragment;
            return;
        }
        if (fragment.isAdded()) {
            getChildFragmentManager().beginTransaction().hide(currentFragment).show(fragment).commit();
        } else {
            getChildFragmentManager().beginTransaction().hide(currentFragment).add(fragmentContainerResId, fragment).show(fragment).commitAllowingStateLoss();
        }
        currentFragment = fragment;
    }


    protected void addBackStackFragment(Fragment fragment, View... views) {
        ((BaseActivity) getActivity()).addBackStackFragment(fragment, true, views);
    }


    protected void addBackStackFragment(Fragment fragment) {
        ((BaseActivity) getActivity()).addBackStackFragment(fragment, true);
    }


    protected void addBackStackFragment(Fragment fragment, boolean needAddBackStack, View... views) {
        ((BaseActivity) getActivity()).addBackStackFragment(fragment, needAddBackStack, views);
    }


}
