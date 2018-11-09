package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.Utils
import kotlinx.android.synthetic.main.activity_about_we.*

class AboutWeActivity : SimpleActivity() {
    override fun getLayout(): Int = R.layout.activity_about_we

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity,tb_about)
        iv_about_we.setOnClickListener({backActivity()})
    }

    override fun initData() {
        tv_version.text = "版本号："+Utils.getVersion(mActivity)
    }

}
