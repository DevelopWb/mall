package com.juntai.shop.mall.ui.goods;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.juntai.mall.base.base.BaseActivity;
import com.juntai.mall.base.base.BaseObserver;
import com.juntai.mall.base.base.BaseResult;
import com.juntai.mall.base.utils.ImageLoadUtil;
import com.juntai.mall.base.utils.LogUtil;
import com.juntai.mall.base.utils.ToastUtils;
import com.juntai.mall.im.ModuleIm_Init;
import com.juntai.mall.im.UserIM;
import com.juntai.shop.mall.App;
import com.juntai.shop.mall.AppHttpPath;
import com.juntai.shop.mall.AppNetModule;
import com.juntai.shop.mall.R;
import com.juntai.shop.mall.bean.CartItemLocB;
import com.juntai.shop.mall.bean.IntBean;
import com.juntai.shop.mall.bean.ShopBean;
import com.juntai.shop.mall.bean.ShopCartsBean;
import com.juntai.shop.mall.bean.event.EventDetailsMessage;
import com.juntai.shop.mall.bean.event.EventToSubmit;
import com.juntai.shop.mall.ui.act.ReportActivity;
import com.juntai.shop.mall.ui.adapter.TopTabAdapter;
import com.juntai.shop.mall.ui.goods.fmt.CommentsFragment;
import com.juntai.shop.mall.ui.goods.fmt.GoodsDetailsFragment;
import com.juntai.shop.mall.ui.goods.fmt.GoodsFragment;
import com.juntai.shop.mall.ui.goods.fmt.ShopCartFragment;
import com.juntai.shop.mall.ui.goods.fmt.ShopInfoFragment;
import com.juntai.shop.mall.ui.order.OrderConfirmActivity;
import com.juntai.shop.mall.utils.AppUtils;
import com.juntai.shop.mall.utils.DpTools;
import com.juntai.shop.mall.utils.GlideImageLoader;
import com.juntai.shop.mall.utils.ShareUtils;
import com.juntai.shop.mall.utils.StringTools;
import com.juntai.shop.mall.utils.listener.GoodsStatusChangeListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * ??????
 * Created by Ma
 * on 2019/8/28
 */
