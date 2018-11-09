package com.huaxi.hailuo.ui.activity

import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_post_mobile.*

/**
 * 邮寄手机
 *
 * @author liu wei
 * @date 2018/4/17
 */
class PostMobileActivity : SimpleActivity() {

    override fun getLayout(): Int = R.layout.activity_post_mobile

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_post_mobile_bar)
        iv_post_mobile_return.setOnClickListener { backActivity() }
    }

    override fun initData() {
    }

}