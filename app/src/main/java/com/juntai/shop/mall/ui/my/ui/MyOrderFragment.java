package com.juntai.shop.mall.ui.my.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.juntai.mall.base.base.BaseLazyFragment;
import com.juntai.mall.base.base.BaseObserver;
import com.juntai.mall.base.base.BaseResult;
import com.juntai.mall.base.utils.ToastUtils;
import com.juntai.mall.im.ModuleIm_Init;
import com.juntai.shop.mall.App;
import com.juntai.shop.mall.AppHttpPath;
import com.juntai.shop.mall.AppNetModule;
import com.juntai.shop.mall.R;
import com.juntai.shop.mall.bean.MyCollcetB;
import com.juntai.shop.mall.bean.OrderListBean;
import com.juntai.shop.mall.ui.after_sales.ReturnDetailsActivity;
import com.juntai.shop.mall.ui.dialog.PromptDialog;
import com.juntai.shop.mall.ui.goods.ShopActivity;
import com.juntai.shop.mall.ui.my.adt.MyCollectAdapter;
import com.juntai.shop.mall.ui.my.adt.MyOrderAdapter;
import com.juntai.shop.mall.ui.order.LogisticsActivity;
import com.juntai.shop.mall.ui.order.OrderCommentActivicty;
import com.juntai.shop.mall.ui.order.OrderDeatilsActivity;
import com.juntai.shop.mall.utils.AppCode;
import com.juntai.shop.mall.utils.DpTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ma
 * on 2019/11/26
 */
public class MyOrderFragment extends BaseLazyFragment {
    private SmartRefreshLayout smartRefreshLayout ;
    private RecyclerView recyclerView;
    MyOrderAdapter adapter;
    PromptDialog promptDialog = new PromptDialog();
    int type;
    String nowUrl;
    int nowOrderId;
    int page = 1,pagesize = 10;
    boolean isRefresh = false;//????????????????????????
    public static MyOrderFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type",type);
        MyOrderFragment fragment = new MyOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Override
    protected void lazyLoad() {
        getData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (adapter != null && isRefresh){
            page = 1;
            getData();
        }
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_collect;
    }

