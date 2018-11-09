package com.huaxi.hailuo.model.http

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.event.IdCardFailureEvent
import com.huaxi.hailuo.event.TokenErrorEvent
import com.huaxi.hailuo.toast
import com.huaxi.hailuo.ui.activity.LoginActivity
import com.huaxi.hailuo.util.DialogUtil
import com.huaxi.hailuo.util.ToastUtil
import com.huaxi.hailuo.util.UserUtil
import io.reactivex.subscribers.ResourceSubscriber
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by cuiyue on 2017/6/22.
 */
abstract class CommonSubscriber<T> : ResourceSubscriber<T> {

    private var mView: BaseView?
    private var mErrorMsg: String = ""
    //请求的url
    private var mUrl = ""
    private var isShowErrorState: Boolean = true
    private var isShowLoading: Boolean = true

    constructor(view: BaseView?, url: String) : this(view) {
        this.mView = view
        this.mUrl = url
    }

    constructor(view: BaseView?, isShowLoading: Boolean = true) {
        this.mView = view
        this.isShowLoading = isShowLoading
    }

    constructor(view: BaseView?, isShowLoading: Boolean = true, url: String = "") : this(view) {
        this.mView = view
        this.isShowLoading = isShowLoading
        this.mUrl = url
    }

    override fun onStart() {
        super.onStart()
        if (isShowLoading) {
            DialogUtil.showLoadingDialog()
        }
    }

    override fun onComplete() {
        if (isShowLoading) {
            DialogUtil.hideLoadingDialog()
            isShowLoading = false
        }
    }

    override fun onError(e: Throwable?) {

        if (mView == null) {
            return
        }

        if (e is ApiException) {
            var responseData = ""
            try {
                responseData = e.getResponseData()
                val json = JSONObject(responseData)
                mErrorMsg = json.optString("msg")
                val code = json.optInt("code")
                when (code) {
                    ApiCode.TOKEN_ERROR -> {
                        mErrorMsg = ""
                        UserUtil.clearUser(App.instance)
                        EventBus.getDefault().post(TokenErrorEvent())
                        val intent = Intent(App.instance, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        App.instance.startActivity(intent)
                    }
                    ApiCode.UPDATE -> {
                        mErrorMsg = ""
                        val data = json.getJSONObject("data")
                        val download_url = data.getString("download_url")
                        DialogUtil.showUpdateDialog(download_url)
                    }
                    ApiCode.ERROR -> {
                        mErrorMsg = ""
                        DialogUtil.showServiceErrorDialog()
                    }
                }
            } catch (e: Exception) {
                mErrorMsg = "服务器忙，稍后再试"
                e.printStackTrace();
            }
        } else if (e is HttpException || e is ConnectException || e is UnknownHostException || e is SocketTimeoutException || e is NoRouteToHostException) {
            mErrorMsg = "服务器忙，稍后再试"

            if (mUrl == ApiSettings.OCR_IDCARD) {
                mErrorMsg = "识别身份证信息失败"
                EventBus.getDefault().post(IdCardFailureEvent())
            }
            if (mUrl == ApiSettings.BURIED_POINT) { //如果是埋点错误，就不提示
                mErrorMsg = ""
            }

        } else {
            mErrorMsg = "未知错误ヽ(≧Д≦)ノ"
        }

        if (!TextUtils.isEmpty(mErrorMsg)) {
            App.instance.toast(mErrorMsg)
        }

        //隐藏dialog样式的loading
        if (isShowLoading) {
            DialogUtil.hideLoadingDialog()
            isShowLoading = false
        }

        if (isShowErrorState) {
            mView?.showError()
        }
    }
}