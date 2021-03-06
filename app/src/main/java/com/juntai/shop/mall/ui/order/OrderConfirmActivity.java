package com.juntai.shop.mall.ui.order;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.juntai.mall.base.base.BaseActivity;
import com.juntai.mall.base.base.BaseObserver;
import com.juntai.mall.base.base.BaseResult;
import com.juntai.mall.base.utils.ImageLoadUtil;
import com.juntai.mall.base.utils.ToastUtils;
import com.juntai.shop.mall.App;
import com.juntai.shop.mall.AppNetModule;
import com.juntai.shop.mall.R;
import com.juntai.shop.mall.bean.AddressListBean;
import com.juntai.shop.mall.bean.OrderCreateBean;
import com.juntai.shop.mall.bean.SettlementBean;
import com.juntai.shop.mall.ui.adapter.OrderConfirmGoodsAdapter;
import com.juntai.shop.mall.ui.address.AddressSelectDialog;
import com.juntai.shop.mall.ui.goods.ShopActivity;
import com.juntai.shop.mall.utils.AppCode;
import com.juntai.shop.mall.utils.StringTools;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 订单确认
 * @aouther Ma
 * @date 2019/3/6
 */
public class OrderConfirmActivity extends BaseActivity implements View.OnClickListener {
    AddressSelectDialog selectDialog;
    RelativeLayout relativeLayoutAdd,relativeLayoutAddress;
    RecyclerView recyclerView;
    TextView tvShopName,tvPrice1,tvPrice2,tvRemark,tvName,tvPhone,tvTag,tvAddress,tvY,tvB;
    ImageView imageView;
    int shopid,logoId;
    private OrderConfirmGoodsAdapter adapter;
    double price1,price2;
    //默认选择的地址
    AddressListBean.ReturnValueBean addressBean;
    @Override
    public int getLayoutView() {
        return R.layout.activity_order_confirm;
    }

