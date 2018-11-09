package com.huaxi.hailuo.ui.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.BackHomeEvent
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.presenter.contract.AssessSuccessContract
import com.huaxi.hailuo.presenter.impl.AssessSuccessPresenter
import com.huaxi.hailuo.ui.adapter.AssessSuccessAdapter
import com.huaxi.hailuo.util.*
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_assess_success.*
import kotlinx.android.synthetic.main.dialog_assess_success.view.*
import kotlinx.android.synthetic.main.dialog_sms_verify.view.*
import kotlinx.android.synthetic.main.layout_assess_success_header.view.*
import me.drakeet.materialdialog.MaterialDialog
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

/**
 * 评测成功页面
 */
class AssessSuccessActivity : BaseActivity<AssessSuccessContract.View, AssessSuccessContract.Presenter>(), AssessSuccessContract.View {


    var mAdapter: AssessSuccessAdapter? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var mList: ArrayList<AssessSuccessBean.RecoveryProcessBean>? = null
    var mData: AssessSuccessBean? = null
    var mHeader: View? = null
    private var mView: View? = null
    private var isSmsCodeNull: String? = ""
    private var bank_mobile = ""
    //优惠券id
    private var mCouponId: String? = ""
    var isHasDialogPhone: Boolean = false
    var isHasDialogCode: Boolean = false
    private var mTimeCount: TimeCount? = null
    private var smsBean: SmsBean? = null
    private var mMaterialDialog: MaterialDialog? = null
    override fun initData() {
        val extras = intent.extras
        if (extras != null) {
            var mobile_model = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, "")
            var mobile_memory = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, "")
            mPresenter.getAssessSuccessData(mobile_model, mobile_memory)

        } else {
            ToastUtil.showToast(mActivity, "数据错误")
        }
    }


    override var mPresenter: AssessSuccessContract.Presenter = AssessSuccessPresenter()

    override fun getLayout(): Int = R.layout.activity_assess_success

    override fun showProgress() {}

    override fun hideProgress() {
    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_assess_success)
        tv_assess_success_back.setOnClickListener {
            EventBus.getDefault().post(BackHomeEvent())
            backActivity()
        }
        RxView.clicks(tv_get_money)
                .throttleFirst(5, TimeUnit.SECONDS)
                .subscribe {
                    showAgreementDialog()
                }
        initRV()
    }

    private fun showAgreementDialog() {
        if (mData == null) {
            return
        }

        var mDialog = Dialog(this, R.style.MyDialog)

        val mLayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.dialog_assess_success, null, false)
        mDialog.setContentView(view)
        val layoutParams = view.layoutParams
        layoutParams.width = (300 * resources.displayMetrics.density).toInt()
        view.layoutParams = layoutParams
        mDialog.setCancelable(true)
        view.tv_dialog_assess_success_title.text = mData?.pop_title
        view.tv_dialog_assess_success_content.text = mData?.pop_content
        view.tv_dialog_assess_success_agreement.text = mData?.pop_agreement_title

        //阅读并理解手机合同跳转逻辑
        view.tv_dialog_assess_success_agreement.setOnClickListener {
            if (mData != null && mData!!.pop_url != null) {
                startActivity<WebActivity>(WebActivity.WEB_URL_KEY to mData!!.pop_url, WebActivity.WEB_URL_TITLE to "手机回收合同")
            }
        }
        view.tv_dialog_assess_success_not_agree.setOnClickListener { mDialog.dismiss() }
        RxView.clicks(view.tv_dialog_assess_success_agree)
                .throttleFirst(5, TimeUnit.SECONDS)
                .subscribe({
                    if (mData == null) {
                        return@subscribe
                    }
                    //未绑定银行卡 跳转到绑定银行卡的界面
                    if (mData!!.is_bind_bank == "2") {
                        var bundle = Bundle()
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, mData!!.mobile_model)
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, mData!!.mobile_memory)
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHANNEL_TYPE, mData!!.channel_type)
                        gotoActivity(mActivity, BindBankCardActivity::class.java, bundle)
                        //绑定了银行卡
                    } else if (mData!!.is_bind_bank == "1") {
                        //全部绑定过还款渠道 然后去下单
                        if (mData!!.is_all_bind == "1") {
                            mPresenter.genOrder(mData!!.mobile_model, mData!!.mobile_memory, mData!!.channel_type, mData!!.bank_mobile)
                            //没有全部绑定还款渠道  进行支付渠道绑定
                        } else if (mData!!.is_all_bind == "2") {
                            showOldNoAllAuthDialog()
                        }
                    }

                    mDialog.dismiss()
                })
        mDialog.show()
    }

    private fun initRV() {
        if (mList == null) {
            mList = ArrayList()
        }
        if (mLinearLayoutManager == null) {
            mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        if (mAdapter == null) {
            mAdapter = AssessSuccessAdapter(this, mList)
        }
        rv_assess_success.layoutManager = mLinearLayoutManager
        rv_assess_success.adapter = mAdapter
    }

    private fun initHeader(money: String) {
        val mLayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mHeader = mLayoutInflater.inflate(R.layout.layout_assess_success_header, rv_assess_success, false)
        val changeF2Y = MoneyUtils.getPointTwoMoney(money)
        mHeader?.tv_assess_success_price?.text = "￥$changeF2Y"
        mAdapter?.setHeaderView(mHeader)
        mAdapter?.notifyDataSetChanged()
    }

    override fun getCheckOldUserStatusResult(data: GetSmsCodeBean?) {
        if (data == null) {
            return
        }
        if (data.error_msg == "") {
            //开始下单
            mPresenter.genOrder(mData!!.mobile_model, mData!!.mobile_memory, mData!!.channel_type, mData!!.bank_mobile)
            mMaterialDialog?.dismiss()
        } else {
            mView?.tv_mobile_error?.visibility = View.VISIBLE
            mView?.tv_mobile_error?.text = data.error_msg

        }
    }

    override fun processAssessSuccessData(data: AssessSuccessBean?) {
        if (data == null) {
            return
        }
        mData = data
        initHeader(data.money)
        if (data.recovery_process != null && data.recovery_process.size != 0) {
            mList?.clear()
            mList?.addAll(data.recovery_process)
            mAdapter?.notifyDataSetChanged()
        }
        mHeader?.tv_assess_success_title?.text = "您的手机${data.mobile_desc}评估价格"

    }


    override fun processGenOrderResult(t: GenOrderBean?) {
        if (t != null) {
            var bundle = Bundle()
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_ID, t.order_id)
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_SUCCESS_CONTENT, t.order_success_content)
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_SUCCESS_ICON, t.order_success_icon)
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_SUCCESS_TITLE, t.order_success_title)
            gotoActivity(mActivity, GenOrderSuccessActivity::class.java, bundle)
        }
    }

    override fun processGenOrderFinish() {
        EventBus.getDefault().post(BackHomeEvent())
        finish()
    }

    /**
     *
     * 获取到验证码的返回结果
     */
    override fun getVeryCodeResult(data: SmsBean?) {
        smsBean = data
        mTimeCount = TimeCount(mView?.dialog_get_sms_code, 60000, 1000, "重新获取", true, R.drawable.shape_circle)
        mTimeCount!!.start()
        ToastUtil.showToast(mActivity, "验证码发送成功")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0) {
            EventBus.getDefault().post(BackHomeEvent())
            backActivity()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    //老户但是没有绑定完支付渠道的dialog
    private fun showOldNoAllAuthDialog() {
        //验证码验证方式
        val mLayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = mLayoutInflater.inflate(R.layout.dialog_sms_verify, null, false)
        mMaterialDialog = MaterialDialog(this).setView(mView)
        mMaterialDialog?.setCanceledOnTouchOutside(true)
        mView?.et_input_phone_num?.setText(mData?.bank_mobile?.toString())
        mView?.dialog_sms_cancle?.setOnClickListener {
            mMaterialDialog?.dismiss()
        }
        mView?.iv_clear_et?.setOnClickListener {
            mView?.et_input_phone_num?.text?.clear()
        }

        RxView.clicks(mView!!.dialog_get_sms_code)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe({
                    if (!RegexUtil.IsTelephone(mView?.et_input_phone_num?.text.toString())) {
                        ToastUtil.showToast(mActivity, "手机号格式不正确")
                    } else {
                        var mobile = mView?.et_input_phone_num?.text.toString().trim()
                        mPresenter.getVeryCode(mobile, "6")
                    }
                })
        mView?.et_input_phone_num?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s.toString())) {
                    isHasDialogPhone = false
                    mView?.tv_dialog_confirm_refund?.isClickable = false
                    mView?.tv_mobile_error?.visibility = View.VISIBLE
                    mView?.tv_mobile_error?.text = "请输入手机号"
                    mView?.tv_dialog_confirm_refund?.background = resources.getDrawable(R.drawable.shape_sms_unconfir_bg)
                } else {
                    mView?.tv_mobile_error?.visibility = View.INVISIBLE
                    isHasDialogPhone = true
                    if (isHasDialogPhone && isHasDialogCode) {
                        mView?.tv_dialog_confirm_refund?.isClickable = true
                        mView?.tv_dialog_confirm_refund?.background = resources.getDrawable(R.drawable.global_btn_selector)
                    }
                }
            }
        })

        mView?.et_input_verify_code?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s.toString())) {
                    isHasDialogCode = false
                    mView?.tv_dialog_confirm_refund?.isClickable = false
                    mView?.tv_mobile_error?.visibility = View.VISIBLE
                    mView?.tv_mobile_error?.text = "请输入验证码"
                    mView?.tv_dialog_confirm_refund?.background = resources.getDrawable(R.drawable.shape_sms_unconfir_bg)
                } else {
                    mView?.tv_mobile_error?.visibility = View.INVISIBLE
                    isHasDialogCode = true
                    if (!TextUtils.isEmpty(mView?.et_input_phone_num?.text)) {
                        mView?.tv_dialog_confirm_refund?.isClickable = true
                        mView?.tv_dialog_confirm_refund?.background = resources.getDrawable(R.drawable.global_btn_selector)
                    }
                }
            }
        })
        //验证手机号和验证码
        mView?.tv_dialog_confirm_refund?.setOnClickListener {
            isSmsCodeNull = mView?.et_input_verify_code?.text.toString().trim()

            bank_mobile = mView?.et_input_phone_num?.text.toString()
            if (!RegexUtil.IsTelephone(bank_mobile)) {
                ToastUtil.showToast(mActivity, "手机号格式不正确")
            } else {
                if (smsBean?.error_msg != null) {
                    mView?.tv_mobile_error?.visibility = View.INVISIBLE
                    mView?.tv_mobile_error?.text = smsBean?.error_msg
                }
                mPresenter.getCheckOldUserStatus(mData!!.channel_type, bank_mobile, isSmsCodeNull!!)
            }


        }
        mMaterialDialog?.show()
    }
}
