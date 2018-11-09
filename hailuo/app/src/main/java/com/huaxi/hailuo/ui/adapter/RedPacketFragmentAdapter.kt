package com.huaxi.hailuo.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.huaxi.hailuo.ui.fragment.RedPacketFragment

class RedPacketFragmentAdapter(fm: FragmentManager, share_url: String, share_desc: String, share_image: String, share_title: String, withdraw_money: String) : FragmentPagerAdapter(fm) {
    //    private var mTitles: Array<*> = titles
    private var share_url: String = share_url
    private var share_desc: String = share_desc
    private var share_image: String = share_image
    private var share_title: String = share_title
    private var withdraw_money: String = withdraw_money
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> RedPacketFragment.newInstance(RedPacketFragment.BUNDLE_VALUE_COUPONS_TYPE_INCOME_ORDER, share_url, share_desc, share_image, share_title, withdraw_money)
            else -> RedPacketFragment.newInstance(RedPacketFragment.BUNDLE_VALUE_COUPONS_TYPE_WITHDRAW_RECODER, share_url, share_desc, share_image, share_title, withdraw_money)
        }
    }

    override fun getCount(): Int {
        return 2
    }


}