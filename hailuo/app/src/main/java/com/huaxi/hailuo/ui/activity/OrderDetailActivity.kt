package com.huaxi.hailuo.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.event.RefreshOrderDetailEvent
import com.huaxi.hailuo.model.bean.OrderDetailBean
import com.huaxi.hailuo.presenter.contract.OrderDetailContract
import com.huaxi.hailuo.presenter.impl.OrderDetailPresenter
import com.huaxi.hailuo.ui.adapter.RecyclProgressAdapter
import com.huaxi.hailuo.util.CalendarUtil
import com.huaxi.hailuo.util.MoneyUtils
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.ToastUtil
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.dialog_home_assess_fail.view.*
import kotlinx.android.synthetic.main.item_my_order.view.*
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 订单详情界面
 */
class OrderDetailActivity : BaseActivity<OrderDetailContract.View, OrderDetailContract.Presenter>(), OrderDetailContract.View, SwipeRefreshLayout.OnRefreshListener {

    private var mFlag: Boolean = true
    private var mDialog: Dialog? = null
    private var mOrderId: String? = null
    private var mOrderDetailBean: OrderDetailBean? = null
    private var mAdapter: RecyclProgressAdapter? = null
    private var mList: MutableList<OrderDetailBean.StatusListBean>? = null
    private var headView: View? = null
    override var mPresenter: OrderDetailContract.Presenter = OrderDetailPresenter()

    companion object {
        var ORDERI_ID: String = "order_id"
    }

