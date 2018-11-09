package com.huaxi.hailuo.model.http

import com.huaxi.hailuo.model.bean.*
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST(ApiSettings.GET_IDNUM_INFO)
    fun getIdNumInfo(@Body body: RequestBody): Flowable<MyHttpResponse<TianShenIdNumInfoBean>>
    @POST(ApiSettings.CHECK_UPGRADE)
    fun checkUpgrade(@Body body: RequestBody): Flowable<MyHttpResponse<UpgradeBean>>

    @POST(ApiSettings.SIGN_UP)
    fun signUp(@Body body: RequestBody): Flowable<MyHttpResponse<RegistLoginBean>>

    @POST(ApiSettings.SIGN_IN)
    fun signIn(@Body body: RequestBody): Flowable<MyHttpResponse<LoginBean>>

    @POST(ApiSettings.SIGN_OUT)
    fun signOut(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.SET_PASSWORD)
    fun setPassword(@Body body: RequestBody): Flowable<MyHttpResponse<SetPasswordBean>>

    @POST(ApiSettings.SIGN_UP_AGREEMENT)
    fun signUpAgreement(@Body body: RequestBody): Flowable<MyHttpResponse<SignUpAgreementBean>>

    @POST(ApiSettings.HOME_PAGE)
    fun homePage(@Body body: RequestBody): Flowable<MyHttpResponse<HomePageBean>>

    @POST(ApiSettings.CREDIT_ASSESS)
    fun creditAssess(@Body body: RequestBody): Flowable<MyHttpResponse<CreditAssessBean>>

    @POST(ApiSettings.ASSESS)
    fun assess(@Body body: RequestBody): Flowable<MyHttpResponse<AssessBean>>

    @POST(ApiSettings.GET_PROVINCE)
    fun getProvince(@Body body: RequestBody): Flowable<MyHttpResponse<ProvinceBean>>

    @POST(ApiSettings.GET_CITY)
    fun getCity(@Body body: RequestBody): Flowable<MyHttpResponse<CityBean>>

    @POST(ApiSettings.GET_BANK_LIST)
    fun getBankList(@Body body: RequestBody): Flowable<MyHttpResponse<BankListBean>>

    @POST(ApiSettings.BIND_BANK_CARD)
    fun bindBankCard(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.SEND_CODE)
    fun sendCode(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.CREDIT_APPLY)
    fun creditApply(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.RESET_PASSWORD)
    fun reSetpwd(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @Multipart
    @POST(ApiSettings.SUBMIT_FEEDBACK)
    fun upLoadOpion(@Part("json") description: RequestBody, @Part files: ArrayList<MultipartBody.Part>): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.SUBMIT_FEEDBACK)
    fun upLoadOpionWithoutImage(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>


    @POST(ApiSettings.GET_COUNTY)
    fun getCounty(@Body body: RequestBody): Flowable<MyHttpResponse<CountyBean>>

    @POST(ApiSettings.GET_USER_INFO)
    fun getUserInfo(@Body body: RequestBody): Flowable<MyHttpResponse<UserInfoBean>>

    @POST(ApiSettings.SAVE_USER_INFO)
    fun saveUserInfo(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.HELP_CENTER)
    fun helpCenter(@Body body: RequestBody): Flowable<MyHttpResponse<HelpCenterBean>>

    @POST(ApiSettings.GET_EXTRO_CONTACTS)
    fun getExtroContacts(@Body body: RequestBody): Flowable<MyHttpResponse<ExtroContactsBean>>

    @POST(ApiSettings.SAVE_EXTRO_CONTACTS)
    fun saveExtroContacts(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.ORDER_RECORD)
    fun getMyOrderData(@Body body: RequestBody): Flowable<MyHttpResponse<OrderListBean>>

    @POST(ApiSettings.SAVE_CONTACTS)
    fun saveContacts(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.GET_LAST_SMS_TIME)
    fun getLastSmsTime(@Body body: RequestBody): Flowable<MyHttpResponse<LastSmsTimeBean>>

    @POST(ApiSettings.SAVE_PHONE_INFO)
    fun savePhoneInfo(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.ASSESS_SUCCESS)
    fun assessSuccess(@Body body: RequestBody): Flowable<MyHttpResponse<AssessSuccessBean>>

    @POST(ApiSettings.GET_USER_BANK_LIST)
    fun getUserBankList(@Body body: RequestBody): Flowable<MyHttpResponse<MyBankListBean>>

    @POST(ApiSettings.DEL_BANK_CARD)
    fun deleteUserBankList(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.DOWN_REFRESH)
    fun downRefresh(@Body body: RequestBody): Flowable<MyHttpResponse<DownRefreshBean>>

    @POST(ApiSettings.RISK_WAITING_FEED_BACK)
    fun riskWaitingFeedback(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.GET_ORDER_INFO)
    fun getOrderDetail(@Body body: RequestBody): Flowable<MyHttpResponse<OrderDetailBean>>

    @POST(ApiSettings.GEN_ORDER)
    fun genOrder(@Body body: RequestBody): Flowable<MyHttpResponse<GenOrderBean>>


    @POST(ApiSettings.GET_REFUND_INFO)
    fun getRefundInfo(@Body body: RequestBody): Flowable<MyHttpResponse<RefundInfoBean>>


    @POST(ApiSettings.CONFIRM_REFUND)
    fun confirmRefund(@Body body: RequestBody): Flowable<MyHttpResponse<ConfirRefundBean>>

    @POST(ApiSettings.USER_LOCATION)
    fun userLocation(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.GET_MOBILE_LIST)
    fun getMobileList(@Body body: RequestBody): Flowable<MyHttpResponse<MobileListBean>>

    @POST(ApiSettings.FORGET_PWD)
    fun forgetPwd(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.MINE)
    fun getMineUrl(@Body body: RequestBody): Flowable<MyHttpResponse<MineBean>>

    @POST(ApiSettings.GET_COUPON_LIST)
    fun getCouponList(@Body body: RequestBody): Flowable<MyHttpResponse<CouponListBean>>

    @POST(ApiSettings.CHECK_USE_COUPON)
    fun checkUseCoupon(@Body body: RequestBody): Flowable<MyHttpResponse<CheckUseCouponBean>>

    @POST(ApiSettings.GET_REFUND_COUPON_LIST)
    fun getUseCoupon(@Body body: RequestBody): Flowable<MyHttpResponse<UseCoupon>>

    @POST(ApiSettings.GET_MESSAGE_CENTER_LIST)
    fun getMessageCenterList(@Body body: RequestBody): Flowable<MyHttpResponse<MessageCenterBean>>

    @POST(ApiSettings.READ_MESSAGE)
    fun readMsg(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.BURIED_POINT)
    fun buriedPoint(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.SEND_CODE)
    fun sendSmsCode(@Body body: RequestBody): Flowable<MyHttpResponse<SmsBean>>

    @POST(ApiSettings.CHECK_OLD_USER_STATUS)
    fun checkOldUserStatus(@Body body: RequestBody): Flowable<MyHttpResponse<GetSmsCodeBean>>

    //我要吐槽
    @POST(ApiSettings.GET_FEED_BACK)
    fun feedBack(@Body body: RequestBody): Flowable<MyHttpResponse<SpeekBean>>

    //首页认证优惠券
    @POST(ApiSettings.HOME_COUPON_DIALOG)
    fun homeCouponDialog(@Body body: RequestBody): Flowable<MyHttpResponse<HomeCouponBean>>
    //消息详情页
    @POST(ApiSettings.GET_MSG_INFO)
    fun getMsgInfo(@Body body: RequestBody): Flowable<MyHttpResponse<ArticalDetailBean>>
    //邀请好友
    @POST(ApiSettings.GET_INVITATION_FRIENDS)
    fun getInvitationFriends(@Body body: RequestBody): Flowable<MyHttpResponse<InviteFriendBean>>
    //我的现金红包
    @POST(ApiSettings.MYREDPACKET)
    fun myRedPacket(@Body body: RequestBody): Flowable<MyHttpResponse<CashRedPacketBean>>
    //我的现金红包收入明细/提现记录列表
    @POST(ApiSettings.GET_RED_PACKET_MONEY_LIST)
    fun getRedPacketMoneyList(@Body body: RequestBody): Flowable<MyHttpResponse<IncomeDetailOrderBean>>

    //我的现金红包提现
    @POST(ApiSettings.GET_WITHDRAW)
    fun get_withdraw(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>
}