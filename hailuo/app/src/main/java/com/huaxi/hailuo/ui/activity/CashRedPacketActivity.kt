package com.huaxi.hailuo.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.CashRedPacketBean
import com.huaxi.hailuo.presenter.contract.MyCashRedPacketContract
import com.huaxi.hailuo.presenter.impl.MyCashRedPacketPresenter
import com.huaxi.hailuo.ui.adapter.RedPacketFragmentAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.TimeCount
import com.huaxi.hailuo.util.ToastUtil
import com.huaxi.hailuo.util.UserUtil
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_cash_red_packet.*
import kotlinx.android.synthetic.main.dialog_cash_red_packet.view.*
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

class CashRedPacketActivity : BaseActivity<MyCashRedPacketContract.View, MyCashRedPacketContract.Presenter>(), MyCashRedPacketContract.View {


    private var mCashBean: CashRedPacketBean? = null
    private var mWithdrawMoney: Double? = 0.00
    private var mDialog: Dialog? = null
    private var userMobile: String? = null
    private var userMobileAfterSubstring: String? = null
    private var view: View? = null
    private var mTimeCount: TimeCount? = null
    private var share_link: String? = ""
    private var share_desc: String? = ""
    private var share_image: String? = ""
    private var share_title: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override var mPresenter: MyCashRedPacketContract.Presenter = MyCashRedPacketPresenter()

    override fun getLayout(): Int = R.layout.activity_cash_red_packet

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_cash_red_packet)
//        userMobile = UserUtil.getMobile(mActivity)
        var mIntent = intent
        if (mIntent != null) {
            share_link = mIntent.getStringExtra("share_link")
            share_desc = mIntent.getStringExtra("share_desc")
            share_image = mIntent.getStringExtra("share_image")
            share_title = mIntent.getStringExtra("share_title")
        }
        iv_cash_red_packet_return.setOnClickListener {
            backActivity()
        }


    }


    override fun initData() {
        mPresenter.myCashRedPacket()
    }

    //现金红包提现结果
    override fun myCashWithdrawResult(data: Any?) {

//        ToastUtil.showToast(mActivity, "提现成功！")
        mDialog?.dismiss()
        mPresenter.myCashRedPacket()
    }

    override fun myCashRedPacketCompelete(data: CashRedPacketBean?) {

        if (data == null) {
            return
        }
        mCashBean = data

        tv_withdraw_avaliable.text = data.current_money
        tv_total_income.text = data.all_money
        tv_withdraw_history.text = data.history_money
        tv_withdraw_des.text = data.income_des
        userMobile = data.reserved_mobile

        //用户当前金额小于最小可提现金额，按钮不可点击,置灰
        if (data.current_money.toDouble() < data.income_min.toDouble()) {
            tv_withdraw.isEnabled = false
            tv_withdraw.setBackgroundResource(R.drawable.icon_withdraw_gray)
        } else {
            if (data.button_status == "1") {
                tv_withdraw.isEnabled = true
                tv_withdraw.setBackgroundResource(R.drawable.icon_withdraw)
            } else if (data.button_status == "2") {
                tv_withdraw.isEnabled = false
                tv_withdraw.setBackgroundResource(R.drawable.icon_withdraw_gray)
            }
        }
        //用户当前金额大于最大可提现金额，则提现金额为最大金额数
        if (data.current_money.toDouble() > data.income_max.toDouble()) {
            mWithdrawMoney = data.income_max.toDouble()
        }
        //用户当前金额小于最大可提现金额并且当前金额当前金额大于最小可提现金额，则提现金额为当前金额数
        if (data.current_money.toDouble() < data.income_max.toDouble() && data.current_money.toDouble() > data.income_min.toDouble()) {
            mWithdrawMoney = data.current_money.toDouble()
        }

        tv_withdraw.setOnClickListener {
            //已绑定银行卡
            if (mCashBean?.is_bind_bank == "1") {
                mPresenter.sendCodeToRedPacket(UserUtil.getMobile(mActivity), "7")
                showRedPacketDialog()
                //未绑定银行卡
            } else if (mCashBean?.is_bind_bank == "2") {
                //身份认证已认证，则让用户去绑银行卡
                if (mCashBean?.is_auth_identity == "1") {
                    startActivity<BindBankCardActivity>(BindBankCardActivity.Money to mWithdrawMoney.toString(), BindBankCardActivity.TITLE to "提现银行卡")
                    //身份证认证未认证，跳转到身份认证页面
                } else if (mCashBean?.is_auth_identity == "2") {
                    val intent = Intent(mActivity, IdentityActivity::class.java)
                    intent.putExtra("withdraw_money", mCashBean?.current_money)
                    startActivity(intent)
                }
            }

        }
        initTabLayout()


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

    private fun initTabLayout() {
        viewPager_red_packet.adapter = RedPacketFragmentAdapter(
                supportFragmentManager,
                share_link!!,
                share_desc!!,
                share_image!!,
                share_title!!,
                mCashBean?.current_money!!
        )
        viewPager_red_packet.offscreenPageLimit = 2
        xTablayout_red_packet.setupWithViewPager(viewPager_red_packet)
        xTablayout_red_packet.getTabAt(0)?.text = "收入明细"
        xTablayout_red_packet.getTabAt(1)?.text = "提现记录"

    }

    //得到验证码的结果
    override fun sendCodeToRedPacketResult(data: Any?) {
        ToastUtil.showToast(mActivity, "验证码发送成功")
        mTimeCount = TimeCount(view!!.tv_red_packet_get_sms_code, 60000, 1000, "重新获取", true, R.drawable.shape_circle)
        mTimeCount!!.start()
    }

    //现金红包弹框
    private fun showRedPacketDialog() {
        if (mDialog == null) {
            mDialog = Dialog(mActivity, R.style.MyDialog)
        }

        val mLayoutInflater = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = mLayoutInflater.inflate(R.layout.dialog_cash_red_packet, null, false)
        mDialog?.setContentView(view)
        mDialog?.setCancelable(false)
        if (!TextUtils.isEmpty(userMobile)) {
            userMobileAfterSubstring = userMobile?.substring(0, 3) + "****" + userMobile?.substring(7, 11)
        }
        var str = "<p>请输入<span style='color:#ff6633'>$userMobileAfterSubstring</span>发送的短信验证码</p>"
        view!!.tv_red_packet_tip.text = Html.fromHtml(str)

        view!!.tv_card.text = "提现到" + mCashBean?.bank_card + "银行卡"
        view!!.dialog_red_packet_cancle.setOnClickListener {
            mDialog?.dismiss()
        }
        view!!.tv_red_packet_get_sms_code.setOnClickListener {
            mPresenter.sendCodeToRedPacket(UserUtil.getMobile(mActivity), "7")
        }
        RxView.clicks(view!!.tv_dialog_red_packet_confirm).throttleFirst(5, TimeUnit.SECONDS)
                .subscribe {
                    if (TextUtils.isEmpty(view!!.et_red_packet_verify_code.text.trim().toString())) {
                        ToastUtil.showToast(mActivity, "验证码不能为空")
                        return@subscribe
                    }

                    //调用提现的接口
                    mPresenter.myCashWithdraw((mWithdrawMoney?.toInt()!! * 100).toString(), view!!.et_red_packet_verify_code.text.toString())
                }
        mDialog?.show()
    }
}
