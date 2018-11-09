package com.huaxi.hailuo.model.http

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.log.okHttpLog.HttpLoggingInterceptorM
import com.huaxi.hailuo.log.okHttpLog.LogInterceptor
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.util.Utils
import io.reactivex.Flowable
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.TimeUnit

object ApiManager {

    //正式
    val HOST_PRO: String = "http://hailuoapi.crslljj.com/"
    // 预发布
    val HOST_PRE: String = "http://pre.hailuoapi.crslljj.com/"
    //测试
    val HOST_DEV: String = "http://dev.hailuoapi.huaxick.com/"
    // 审核
//    val HOST_CHECK: String = "http://review.recycle.tianshenjr.com/"
    val HTTP_LOG_TAG: String = "http"

    var mHost = ""

    private lateinit var mApiService: ApiService

    init {
        val retrofit = initRetrofit()
        initServices(retrofit)
    }

    private fun initRetrofit(): Retrofit {


        val builder = OkHttpClient.Builder()
        //打印日志 不区分是否是debug模式
        val interceptor = HttpLoggingInterceptorM(LogInterceptor(HTTP_LOG_TAG))
        interceptor.level = HttpLoggingInterceptorM.Level.BODY
        builder.addInterceptor(interceptor)

//        builder.addInterceptor(FakeApiInterceptor())

        mHost = when (App.instance.mCurrentHost) {
            App.HOST.DEV -> HOST_DEV
            App.HOST.PRE -> HOST_PRE
            App.HOST.PRO -> HOST_PRO
        }

//        val cachePath = Utils.getCachePath()
        val cachePath = Utils.getOKHttpCachePath()
        val cacheFile = File(cachePath)
        val cache = Cache(cacheFile, (1024 * 1024 * 50).toLong())
        val cacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!Utils.isNetworkConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (Utils.isNetworkConnected()) {
                val maxAge = 0
                // 有网络时, 不缓存, 最大保存时长为0
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")
                        .build()
            } else {
                // 无网络时，设置超时为4周
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build()
            }
        }
//        val apikey = Interceptor { chain ->
//            var request = chain.request()
//            request = request.newBuilder()
//                    .addHeader("apikey", "header")
//                    .build()
//            chain.proceed(request)
//        }
//        //设置统一的请求头部参数
//        builder.addInterceptor(apikey)
        builder.addNetworkInterceptor(cacheInterceptor)
        builder.addInterceptor(cacheInterceptor)
        //设置缓存
        builder.cache(cache)
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        //防止抓包
//        builder.proxy(Proxy.NO_PROXY)
        val okHttpClient = builder.build()
        return Retrofit.Builder().baseUrl(mHost)
                .client(okHttpClient)
                .addConverterFactory(CheckGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun initServices(retrofit: Retrofit) {
        mApiService = retrofit.create(ApiService::class.java)
    }
    //得到用户认证的信息
    fun getIdNumInfo(body: RequestBody): Flowable<MyHttpResponse<TianShenIdNumInfoBean>> = mApiService.getIdNumInfo(body)
    //检查更新
    fun checkUpgrade(body: RequestBody): Flowable<MyHttpResponse<UpgradeBean>> = mApiService.checkUpgrade(body)

    //用户注册
    fun signUp(body: RequestBody): Flowable<MyHttpResponse<RegistLoginBean>> = mApiService.signUp(body)

    //用户登录
    fun signIn(body: RequestBody): Flowable<MyHttpResponse<LoginBean>> = mApiService.signIn(body)

    //退出登录
    fun signOut(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.signOut(body)

    //设置密码
    fun setPassword(body: RequestBody): Flowable<MyHttpResponse<SetPasswordBean>> = mApiService.setPassword(body)

    //注册协议
    fun signUpAgreement(body: RequestBody): Flowable<MyHttpResponse<SignUpAgreementBean>> = mApiService.signUpAgreement(body)

    //主页面
    fun homePage(body: RequestBody): Flowable<MyHttpResponse<HomePageBean>> = mApiService.homePage(body)

    //信用评估
    fun creditAssess(body: RequestBody): Flowable<MyHttpResponse<CreditAssessBean>> = mApiService.creditAssess(body)

    //评估
    fun assess(body: RequestBody): Flowable<MyHttpResponse<AssessBean>> = mApiService.assess(body)

    //得到省
    fun getProvince(body: RequestBody): Flowable<MyHttpResponse<ProvinceBean>> = mApiService.getProvince(body)

    //得到城市
    fun getCity(body: RequestBody): Flowable<MyHttpResponse<CityBean>> = mApiService.getCity(body)

    //得到银行卡列表
    fun getBankList(body: RequestBody): Flowable<MyHttpResponse<BankListBean>> = mApiService.getBankList(body)

    //绑定银行卡
    fun bindBankCard(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.bindBankCard(body)

    //获取验证码
    fun sendCode(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.sendCode(body)

    //信用提交
    fun creditApply(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.creditApply(body)

    //重置密码
    fun reSetpwd(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.reSetpwd(body)

    //意见反馈
    fun upLoadOpion(body: RequestBody, @Part files: ArrayList<MultipartBody.Part>): Flowable<MyHttpResponse<Any>> = mApiService.upLoadOpion(body, files)

    fun upLoadOpionWithoutImage(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.upLoadOpionWithoutImage(body)


    //获取区域
    fun getCounty(body: RequestBody): Flowable<MyHttpResponse<CountyBean>> = mApiService.getCounty(body)

    //获取用户个人信息
    fun getUserInfo(body: RequestBody): Flowable<MyHttpResponse<UserInfoBean>> = mApiService.getUserInfo(body)

    //保存用户个人信息
    fun saveUserInfo(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.saveUserInfo(body)

    //帮助中心
    fun helpCenter(body: RequestBody): Flowable<MyHttpResponse<HelpCenterBean>> = mApiService.helpCenter(body)

    //获取紧急联系人信息
    fun getExtroContacts(body: RequestBody): Flowable<MyHttpResponse<ExtroContactsBean>> = mApiService.getExtroContacts(body)

    //保存紧急联系人信息
    fun saveExtroContacts(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.saveExtroContacts(body)

    //保存紧急联系人信息
    fun getMyOrderData(body: RequestBody): Flowable<MyHttpResponse<OrderListBean>> = mApiService.getMyOrderData(body)

    //保存所有联系人
    fun saveContacts(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.saveContacts(body)

    //获取上次短信上传时间
    fun getLastSmsTime(body: RequestBody): Flowable<MyHttpResponse<LastSmsTimeBean>> = mApiService.getLastSmsTime(body)

    //保存手机信息 上传短信等
    fun savePhoneInfo(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.savePhoneInfo(body)

    //评估结果成功页面
    fun assessSuccess(body: RequestBody): Flowable<MyHttpResponse<AssessSuccessBean>> = mApiService.assessSuccess(body)

    //我的银行卡列表
    fun getUserBankList(body: RequestBody): Flowable<MyHttpResponse<MyBankListBean>> = mApiService.getUserBankList(body)

    //删除我的银行卡列表
    fun deleteUserBankList(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.deleteUserBankList(body)

    //手机评估页面-下拉刷新
    fun downRefresh(body: RequestBody): Flowable<MyHttpResponse<DownRefreshBean>> = mApiService.downRefresh(body)

    //风控等待超长反馈
    fun riskWaitingFeedback(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.riskWaitingFeedback(body)

    //订单详情
    fun getOrderDetail(body: RequestBody): Flowable<MyHttpResponse<OrderDetailBean>> = mApiService.getOrderDetail(body)

    //订单详情
    fun genOrder(body: RequestBody): Flowable<MyHttpResponse<GenOrderBean>> = mApiService.genOrder(body)

    //获取退款详情
    fun getRefundInfo(body: RequestBody): Flowable<MyHttpResponse<RefundInfoBean>> = mApiService.getRefundInfo(body)

    //确认退款
    fun confirmRefund(body: RequestBody): Flowable<MyHttpResponse<ConfirRefundBean>> = mApiService.confirmRefund(body)

    //上传用户地址
    fun userLocation(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.userLocation(body)

    //得到手机型号列表
    fun getMobileList(body: RequestBody): Flowable<MyHttpResponse<MobileListBean>> = mApiService.getMobileList(body)

    //忘记密码
    fun forgetPwd(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.forgetPwd(body)

    //我的--客服URL
    fun getMineUrl(body: RequestBody): Flowable<MyHttpResponse<MineBean>> = mApiService.getMineUrl(body)

    //优惠券列表
    fun getCouponList(body: RequestBody): Flowable<MyHttpResponse<CouponListBean>> = mApiService.getCouponList(body)

    //检查是否能使用优惠券
    fun checkUseCoupon(body: RequestBody): Flowable<MyHttpResponse<CheckUseCouponBean>> = mApiService.checkUseCoupon(body)

    //获取可用优惠券
    fun getUseCoupon(body: RequestBody): Flowable<MyHttpResponse<UseCoupon>> = mApiService.getUseCoupon(body)

    //消息中心列表
    fun getMessageCenterList(body: RequestBody): Flowable<MyHttpResponse<MessageCenterBean>> = mApiService.getMessageCenterList(body)

    //设置消息已读
    fun readMessage(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.readMsg(body)

    //埋点
    fun buriedPoint(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.buriedPoint(body)


    //获取验证码
    fun sendSmsCode(body: RequestBody): Flowable<MyHttpResponse<SmsBean>> = mApiService.sendSmsCode(body)

    //获取checkOldUserStatus
    fun checkOldUserStatus(body: RequestBody): Flowable<MyHttpResponse<GetSmsCodeBean>> = mApiService.checkOldUserStatus(body)

    //获取我要吐槽的内容
    fun getSpeekType(body: RequestBody): Flowable<MyHttpResponse<SpeekBean>> = mApiService.feedBack(body)

    //首页弹框
    fun homeCouponDialog(body: RequestBody): Flowable<MyHttpResponse<HomeCouponBean>> = mApiService.homeCouponDialog(body)

    //消息详情页
    fun getMsgInfo(body: RequestBody): Flowable<MyHttpResponse<ArticalDetailBean>> = mApiService.getMsgInfo(body)

    //邀请好友
    fun getInvitaionFriends(body: RequestBody): Flowable<MyHttpResponse<InviteFriendBean>> = mApiService.getInvitationFriends(body)

    //我的现金红包
    fun getMyRedPacket(body: RequestBody): Flowable<MyHttpResponse<CashRedPacketBean>> = mApiService.myRedPacket(body)

    //我的收入和提现记录红包列表
    fun getMyRedPacketMoneyList(body: RequestBody): Flowable<MyHttpResponse<IncomeDetailOrderBean>> = mApiService.getRedPacketMoneyList(body)

    //我的现金红包提现
    fun get_withdraw(body: RequestBody): Flowable<MyHttpResponse<Any>> = mApiService.get_withdraw(body)
}