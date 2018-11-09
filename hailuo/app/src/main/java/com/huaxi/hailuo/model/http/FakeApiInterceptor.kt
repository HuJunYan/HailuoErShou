package com.huaxi.hailuo.model.http

import android.text.TextUtils
import com.huaxi.hailuo.BuildConfig
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.ui.adapter.CreditAdapter
import com.huaxi.hailuo.util.GsonUtil
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * 伪造测试数据拦截器
 */
class FakeApiInterceptor : Interceptor {
    private val LOG_TAG = "http"
    //返回的http状态码
    private val HTTP_CODE = 200

    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response
        if (BuildConfig.DEBUG) {
            val url = chain.request().url().toString()
            val responseJson = checkURL2FakeJson(url)
            if (TextUtils.isEmpty(responseJson)) { //测试数据没有写的情况下走真实接口
                return chain.proceed(chain.request())
            }

            //打印log
            //打印request url
            LogUtil.d(LOG_TAG, url)
            //打印request json
            var charset: Charset? = Charset.forName("UTF-8")
            val requestBody = chain.request().body()
            val contentType = requestBody!!.contentType()
            charset = contentType!!.charset(charset)
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            LogUtil.d(LOG_TAG, "--> start")
            LogUtil.json(LOG_TAG, buffer.readString(charset!!))
            LogUtil.d(LOG_TAG, "--> end")

            //打印http code
            LogUtil.d(LOG_TAG, "<--http code:" + HTTP_CODE)
            LogUtil.d(LOG_TAG, "<-- start")
            //打印response log
            LogUtil.json(LOG_TAG, responseJson)
            LogUtil.d(LOG_TAG, "<-- end")

            response = Response.Builder()
                    .code(HTTP_CODE)
                    .addHeader("Content-Type", "application/json")
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseJson))
                    .message(responseJson)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .build()
        } else {
            response = chain.proceed(chain.request())
        }
        return response
    }

    /**
     * 根据URL生成测试数据
     */
    private fun checkURL2FakeJson(url: String): String {
        var json = ""
        when (url) {
        //检查更新
            ApiManager.mHost + ApiSettings.CHECK_UPGRADE -> {
//                val bean = UpgradeBean("1", "1", "1", "1", "1", "1", "1")
//                val myHttpResponse = MyHttpResponse<UpgradeBean>(0, "success", bean)
//                json = GsonUtil.bean2json(myHttpResponse)

                val root = JSONObject()
                val data = JSONObject()
                data.put("download_url", "http://cdn.tianshenjr.com/test1.0.5.apk")
                root.put("msg", "有新版本！")
                root.put("code", 888)
                root.put("data", data)
                json = root.toString()
            }
        //用户注册
            ApiManager.mHost + ApiSettings.SIGN_UP -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //用户登录
            ApiManager.mHost + ApiSettings.SIGN_IN -> {
                val bean = LoginBean("1", "1", "1")
                val myHttpResponse = MyHttpResponse<LoginBean>(0, "success", bean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //设置密码
            ApiManager.mHost + ApiSettings.RESET_PASSWORD -> {
                val bean = SetPasswordBean("1")
                val myHttpResponse = MyHttpResponse<SetPasswordBean>(0, "success", bean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //首页
            ApiManager.mHost + ApiSettings.HOME_PAGE -> {
                val scroll_bar_list = ArrayList<ContentBean>()
                scroll_bar_list.add(ContentBean("1"))
                scroll_bar_list.add(ContentBean("2"))
                scroll_bar_list.add(ContentBean("3"))
                val ScrollBarBean = ScrollBarBean("www.baidu.com/img/bd_logo1.png", scroll_bar_list)


                val banner_list = ArrayList<BannerBean>()
                val bannerBean1 = BannerBean("www.baidu.com/img/bd_logo1.png", "www.baidu.com/img/bd_logo1.png")
                val bannerBean2 = BannerBean("www.baidu.com/img/bd_logo1.png", "www.baidu.com/img/bd_logo1.png")
                banner_list.add(bannerBean1)
                banner_list.add(bannerBean2)

                val mobileInfoBean = MobileInfoBean("www.baidu.com/img/bd_logo1.png", "测试标题", "测试型号", "64", "100000", "1")

                val flow_list = ArrayList<FlowBean>()
                val flowBean1 = FlowBean("www.baidu.com/img/bd_logo1.png", "测试标题")
                val flowBean2 = FlowBean("www.baidu.com/img/bd_logo1.png", "测试标题")
                flow_list.add(flowBean1)
                flow_list.add(flowBean2)
                val recoveryFlowBean = RecoveryFlowBean("测试标题", flow_list)

                val notice = OrderDetailBean.Notice()
                notice.notice_timestamp = (System.currentTimeMillis() / 1000).toInt().toString()
                notice.notice_title = "海螺商城还款提醒"

                val bean = HomePageBean(ScrollBarBean, banner_list, mobileInfoBean, recoveryFlowBean, "测试标题", "测试标题", "123", "2", "1", notice, "http://www.baidu.com", "1", "", "1")
                val myHttpResponse = MyHttpResponse<HomePageBean>(0, "success", bean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //省份列表
            ApiManager.mHost + ApiSettings.GET_PROVINCE -> {
                var list = ArrayList<ProvinceBean.ProvinceItemBean>()
                var proviceList = ProvinceBean(list)
                list.add(ProvinceBean.ProvinceItemBean("1", "北京"))
                list.add(ProvinceBean.ProvinceItemBean("2", "天津"))
                val myHttpResponse = MyHttpResponse<ProvinceBean>(0, "success", proviceList)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //城市列表
            ApiManager.mHost + ApiSettings.GET_CITY -> {
                var list = ArrayList<CityBean.CityItemBean>()
                var proviceList = CityBean(list)
                list.add(CityBean.CityItemBean("3", "东城区"))
                list.add(CityBean.CityItemBean("4", "西城区"))
                val myHttpResponse = MyHttpResponse<CityBean>(0, "success", proviceList)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //区域列表
            ApiManager.mHost + ApiSettings.GET_COUNTY -> {
                var list = ArrayList<CountyBean.CountyItemBean>()
                var county = CountyBean(list)
                list.add(CountyBean.CountyItemBean("10", "一个县"))
                list.add(CountyBean.CountyItemBean("11", "两个县"))
                val myHttpResponse = MyHttpResponse<CountyBean>(0, "success", county)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //验证码
            ApiManager.mHost + ApiSettings.SEND_CODE -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //银行卡列表
            ApiManager.mHost + ApiSettings.GET_BANK_LIST -> {
                var bankList = ArrayList<BankListBean.BankBean>()
                var bankListBean = BankListBean(bankList)
                bankList.add(BankListBean.BankBean("5", "中国银行"))
                bankList.add(BankListBean.BankBean("6", "中信银行"))
                val myHttpResponse = MyHttpResponse<BankListBean>(0, "success", bankListBean)
                json = GsonUtil.bean2json(myHttpResponse)
            }

        //银行卡列表
            ApiManager.mHost + ApiSettings.HELP_CENTER -> {
                var bankList = ArrayList<HelpCenterBean.HelpListBean>()
                var helpcenter = HelpCenterBean(bankList)
                bankList.add(HelpCenterBean.HelpListBean("标题1", "内容1"))
                bankList.add(HelpCenterBean.HelpListBean("标题2", "内容2"))
                val myHttpResponse = MyHttpResponse<HelpCenterBean>(0, "success", helpcenter)
                json = GsonUtil.bean2json(myHttpResponse)
            }

        //绑定银行卡
            ApiManager.mHost + ApiSettings.BIND_BANK_CARD -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //获取认证信息
            ApiManager.mHost + ApiSettings.CREDIT_ASSESS -> {
                var mData = ArrayList<CreditAssessBean.CreditAssessItemBean>()
                var mData2 = ArrayList<CreditAssessBean.CreditAssessItemBean>()
                var data = CreditAssessBean(mData, mData2);
                mData.add(CreditAssessBean.CreditAssessItemBean("1",
                        "http://pic4.nipic.com/20091217/3885730_124701000519_2.jpg", "身份认证", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData.add(CreditAssessBean.CreditAssessItemBean("2",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg"
                        , "收款银行卡", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData.add(CreditAssessBean.CreditAssessItemBean("5", "http://pic4.nipic.com/20091217/3885730_124701000519_2.jpg"
                        , "个人信息", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData.add(CreditAssessBean.CreditAssessItemBean("3",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg"
                        , "运营商认证", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData.add(CreditAssessBean.CreditAssessItemBean("4",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "紧急联系人", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData.add(CreditAssessBean.CreditAssessItemBean("6",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "芝麻信用", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData.add(CreditAssessBean.CreditAssessItemBean("7",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "淘宝", "1", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData2.add(CreditAssessBean.CreditAssessItemBean("8",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "京东", "2", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData2.add(CreditAssessBean.CreditAssessItemBean("9",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "信用卡", "2", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData2.add(CreditAssessBean.CreditAssessItemBean("10",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "社保", "2", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData2.add(CreditAssessBean.CreditAssessItemBean("11",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "公积金", "2", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData2.add(CreditAssessBean.CreditAssessItemBean("12",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "学信网", "2", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))
                mData2.add(CreditAssessBean.CreditAssessItemBean("13",
                        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "征信", "2", "1", "http://www.baidu.com", CreditAdapter.ItemType.TYPE_REQUIRED))

                val myHttpResponse = MyHttpResponse<CreditAssessBean>(0, "success", data)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //获取个人信息
            ApiManager.mHost + ApiSettings.GET_USER_INFO -> {
                var list = ArrayList<UserInfoBean.OccupationBean>()
                var subList1 = ArrayList<UserInfoBean.SubOccupation>()
                var subList2 = ArrayList<UserInfoBean.SubOccupation>()
                subList1.add(UserInfoBean.SubOccupation("100", "护士"))
                subList1.add(UserInfoBean.SubOccupation("100", "医生"))
                subList1.add(UserInfoBean.SubOccupation("100", "医师"))
                subList2.add(UserInfoBean.SubOccupation("100", "法官"))
                subList2.add(UserInfoBean.SubOccupation("100", "律师"))
                subList2.add(UserInfoBean.SubOccupation("100", "嫌疑人"))
                var userInfoBean = UserInfoBean("张三", "18312329009", "北京", "东城区", "朝阳路", "嘻嘻嘻嘻", "手机回收公司", "010-83675492", "北京", "东城区", "哈哈路", "嘻嘻嘻嘻嘻", "909009", "公务员", list)
                list.add(UserInfoBean.OccupationBean("1", "医院", subList1))
                list.add(UserInfoBean.OccupationBean("2", "法院", subList2))
                val myHttpResponse = MyHttpResponse<UserInfoBean>(0, "success", userInfoBean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //保存个人信息
            ApiManager.mHost + ApiSettings.SAVE_USER_INFO -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //获取紧急联系人信息
            ApiManager.mHost + ApiSettings.GET_EXTRO_CONTACTS -> {
                var eme = ArrayList<ExtroContactsBean.EmeContacts>()
                var listtype = ArrayList<ExtroContactsBean.TypeList>()
                eme.add(ExtroContactsBean.EmeContacts("1", "父母", "王二", "13888888888"))
                eme.add(ExtroContactsBean.EmeContacts("2", "配偶", "王三", "12312312321"))
                listtype.add(ExtroContactsBean.TypeList("1", "父母"));
                listtype.add(ExtroContactsBean.TypeList("2", "配偶"));
                listtype.add(ExtroContactsBean.TypeList("3", "直亲"));
                listtype.add(ExtroContactsBean.TypeList("4", "朋友"));
                listtype.add(ExtroContactsBean.TypeList("5", "同事"));

                var extro = ExtroContactsBean(eme, listtype)
                val myHttpResponse = MyHttpResponse<ExtroContactsBean>(0, "success", extro)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //保存紧急联系人信息
            ApiManager.mHost + ApiSettings.SAVE_EXTRO_CONTACTS -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            } //保存紧急联系人信息
            ApiManager.mHost + ApiSettings.SAVE_CONTACTS -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
            ApiManager.mHost + ApiSettings.SAVE_PHONE_INFO -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
            ApiManager.mHost + ApiSettings.GET_LAST_SMS_TIME -> {
                val lastSmsTimeBean = LastSmsTimeBean("", "")
                val myHttpResponse = MyHttpResponse<LastSmsTimeBean>(0, "success", lastSmsTimeBean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
            ApiManager.mHost + ApiSettings.CREDIT_APPLY -> {
                val myHttpResponse = MyHttpResponse<Any>(0, "success", Any())
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //审核通过，评估结果页面
            ApiManager.mHost + ApiSettings.ASSESS_SUCCESS -> {
                val money = "120000"
                val pop_title = "海螺商城<<手机回收合同>>"
                val pop_content = "您在使用多多回收产品请认真阅读<<手机回收合同>>测试文字测试文字测试文字测试文字测试文字测试文字测试文字测试文字测试文字测试文字我阅读并理解<<手机回收合同>>"
                val pop_url = "https://www.baidu.com"
                val list = ArrayList<AssessSuccessBean.RecoveryProcessBean>()
                for (i in 1..4) {
                    val recoveryProcessBean = AssessSuccessBean.RecoveryProcessBean("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "标题$i", "内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容$i")
                    val recoveryProcessBean2 = AssessSuccessBean.RecoveryProcessBean("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "标题$i", "内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容$i")
                    val recoveryProcessBean3 = AssessSuccessBean.RecoveryProcessBean("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "标题$i", "内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容$i")
                    list.add(recoveryProcessBean)
                    list.add(recoveryProcessBean2)
                    list.add(recoveryProcessBean3)
                }
                val assessSuccessBean = AssessSuccessBean("", "", "", "", "", money, pop_title, pop_content, pop_url, "合同", "", "", "", list)
                val myHttpResponse = MyHttpResponse<AssessSuccessBean>(0, "success", assessSuccessBean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //手机评估页面-下拉刷新
            ApiManager.mHost + ApiSettings.DOWN_REFRESH -> {
                val downRefreshBean = DownRefreshBean("", "2", "hahahaha", "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "", "", "", "", "", "")
                val myHttpResponse = MyHttpResponse<DownRefreshBean>(0, "success", downRefreshBean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        //我的银行卡页面
            ApiManager.mHost + ApiSettings.GET_USER_BANK_LIST -> {
                val list = ArrayList<MyBankListBean.BankBean>()
                for (i in 1..4) {
                    val recoveryProcessBean = MyBankListBean.BankBean("$i",
                            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg",
                            "招商银行$i", "6225880159721407", "$i")
                    list.add(recoveryProcessBean)
                }
                val myBankListBean = MyBankListBean(list, "")
                val myHttpResponse = MyHttpResponse<MyBankListBean>(0, "success", myBankListBean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
            ApiManager.mHost + ApiSettings.GEN_ORDER -> {
                var bean = GenOrderBean("1", "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3530122825,3588284255&fm=27&gp=0.jpg", "成功", "成功内容成功内容成功内容成功内容")
                val myHttpResponse = MyHttpResponse<GenOrderBean>(0, "success", bean)
                json = GsonUtil.bean2json(myHttpResponse)
            }
        // 消息中心列表
            ApiManager.mHost + ApiSettings.GET_MESSAGE_CENTER_LIST -> {
                val messageBean1 = MessageCenterBean.MessageBean("2018-04-01 11:12", "1", "点击打开百度", "百度链接地址", "1", "http://www.baidu.com")
                val messageBean2 = MessageCenterBean.MessageBean("2018-03-22 03:31", "2", "这是一条新的消息", "这是一条消息", "2", "")
                val messageBean3 = MessageCenterBean.MessageBean("2018-02-18 06:54", "1", "点击打开新浪", "新浪", "3", "http://www.sina.com")
                val messageBean4 = MessageCenterBean.MessageBean("2018-01-09 18:62", "2", "这也是一条新的消息点击查看详情", "新的消息新消息", "4", "")
                val list = arrayListOf<MessageCenterBean.MessageBean>()
                list.add(messageBean1)
                list.add(messageBean2)
                list.add(messageBean3)
                list.add(messageBean4)

                val callbackParam = MessageCenterBean.CallbackParam("0", "0")
                val messageCenterBean = MessageCenterBean(list, "1", callbackParam, "4")

                json = "{\"code\":0,\"msg\":\"success\",\"data\":${GsonUtil.bean2json(messageCenterBean)}}"
            }
        // 阅读消息
            ApiManager.mHost + ApiSettings.READ_MESSAGE -> {
                json = "{\"code\":0,\"msg\":\"success\",\"data\":{}}"
            }
        }
        return json
    }

}