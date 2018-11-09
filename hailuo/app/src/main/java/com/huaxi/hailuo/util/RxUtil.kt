package com.huaxi.hailuo.util

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiException
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.toast

object RxUtil {

    private val ERROR_LOG_TAG: String = "http"

    /**
     * 统一线程处理
     * @param <T>
     * @return
    </T> */
    fun <T> rxSchedulerHelper(): FlowableTransformer<T, T> {    //compose简化线程
        return FlowableTransformer<T, T> {
            it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 处理返回结果
     */
    fun <T> handleResult(): FlowableTransformer<MyHttpResponse<T>, T> {
        return FlowableTransformer<MyHttpResponse<T>, T> {
            it.flatMap {
                createData(it.data)
            }
        }
    }

    /**
     * 生成Flowable
     * @param <T>
     * @return
    </T> */
    fun <T> createData(t: T): Flowable<T> {
        return Flowable.create({ emitter ->
            try {
                if (t != null) {
                    emitter.onNext(t)
                } else {
                    App.instance.toast("服务器忙，稍后再试")
                    CrashReport.postCatchedException(ApiException())
                }
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
                App.instance.toast("服务器忙，稍后再试")
                CrashReport.postCatchedException(ApiException())
                LogUtil.d(ERROR_LOG_TAG, e.getStackTraceString())
                DialogUtil.hideLoadingDialog()
            }
        }, BackpressureStrategy.BUFFER)
    }

}