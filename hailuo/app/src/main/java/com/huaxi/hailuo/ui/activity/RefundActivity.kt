package com.huaxi.hailuo.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.event.RefreshOrderDetailEvent
import com.huaxi.hailuo.model.bean.ConfirRefundBean
import com.huaxi.hailuo.model.bean.RefundInfoBean
import com.huaxi.hailuo.presenter.contract.ReFundContract
import com.huaxi.hailuo.presenter.impl.RefundPresenter
import com.huaxi.hailuo.ui.adapter.WhatAdapter
import com.huaxi.hailuo.util.*
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_re_fund.*
import kotlinx.android.synthetic.main.dialog_refund_what.view.*
import kotlinx.android.synthetic.main.dialog_sms_verify.view.*
import kotlinx.android.synthetic.main.view_alipay_layout.*
import kotlinx.android.synthetic.main.view_pay_way_layout_normal.*
import kotlinx.android.synthetic.main.view_wxpay_layout.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit


/**
 * 退款页面
 */
class RefundActivity : BaseActivity<ReFundContract.View, ReFundContract.Presenter>(), ReFundContract.View {


    //确认按钮上 展示用户的还款金额
    private var payMoney: String? = null
    private var mDialog: Dialog? = null
    override var mPresenter: ReFundContract.Presenter = RefundPresenter()
    private var mOrderId: String? = null
    private var bank_mobile: String? = ""
    //0 银行卡还款  1 支付宝还款 2 微信还款(默认选择银行卡)
    private var payWayType: Int = 0
    private var isLastChose: Boolean = false
    private var mBean: RefundInfoBean? = null
    private var mTimeCount: TimeCount? = null
    private var isSmsCodeNull: String? = ""
    private var mRefundBean: ConfirRefundBean? = null
    private var mView: View? = null
    private var mMaterialDialog: Dialog? = null
    //0 银行卡还款  1 支付宝还款 2 微信还款
    //优惠券Id
    private var mCouponId: String? = ""
    var isHasDialogPhone: Boolean = false
    var isHasDialogCode: Boolean = false
    override fun getLayout(): Int = R.layout.activity_re_fund

    companion object {

        public var ORDER_ID = "order_id"
        public var COUPON_ID = "coupon_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun getVeryCodeResult() {

        ToastUtil.showToast(mActivity, "验证码发送成功")
    }

    override fun initView() {

        StatusBarUtil.setPaddingSmart(mActivity, refund_tb)
        iv_refund_return.setOnClickListener({ backActivity() })
        //判断网络是否连接
        if (Utils.isNetworkConnected()) {

            bank_way.visibility = View.VISIBLE
            confim_refund.visibility = View.VISIBLE
            //设置下划线
            tv_change_bank_card.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            tv_change_bank_card.setOnClickListener {

                startActivity<BindBankCardActivity>(BindBankCardActivity.TITLE to "更换银行卡", BindBankCardActivity.Money to "")
            }

            what.setOnClickListener({
                showWhatDialog()
            })
            cb_bank_card.setOnCheckedChangeListener { _, _ ->
                if (cb_bank_card.isChecked) {
                    isLastChose = true
                    payWayType = 0
                    cb_other_pay.isChecked = false
                    cb_other_pay_wx.isChecked = false
                    confim_refund.text = "支付金额" + MoneyUtils.changeF2Y(mBean?.real_money, 2) + "元"
                } else {
                    if (!isLastChose || payWayType == 0) {
                        payWayType = -1
                        confim_refund.text = "支付金额" + MoneyUtils.changeF2Y(mBean?.real_money, 2) + "元"
                    }
                }
            }
            cb_other_pay.setOnCheckedChangeListener { _, _ ->
                if (cb_other_pay.isChecked) {
                    isLastChose = true
                    payWayType = 1
                    cb_bank_card.isChecked = false
                    cb_other_pay_wx.isChecked = false
                    var aliMoney = MoneyUtils.changeY2F(mBean!!.repayment_style.alipay.aisle_amount)
                    confim_refund.text = "支付金额" + (MoneyUtils.getPointTwoMoney(mBean?.real_money, aliMoney)) + "元"

                } else {
                    if (!isLastChose || payWayType == 1) {
                        payWayType = -1
                        confim_refund.text = "支付金额" + MoneyUtils.changeF2Y(mBean?.real_money, 2) + "元"
                    }
                }
            }
            cb_other_pay_wx.setOnCheckedChangeListener { _, _ ->
                if (cb_other_pay_wx.isChecked) {
                    isLastChose = true
                    payWayType = 2
                    cb_other_pay.isChecked = false
                    cb_bank_card.isChecked = false
                    var wxMoney = MoneyUtils.changeY2F(mBean!!.repayment_style.wxpay.aisle_amount)
                    confim_refund.text = "支付金额" + (MoneyUtils.getPointTwoMoney(mBean?.real_money, wxMoney)) + "元"

                } else {
                    if (!isLastChose) {
                        payWayType = -1
                        confim_refund.text = "支付金额" + MoneyUtils.changeF2Y(mBean?.real_money, 2) + "元"
                    }
                }
            }
            RxView.clicks(confim_refund)
                    .throttleFirst(5, TimeUnit.SECONDS)
                    .subscribe({
                        if (payWayType == -1) {
                            ToastUtil.showToast(mActivity, "请选择还款方式")
                            return@subscribe
                        }
                        when (mBean?.is_all_bind) {
                            "1" -> {
                                payWay(isSmsCodeNull!!, bank_mobile!!)
                            }
                            "2" -> {
                                if (cb_bank_card.isChecked) {
                                    showFastPayDialog()
                                } else {
                                    payWay(isSmsCodeNull!!, bank_mobile!!)
                                }
                            }
                        }
                    })
            //支付金额
            rl_yhq.setOnClickListener({
                if (mBean?.unused_count!!.toInt() > 0) {
                    mCouponId = mBean?.coupon_free_id
                    //使用优惠券
                    var couponIntent = Intent(mActivity, UserCouponActivity::class.java)
                    couponIntent.putExtra(COUPON_ID, mCouponId)
                    couponIntent.putExtra(ORDER_ID, mOrderId)
                    startActivityForResult(couponIntent, 0)
                }
            })
        } else {
            bank_way.visibility = View.GONE
            confim_refund.visibility = View.GONE
        }
    }

