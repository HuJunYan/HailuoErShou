package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MessageCenterBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.ApiSettings
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.MessageCenterContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import org.json.JSONObject

/**
 * MessageCenterPresenter
 * @author liu wei
 * @date 2018/4/12
 */

class MessageCenterPresenter : RxPresenter<MessageCenterContract.View>(), MessageCenterContract.Presenter {
    var READ_ALL = -1
    var page = 1
    //表的偏移量,默认传0
    var table_identifier: String? = "0"
    //页码偏移量
    var table_page_offset: String? = "0"

    val PAGE_SIZE = 10

    override fun readAllMessage() {
        readMessage(null, READ_ALL, "1")
    }

    override fun readMessage(position: Int, msg: MessageCenterBean.MessageBean?) {
        readMessage(msg, position, "2")
    }

    /**
     * @param type 1代表全部已读 ，2 代表阅读单条，需传入id
     */
    private fun readMessage(msg: MessageCenterBean.MessageBean?, position: Int, type: String) {
        mView?.showProgress()

        val jsonObject = JSONObject()
        // 阅读一条消息，传入消息id和偏移量
        if (position != READ_ALL) {
            jsonObject.put("msg_id", msg?.msgId)
            jsonObject.put("table_identifier", msg?.table_identifier)
        }
        jsonObject.put("is_read_all", type)

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.readMessage(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult())
                .subscribeWith(object : CommonSubscriber<Any>(mView, ApiSettings.READ_MESSAGE) {
                    override fun onNext(bean: Any?) {
                        mView?.hideProgress()
                        if (position == READ_ALL) {
                            mView?.refreshAllMessageState()
                            mView?.setReadAllEnable(false)
                        } else {
                            mView?.refreshMessageReadState(position)
                        }
                    }
                })
        )
    }


    override fun refreshMessageList() {
        page = 1
        table_identifier = "0"
        table_page_offset = "0"
        getMessageList()
    }

    override fun getMessageList() {
        mView?.showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("now_page", page.toString())
        jsonObject.put("page_size", PAGE_SIZE.toString())
        jsonObject.put("table_identifier", table_identifier)
        jsonObject.put("table_page_offset", table_page_offset)

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.getMessageCenterList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<MessageCenterBean>>())
                .compose(RxUtil.handleResult())
                .subscribeWith(object : CommonSubscriber<MessageCenterBean>(mView, ApiSettings.GET_MESSAGE_CENTER_LIST) {
                    override fun onNext(bean: MessageCenterBean?) {
                        mView?.hideProgress()
                        mView?.showMessageList(bean?.msgList)

                        var haveNewMsg = true
                        if (bean?.is_new_msg != null) {
                            //1有新消息，2没有新消息
                            haveNewMsg = bean.is_new_msg == "1"
                        }
                        mView?.setReadAllEnable(haveNewMsg)

                        if (bean?.msgList != null) {
                            val size = bean.msgList.size

                            mView?.setLoadMoreEnable(size >= PAGE_SIZE)
                        }

                        table_identifier = bean?.callbackParam?.table_identifier

                        table_page_offset = bean?.callbackParam?.table_page_offset

                        this@MessageCenterPresenter.page += 1
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        mView?.hideProgress()
                    }
                })
        )
    }

}