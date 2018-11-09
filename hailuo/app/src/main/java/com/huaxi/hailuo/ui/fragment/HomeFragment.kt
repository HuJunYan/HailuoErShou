package com.huaxi.hailuo.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseFragment
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.HomeDiversionEvent
import com.huaxi.hailuo.event.LocationEvent
import com.huaxi.hailuo.event.LogOutEvent
import com.huaxi.hailuo.event.LoginEvent
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.presenter.contract.HomeContract
import com.huaxi.hailuo.presenter.impl.HomePresenter
import com.huaxi.hailuo.ui.activity.*
import com.huaxi.hailuo.ui.adapter.AuthGiveCouponAdapter
import com.huaxi.hailuo.util.*
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.dialog_auth_coupon.view.*
import kotlinx.android.synthetic.main.dialog_home_assess_fail.view.*
import kotlinx.android.synthetic.main.dialog_user_not_log_in.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import me.yokeyword.fragmentation.ISupportFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.support.v4.startActivity

/**
 * 主页面
 */
@SuppressLint("ValidFragment")
class HomeFragment : BaseFragment<HomeContract.View, HomePresenter>(), HomeContract.View {

    private var mMobileName: String? = ""
    private var mMobileMemory: String? = ""
    private var mMobilePrice = "0.00"
    private var mMobileIconUrl = ""
    private var mDialog: Dialog? = null
    private var mHomeCouponBean: HomeCouponBean? = null
    private var mHomeCouponList: ArrayList<HomeCouponBean.AuthListBean>? = null

    private var mHomeBean: HomePageBean? = null
    private var mMobileListBean: MobileListBean? = null
    private var mAssess: AssessBean? = null
    private var mMobileListItemSubBeanList: ArrayList<MobileListBean.MobileListItemSubBean>? = null
    override var mPresenter: HomePresenter = HomePresenter()
    override fun getLayout(): Int = R.layout.fragment_home
    private var mCurrentIndex: Int = 0//当前滚动条的位置
    private var mStatisticsRollDatas: MutableList<ContentBean>? = null
    private var mIsAutoJump: Boolean? = false
    private val REQUEST_LOGIN = 10001
    private var dialog: Dialog? = null
    private var userDialog: Dialog? = null
    private var mIsShowDefaultMobile = true

    companion object {
        const val HOME_AUTO_JUMP = "isAutoJump"
        private val MSG_SHOW_TEXT = 1
        private val SHOW_TEXT_TIME = 5 * 1000
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(message: Message) {
            refreshStaticsRollUI()
        }
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacksAndMessages(null)
    }


    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {


    }

    override fun showEmpty() {
    }

