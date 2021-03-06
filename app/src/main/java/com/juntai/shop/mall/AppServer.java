package com.juntai.shop.mall;

import com.juntai.mall.base.base.BaseResult;
import com.juntai.mall.base.bean.OpenLiveBean;
import com.juntai.shop.mall.bean.AddressInfoBean;
import com.juntai.shop.mall.bean.AddressListBean;
import com.juntai.shop.mall.bean.CameraDetailsBean;
import com.juntai.shop.mall.bean.CameraLocBean;
import com.juntai.shop.mall.bean.CartB;
import com.juntai.shop.mall.bean.GoodsB;
import com.juntai.shop.mall.bean.IntBean;
import com.juntai.shop.mall.bean.LogisticsBean;
import com.juntai.shop.mall.bean.MallHomeB;
import com.juntai.shop.mall.bean.MyCollcetB;
import com.juntai.shop.mall.bean.MyCommentB;
import com.juntai.shop.mall.bean.NearB;
import com.juntai.shop.mall.bean.OrderConfirmB;
import com.juntai.shop.mall.bean.OrderCreateBean;
import com.juntai.shop.mall.bean.OrderInfoBean;
import com.juntai.shop.mall.bean.OrderListBean;
import com.juntai.shop.mall.bean.OrderPayWxBean;
import com.juntai.shop.mall.bean.PlaceBean;
import com.juntai.shop.mall.bean.ReportTypeBesan;
import com.juntai.shop.mall.bean.ReturnDetailsBean;
import com.juntai.shop.mall.bean.ReturnReasonBean;
import com.juntai.shop.mall.bean.SearchGoodsBean;
import com.juntai.shop.mall.bean.SearchShopBean;
import com.juntai.shop.mall.bean.SettlementBean;
import com.juntai.shop.mall.bean.ShopCartsBean;
import com.juntai.shop.mall.bean.ShopCommentsBean;
import com.juntai.shop.mall.bean.ShopBean;
import com.juntai.shop.mall.bean.ShopInfoBean;
import com.juntai.shop.mall.bean.ShopLocationB;
import com.juntai.shop.mall.bean.StringBean;
import com.juntai.shop.mall.bean.UserB;
import com.juntai.shop.mall.bean.WeatherBean;
import com.juntai.shop.mall.bean.stream.StreamCameraBean;
import com.juntai.shop.mall.bean.stream.StreamCameraDetailBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AppServer {
    /**
     * ??????
     * @param account????????????
     * @param password?????????
     * @param weChatId?????????id
     * @param weChatName???????????????
     * @param qqId???qqid
     * @param qqName:qq??????
     * @return
     */
    @POST(AppHttpPath.LOGIN)
    Observable<ResponseBody> login(@Query("account")String account,
                             @Query("password")String password,
                             @Query("weChatId")String weChatId,
                             @Query("weChatName")String weChatName,
                             @Query("qqId")String qqId,
                             @Query("qqName")String qqName);

    @POST(AppHttpPath.BIND)
    Observable<BaseResult> bind(@Query("account")String account,
                                 @Query("token")String token,
                                 @Query("weChatId")String weChatId,
                                 @Query("weChatName")String weChatName,
                                 @Query("qqId")String qqId,
                                 @Query("qqName")String qqName);

    /**
     * ????????????
     * @param account
     * @param token
     * @return
     */
    @POST(AppHttpPath.LOGIN_OUT)
    Observable<BaseResult> loginOut(@Query("account")String account,
                                    @Query("token")String token);

    /**
     * ????????????
     * @param account
     * @param token
     * @param purchaserId
     * @return
     */
    @GET(AppHttpPath.USERINFO)
    Observable<UserB> userInfo(@Query("account")String account,
                               @Query("token")String token,
                               @Query("purchaserId")int purchaserId);

    /**
     * ????????????
     * @param account
     * @param token
     * @param password:rsa??????
     * @return
     */
    @POST(AppHttpPath.EDIT_PASS)
    Observable<BaseResult> editPass(@Query("account")String account,
                               @Query("token")String token,
                               @Query("password")String password);

    /**
     * ????????????
     * @param account
     * @param NewPassWord
     * @return
     */
    @POST(AppHttpPath.FIND_PASS)
    Observable<BaseResult> findPass(@Query("account")String account,
                                    @Query("NewPassWord")String NewPassWord);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @param id
     * @param nickName
     * @param birthday
     * @return
     */
    @POST(AppHttpPath.USER_EDIT)
    Observable<UserB> userInfoEdit(@Query("account")String account,
                                   @Query("token")String token,
                                   @Query("id")int id,
                                   @Query("nickName")String nickName,
                                   @Query("birthday")String birthday);

    /**
     * ???????????????
     * @param account
     * @param token
     * @param phone
     * @return
     */
    @POST(AppHttpPath.USER_PHONE)
    Observable<BaseResult> editPhone(@Query("account")String account,
                                     @Query("token")String token,
                                     @Query("phone")String phone);

    /**
     * ????????????
     * @param account
     * @param token
     * @param latitude
     * @param longitude
     * @return
     */
    @GET(AppHttpPath.WEATHER)
    Observable<WeatherBean> weather(@Query("account")String account,
                                    @Query("token")String token,
                                    @Query("latitude")String latitude,
                                    @Query("longitude")String longitude);
    /**
     * ????????????????????????????????????5KM??????
     * @param latitude
     * @param longitude
     * @return
     */
    @GET(AppHttpPath.SHOP_LOCATION)
    Observable<ShopLocationB> shopsLocation(@Query("latitude")String latitude,
                                            @Query("longitude")String longitude);

    /**
     * ???????????????
     * @return
     */
    @POST(AppHttpPath.CAMERA_LOCATION)
    Observable<CameraLocBean> cameraLocation();

    /**
     * ???????????????
     * @return
     */
    @POST(AppHttpPath.CAMERA_INFO)
    Observable<CameraDetailsBean> cameraInfo(@Query("cameraId")int cameraId);
    /**
     * ????????????
     * @param account
     * @param token
     * @param shopId?????????id
     * @param purchaserId?????????id
     * @return
     */
    @GET(AppHttpPath.SHOP_INFO1)
    Observable<ShopBean> shop(@Query("account")String account,
                              @Query("token")String token,
                              @Query("shopId")int shopId,
                              @Query("purchaserId")int purchaserId);
    /**
     * ????????????
     * @param account
     * @param token
     * @param merchantId???	??????id
     * @param typeId????????????????????????1:????????????2:????????????3:????????????4:????????????5:?????????
     * @param currentPage?????????????????????1?????????????????????1
     * @param pageSize?????????????????????????????????10
     * @return
     */
    @GET(AppHttpPath.SHOP_COMMENTS)
    Observable<ShopCommentsBean> shopComments(@Query("account")String account,
                                              @Query("token")String token,
                                              @Query("merchantId")int merchantId,
                                              @Query("typeId")int typeId,
                                              @Query("currentPage")int currentPage,
                                              @Query("pageSize")int pageSize);


    /**
     * ???????????????????????????
     * @param account
     * @param token
     * @param shopId
     * @return
     */
    @GET(AppHttpPath.SHOP_INFO2)
    Observable<ShopInfoBean> shopInfo(@Query("account")String account,
                                      @Query("token")String token,
                                      @Query("shopId")int shopId);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @return
     */
    @POST(AppHttpPath.REPORT_TYPE)
    Observable<ReportTypeBesan> reportType(@Query("account")String account,
                                           @Query("token")String token);

    /**
     * ???????????????
     * @param account
     * @param token
     * @param purchaserId
     * @param merchantId
     * @return
     */
    @GET(AppHttpPath.SHOP_CART)
    Observable<ShopCartsBean> shopCarts(@Query("account")String account,
                                        @Query("token")String token,
                                        @Query("purchaserId")int purchaserId,
                                        @Query("merchantId")int merchantId);

    /**
     * ???????????????
     * @param account
     * @param token
     * @param purchaserId
     * @param shopId
     * @param commodityJson
     * @return
     */
    @POST(AppHttpPath.CART_SYS)
    Observable<BaseResult> cartSys(@Query("account")String account,
                                   @Query("token")String token,
                                   @Query("purchaserId")int purchaserId,
                                   @Query("merchantId")int shopId,
                                   @Query("commodityJson") String commodityJson);

    /**
     * ???????????????
     * @param account???
     * @param token???
     * @param purchaserId???
     * @param merchantId???
     */
    @POST(AppHttpPath.SETTLEMENT)
    Observable<SettlementBean> settlement(@Query("account")String account,
                                          @Query("token")String token,
                                          @Query("purchaserId")int purchaserId,
                                          @Query("merchantId")int merchantId);

    /**
     * ????????????
     * @param account:
     * @param token:
     * @param purchaserId:??????id
     * @param merchantId:??????id
     * @param addressId:??????id
     * @param sumPackingCharges:????????????
     * @param footing:????????????
     * @param remark?????????
     * @return
     */
    @POST(AppHttpPath.ORDER_CREATE)
    Observable<OrderCreateBean> orderCreate(@Query("account")String account,
                                            @Query("token")String token,
                                            @Query("purchaserId")int purchaserId,
                                            @Query("merchantId")int merchantId,
                                            @Query("addressId")int addressId,
                                            @Query("sumPackingCharges")double sumPackingCharges,
                                            @Query("footing")double footing,
                                            @Query("remark")String remark);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @param purchaserId
     * @return
     */
    @GET(AppHttpPath.ADDRESS_LIST)
    Observable<AddressListBean> addressList(@Query("account")String account,
                                            @Query("token")String token,
                                            @Query("purchaserId")int purchaserId);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @param addressId
     * @return
     */
    @GET(AppHttpPath.ADDRESS_INFO)
    Observable<AddressInfoBean> addressInfo(@Query("account")String account,
                                            @Query("token")String token,
                                            @Query("addressId")int addressId);

    /**
     * ????????????
     * @param account
     * @param token
     * @param addressId
     * @return
     */
    @GET(AppHttpPath.ADDRESS_DEL)
    Observable<BaseResult> addressDel(@Query("account")String account,
                                      @Query("token")String token,
                                      @Query("addressId")int addressId);

    /**
     * ????????????
     * @param commodityId:??????id
     * @param purchaserId:??????id
     * @return
     */
    @POST(AppHttpPath.GOODS_INFO)
    Observable<GoodsB> GoodsDetalis(@Query("commodityId") int commodityId,@Query("purchaserId") int purchaserId);

    /**
     * ????????????
     * @param account
     * @param token
     * @param purchaserId?????????id
     * @param typeId:???0?????????????????????1??????????????????
     * @param latitude
     * @param longitude
     * @return
     */
    @GET(AppHttpPath.COLLECT_MY)
    Observable<MyCollcetB> collectsMy(@Query("account")String account,
                                      @Query("token")String token,
                                      @Query("purchaserId")int purchaserId,
                                      @Query("typeId")int typeId,
                                      @Query("latitude")String latitude,
                                      @Query("longitude")String longitude);

    /**
     * ????????????
     * @param account
     * @param token
     * @param purchaserId
     * @param typeId
     * @param latitude
     * @param longitude
     * @return
     */
    @GET(AppHttpPath.SHARE_MY)
    Observable<MyCollcetB> shareMy(@Query("account")String account,
                                      @Query("token")String token,
                                      @Query("purchaserId")int purchaserId,
                                      @Query("typeId")int typeId,
                                      @Query("latitude")String latitude,
                                      @Query("longitude")String longitude);
    /**
     * ????????????
     * @param account
     * @param token
     * @param purchaserId
     * @return
     */
    @GET(AppHttpPath.COMMENT_MY)
    Observable<MyCommentB> commentMy(@Query("account")String account,
                                     @Query("token")String token,
                                     @Query("purchaserId")int purchaserId);
    /**
     * ????????????
     * @param account
     * @param token
     * @param purchaserId
     * @param status???(0??????????????????1??????????????????2??????????????????3??????????????????4??????????????????5???????????????6:??????????????????7?????????????????????8??????????????????
     * @return
     */
    @POST(AppHttpPath.ORDER_MY)
    Observable<OrderListBean> orderMy(@Query("account")String account,
                                      @Query("token")String token,
                                      @Query("purchaserId")int purchaserId,
                                      @Query("status")int status);

    @POST(AppHttpPath.ORDER_DETAILS)
    Observable<OrderInfoBean> orderInfo(@Query("account")String account,
                                        @Query("token")String token,
                                        @Query("orderId")int orderId);
    /**
     * ??????????????????
     * @param path
     * @param account
     * @param token
     * @param purchaserId
     * @param orderId?????????id
     * @param time:???????????? - ??????????????????????????????)???--
     * @return
     */
    @POST()
    Observable<BaseResult> orderEdit(@Url String path,
                                     @Query("account")String account,
                                     @Query("token")String token,
                                     @Query("purchaserId")int purchaserId,
                                     @Query("orderId")int orderId,
                                     @Query("time")String time);
    @GET(AppHttpPath.LOGISTICS)
    Observable<LogisticsBean> logistics(@Query("account")String account,
                                        @Query("token")String token,
                                        @Query("orderId")int orderId);

    /**
     * ?????????????????????(???????????????)
     * @param account
     * @param token
     * @param purchaserId
     * @param typeId??????0???????????????1????????????
     * @param merchantId:??????id
     * @param commodityId:??????id
     * @param isCollect?????????????????????-???????????????????????????
     * @return
     */
    @POST(AppHttpPath.COLLECTS_OPERATE)
    Observable<IntBean> collectOperateOne(@Query("account")String account,
                                          @Query("token")String token,
                                          @Query("purchaserId")int purchaserId,
                                          @Query("typeId")int typeId,
                                          @Query("merchantId")int merchantId,
                                          @Query("commodityId")int commodityId,
                                          @Query("isCollect")int isCollect,
                                          @Query("ids")int ids);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @param typeId??????0???????????????1????????????
     * @param isCollect?????????????????????-??????1
     * @param ids
     * @return
     */
    @POST(AppHttpPath.COLLECTS_OPERATE)
    Observable<BaseResult> collectsOperate(@Query("account")String account,
                                           @Query("token")String token,
                                           @Query("typeId")int typeId,
                                           @Query("isCollect")int isCollect,
                                           @Query("ids")List<Integer> ids);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @param ids
     * @return
     */
    @POST(AppHttpPath.DELETE_SHARE)
    Observable<BaseResult> deleteShare(@Query("account")String account,
                                       @Query("token")String token,
                                       @Query("ids")List<Integer> ids);

    /**
     * ??????-??????
     * @param keyWord
     * @param latitude
     * @param longitude
     * @param typeId
     * @param currentPage
     * @param pageSize
     * @return
     */
    @POST(AppHttpPath.SEARCH)
    Observable<SearchShopBean> searchShop(@Query("keyWord")String keyWord,
                                          @Query("latitude")String latitude,
                                          @Query("longitude")String longitude,
                                          @Query("typeId")int typeId,
                                          @Query("currentPage")int currentPage,
                                          @Query("pageSize")int pageSize);
    /**
     * ????????????
     * */
    @POST(AppHttpPath.SEARCH)
    Observable<SearchGoodsBean> searchGoods(@Query("keyWord")String keyWord,
                                            @Query("latitude")String latitude,
                                            @Query("longitude")String longitude,
                                            @Query("typeId")int typeId,
                                            @Query("currentPage")int currentPage,
                                            @Query("pageSize")int pageSize);

    /**
     * ????????????????????????
     * @param typeId:(1:??????)(2:????????????)
     * @return
     */
    @POST(AppHttpPath.RETURNCAUSE)
    Observable<ReturnReasonBean> returnCause(@Query("typeId")int typeId);

    /**
     * ?????????????????????
     * @return
     */
    @GET(AppHttpPath.GET_PROVINCE)
    Observable<PlaceBean> getProvince();
    /**
     * @param cityName:?????????
     * @return
     */
    @GET(AppHttpPath.GET_CITY)
    Observable<PlaceBean> getCity(@Query("cityName")long cityName);
    @GET(AppHttpPath.GET_AREA)
    Observable<PlaceBean> getArea(@Query("cityName")long cityName);
    @GET(AppHttpPath.GET_STREET)
    Observable<PlaceBean> getStreet(@Query("cityName")long cityName);

    /**
     * RequestBody????????????
     * @param jsonBody
     * @return
     */
    @POST()
    Observable<BaseResult> submitBody(@Url String path,@Body RequestBody jsonBody);

    /**
     * ????????????-??????
     * @param path
     * @param commodityId
     * @param jsonBody
     * @return
     */
    @POST()
    Observable<BaseResult> returnGoods(@Url String path,@Query("commodityId")List<Integer> commodityId,@Body RequestBody jsonBody);

    /**
     * ??????????????????
     * @param account
     * @param token
     * @param orderId
     * @return
     */
    @POST(AppHttpPath.RETURN_INFO)
    Observable<ReturnDetailsBean> returnInfo(@Query("account")String account,
                                             @Query("token")String token,
                                             @Query("orderId")int orderId);

    /**
     * ???????????????
     * @param account
     * @param token
     * @param orderId
     * @return
     */
    @POST(AppHttpPath.ORDERINFO_ZFB)
    Observable<StringBean> orderInfoZfb(@Query("account")String account,
                                        @Query("token")String token,
                                        @Query("orderId")int orderId);

    /**
     * ????????????
     * @param account
     * @param token
     * @param orderId
     * @return
     */
    @POST(AppHttpPath.ORDERINFO_WX)
    Observable<OrderPayWxBean> orderInfoWx(@Query("account")String account,
                                           @Query("token")String token,
                                           @Query("orderId")int orderId);



    /*====================================================    ?????????   ==============================================================*/




    /**
     * ???????????????????????????
     *
     * @return
     */
    @POST(AppHttpPath.STREAM_CAMERAS)
    Observable<StreamCameraBean> getAllStreamCameras(@Body RequestBody requestBody);


    /**
     * ????????????????????????????????????????????????
     *
     * @return
     */
    @POST(AppHttpPath.STREAM_CAMERAS_FROM_VCR)
    Observable<StreamCameraBean> getAllStreamCamerasFromVCR(@Body RequestBody requestBody);

    /**
     * ???????????????
     *
     * @return
     */
    @POST(AppHttpPath.STREAM_CAMERAS_DETAIL)
    Observable<StreamCameraDetailBean> getStreamCameraDetail(@Body RequestBody requestBody);

    /**
     * ???????????????
     *
     * @return
     */
    @POST(AppHttpPath.UPLOAD_STREAM_CAMERAS_THUMB)
    Observable<BaseResult> uploadStreamCameraThumbPic(@Body RequestBody requestBody);

    /**
     * ???????????????
     *
     * ???????????????
     * 	"channelid":  (?????????)   ??????20?????????
     * 	"type":       (??????)   	 ???????????????????????????1???udp 2???tcp?????? 3???tcp??????
     * 	"videourltype":  (?????????)   ???????????????rtsp?????????rtsp??????  rtmp?????????rtmp?????? hls?????????hls??????
     * @param channelid
     * @param type
     * @param videourltype
     * @return
     */
    @GET(AppHttpPath.BASE_CAMERA_URL+"/vss/open_stream/{channelid}/{type}/{videourltype}")
    Observable<OpenLiveBean> openStream(@Path("channelid") String channelid, @Path("type")String type,
                                        @Path("videourltype")String videourltype);

    /**
     * ??????id   ???????????????
     * @param sessionid
     * @return
     */
    @GET(AppHttpPath.BASE_CAMERA_URL+"/vss/video_keepalive/{sessionid}")
    Observable<OpenLiveBean> keepAlive(@Path("sessionid") String sessionid);
    /**
     * ??????
     * @param channelid
     * @return
     */
    @GET(AppHttpPath.BASE_CAMERA_URL+"/vss/get_image/{channelid}/{type}")
    Observable<OpenLiveBean> capturePic(@Path("channelid") String channelid,@Path("type") String type);



}