public class ShopActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public static int shopId;
    public static String shopRyid,shopName;
    private Banner banner;
    GlideImageLoader glideImageLoader = new GlideImageLoader();
    TopTabAdapter adapter;
    ImageView headView;
    ViewPager viewpager;
    TabLayout tablayout;
    ShopBean.ReturnValueBean shopInfoB;
    private String[] title = new String[]{"??????","??????", "??????"};
    List<Fragment> mFragments = new ArrayList<>();
    GoodsFragment goodsFragment = new GoodsFragment();
    //title
    ImageView ivBack,ivCollect,ivMore;
    TextView tvTItle;
    View viewNull;
    LinearLayout titlelayout;
    CoordinatorLayout coordinatorLayout;
    boolean isToSubmit = false,isCollect = false;
    //cart
    GoodsDetailsFragment detailsFragment = new GoodsDetailsFragment();
    ShopCartFragment shopCartFragment = new ShopCartFragment();
    int gid;
    @Override
    public int getLayoutView() {
        return R.layout.activity_shop;
    }

    @Override
    public void initView() {
        shopId = getIntent().getIntExtra("shopId",-1);
        if (shopId == -1){
            finish();
        }
        getToolbar().setVisibility(View.GONE);
        getStatusTopNullView().setVisibility(View.GONE);
        getContentLayout().setBackground(null);
        viewNull = findViewById(R.id.shop_null_view);
        viewNull.getLayoutParams().height = App.statusBarH;
        titlelayout = findViewById(R.id.shop_title_layout);
        coordinatorLayout = findViewById(R.id.shop_content);
        ivBack = findViewById(R.id.title_shop_back);
        tvTItle = findViewById(R.id.title_shop_title);
        ivCollect = findViewById(R.id.title_shop_check);
        ivMore = findViewById(R.id.title_shop_more);
        ivBack.setOnClickListener(this);
        findViewById(R.id.title_goods_back).setOnClickListener(this);
        ivCollect.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        //
        headView = findViewById(R.id.shop_logo_view);
        //
        banner = findViewById(R.id.banner);
        banner.isAutoPlay(false);
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                glideImageLoader.pause();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //banner.setImages(Arrays.asList(images)).setImageLoader(new GlideImageLoader()).start();
        banner.setOnBannerListener(position -> {
//            if (imagesAdapter.getData().get(position).indexOf(".mp4") != -1){
//                mContext.startActivity(new Intent(mContext, VideoNetPlayerActivity.class)
//                        .putExtra("path",imagesAdapter.getData().get(position)));
//            }else {
//                imagepos = position;
//                if (imagesAdapter.getData().get(0).indexOf(".mp4") != -1) {
//                    imagepos = position - 1;
//                }
//                mContext.startActivity(new Intent(mContext, ImageZoomActivity.class)
//                        .putStringArrayListExtra("paths", (ArrayList<String>) imagesAdapter.getData())
//                        .putExtra("item",imagepos));
//            }
        });
        //
        viewpager = findViewById(R.id.shop_viewpager);
        tablayout = findViewById(R.id.shop_tablayout);
        mFragments.add(goodsFragment);
        mFragments.add(new CommentsFragment());
        mFragments.add(new ShopInfoFragment());
        //
        initTab();
        //
        showTitleRes(R.id.title_collect,R.id.title_more);
        goodsFragment.setGoodsStatusChangeListener(new GoodsStatusChangeListener(){

        });

        detailsFragment = (GoodsDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.shop_fragment_goodsdetails);
        shopCartFragment = (ShopCartFragment) getSupportFragmentManager().findFragmentById(R.id.shop_fragment_shopcart);
        //????????????????????????
        gid = getIntent().getIntExtra("goodsId",-1);
        if (gid != -1){
            titlelayout.setVisibility(View.GONE);
            detailsFragment.setGoodsId(gid);
        }else {
            getSupportFragmentManager().beginTransaction().hide(detailsFragment).commit();
        }

        //ShopCartFragment shopCartFragment = new ShopCartFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.shop_shopcart_goodsdetails,shopCartFragment,"cart").show(shopCartFragment).commit();
    }

    @Override
    public void initData() {
        getData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isToSubmit = false;
        getCarts();
    }

    @Override
    protected void onPause() {
        glideImageLoader.pause();
        super.onPause();
    }

    public void getData(){
        AppNetModule.createrRetrofit()
                .shop(App.app.getAccount(),App.app.getUserToken(),shopId,App.app.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ShopBean>() {
                    @Override
                    public void onSuccess(ShopBean result) {
                        shopInfoB = result.getReturnValue();
                        shopRyid = shopInfoB.getShopAccount();
                        shopName = shopInfoB.getShopName();
                        //0???????????? 1????????????
                        ivCollect.setImageResource(shopInfoB.getIsCollect() == 0?R.mipmap.ic_collect:R.mipmap.ic_collect_check);
                        tvTItle.setText(result.getReturnValue().getShopName());
                        ImageLoadUtil.loadImageNoCrash(mContext,StringTools.getImageForCrmInt(result.getReturnValue().getLogoId()),headView);
                        List<String> sssss = new ArrayList<>();
                        if (shopInfoB.getVideoUrl() != null && !shopInfoB.getVideoUrl().isEmpty()){
                            sssss.add(AppHttpPath.VIDEO_FOR_CRM + shopInfoB.getShopId());
                        }
                        sssss.addAll(StringTools.getImagesForCrmBig(result.getReturnValue().getImgId()));
                        banner.setImages(sssss).setImageLoader(glideImageLoader).start();
                        //
                        goodsFragment.setDateList(result.getReturnValue().getShopClassify());
                        if (gid != -1){
                            coordinatorLayout.setVisibility(View.GONE);
                        }
                        //
                        ModuleIm_Init.setUser(new UserIM(shopInfoB.getShopAccount(),shopName,StringTools.getImageForCrmInt(shopInfoB.getShopUserId())));
                    }
                    @Override
                    public void onError(String msg) {
                        ToastUtils.toast(mContext,msg);
                    }
                });
    }

    /**
     * ???????????????
     */
    public void getCarts(){
        AppNetModule.createrRetrofit()
                .shopCarts(App.app.getAccount(),App.app.getUserToken(),App.app.getUid(), shopId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ShopCartsBean>() {
                    @Override
                    public void accept(ShopCartsBean shopCartsBean) throws Exception {
                        StringBuffer stringBuffer;
                        if (shopCartsBean.getReturnValue() != null && shopCartsBean.getReturnValue().size() > 0) {
                            App.app.getCartBeansForShop(shopId).clear();
                            for (ShopCartsBean.ReturnValueBean bean : shopCartsBean.getReturnValue()) {
                                stringBuffer = new StringBuffer();
                                for (ShopCartsBean.ReturnValueBean.SpecificationBean bean1 : bean.getSpecification()) {
                                    stringBuffer.append(bean1.getParameterName());
                                }
                                CartItemLocB cartItemLocB = new CartItemLocB(
                                        bean.getTrolleyId(),
                                        bean.getCommodityId(),
                                        bean.getCommodityName(),
                                        bean.getCommodityImg(),
                                        bean.getCommodityNum(),
                                        bean.getInventoryNum(),
                                        bean.getPackingCharges(),
                                        bean.getPrice(),
                                        bean.getAttributeId(),
                                        stringBuffer.toString());
                                App.app.setCartBean(shopId, cartItemLocB);
                            }
                        }
                        if (shopCartFragment != null) {
                            shopCartFragment.update();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (shopCartFragment != null){
                            shopCartFragment.update();
                        }
                        LogUtil.e("?????????????????????");
                    }
                });
    }
    /**
     * ??????
     */
    public void collect(){
        if (shopInfoB == null || isCollect) return;
        isCollect = true;
        AppNetModule.createrRetrofit()
                .collectOperateOne(App.app.getAccount(),App.app.getUserToken(),App.app.getUid(),
                        0,shopId,0,shopInfoB.getIsCollect(),shopInfoB.getIsCollect())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<IntBean>() {
                    @Override
                    public void onSuccess(IntBean result) {
                        //0???????????? 1????????????
                        shopInfoB.setIsCollect(result.getReturnValue());
                        ivCollect.setImageResource(shopInfoB.getIsCollect() == 0?R.mipmap.ic_collect:R.mipmap.ic_collect_check);
                    }
                    @Override
                    public void onError(String msg) {
                        ToastUtils.toast(mContext,msg);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        isCollect = false;
                    }
                });
    }
    PopupWindow popupWindow;
    public void showPopMore(){
        if (popupWindow == null){
            View view= LayoutInflater.from(mContext).inflate(R.layout.pop_shop_more, null);
            //????????????
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            view.findViewById(R.id.report_shop).setOnClickListener(this);
            view.findViewById(R.id.share_shop).setOnClickListener(this);
            //???????????????????????????
        }
        popupWindow.showAsDropDown(ivMore, -DpTools.px2dip(mContext,40),0);//?????????????????????
    }

    public void initTab() {
        adapter = new TopTabAdapter(getSupportFragmentManager(), this, title, mFragments);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(title.length);
        /*viewpager???????????????????????????????????????*/
        viewpager.addOnPageChangeListener(this);
        //TabLayout
        tablayout.setupWithViewPager(viewpager);
//        tabLayout.setOnTabSelectedListener();
        /**
         * ???????????????tab??????
         * */
        for (int i = 0; i < title.length; i++) {
            TabLayout.Tab tab = tablayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }
//        tablayout.newTab();
        /*viewpager?????????????????????*/
        viewpager.setCurrentItem(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_shop://????????????
                startActivity(new Intent(mContext, ReportActivity.class));
                break;
            case R.id.share_shop://??????
                if (shopInfoB.getWebUrl() != null && !shopInfoB.getWebUrl().isEmpty()){
                    ShareUtils.shareForMob(mContext,
                            AppUtils.getAppName(),
                            shopInfoB.getWebUrl(),
                            shopInfoB.getShopName(),
                            StringTools.getImageForCrmInt(shopInfoB.getLogoId()));
                }
                break;
            case R.id.title_goods_back:
                //??????????????????
                onMessageEvent(new EventDetailsMessage(-1));
                break;
            case R.id.title_shop_back:
                finish();
                break;
            case R.id.title_shop_check:
                collect();
                break;
            case R.id.title_shop_more:
                showPopMore();
                break;
        }
    }

    /**
     * ???????????????
     */
    public void cartSys(){
        if (App.app.getCartBeansForShop(shopId).size() == 0){
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (CartItemLocB b:App.app.getCartBeansForShop(shopId)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",b.getCartId());//?????????id,?????????0
                jsonObject.put("commodityId",b.getGoodsId());
                jsonObject.put("commodityNum",b.getNum());
                jsonObject.put("attributeId",b.getSpcId());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("??????",e.toString());
            }
        }
        AppNetModule.createrRetrofit()
                .cartSys(App.app.getAccount(),App.app.getUserToken(),App.app.getUid(),shopId,jsonArray.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (isToSubmit){
                        App.app.activityTool.toOrderConfirmActivity(mContext,shopId,shopInfoB.getLogoId(),shopInfoB.getShopName());
                    }
                    LogUtil.d("???????????????success-???"+ result.success);
                }, throwable -> {
                    LogUtil.d("???????????????error-???"+ throwable.toString());
                });
    }
    /**
     *  ??????
     * @param eventToSubmit:???????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toSubmit(EventToSubmit eventToSubmit){
        isToSubmit = true;
        cartSys();
    }

    /**
     * ???????????????
     * @param itemLocB
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartEvent(CartItemLocB itemLocB){
        //
        if (itemLocB.getGoodsId() != 0){
            App.app.setCartBean(shopId,itemLocB);
        }
        shopCartFragment.update();
    }

    /**
     * @param locB
     */
    public void refrehsGoods(CartItemLocB locB){
        goodsFragment.refreshList(locB.getGoodsId(),locB.getNum());
        if (detailsFragment != null){
            detailsFragment.refreshNum(locB.getGoodsId(),locB.getNum());
        }
    }

    /**
     * ??????????????????
     * @param detailsMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventDetailsMessage detailsMessage){
        if (detailsMessage.goodsId == -1){
            //??????fragment
            titlelayout.setVisibility(View.VISIBLE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().hide(detailsFragment).commit();
        }else {
            titlelayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.GONE);
            detailsFragment.setGoodsId(detailsMessage.goodsId);
            getSupportFragmentManager().beginTransaction().show(detailsFragment).commit();
        }
        glideImageLoader.pause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (detailsFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().hide(detailsFragment).commit();
        }else {
            //???????????????
            cartSys();
            goodsFragment.setGoodsStatusChangeListener(null);
            goodsFragment.setDateList(null);
            glideImageLoader.release();
            super.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (detailsFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().hide(detailsFragment).commit();
            titlelayout.setVisibility(View.VISIBLE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }
}
