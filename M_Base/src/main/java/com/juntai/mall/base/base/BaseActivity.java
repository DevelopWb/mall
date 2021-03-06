package com.juntai.mall.base.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.juntai.mall.base.R;
import com.juntai.mall.base.app.BaseApplication;
import com.juntai.mall.base.utils.FileCacheUtils;
import com.juntai.mall.base.utils.LoadingDialog;
import com.juntai.mall.base.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;


public abstract class BaseActivity extends RxAppCompatActivity implements Toolbar.OnMenuItemClickListener {
    public abstract int getLayoutView();

    public abstract void initView();

    public abstract void initData();

    public Context mContext;
    public Toast toast;
    private Toolbar toolbar;
    private boolean title_menu_first = true;
    private ImageView titleBack;
    private TextView titleName, titleRightTv, statusTopNullView;
    private boolean autoHideKeyboard = true;
    FrameLayout contentLayout;
    public LinearLayout mBaseRootCol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //??????
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        setContentView(R.layout.activity_base);
        mBaseRootCol = findViewById(R.id.base_layout);
        contentLayout = findViewById(R.id.base_content);
        contentLayout.addView(View.inflate(mContext, getLayoutView(), null));
        toolbar = findViewById(R.id.base_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        titleBack = findViewById(R.id.title_back);
        titleName = findViewById(R.id.title_name);
        titleRightTv = findViewById(R.id.title_rightTv);
        statusTopNullView = findViewById(R.id.status_top_null_view);
        statusTopNullView.getLayoutParams().height = BaseApplication.statusBarH;
        ImmersionBar.with(this).statusBarDarkFont(true).init();
        initView();
        initData();
        EventBus.getDefault().register(this);
    }
    /**
     * ?????????recyclerview LinearLayoutManager
     */
    public void initRecyclerview(RecyclerView recyclerView, BaseQuickAdapter baseQuickAdapter,
                                 @RecyclerView.Orientation int orientation) {
        if (recyclerView == null) {
            return;
        }
        LinearLayoutManager managere = new LinearLayoutManager(this, orientation, false);
        //        baseQuickAdapter.setEmptyView(getAdapterEmptyView("?????????????????????",0));
        recyclerView.setLayoutManager(managere);
        recyclerView.setAdapter(baseQuickAdapter);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TextView getTitleRightTv() {
        titleRightTv.setVisibility(View.VISIBLE);
        return titleRightTv;
    }

    public TextView getStatusTopNullView() {
        return statusTopNullView;
    }

    public FrameLayout getContentLayout() {
        return contentLayout;
    }

    public LinearLayout getmBaseRootCol() {
        return mBaseRootCol;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringEvent(String str) {
    }

    /**
     * ??????
     *
     * @param title
     */
    public void setTitleName(String title) {
        titleName.setText(title);
//        toolbar.setTitle(title);
    }

    /**
     * ????????????
     */
    public void hindTitleBack() {

    }

    /**
     * title??????:?????????
     */
    private void setRightRes() {
        //??????menu
        toolbar.inflateMenu(R.menu.toolbar_menu);
        //????????????
        toolbar.setOnMenuItemClickListener(this);
    }

    /**
     * ??????????????????
     *
     * @param itemId
     */
    public void showTitleRes(int... itemId) {
        if (title_menu_first) {
            setRightRes();
            title_menu_first = false;
        }
        for (int item : itemId) {
            //??????
            toolbar.getMenu().findItem(item).setVisible(true);//??????id??????,????????????setIcon()????????????
            //            toolBar.getMenu().getItem(0).setVisible(true);//??????????????????
        }
    }

    /**
     * ??????title??????
     *
     * @param itemId :?????????????????????id
     */
    public void hindTitleRes(int... itemId) {
        //        if (titleBack != null)
        //            titleBack.setVisibility(View.GONE);
        for (int item : itemId) {
            //??????
            toolbar.getMenu().findItem(item).setVisible(false);
        }
    }

    /**
     * toolbar????????????---???activity??????onMenuItemClick()
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    /**
     * ??????webview
     *
     * @param webView
     */
    public void closeWebView(WebView webView) {
        if (webView != null) {
//            ViewGroup parent = webView.getParent();
//            if (parent != null) {
//                parent.re(webView);
//            }
            webView.removeAllViews();
            webView.destroy();
        }
    }
//    @Override
//    public void showLoadingFileDialog() {
//        showFileDialog();
//    }
//
//    @Override
//    public void hideLoadingFileDialog() {
//        hideFileDialog();
//    }

//    @Override
//    public void onProgress(long totalSize, long downSize) {
//        if (dialog != null) {
//            dialog.setProgress((int) (downSize * 100 / totalSize));
//        }
//    }

    /**
     * ????????????????????????
     *
     * @param event
     * @param view
     * @param activity
     */
    public static void hideKeyboard(MotionEvent event, View view,
                                    Activity activity) {
        try {
            if (view != null && view instanceof EditText) {
                int[] location = {0, 0};
                view.getLocationInWindow(location);
                int left = location[0], top = location[1], right = left
                        + view.getWidth(), bootom = top + view.getHeight();
                // ???????????????????????????????????????????????????????????????????????????????????????
                if (event.getRawX() < left || event.getRawX() > right
                        || event.getY() < top || event.getRawY() > bootom) {
                    // ????????????
                    IBinder token = view.getWindowToken();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(token,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????????????????- - - ??????
     *
     * @param autoHideKeyboard:false - ???????????????
     */
    public void setAutoHideKeyboard(boolean autoHideKeyboard) {
        this.autoHideKeyboard = autoHideKeyboard;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                if (autoHideKeyboard) {
                    hideKeyboard(ev, view, BaseActivity.this);//??????????????????????????????????????????
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }



    /**
     * ????????????
     * @param path  ????????????
     * @param saveDirName  ???????????????????????????
     * @param onImageCompressedPath
     * @param saveFileName  ?????????????????????
     */
    public void  compressImage(String path, String saveDirName,
                               String saveFileName,OnImageCompressedPath onImageCompressedPath) {
        //        showLoadingDialog(mContext);
        Luban.with(mContext).load(path).ignoreBy(100)
                .setTargetDir(FileCacheUtils.getAppImagePath(saveDirName))
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                }).setRenameListener(new OnRenameListener() {
            @Override
            public String rename(String filePath) {
                return TextUtils.isEmpty(saveFileName)||saveFileName==null?System.currentTimeMillis()+".jpg":
                        saveFileName+".jpg";
            }
        })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        //  ???????????????????????????????????????????????? loading UI

                    }

                    @Override
                    public void onSuccess(File file) {
                        //  ??????????????????????????????????????????????????????
                        if (onImageCompressedPath != null) {
                            onImageCompressedPath.compressedImagePath(file);
                        }
                        stopLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("push-??????????????????");
                        stopLoadingDialog();
                    }
                }).launch();
    }
    /**
     * ??????????????????
     */
    public void stopLoadingDialog() {
        LoadingDialog.getInstance().dismissProgress();
    }

    /**
     * ????????????????????????
     */
    public interface OnImageCompressedPath {
        void  compressedImagePath(File file);
    }

    /**
     * ??????????????????
     */
    public void showLoadingDialog(Context context) {
        LoadingDialog.getInstance().showProgress(context);
    }
}