    private fun payWay(codeNull: String, mobile: String) {
        var url = ""
        when (payWayType) {
            0 -> {
                if (!TextUtils.isEmpty(mOrderId))
                    mPresenter.confirmRefund(mOrderId, mCouponId!!, codeNull, mBean?.channel_type!!, mobile)
            }
            1 -> {
                url = mBean!!.repayment_style.alipay.alipay_url
                //支付宝还款
                payWayOther(url)
            }

            2 -> {
                url = mBean!!.repayment_style.wxpay.wxpay_url
                //微信还款
                payWayOther(url)
            }

        }
    }

    private fun payWayOther(url: String) {
        //打开其他还款方式方
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        startActivity(intent)
    }

    override fun initData() {
        mOrderId = intent.getStringExtra(ORDER_ID)
        mCouponId = intent.getStringExtra(COUPON_ID)
        if (mCouponId == null) {
            mCouponId = ""
        }

    }

    override fun onResume() {
        super.onResume()
        if (TextUtils.isEmpty(mOrderId)) {
            ToastUtil.showToast(mActivity, "数据异常，没有订单号")
            return
        }
        mPresenter.loadInitData(mOrderId, mCouponId!!)
    }

    override fun onDestroy() {
        if (mMaterialDialog != null) {
            mMaterialDialog?.dismiss()
        }
        super.onDestroy()
    }

    override fun loadInitComplete(data: RefundInfoBean?) {
        if (data == null) {
            return
        }
        mBean = data
        showView()

    }

    override fun finishActivity() {
        if (mBean!!.status == "7" || mBean!!.status == "8") {
            //7已还款  8还款中
            backActivity()
        }

    }

    override fun confirmRefundComplete(data: ConfirRefundBean?) {
        mRefundBean = data
        if (TextUtils.isEmpty(mRefundBean?.error_msg)) {
            mMaterialDialog?.dismiss()
            EventBus.getDefault().post(RefreshOrderDetailEvent())
            backActivity()
        } else {
            mView?.tv_mobile_error?.visibility = View.VISIBLE
            mView?.tv_mobile_error?.text = mRefundBean!!.error_msg
        }

    }

    //快捷支付的dialog
    private fun showFastPayDialog() {
        //验证码验证方式
        mView = layoutInflater.inflate(R.layout.dialog_sms_verify, null, false)
        mMaterialDialog = Dialog(mActivity, R.style.MyDialog)
        mMaterialDialog?.setContentView(mView)
        mMaterialDialog?.setCanceledOnTouchOutside(true)
        mView?.et_input_phone_num?.setText(mBean?.bank_mobile?.toString())
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
                        mTimeCount = TimeCount(mView?.dialog_get_sms_code, 60000, 1000, "重新获取", true, R.drawable.shape_circle)
                        mTimeCount!!.start()
                        val mobile = mView?.et_input_phone_num?.text.toString().trim()
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

        //确认付款
        mView?.tv_dialog_confirm_refund?.setOnClickListener {
            isSmsCodeNull = mView?.et_input_verify_code?.text.toString().trim()
            if (payWayType == 0) {

                bank_mobile = mView?.et_input_phone_num?.text.toString()
                if (!RegexUtil.IsTelephone(bank_mobile)) {
                    ToastUtil.showToast(mActivity, "手机号格式不正确")
                } else {
                    mPresenter.confirmRefund(mOrderId, mCouponId!!, isSmsCodeNull!!, mBean?.channel_type!!, bank_mobile!!)
                }


            }
        }
        mMaterialDialog?.show()
    }

