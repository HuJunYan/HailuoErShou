package com.huaxi.hailuo.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseFragment
import com.huaxi.hailuo.model.bean.CheckUseCouponBean
import com.huaxi.hailuo.model.bean.CouponListBean
import com.huaxi.hailuo.presenter.contract.CouponsContract
import com.huaxi.hailuo.presenter.impl.CouponsPresenter
import com.huaxi.hailuo.ui.activity.MyCouponActivity
import com.huaxi.hailuo.ui.activity.RefundActivity
import com.huaxi.hailuo.ui.adapter.InvalidCouponsAdapter
import com.huaxi.hailuo.ui.adapter.UnUseCouponsAdapter
import com.huaxi.hailuo.ui.adapter.UsedCouponsAdapter
import com.huaxi.hailuo.util.ToastUtil
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.dialog_cousons_tips.view.*
import kotlinx.android.synthetic.main.fragment_coupons.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * 优惠券fragment
 */
class CouponsFragment : BaseFragment<CouponsContract.View, CouponsPresenter>(), CouponsContract.View {

    private var mType = BUNDLE_VALUE_COUPONS_TYPE_UN_USE
    private var mSize = DEFAULT_SIZE
    private var mPage = DEFAULT_PAGE
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mUnUseAdapter: UnUseCouponsAdapter
    private lateinit var mUsedAdapter: UsedCouponsAdapter
    private lateinit var mInvalidCouponsAdapter: InvalidCouponsAdapter
    private lateinit var mList: MutableList<CouponListBean.CouponBean>

    companion object {
        var BUNDLE_KEY_COUPONS_TYPE = "bundle_key_coupons_type"
        var BUNDLE_VALUE_COUPONS_TYPE_UN_USE = 1 //未使用
        var BUNDLE_VALUE_COUPONS_TYPE_USED = 2 //已使用
        var BUNDLE_VALUE_COUPONS_TYPE_INVALID = 3 //过期

        var DEFAULT_PAGE = 1
        var DEFAULT_SIZE = 10

        fun newInstance(from: Int): CouponsFragment {
            val args = Bundle()
            args.putInt(CouponsFragment.BUNDLE_KEY_COUPONS_TYPE, from)

            val fragment = CouponsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            mType = arguments.getInt(BUNDLE_KEY_COUPONS_TYPE, BUNDLE_VALUE_COUPONS_TYPE_UN_USE)
        }
    }

    override var mPresenter: CouponsPresenter = CouponsPresenter()
    override fun getLayout(): Int = R.layout.fragment_coupons

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
        srl_coupons.finishRefresh()
        srl_coupons.finishLoadmore()
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

