package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_question_feed_back.*

class QuestionFeedBackActivity : SimpleActivity() {
    override fun getLayout(): Int = R.layout.activity_question_feed_back

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_feed_back)
        iv_question_feed_back.setOnClickListener({backActivity()})
    }

    override fun initData() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
