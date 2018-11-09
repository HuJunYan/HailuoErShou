package com.huaxi.hailuo.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import com.huaxi.hailuo.R
import com.huaxi.hailuo.R.id.*
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseFragment
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.presenter.contract.HomeContract
import com.huaxi.hailuo.presenter.impl.HomePresenter
import com.huaxi.hailuo.ui.activity.*
import com.huaxi.hailuo.ui.adapter.VerifyMainProgressAdapter
import com.huaxi.hailuo.util.*
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_verify_home.*
import org.jetbrains.anko.support.v4.startActivity

class VerifyHomeFragment : BaseFragment<HomeContract.View, HomePresenter>(), HomeContract.View {


    var mAdapter: VerifyMainProgressAdapter? = null
    private var mFlowListBean: ArrayList<FlowBean> = ArrayList<FlowBean>()
    var mAssess: AssessBean? = null
    var mHomeBean: HomePageBean? = null
    private var mCurrentIndex: Int = 0//当前滚动条的位置
    private var mStatisticsRollDatas: MutableList<ContentBean>? = null
    private var url: String = ""
    private var mIsShowDefaultMobile = true

    companion object {
        private val MSG_SHOW_TEXT = 1
        private val SHOW_TEXT_TIME = 5 * 1000
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(message: Message) {
//            refreshStaticsRollUI()
        }
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacksAndMessages(null)
    }

    override var mPresenter: HomePresenter = HomePresenter()


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

    override fun processHomeData(data: HomePageBean, isNeedShowDialog: Boolean) {
        mHomeBean = data
        showView()
        if ("1" == mHomeBean?.status) {
            mPresenter.getMobileList()
        }
    }

    override fun homeCouponDialogResult(data: HomeCouponBean?) {

    }

    override fun assessComplete(data: AssessBean?) {
        mAssess = data
        val target = mAssess?.target_page
        var bundle = Bundle()
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, verify_tv_home_phone_os.text.toString().trim())
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, verify_tv_home_phone_memory_value.text.toString().trim())
        bundle.putString("status", mHomeBean?.check_user_risk)

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
//            showHomeDialog()
            gotoActivity(activity!!, AssessFailureActivity::class.java, bundle)
        }
    }

    override fun processMobileList(data: MobileListBean?) {

    }

    override fun getLayout(): Int = R.layout.fragment_verify_home

    override fun initView() {
        verify_tv_home_phone_memory_value.text = Tools.getStringToatalSpace(mContext)
        url = "\"http:\\/\\/dev.recycle.tianshenjr.com\\/zf_h5\\/loading\\/index.html"
        showView()

        verify_home_banner.setOnClickListener {
            startActivity<WebActivity>(WebActivity.WEB_URL_KEY to url, WebActivity.WEB_URL_TITLE to "详情")
        }
        mStatisticsRollDatas?.add(ContentBean("用户尾号0205,刚刚卖出一部iphone7 plus获得1000元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号8105,刚刚卖出一部iphone7 plus获得1000元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号7546,刚刚卖出一部iphone7 plus获得1700元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号5647,刚刚卖出一部iphone7 plus获得1800元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号0687,刚刚卖出一部iphone7 plus获得1000元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号9908,刚刚卖出一部iphone7 plus获得1500元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号1287,刚刚卖出一部iphone7 plus获得1100元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号2138,刚刚卖出一部iphone7 plus获得1900元"))
        mStatisticsRollDatas?.add(ContentBean("用户尾号6758,刚刚卖出一部iphone7 plus获得1700元"))

    }

    override fun initData() {
        mPresenter.getHomeData(true)
        SharedPreferencesUtil.getInstance(activity).putString("choose_mobile", verify_tv_home_phone_os.text.toString())
        SharedPreferencesUtil.getInstance(activity).putString("choose_memory", verify_tv_home_phone_memory_value.text.toString())
        verify_bt_home_identity.setOnClickListener {
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            if ("1" == mHomeBean?.status) {
                mPresenter.assess(verify_tv_home_phone_os.text.toString().trim(), verify_tv_home_phone_memory_value.text.toString().trim())

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
    }

    /**
     * 初始化TextSwitcher
     */
    private fun initTextSwitcher() {

        main_top_txt.setFactory(ViewSwitcher.ViewFactory {
            val tv = TextView(mContext)
            tv.textSize = 11f
            tv.setTextColor(resources.getColor(R.color.white))
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


    private fun showView() {
//        mStatisticsRollDatas = mHomeBean!!.scroll_bar.scroll_bar_list
//        refreshStaticsRollUI()


        mFlowListBean.clear()
        mFlowListBean.add(FlowBean("http:\\/\\/recycle.tianshenjr.com\\/static\\/tiantian\\/flow_icon\\/oldmobile.png", "旧机估价"))
        mFlowListBean.add(FlowBean("http:\\/\\/recycle.tianshenjr.com\\/static\\/tiantian\\/flow_icon\\/online.png", "在线下单"))
        mFlowListBean.add(FlowBean("http:\\/\\/recycle.tianshenjr.com\\/static\\/tiantian\\/flow_icon\\/money.png", "急速结款"))
        mFlowListBean.add(FlowBean("http:\\/\\/recycle.tianshenjr.com\\/static\\/tiantian\\/flow_icon\\/safe.png", "质检入库"))

        if (mAdapter == null) {
            mAdapter = VerifyMainProgressAdapter(mContext, mFlowListBean)
        }
        verify_home_recycle.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)
        verify_home_recycle.adapter = mAdapter

    }

    /**
     * 刷新滚动条
     */
    private fun refreshStaticsRollUI() {
        if (mCurrentIndex == 0) {
            //第一次不执行动画，立刻显示
            main_top_txt.setCurrentText(mStatisticsRollDatas!!.get(mCurrentIndex).content)
        } else if (mCurrentIndex == mStatisticsRollDatas!!.size) {
            //跳回第一次
            mCurrentIndex = 0
            main_top_txt.setText(mStatisticsRollDatas!!.get(mCurrentIndex).content)
        } else {
            //执行动画
            main_top_txt.setText(mStatisticsRollDatas!!.get(mCurrentIndex).content)
        }
        mCurrentIndex++
        mHandler.removeMessages(VerifyHomeFragment.MSG_SHOW_TEXT)
        mHandler.sendEmptyMessageDelayed(VerifyHomeFragment.MSG_SHOW_TEXT, VerifyHomeFragment.SHOW_TEXT_TIME.toLong())

    }
}