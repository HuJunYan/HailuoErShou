package com.huaxi.hailuo.ui.activity

import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.ui.adapter.CouponFragmentAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_my_coupon.*
import org.jetbrains.anko.startActivity

/**
 * 我的优惠券页面
 */
class MyCouponActivity : SimpleActivity() {

    override fun getLayout(): Int = R.layout.activity_my_coupon

    private var mTitleUnUse = "未使用"
    private var mTitleUse = "使用记录"
    private var mTitleInvalid = "已过期"

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setPaddingSmart(mActivity, tb_my_coupon)
    }

    override fun initView() {
        iv_my_coupon_return.setOnClickListener {
            backActivity()
        }
        tv_rule.setOnClickListener {
            startActivity<UseRuleActivity>()
        }
        initTabLayout()
    }

    override fun initData() {
    }

    private fun initTabLayout() {
        viewPager.adapter = CouponFragmentAdapter(supportFragmentManager, mTitleUnUse, mTitleUse, mTitleInvalid)
        viewPager.offscreenPageLimit = 2
        xTablayout.setupWithViewPager(viewPager)
        setCouponCount()
    }

    /**
     * 设置优惠券的数量
     */
    fun setCouponCount(unUseCount: Int = 0, useCount: Int = 0, invalidCount: Int = 0) {
        xTablayout.getTabAt(0)?.text = "$mTitleUnUse($unUseCount)"
        xTablayout.getTabAt(1)?.text = "$mTitleUse($useCount)"
        xTablayout.getTabAt(2)?.text = "$mTitleInvalid($invalidCount)"
    }

}
