package com.huaxi.hailuo.ui.activity

import android.support.v7.widget.LinearLayoutManager
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.HelpCenterBean
import com.huaxi.hailuo.presenter.contract.HelpCenterContract
import com.huaxi.hailuo.presenter.impl.HelpcenterPresenter
import com.huaxi.hailuo.ui.adapter.UseRulesHelperCenterAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_help_center.*

/**
 * 帮助中心界面
 */
class HelpCenterActivity : BaseActivity<HelpCenterContract.View, HelpCenterContract.Presenter>(), HelpCenterContract.View {

    var mList: ArrayList<HelpCenterBean.HelpListBean>? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var mAdapter: UseRulesHelperCenterAdapter? = null
    var mData: HelpCenterBean? = null

    override var mPresenter: HelpCenterContract.Presenter = HelpcenterPresenter()

    override fun getLayout(): Int = R.layout.activity_help_center
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
        StatusBarUtil.setPaddingSmart(this, tb_helper_center)
        tv_heper_center_back.setOnClickListener { backActivity() }
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
        rv_repayment.layoutManager = mLinearLayoutManager
        rv_repayment.adapter = mAdapter
    }

    override fun initData() {
        mPresenter.getData("1")

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
