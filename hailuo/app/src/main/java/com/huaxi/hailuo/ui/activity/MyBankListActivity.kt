package com.huaxi.hailuo.ui.activity

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.event.BindBankEvent
import com.huaxi.hailuo.model.bean.MyBankListBean
import com.huaxi.hailuo.presenter.contract.MyBankListContract
import com.huaxi.hailuo.presenter.impl.MyBankListPresenter
import com.huaxi.hailuo.ui.adapter.MyBankListAdapter
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.ToastUtil
import kotlinx.android.synthetic.main.activity_my_bank_list.*
import kotlinx.android.synthetic.main.dialog_unbind_card.view.*
import me.drakeet.materialdialog.MaterialDialog
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity

/**
 * 银行卡列表界面
 */
class MyBankListActivity : BaseActivity<MyBankListContract.View, MyBankListContract.Presenter>(), MyBankListContract.View, SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: MyBankListAdapter? = null
    private lateinit var mList: MutableList<MyBankListBean.BankBean>
    private var mFlag: Boolean = true
    private var mPosotion: Int = -1

    override var mPresenter: MyBankListContract.Presenter = MyBankListPresenter()

    override fun getLayout(): Int = R.layout.activity_my_bank_list

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
        swiperefre.isRefreshing = false
    }

    override fun showErrorMsg(msg: String) {
        swiperefre.isRefreshing = false
    }

    override fun showEmpty() {
    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, my_bank_tb)
        swiperefre.setOnRefreshListener(this)
        swiperefre.setColorSchemeResources(R.color.colorPrimary)
        iv_bank_list_return.setOnClickListener({ backActivity() })
    }

    override fun initData() {
        mList = ArrayList()
        if (mAdapter == null) {
            mAdapter = MyBankListAdapter(mActivity, mList)
        }
        recly_bank_list.layoutManager = LinearLayoutManager(mActivity)
        recly_bank_list.adapter = mAdapter
        mPresenter.loadData()
        mAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            mPosotion = position
            val bankBean = adapter.getItem(position) as MyBankListBean.BankBean
            showUnbindDialog(bankBean)
        }
    }

    override fun loadDataComplete(data: MyBankListBean) {
        swiperefre.isRefreshing = false

        if (data.bank_list != null) {
            mList.clear()
            mList = data.bank_list
            mAdapter!!.setNewData(mList)
            mAdapter!!.notifyDataSetChanged()
        }
        if (mFlag) {
            val view = View.inflate(mActivity, R.layout.view_bank_list_foot, null)
            view.setOnClickListener({
                //是否认证过，没有的话就提示跳转到认证中心
                if ("1" == data.is_auth_identity) {
                    startActivity<BindBankCardActivity>(
                            BindBankCardActivity.TITLE to "添加银行卡",
                            BindBankCardActivity.Money to "")
                    mFlag = false
                } else {
                    ToastUtil.showToast(mActivity, "请先进行身份认证", ToastUtil.SHOW)
                    return@setOnClickListener
                }
            })
            mAdapter!!.addFooterView(view)
        }

    }

    override fun deleteCardComplete() {
        swiperefre.isRefreshing = false
        mAdapter!!.remove(mPosotion)
        ToastUtil.showToast(mActivity, "删除成功")
    }

    override fun onRefresh() {
        swiperefre.isRefreshing = false
        mFlag = false
        mPresenter.loadData()
    }


    /**
     * 显示是否解绑弹窗
     */
    private fun showUnbindDialog(bankBean: MyBankListBean.BankBean) {
        val mCardNum = bankBean.mobile_card_num
        val dialog = MaterialDialog(mActivity)
        val view = View.inflate(mActivity, R.layout.dialog_unbind_card, null)
        view.dialog_content.text = "      您确定解除绑定\n尾号为" + mCardNum!!.substring(mCardNum!!.length - 4, mCardNum!!.lastIndex + 1) + "的银行卡吗"
        view.confirm.setOnClickListener({
            mPresenter.deleteCard(bankBean.card_id)
            dialog.dismiss()
        })
        view.dialog_canele.setOnClickListener({ dialog.dismiss() })
        dialog.setView(view)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    @Subscribe
    fun BindBankEventSuccess(event: BindBankEvent) {
        //刷新数据
        mPresenter.loadData()
    }
}