    override fun initView() {

        srl_coupons.isEnableRefresh = true
        srl_coupons.isEnableLoadmore = false
        srl_coupons.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout?) {
                mPage = DEFAULT_PAGE
                mPresenter.getCouponList(mType, mSize, mPage, true)
            }
        })
        srl_coupons.setOnLoadmoreListener(object : OnLoadmoreListener {
            override fun onLoadmore(refreshlayout: RefreshLayout?) {
                var page = mPage + 1
                mPresenter.getCouponList(mType, mSize, page, false)

            }
        })
        initRecyclerView(mType)

    }

    private fun initRecyclerView(mType: Int) {
        mLinearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_coupons.layoutManager = mLinearLayoutManager
        mList = mutableListOf()
        when (mType) {
            BUNDLE_VALUE_COUPONS_TYPE_UN_USE -> initUnUseAdapterAndTips()
            BUNDLE_VALUE_COUPONS_TYPE_USED -> initUsedAdapterAndTips()
            BUNDLE_VALUE_COUPONS_TYPE_INVALID -> initInvalidAdapterAndTips()
            else -> initUnUseAdapterAndTips()
        }
    }

    //初始化未使用优惠券adapter
    private fun initUnUseAdapterAndTips() {
        tv_coupons_tips.text = resources.getString(R.string.text_coupons_tips_no_un_used)
        mUnUseAdapter = UnUseCouponsAdapter(mList, {
            mPresenter.checkUseCoupon(it.coupon_id)

        })
        rv_coupons.adapter = mUnUseAdapter
    }

    //初始化已使用优惠券adapter
    private fun initUsedAdapterAndTips() {
        tv_coupons_tips.text = resources.getString(R.string.text_coupons_tips_no_used)
        mUsedAdapter = UsedCouponsAdapter(activity!!, mList)
        rv_coupons.adapter = mUsedAdapter
    }

    //初始化已过期优惠券adapter
    private fun initInvalidAdapterAndTips() {
        tv_coupons_tips.text = resources.getString(R.string.text_coupons_tips_no_invalid)
        mInvalidCouponsAdapter = InvalidCouponsAdapter(activity!!, mList)
        rv_coupons.adapter = mInvalidCouponsAdapter
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        mPage = DEFAULT_PAGE
        mPresenter.getCouponList(mType, mSize, mPage, true)
    }

    override fun initData() {

//        mPresenter.getCouponList(mType, mSize, mPage, true)
    }

    override fun processGetCouponListData(data: CouponListBean?, isRefresh: Boolean) {
        srl_coupons.finishRefresh()
        srl_coupons.finishLoadmore()
        if (data == null) {
            return
        }
        if (isRefresh) {
            mList.clear()
        }
        mList.addAll(data.couponList)
//        mList.add(CouponListBean.CouponBean("1", "退款专项红包", "5", "履约期内用户可用", "仅限在海螺商城平台退款时使用", "2017-11-21-2017-12-30", "2018.03.18"))
//        mList.add(CouponListBean.CouponBean("2", "退款专项红包", "5", "履约期内用户可用", "仅限在海螺商城平台退款时使用", "2017-11-21-2017-12-30", "2018.03.18"))
        rv_coupons.adapter?.notifyDataSetChanged()
        //处理不同类型数据结果
        if (!isRefresh) {
            when (mType) {
                BUNDLE_VALUE_COUPONS_TYPE_UN_USE -> refreshState(data.unused_count)
                BUNDLE_VALUE_COUPONS_TYPE_USED -> refreshState(data.used_count)
                BUNDLE_VALUE_COUPONS_TYPE_INVALID -> refreshState(data.past_time_count)
                else -> refreshState(data.unused_count)
            }
        }
        if (activity is MyCouponActivity) {
            val myCouponActivity = activity as MyCouponActivity
            myCouponActivity.setCouponCount(data.unused_count, data.used_count, data.past_time_count)
        }

        //处理显隐以及是否可以加载更多
        if (mList.size == 0) {
            rl_coupons.visibility = View.VISIBLE
            rv_coupons.visibility = View.GONE
            srl_coupons.isEnableLoadmore = false
        } else {
            rl_coupons.visibility = View.GONE
            rv_coupons.visibility = View.VISIBLE
            srl_coupons.isEnableLoadmore = true
        }

    }

    private fun refreshState(count: Int) {
        if (mList.size == count) {
            ToastUtil.showToast(mContext, "没有更多条目了。")
            srl_coupons.finishLoadmore()
        } else {
            mPage++
        }
    }


    override fun processCheckUseCoupon(data: CheckUseCouponBean?, coupon_id: String) {
        if (data == null) {
            return
        }
        if ("1" == data.use_coupon_result) {
            //去还款页面
            startActivity<RefundActivity>(RefundActivity.ORDER_ID to data.order_id, RefundActivity.COUPON_ID to coupon_id)
        } else {
            showErrorDialog(data.content)
        }
    }

    private fun showErrorDialog(content: String) {
        var mDialog = Dialog(activity, R.style.MyDialog)

        val mLayoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.dialog_cousons_tips, null, false)
        mDialog.setContentView(view)
        view.tv_dialog_coupons_tips.text = content
        view.tv_dialog_coupons_confirm.setOnClickListener { mDialog.cancel() }
        val layoutParams = view.getLayoutParams()
        layoutParams.width = (300 * resources.displayMetrics.density).toInt()
        layoutParams.height = (200 * resources.displayMetrics.density).toInt()
        view.layoutParams = layoutParams
        mDialog.setCancelable(true)

        mDialog.show()

    }

}