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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.commonlibrary.adaptScreen.IAdaptScreen;
import com.example.commonlibrary.base.ToolBarOption;
import com.example.commonlibrary.dagger.component.AppComponent;
import com.example.commonlibrary.mvp.presenter.BasePresenter;
import com.example.commonlibrary.mvp.view.IView;
import com.example.commonlibrary.skin.SkinManager;
import com.example.commonlibrary.utils.CommonLogger;
import com.example.commonlibrary.utils.Constant;
import com.example.commonlibrary.utils.StatusBarUtil;
import com.example.commonlibrary.utils.ToastUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * 项目名称:    Cugappplat
 * 创建人:        陈锦军
 * 创建时间:    2017/4/3      14:21
 * QQ:             1981367757
 */

public abstract class BaseActivity<T, P extends BasePresenter> extends AppCompatActivity implements IView<T>, IAdaptScreen {

    //  这里的布局view可能为空，取决于子类布局中是否含有该空布局


    protected int fragmentContainerResId = 0;
    protected Fragment currentFragment;
    private View headerLayout;
    private ImageView icon;
    protected TextView right;
    protected TextView title;
    protected ImageView rightImage;
    protected ImageView back;
    private CompositeDisposable compositeDisposable;
    protected ViewGroup bg;



    protected boolean needSlide() {
        return true;
    }

    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }


    public AppComponent getAppComponent() {
        return BaseApplication.getAppComponent();
    }


    @Override
    public boolean isBaseOnWidth() {
        return true;
    }


    @Override
    public int getScreenSize() {
        return 250;
    }


    @Override
    public boolean cancelAdapt() {
        return true;
    }


    @Override
    public boolean needResetAdapt() {
        return getScreenSize() != (isBaseOnWidth() ? BaseApplication.getAppComponent().getSharedPreferences().getInt(Constant.DESIGNED_WIDTH, 0) :
                BaseApplication.getAppComponent().getSharedPreferences().getInt(Constant.DESIGNED_HEIGHT, 0));
    }

    @Nullable
    @Inject
    protected P presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SkinManager.getInstance().apply(this);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        initBaseView();
        initData();
        updateStatusBar();
    }





    protected abstract int getContentLayout();

    protected abstract void initView();

    protected abstract void initData();

    private void initBaseView() {
       ToolBarOption toolBarOption=getToolBar();
        if (toolBarOption != null) {
            bg = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.content_layout_ll, null);
            headerLayout = LayoutInflater.from(this).inflate(R.layout.header_layout, null);
            ((TextView) headerLayout.findViewById(R.id.tv_header_layout_title)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            bg.addView(headerLayout);
            if (getContentLayout() != 0) {
                LayoutInflater.from(this).inflate(getContentLayout(), bg);
            }
            setContentView(bg);
            icon = headerLayout.findViewById(R.id.riv_header_layout_icon);
            title = headerLayout.findViewById(R.id.tv_header_layout_title);
            right = headerLayout.findViewById(R.id.tv_header_layout_right);
            back = headerLayout.findViewById(R.id.iv_header_layout_back);
            rightImage = headerLayout.findViewById(R.id.iv_header_layout_right);
            rightImage.setVisibility(View.GONE);
            right.setVisibility(View.VISIBLE);
            setSupportActionBar(headerLayout.findViewById(R.id.toolbar));
            getSupportActionBar().setTitle("");
            setToolBar(toolBarOption);
        }else {
            setContentView(getContentLayout());
        }
        initView();
    }

    protected  ToolBarOption getToolBar(){
        return null;
    }

    protected void updateStatusBar() {
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, getPaddingView());
    }

    private View getPaddingView() {
        if (needStatusPadding()) {
            return headerLayout != null ? headerLayout : ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        return null;
    }

    protected boolean needStatusPadding() {
        return true;
    }




    private void setToolBar(ToolBarOption option) {
        if (option==null)return;
        if (option.getBgColor() != -1) {
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
            Glide.with(this).load(option.getAvatar()).into(icon);
        } else {
            icon.setVisibility(View.GONE);
        }
        if (option.getRightResId() != 0) {
            right.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            rightImage.setImageResource(option.getRightResId());
            rightImage.setOnClickListener(option.getRightListener());
        } else if (option.getRightText() != null) {
            right.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            right.setText(option.getRightText());
            right.setOnClickListener(option.getRightListener());
        } else {
            right.setVisibility(View.GONE);
            rightImage.setVisibility(View.GONE);
        }
        if (option.getTitle() != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(option.getTitle());
            if (option.getTitleColor() != -10) {
                title.setTextColor(option.getTitleColor());
            }

        } else {
            title.setVisibility(View.GONE);
        }
        if (option.isNeedNavigation()) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(v -> finish());
        } else {
            back.setVisibility(View.GONE);
        }

        if (option.getBackResId() != 0) {
            back.setImageResource(option.getBackResId());
        }


    }







    public void showLoadDialog(final String message) {
        ToastUtils.showShortToast(message);
    }



    public void addOrReplaceFragment(Fragment fragment) {
        addOrReplaceFragment(fragment, 0);
    }

    /**
     * 第一次加载的时候调用该方法设置resId
     */
    public void addOrReplaceFragment(Fragment fragment, int resId) {
        if (resId != 0) {
            fragmentContainerResId = resId;
        }
        if (fragment == null) {
            return;
        }
        if (currentFragment == null) {
            getSupportFragmentManager().beginTransaction().add(fragmentContainerResId, fragment).show(fragment).commitAllowingStateLoss();
            currentFragment = fragment;
        } else if (currentFragment != fragment) {
            if (fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(currentFragment).show(fragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(currentFragment).add(fragmentContainerResId, fragment).show(fragment).commitAllowingStateLoss();
            }
            currentFragment = fragment;
        }
    }


    private int backStackLayoutId = 0;

    protected void addBackStackFragment(Fragment fragment, int resId) {
        backStackLayoutId = resId;
        addBackStackFragment(fragment, true);
    }


    protected void addBackStackFragment(Fragment fragment, int resId, boolean needAddBackStack) {
        backStackLayoutId = resId;
        addBackStackFragment(fragment, needAddBackStack);
    }


    protected void addBackStackFragment(Fragment fragment, boolean needAddBackStack) {
        addBackStackFragment(fragment, needAddBackStack, (View) null);
    }


    protected void addBackStackFragment(Fragment fragment, boolean needAddBackStack, View... views) {
        if (backStackLayoutId == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if (views != null && views.length > 0) {
            fragmentTransaction.replace(backStackLayoutId, fragment);
            for (View item :
                    views) {
                fragmentTransaction.addSharedElement(item, ViewCompat.getTransitionName(item));
            }
        } else {
            fragmentTransaction
                    .add(backStackLayoutId, fragment);
        }
        if (needAddBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }


    @Override
    public void showLoading(String loadMessage) {
        showLoadDialog(loadMessage);
    }

    @Override
    public void hideLoading() {
    }


    @Override
    public void showError(String errorMsg) {
        ToastUtils.showShortToast(errorMsg);
        CommonLogger.e(errorMsg);
    }


    @Override
    public void showEmptyView() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().clear(this);
        if (presenter != null) {
            presenter.onDestroy();
        }
        if (compositeDisposable != null) {
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
            compositeDisposable.clear();
            compositeDisposable = null;
        }

    }


}
