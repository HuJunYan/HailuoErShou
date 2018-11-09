package com.huaxi.hailuo.ui.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.RefreshCreditStatusEvent
import com.huaxi.hailuo.model.bean.CreditAssessBean
import com.huaxi.hailuo.model.bean.LastSmsTimeBean
import com.huaxi.hailuo.presenter.contract.CreditAssessmentContract
import com.huaxi.hailuo.presenter.impl.CreditAssessmentPresenter
import com.huaxi.hailuo.ui.activity.ServiceOnlineActivity.SERVICE_ONLINE_KEY
import com.huaxi.hailuo.ui.adapter.CreditAdapter
import com.huaxi.hailuo.util.*
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_credit_assessment.*
import kotlinx.android.synthetic.main.dialog_auth_problem.view.*
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CreditAssessmentActivity : BaseActivity<CreditAssessmentContract.View, CreditAssessmentContract.Presenter>(), CreditAssessmentContract.View {

    var mLocationUtil: LocationUtil? = null
    var mData: ArrayList<CreditAssessBean.CreditAssessItemBean>? = null
    var mAdapter: CreditAdapter? = null
    var mGridLayoutManager: GridLayoutManager? = null
    private var userDialog: Dialog? = null
    var choose_mobile = ""
    var choose_memory = ""
    override var mPresenter: CreditAssessmentContract.Presenter = CreditAssessmentPresenter()
    private var isCommit = false//是否使下一个页面变成  提交  而不是 下一步
    private var isAllAuth = false;
    private var isSuccessGetSmsLastTime = false
    private var lastTimeStr: String = ""
    private var status: String = ""
    override fun getLayout(): Int = R.layout.activity_credit_assessment

    var authCount = 0
    var notAuthCount = 0

    override fun showProgress() {}

    override fun hideProgress() {}

    override fun showError() {}

    override fun showErrorMsg(msg: String) {}

    override fun showEmpty() {}

    private lateinit var mPollingUtil: PollingUtil
    private var firstOnResume = false
    private var clickOtherAuth = false

    override fun initView() {
        val extras = intent.extras
        if (extras != null) {
            choose_mobile = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, "android")
            choose_memory = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, "64G")
            status = extras.getString("status")
            SharedPreferencesUtil.getInstance(mActivity).putString("choose_mobile", choose_mobile)
            SharedPreferencesUtil.getInstance(mActivity).putString("choose_memory", choose_memory)
        }
        StatusBarUtil.setPaddingSmart(mActivity, tb_credit_assessment)

        RxView.clicks(tv_credit_assessment_submit).throttleFirst(5, TimeUnit.SECONDS)
                .subscribe {
                    submitResult()
                }
        initRecyclerView()
        initLocation()

        //跳转到在线客服
        tv_on_line_service.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(SERVICE_ONLINE_KEY, SharedPreferencesUtil.getInstance(mActivity).getString(GlobalParams.SERVICE_URL))
            gotoActivity(mActivity, ServiceOnlineActivity::class.java, bundle)
        }
        //跳转到我要吐槽
        tv_speek.setOnClickListener {
            startActivity<UpOpionActivity>()
        }
    }

    private fun initLocation() {

        mLocationUtil = LocationUtil.getInstance()
        mLocationUtil?.startLocation(this)
    }


    private fun initRecyclerView() {
        if (mData == null) {
            mData = ArrayList()
        }
        if (mAdapter == null) {
            mAdapter = CreditAdapter(mActivity, mData)
        }
        if (mGridLayoutManager == null) {
            mGridLayoutManager = GridLayoutManager(mActivity, 1, GridLayoutManager.VERTICAL, false)
//            mGridLayoutManager?.spanSizeLookup = SpanSize()
        }

        rv_credit.layoutManager = mGridLayoutManager
        rv_credit.adapter = mAdapter
        mAdapter?.setStartNewActivityListener {
            mPollingUtil.stop()
            clickOtherAuth = true
        }
    }

    //提交认证
    private fun submitResult() {
        if (!isAllAuth) {
            ToastUtil.showToast(mActivity, "请先完成认证", ToastUtil.SHOW)
            return
        }
        if (isSuccessGetSmsLastTime) {
            getUserInfo()
        } else {
            mPresenter.getLastSmsTime(false)
        }
    }

    override fun initData() {
        mPresenter.getCreditAssessData(false)
        mPresenter.getLastSmsTime(true)

        mPollingUtil = PollingUtil()
                .timeUnit(TimeUnit.SECONDS)
                .timeInterval(1)
                .take(15)
                .skip(4)
                .setTask(Runnable {
                    mPresenter.getCreditAssessData(false, false)
                })

        //判断是否全部认证完，没有全部认证完点击返回按钮跳转到我要吐槽页面
        tv_credit_assessment_back.setOnClickListener({
            if (!isAllAuth) {
                showAuthProblemDialog()
            } else {
                backActivity()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (firstOnResume) {
            mPresenter.getCreditAssessData(false)
            if (clickOtherAuth) {
                mPollingUtil.start()
                clickOtherAuth = false
            }
        } else {
            firstOnResume = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPollingUtil.stop()
    }

    override fun getCreditAssessResult(data: CreditAssessBean?, isNeedJump: Boolean) {
        tv_credit_assessment_submit.isEnabled = true
        if (data == null || data.required_list == null) {
            ToastUtil.showToast(mActivity, "数据错误")
            return
        }
        var isAuthIdentity = false
        var isNeedJumpLocal = isNeedJump
        mData?.clear()
        if (data.required_list.size > 0) {
            val required_list = data.required_list
            authCount = 0
            notAuthCount = 0
            for (index in required_list.indices) {
                val creditAssessItemBean = required_list[index]
                creditAssessItemBean.local_item_type = CreditAdapter.ItemType.TYPE_REQUIRED
                val item_status = creditAssessItemBean.item_status;
                if ("1" == creditAssessItemBean.item_num && "1".equals(item_status)) {
                    //身份认证通过
                    isAuthIdentity = true
                }
                if ("2" == item_status) {
                    notAuthCount++
                    if (isNeedJumpLocal) {
                        //跳转到指定的认证项 并只有一个跳转
                        mAdapter?.checkToJump(creditAssessItemBean)
                        isNeedJumpLocal = false
                    }
                }
                if ("1".equals(item_status)) {
                    authCount++
                }

                /* if (mWithdrawMoney != "" && isAuthIdentity) {
                     var intent = Intent(mActivity, BindBankCardActivity.javaClass)
 //                    intent.putExtra("bind_bank_type", "red_packet_bind_bank")
                     intent.putExtra("title", "绑定银行卡")
                     intent.putExtra("withdraw_money", mWithdrawMoney)
 //                    gotoActivity(mActivity, BindBankCardActivity::class.java, bundle)
                     startActivity(intent)
                 }*/
            }
            val size = required_list.size
            isAllAuth = authCount == size
            //是否使下一个页面变成  提交  而不是 下一步
            isCommit = notAuthCount == 0 || notAuthCount == 1

            mData?.addAll(required_list)
//            //添加空白item
//            val i = 4 - required_list.size % 4
//            if (i != 0) {
//                for (index in 1..i) {
//                    mData?.add(CreditAssessBean.CreditAssessItemBean(CreditAdapter.ItemType.TYPE_REQUIRED, "", "", true))
//                }
//            }
        }

        var hideData = ArrayList<CreditAssessBean.CreditAssessItemBean>()
        if (data.not_required_list != null && data.not_required_list.size > 0) {

            val not_required_list = data.not_required_list
            for (index in not_required_list.indices) {
                val creditAssessItemBean = not_required_list[index]
                creditAssessItemBean.local_item_type = CreditAdapter.ItemType.TYPE_NOT_REQUIRED
//                if (index >= 2) {
//                    hideData.add(not_required_list[index])
//                } else {
                mData?.add(not_required_list[index])
//                }
            }
        }
        mAdapter?.refreshData(isCommit, hideData, isAuthIdentity)
        //用户没有过风控并且没有全部认证完
        if (status != "1") {
            //判断是否全部认证完，认证完之后弹出下面的提示
            if (isAllAuth) {
                ToastUtil.showToast(mActivity, "68元红包已到账，点击提交按钮获取额度后可使用！")
            }
        }
    }

    //提交认证完成后的跳转逻辑
    override fun processSubmitCreditResult() {
        var bundle = Bundle()
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, choose_mobile)
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, choose_memory)
        gotoActivity(mActivity, PhoneAssessActivity::class.java, bundle)
        //关闭本页面
        finish()
    }

    inner class SpanSize : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            if (mData == null) {
                return 1
            } else {
                if (mData!![position].local_item_type == CreditAdapter.ItemType.TYPE_REQUIRED) {
                    return 1
                } else {
                    return 4
                }
            }
        }
    }

    //刷新认证项   为true时在成功后跳转下一个未认证项
    @Subscribe
    fun onRefreshCreditStatusEvent(event: RefreshCreditStatusEvent) {
//        mPresenter.getCreditAssessData(false)
    }

    override fun processLastSmsTime(t: LastSmsTimeBean?, isFirst: Boolean) {
        if (t == null) {
            ToastUtil.showToast(mActivity, "数据错误")
            return
        }
        isSuccessGetSmsLastTime = true
        lastTimeStr = t.message_form_time
        if (!isFirst) {
            getUserInfo()
        }
    }

    private var mJSONObject: JSONObject? = null  // 上传用户信息的json
    private var step = 0   // 获取信息的步数  分别为 app列表 短信
    // 获取用户信息并上传
    fun getUserInfo() {
        //获取用户信息 成功后 提交认证\
        DialogUtil.showLoadingDialog()
        mJSONObject = JSONObject()
        PhoneInfoUtil.getApp_list(mActivity, myCallBack)
    }

    private val myCallBack = object : PhoneInfoUtil.PhoneInfoCallback {
        override fun sendMessageToRegister(jsonArray: JSONArray, jsonArrayName: String) {
            try {
                step++
                //获取app列表完毕后 去获取短信
                if (GlobalParams.USER_INFO_APP_LIST.equals(jsonArrayName)) {

                    if (!isFinishing) {
                        PhoneInfoUtil.getMessage_list(mActivity, this, lastTimeStr)
                    }
                }
                mJSONObject?.put(jsonArrayName, jsonArray)
                if (step == 2) {
                    step = 0
                    getUserOtherInfo()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                step = 0
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mLocationUtil?.stopLocation()
    }

    private fun getUserOtherInfo() {
        val deviceId = UserUtil.getDeviceId()
        DialogUtil.hideLoadingDialog()
        //开始上传信息
        mPresenter.submitPhoneInfo(mJSONObject!!, deviceId)
    }

    //上传用户信息成功了  去提交认证
    override fun processSubmitPhoneInfoResult() {
        mPresenter.submitCredit(choose_mobile, choose_memory)
    }

    private fun showAuthProblemDialog() {
        if (userDialog == null) {
            userDialog = Dialog(mActivity, R.style.MyDialog)
        }
        if (userDialog!!.isShowing) {
            return
        }

        val mLayoutInflater = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView = mLayoutInflater.inflate(R.layout.dialog_auth_problem, null, false)
        //设置布局
        userDialog?.setContentView(mView)
        userDialog?.show()
        userDialog?.setCanceledOnTouchOutside(true)
        mView.tv_dialog_back_to_homepage.setOnClickListener {
            userDialog?.dismiss()
            backActivity()
        }
        mView.tv_dialog_yes_speek.setOnClickListener {
            userDialog?.dismiss()
            startActivity<UpOpionActivity>()
        }
        mView.iv_auth_cancle.setOnClickListener {
            userDialog?.dismiss()
        }
    }

    override fun onBackPressedSupport() {
        if (!isAllAuth) {
            showAuthProblemDialog()
        } else {
            super.onBackPressedSupport()
        }
    }
}
