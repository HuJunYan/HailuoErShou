package com.huaxi.hailuo.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseFragment
import com.huaxi.hailuo.model.bean.IncomeDetailOrderBean
import com.huaxi.hailuo.presenter.contract.IncomeWithdrawContract
import com.huaxi.hailuo.presenter.impl.IncomeWithdrawPresenter
import com.huaxi.hailuo.ui.adapter.*
import com.huaxi.hailuo.util.ToastUtil
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_red_packet.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.graphics.Bitmap
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.InviteFriendBean
import com.huaxi.hailuo.ui.activity.CashRedPacketActivity
import com.huaxi.hailuo.ui.view.InviteBottomDialog
import com.huaxi.hailuo.util.QRCodeUtils
import com.huaxi.hailuo.util.RecycleShareUtils
import com.huaxi.hailuo.util.SharedPreferencesUtil


/**
 * 优惠券fragment
 */
class RedPacketFragment : BaseFragment<IncomeWithdrawContract.View, IncomeWithdrawPresenter>(), IncomeWithdrawContract.View {


    private var mType = BUNDLE_VALUE_COUPONS_TYPE_INCOME_ORDER
    private var mSize = DEFAULT_SIZE
    private var mPage = DEFAULT_PAGE
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mIncomeOrderAdapter: IncomeOrderAdapter
    private lateinit var mWithDrawRecoderAdapter: WithDrawRecoderAdapter
    private lateinit var mList: MutableList<IncomeDetailOrderBean.Income>
    private var isRefresh: Boolean = true
    private var inviteBottomDialog: InviteBottomDialog? = null
    private var mInvitaionBean: InviteFriendBean? = null
    private var mQRBitmap: Bitmap? = null
    private var mShareUrl: String? = null
    private var share_desc: String? = null
    private var share_image: String? = null
    private var share_title: String? = null
    private var withdraw_money: String? = null
    private var incomeDetailOrderBean: IncomeDetailOrderBean? = null

    companion object {
        var BUNDLE_KEY_INCOME_TYPE = "bundle_key_income_type"
        var BUNDLE_VALUE_COUPONS_TYPE_INCOME_ORDER = 1 //收入明细
        var BUNDLE_VALUE_COUPONS_TYPE_WITHDRAW_RECODER = 2 //提现记录
        var BUNDLE_KEY_SHARE_URL = "share_link"
        var BUNDLE_KEY_SHARE_DESC = "share_desc"
        var BUNDLE_KEY_SHARE_IMAGE = "share_image"
        var BUNDLE_KEY_SHARE_TITLE = "share_title"
        var BUNDLE_KEY_WITHDRAW_MONEY = "withdraw_money"

        var DEFAULT_PAGE = 1
        var DEFAULT_SIZE = 10

        fun newInstance(from: Int, share_link: String, share_desc: String, share_image: String, share_title: String, withdraw_money: String): RedPacketFragment {
            val args = Bundle()
            args.putInt(RedPacketFragment.BUNDLE_KEY_INCOME_TYPE, from)
            args.putString(RedPacketFragment.BUNDLE_KEY_SHARE_URL, share_link)
            args.putString(RedPacketFragment.BUNDLE_KEY_SHARE_DESC, share_desc)
            args.putString(RedPacketFragment.BUNDLE_KEY_SHARE_IMAGE, share_image)
            args.putString(RedPacketFragment.BUNDLE_KEY_SHARE_TITLE, share_title)
            args.putString(RedPacketFragment.BUNDLE_KEY_WITHDRAW_MONEY, withdraw_money)

            val fragment = RedPacketFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            mType = arguments.getInt(BUNDLE_KEY_INCOME_TYPE, BUNDLE_VALUE_COUPONS_TYPE_INCOME_ORDER)
            mShareUrl = arguments.getString(BUNDLE_KEY_SHARE_URL)
            share_desc = arguments.getString(BUNDLE_KEY_SHARE_DESC)
            share_image = arguments.getString(BUNDLE_KEY_SHARE_IMAGE)
            share_title = arguments.getString(BUNDLE_KEY_SHARE_TITLE)
            withdraw_money = arguments.getString(BUNDLE_KEY_WITHDRAW_MONEY)
        }
    }