    override fun initView() {
//        phone_type.paint.isFakeBoldText = true
        initTextSwitcher()
        bt_home_identity.setOnClickListener {
            if (!UserUtil.isLogin(App.instance)) {
                val intent = Intent(_mActivity, LoginActivity::class.java)
                intent.putExtra(HomeFragment.HOME_AUTO_JUMP, true)
                startActivityForResult(intent, REQUEST_LOGIN)
                return@setOnClickListener
            }
            if ("1" == mHomeBean?.status) {
                if (TextUtils.isEmpty(tv_home_phone_name.text.toString().trim())) {
                    ToastUtil.showToast(activity, "请选择型号")
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(tv_home_phone_memory.text.toString().trim())) {
                    ToastUtil.showToast(activity, "请选择内存")
                    return@setOnClickListener
                }
                mPresenter.assess(tv_home_phone_os.text.toString().trim(), tv_home_phone_memory_value.text.toString().trim())
            } else if ("2" == mHomeBean?.status) {
                //订单详情界面
                if (TextUtils.isEmpty(mHomeBean?.order_id)) {
                    ToastUtil.showToast(_mActivity, "订单信息异常")
                    return@setOnClickListener
                }
                startActivity<OrderDetailActivity>(OrderDetailActivity.ORDERI_ID to mHomeBean!!.order_id)
            } else {
                ToastUtil.showToast(_mActivity, "数据错误")
                return@setOnClickListener
            }
        }
        /*rl_phone_name.setOnClickListener {
            if ("1" == mHomeBean?.status) {
                mIsShowDefaultMobile = false
                mPresenter.getMobileList()
            }
        }*/
        rl_phone_memory.setOnClickListener { showMemoryDialog() }

        try {
            val metric = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metric)
            val height = metric.heightPixels   // 屏幕高度（像素）

            val params = rl_phone_info_layout.layoutParams
            params.height = (height * 0.4).toInt()
            rl_phone_info_layout.layoutParams = params
        } catch (e: Exception) {
        }
    }

    private var loadFirstNetWork = false
    override fun initData() {
        dialog?.dismiss()
        mIsAutoJump = arguments?.getBoolean(HomeFragment.HOME_AUTO_JUMP)

        mStatisticsRollDatas = ArrayList()
        mPresenter.getHomeData(true)
        loadFirstNetWork = true
        SharedPreferencesUtil.getInstance(activity).putString("choose_mobile", tv_home_phone_name.text.toString())
        SharedPreferencesUtil.getInstance(activity).putString("choose_memory", tv_home_phone_memory.text.toString())

    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        if (loadFirstNetWork) {
            mPresenter.getHomeData(true)
        }
    }

    //首页认证优惠券弹框的结果
    override fun homeCouponDialogResult(data: HomeCouponBean?) {
        dialog?.dismiss()
        userDialog?.dismiss()
        if (data == null) {
            return
        }
        mHomeCouponBean = data
        if (mHomeCouponList == null) {
            mHomeCouponList = ArrayList()
        }
        mHomeCouponList?.clear()
        if (data.auth_list != null) {
            mHomeCouponList?.addAll(data.auth_list)
        }

        //认证送优惠券
        //已过风控，不需要弹框
        if (mHomeBean?.check_user_risk == "1") {
//            当前用户未过风控，需要弹出认证送优惠券的弹框
        } else if (mHomeBean?.check_user_risk == "2") {


            showAuthCouponDialog()
            //用户未登录状态
        } else if (mHomeBean?.check_user_risk == "") {
            //展示未登录状态的弹框
            UserNotLoggedInDialog()
        } else {
            ToastUtil.showToast(_mActivity, "数据错误")
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ISupportFragment.RESULT_OK && requestCode == REQUEST_LOGIN) {
            mIsAutoJump = true
        }
    }

    override fun processHomeData(data: HomePageBean, isNeedShowDialog: Boolean) {
        try {
            if ("1" == data.is_show_bubble) {
                EventBus.getDefault().post(HomeDiversionEvent(data.daoliu_title, data.bubble_url, data.daoliu_image))
            }
        } catch (e: Exception) {
        }

        mHomeBean = data
        if (mIsAutoJump!!) {
            mIsAutoJump = false
            bt_home_identity.performClick()
        }
        if ("1" == mHomeBean?.status) {
            mIsShowDefaultMobile = true
            mPresenter.getMobileList()
        }
        if (!TextUtils.isEmpty(data.mobile_info.mobile_status)) {

            phone_re_status.text = data.mobile_info?.mobile_status
        } else {
            phone_re_status.text = "待申请"
        }
        addToCalendar(data)
        //首页认证弹框
        mPresenter.getHomeCouponDialog()
        showView()
    }

    private fun addToCalendar(data: HomePageBean?) {
        //1代表需要放款成功需要添加到系统日历里面,非1的时候APP不需要显示
        if (data == null || data.is_show_calendar_dialog != "1" || data.notice == null) {
            return
        }

        // 如果为true 代表不在提示当前订单添加到日历
//        val show = SharedPreferencesUtil.getInstance(_mActivity).getBoolean(CalendarUtil.CALENDER_NOTICE + data.order_id)

//        if (!show) {
        CalendarUtil.showCalendarDialog(activity!!, data.order_id, data.notice)
//        }
    }

    override fun assessComplete(data: AssessBean?) {
        mAssess = data
        val target = mAssess?.target_page
        if (TextUtils.isEmpty(tv_home_phone_name.text.toString().trim())) {
            ToastUtil.showToast(activity, "请选择型号")
            return
        }
        if (TextUtils.isEmpty(tv_home_phone_memory.text.toString().trim())) {
            ToastUtil.showToast(activity, "请选择内存")
            return
        }

        var bundle = Bundle()
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, tv_home_phone_os.text.toString().trim())
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, tv_home_phone_memory_value.text.toString().trim())
        bundle.putString("status", mHomeBean?.check_user_risk)
        bundle.putString("withdraw_money", "")
        if (data!!.assess_status == "1") {
            //评估通过
            if ("1" == target) {
                gotoActivity(activity!!, CreditAssessmentActivity::class.java, bundle)
            } else if ("2" == target) {
                //评测失败页面
                gotoActivity(activity!!, AssessFailureActivity::class.java, bundle)
            } else if ("3" == target) {
                //评测成功页面

                gotoActivity(activity!!, AssessSuccessActivity::class.java, bundle)
            } else if ("4" == target) {
                //评测中
                gotoActivity(activity!!, PhoneAssessActivity::class.java, bundle)
            }
        } else {
            //不通过
            gotoActivity(activity!!, AssessFailureActivity::class.java, bundle)
        }
    }

    private fun showView() {
        if (mHomeBean == null) {
            return
        }
//        phone_type.text = mHomeBean!!.mobile_info.mobile_title
        mStatisticsRollDatas = mHomeBean?.scroll_bar?.scroll_bar_list
        refreshStaticsRollUI()

        home_banner.setImages(mHomeBean?.banner_list).setImageLoader(HeaderGlideImageLoader())
                .setDelayTime(3000)
                .setIndicatorGravity(BannerConfig.CENTER)
                .setOnBannerListener { position ->
                    var url = mHomeBean!!.banner_list[position].jump_url
                    if (!TextUtils.isEmpty(url)) {
                        startActivity<WebActivity>(WebActivity.WEB_URL_KEY to url, WebActivity.WEB_URL_TITLE to "详情")
                    }
                }
                .start()
//        if (!TextUtils.isEmpty(mHomeBean?.mobile_info?.mobile_icon)) {
//            ImageUtil.load(activity!!, mHomeBean?.mobile_info?.mobile_icon!!, phone_img)
//        }

        if (!TextUtils.isEmpty(mHomeBean!!.mobile_info.mobile_model)) {
            //如果有值就设置上 ，没有就不管 默认Android
            tv_home_phone_os.text = mHomeBean?.mobile_info?.mobile_model
        }

        if (!TextUtils.isEmpty(mHomeBean!!.mobile_info.mobile_memory)) {
            tv_home_phone_memory_value.text = mHomeBean!!.mobile_info.mobile_memory
        } else {
            tv_home_phone_memory_value.text = Tools.getStringToatalSpace(mContext)
        }

        if (!TextUtils.isEmpty(mHomeBean?.mobile_info?.mobile_price_str)) {
            phone_max_price.text = "最高额度：" + "￥" + MoneyUtils.changeF2Y(mHomeBean?.mobile_info?.mobile_price_str, 2)
        } else {
            phone_max_price.text = "最高额度(元)"
        }
        bt_home_identity.text = mHomeBean!!.status_title
        bt_home_identity.setBackgroundResource(R.drawable.global_btn_selector)

    }

    /**
     * 初始化TextSwitcher
     */
    private fun initTextSwitcher() {

        main_top_txt.setFactory(ViewSwitcher.ViewFactory {
            val tv = TextView(mContext)
            tv.textSize = 11f
            tv.setTextColor(resources.getColor(R.color.global_txt_black5))
            tv.setSingleLine()
            tv.ellipsize = TextUtils.TruncateAt.END
            val drawable = resources.getDrawable(R.drawable.news)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tv.setCompoundDrawables(drawable, null, null, null)//设置TextView的drawableleft
            tv.compoundDrawablePadding = 10//设置图片和text之间的间距
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER
            tv.layoutParams = lp
            tv
        })
    }

    /**
     * 刷新滚动条
     */
    private fun refreshStaticsRollUI() {
        if (mStatisticsRollDatas != null) {
            if (mCurrentIndex == 0) {
                //第一次不执行动画，立刻显示
                main_top_txt.setCurrentText(mStatisticsRollDatas?.get(mCurrentIndex)?.content)
            } else if (mCurrentIndex == mStatisticsRollDatas?.size) {
                //跳回第一次
                mCurrentIndex = 0
                main_top_txt.setText(mStatisticsRollDatas?.get(mCurrentIndex)?.content)
            } else {
                //执行动画
                main_top_txt.setText(mStatisticsRollDatas?.get(mCurrentIndex)?.content)
            }
            mCurrentIndex++
            if (mHandler != null) {
                mHandler.removeMessages(MSG_SHOW_TEXT)
                mHandler.sendEmptyMessageDelayed(MSG_SHOW_TEXT, SHOW_TEXT_TIME.toLong())
            }
        }
    }

    /**
     * 评估不沟通弹窗
     */
    private fun showHomeDialog() {

        if (mAssess == null) {
            return
        }
        mDialog = Dialog(activity, R.style.MyDialog)
        val mLayoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.dialog_home_assess_fail, null, false)
        view.dialog_home_title.text = mAssess!!.assess_title
        view.dialog_home_content.text = mAssess!!.assess_message
        mDialog?.setContentView(view)
        mDialog?.setCancelable(false)
        view.i_know.setOnClickListener {
            mDialog?.dismiss()
        }

        mDialog?.show()
    }

    @Subscribe
    fun onLocationEvent(event: LocationEvent) {
        mPresenter.upLoadLocation()
    }

    override fun processMobileList(data: MobileListBean?) {
        if (data == null) {
            ToastUtil.showToast(activity, "数据错误")
            return
        }
        mMobileListBean = data
        if (mIsShowDefaultMobile) {
            if (mMobileListBean?.mobile_list?.size!! > 0) {
                mMobileListItemSubBeanList = mMobileListBean?.mobile_list!![0].item_sub
            }
            showView()
        } else {
            showMobileDialog()
        }

    }

    private fun showMobileDialog() {
        if ("1" == mHomeBean?.status) {

            if (mMobileListBean!!.mobile_list == null || mMobileListBean!!.mobile_list!!.size == 0) {
                return
            }
            val dialogView = View.inflate(activity, R.layout.dialog_listview, null)
            val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1)
            if (mMobileListBean?.mobile_list!!.size > 0) {
                for (i in mMobileListBean?.mobile_list!!.indices) {
                    arrayAdapter.add(mMobileListBean!!.mobile_list[i].item_name)
                }
            }
            val mListView = dialogView.findViewById(R.id.listview) as ListView
            mListView.setDividerHeight(0)
            mListView.setAdapter(arrayAdapter)


            val builder = AlertDialog.Builder(activity!!)
                    .setView(dialogView)
                    .setCancelable(true)

            val alertDialog = builder.create()

            alertDialog.setTitle("请选择型号")
            alertDialog.setView(dialogView)
            alertDialog.setCancelable(true)

            mListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                tv_home_phone_name.text = mMobileListBean?.mobile_list!![i].item_name
                tv_home_phone_memory.text = ""
                phone_max_price.text = "最高额度：" + "￥" + MoneyUtils.changeF2Y("0", 2)
                mMobileIconUrl = mMobileListBean?.mobile_list!![i].item_img_url
                mMobileName = mMobileListBean?.mobile_list!![i].item_name
                mMobileMemory = ""
//                ImageUtil.load(activity!!, mMobileListBean?.mobile_list!![i].item_img_url, phone_img)
                mMobileListItemSubBeanList = mMobileListBean?.mobile_list!![i].item_sub
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

    }


    private fun showMemoryDialog() {
        if ("1" == mHomeBean?.status) {

            if (TextUtils.isEmpty(tv_home_phone_name.text.toString().trim()) || mMobileListItemSubBeanList == null) {
                ToastUtil.showToast(activity, "请先选择型号")
                return
            }

            val dialogView = View.inflate(activity, R.layout.dialog_listview, null)
            val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1)
            if (mMobileListItemSubBeanList!!.size > 0) {
                for (i in mMobileListItemSubBeanList!!.indices) {
                    arrayAdapter.add(mMobileListItemSubBeanList!![i].item_mem)
                }
            }
            val mListView = dialogView.findViewById(R.id.listview) as ListView
            mListView.setDividerHeight(0)
            mListView.setAdapter(arrayAdapter)


            val builder = AlertDialog.Builder(activity!!)
                    .setView(dialogView)
                    .setCancelable(true)

            val alertDialog = builder.create()

            alertDialog.setTitle("请选择内存")
            alertDialog.setView(dialogView)
            alertDialog.setCancelable(true)

            mListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                tv_home_phone_memory.text = mMobileListItemSubBeanList!![i].item_mem
                mMobileMemory = mMobileListItemSubBeanList!![i].item_mem
                mMobilePrice = MoneyUtils.changeF2Y(mMobileListItemSubBeanList!![i].item_price, 2)
                phone_max_price.text = "最高额度：" + "￥" + MoneyUtils.changeF2Y(mMobileListItemSubBeanList!![i].item_price, 2)
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

    }


    @Subscribe
    fun onLogoutEvent(event: LogOutEvent) {
        mMobileName = ""
        mMobileMemory = ""
        mMobilePrice = "0.00"
        mPresenter.getHomeData(true)
    }

    @Subscribe
    fun onLoginEvent(event: LoginEvent) {
        mPresenter.getHomeData(true)
    }

    //是否展示认证送优惠券弹框
    private fun showAuthCouponDialog() {

        if (dialog == null) {
            dialog = Dialog(activity, R.style.MyDialog)
        }
        if (dialog!!.isShowing) {
            return
        }
        val mLayoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view_coupon = mLayoutInflater.inflate(R.layout.dialog_auth_coupon, null, false)
        dialog?.setCanceledOnTouchOutside(false)
        //设置布局
        dialog?.setContentView(view_coupon)
        view_coupon.tv_left_days.text = Html.fromHtml(mHomeCouponBean?.login_dialog_content)
        view_coupon.lv_home_red_packet.adapter = AuthGiveCouponAdapter(mContext, mHomeCouponList)
        //放弃红包
        view_coupon.tv_dialog_give_up_red_packet.setOnClickListener {
            dialog?.dismiss()
        }
        //领取红包
        view_coupon.tv_dialog_yes_get_red_packet.setOnClickListener {
            var bundle = Bundle()
            bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, tv_home_phone_os.text.toString().trim())
            bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, tv_home_phone_memory_value.text.toString().trim())
            bundle.putString("status", mHomeBean?.check_user_risk)
            //跳转到认证评估页面
            gotoActivity(activity!!, CreditAssessmentActivity::class.java, bundle)
            dialog?.dismiss()
        }
        dialog?.show()
    }

    //认证送优惠券  用户未登录状态
    private fun UserNotLoggedInDialog() {
        if (userDialog == null) {
            userDialog = Dialog(mContext, R.style.MyDialog)
        }
        if (userDialog!!.isShowing) {
            return
        }
        val mLayoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view_dialog_not_login = mLayoutInflater.inflate(R.layout.dialog_user_not_log_in, null, false)
        //设置布局
        userDialog?.setContentView(view_dialog_not_login)

        userDialog?.show()

        view_dialog_not_login.iv_new_user.setOnClickListener {
            userDialog?.dismiss()
            startActivity<LoginActivity>()
        }
        view_dialog_not_login.iv_new_user_cancle.setOnClickListener {
            userDialog?.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
//        userDialog = null
        userDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
//        userDialog = null
        userDialog?.dismiss()
//        CalendarUtil.cancel()

    }
}