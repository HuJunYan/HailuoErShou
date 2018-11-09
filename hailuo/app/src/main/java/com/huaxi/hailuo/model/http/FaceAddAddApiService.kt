package com.huaxi.hailuo.model.http

import com.huaxi.hailuo.model.bean.IDCardBean
import com.huaxi.hailuo.model.bean.VerifyBean
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST

interface FaceAddAddApiService {

    @POST(ApiSettings.OCR_IDCARD)
    fun ocrIdCard(@Body body: RequestBody): Flowable<IDCardBean>

    @POST(ApiSettings.VERIFY)
    fun idEqualsFace(@Body body: RequestBody): Flowable<VerifyBean>

}