    override var mPresenter: IncomeWithdrawPresenter = IncomeWithdrawPresenter()
    override fun getLayout(): Int = R.layout.fragment_red_packet

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
        srl_red_packet.finishRefresh()
        srl_red_packet.finishLoadmore()
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }


    override fun myCashRedPacketMoneyListResult(data: IncomeDetailOrderBean?, isRefresh: Boolean) {

        srl_red_packet.finishRefresh()
        srl_red_packet.finishLoadmore()
        if (data == null) {
            return
        }
        incomeDetailOrderBean = data
        if (isRefresh) {
            mList.clear()
        }

        mList.addAll(data.income_list)
        rv_red_packet.adapter?.notifyDataSetChanged()

        //处理不同类型数据结果
        if (!isRefresh) {
            when (mType) {
                BUNDLE_VALUE_COUPONS_TYPE_INCOME_ORDER -> refreshState(data.count.toInt())
                BUNDLE_VALUE_COUPONS_TYPE_WITHDRAW_RECODER -> refreshState(data.count.toInt())
                else -> refreshState(data.count.toInt())
            }
        }


        //处理显隐以及是否可以加载更多
        if (mList.size == 0) {
            rl_red_packet.visibility = View.VISIBLE
            rv_red_packet.visibility = View.GONE
            srl_red_packet.isEnableLoadmore = false
        } else {
            rl_red_packet.visibility = View.GONE
            rv_red_packet.visibility = View.VISIBLE
            srl_red_packet.isEnableLoadmore = true
        }


        LogUtil.e("我的钱是：",withdraw_money.toString())
        if (withdraw_money?.toDouble()!! > 0) {
            if (incomeDetailOrderBean?.income_list?.size!! == 0) {
                if (mType == 1) {
                    tv_packet_tips.text = "暂无收支明细"
                } else if (mType == 2) {
                    tv_packet_tips.text = "暂无提现记录"
                }
            }
        } else {
            tv_packet_tips.text = "竟然还没有现金红包，赶快邀请好友吧~"
            val spannable = SpannableString(tv_packet_tips.text)
            spannable.setSpan(ForegroundColorSpan(Color.RED), 12, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


            spannable.setSpan(object : ClickableSpan() {

                override fun onClick(widget: View) {
                    showShareDialog()
                }

                override fun updateDrawState(ds: TextPaint?) {
                    super.updateDrawState(ds)
                    if (ds != null) {
                        ds?.isUnderlineText = false
                    }
                }
            }, 12, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv_packet_tips.movementMethod = LinkMovementMethod.getInstance()
            tv_packet_tips.text = spannable
        }

    }


    override fun initView() {
        srl_red_packet.isEnableRefresh = true
        srl_red_packet.isEnableLoadmore = false
        srl_red_packet.setOnRefreshListener {
            mPage = DEFAULT_PAGE
            mPresenter.myCashRedPacketMoneyList(mType.toString(), mPage.toString(), mSize.toString(), true)
        }
        srl_red_packet.setOnLoadmoreListener {
            var page = mPage + 1
            mPresenter.myCashRedPacketMoneyList(mType.toString(), page.toString(), mSize.toString(), false)
        }

        initRecyclerView(mType)




    }


    //邀请好友的dialog
    private fun showShareDialog() {

        if (mShareUrl == null) {
            ToastUtil.showToast(activity, "数据错误")
            return
        }
        mQRBitmap = QRCodeUtils.createQRCode(mShareUrl)
        inviteBottomDialog = InviteBottomDialog(activity, RecycleShareUtils.getIUiListenerInstance(activity),
                share_title, share_desc, InviteBottomDialog.TYPE_NORMAL_SHARE, share_image)
                .setQRCodeBitmap(mQRBitmap).setShareUrl(mShareUrl)
                .setShareIconResAndName(R.drawable.icon_wx_share, resources.getString(R.string.app_name))
        inviteBottomDialog?.show()
    }

    private fun initRecyclerView(mType: Int) {
        mLinearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_red_packet.layoutManager = mLinearLayoutManager

        mList = mutableListOf()
        when (mType) {
            BUNDLE_VALUE_COUPONS_TYPE_INCOME_ORDER -> initIncomeDetail()//收入明细
            BUNDLE_VALUE_COUPONS_TYPE_WITHDRAW_RECODER -> initWithDrawRecoder()//提现记录
            else -> initIncomeDetail()
        }

    }


    //初始化收入明细adapter
    private fun initIncomeDetail() {
        mIncomeOrderAdapter = IncomeOrderAdapter(mList, {})

        rv_red_packet.adapter = mIncomeOrderAdapter
    }

    //初始化提现记录adapter
    private fun initWithDrawRecoder() {

        mWithDrawRecoderAdapter = WithDrawRecoderAdapter(activity!!, mList)
        rv_red_packet.adapter = mWithDrawRecoderAdapter
    }


    override fun onSupportVisible() {
        super.onSupportVisible()
        mPage = DEFAULT_PAGE
        mPresenter.myCashRedPacketMoneyList(mType.toString(), mPage.toString(), mSize.toString(), true)
    }

    override fun initData() {
//        mPresenter.myCashRedPacketMoneyList(mType.toString(), mPage.toString(), mSize.toString())
    }

    private fun refreshState(count: Int) {
        if (mList.size == count) {
            ToastUtil.showToast(mContext, "没有更多条目了。")
            srl_red_packet.finishLoadmore()
        } else {
            mPage++
        }
    }

}