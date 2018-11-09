package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.ContactsBean
import com.huaxi.hailuo.model.bean.ExtroContactsBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.AuthExtroContactsContract
import com.huaxi.hailuo.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by admin on 2018/1/16.
 */
class AuthExtroContactsPresenter : RxPresenter<AuthExtroContactsContract.View>(), AuthExtroContactsContract.Presenter {
    override fun uploadContacts(list: List<ContactsBean>) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("contact_list", JSONArray(GsonUtil.bean2json(list)))
        val jsonObjectSigned = SignUtils.signJsonContainList(jsonObject, "contact_list")
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.saveContacts(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(t: Any?) {
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.processUploadContactsResult()
                    }
                }))
    }

    override fun getExtroContacts() {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.getExtroContacts(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<ExtroContactsBean>>())
                .compose(RxUtil.handleResult<ExtroContactsBean>())
                .subscribeWith(object : CommonSubscriber<ExtroContactsBean>(mView) {
                    override fun onNext(t: ExtroContactsBean?) {
                        mView?.processExtroData(t)
                    }
                }))
    }

    override fun saveExtroContacts(map: HashMap<String, ExtroContactsBean.EmeContacts>) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonArray = JSONArray()
        for (key in map.keys) {
            val emeContacts = map.get(key)
            if (emeContacts != null) {
                var jsonObject = JSONObject()
                jsonObject.put("type", emeContacts.type)
                jsonObject.put("contact_name", emeContacts.contact_name)
                jsonObject.put("contact_phone", emeContacts.contact_phone)
                jsonArray.put(jsonObject)
            }
        }

        jsonObject.put("eme_contacts", jsonArray)
        val jsonObjectSigned = SignUtils.signJsonContainList(jsonObject, "eme_contacts")
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.saveExtroContacts(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(t: Any?) {
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.processUploadResult()
                    }
                }))
    }


}