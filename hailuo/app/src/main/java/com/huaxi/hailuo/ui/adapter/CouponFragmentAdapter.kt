package com.huaxi.hailuo.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.huaxi.hailuo.ui.fragment.CouponsFragment

class CouponFragmentAdapter(fm: FragmentManager, vararg titles: String) : FragmentPagerAdapter(fm) {
    private var mTitles: Array<*> = titles

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CouponsFragment.newInstance(CouponsFragment.BUNDLE_VALUE_COUPONS_TYPE_UN_USE)
            1 -> CouponsFragment.newInstance(CouponsFragment.BUNDLE_VALUE_COUPONS_TYPE_USED)
            else -> CouponsFragment.newInstance(CouponsFragment.BUNDLE_VALUE_COUPONS_TYPE_INVALID)
        }
    }

    override fun getCount(): Int {
        return mTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles.get(position)?.toString()
    }
}