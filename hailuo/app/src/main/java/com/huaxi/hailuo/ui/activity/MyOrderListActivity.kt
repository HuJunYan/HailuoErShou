package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.OrderListBean
import com.huaxi.hailuo.presenter.contract.OrderListContract
import com.huaxi.hailuo.presenter.impl.OrderListPresenter
import com.huaxi.hailuo.ui.adapter.MyOrderListAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_recycling_records.*
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * 我的订单列表 即回收列表
 */
class MyOrderListActivity : BaseActivity<OrderListContract.View,OrderListContract.Presenter>(),OrderListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private var page: Int = 1
    private var size: String? = "10"
    private var mAdapter: MyOrderListAdapter? = null
    private var mList: MutableList<OrderListBean.Order_list>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override var mPresenter: OrderListContract.Presenter = OrderListPresenter()

    override fun getLayout(): Int = R.layout.activity_recycling_records

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
        swiperefre.setRefreshing(false);
        mAdapter!!.loadMoreFail()
        rl_empty.visibility= View.VISIBLE
        recyclerview.visibility = View.GONE
    }

    override fun showErrorMsg(msg: String) {
        swiperefre.setRefreshing(false);
        mAdapter!!.loadMoreFail()
        rl_empty.visibility= View.VISIBLE
        recyclerview.visibility = View.GONE
    }

    override fun showEmpty() {
    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity,tb_re_history)
        recycl_history_return.setOnClickListener({backActivity()})
        swiperefre.setOnRefreshListener(this)
        swiperefre.setColorSchemeResources(R.color.colorPrimary)
        rl_empty.visibility= View.GONE
        recyclerview.visibility = View.VISIBLE

    }

    override fun initData() {
        mList = ArrayList()
        if (mAdapter == null) {
            mAdapter = MyOrderListAdapter(mList, mActivity)
        }
        recyclerview.layoutManager  = LinearLayoutManager(mActivity)
        recyclerview.adapter  =mAdapter
        mAdapter!!.setOnLoadMoreListener(this)
        mAdapter!!.setOnItemChildClickListener { adapter, view, position ->

            val item = adapter.getItem(position) as OrderListBean.Order_list

            startActivity<OrderDetailActivity>(OrderDetailActivity.ORDERI_ID to item.order_id)
        }
        mPresenter.loadData(page.toString(), size!!)
    }


    override fun loadDataComplete(data: OrderListBean?) {

        swiperefre.setRefreshing(false);
        if (data==null || data.order_list==null){
            rl_empty.visibility= View.VISIBLE
            recyclerview.visibility = View.GONE
            return
        }
        if (data.order_list.size==0){
            rl_empty.visibility= View.VISIBLE
            recyclerview.visibility = View.GONE
            return

        }

        if (page == 1) {
            mList?.clear()
            mList?.addAll(data!!.order_list)
            mAdapter?.setNewData(mList)
            mAdapter?.notifyDataSetChanged()

        } else {
            mList?.addAll(data!!.order_list)
            mAdapter?.addData(mList!!)
            mAdapter?.notifyDataSetChanged()
        }

        if (data!!.order_list.size<10){
            mAdapter!!.loadMoreComplete()
            mAdapter!!.loadMoreEnd()
        }
        mAdapter!!.loadMoreComplete()
        mAdapter!!.loadMoreEnd()
    }

    override fun onRefresh() {
        page = 1
      // 开始刷新，设置当前为刷新状态
        swiperefre.setRefreshing(true)
        mPresenter.loadData(page.toString(), size!!)
    }


    override fun onLoadMoreRequested() {
        page++
        mPresenter.loadData(page.toString(),size!!)
    }


}
