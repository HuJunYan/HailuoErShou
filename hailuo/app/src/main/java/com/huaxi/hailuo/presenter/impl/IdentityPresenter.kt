package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.IDCardBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SaveBackIdCardDataBean
import com.huaxi.hailuo.model.bean.TianShenIdNumInfoBean
import com.huaxi.hailuo.model.http.*
import com.huaxi.hailuo.presenter.contract.IdentityContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class IdentityPresenter : RxPresenter<IdentityContract.View>(), IdentityContract.Presenter {

    /**
     * 得到用户已经认证过的信息
     */
    override fun getIdNumInfo() {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.getIdNumInfo(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<TianShenIdNumInfoBean>>())
                .compose(RxUtil.handleResult<TianShenIdNumInfoBean>())
                .subscribeWith(object : CommonSubscriber<TianShenIdNumInfoBean>(mView, true, ApiSettings.GET_IDNUM_INFO) {
                    override fun onNext(data: TianShenIdNumInfoBean) {
                        mView?.showIdNumInfo(data)
                    }
                }))
    }

    /**
     * 上传身份证信息
     */
    override fun saveIdNumInfo(saveBackIdCardDataBean: SaveBackIdCardDataBean) {

        val jsonObject = JSONObject()
        jsonObject.put("type", saveBackIdCardDataBean.type)
        jsonObject.put("real_name", saveBackIdCardDataBean.real_name)
        jsonObject.put("id_num", saveBackIdCardDataBean.id_num)
        jsonObject.put("gender", saveBackIdCardDataBean.gender)
        jsonObject.put("nation", saveBackIdCardDataBean.nation)
        jsonObject.put("birthday", saveBackIdCardDataBean.birthday)
        jsonObject.put("birthplace", saveBackIdCardDataBean.birthplace)
        jsonObject.put("sign_organ", saveBackIdCardDataBean.sign_organ)
        jsonObject.put("valid_period", saveBackIdCardDataBean.valid_period)
        jsonObject.put("request_id", saveBackIdCardDataBean.request_id)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        //身份证照片
        val files = arrayListOf<MultipartBody.Part>()
        val file = File(saveBackIdCardDataBean.id_numPath)
        val requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val fileBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
        files.add(fileBody)

        addDisposable(TianShenApiManager.saveIdNumInfo(body, files)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView, true, ApiSettings.SAVE_IDNUM_INFO) {
                    override fun onNext(data: Any) {
                        mView?.onSaveIdNumInfoResult()
                    }
                }))

    }

    /**
     * 校验身份一致性并保存
     */
    override fun checkFace(type: String, real_name: String, id_num: String, delta: String, bestPath: String, envPath: String) {

        val jsonObject = JSONObject()
        jsonObject.put("type", type)
        jsonObject.put("real_name", real_name)
        jsonObject.put("id_num", id_num)
        jsonObject.put("delta", delta)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        //身份证照片
        val files = arrayListOf<MultipartBody.Part>()
        //最佳活体照片
        val bestFile = File(bestPath)
        val bestRequestFile = RequestBody.create(MediaType.parse("application/octet-stream"), bestFile)
        val bestFileBody = MultipartBody.Part.createFormData("image_best", bestFile.name, bestRequestFile)
        //防攻击照片
        val envFile = File(envPath)
        val envRequestFile = RequestBody.create(MediaType.parse("application/octet-stream"), envFile)
        val envFileBody = MultipartBody.Part.createFormData("image_env", envFile.name, envRequestFile)

        files.add(bestFileBody)
        files.add(envFileBody)

        addDisposable(TianShenApiManager.checkFace(body, files)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView, true, ApiSettings.CHECK_FACE) {
                    override fun onNext(data: Any) {
                        mView?.onCheckFaceResult()
                    }
                }))
    }


    override fun ding(flag: String, result: String) {
        val jsonObject = JSONObject()
        jsonObject.put("flag", flag)
        jsonObject.put("result", result)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(TianShenApiManager.buriedPoint(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult())
                .subscribeWith(object : CommonSubscriber<Any>(mView, false, ApiSettings.BURIED_POINT) {
                    override fun onNext(data: Any?) {
                    }
                }))
    }

    /**
     * 调用face++服务器得到身份证信息
     */
    override fun ocrIdCard(idFile: File) {

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("api_key", GlobalParams.FACE_ADD_ADD_APPKEY)
                .addFormDataPart("api_secret", GlobalParams.FACE_ADD_ADD_APPSECRET)
                .addFormDataPart("legality", "1")
                .addFormDataPart("image", idFile.name, RequestBody.create(MediaType.parse("multipart/form-data"), idFile))
                .build()

        addDisposable(FaceAddAddApiManager.ocrIdCard(requestBody)
                .compose(RxUtil.rxSchedulerHelper<IDCardBean>())
                .subscribeWith(object : CommonSubscriber<IDCardBean>(mView, ApiSettings.OCR_IDCARD) {
                    override fun onNext(data: IDCardBean) {
                        mView?.onOcrIdCardResult(data)
                    }
                }))
    }

    /**
     * 调用face++服务器判断人脸真伪
     */
//     fun verify(delta: String, image_best: File, image_env: File) {
//
//        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("api_key", GlobalParams.FACE_ADD_ADD_APPKEY)
//                .addFormDataPart("api_secret", GlobalParams.FACE_ADD_ADD_APPSECRET)
//                .addFormDataPart("comparison_type", "0")
//                .addFormDataPart("face_image_type", "meglive")
//                .addFormDataPart("uuid", UserUtil.getUserId(App.instance))
//                .addFormDataPart("delta", delta)
//                .addFormDataPart("image_best", image_best.name, RequestBody.create(MediaType.parse("image/*"), image_best))
//                .addFormDataPart("image_ref1", image_best.name, RequestBody.create(MediaType.parse("image/*"), image_best))
//                .addFormDataPart("image_env", image_env.name, RequestBody.create(MediaType.parse("image/*"), image_env))
//                .build()
//
//        addDisposable(FaceAddAddApiManager.verify(requestBody)
//                .compose(RxUtil.rxSchedulerHelper<VerifyBean>())
//                .subscribeWith(object : CommonSubscriber<VerifyBean>(mView!!, ApiSettings.VERIFY) {
//                    override fun onNext(data: VerifyBean) {
//                        mView?.onVerifyResult(data)
//                    }
//                }))
//
//
//    }

}