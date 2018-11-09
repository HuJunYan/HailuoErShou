package com.huaxi.hailuo.model.http

import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.TianShenIdNumInfoBean
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface TianShenApiService {

    @POST(ApiSettings.GET_IDNUM_INFO)
    fun getIdNumInfo(@Body body: RequestBody): Flowable<MyHttpResponse<TianShenIdNumInfoBean>>

    @Multipart
    @POST(ApiSettings.SAVE_IDNUM_INFO)
    fun saveIdNumInfo(@Part("json") description: RequestBody, @Part files: ArrayList<MultipartBody.Part>): Flowable<MyHttpResponse<Any>>

    @Multipart
    @POST(ApiSettings.CHECK_FACE)
    fun checkFace(@Part("json") description: RequestBody, @Part files: ArrayList<MultipartBody.Part>): Flowable<MyHttpResponse<Any>>

    @POST(ApiSettings.BURIED_POINT_UCENTER)
    fun buriedPoint(@Body body: RequestBody): Flowable<MyHttpResponse<Any>>


    @Multipart
    @POST(ApiSettings.SUBMIT_FEEDBACK)
    fun upLoadOpion(@Part("json") description: RequestBody, @Part files: ArrayList<MultipartBody.Part>): Flowable<MyHttpResponse<Any>>
}