    private fun showView() {

        if (mBean!!.status == "7" || mBean!!.status == "8") {
            //7已还款  8还款中
            backActivity()
            return
        }

        var card_num = mBean!!.repayment_style.bank_list.bank_card_num
        tv_should_be_money.text = MoneyUtils.changeF2Y(mBean!!.total_money, 2)
        payMoney = mBean!!.real_money
        if (cb_other_pay.isChecked) {
            var alMoney = MoneyUtils.changeY2F(mBean!!.repayment_style.alipay.aisle_amount)
            confim_refund.text = "支付金额" + MoneyUtils.getPointTwoMoney(mBean!!.real_money, alMoney) + "元"

        } else if (cb_other_pay_wx.isChecked) {
            var wxMoney = MoneyUtils.changeY2F(mBean!!.repayment_style.wxpay.aisle_amount)
            confim_refund.text = "支付金额" + MoneyUtils.getPointTwoMoney(mBean!!.real_money, wxMoney) + "元"

        } else {

            confim_refund.text = "支付金额" + MoneyUtils.changeF2Y(payMoney, 2) + "元"
        }
        ImageUtil.load(mActivity, mBean!!.repayment_style.bank_list.bank_card_logo, bank_icon)
        bank_name.text = mBean!!.repayment_style.bank_list.bank_name + " 尾号" + card_num.substring(card_num.length - 4, card_num.length)

        if (mBean!!.repayment_style.alipay != null && mBean!!.repayment_style.alipay.alipay_url != null) {
            ImageUtil.load(mActivity, mBean!!.repayment_style.alipay.alipay_img, iv_icon)
            other_pay.text = mBean!!.repayment_style.alipay.title
            descption.text = Html.fromHtml(mBean!!.repayment_style.alipay.alipay_description)
            ali_pay.visibility = View.VISIBLE
        } else {
            ali_pay.visibility = View.GONE
        }
        if (mBean!!.repayment_style.wxpay != null && mBean!!.repayment_style.wxpay.wxpay_url != null) {
            ImageUtil.load(mActivity, mBean!!.repayment_style.wxpay.wxpay_img, iv_icon_wx)
            other_pay_wx.text = mBean!!.repayment_style.wxpay.title
            descption_wx.text = Html.fromHtml(mBean!!.repayment_style.alipay.alipay_description)
            wx_pay.visibility = View.VISIBLE
        } else {
            wx_pay.visibility = View.GONE
        }

        //优惠券逻辑

        if (TextUtils.isEmpty(mBean?.coupon_money) || "0" == mBean?.unused_count || "0" == mBean?.coupon_money) {
            //减免金额为空

            if (TextUtils.isEmpty(mBean?.unused_count) || "0" == mBean?.unused_count) {
                //优惠券可用张数为空
                tv_yhq.text = "暂无可用"
            } else {
//                tv_yhq.text = mBean?.unused_count + "个可用"
                tv_yhq.text = "-" + MoneyUtils.changeF2Y(mBean?.coupon_money, 2) + "元"
//                tv_yhq.text = "-" +MoneyUtils.addTwoPoint(mBean?.coupon_money?.toLong()!!) + "元"
                tv_yhq.setTextColor(Color.RED)
                val drawable = resources.getDrawable(R.drawable.yhq_right)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                tv_yhq.setCompoundDrawables(null, null, drawable, null)
            }

        } else {
            //减免金额不为空
            tv_yhq.setTextColor(Color.RED)
            tv_yhq.text = "-" + MoneyUtils.changeF2Y(mBean?.coupon_money, 2) + "元"
//            tv_yhq.text = "-" + MoneyUtils.addTwoPoint(mBean?.coupon_money?.toLong()!!) + "元"

        }

    }

    /**
     * 弹窗
     */
    private fun showWhatDialog() {
        if (mBean == null) {
            return
        }
        if (mBean!!.pop_info == null || mBean!!.pop_info.size == 0) {
            ToastUtil.showToast(mActivity, "暂无数据")
            return
        }
        mDialog = Dialog(mActivity, R.style.MyDialog)
        val mLayoutInflater = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.dialog_refund_what, null, false)
        val mDialogAdapter = WhatAdapter(mBean!!.pop_info)
        view.dialog_what_recycle.layoutManager = LinearLayoutManager(mActivity)
        view.dialog_what_recycle.adapter = mDialogAdapter
        view.i_know.setOnClickListener({
            mDialog!!.dismiss()
        })
        mDialog?.setContentView(view)
        mDialog?.setCancelable(true)
        mDialog?.show()

    }

    override fun onPause() {
        super.onPause()
        payMoney = confim_refund.text.toString().trim()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null) {
            mCouponId = data.getStringExtra(COUPON_ID)
            if (mCouponId == null) {
                mCouponId = ""
            }
            mPresenter.loadInitData(mOrderId, mCouponId!!)
        }
    }

}