    @Override
    public void initView() {
        setTitleName("提交订单");
        getContentLayout().setBackground(null);
        selectDialog = new AddressSelectDialog();

        relativeLayoutAdd = findViewById(R.id.order_address_toadd_layout);
        relativeLayoutAddress = findViewById(R.id.order_address_layout);
        tvShopName = findViewById(R.id.order_confirm_shop_name);
        tvName = findViewById(R.id.order_confirm_address_name);
        tvTag = findViewById(R.id.address_tag);
        tvAddress = findViewById(R.id.order_confirm_address);
        tvPhone = findViewById(R.id.order_confirm_address_phone);
        tvPrice1 = findViewById(R.id.order_comfirm_price1);
        tvPrice2 = findViewById(R.id.order_comfirm_price2);
        tvY = findViewById(R.id.order_comfirm_yf);
        tvB = findViewById(R.id.order_comfirm_bzf);
        tvRemark = findViewById(R.id.order_comfirm_remarks);
        imageView = findViewById(R.id.order_confirm_shop_logo);

        shopid = getIntent().getIntExtra("id",-1);
        logoId = getIntent().getIntExtra("logoId",-1);
        if (shopid == -1){
            finish();
        }
        tvShopName.setText(getIntent().getStringExtra("shopname"));

        recyclerView = findViewById(R.id.orderconfirm_goodslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new OrderConfirmGoodsAdapter(R.layout.item_order_goods,new ArrayList());
        recyclerView.setAdapter(adapter);

        relativeLayoutAddress.setOnClickListener(this);
        tvRemark.setOnClickListener(this);
        findViewById(R.id.shopcart_confirm).setOnClickListener(this);
        findViewById(R.id.order_address_toadd_layout).setOnClickListener(this);

        ImageLoadUtil.loadImageNoCrashRound(mContext,10, StringTools.getImageForCrmInt(logoId),R.mipmap.ic_launcher,imageView);

        selectDialog.setOnSelectorListener(selectBean -> {
            addressBean = selectBean;
            setAddress();
        });
    }

    @Override
    public void initData() {
        AppNetModule.createrRetrofit()
                .settlement(App.app.getAccount(),App.app.getUserToken(),App.app.getUid(),shopid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<SettlementBean>() {
                    @Override
                    public void onSuccess(SettlementBean o) {
                        price1 = o.getReturnValue().getSumPackingCharges();
                        price2 = o.getReturnValue().getFooting();
                        tvY.setText(String.format("运费 ￥%s",o.getReturnValue().getTransportCharges()));
                        tvB.setText(String.format("包装费 ￥%s",o.getReturnValue().getSumPackingCharges()));
                        tvPrice1.setText(String.valueOf(o.getReturnValue().getFooting()));
                        tvPrice2.setText(String.valueOf(o.getReturnValue().getFooting()));
                        adapter.getData().clear();
                        adapter.addData(o.getReturnValue().getCommodity());

                        getAddress();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_comfirm_remarks://添加备注
                startActivityForResult(new Intent(mContext, RemarksActivity.class).putExtra("remark",tvRemark.getText().toString()),AppCode.REMARK);
                break;
            case R.id.shopcart_confirm://提交订单
                if (addressBean != null){
                    orderCreate();
                }else {
                    ToastUtils.toast(mContext,"请选择/添加地址");
                }
                break;
            case R.id.order_address_toadd_layout://
                App.app.activityTool.toAddAddress(OrderConfirmActivity.this,-1);
                break;
            case R.id.order_address_layout://
                selectDialog.show(getSupportFragmentManager(),"ss");
                break;
        }
    }

    public void getAddress(){
        AppNetModule.createrRetrofit()
                .addressList(App.app.getAccount(),App.app.getUserToken(),App.app.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AddressListBean>(null) {
                    @Override
                    public void onSuccess(AddressListBean o) {
                        if (o.getReturnValue()!= null && o.getReturnValue().size() > 0){
                            relativeLayoutAdd.setVisibility(View.GONE);
                            relativeLayoutAddress.setVisibility(View.VISIBLE);
                            addressBean = o.getReturnValue().get(0);
                            setAddress();
                        }else {
                            relativeLayoutAdd.setVisibility(View.VISIBLE);
                            relativeLayoutAddress.setVisibility(View.GONE);
                        }

                        selectDialog.updateList(o.getReturnValue());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    /**
     *
     */
    private void setAddress(){
        if (addressBean.getLabel() == null || addressBean.getLabel().isEmpty()){
            tvTag.setVisibility(View.GONE);
        }else {
            tvTag.setVisibility(View.VISIBLE);
        }
        tvName.setText(addressBean.getName());
        tvPhone.setText(addressBean.getPhone());
        tvTag.setText(addressBean.getLabel());
        tvAddress.setText(addressBean.getProvinceName()
                +addressBean.getCityName()
                +addressBean.getAreaName()
                +addressBean.getStreetName()
                +addressBean.getDetailedAddress());
    }
    /**
     * 创建订单
     */
    private void orderCreate(){
        AppNetModule.createrRetrofit()
                .orderCreate(App.app.getAccount(),
                        App.app.getUserToken(),
                        App.app.getUid(),
                        shopid,
                        addressBean.getId(),
                        price1,
                        price2,
                        tvRemark.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OrderCreateBean>() {
                    @Override
                    public void onSuccess(OrderCreateBean o) {
                        App.app.activityTool.toOrderPayActivity(OrderConfirmActivity.this,
                                o.getReturnValue().getId(),
                                o.getReturnValue().getFooting(),
                                o.getReturnValue().getShopName(),
                                StringTools.getDataForString(o.getReturnValue().getEstablishTime()).getTime()/1000);
                        App.app.getCartBeansForShop(shopid).clear();
                        finish();
                    }

                    @Override
                    public void onError(String msg) {
                        ToastUtils.toast(mContext,msg);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppCode.REMARK && resultCode == RESULT_OK){
            tvRemark.setText(data.getStringExtra("remark"));
        }
        if (requestCode == AppCode.ADDRESS && resultCode == RESULT_OK){
            getAddress();
        }
    }
}
