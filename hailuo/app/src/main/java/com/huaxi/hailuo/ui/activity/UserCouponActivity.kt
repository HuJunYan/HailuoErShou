package com.huaxi.hailuo.ui.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.UseCoupon
import com.huaxi.hailuo.presenter.contract.UseCouponContract
import com.huaxi.hailuo.presenter.impl.UseCouponPresenter
import com.huaxi.hailuo.ui.adapter.UseCouponAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.ToastUtil
import kotlinx.android.synthetic.main.activity_user_coupon.*

/**
 * 使用优惠券界面
 */
class UserCouponActivity : BaseActivity<UseCouponContract.View, UseCouponContract.Presenter>(), UseCouponContract.View {


    private var mBean: UseCoupon? = null
    private var mAdapter: UseCouponAdapter? = null
    //上次选中的位置
    private var mPosotion: Int = -1
    //选中的优惠券Id
    private var mCouponId: String? = null
    //订单Id
    private var mOrderId: String? = null

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

    override var mPresenter: UseCouponContract.Presenter = UseCouponPresenter()

    override fun getLayout(): Int = R.layout.activity_user_coupon

    companion object {
        var POSOTION: String = "posotion"
    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, yhq_tb)
    }

    override fun initData() {
        mPosotion = intent.getIntExtra(POSOTION, -1)
        mCouponId = intent.getStringExtra(RefundActivity.COUPON_ID)
        mOrderId = intent.getStringExtra(RefundActivity.ORDER_ID)

        mPresenter.loadData(mOrderId)

        iv_yhq_return.setOnClickListener {
            setValue()
        }
    }

    override fun loadDataComplete(data: UseCoupon?) {

        if (data == null) {
            return
        }
        mBean = data
        showView()
    }

    private fun showView() {

        if (mBean != null) {
            if (mBean?.couponList!!.size > 0) {

                if (mCouponId != null) {

                    for (item in mBean!!.couponList) {

                        if (item.coupon_id == mCouponId) {
                            item.chose = true
                        }
                    }
                }

                recyclerview.layoutManager = LinearLayoutManager(mActivity)
                mAdapter = UseCouponAdapter(mBean?.couponList)
                recyclerview.adapter = mAdapter
                itemClick()
            } else {
                //暂无可用优惠券
                ToastUtil.showToast(mActivity, "暂无可用优惠券")
            }
        }

    }

    //点击事件
    private fun itemClick() {

        if (mAdapter != null) {
            mAdapter!!.setOnItemChildClickListener { adapter, view, position ->

                for (item in mBean!!.couponList) {
                    if (item.coupon_id == mCouponId) {
                        item.chose = false
                    }
                }

                mPosotion = position
                val item = adapter.getItem(mPosotion) as UseCoupon.CouponListBean
                mCouponId = item.coupon_id
                item.chose = true
                mAdapter?.notifyDataSetChanged()
                setValue()
            }
        }
    }

    //設置值
    private fun setValue() {
        val intent = Intent()
        intent.putExtra(RefundActivity.COUPON_ID, mCouponId)
        setResult(0, intent)
        finish()
    }
}
