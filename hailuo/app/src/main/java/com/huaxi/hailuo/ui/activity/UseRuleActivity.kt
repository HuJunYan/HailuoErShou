package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.HelpCenterBean
import com.huaxi.hailuo.presenter.contract.HelpCenterContract
import com.huaxi.hailuo.presenter.impl.HelpcenterPresenter
import com.huaxi.hailuo.ui.adapter.UseRulesHelperCenterAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_use_rule.*

/**
 * 帮助中心界面
 */
class UseRuleActivity : BaseActivity<HelpCenterContract.View, HelpCenterContract.Presenter>(), HelpCenterContract.View {

    var mList: ArrayList<HelpCenterBean.HelpListBean>? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var mAdapter: UseRulesHelperCenterAdapter? = null
    var mData: HelpCenterBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override var mPresenter: HelpCenterContract.Presenter = HelpcenterPresenter()

    override fun getLayout(): Int = R.layout.activity_use_rule
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

    override fun initView() {
        StatusBarUtil.setPaddingSmart(this, tb_use_rule)
        tv_use_rule_back.setOnClickListener { backActivity() }
        if (mLinearLayoutManager == null) {
            mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        if (mList == null) {
            mList = ArrayList()
        }
        //添加一个头部的背景item
        if (mAdapter == null) {
            mAdapter = UseRulesHelperCenterAdapter(mActivity, mList)
        }
        rv_use_rule.layoutManager = mLinearLayoutManager
        rv_use_rule.adapter = mAdapter
    }

    override fun initData() {
        mPresenter.getData("2")

    }

    override fun processData(data: HelpCenterBean?) {
        if (data == null) {
            return
        }
        mData = data
        val qaList = data.help_list
        if (qaList != null) {
            mList?.addAll(qaList)
            mAdapter?.notifyDataSetChanged()
        }

    }


}