    override fun getLayout(): Int = R.layout.activity_order_detail

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
        order_detail_refresh.setRefreshing(false)
        mAdapter!!.loadMoreFail()
    }

    override fun showErrorMsg(msg: String) {
        order_detail_refresh.setRefreshing(false)
        mAdapter!!.loadMoreFail()
    }

    override fun showEmpty() {
    }

    override fun initView() {
        tv_time.paint.isFakeBoldText = true
        StatusBarUtil.setPaddingSmart(mActivity, order_detail_tb)
        order_detail_refresh.setOnRefreshListener(this)
        order_detail_refresh.setColorSchemeResources(R.color.colorPrimary)
        iv_order_detail_return.setOnClickListener({ backActivity() })
        RxView.clicks(no_sell)
                .throttleFirst(5, TimeUnit.SECONDS)
                .subscribe({
                    //不卖了
                    mFlag = false
                    if (!TextUtils.isEmpty(mOrderId)) {
                        startActivity<RefundActivity>(RefundActivity.ORDER_ID to mOrderId!!)
                    }
                })

        confim_buy.setOnClickListener({
            //确定卖出
            mFlag = false
//            val bundle = Bundle()
//            if (mOrderDetailBean == null || TextUtils.isEmpty(mOrderDetailBean!!.service_url)) {
//                ToastUtil.showToast(mActivity, "数据错误")
//                return@setOnClickListener
//            }
//            bundle.putString(ServiceOnlineActivity.SERVICE_ONLINE_KEY, mOrderDetailBean!!.service_url)
//            gotoActivity(mActivity, ServiceOnlineActivity::class.java, bundle)
            gotoActivity(mActivity, PostMobileActivity::class.java, null)
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null || intent.extras == null) {
            return
        }
        order_detail_refresh.isRefreshing = true
        mFlag = true
        mOrderId = intent.getStringExtra(ORDERI_ID)
        mPresenter.getOrderDetail(mOrderId!!)
    }

    override fun initData() {
        mList = ArrayList()
        mOrderId = intent.getStringExtra(ORDERI_ID)
        if (mAdapter == null) {
            mAdapter = RecyclProgressAdapter(mList)
        }
        if (TextUtils.isEmpty(mOrderId)) {
            ToastUtil.showToast(mActivity, "订单信息异常")
            return
        }
        mPresenter.getOrderDetail(mOrderId!!)
        recyclerview.layoutManager = LinearLayoutManager(mActivity)
        recyclerview.adapter = mAdapter
    }

    override fun getOrderDetailResult(data: OrderDetailBean?) {
        order_detail_refresh.setRefreshing(false);
        if (data == null) {
            return
        }
        mOrderDetailBean = data
        showView()

//        // 测试数据
//        data.is_show_calendar_dialog = "1"
//        val notice = OrderDetailBean.Notice()
//        notice.notice_timestamp = (System.currentTimeMillis() / 1000).toInt().toString()
//        notice.notice_title = "海螺商城还款提醒"
//        data.notice = notice
        addToCalendar(data)
    }

    private fun addToCalendar(data: OrderDetailBean?) {
        //1代表需要放款成功需要添加到系统日历里面,非1的时候APP不需要显示
        if (data == null || data.is_show_calendar_dialog != "1" || data.notice == null) {
            return
        }
        // 如果为true 代表不再提示当前订单添加到日历
//        val show = SharedPreferencesUtil.getInstance(mActivity).getBoolean(CalendarUtil.CALENDER_NOTICE + data.order_id)
//        if (!show) {
        CalendarUtil.showCalendarDialog(mActivity, data.order_id, data.notice)
//        }
    }

    private fun showView() {
        val remaining_term = mOrderDetailBean!!.remaining_term
        val overdue_term = mOrderDetailBean!!.overdue_term

        if (remaining_term.toInt() > 0 && overdue_term.toInt() > 0) {
            ToastUtil.showToast(mActivity, "数据错误")
            return
        }

        if (mOrderDetailBean!!.status_list != null && mOrderDetailBean!!.status_list.size > 0) {
            //有数据

            mList = mOrderDetailBean?.status_list
            mAdapter!!.list = mList
            mAdapter!!.setNewData(mList!!)
            mAdapter!!.notifyDataSetChanged()

            val detail_info = mOrderDetailBean!!.detail_info
            if (mFlag) {
                if (headView == null) {
                    headView = View.inflate(mActivity, R.layout.item_my_order, null)
                    val drawable = resources.getDrawable(R.drawable.what)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    headView!!.time_key.setCompoundDrawables(null, null, drawable, null)
                    headView!!.time_key.setOnClickListener({
                        showWhatDialog(mOrderDetailBean!!.detail_info)
                    })

                    mAdapter!!.addHeaderView(headView)
                }
//                ImageUtil.loadFitXY(mActivity, detail_info.mobile_icon, headView?.phone_img)
                headView!!.phone_name.text = detail_info.mobile_model
                headView!!.phone_price.text = "${MoneyUtils.changeF2Y(detail_info.assess_money, 2)}元"
                headView!!.less_price.text = "${MoneyUtils.changeF2Y(detail_info.pre_money, 2)}元"
                headView!!.time.text = """${detail_info.order_term}天"""
                headView!!.detail.visibility = View.GONE
            }
        }


        if ("1" == mOrderDetailBean!!.order_status || "2" == mOrderDetailBean!!.order_status ||
                "3" == mOrderDetailBean!!.order_status || "6" == mOrderDetailBean!!.order_status) {
            //显示区域
            ll_order_detail_bottom.visibility = View.VISIBLE
            rl_time.visibility = View.VISIBLE
            if (overdue_term.toInt() > 0) {
                //逾期
                tv_time.setTextColor(resources.getColor(R.color.red))
                tv_time.text = "已违约" + overdue_term + "天"
            } else {
                //未逾期
                tv_time.setTextColor(resources.getColor(R.color.black))
                tv_time.text = "履约期还有" + remaining_term + "天"
            }
        } else {
            //不显示下面区域
            ll_order_detail_bottom.visibility = View.GONE
            rl_time.visibility = View.GONE
        }
    }

    /**
     * 显示waht 弹窗
     */
    private fun showWhatDialog(detail_info: OrderDetailBean.DetailInfoBean) {

        mDialog = Dialog(mActivity, R.style.MyDialog)
        val mLayoutInflater = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.dialog_home_assess_fail, null, false)
        view.dialog_home_title.text = detail_info.term_title
        view.dialog_home_content.text = detail_info.term_content
        mDialog?.setContentView(view)
        mDialog?.setCancelable(true)
        view.i_know.setOnClickListener {
            mDialog?.dismiss()
        }

        mDialog?.show()
    }

    override fun onRefresh() {
        order_detail_refresh.isRefreshing = false
        mFlag = false
        mPresenter.getOrderDetail(mOrderId!!)
    }


    @Subscribe
    fun reFreshData(event: RefreshOrderDetailEvent) {
        mPresenter.getOrderDetail(mOrderId)
    }

    override fun backActivity() {
        if (App.instance.isBackground()) {
            startActivity<LauncherActivity>()
        }
        super.backActivity()
    }

}