    @Override
    protected void initView() {
        smartRefreshLayout = getView(R.id.smartRefreshLayout);
        recyclerView = getView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new MyOrderAdapter(R.layout.item_myorder,new ArrayList());
        recyclerView.setAdapter(adapter);
        //??????
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            page = 1;
            getData();
        });
        //????????????
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getData();
        });
        promptDialog.setOkClickListener(new PromptDialog.OnOkClickListener() {
            @Override
            public void onClick() {
                editOrder(null);
            }

            @Override
            public void cancle() {

            }
        });
        adapter.setOnItemClickListener((adapter1, view, position) ->{
            //????????????
            startActivity(new Intent(mContext, OrderDeatilsActivity.class).putExtra("orderId",adapter.getData().get(position).getOrderId()));
        });
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            if (smartRefreshLayout.isRefreshing()) return;
            nowOrderId = adapter.getData().get(position).getOrderId();
            switch (view.getId()){
                case R.id.item_myorder_shoplayout://?????????
                    App.app.activityTool.toShopActivity(mContext,adapter.getData().get(position).getShopId());
                    break;
                case R.id.item_myorder_maijia://????????????
                    //im
                    ModuleIm_Init.chat(mContext,adapter.getData().get(position).getShopAccount(),adapter.getData().get(position).getShopName());
                    break;
                case R.id.item_myorder_cancle://????????????
                    nowUrl = AppHttpPath.ORDER_MY_QX;
                    promptDialog.setMessage("??????????????????","???","????????????");
                    promptDialog.show(getChildFragmentManager(),"dialog");
                    break;
                case R.id.item_myorder_fukuan://??????
                    App.app.activityTool.toOrderPayActivity(MyOrderFragment.this,
                            adapter.getData().get(position).getOrderId(),
                            adapter.getData().get(position).getFooting(),
                            adapter.getData().get(position).getShopName(),
                            adapter.getData().get(position).getEstablishTime());
                    break;
                case R.id.item_myorder_wuliu://??????
                    startActivity(new Intent(mContext, LogisticsActivity.class).putExtra("id",nowOrderId));
                    break;
                case R.id.item_myorder_shouhuo://??????
                    nowUrl = AppHttpPath.ORDER_MY_SH;
                    //??????????????????????????????
                    promptDialog.setMessage("??????????????????","???","????????????");
                    promptDialog.show(getChildFragmentManager(),"dialog");
                    break;
                case R.id.item_myorder_deails://????????????
                    //????????????
                    startActivityForResult(App.app.activityTool.getReturnDetailsIntent(mContext,nowOrderId), AppCode.RETURN);
                    //startActivity(new Intent(mContext, OrderDeatilsActivity.class).putExtra("id",nowOrderId));
                    break;
                case R.id.item_myorder_after_sales://??????
                    App.app.goodsReturnBeans.clear();
                    App.app.goodsReturnBeans.addAll(adapter.getData().get(position).getCommodityList());
                    App.app.activityTool.toGoodsSelectActivity(
                            mContext,
                            nowOrderId,
                            adapter.getData().get(position).getShopName(),
                            adapter.getData().get(position).getShopAccount());
                    break;
                case R.id.item_myorder_comment://??????
                    startActivity(new Intent(mContext, OrderCommentActivicty.class)
                            .putExtra("shopid", adapter.getData().get(0).getShopId())
                            .putExtra("ordid",nowOrderId));
                    break;
                case R.id.item_myorder_more://??????
                    nowUrl = AppHttpPath.ORDER_MY_DEL;
                    showPopMore(view);
                    break;
            }
        });
    }

    @Override
    protected void initData() {

    }
    /**
     * ????????????
     */
    public void getData(){
        AppNetModule.createrRetrofit()
                .orderMy(App.app.getAccount(), App.app.getUserToken(),App.app.getUid(),type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OrderListBean>(null) {
                    @Override
                    public void onSuccess(OrderListBean result) {
                        isRefresh = false;
                        smartRefreshLayout.finishRefresh();
                        if (page == 1){
                            adapter.getData().clear();
                        }
                        smartRefreshLayout.setNoMoreData(result.getReturnValue().getDatas().size() < pagesize);
                        adapter.addData(result.getReturnValue().getDatas());
                        page ++;
                    }
                    @Override
                    public void onError(String msg) {
                        smartRefreshLayout.finishRefresh();
                        isRefresh = false;
                        ToastUtils.toast(mContext,msg);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    /**
     * ??????????????????
     * @param time
     */
    public void editOrder(String time){
        AppNetModule.createrRetrofit()
                .orderEdit(nowUrl,App.app.getAccount(), App.app.getUserToken(),App.app.getUid(),nowOrderId,time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        page = 1;
                        getData();
                        ((MyOrderActivity)getActivity()).needRefresh();
                    }
                    @Override
                    public void onError(String msg) {
                        ToastUtils.toast(mContext,msg);
                        page = 1;
                        getData();
                    }
                });
    }

    PopupWindow popupWindow;
    int[] loca;
    public void showPopMore(View viewP){
        if (popupWindow == null){
            View view= LayoutInflater.from(mContext).inflate(R.layout.pop_item_order_more, null);
            //????????????
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            view.findViewById(R.id.pop_order_more_delte).setOnClickListener(v -> {
                promptDialog.setMessage("??????????????????","???","????????????");
                promptDialog.show(getChildFragmentManager(),"dialog");
                popupWindow.dismiss();
            });
        }
        loca = new int[2];
        viewP.getLocationOnScreen(loca);
        //???????????????????????????
        popupWindow.showAtLocation(viewP,Gravity.NO_GRAVITY,loca[0] - DpTools.dip2px(mContext,50),loca[1] - DpTools.dip2px(mContext,50));
//        popupWindow.showAsDropDown(viewP,- DpTools.dip2px(mContext,50),10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AppCode.RETURN && resultCode == getActivity().RESULT_OK){
        if (resultCode == getActivity().RESULT_OK){
            page = 1;
            getData();
        }
    }
}
