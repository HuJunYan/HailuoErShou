package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SpeekBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.model.http.TianShenApiManager
import com.huaxi.hailuo.presenter.contract.OpionContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

/**
 * Created by admin on 2018/1/30.
 */
class UpLoadOpionPresenter : RxPresenter<OpionContract.View>(), OpionContract.Presenter {
    override fun upLoadOpionWithoutImage(feed_type: String, feed_content: String, file: String) {
        val jsonObject = JSONObject()

        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("feed_content", feed_content)
        jsonObject.put("feed_type", feed_type)
        jsonObject.put("file", file)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.upLoadOpionWithoutImage(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {

                    }
                    override fun onComplete() {
                        super.onComplete()
                        mView?.upLoadComplete()
                    }
                }))
    }

    override fun upLoadOpion(feed_type: String, feed_content: String, filePath: String) {
        val jsonObject = JSONObject()

        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("feed_content", feed_content)
        jsonObject.put("feed_type", feed_type)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        val files = arrayListOf<MultipartBody.Part>()
        val file = File(filePath)
        val requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val fileBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

        files.add(fileBody)
        addDisposable(ApiManager.upLoadOpion(body, files)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.upLoadComplete()
                    }
                }))
    }

    //获取我要吐槽的类型
    override fun getFeedBack() {
        val jsonObject = JSONObject()

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getSpeekType(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<SpeekBean>>())
                .compose(RxUtil.handleResult<SpeekBean>())
                .subscribeWith(object : CommonSubscriber<SpeekBean>(mView) {
                    override fun onNext(data: SpeekBean?) {
                        mView?.getFeedBackResult(data)
                    }


                }))
    }

}