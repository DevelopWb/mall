package com.juntai.shop.mall.ui.homepage;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.juntai.mall.base.net.FileRetrofit;
import com.juntai.mall.bdmap.act.DistanceUtilActivity;
import com.juntai.mall.bdmap.act.PanoramaActivity;
import com.juntai.mall.bdmap.utils.clusterutil.clustering.Cluster;
import com.juntai.mall.bdmap.utils.clusterutil.clustering.ClusterManager;
import com.juntai.shop.mall.App;
import com.juntai.shop.mall.AppHttpPath;
import com.juntai.shop.mall.R;
import com.juntai.shop.mall.baseinfo.BaseAppFragment;
import com.juntai.shop.mall.bean.CameraLocBean;
import com.juntai.shop.mall.bean.MyItem;
import com.juntai.shop.mall.bean.ShopLocationB;
import com.juntai.shop.mall.homepage.camera.ClusterClickAdapter;
import com.juntai.shop.mall.homepage.camera.ijkplayer.PlayerLiveActivity;
import com.juntai.shop.mall.ui.act.SearchActivity;
import com.juntai.shop.mall.ui.act.WeatherActivity;
import com.juntai.shop.mall.ui.dialog.MapDateListDialog;
import com.juntai.shop.mall.utils.ImageUtil;
import com.juntai.shop.mall.utils.StringTools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * ??????
 */
