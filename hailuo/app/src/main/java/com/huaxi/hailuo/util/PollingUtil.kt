package com.huaxi.hailuo.util

import com.huaxi.hailuo.log.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * PollingUtil
 * 轮询计时器
 *
 * @author liu wei
 * @date 2018/5/24
 */
class PollingUtil {
    /**
     * 轮询时间间隔
     */
    private var mLoopInterval: Long = 1
    /**
     * 轮询次数
     */
    private var mLoopEndTime: Long = 30
    /**
     * 时间单位
     */
    private var mTimeUnit: TimeUnit = TimeUnit.MINUTES
    /**
     * 跳过的事件个数
     */
    private var mSkip: Int = 0

    private var disposables: CompositeDisposable? = null

    private var mTask: Runnable? = null

    fun timeInterval(value: Long): PollingUtil {
        mLoopInterval = value
        return this
    }

    fun take(value: Long): PollingUtil {
        mLoopEndTime = value
        return this
    }

    fun skip(value: Int): PollingUtil {
        mSkip = value + 1
        return this
    }

    fun timeUnit(value: TimeUnit): PollingUtil {
        mTimeUnit = value
        return this
    }

    fun setTask(runnable: Runnable): PollingUtil {
        mTask = runnable
        return this
    }

    fun start(): PollingUtil {
        if (disposables != null) {
            disposables?.dispose()
        }

        disposables = CompositeDisposable()
        disposables?.add(
                Observable
                        .interval(0, mLoopInterval, mTimeUnit)
                        .take(mLoopEndTime)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getObserver())
        )
        return this
    }

    private fun getObserver(): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {

            override fun onNext(t: Long) {
                val currentIndex = (t + 1).toInt()
                LogUtil.e("PollingUtil---> $currentIndex")

                if (mSkip == 0 || currentIndex % mSkip == 0) {
                    mTask?.run()
                    LogUtil.e("PollingUtil---> task run()")
                }
            }

            override fun onError(e: Throwable) {
                LogUtil.e("PollingUtil--->")
                e.printStackTrace()
            }

            override fun onComplete() {
                disposables?.dispose()
            }
        }
    }

    fun stop() {
        disposables?.dispose()
    }
}
