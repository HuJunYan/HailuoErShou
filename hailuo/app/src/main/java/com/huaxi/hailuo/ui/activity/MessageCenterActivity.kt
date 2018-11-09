package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.ViewGroup
import cn.jpush.android.api.JPushInterface
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.MessageCenterBean
import com.huaxi.hailuo.presenter.contract.MessageCenterContract
import com.huaxi.hailuo.presenter.impl.MessageCenterPresenter
import com.huaxi.hailuo.ui.adapter.MessageListAdapter
import com.huaxi.hailuo.util.MessageNotificationUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_message_center.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * MessageCenterActivity
 * @author liu wei
 * @date 2018/4/12
 */
class MessageCenterActivity :
        BaseActivity<MessageCenterContract.View, MessageCenterContract.Presenter>(),
        MessageCenterContract.View {

    val MSG_READ = "1" // 已阅读
    val MSG_UNREAD = "2" // 未阅读

    override var mPresenter: MessageCenterContract.Presenter = MessageCenterPresenter()
    val mList: ArrayList<MessageCenterBean.MessageBean> = arrayListOf()
    lateinit var mAdapter: MessageListAdapter
    // 1 为已读消息，2 未读
    val READ: String = "1"

    override fun getLayout(): Int = R.layout.activity_message_center

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_message_center)
        iv_message_center_return.setOnClickListener {
            backActivity()
        }
        RxView.clicks(tv_message_read_all)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    if (mList.isNotEmpty()) {
                        JPushInterface.clearAllNotifications(mActivity)
                        mPresenter.readAllMessage()
                    }
                }
        // 下拉刷新 加载更多
        refresh_layout.isEnableLoadmore = false

        refresh_layout.setOnRefreshListener {
            mPresenter.refreshMessageList()
            if (mList.size > 0) {
                mList.clear()
                mAdapter.notifyDataSetChanged()
            }
        }
        refresh_layout.setOnLoadmoreListener {
            mPresenter.getMessageList()
        }

        mAdapter = MessageListAdapter(mList)

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.ll_message_content) {
                val message = mList[position]
                // 如果消息已读，不调用阅读消息的接口
                if (message.isRead != MSG_READ) {
                    // 隐藏消息通知
                    EventBus.getDefault().post(MessageNotificationUtil.MessageClickEvent(message.msgId))

                    mPresenter.readMessage(position, message)
                }

                if (!TextUtils.isEmpty(message.msgUrl)) {
                    // 地址不为空，才可跳转
                    startActivity<WebActivity>(
                            WebActivity.WEB_URL_KEY to message.msgUrl,
                            WebActivity.WEB_URL_TITLE to message.title)
                }
                if (TextUtils.isEmpty(message.msgUrl)) {
                    var bundle = Bundle()
                    bundle.putString("msg_id", message.msgId)
                    gotoActivity(mActivity, ArticalDetailActivity::class.java, bundle)
                }

            }
        }

        recycler_view_message.layoutManager = LinearLayoutManager(mActivity)
        recycler_view_message.setHasFixedSize(true)
        mAdapter.setEmptyView(R.layout.view_msg_center_empty, recycler_view_message.parent as ViewGroup)
        recycler_view_message.adapter = mAdapter
    }

    override fun initData() {
        refresh_layout.autoRefresh(0)
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
        refresh_layout.finishRefresh()
        refresh_layout.finishLoadmore()
    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

    override fun showMessageList(list: List<MessageCenterBean.MessageBean>?) {
        if (list != null) {
            mList.addAll(list)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun setLoadMoreEnable(isEnable: Boolean) {
        refresh_layout.isEnableLoadmore = isEnable
    }

    override fun setReadAllEnable(isEnable: Boolean) {
        tv_message_read_all.isEnabled = isEnable
    }


    override fun refreshMessageReadState(position: Int) {
        mList[position].isRead = READ
        mAdapter.notifyItemChanged(position)
    }

    override fun refreshAllMessageState() {
        mList.forEach {
            it.isRead = READ
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun backActivity() {
        if (App.instance.isBackground()) {
            startActivity<LauncherActivity>()
        }
        super.backActivity()
    }

}