public class HomepageFragment extends BaseAppFragment<HomepagePresent> implements View.OnClickListener, BaiduMap.OnMapLoadedCallback, HomepageContract.IHomepageView
        , ClusterManager.OnClusterClickListener<MyItem>,
        ClusterManager.OnClusterItemClickListener<MyItem>{
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatus mMapStatus;
    //????????????=======================================
    private DrawerLayout drawerLayout = null;
    private RelativeLayout sideLayout = null;
    //
    private ClusterManager<MyItem> mClusterManager;
    ArrayList<ShopLocationB.ReturnValueBean> arrayList = new ArrayList<>();
    //???????????????marker
    Marker nowMarker;
    //???????????????????????????
    CheckBox checkBoxMap, checkBoxWeather, checkBoxVideo;
    //2d,3d,??????????????????
    ImageView type2D, type3D, typeWx, typeQj;
    //??????????????????
    ImageView typeLk, typeCj;
    boolean isCheckedLk = false, isCheckedCj = false;
    //
    TextView tv2D, tv3D, tvWx, tvQj, tvLk, tvCj, nowTv;
    MapDateListDialog mapDateListDialog = new MapDateListDialog();
    int dateType;//1-??????,2-?????????
    int nowShopId = 0;
    boolean isUpdateShop = true;//??????????????????????????????
    private BitmapDescriptor bitmapDescriptor;
    private ClusterClickAdapter clusterClickAdapter;
    private BottomSheetDialog mapBottomDialog;

    @Override
    protected int getLayoutRes() {
        return R.layout.main_map;
    }

    @Override
    protected void initView() {
        drawerLayout = getView(R.id.drawerlayout);
        sideLayout = getView(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                checkBoxMap.setChecked(false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //????????????
        checkBoxMap = getView(R.id.map_menu_map);
        checkBoxWeather = getView(R.id.map_menu_weather);
        checkBoxVideo = getView(R.id.map_menu_video);
        type2D = getView(R.id.drawer_map_type_2d);
        type3D = getView(R.id.drawer_map_type_3d);
        typeWx = getView(R.id.drawer_map_type_wx);
        typeQj = getView(R.id.drawer_map_type_qj);
        typeLk = getView(R.id.drawer_map_type_lk);
        typeCj = getView(R.id.drawer_map_type_cj);
        tv2D = getView(R.id.drawer_text_2d);
        tv3D = getView(R.id.drawer_text_3d);
        tvWx = getView(R.id.drawer_text_wx);
        tvQj = getView(R.id.drawer_text_qj);
        tvLk = getView(R.id.drawer_text_lk);
        tvCj = getView(R.id.drawer_text_cj);
        checkBoxMap.setOnClickListener(this);
        checkBoxWeather.setOnClickListener(this);
        checkBoxVideo.setOnClickListener(this);
        type2D.setOnClickListener(this);
        type3D.setOnClickListener(this);
        typeWx.setOnClickListener(this);
        typeQj.setOnClickListener(this);
        typeLk.setOnClickListener(this);
        typeCj.setOnClickListener(this);
        tv2D.setTextColor(getResources().getColor(R.color.colorTheme));
        type2D.setImageResource(R.mipmap.ic_map_type2d_check);
        nowTv = tv2D;
        //
        getView(R.id.map_zoom_big).setOnClickListener(this);
        getView(R.id.map_zoom_small).setOnClickListener(this);
        getView(R.id.map_location_my).setOnClickListener(this);
        getView(R.id.map_search).setOnClickListener(this);
        //
        mMapView = getView(R.id.bdMapView);
        mBaiduMap = mMapView.getMap();
        initBaiduMapConfig();
        onMarkerClick();


    }

    private void onMarkerClick() {
        mBaiduMap.setOnMarkerClickListener(marker -> {
            //?????????????????????
            if (!mClusterManager.getClusterMarkerCollection().getMarkers().contains(marker)) {
                if (nowMarker != null) {
                    nowMarker.setIcon(bitmapDescriptor);
                }
                //marker.setIcon(BitmapDescriptorFactory.fromBitmap(compoundBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_client_location_pre), BitmapFactory.decodeResource(getResources(),R.mipmap.ic_my_default_head))));
                nowMarker = marker;
            }
            //???nowMarker????????????
            mClusterManager.onMarkerClick(marker);
            return false;
        });
    }


    /**
     * ?????????????????????
     *
     * @param bdLocation
     */
    public void onLocationReceived(BDLocation bdLocation) {

        App.app.setBdLocation(bdLocation);
        if (isUpdateShop) {
            isUpdateShop = false;
        }
        setMap(bdLocation.getLatitude(), bdLocation.getLongitude());
    }

    //????????????
    private void initBaiduMapConfig() {

        // ???????????????LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // ???????????????????????????
        mMapView.showScaleControl(false);
        // ????????????????????????????????????????????????
        mMapView.showZoomControls(false);
        //???????????????:??????????????????????????????????????????????????????????????????????????????????????????????????????????????????5????????????
        mBaiduMap.setMyLocationConfiguration(
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                        true,
                        BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_center),
                        0x55dfa820, 0xAAcd940a));

        mBaiduMap.setOnMapLoadedCallback(this);
        // ????????????????????????ClusterManager
        mClusterManager = new ClusterManager<MyItem>(mContext, mBaiduMap);
        // ???????????????????????????????????????????????????????????????????????????
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(HomepageFragment.this);//?????????
        mClusterManager.setOnClusterClickListener(HomepageFragment.this);//????????????
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_search:
                //??????
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
            case R.id.map_location_my:
                //??????????????????
                checkBoxMap.setChecked(false);
                checkBoxWeather.setChecked(false);
                checkBoxVideo.setChecked(false);
                if (App.app.getBdLocation() != null) {
                    setMap(App.app.getBdLocation().getLatitude(), App.app.getBdLocation().getLongitude());
                }
                break;
            case R.id.map_zoom_big:
                //????????????
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.map_zoom_small:
                //????????????
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case R.id.map_menu_map://??????
                checkBoxWeather.setChecked(false);
                checkBoxVideo.setChecked(false);
                if (checkBoxMap.isChecked()) {
                    drawerLayout.openDrawer(sideLayout);
                } else {
                    drawerLayout.closeDrawer(sideLayout);
                }
                break;
            case R.id.map_menu_weather://??????
                checkBoxMap.setChecked(false);
                checkBoxVideo.setChecked(false);
                startActivity(new Intent(mContext, WeatherActivity.class));
                checkBoxWeather.setChecked(false);
                break;
            case R.id.map_menu_video://??????
                checkBoxMap.setChecked(false);
                checkBoxWeather.setChecked(false);
                mPresenter.getCameraData(AppHttpPath.CAMERA_LOCATION);
                break;
            case R.id.drawer_map_type_2d://2d
                //?????????????????????3d?????????2d???
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mapType(tv2D, type2D, R.mipmap.ic_map_type2d_check);
                break;
            case R.id.drawer_map_type_3d://3d
                //???????????? ,mBaiduMap????????????????????????
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mapType(tv3D, type3D, R.mipmap.ic_map_type3d_check);
                break;
            case R.id.drawer_map_type_wx://?????????
                //????????????
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mapType(tvWx, typeWx, R.mipmap.ic_map_type_wx_check);
                break;
            case R.id.drawer_map_type_qj://?????????
                startActivity(new Intent(mContext, PanoramaActivity.class)
                        .putExtra("lat", App.app.getBdLocation().getLatitude())
                        .putExtra("lon", App.app.getBdLocation().getLongitude()));
                mapType(tvQj, typeQj, R.mipmap.ic_map_type_qj_check);
                break;
            case R.id.drawer_map_type_lk://??????
                //???????????????
                isCheckedLk = !isCheckedLk;
                textColor(isCheckedLk, tvLk, typeLk, R.mipmap.ic_map_type_lk_check, R.mipmap.ic_map_type_lk);
                mBaiduMap.setTrafficEnabled(isCheckedLk);
                break;
            case R.id.drawer_map_type_cj://??????
                //isCheckedCj = !isCheckedCj;
                //textColor(isCheckedCj,tvCj,typeCj,R.mipmap.ic_map_type_cj_check,R.mipmap.ic_map_type_cj);
                startActivity(new Intent(mContext, DistanceUtilActivity.class)
                        .putExtra("lat", App.app.getBdLocation().getLatitude())
                        .putExtra("lon", App.app.getBdLocation().getLongitude()));
                break;
        }
    }

    /**
     * ??????????????????-?????????
     *
     * @param isCheck
     * @param tv
     */
    public void textColor(boolean isCheck, TextView tv, ImageView imageView, int checkedImage, int image) {
        nowTv.setTextColor(getResources().getColor(R.color.black));
        imageView.setImageResource(isCheck ? checkedImage : image);
        tv.setTextColor(isCheck ? getResources().getColor(R.color.colorTheme) : getResources().getColor(R.color.black));
        nowTv = tv;
    }

    /**
     * ????????????-??????
     *
     * @param tv
     * @param imageView
     * @param checkedImage
     */
    public void mapType(TextView tv, ImageView imageView, int checkedImage) {
        type2D.setImageResource(R.mipmap.ic_map_type2d);
        type3D.setImageResource(R.mipmap.ic_map_type3d);
        typeWx.setImageResource(R.mipmap.ic_map_type_wx);
        typeQj.setImageResource(R.mipmap.ic_map_type_qj);
        nowTv.setTextColor(getResources().getColor(R.color.black));
        imageView.setImageResource(checkedImage);
        tv.setTextColor(getResources().getColor(R.color.colorTheme));
        nowTv = tv;
    }

    public void showDialog() {
        mapDateListDialog.setDate(arrayList);
        getChildFragmentManager().beginTransaction().remove(mapDateListDialog).commit();
        mapDateListDialog.show(getChildFragmentManager(), "mapdate");
    }




    /**
     * ????????????
     */
    public void setMap(double lat, double lon) {
        mMapStatus = new MapStatus.Builder().target(new LatLng(lat, lon)).zoom(16).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));

        //????????????
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(App.app.getBdLocation().getRadius())
                // ?????????????????????????????????????????????????????????0-360
                .direction(App.app.getBdLocation().getDirection()).latitude(App.app.getBdLocation().getLatitude())
                .longitude(App.app.getBdLocation().getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        mPresenter.getShopsData(lat, lon,AppHttpPath.SHOP_LOCATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        // ???activity??????onResume???????????????mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        // ???activity??????onPause???????????????mMapView. onPause ()
        mMapView.onPause();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        // ???activity??????onDestroy???????????????mMapView.onDestroy()
        mClusterManager.clearItems();
        mMapView.onDestroy();
        if (mapBottomDialog != null) {
            mapBottomDialog.dismiss();
            mapBottomDialog = null;
        }
        super.onDestroy();
    }

    /**
     * ???????????????Marker???-??????
     *
     * @param result
     */
    public void addMarkers(ShopLocationB result) {
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_shop);
        List<MyItem> items = new ArrayList<>();
        for (ShopLocationB.ReturnValueBean valueBean : result.getReturnValue()) {
            items.add(new MyItem<>(valueBean, valueBean.getLatitude(), valueBean.getLongitude(),bitmapDescriptor));
        }

        mClusterManager.clearItems();
        mClusterManager.addItems(items);
        mClusterManager.cluster();
        dateType = 1;
    }

    /**
     * ???????????????Marker???-?????????
     *
     * @param result
     */
    public void addMarkersCmera(CameraLocBean result) {
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_camera);
        List<MyItem> items = new ArrayList<>();
        for (CameraLocBean.ReturnValueBean valueBean : result.getReturnValue()) {
            items.add(new MyItem(valueBean, Double.parseDouble(valueBean.getLatitude()), Double.parseDouble(valueBean.getLongitude()),bitmapDescriptor));
        }
        mClusterManager.clearItems();
        mClusterManager.addItems(items);
        mClusterManager.cluster();
        dateType = 2;
    }


    @Override
    protected HomepagePresent createPresenter() {
        return new HomepagePresent();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onSuccess(String tag, Object o) {
        switch (tag) {
            case AppHttpPath.CAMERA_LOCATION:
                //?????????????????????
                CameraLocBean cameraLocBean = (CameraLocBean) o;
                // ??????Marker???
                addMarkersCmera(cameraLocBean);
                break;
            case AppHttpPath.SHOP_LOCATION:
                //??????????????????
                ShopLocationB result = (ShopLocationB) o;
                // ??????Marker???
                addMarkers(result);
                break;
            default:
                break;
        }
    }



    @Override
    public void onMapLoaded() {
        mMapStatus = new MapStatus.Builder().zoom(9).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        //???????????????
        //?????????
        if (dateType == 2){
            List<MyItem> items = new ArrayList<MyItem>(cluster.getItems());
            if (mapBottomDialog == null) {
                mapBottomDialog = new BottomSheetDialog(mContext);
                View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_list_layout, null);
                mapBottomDialog.setContentView(view);
                RecyclerView bottomRv = view.findViewById(R.id.map_bottom_list_rv);
                clusterClickAdapter = new ClusterClickAdapter(R.layout.care_item_layout);
                getBaseActivity().initRecyclerview(bottomRv, clusterClickAdapter, LinearLayoutManager.VERTICAL);
                clusterClickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        MyItem item = (MyItem) adapter.getData().get(position);
//                        if (infowindow != null) {
//                            mBmapView.removeView(infowindow);
//                        }
                        if (nowMarker != null) {
                            nowMarker.setIcon(bitmapDescriptor);
                        }
                        nowMarker = null;
                        mBaiduMap.hideInfoWindow();
//                        clickItemType = 1;
                        onClusterItemClick(item);
//                    releaseBottomListDialog();
                    }
                });
            }
            clusterClickAdapter.setNewData(items);
            mapBottomDialog.show();
            return false;
        }
        //cluster.getItems()
        arrayList.clear();
        for (MyItem bean : cluster.getItems()) {
            arrayList.add((ShopLocationB.ReturnValueBean) bean.bean);
        }
        showDialog();
        //Toast.makeText(mContext, "???" + cluster.getSize() + "??????", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onClusterItemClick(MyItem item) {

        //????????????
        if (dateType == 2) {//?????????
            CameraLocBean.ReturnValueBean currentStreamCamera = (CameraLocBean.ReturnValueBean) item.getBean();
            startActivity(new Intent(mContext.getApplicationContext(), PlayerLiveActivity.class)
                    .putExtra(PlayerLiveActivity.STREAM_CAMERA_ID, currentStreamCamera.getId())
                    .putExtra(PlayerLiveActivity.STREAM_CAMERA_THUM_URL, currentStreamCamera.getEzOpen())
                    .putExtra(PlayerLiveActivity.STREAM_CAMERA_NUM, currentStreamCamera.getNumber()));
            return false;
        }
        //??????????????????
        if (((ShopLocationB.ReturnValueBean) item.bean).getId() == nowShopId) {
            App.app.activityTool.toShopActivity(mContext, ((ShopLocationB.ReturnValueBean) item.bean).getId());
        }
        nowShopId = ((ShopLocationB.ReturnValueBean) item.bean).getId();
        //????????????
        //??????
        try {
            nowMarker.setIcon(BitmapDescriptorFactory.fromBitmap(ImageUtil.combineBitmap(
                    BitmapFactory.decodeStream(getResources().getAssets().open("ic_map_shop_bg.png")),
                    ImageUtil.getRoundedCornerBitmap(ImageUtil.zoomImg(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_map_shop)), 200))));
        } catch (Exception e) {
            Log.e("Marker??????????????????", e.toString());
            e.printStackTrace();
        }
        FileRetrofit.getInstance().getFileService().getFile_GET(StringTools.getImageForCrmInt(((ShopLocationB.ReturnValueBean) item.bean).getLogoId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    InputStream inputStream = responseBody.byteStream();
                    nowMarker.setIcon(BitmapDescriptorFactory.fromBitmap(ImageUtil.combineBitmap(
                            BitmapFactory.decodeStream(getResources().getAssets().open("ic_map_shop_bg.png")),
                            ImageUtil.getRoundedCornerBitmap(ImageUtil.zoomImg(BitmapFactory.decodeStream(inputStream)), 200))));
                }, throwable -> {
                });
        arrayList.clear();
        arrayList.add((ShopLocationB.ReturnValueBean) item.bean);
        return false;
    }
}
